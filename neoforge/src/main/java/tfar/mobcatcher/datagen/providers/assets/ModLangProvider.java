package tfar.mobcatcher.datagen.providers.assets;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.data.LanguageProvider;
import tfar.mobcatcher.MobCatcher;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import org.codehaus.plexus.util.StringUtils;
import tfar.mobcatcher.ModItems;


public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput gen) {
        super(gen, MobCatcher.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        add(ModItems.net_launcher, "Mob Capture Device Launcher");
        add(ModItems.net_item, "Mob Capture Device");
        add("mobcatcher.capturing", "Capturing");
        add("mobcatcher.releasing", "Releasing");
        add("mobcatcher.health", "Health");
        add(ModItems.net, "Mob Capture Device");
        creativeTab(ModItems.tab,"Mob Catcher");
    }

    public void defaultName(Item item) {
        addItem(() -> item, getNameFromItem(item));
    }

    public void defaultName(Block block) {
        addBlock(() -> block, getNameFromBlock(block));
    }

    public void creativeTab(CreativeModeTab tab, String value) {
        MutableComponent component = (MutableComponent) tab.getDisplayName();
        addComponent(component, value);
    }

    public void addComponent(MutableComponent component,String value) {
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(),value);
        }
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromBlock(Block block) {
        return StringUtils.capitaliseAllWords(block.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    protected void addDesc(Item item, String s) {
        add(item.getDescriptionId() + ".desc", s);
    }
}
