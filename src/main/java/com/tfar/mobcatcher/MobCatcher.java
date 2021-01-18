package com.tfar.mobcatcher;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

@Mod(value = MobCatcher.MODID)
public class MobCatcher {
  public static final String MODID = "mobcatcher";

  public static final ITag<EntityType<?>> blacklisted = EntityTypeTags.getTagById(new ResourceLocation(MobCatcher.MODID,"blacklisted").toString());

  public MobCatcher() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    bus.addGenericListener(Item.class,this::registerItems);
    bus.addGenericListener(EntityType.class,this::registerEntity);
    bus.addListener(this::init);
    bus.addListener(this::configChange);

    //MinecraftForge.EVENT_BUS.addListener(this::playerRespawn);
  }

  private void configChange(ModConfig.ModConfigEvent e) {
    if (e.getConfig().getModId().equals(MODID)) {
      int durability = ServerConfig.net_durability.get();
      if (durability > -1) {
        net_item.maxStackSize = 1;
        net_item.maxDamage = durability;
      }
    }
  }

  public static final ServerConfig SERVER;
  public static final ForgeConfigSpec SERVER_SPEC;

  static {
    final Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
    SERVER_SPEC = specPair2.getRight();
    SERVER = specPair2.getLeft();
  }

    public void registerItems(RegistryEvent.Register<Item> e) {
      IForgeRegistry<Item> registry = e.getRegistry();
      Item.Properties properties = new Item.Properties().group(ItemGroup.COMBAT);

      registerItem(net_item, "net", registry);
      registerItem(new ItemNetLauncher(properties), "net_launcher", registry);
    }

    private static void registerItem(Item item, String name, IForgeRegistry<Item> registry) {
      registry.register(item.setRegistryName(name));
    }

    public void registerEntity(RegistryEvent.Register<EntityType<?>> e) {
      e.getRegistry().register(net.setRegistryName("net"));
    }

    public void init(FMLCommonSetupEvent event) {
      DispenserBlock.registerDispenseBehavior(net_item, new ProjectileDispenseBehavior() {
        /**
         * Return the projectile entity spawned by this dispense behavior.
         */
        @Nonnull
        @Override
        protected ProjectileEntity getProjectileEntity(@Nonnull World world, @Nonnull IPosition pos, @Nonnull ItemStack stack) {
          ItemStack newStack = stack.copy();
          newStack.setCount(1);
          return new NetEntity(pos.getX(), pos.getY(), pos.getZ(), world, newStack);
        }
      });
    }

  public static EntityType<NetEntity> net = EntityType.Builder
          .<NetEntity>create(NetEntity::new, EntityClassification.MISC)
          .setShouldReceiveVelocityUpdates(true)
          .setUpdateInterval(1)
          .setTrackingRange(128)
          .size(.6f, .6f)
          .build("net");
  public static Item net_item = new ItemNet(new Item.Properties().group(ItemGroup.COMBAT));


  public static class ServerConfig {

    public static ForgeConfigSpec.IntValue net_durability;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
      builder.push("general");
      net_durability = builder.comment("Number of uses before mob catcher breaks, damaged every time a mob is released, -1 disables durability, numbers above will set stack size to 1")
              .defineInRange("net_durability", -1, -1, Integer.MAX_VALUE);
      builder.pop();
    }
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  @SuppressWarnings("unused")
  public static class ClientEvents {
    @SubscribeEvent
    public static void registerModels(FMLClientSetupEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(net, render -> new SpriteRenderer<>(render, Minecraft.getInstance().getItemRenderer()));
    }
  }
}
