package com.tfar.mobcatcher;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@Mod(value = MobCatcher.MODID)
public class MobCatcher {
  public static final String MODID = "mobcatcher";

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  @SuppressWarnings("unused")
  public static class RegistryEvents {

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
      IForgeRegistry<Item> registry = e.getRegistry();
      Item.Properties properties = new Item.Properties().group(ItemGroup.COMBAT);

      registerItem(new ItemNet(properties), "net", registry);
      registerItem(new ItemNetLauncher(properties), "net_launcher", registry);
    }

    private static void registerItem(Item item, String name, IForgeRegistry<Item> registry) {
      registry.register(item.setRegistryName(name));
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> e) {

      e.getRegistry().register(
              EntityType.Builder
                      .<NetEntity>create(NetEntity::new, EntityClassification.MISC)
                      .setShouldReceiveVelocityUpdates(true)
                      .setUpdateInterval(1)
                      .setTrackingRange(128)
                      .size(.6f, .6f)
                      .build("net")
                      .setRegistryName("net"));
    }
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
      DispenserBlock.registerDispenseBehavior(ObjectHolders.net, new ProjectileDispenseBehavior() {
        /**
         * Return the projectile entity spawned by this dispense behavior.
         */
        @Nonnull
        @Override
        protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition pos, @Nonnull ItemStack stack) {
          ItemStack newStack = stack.copy();
          newStack.setCount(1);
          return new NetEntity(pos.getX(), pos.getY(), pos.getZ(), world, newStack);
        }
      });
    }
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  @SuppressWarnings("unused")
  public static class ClientEvents {
    @SubscribeEvent
    public static void registerModels(FMLClientSetupEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(ObjectHolders.Entities.net, render -> new SpriteRenderer<>(render, Minecraft.getInstance().getItemRenderer()));
    }
  }

  @ObjectHolder(value = MODID)
  public static class ObjectHolders {
    public static final Item net = null;
    @ObjectHolder(MODID)
    public static class Entities {
      public static final EntityType<NetEntity> net = null;
    }
  }
}
