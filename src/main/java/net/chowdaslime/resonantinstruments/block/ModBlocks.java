package net.chowdaslime.resonantinstruments.block;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(ResonantInstruments.MODID);

    public static final DeferredBlock<Block> RESONANCE_CHAMBER = registerBlock(
            "resonance_chamber",
            properties -> new ResonanceChamberBlock(properties.strength(3.0F, 3.0F).requiresCorrectToolForDrops().noOcclusion())
    );

    private static <T extends Block> DeferredBlock<T> registerBlock(
            String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        ModItems.ITEMS.registerItem(name,
                properties -> new BlockItem(toReturn.value(), properties.useBlockDescriptionPrefix()));
        return toReturn;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}