package net.chowdaslime.resonantinstruments.item.resonance;

import net.chowdaslime.resonantinstruments.ResonantInstrumentsConfig;
import net.minecraft.world.item.ToolMaterial;

public enum HoningTier {
    UNHONED(ModToolMaterials.TIER_1),
    TIER_1(ModToolMaterials.TIER_1),
    TIER_2(ModToolMaterials.TIER_2),
    TIER_3(ModToolMaterials.TIER_3),
    TIER_4(ModToolMaterials.TIER_4),
    TIER_5(ModToolMaterials.TIER_5);

    private final ToolMaterial material;

    HoningTier(ToolMaterial material) {
        this.material = material;
    }

    public ToolMaterial material() { return material; }

    public int threshold() {
        return switch (this) {
            case UNHONED -> 0;
            case TIER_1 -> ResonantInstrumentsConfig.HONE_TIER_1_THRESHOLD.get();
            case TIER_2 -> TIER_1.threshold() + ResonantInstrumentsConfig.HONE_TIER_2_THRESHOLD.get();
            case TIER_3 -> TIER_2.threshold() + ResonantInstrumentsConfig.HONE_TIER_3_THRESHOLD.get();
            case TIER_4 -> TIER_3.threshold() + ResonantInstrumentsConfig.HONE_TIER_4_THRESHOLD.get();
            case TIER_5 -> TIER_4.threshold() + ResonantInstrumentsConfig.HONE_TIER_5_THRESHOLD.get();
        };
    }

    public static HoningTier fromMinedCount(int minedCount) {
        HoningTier result = UNHONED;
        for (HoningTier t : values()) {
            if (minedCount >= t.threshold()) result = t;
        }
        return result;
    }
}