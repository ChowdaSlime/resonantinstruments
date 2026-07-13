package net.chowdaslime.resonantinstruments;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ResonantInstrumentsConfig {
    public static final ModConfigSpec SPEC;

    // Harmonic of Vitality
    public static final ModConfigSpec.IntValue VITALITY_AREA_RADIUS;
    public static final ModConfigSpec.IntValue VITALITY_GROWTH_ACCELERATION_SECONDS;


    // Harmonic of Resonance
    public static final ModConfigSpec.IntValue HONE_TIER_1_THRESHOLD;
    public static final ModConfigSpec.IntValue HONE_TIER_2_THRESHOLD;
    public static final ModConfigSpec.IntValue HONE_TIER_3_THRESHOLD;
    public static final ModConfigSpec.IntValue HONE_TIER_4_THRESHOLD;
    public static final ModConfigSpec.IntValue HONE_TIER_5_THRESHOLD;

    // Harmonic of Chronos
    public static final ModConfigSpec.IntValue CHRONOS_MAX_MULTIPLIER;
    public static final ModConfigSpec.IntValue CHRONOS_HUNGER_DRAIN_INTERVAL;

    // Harmonic of Divination
    public static final ModConfigSpec.IntValue DIVINATION_COOLDOWN_TICKS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("vitality");
        VITALITY_AREA_RADIUS = builder
                .comment("Radius (in blocks) affected by the Harmonic of Vitality's growth pulse")
                .defineInRange("areaRadius", 7, 1, 16);
        VITALITY_GROWTH_ACCELERATION_SECONDS = builder
                .comment("Seconds shaved off a baby animal's growth timer per Vitality pulse")
                .defineInRange("growthAccelerationSeconds", 15, 1, 1200);
        builder.pop();

        builder.push("resonance");
        HONE_TIER_1_THRESHOLD = builder
                .comment("Blocks mined to reach honing tier 1 (iron level)")
                .defineInRange("honeTier1Threshold", 128, 1, Integer.MAX_VALUE);
        HONE_TIER_2_THRESHOLD = builder
                .comment("Blocks mined to reach honing tier 2 (diamond level)")
                .defineInRange("honeTier2Threshold", 512, 1, Integer.MAX_VALUE);
        HONE_TIER_3_THRESHOLD = builder
                .comment("Blocks mined to reach honing tier 3 (netherite level)")
                .defineInRange("honeTier3Threshold", 2048, 1, Integer.MAX_VALUE);
        HONE_TIER_4_THRESHOLD = builder
                .comment("Blocks mined to reach honing tier 4 (netherite+ level)")
                .defineInRange("honeTier4Threshold", 4096, 1, Integer.MAX_VALUE);
        HONE_TIER_5_THRESHOLD = builder
                .comment("Blocks mined to reach honing tier 5 (netherite++ level)")
                .defineInRange("honeTier5Threshold", 8192, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("chronos");
        CHRONOS_MAX_MULTIPLIER = builder
                .comment("Maximum tick acceleration multiplier")
                .defineInRange("maxMultiplier", 8, 2, 64);
        CHRONOS_HUNGER_DRAIN_INTERVAL = builder
                .comment("Ticks between hunger drain while Chronos is active")
                .defineInRange("hungerDrainInterval", 20, 1, Integer.MAX_VALUE);
        builder.pop();

        builder.push("divination");
        DIVINATION_COOLDOWN_TICKS = builder
                .comment("Cooldown in ticks between Divination uses (20 ticks = 1 second)")
                .defineInRange("cooldownTicks", 600, 0, Integer.MAX_VALUE);
        builder.pop();

        SPEC = builder.build();
    }
}