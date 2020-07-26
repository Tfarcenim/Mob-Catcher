package com.tfar.mobcatcher;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemNetLauncher extends Item {

  public ItemNetLauncher(Properties properties) {
    super(properties);
  }

  protected ItemStack findNet(PlayerEntity player) {
    ItemStack stack = player.getHeldItemMainhand();
    if (isCaptureMode(stack)){
    if (isEmptyNet(player.getHeldItem(Hand.OFF_HAND))) {
      return player.getHeldItem(Hand.OFF_HAND);
    } else if (isEmptyNet(player.getHeldItem(Hand.MAIN_HAND))) {
      return player.getHeldItem(Hand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
        ItemStack itemstack = player.inventory.getStackInSlot(i);

        if (isEmptyNet(itemstack)) {
          return itemstack;
        }
      }
    }
      return ItemStack.EMPTY;
    }
    if (isFilledNet(player.getHeldItem(Hand.OFF_HAND))) {
      return player.getHeldItem(Hand.OFF_HAND);
    } else if (isFilledNet(player.getHeldItem(Hand.MAIN_HAND))) {
      return player.getHeldItem(Hand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
        ItemStack itemstack = player.inventory.getStackInSlot(i);

        if (isFilledNet(itemstack)) {
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
            NetEntity netEntity = itemNet.createNet(worldIn, player, stackAmmo);
            netEntity.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 0);

            worldIn.addEntity(netEntity);
          }

          worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

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

  /**
   * Called when the equipped item is right clicked.
   */
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, @Nonnull Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (player.isCrouching()){
      CompoundNBT nbt = stack.getOrCreateTag();
      boolean capture = isCaptureMode(stack);
      nbt.putBoolean("capture",!capture);
      stack.setTag(nbt);
      player.sendStatusMessage(new TranslationTextComponent(capture ? "mobcatcher.releasing" : "mobcatcher.capturing"),true);
      return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
    boolean hasAmmo = !this.findNet(player).isEmpty();

    if (!player.abilities.isCreativeMode && !hasAmmo) {
      return new ActionResult<>(ActionResultType.FAIL, stack);
    } else {
      player.setActiveHand(hand);
      return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
  }

  @Override
  @Nonnull
  public ITextComponent getDisplayName(@Nonnull ItemStack stack) {
    return new TranslationTextComponent(I18n.format(super.getTranslationKey(stack)) + " ("+I18n.format(isCaptureMode(stack) ? "mobcatcher.capturing": "mobcatcher.releasing")+ ")");
  }

  //helpers
  public static boolean isCaptureMode(ItemStack stack){
    return stack.getOrCreateTag().getBoolean("capture");
  }
  public static boolean isEmptyNet(ItemStack stack) {
    return stack.getItem() instanceof ItemNet && !ItemNet.containsEntity(stack);
  }
  public static boolean isFilledNet(ItemStack stack){
    return stack.getItem() instanceof ItemNet && ItemNet.containsEntity(stack);
  }

}
