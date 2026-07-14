package net.chowdaslime.resonantinstruments.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ResonanceChamberBlockEntity extends BlockEntity {

    public ResonanceChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_CHAMBER_BLOCK_ENTITY.get(), pos, state);
    }
}
