package net.chowdaslime.resonantinstruments.datagen;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ResonantInstruments.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.MINING_ENCHANTABLE)
                .add(ModItems.HARMONIC_OF_RESONANCE.get());
        this.tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(ModItems.HARMONIC_OF_RESONANCE.get());
        this.tag(ItemTags.MINING_LOOT_ENCHANTABLE)
                .add(ModItems.HARMONIC_OF_RESONANCE.get());
    }
}