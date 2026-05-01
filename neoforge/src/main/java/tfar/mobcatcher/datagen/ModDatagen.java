package tfar.mobcatcher.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tfar.mobcatcher.datagen.providers.assets.ModModelProvider;
import tfar.mobcatcher.datagen.providers.data.tags.ModBlockTagsProvider;
import tfar.mobcatcher.datagen.providers.data.tags.ModEntityTypeTagsProvider;
import tfar.mobcatcher.datagen.providers.data.tags.ModItemTagsProvider;
import tfar.mobcatcher.datagen.providers.ModRecipeProvider;
import tfar.mobcatcher.datagen.providers.assets.ModLangProvider;
import net.minecraft.data.DataGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class ModDatagen {
    public static void start(GatherDataEvent.Client e) {
        DataGenerator dataGenerator = e.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();
        boolean client = true;
        boolean server = true;
        dataGenerator.addProvider(client, new ModModelProvider(packOutput));
        dataGenerator.addProvider(client, new ModLangProvider(packOutput));

        dataGenerator.addProvider(server, bindRegistries(ModRecipeProvider.Runner::new,lookupProvider));
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider);
        dataGenerator.addProvider(server, blockTagsProvider);
        dataGenerator.addProvider(server, new ModItemTagsProvider(packOutput,lookupProvider));
        dataGenerator.addProvider(server, new ModEntityTypeTagsProvider(packOutput,lookupProvider));
    }

    private static <T extends DataProvider> DataProvider.Factory<T> bindRegistries(
            BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, T> target, CompletableFuture<HolderLookup.Provider> registries
    ) {
        return output -> target.apply(output, registries);
    }
}
