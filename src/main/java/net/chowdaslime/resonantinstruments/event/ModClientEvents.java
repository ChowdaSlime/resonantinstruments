package net.chowdaslime.resonantinstruments.event;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.block.entity.ModBlockEntities;
import net.chowdaslime.resonantinstruments.client.*;
import net.chowdaslime.resonantinstruments.item.HarmonicOfAlchemyItem;
import net.chowdaslime.resonantinstruments.item.HarmonicOfDivinationItem;
import net.chowdaslime.resonantinstruments.item.HarmonicOfResonanceItem;
import net.chowdaslime.resonantinstruments.item.ModEntities;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ResonantInstruments.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();

        if (item instanceof HarmonicOfResonanceItem) {
            ResonanceTooltip.addDynamicTooltip(event.getItemStack(), event.getToolTip());
        }

        if (item instanceof HarmonicOfDivinationItem) {
            DivinationTooltip.addDynamicTooltip(event.getItemStack(), event.getToolTip());
        }

        if (item instanceof HarmonicOfAlchemyItem) {
            AlchemyTooltip.addDynamicTooltip(event.getItemStack(), event.getToolTip());
        }
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CHRONOS_ACCELERATOR.get(), ChronosAcceleratorRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HARMONIC_NODE_BLOCK_ENTITY.get(), HarmonicNodeRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RESONANCE_CHAMBER_BLOCK_ENTITY.get(), ResonanceChamberRenderer::new);
    }
}