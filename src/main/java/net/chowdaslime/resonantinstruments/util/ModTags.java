package net.chowdaslime.resonantinstruments.util;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_RESONANCE_TIER_4 =
                tag("incorrect_for_resonance_tier_4");
        public static final TagKey<Block> INCORRECT_FOR_RESONANCE_TIER_5 =
                tag("incorrect_for_resonance_tier_5");
        public static final TagKey<Block> CHRONOS_BLACKLIST =
                tag("chronos_blacklist");

        private static TagKey<Block> tag(String name) {
            return TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                    Identifier.fromNamespaceAndPath(ResonantInstruments.MODID, name));
        }
    }
}