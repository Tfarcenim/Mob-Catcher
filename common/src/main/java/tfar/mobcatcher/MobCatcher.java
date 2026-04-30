package tfar.mobcatcher;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class MobCatcher {
    public static final String MODID = "mobcatcher";
    public static final TagKey<EntityType<?>> blacklisted = create("blacklisted");

    public static void init(){

    }

    public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath(MODID,path);
    }

    private static TagKey<EntityType<?>> create(String pName) {
      return TagKey.create(Registries.ENTITY_TYPE, id(pName));
    }
}
