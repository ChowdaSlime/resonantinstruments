package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.block.HarmonicNodeBlock;
import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.chowdaslime.resonantinstruments.block.ResonanceChamberBlock;
import net.chowdaslime.resonantinstruments.block.entity.ResonanceChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class CarvingKnifeItem extends Item {
    public CarvingKnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState oldState = level.getBlockState(pos);

        BlockState newState = null;
        boolean isStrip = false;

        BlockState strippedState = oldState.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false);

        if (strippedState != null) {
            newState = strippedState;
            isStrip = true;
        } else if (oldState.is(Blocks.STRIPPED_SPRUCE_LOG)) {
            newState = ModBlocks.RESONANCE_CHAMBER.get().defaultBlockState()
                    .setValue(ResonanceChamberBlock.FACING, context.getHorizontalDirection().getOpposite());
        } else if (oldState.is(ModBlocks.RESONANCE_CHAMBER.get())) {
            if (level.getBlockEntity(pos) instanceof ResonanceChamberBlockEntity chamber
                    && chamber.getPhase() != ResonanceChamberBlockEntity.Phase.IDLE) {
                return InteractionResult.FAIL;
            }
            newState = ModBlocks.HARMONIC_NODE.get().defaultBlockState()
                    .setValue(HarmonicNodeBlock.FACING, oldState.getValue(ResonanceChamberBlock.FACING));
        }

        if (newState == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            level.setBlock(pos, newState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            if (isStrip) {
                level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            if (player != null) {
                context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            }
        }

        return InteractionResult.SUCCESS;
    }

    public boolean canPerformAction(ItemInstance stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);
    }
}