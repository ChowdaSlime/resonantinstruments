package net.chowdaslime.resonantinstruments.datagen;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ResonantInstruments.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.RESONANCE_CHAMBER.get())
                .add(ModBlocks.HARMONIC_NODE.get())
                .add(ModBlocks.MARBLE.get())
                .add(ModBlocks.RUNED_MARBLE.get())
                .add(ModBlocks.ENGRAVED_MARBLE.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.RESONANCE_CHAMBER.get())
                .add(ModBlocks.HARMONIC_NODE.get())
                .add(ModBlocks.MARBLE.get())
                .add(ModBlocks.RUNED_MARBLE.get())
                .add(ModBlocks.ENGRAVED_MARBLE.get());
    }
}