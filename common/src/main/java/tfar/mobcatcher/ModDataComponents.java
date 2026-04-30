package tfar.mobcatcher;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final DataComponentType<Unit> RELEASE = register(
            "release", p_331610_ -> p_331610_.persistent(Codec.unit(Unit.INSTANCE))
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));


    private static <T> DataComponentType<T> register(String pName, UnaryOperator<DataComponentType.Builder<T>> pBuilder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, MobCatcher.id(pName), pBuilder.apply(DataComponentType.builder()).build());
    }

    public static void init() {

    }
}
