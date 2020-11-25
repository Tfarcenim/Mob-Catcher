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

  public static final ITag<EntityType<?>> blacklisted = EntityTypeTags.getTagById(new ResourceLocation(MobCatcher.MODID,"blacklisted").toString());

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  @SuppressWarnings("unused")
  public static class RegistryEvents {


    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
      IForgeRegistry<Item> registry = e.getRegistry();
      Item.Properties properties = new Item.Properties().group(ItemGroup.COMBAT);

      registerItem(net_item, "net", registry);
      registerItem(new ItemNetLauncher(properties), "net_launcher", registry);
    }

    private static void registerItem(Item item, String name, IForgeRegistry<Item> registry) {
      registry.register(item.setRegistryName(name));
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> e) {
      e.getRegistry().register(net.setRegistryName("net"));
    }
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
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
  }

  public static EntityType<NetEntity> net = EntityType.Builder
          .<NetEntity>create(NetEntity::new, EntityClassification.MISC)
          .setShouldReceiveVelocityUpdates(true)
          .setUpdateInterval(1)
          .setTrackingRange(128)
          .size(.6f, .6f)
          .build("net");
  public static Item net_item = new ItemNet(new Item.Properties().group(ItemGroup.COMBAT));


  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  @SuppressWarnings("unused")
  public static class ClientEvents {
    @SubscribeEvent
    public static void registerModels(FMLClientSetupEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(net, render -> new SpriteRenderer<>(render, Minecraft.getInstance().getItemRenderer()));
    }
  }
}
