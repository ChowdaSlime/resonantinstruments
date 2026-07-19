package net.chowdaslime.resonantinstruments.datagen;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, ResonantInstruments.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.UNATTUNED_FORK.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.HARMONIC_OF_VITALITY.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.HARMONIC_OF_RESONANCE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.HARMONIC_OF_CHRONOS.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.HARMONIC_OF_DIVINATION.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.HARMONIC_OF_ALCHEMY.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.GUIDE_BOOK.get(), ModelTemplates.FLAT_ITEM);

        Block chamber = ModBlocks.RESONANCE_CHAMBER.get();
        var chamberModelLoc = ModelLocationUtils.getModelLocation(chamber);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(chamber, BlockModelGenerators.plainVariant(chamberModelLoc))
                        .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                                .select(Direction.NORTH, v -> v)
                                .select(Direction.EAST,  BlockModelGenerators.Y_ROT_90)
                                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                                .select(Direction.WEST,  BlockModelGenerators.Y_ROT_270))
        );

        blockModels.registerSimpleItemModel(chamber.asItem(), chamberModelLoc);

        Block node = ModBlocks.HARMONIC_NODE.get();
        var nodeModelLoc = ModelLocationUtils.getModelLocation(node);

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(node, BlockModelGenerators.plainVariant(nodeModelLoc))
                        .with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
                                .select(Direction.NORTH, v -> v)
                                .select(Direction.EAST,  BlockModelGenerators.Y_ROT_90)
                                .select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                                .select(Direction.WEST,  BlockModelGenerators.Y_ROT_270))
        );

        blockModels.registerSimpleItemModel(node.asItem(), nodeModelLoc);
    }
}