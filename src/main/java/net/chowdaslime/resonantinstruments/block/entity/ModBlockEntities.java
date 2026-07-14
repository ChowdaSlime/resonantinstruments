package net.chowdaslime.resonantinstruments.block.entity;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ResonantInstruments.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonanceChamberBlockEntity>> RESONANCE_CHAMBER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("resonance_chamber_block_entity", () ->
                    new BlockEntityType<>(ResonanceChamberBlockEntity::new, Set.of(ModBlocks.RESONANCE_CHAMBER.get()))
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}