package tfar.mobcatcher.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.mobcatcher.datagen.providers.data.tags.ModBlockTagsProvider;
import tfar.mobcatcher.datagen.providers.data.tags.ModEntityTypeTagsProvider;
import tfar.mobcatcher.datagen.providers.data.tags.ModItemTagsProvider;
import tfar.mobcatcher.datagen.providers.ModRecipeProvider;
import tfar.mobcatcher.datagen.providers.assets.ModItemModelProvider;
import tfar.mobcatcher.datagen.providers.assets.ModLangProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModDatagen {
    public static void start(GatherDataEvent e) {
        DataGenerator dataGenerator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();
        boolean client = e.includeClient();
        boolean server = e.includeServer();
        dataGenerator.addProvider(client, new ModItemModelProvider(packOutput, helper));
        dataGenerator.addProvider(client, new ModLangProvider(packOutput));

        dataGenerator.addProvider(server, new ModRecipeProvider(packOutput));
        BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider, helper);
        dataGenerator.addProvider(server, blockTagsProvider);
        dataGenerator.addProvider(server, new ModItemTagsProvider(packOutput,lookupProvider, blockTagsProvider, helper));
        dataGenerator.addProvider(server, new ModEntityTypeTagsProvider(packOutput,lookupProvider, helper));
    }
}
