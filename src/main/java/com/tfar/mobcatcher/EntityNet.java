package com.tfar.mobcatcher;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityNet extends EntityThrowable {

  protected ItemStack stack;

  public EntityNet(World worldIn)
  {
    super(worldIn);
  }

  public EntityNet(World worldIn, double x, double y, double z,ItemStack stack) {
    super(worldIn, x, y, z);
    this.stack = stack;
  }

  /**
   * Called when this EntityThrowable hits a block or entity.
   *
   * @param result
   */
  @Override
  protected void onImpact(@Nonnull RayTraceResult result) {
    if (world.isRemote || result.entityHit instanceof EntityPlayer || this.isDead) return;
    ItemStack netStack = getNet();
    if (netStack.getItem() instanceof ItemNet && ((ItemNet) netStack.getItem()).containsEntity(netStack)) {

      EnumFacing facing = result.sideHit;

      Entity entity = ((ItemNet)netStack.getItem()).getEntityFromStack(netStack, world, true);
      BlockPos pos;
      if (facing != null)
      pos = this.getPosition().offset(result.sideHit);
      else pos = new BlockPos(this.posX,this.posY,this.posZ);
      entity.setPositionAndRotation(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5, 0, 0);
      world.spawnEntity(entity);
      EntityItem entityItem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(netStack.getItem()));
      world.spawnEntity(entityItem);
      if (entity instanceof EntityLiving) ((EntityLiving) entity).playLivingSound();
    } else {

      Entity target = result.entityHit;
      if (!(target instanceof EntityLiving) || !target.isEntityAlive()) {
        EntityItem entityItem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(MobCatcher.ObjectHolders.net));
        world.spawnEntity(entityItem);
      } else {
        String entityID = EntityList.getKey(target).toString();
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
    }
    this.setDead();
  }

  @Override
  @Nonnull
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    ItemStack stack = getNet();
    if (!stack.isEmpty()){
      //Item item = stack.getItem();
      NBTTagCompound entityData = stack.getTagCompound();
      if (stack.hasTagCompound())
      compound.setTag("entity",entityData);
    }
    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);

  }

  /**
   * (abstract) Protected helper method to read subclass entity data from NBT.
   */
  @Override
  public void readEntityFromNBT(NBTTagCompound compound) {
    super.readEntityFromNBT(compound);
    if (compound.hasKey("entity")) {
      ItemStack stack = new ItemStack(MobCatcher.ObjectHolders.net);
      stack.setTagCompound(new NBTTagCompound());
      stack.getTagCompound().setTag("entity",compound.getCompoundTag("entity"));
      this.stack = stack;
    }
  }
  public ItemStack getNet(){
    return this.stack;
  }
}
