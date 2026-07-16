package net.chowdaslime.resonantinstruments.event;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.block.entity.ResonanceChamberBlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ResonantInstruments.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack stack = event.getItemStack();

        if (!player.isShiftKeyDown() || !stack.is(Items.NETHER_STAR)) return;
        if (level.isClientSide()) return;

        if (level.getBlockEntity(event.getPos()) instanceof ResonanceChamberBlockEntity chamber) {
            boolean started = chamber.tryStartRitual();
            if (started) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.CONSUME);
            }
        }
    }
}