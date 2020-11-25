package com.tfar.mobcatcher;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

import static com.tfar.mobcatcher.ItemNet.containsEntity;

public class NetEntity extends ProjectileItemEntity {

  public ItemStack stack = ItemStack.EMPTY;

  public NetEntity(EntityType<? extends ProjectileItemEntity> entityType, World world) {
    super(entityType, world);
  }

  public NetEntity(double x, double y, double z, World world, ItemStack newStack) {
    super(MobCatcher.net, x, y, z, world);
    this.stack = newStack;
  }

  @Nonnull
  @Override
  protected Item getDefaultItem() {
    return MobCatcher.net_item;
  }

  /**
   * Called when this EntityThrowable hits a block or entity.
   *
   * @param result
   */
  @Override
  protected void onImpact(@Nonnull RayTraceResult result) {
    if (world.isRemote || !this.isAlive()) return;
    RayTraceResult.Type type = result.getType();

    if (containsEntity(stack)) {

      Entity entity = ItemNet.getEntityFromStack(stack, world, true);
      BlockPos pos;
      if (type == RayTraceResult.Type.ENTITY)
        pos = ((EntityRayTraceResult) result).getEntity().getPosition();
      else
        pos = ((BlockRayTraceResult) result).getPos();
      entity.setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
      stack.removeChildTag(ItemNet.KEY);
      world.addEntity(entity);
      ItemEntity emptynet = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), new ItemStack(stack.getItem()));
      world.addEntity(emptynet);
    } else if (!containsEntity(stack)) {
      if (type == RayTraceResult.Type.ENTITY) {
        EntityRayTraceResult entityRayTrace = (EntityRayTraceResult) result;
        Entity target = entityRayTrace.getEntity();
        if (target instanceof PlayerEntity || !target.isAlive()) return;
        if (containsEntity(stack)) return;
        Item item = stack.getItem();
        if (item instanceof ItemNet && ((ItemNet) item).isBlacklisted(target.getType())) return;

        CompoundNBT nbt = ItemNet.getNBTfromEntity(target);
        ItemStack newStack = stack.copy();
        newStack.getOrCreateTag().put(ItemNet.KEY,nbt);
        ItemEntity itemEntity = new ItemEntity(target.world, target.getPosX(), target.getPosY(), target.getPosZ(), newStack);
        world.addEntity(itemEntity);
        target.remove();
      } else {
        ItemEntity emptynet = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), new ItemStack(stack.getItem()));
        world.addEntity(emptynet);
      }
    }
    this.remove();
  }

  public void writeAdditional(CompoundNBT p_213281_1_) {
    super.writeAdditional(p_213281_1_);
    if (!stack.isEmpty()) {
      p_213281_1_.put("mobcatcher", stack.write(stack.getOrCreateTag()));
    }

  }

  public void readAdditional(CompoundNBT p_70037_1_) {
    super.readAdditional(p_70037_1_);
    stack = ItemStack.read(p_70037_1_.getCompound("mobcatcher"));
  }

  @Nonnull
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
