package tfar.mobcatcher;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobCatcher {
    public static final String MODID = "mobcatcher";

    public static final Logger LOG = LogManager.getLogger();
    public static final TagKey<EntityType<?>> blacklisted = create("blacklisted");

    public static void init(){

    }

    public static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath(MODID,path);
    }

    private static TagKey<EntityType<?>> create(String pName) {
      return TagKey.create(Registries.ENTITY_TYPE, id(pName));
    }
}
