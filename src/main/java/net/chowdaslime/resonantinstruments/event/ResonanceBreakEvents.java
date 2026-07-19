package net.chowdaslime.resonantinstruments.event;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.chowdaslime.resonantinstruments.item.HarmonicOfResonanceItem;
import net.chowdaslime.resonantinstruments.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

@EventBusSubscriber(modid = ResonantInstruments.MODID)
public class ResonanceBreakEvents {

    @SubscribeEvent
    public static void onBreak(BreakBlockEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof HarmonicOfResonanceItem)) return;

        event.setCanceled(true);
        event.setNotifyClient(true);

        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState state = event.getState();
        var pos = event.getPos();
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);

        serverLevel.removeBlock(pos, false);

        if (!player.isCreative()) {
            Block.dropResources(state, serverLevel, pos, blockEntity, player, tool);
            tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

            int minedCount = HarmonicOfResonanceItem.getMinedCount(tool);
            tool.set(ModDataComponents.BLOCKS_MINED.get(), minedCount + 1);
        }

        serverLevel.playSound((Entity) null, pos, ModSounds.RESONANCE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}