package tfar.mobcatcher;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class NetLauncherItem extends Item {

  public NetLauncherItem(Properties properties) {
    super(properties);
  }

  protected ItemStack findNet(Player player) {
    ItemStack stack = player.getMainHandItem();
    if (isCaptureMode(stack)){
    if (isEmptyNet(player.getItemInHand(InteractionHand.OFF_HAND))) {
      return player.getItemInHand(InteractionHand.OFF_HAND);
    } else if (isEmptyNet(player.getItemInHand(InteractionHand.MAIN_HAND))) {
      return player.getItemInHand(InteractionHand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
        ItemStack itemstack = player.getInventory().getItem(i);

        if (isEmptyNet(itemstack)) {
          return itemstack;
        }
      }
    }
      return ItemStack.EMPTY;
    }
    if (isFilledNet(player.getItemInHand(InteractionHand.OFF_HAND))) {
      return player.getItemInHand(InteractionHand.OFF_HAND);
    } else if (isFilledNet(player.getItemInHand(InteractionHand.MAIN_HAND))) {
      return player.getItemInHand(InteractionHand.MAIN_HAND);
    } else {
      for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
        ItemStack itemstack = player.getInventory().getItem(i);

        if (isFilledNet(itemstack)) {
          return itemstack;
        }
      }
    }
    return ItemStack.EMPTY;
  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
    if (entityLiving instanceof Player player) {
      ItemStack stackAmmo = this.findNet(player);

      int i = this.getUseDuration(stackAmmo,player) - timeLeft;
      if (i < 0) return;

      if (!stackAmmo.isEmpty() || player.getAbilities().instabuild) {
        if (stackAmmo.isEmpty()) stackAmmo = new ItemStack(ModItems.net_item);

        float f = getNetVelocity(i);

        if (f >= 0.1) {

          if (!worldIn.isClientSide) {
            NetItem itemNet = stackAmmo.getItem() instanceof NetItem ? (NetItem)stackAmmo.getItem() : (NetItem) ModItems.net_item;
            NetEntity netEntity = itemNet.createNet(worldIn, player, stackAmmo);
            netEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 0);

            worldIn.addFreshEntity(netEntity);
          }

          worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

          if (!player.getAbilities().instabuild) {
            stackAmmo.shrink(1);

            if (stackAmmo.isEmpty()) {
              player.getInventory().removeItem(stackAmmo);
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
  public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
    return 72000;
  }

  /**
   * Called when the equipped item is right clicked.
   */
  @Nonnull
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, @Nonnull InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (player.isCrouching()){
      boolean capture = isCaptureMode(stack);
      if (capture) {
        stack.set(ModDataComponents.RELEASE, Unit.INSTANCE);
      } else {
        stack.remove(ModDataComponents.RELEASE);
      }
      player.displayClientMessage(capture? RELEASE: CAPTURE,true);
      return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide());
    }
    boolean hasAmmo = !this.findNet(player).isEmpty();

    if (!player.getAbilities().instabuild && !hasAmmo) {
      return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
    } else {
      player.startUsingItem(hand);
      return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
  }

  public static final Component CAPTURE = Component.translatable("mobcatcher.capturing");
  public static final Component RELEASE = Component.translatable("mobcatcher.releasing");

  @Override
  @Nonnull
  public Component getName(@Nonnull ItemStack stack) {
    MutableComponent base = (MutableComponent) super.getName(stack);
    return base.append(" (").append(isCaptureMode(stack) ? CAPTURE : RELEASE).append(")");
  }

  //helpers
  public static boolean isCaptureMode(ItemStack stack){
    return !stack.has(ModDataComponents.RELEASE);
  }
  public static boolean isEmptyNet(ItemStack stack) {
    return stack.getItem() instanceof NetItem && !NetItem.containsEntity(stack);
  }
  public static boolean isFilledNet(ItemStack stack){
    return stack.getItem() instanceof NetItem && NetItem.containsEntity(stack);
  }

}
