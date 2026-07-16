package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(ResonantInstruments.MODID);

    public static final DeferredHolder<Item, UnattunedForkItem> UNATTUNED_FORK =
            ITEMS.registerItem("unattuned_fork",
                    properties -> new UnattunedForkItem(properties.stacksTo(1))
            );
    public static final DeferredHolder<Item, HarmonicOfVitalityItem> HARMONIC_OF_VITALITY =
            ITEMS.registerItem("harmonic_of_vitality",
                    properties -> new HarmonicOfVitalityItem(properties.stacksTo(1))
            );
    public static final DeferredHolder<Item, HarmonicOfResonanceItem> HARMONIC_OF_RESONANCE =
            ITEMS.registerItem("harmonic_of_resonance",
                    properties -> new HarmonicOfResonanceItem(properties.durability(1561).enchantable(10))
            );
    public static final DeferredHolder<Item, HarmonicOfChronosItem> HARMONIC_OF_CHRONOS =
            ITEMS.registerItem("harmonic_of_chronos",
                    properties -> new HarmonicOfChronosItem(properties.stacksTo(1))
            );
    public static final DeferredHolder<Item, HarmonicOfDivinationItem> HARMONIC_OF_DIVINATION =
            ITEMS.registerItem("harmonic_of_divination",
                    properties -> new HarmonicOfDivinationItem(properties.stacksTo(1))
            );
    public static final DeferredHolder<Item, HarmonicOfAlchemyItem> HARMONIC_OF_ALCHEMY =
            ITEMS.registerItem("harmonic_of_alchemy",
                    properties -> new HarmonicOfAlchemyItem(properties.stacksTo(1))
            );


    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}