package tfar.mobcatcher;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class NetItem extends Item {

  public static final String KEY = "entity_holder";
  private static final Logger LOGGER = LogUtils.getLogger();


  public NetItem(Properties properties) {
    super(properties);
  }

  @Override
  @Nonnull
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    Level world = context.getLevel();
    if (player == null)return InteractionResult.FAIL;
    ItemStack stack = context.getItemInHand();
    if (world.isClientSide || !containsEntity(stack)) return InteractionResult.FAIL;
    Entity entity = getEntityFromStack(stack, world, true);
    BlockPos blockPos = context.getClickedPos();
    entity.absMoveTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0, 0);
    stack.setTag(null);
    world.addFreshEntity(entity);
    if (this.canBeDepleted()) {
      stack.hurtAndBreak(1,player,playerEntity -> playerEntity.broadcastBreakEvent(context.getHand()));
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
    if (target.getCommandSenderWorld().isClientSide || containsEntity(stack)|| !isValidCapture(target))
      return InteractionResult.FAIL;
    CompoundTag nbt = getNBTfromEntity(target);
    ItemStack newerStack = stack.split(1);
    newerStack.getOrCreateTag().put(KEY,nbt);
    player.swing(hand);
 //   player.setItemInHand(hand,stack);
    if(!player.addItem(newerStack)){
      ItemEntity itemEntity = new ItemEntity(player.level,player.getX(),player.getY(),player.getZ(),newerStack);
      player.level.addFreshEntity(itemEntity);
    }
    target.discard();
    player.getCooldowns().addCooldown(this, 5);
    return InteractionResult.SUCCESS;
  }

  static Set<String> warned;
  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
    if (containsEntity(stack)) {
      CompoundTag holder = getEntityData(stack);
      String id = holder.getString("id");
      EntityType<?> type = Registry.ENTITY_TYPE.get(new ResourceLocation(id));
      tooltip.add(type.getDescription());
      tooltip.add(Component.translatable("mobcatcher.health").append(": "+ getEntityData(stack).getDouble("Health")));
    }
  }

  public static Component getNameFromStoredEntity(ItemStack stack) {
    CompoundTag holder = getEntityData(stack);
    if (holder.contains("CustomName", Tag.TAG_STRING)) {
      String s = holder.getString("CustomName");
      try {
        return Component.Serializer.fromJson(s);
      } catch (Exception exception) {
        if (!warned.contains(s)) {
          LOGGER.warn("Failed to parse entity custom name {}", s, exception);
          warned.add(s);
        }
      }
    }
    String id = holder.getString("id");
    EntityType<?> type = Registry.ENTITY_TYPE.get(new ResourceLocation(id));
    return type.getDescription();
  }

  @Override
  @Nonnull
  public Component getName(@Nonnull ItemStack stack) {
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

  public static boolean containsEntity(@Nonnull ItemStack stack) {
    return stack.hasTag() && stack.getTag().contains(KEY);
  }

  public static CompoundTag getEntityData(ItemStack stack) {
    return containsEntity(stack) ?  stack.getTag().getCompound(KEY) : new CompoundTag();
  }

  public static String getEntityID(CompoundTag nbt) {
    return nbt.getString("id");
  }

  private static boolean isBlacklisted(EntityType<?> type) {
    return type.is(MobCatcher.blacklisted);
  }

  public static boolean isValidCapture(Entity target) {
    return !(target instanceof Player) && !isBlacklisted(target.getType()) && target.isAlive() &&
            !target.isPassenger() && !target.isVehicle();

  }

  @Nullable
  public static Entity getEntityFromNBT(CompoundTag nbt, Level world, boolean withInfo) {
    if (nbt == null)return null;
    Entity entity = Registry.ENTITY_TYPE.get(new ResourceLocation(getEntityID(nbt))).create(world);
    if (withInfo) entity.load(nbt);
    return entity;
  }

  @Nullable
  public static Entity getEntityFromStack(ItemStack stack, Level world, boolean withInfo) {
    return getEntityFromNBT(stack.getTagElement(KEY),world,withInfo);
  }

  public static CompoundTag getNBTfromEntity(Entity entity) {
    CompoundTag nbt = new CompoundTag();
    entity.save(nbt);
    return nbt;
  }
}
