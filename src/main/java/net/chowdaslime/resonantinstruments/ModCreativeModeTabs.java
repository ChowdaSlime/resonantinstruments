package net.chowdaslime.resonantinstruments;

import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ResonantInstruments.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RESONANT_TAB =
            TABS.register("resonant_instruments_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.resonant_instruments_tab"))
                    .icon(() -> ModItems.UNATTUNED_FORK.get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        output.accept(ModItems.UNATTUNED_FORK.get());
                        output.accept(ModItems.UNATTUNED_FORK.get());
                        output.accept(ModItems.HARMONIC_OF_VITALITY.get());
                        output.accept(ModItems.HARMONIC_OF_RESONANCE.get());
                        output.accept(ModItems.HARMONIC_OF_CHRONOS.get());
                        output.accept(ModItems.HARMONIC_OF_ALCHEMY.get());
                        output.accept(ModItems.HARMONIC_OF_DIVINATION.get());
                        output.accept(ModBlocks.RESONANCE_CHAMBER.get());
                        output.accept(ModBlocks.HARMONIC_NODE.get());
                        output.accept(ModItems.GUIDE_BOOK.get());
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}