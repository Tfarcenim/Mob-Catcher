package tfar.mobcatcher;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final EntityType<NetEntity> net = EntityType.Builder
            .<NetEntity>of(NetEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setUpdateInterval(1)
            .setTrackingRange(128)
            .sized(.6f, .6f)
            .build("net");
    public static final Item net_item = new NetItem(new Item.Properties());
    public static final Item net_launcher = new NetLauncherItem(new Item.Properties());

    public static final DataComponents

    static {
        Registry.register()
    }

    public void init() {

    }
}
