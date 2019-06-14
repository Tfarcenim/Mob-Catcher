package com.tfar.mobcatcher;

import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(value = MobCatcher.MODID)
public class MobCatcher
{
  public static final String MODID = "mobcatcher";

  private static Logger logger;

  @ObjectHolder(MODID+":net_type")
  public static EntityType<EntityNet> TYPE;

  public void init(FMLCommonSetupEvent event) {
    DispenserBlock.registerDispenseBehavior(ObjectHolders.net, new ProjectileDispenseBehavior() {
      /**
       * Return the projectile entity spawned by this dispense behavior.
       */
      @Nonnull
      @Override
      protected IProjectile getProjectileEntity(@Nonnull World world,@Nonnull IPosition pos,@Nonnull ItemStack stack) {
        ItemStack newStack = stack.copy(); stack.setCount(1);
        return new EntityNet(pos.getX(),pos.getY(),pos.getZ(),world,newStack);
      }
    });
  }


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
                      .create(EntityClassification.MISC).setShouldReceiveVelocityUpdates(true).setUpdateInterval(1)
                      .build("mobcatcher:net_type").setRegistryName("mobcatcher:net_type"));
    }
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
  @SuppressWarnings("unused")
  public static class ClientEvents {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
      RenderingRegistry.registerEntityRenderingHandler(EntityNet.class, renderManager -> new RenderNet(renderManager, Minecraft.getInstance().getItemRenderer(),new ResourceLocation(MODID, "textures/net.png")));
    }
  }

  @ObjectHolder(value = MODID)
  public static class ObjectHolders{
    public static Item net;
    public static Item net_launcher;
  }
}
