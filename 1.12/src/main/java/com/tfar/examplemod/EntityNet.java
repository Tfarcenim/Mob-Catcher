package com.tfar.examplemod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityNet extends EntityThrowable {

  public EntityNet(World worldIn)
  {
    super(worldIn);
  }

  public EntityNet(World worldIn, EntityLivingBase throwerIn)
  {
    super(worldIn, throwerIn);
  }

  public EntityNet(World worldIn, double x, double y, double z)
  {
    super(worldIn, x, y, z);
  }


  /**
   * Called when this EntityThrowable hits a block or entity.
   *
   * @param result
   */
  @Override
  protected void onImpact(@Nonnull RayTraceResult result) {
    if (world.isRemote)return;
    Entity target = result.entityHit;
    if (!(target instanceof EntityLiving) || !target.isNonBoss() || !target.isEntityAlive()){
      EntityItem entityItem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(MobCatcher.ObjectHolders.net));
      world.spawnEntity(entityItem);
    }
    else {
      String entityID = EntityList.getKey(target).toString();
     // if (isBlacklisted(entityID)) return false;
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("entity", entityID);
      nbt.setInteger("id", EntityList.getID(target.getClass()));
      target.writeToNBT(nbt);
      ItemStack stack = new ItemStack(MobCatcher.ObjectHolders.net);
      stack.setTagCompound(nbt);
      EntityItem entityItem = new EntityItem(this.world, this.posX, this.posY, this.posZ, stack);
      world.spawnEntity(entityItem);
      target.setDead();
    }
    this.setDead();
  }
}
