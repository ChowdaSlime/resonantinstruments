package net.chowdaslime.resonantinstruments.sound;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, ResonantInstruments.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ALCHEMY =
            registerSound("alchemy");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHRONOS_CHARGE =
            registerSound("chronos_charge");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHRONOS_FIRE =
            registerSound("chronos_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIVINATION_SEARCH =
            registerSound("divination_search");
    public static final DeferredHolder<SoundEvent, SoundEvent> DIVINATION_TUNE =
            registerSound("divination_tune");
    public static final DeferredHolder<SoundEvent, SoundEvent> RESONANCE =
            registerSound("resonance");
    public static final DeferredHolder<SoundEvent, SoundEvent> VITALITY =
            registerSound("vitality");
    public static final DeferredHolder<SoundEvent, SoundEvent> RITUAL_START =
            registerSound("ritual_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> RITUAL =
            registerSound("ritual");
    public static final DeferredHolder<SoundEvent, SoundEvent> RITUAL_COMPLETION =
            registerSound("ritual_completion");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        Identifier.fromNamespaceAndPath(ResonantInstruments.MODID, name)));
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}