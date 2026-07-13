package net.chowdaslime.resonantinstruments.client;

import net.chowdaslime.resonantinstruments.data.component.ModDataComponents;
import net.chowdaslime.resonantinstruments.item.resonance.HoningTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ResonanceTooltip {
    public static void addDynamicTooltip(ItemStack stack, List<Component> tooltip) {
        int mined = stack.getOrDefault(ModDataComponents.BLOCKS_MINED.get(), 0);
        HoningTier tier = HoningTier.fromMinedCount(mined);

        HoningTier[] all = HoningTier.values();
        int currentIndex = tier.ordinal();

        if (currentIndex == all.length - 1) {
            tooltip.add(Component.literal("Tier " + currentIndex + " (MAX) - " + mined + " blocks mined")
                    .withStyle(ChatFormatting.AQUA));
        } else {
            int currentFloor = tier.threshold();
            int nextThreshold = all[currentIndex + 1].threshold();
            int progressInTier = mined - currentFloor;
            int neededForNext = nextThreshold - currentFloor;
            tooltip.add(Component.literal("Tier " + currentIndex + " - " + progressInTier + "/" + neededForNext)
                    .withStyle(ChatFormatting.AQUA));
            int maxDamage = stack.getMaxDamage();
            if (maxDamage > 0) {
                int damage = stack.getDamageValue();
                int remaining = maxDamage - damage;
                tooltip.add(Component.literal("Durability: " + remaining + "/" + maxDamage)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }
}