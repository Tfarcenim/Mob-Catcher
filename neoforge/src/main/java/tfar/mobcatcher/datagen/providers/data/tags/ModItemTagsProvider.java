package tfar.mobcatcher.datagen.providers.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import tfar.mobcatcher.MobCatcher;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(pGenerator,lookupProvider, MobCatcher.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
