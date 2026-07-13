package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.entity.ChronosAcceleratorEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ResonantInstruments.MODID);

    public static final Supplier<EntityType<ChronosAcceleratorEntity>> CHRONOS_ACCELERATOR =
            ENTITIES.register("chronos_accelerator",
                    () -> EntityType.Builder.<ChronosAcceleratorEntity>of(ChronosAcceleratorEntity::new, MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(8)
                            .noSummon()
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(ResonantInstruments.MODID, "chronos_accelerator"))));

    public static void register(IEventBus modEventBus) {
        ENTITIES.register(modEventBus);
    }
}