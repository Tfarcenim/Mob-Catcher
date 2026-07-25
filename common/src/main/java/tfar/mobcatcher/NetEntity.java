package tfar.mobcatcher;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;


public class NetEntity extends ThrowableItemProjectile {

    public NetEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public NetEntity(double x, double y, double z, Level world, ItemStack newStack) {
        super(ModItems.net, x, y, z, world,newStack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.net_item;
    }


    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide()) {
            ItemStack stack = getItem();
            boolean containsEntity = NetItem.containsEntity(stack);
            if (containsEntity) {
                Entity entity = NetItem.getEntityFromStack(stack, level(), true);
                BlockPos pos = result.getBlockPos();

                entity.snapTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
                stack.remove(DataComponents.ENTITY_DATA);
                level().addFreshEntity(entity);
                spawnAtLocation((ServerLevel) level(), stack);
            /*if (stack.isDamageableItem()) {
                Entity owner = this.getOwner();
                if (owner instanceof LivingEntity) {
                    stack.hurtAndBreak(1, (LivingEntity) owner);
                }
            }*/
            } else {
                spawnAtLocation((ServerLevel) level(), stack);
            }
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide()) {
            Entity target = result.getEntity();
            if (!NetItem.isValidCapture(target)) return;
            CompoundTag nbt = NetItem.getNBTfromEntity(target);

            getItem().set(DataComponents.ENTITY_DATA, TypedEntityData.of(target.getType(), nbt));
            spawnAtLocation((ServerLevel) level(), getItem());
            target.discard();
            discard();
        }
    }
}