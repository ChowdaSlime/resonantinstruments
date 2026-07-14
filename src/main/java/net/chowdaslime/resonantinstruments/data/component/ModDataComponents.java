package net.chowdaslime.resonantinstruments.data.component;

import com.mojang.serialization.Codec;
import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ResonantInstruments.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BLOCKS_MINED =
            DATA_COMPONENTS.register("blocks_mined", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> TUNED_BLOCK_ID =
            DATA_COMPONENTS.register("tuned_block_id", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}