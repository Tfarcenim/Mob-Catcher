package com.tfar.mobcatcher;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemNetLauncher extends Item {

  public ItemNetLauncher(Properties properties) {
    super(properties);
  }

  protected ItemStack findNet(PlayerEntity player) {
    ItemStack stack = player.getHeldItemMainhand();
    if (this.isCaptureMode(stack)){
    if (this.isEmptyNet(player.getHeldItem(Hand.OFF_HAND))) {
      return player.getHeldItem(Hand.OFF_HAND);
    } else if (this.isEmptyNet(player.getHeldItem(Hand.MAIN_HAND))) {
      return player.getHeldItem(Hand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
        ItemStack itemstack = player.inventory.getStackInSlot(i);

        if (this.isEmptyNet(itemstack)) {
          return itemstack;
        }
      }
    }
      return ItemStack.EMPTY;
    }
    if (this.isFilledNet(player.getHeldItem(Hand.OFF_HAND))) {
      return player.getHeldItem(Hand.OFF_HAND);
    } else if (this.isFilledNet(player.getHeldItem(Hand.MAIN_HAND))) {
      return player.getHeldItem(Hand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
        ItemStack itemstack = player.inventory.getStackInSlot(i);

        if (this.isFilledNet(itemstack)) {
          return itemstack;
        }
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public int getItemStackLimit(ItemStack stack) {
    return 1;
  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (entityLiving instanceof PlayerEntity) {
      PlayerEntity player = (PlayerEntity) entityLiving;
      ItemStack stackAmmo = this.findNet(player);

      int i = this.getUseDuration(stackAmmo) - timeLeft;
      if (i < 0) return;

      if (!stackAmmo.isEmpty() || player.abilities.isCreativeMode) {
        if (stackAmmo.isEmpty()) stackAmmo = new ItemStack(MobCatcher.ObjectHolders.net);

        float f = getNetVelocity(i);

        if (f >= 0.1) {

          if (!worldIn.isRemote) {
            ItemNet itemNet = stackAmmo.getItem() instanceof ItemNet ? (ItemNet)stackAmmo.getItem() : (ItemNet)MobCatcher.ObjectHolders.net;
            EntityNet entityNet = itemNet.createNet(worldIn, player, stackAmmo);
            System.out.println(entityNet);
            entityNet.shoot(player, player.rotationPitch, player.rotationYaw, 0, f * 3, 1);

            worldIn.addEntity(entityNet);
          }

          worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

          if (!player.abilities.isCreativeMode) {
            stackAmmo.shrink(1);

            if (stackAmmo.isEmpty()) {
              player.inventory.deleteStack(stackAmmo);
            }
          }
        }
      }
    }
  }

  /**
   * Gets the velocity of the net entity from the launcher's charge
   */
  public static float getNetVelocity(int charge) {
    float f = (float) charge / 20;
    f = (f * f + f * 2) / 3;
    f = Math.min(f, 1.5f);
    return f;
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    return 72000;
  }

  public boolean isEmptyNet(ItemStack stack) {
    return stack.getItem() instanceof ItemNet && !stack.hasTag();
  }

  public boolean isFilledNet(ItemStack stack){
    return stack.getItem() instanceof ItemNet && ((ItemNet)stack.getItem()).containsEntity(stack);
  }
  /**
   * Called when the equipped item is right clicked.
   */
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, @Nonnull Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (player.isSneaking()){
      CompoundNBT nbt = stack.getOrCreateTag();
      boolean capture = this.isCaptureMode(stack);
      nbt.putBoolean("capture",!capture);
      stack.setTag(nbt);
      return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    boolean capture = this.isCaptureMode(stack);

    boolean hasAmmo = !this.findNet(player).isEmpty();

    if (!player.abilities.isCreativeMode && !hasAmmo) {
      return new ActionResult<>(ActionResultType.FAIL, stack);
    } else {
      player.setActiveHand(hand);
      return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
  }

  public boolean isCaptureMode(ItemStack stack){
    return stack.getOrCreateTag().getBoolean("capture");
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    if (world == null)return;
    if(this.isCaptureMode(stack))tooltip.add(new StringTextComponent("Capturing"));
    else tooltip.add(new StringTextComponent("Releasing"));
  }
}
