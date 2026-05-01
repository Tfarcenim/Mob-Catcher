package tfar.mobcatcher;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModItems {


    public static final EntityType<NetEntity> net = EntityType.Builder
            .<NetEntity>of(NetEntity::new, MobCategory.MISC)
            .clientTrackingRange(4)
            .updateInterval(10)
            .sized(.6f, .6f)
            .build(ResourceKey.create(Registries.ENTITY_TYPE,MobCatcher.id("net")));
    public static final Item net_item = new NetItem(new Item.Properties().setId(key("net")));
    public static final Item net_launcher = new NetLauncherItem(new Item.Properties().setId(key("net_launcher")));

    public static final CreativeModeTab tab = CreativeModeTab.builder(null,-1)
            .icon(net_item.asItem()::getDefaultInstance)
            .title(Component.translatable("itemGroup.mobcatcher"))
            .displayItems((parameters, output) -> {
                output.accept(net_item);
                output.accept(net_launcher);
            })
            .build();

    static ResourceKey<Item> key(String path) {
        return ResourceKey.create(Registries.ITEM,MobCatcher.id(path));
    }

    static {
        Registry.register(BuiltInRegistries.ENTITY_TYPE,MobCatcher.id("net"),net);
        Registry.register(BuiltInRegistries.ITEM,MobCatcher.id("net"),net_item);
        Registry.register(BuiltInRegistries.ITEM,MobCatcher.id("net_launcher"),net_launcher);
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,MobCatcher.id("tab"),tab);
    }

    public static void init() {

    }
}
