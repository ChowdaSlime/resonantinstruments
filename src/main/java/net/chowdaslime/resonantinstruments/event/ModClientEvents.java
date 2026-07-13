package net.chowdaslime.resonantinstruments.event;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.client.ChronosAcceleratorRenderer;
import net.chowdaslime.resonantinstruments.item.HarmonicOfResonanceItem;
import net.chowdaslime.resonantinstruments.client.ResonanceTooltip;
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
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CHRONOS_ACCELERATOR.get(), ChronosAcceleratorRenderer::new);
    }
}