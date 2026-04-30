package tfar.mobcatcher;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Set;

public class NetItem extends Item implements ProjectileItem {

  public static final String KEY = "entity_holder";
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
    entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0, 0);
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
    filledNet.set(DataComponents.ENTITY_DATA, CustomData.of(nbt));
    ItemStack newerStack = ItemUtils.createFilledResult(player.getItemInHand(hand),player,filledNet);
    target.discard();
    player.getCooldowns().addCooldown(ModItems.net_item, 5);
    player.setItemInHand(hand, newerStack);
    return InteractionResult.sidedSuccess(player.level().isClientSide());
  }

  static Set<String> warned;

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
    if (containsEntity(stack)) {
      CompoundTag holder = getEntityData(stack);
      String id = holder.getString("id");
      EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(id));
      tooltip.add(type.getDescription());
      tooltip.add(Component.translatable("mobcatcher.health").append(": "+ getEntityData(stack).getDouble("Health")));
    }
  }

  public static Component getNameFromStoredEntity(ItemStack stack) {
    if (stack.has(DataComponents.CUSTOM_NAME)) {
      return stack.get(DataComponents.CUSTOM_NAME);
    }
    EntityType<?> type = getType(stack);
    return type.getDescription();
  }

  @Override
  public Component getName(ItemStack stack) {
    Component nameC = super.getName(stack);
    if (!containsEntity(stack))
      return nameC;
    else return ((MutableComponent) nameC)
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

  public static CompoundTag getEntityData(ItemStack stack) {
    return containsEntity(stack) ?  stack.get(DataComponents.ENTITY_DATA).copyTag() : new CompoundTag();
  }

  private static boolean isBlacklisted(EntityType<?> type) {
    return type.is(MobCatcher.blacklisted);
  }

  public static boolean isValidCapture(Entity target) {
    return !(target instanceof Player) && !isBlacklisted(target.getType()) && target.isAlive() &&
            !target.isPassenger() && !target.isVehicle();

  }

  private static final MapCodec<EntityType<?>> ENTITY_TYPE_FIELD_CODEC = BuiltInRegistries.ENTITY_TYPE
          .byNameCodec().fieldOf("id");

  public static EntityType<?> getType(ItemStack pStack) {
    CustomData customdata = pStack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
    return !customdata.isEmpty() ? customdata.read(ENTITY_TYPE_FIELD_CODEC).getOrThrow() : EntityType.PIG;
  }

  @Nullable
  public static Entity getEntityFromStack(ItemStack stack, Level world, boolean withInfo) {
    CustomData customdata = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
    if (customdata.isEmpty()) return null;
    EntityType<?> type = customdata.read(ENTITY_TYPE_FIELD_CODEC).getOrThrow();
    Entity entity = type.create(world);
    if (withInfo) {
      entity.load(customdata.copyTag());
    }
    return entity;
  }

  public static CompoundTag getNBTfromEntity(Entity entity) {
    CompoundTag nbt = new CompoundTag();
    entity.save(nbt);
    return nbt;
  }

  @Override
  public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
    NetEntity entity = new NetEntity(position.x(),position.y(),position.z(),level,itemStack);
    return entity;
  }
}
