package tfar.mobcatcher.datagen.providers.assets;

import net.minecraft.data.PackOutput;
import tfar.mobcatcher.MobCatcher;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;


public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput gen) {
        super(gen, MobCatcher.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {


    }

    public void defaultName(Item item) {
        addItem(() -> item,getNameFromItem(item));
    }

    public void defaultName(Block block) {
        addBlock(() -> block,getNameFromBlock(block));
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromBlock(Block block) {
        return StringUtils.capitaliseAllWords(block.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    protected void addDesc(Item item,String s) {
        add(item.getDescriptionId()+".desc",s);
    }
}
