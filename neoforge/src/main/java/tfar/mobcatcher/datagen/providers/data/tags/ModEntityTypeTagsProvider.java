package tfar.mobcatcher.datagen.providers.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import tfar.mobcatcher.MobCatcher;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(pGenerator,lookupProvider, MobCatcher.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(MobCatcher.blacklisted).addTags(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED).add(EntityType.PAINTING);
    }
}
