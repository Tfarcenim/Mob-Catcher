package com.tfar.mobcatcher;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemNetLauncher extends Item {

  protected ItemStack findNet(EntityPlayer player) {
    ItemStack stack = player.getHeldItemMainhand();
    if (this.isCaptureMode(stack)){
    if (this.isEmptyNet(player.getHeldItem(EnumHand.OFF_HAND))) {
      return player.getHeldItem(EnumHand.OFF_HAND);
    } else if (this.isEmptyNet(player.getHeldItem(EnumHand.MAIN_HAND))) {
      return player.getHeldItem(EnumHand.MAIN_HAND);
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
    if (this.isFilledNet(player.getHeldItem(EnumHand.OFF_HAND))) {
      return player.getHeldItem(EnumHand.OFF_HAND);
    } else if (this.isFilledNet(player.getHeldItem(EnumHand.MAIN_HAND))) {
      return player.getHeldItem(EnumHand.MAIN_HAND);
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

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    if (entityLiving instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) entityLiving;
      ItemStack stackAmmo = this.findNet(player);

      int i = this.getMaxItemUseDuration(stackAmmo) - timeLeft;
      if (i < 0) return;

      if (!stackAmmo.isEmpty() || player.capabilities.isCreativeMode) {
        if (stackAmmo.isEmpty()) stackAmmo = new ItemStack(MobCatcher.ObjectHolders.net);

        float f = getNetVelocity(i);

        if ((double) f >= 0.1D) {

          if (!worldIn.isRemote) {
            ItemNet itemNet = (ItemNet) (stackAmmo.getItem() instanceof ItemNet ? stackAmmo.getItem() : MobCatcher.ObjectHolders.net);
            EntityNet entityNet = itemNet.createNet(worldIn, player, stackAmmo);
            // entityNet = this.customizeArrow(entityNet);
            entityNet.shoot(player, player.rotationPitch, player.rotationYaw, 0, f * 3, 1);

            worldIn.spawnEntity(entityNet);
          }

          worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

          if (!player.capabilities.isCreativeMode) {
            stackAmmo.shrink(1);

            if (stackAmmo.isEmpty()) {
              player.inventory.deleteStack(stackAmmo);
            }
          }
          player.addStat(StatList.getObjectUseStats(this));
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
  public int getMaxItemUseDuration(ItemStack stack) {
    return 72000;
  }

  public boolean isEmptyNet(ItemStack stack) {
    return stack.getItem() instanceof ItemNet && !stack.hasTagCompound();
  }

  public boolean isFilledNet(ItemStack stack){
    return stack.getItem() instanceof ItemNet && ((ItemNet)stack.getItem()).containsEntity(stack);
  }
  /**
   * Called when the equipped item is right clicked.
   */
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
    if (player.isSneaking()){
      NBTTagCompound nbt = stack.getTagCompound();
      boolean capture = this.isCaptureMode(stack);
      nbt.setBoolean("capture",!capture);
      stack.setTagCompound(nbt);
      return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    boolean capture = this.isCaptureMode(stack);

    boolean hasAmmo = !this.findNet(player).isEmpty();

    if (!player.capabilities.isCreativeMode && !hasAmmo) {
      return new ActionResult<>(EnumActionResult.FAIL, stack);
    } else {
      player.setActiveHand(hand);
      return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
  }

  public boolean isCaptureMode(ItemStack stack){
    return stack.hasTagCompound() && stack.getTagCompound().getBoolean("capture");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
    if (world == null)return;
    if(this.isCaptureMode(stack))tooltip.add("Capturing");
    else tooltip.add("Releasing");
  }
}
