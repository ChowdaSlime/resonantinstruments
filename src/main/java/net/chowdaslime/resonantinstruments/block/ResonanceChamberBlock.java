package net.chowdaslime.resonantinstruments.block;

import net.chowdaslime.resonantinstruments.block.entity.ModBlockEntities;
import net.chowdaslime.resonantinstruments.block.entity.ResonanceChamberBlockEntity;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class ResonanceChamberBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    public ResonanceChamberBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResonanceChamberBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof ResonanceChamberBlockEntity chamber)) {
            return InteractionResult.PASS;
        }

        ItemStack handStack = player.getItemInHand(hand);

        if (chamber.hasItem()) {
            if (chamber.getPhase() != ResonanceChamberBlockEntity.Phase.IDLE) {
                return InteractionResult.CONSUME;
            }
            ItemStack old = chamber.extractItem();
            if (!old.isEmpty()) {
                if (!player.getInventory().add(old)) {
                    player.drop(old, false);
                }
            }
            return InteractionResult.CONSUME;
        }

        if (handStack.isEmpty() || !handStack.is(ModItems.UNATTUNED_FORK.get())) {
            return InteractionResult.PASS;
        }

        boolean inserted = chamber.tryInsertFork(handStack);
        if (inserted && !player.getAbilities().instabuild) {
            handStack.shrink(1);
        }
        return inserted ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        if (type == ModBlockEntities.RESONANCE_CHAMBER_BLOCK_ENTITY.get()) {
            return (BlockEntityTicker<T>) (lvl, pos, st, be) ->
                    ResonanceChamberBlockEntity.serverTick(lvl, pos, st, (ResonanceChamberBlockEntity) be);
        }
        return null;
    }
}