package net.chowdaslime.resonantinstruments;

import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.chowdaslime.resonantinstruments.block.entity.ModBlockEntities;
import net.chowdaslime.resonantinstruments.data.component.ModDataComponents;
import net.chowdaslime.resonantinstruments.item.ModEntities;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ResonantInstruments.MODID)
public class ResonantInstruments {
    public static final String MODID = "resonantinstruments";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ResonantInstruments(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);


        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModEntities.register(modEventBus);




        NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, ResonantInstrumentsConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
      }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
       }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
