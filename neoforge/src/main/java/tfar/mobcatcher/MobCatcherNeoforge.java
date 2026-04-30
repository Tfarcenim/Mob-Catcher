package tfar.mobcatcher;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.mobcatcher.datagen.ModDatagen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

@Mod(value = MobCatcher.MODID)
public class MobCatcherNeoforge {

  public MobCatcherNeoforge(IEventBus bus,ModContainer container) {
   // container.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
    bus.addListener(ModDatagen::start);
    bus.addListener(this::registerItems);
    bus.addListener(this::init);
    bus.addListener(this::configChange);
    NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
    MobCatcher.init();
  }

  void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
    Player player = event.getEntity();
    Entity target = event.getTarget();
    InteractionHand hand = event.getHand();
    ItemStack stack = player.getItemInHand(hand);
    if (stack.is(ModItems.net_item) && target instanceof LivingEntity livingTarget) {
      event.setCancellationResult(NetItem.interactLivingEntityS(stack,player,livingTarget,hand));
      event.setCanceled(true);
    }
  }

  private void configChange(ModConfigEvent e) {
    if (e.getConfig().getModId().equals(MobCatcher.MODID)) {
      int durability = ServerConfig.net_durability.get();
    //  if (durability > -1) {
      //  ModItems.net_item.maxStackSize = 1;
       // ModItems.net_item.maxDamage = durability;
     // }
    }
  }

  public static final ServerConfig SERVER;
  public static final ModConfigSpec SERVER_SPEC;

  static {
    final Pair<ServerConfig, ModConfigSpec> specPair2 = new ModConfigSpec.Builder().configure(ServerConfig::new);
    SERVER_SPEC = specPair2.getRight();
    SERVER = specPair2.getLeft();
  }

    public void registerItems(RegisterEvent e) {
    ModItems.init();
    ModDataComponents.init();
    }

  public void init(FMLCommonSetupEvent event) {
      DispenserBlock.registerProjectileBehavior(ModItems.net_item);
    }

  public static class ServerConfig {

    public static ModConfigSpec.IntValue net_durability;

    public ServerConfig(ModConfigSpec.Builder builder) {
      builder.push("general");
      net_durability = builder.comment("Number of uses before mob catcher breaks, damaged every time a mob is released, -1 disables durability, numbers above will set stack size to 1")
              .defineInRange("net_durability", -1, -1, Integer.MAX_VALUE);
      builder.pop();
    }
  }
}
