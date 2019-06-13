package com.tfar.mobcatcher;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
@Mod(modid = MobCatcher.MODID, name = MobCatcher.NAME, version = MobCatcher.VERSION)
public class MobCatcher
{
    public static final String MODID = "mobcatcher";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    public static final List<Item> MOD_ITEMS = new ArrayList<>();

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectHolders.net, new BehaviorProjectileDispense() {
            /**
             * Return the projectile entity spawned by this dispense behavior.
             */
            @Nonnull
            @Override
            protected IProjectile getProjectileEntity(@Nonnull World worldIn,@Nonnull IPosition pos,@Nonnull ItemStack stack) {
                ItemStack newStack = stack.copy(); stack.setCount(1);
                return new EntityNet(worldIn,pos.getX(),pos.getY(),pos.getZ(),newStack);
            }
        });
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e){
        IForgeRegistry<Item> registry = e.getRegistry();

        helper(new ItemNet(),"net",registry);
        helper(new ItemNetLauncher(),"net_launcher",registry);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (Item item : MOD_ITEMS)
            helper3(item);
        RenderingRegistry.registerEntityRenderingHandler(EntityNet.class, renderManager -> new RenderNet(renderManager, ObjectHolders.net, Minecraft.getMinecraft().getRenderItem(),new ResourceLocation(MODID, "textures/net.png")));

    }

    private static void helper3(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    private static void helper(Item item, String name, IForgeRegistry<Item> registry) {
        item.setRegistryName(name);
        item.setTranslationKey(item.getRegistryName().toString());
        item.setCreativeTab(CreativeTabs.COMBAT);
        MOD_ITEMS.add(item);
        registry.register(item);
    }

    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityEntry> e) {

        final ResourceLocation resourceLocation = new ResourceLocation(MODID, "net");

        e.getRegistry().register(
                EntityEntryBuilder.create()
                        .entity(EntityNet.class)
                        .id(resourceLocation, 0)
                        .name(resourceLocation.getPath())
                        .tracker(64, 1, true)
                        .build());
    }

    @GameRegistry.ObjectHolder(value = MODID)
    public static class ObjectHolders{
        public static final Item net = null;
        public static final Item net_launcher = null;
    }
}
