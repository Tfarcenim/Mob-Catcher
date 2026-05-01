package tfar.mobcatcher;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Consumer;

public class NetItem extends Item implements ProjectileItem {

  private static final Logger LOGGER = LogUtils.getLogger();


  public NetItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();
    ItemStack stack = context.getItemInHand();
    if (world.isClientSide() || !containsEntity(stack)) return InteractionResult.FAIL;
    Entity entity = getEntityFromStack(stack, world, true);
    BlockPos blockPos = context.getClickedPos();
    entity.snapTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0, 0);
    stack.remove(DataComponents.ENTITY_DATA);
    world.addFreshEntity(entity);

    return InteractionResult.SUCCESS;
  }

  public static InteractionResult interactLivingEntityS(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
    if (containsEntity(stack)|| !isValidCapture(target))
      return InteractionResult.FAIL;
    CompoundTag nbt = getNBTfromEntity(target);
    ItemStack filledNet = stack.copy();
    filledNet.setCount(1);
    filledNet.set(DataComponents.ENTITY_DATA, TypedEntityData.of(target.getType(),nbt));
    ItemStack newerStack = ItemUtils.createFilledResult(player.getItemInHand(hand),player,filledNet);
    target.discard();
    player.getCooldowns().addCooldown(filledNet, 5);
    player.setItemInHand(hand, newerStack);
    return InteractionResult.SUCCESS;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
    super.appendHoverText(stack, context, display, builder, tooltipFlag);
    if (containsEntity(stack)) {
      TypedEntityData<EntityType<?>> holder = stack.get(DataComponents.ENTITY_DATA);
      EntityType<?> type = holder.type();
      CompoundTag data = holder.getUnsafe();
      builder.accept(type.getDescription());
      builder.accept(Component.translatable("mobcatcher.health").append(": "+ data.getDouble("Health").orElse(0d)));
    }
  }

  public static Component getNameFromStoredEntity(ItemStack stack) {
    if (stack.has(DataComponents.CUSTOM_NAME)) {
      return stack.get(DataComponents.CUSTOM_NAME);
    }
    EntityType<?> type = stack.get(DataComponents.ENTITY_DATA).type();
    return type.getDescription();
  }

  @Override
  public Component getName(ItemStack stack) {
    Component nameC = super.getName(stack);
    if (!containsEntity(stack))
      return nameC;
    else return nameC.copy()
            .append(" (")
            .append(getNameFromStoredEntity(stack))
            .append(")");
  }

  public NetEntity createNet(Level worldIn, LivingEntity shooter, ItemStack stack) {
    ItemStack newStack = stack.copy();
    newStack.setCount(1);
    return new NetEntity(shooter.getX(), shooter.getY() + 1.25, shooter.getZ(), worldIn, newStack);
  }

  //helper methods

  public static boolean containsEntity(ItemStack stack) {
    return stack.has(DataComponents.ENTITY_DATA);
  }


  public static boolean isValidCapture(Entity target) {
    return !(target instanceof Player) && !target.is(MobCatcher.blacklisted) && target.isAlive() &&
            !target.isPassenger() && !target.isVehicle();

  }


  @Nullable
  public static Entity getEntityFromStack(ItemStack stack, Level world, boolean withInfo) {
    TypedEntityData<EntityType<?>> customdata = stack.get(DataComponents.ENTITY_DATA);
    if (customdata==null) return null;
    EntityType<?> type = customdata.type();
    Entity entity = type.create(world, EntitySpawnReason.SPAWN_ITEM_USE);
    if (withInfo) {
      try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
        entity.load(TagValueInput.create(reporter, entity.registryAccess(),customdata.getUnsafe()));
      }
    }
    return entity;
  }

  public static CompoundTag getNBTfromEntity(Entity entity) {
    try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
      TagValueOutput entityData = TagValueOutput.createWithContext(reporter, entity.registryAccess());
      entity.save(entityData);
      return entityData.buildResult();
    }
  }

  @Override
  public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
    NetEntity entity = new NetEntity(position.x(),position.y(),position.z(),level,itemStack);
    return entity;
  }
}
