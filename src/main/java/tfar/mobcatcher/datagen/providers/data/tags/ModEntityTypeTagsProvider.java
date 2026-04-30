package tfar.mobcatcher.datagen.providers.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import tfar.mobcatcher.MobCatcher;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator,lookupProvider, MobCatcher.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(MobCatcher.blacklisted).add(EntityType.PAINTING);
    }
}
