package tfar.mobcatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class MobCatcherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ModItems.init();
        ModDataComponents.init();
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (hitResult != null && !player.isSpectator()&& stack.is(ModItems.net_item) && hitResult.getEntity() instanceof LivingEntity livingTarget){
                return NetItem.interactLivingEntityS(stack,player,livingTarget,hand);
            }
            return InteractionResult.PASS;
        });
    }
}
