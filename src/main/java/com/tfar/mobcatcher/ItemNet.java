package com.tfar.mobcatcher;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemNet extends Item {


  public ItemNet(Properties properties) {
    super(properties);
  }

  @Override
  public int getItemStackLimit(ItemStack stack) {
    return (containsEntity(stack)) ? 1 : 64;
  }

  @Override
  @Nonnull
  public ActionResultType onItemUse(ItemUseContext context) {
    PlayerEntity player = context.getPlayer();
    if (player == null)return ActionResultType.FAIL;
    Hand hand = Hand.MAIN_HAND;
    ItemStack stack = player.getHeldItemMainhand();
    if (player.getEntityWorld().isRemote || !containsEntity(stack)) return ActionResultType.FAIL;
    Entity entity = getEntityFromStack(stack, player.world, true);
    BlockPos blockPos = context.getPos();
    entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0, 0);
    stack.setTag(null);
    player.setHeldItem(hand, stack);
    player.world.addEntity(entity);
  //  if (entity instanceof LivingEntity) ((LivingEntity) entity).playSound();
    return ActionResultType.SUCCESS;
  }

  @Override
  public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
    if (target.getEntityWorld().isRemote || target instanceof PlayerEntity || !target.isAlive() || containsEntity(stack))
      return false;
    String entityID = target.getType().getRegistryName().toString();
    if (isBlacklisted(entityID)) return false;
    ItemStack newStack = stack.copy();
    CompoundNBT nbt = new CompoundNBT();
    nbt.putString("entity", entityID);
    nbt.putString("id", EntityType.getKey(target.getType()).toString());
    target.writeUnlessPassenger(nbt);
    ItemStack newerStack = newStack.split(1);
    newerStack.setTag(nbt);
    player.swingArm(hand);
    player.setHeldItem(hand, newStack);
    if(!player.addItemStackToInventory(newerStack)){
      ItemEntity itemEntity = new ItemEntity(player.world,player.posX,player.posY,player.posZ,newerStack);
      player.world.addEntity(itemEntity);
    }
    target.remove();
    player.getCooldownTracker().setCooldown(this, 5);
    return true;
  }


  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);

   // tooltip.add(new StringTextComponent(stack.getOrCreateTag().toString()));
    if (containsEntity(stack))
      if (!getID(stack).isEmpty()) {
        String s0 = "entity." + getID(stack);
        String s1 = s0.replace(':','.');
        tooltip.add(new StringTextComponent(I18n.format(s1)));
        tooltip.add(new StringTextComponent("Health: " + stack.getTag().getDouble("Health")));
      }
  }

  @Override
  @Nonnull
  public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
    if (!containsEntity(stack))
      return new TranslationTextComponent(super.getTranslationKey(stack) + ".name");
    String s0 = "entity." + getID(stack);
    String s1 = s0.replace(':','.');
    return new TranslationTextComponent(I18n.format(super.getTranslationKey(stack) + ".name") +": "+ I18n.format(s1));
  }

  public NetEntity createNet(World worldIn, LivingEntity shooter, ItemStack stack)
  {
    ItemStack newStack = stack.copy();
    newStack.setCount(1);
    return new NetEntity(shooter.posX, shooter.posY + 1.25, shooter.posZ, worldIn, newStack);
  }

  //helper methods

  public static boolean containsEntity(@Nonnull ItemStack stack) {
    return stack.hasTag() && stack.getTag().contains("entity");
  }

  public static String getID(ItemStack stack) {
    return stack.getOrCreateTag().getString("entity");
  }

  public static boolean isBlacklisted(String entity) {
    return false;
  }

  public static Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
    Entity entity = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(stack.getTag().getString("entity"))).create(world);
    if (withInfo) entity.read(stack.getTag());
    return entity;
  }


}
