package com.tfar.mobcatcher;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemNetLauncher extends Item {

  protected ItemStack findNet(EntityPlayer player) {
    if (this.isNet(player.getHeldItem(EnumHand.OFF_HAND))) {
      return player.getHeldItem(EnumHand.OFF_HAND);
    } else if (this.isNet(player.getHeldItem(EnumHand.MAIN_HAND))) {
      return player.getHeldItem(EnumHand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
        ItemStack itemstack = player.inventory.getStackInSlot(i);

        if (this.isNet(itemstack)) {
          return itemstack;
        }
      }

      return ItemStack.EMPTY;
    }
  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    if (entityLiving instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer) entityLiving;
      ItemStack itemstack = this.findNet(entityplayer);

      int i = this.getMaxItemUseDuration(stack) - timeLeft;
      if (i < 0) return;

      if (!itemstack.isEmpty() || entityplayer.capabilities.isCreativeMode) {
        if (itemstack.isEmpty()) itemstack = new ItemStack(MobCatcher.ObjectHolders.net);

        float f = getNetVelocity(i);

        if ((double) f >= 0.1D) {

          if (!worldIn.isRemote) {
            ItemNet itemNet = (ItemNet) (itemstack.getItem() instanceof ItemNet ? itemstack.getItem() : MobCatcher.ObjectHolders.net);
            EntityNet entityNet = itemNet.createNet(worldIn, entityplayer, itemstack);
            // entityNet = this.customizeArrow(entityNet);
            entityNet.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0, f * 3, 1);

            worldIn.spawnEntity(entityNet);
          }

          worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

          if (!entityplayer.capabilities.isCreativeMode) {
            itemstack.shrink(1);

            if (itemstack.isEmpty()) {
              entityplayer.inventory.deleteStack(itemstack);
            }
          }

          entityplayer.addStat(StatList.getObjectUseStats(this));
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

  public boolean isNet(ItemStack stack) {
    return stack.getItem() instanceof ItemNet && !stack.hasTagCompound();
  }

  /**
   * Called when the equipped item is right clicked.
   */
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    ItemStack itemstack = playerIn.getHeldItem(handIn);
    boolean hasAmmo = !this.findNet(playerIn).isEmpty();

    if (!playerIn.capabilities.isCreativeMode && !hasAmmo) {
      return new ActionResult<>(EnumActionResult.FAIL, itemstack);
    } else {
      playerIn.setActiveHand(handIn);
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
    }
  }
}
