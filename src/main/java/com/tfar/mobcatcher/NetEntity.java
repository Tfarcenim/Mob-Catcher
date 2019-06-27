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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetEntity extends ProjectileItemEntity {

  protected ItemStack stack;

  public NetEntity(EntityType<? extends ProjectileItemEntity> entityType, World world) {
    super(entityType, world);
  }

  public NetEntity(double x, double y, double z, World world, ItemStack newStack) {
    super(MobCatcher.TYPE,x,y,z,world);
    this.stack = newStack;
  }

  @Nonnull
  @Override
  protected Item func_213885_i() {
    return MobCatcher.ObjectHolders.net;
  }

  public NetEntity(EntityType<? extends ProjectileItemEntity> entityType, double x, double y, double z, World worldIn, ItemStack stack) {
    super(entityType, x, y, z,worldIn);
    this.stack = stack;
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

    if (stack == null){
      this.remove();
      return;
    }

    if (((ItemNet)stack.getItem()).containsEntity(stack)){

      Entity entity = ((ItemNet)stack.getItem()).getEntityFromStack(stack, world, true);
      BlockPos pos;
      if (type == RayTraceResult.Type.ENTITY)
      pos = ((EntityRayTraceResult)result).getEntity().getPosition();
      else
        pos = ((BlockRayTraceResult)result).getPos();
      entity.setPositionAndRotation(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
      stack.setTag(new CompoundNBT());
      world.addEntity(entity);
      ItemEntity emptynet = new ItemEntity(this.world, this.posX, this.posY, this.posZ, new ItemStack(stack.getItem()));
      world.addEntity(emptynet);
    }

    if (!((ItemNet)stack.getItem()).containsEntity(stack)){

    if (type == RayTraceResult.Type.ENTITY) {
      EntityRayTraceResult entityRayTrace = (EntityRayTraceResult) result;
      Entity target = entityRayTrace.getEntity();
      if (target instanceof PlayerEntity || !target.isAlive()) return;
      if (((ItemNet) stack.getItem()).containsEntity(stack)) return;
      String entityID = EntityType.getKey(target.getType()).toString();
      if (((ItemNet) stack.getItem()).isBlacklisted(entityID)) return;

      CompoundNBT nbt = new CompoundNBT();
      nbt.putString("entity", entityID);
      nbt.putString("id", EntityType.getKey(target.getType()).toString());
      //would use target.writeAdditional(nbt); but of course it's protected because mahjong
      Method m = ObfuscationReflectionHelper.findMethod(Entity.class, "func_213281_b", CompoundNBT.class);
      try {
        m.invoke(target, nbt);
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
      ItemStack newStack = stack.copy();
      newStack.setTag(nbt);
      ItemEntity itemEntity = new ItemEntity(target.world, target.posX, target.posY, target.posZ, newStack);
      world.addEntity(itemEntity);
      target.remove();
    }else {
      ItemEntity emptynet = new ItemEntity(this.world, this.posX, this.posY, this.posZ, new ItemStack(stack.getItem()));
      world.addEntity(emptynet);
    }

    }
    this.remove();
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    if (!stack.isEmpty()){
      //Item item = stack.getItem();
      CompoundNBT entityData = stack.getTag();
      if (stack.hasTag())
      compound.put("entity",entityData);
    }
  }

  /**
   * (abstract) Protected helper method to read subclass entity data from NBT.
   */
  @Override
  public void read(CompoundNBT compound) {
    super.read(compound);
    if (compound.contains("entity")) {
      ItemStack stack = new ItemStack(MobCatcher.ObjectHolders.net);
      stack.getOrCreateTag().put("entity",compound.get("entity"));
      this.stack = stack;
    }
  }

  @Nonnull
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
