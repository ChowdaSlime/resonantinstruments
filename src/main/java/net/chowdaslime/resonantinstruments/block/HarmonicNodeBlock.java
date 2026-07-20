package net.chowdaslime.resonantinstruments.block;

import net.chowdaslime.resonantinstruments.block.entity.HarmonicNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HarmonicNodeBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape SHAPE = buildShape();

    private static VoxelShape buildShape() {
        VoxelShape shape = Block.box(4, 0, 4, 12, 2, 12);
        shape = Shapes.join(shape, Block.box(4, 2, 4, 5, 16, 5), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(11, 2, 4, 12, 16, 5), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(4, 2, 11, 5, 16, 12), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(11, 2, 11, 12, 16, 12), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(4, 14, 4, 12, 16, 12), BooleanOp.OR);
        return shape;
    }

    public HarmonicNodeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(LIT, false)
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HarmonicNodeBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LIT, false);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof HarmonicNodeBlockEntity node)) {
            return InteractionResult.PASS;
        }

        ItemStack handStack = player.getItemInHand(hand);

        if (node.hasPotion()) {
            ItemStack old = node.extractPotion();
            if (!old.isEmpty()) {
                if (!player.getInventory().add(old)) {
                    player.drop(old, false);
                }
            }
            return InteractionResult.CONSUME;
        }

        if (handStack.isEmpty() || !handStack.has(DataComponents.POTION_CONTENTS)) {
            return InteractionResult.PASS;
        }

        boolean inserted = node.tryInsertPotion(handStack);
        if (inserted && !player.getAbilities().instabuild) {
            handStack.shrink(1);
        }
        return inserted ? InteractionResult.CONSUME : InteractionResult.PASS;
    }
}