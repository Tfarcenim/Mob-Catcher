package tfar.mobcatcher;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final EntityType<NetEntity> net = EntityType.Builder
            .<NetEntity>of(NetEntity::new, MobCategory.MISC)
            .clientTrackingRange(4)
            .updateInterval(10)
            .sized(.6f, .6f)
            .build("net");
    public static final Item net_item = new NetItem(new Item.Properties());
    public static final Item net_launcher = new NetLauncherItem(new Item.Properties());


    static {
        Registry.register(BuiltInRegistries.ENTITY_TYPE,MobCatcher.id("net"),net);
        Registry.register(BuiltInRegistries.ITEM,MobCatcher.id("net"),net_item);
        Registry.register(BuiltInRegistries.ITEM,MobCatcher.id("net_launcher"),net_launcher);
    }

    public static void init() {

    }
}
