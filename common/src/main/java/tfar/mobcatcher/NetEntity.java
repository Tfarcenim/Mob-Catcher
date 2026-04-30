package tfar.mobcatcher;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;

public class NetEntity extends ThrowableItemProjectile {

    public ItemStack stack = ItemStack.EMPTY;

    public NetEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public NetEntity(double x, double y, double z, Level world, ItemStack newStack) {
        super(ModItems.net, x, y, z, world);
        this.stack = newStack;
    }

    @Nonnull
    @Override
    protected Item getDefaultItem() {
        return ModItems.net_item;
    }


    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        boolean containsEntity = NetItem.containsEntity(stack);
        if (containsEntity) {
            Entity entity = NetItem.getEntityFromStack(stack, level(), true);
            BlockPos pos = result.getBlockPos();
            entity.absMoveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0);
            stack.remove(DataComponents.ENTITY_DATA);
            level().addFreshEntity(entity);
            spawnAtLocation(stack);
            /*if (stack.isDamageableItem()) {
                Entity owner = this.getOwner();
                if (owner instanceof LivingEntity) {
                    stack.hurtAndBreak(1, (LivingEntity) owner);
                }
            }*/
        } else {
            spawnAtLocation(stack);
        }
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (!NetItem.isValidCapture(target)) return;
        CompoundTag nbt = NetItem.getNBTfromEntity(target);
        stack.set(DataComponents.ENTITY_DATA, CustomData.of(nbt));
        spawnAtLocation(stack);
        target.discard();
        discard();
    }

    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (!stack.isEmpty()) {
            nbt.put(MobCatcher.MODID, stack.save(registryAccess()));
        }
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        stack = ItemStack.parseOptional(registryAccess(),nbt.getCompound(MobCatcher.MODID));
    }
}