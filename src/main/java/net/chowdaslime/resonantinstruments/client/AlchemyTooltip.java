package net.chowdaslime.resonantinstruments.client;

import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.chowdaslime.resonantinstruments.data.StoredPotionsData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

public class AlchemyTooltip {
    public static void addDynamicTooltip(ItemStack stack, List<Component> tooltip) {
        StoredPotionsData data = stack.getOrDefault(ModDataComponents.STORED_POTIONS.get(), StoredPotionsData.EMPTY);

        if (data.potions().isEmpty()) {
            tooltip.add(Component.literal("No potions stored").withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.add(Component.literal("Stored Potions:").withStyle(ChatFormatting.AQUA));

        int slot = 1;
        for (PotionContents contents : data.potions()) {
            List<MobEffectInstance> effects = (List<MobEffectInstance>) contents.getAllEffects();

            if (effects.isEmpty()) {
                tooltip.add(Component.literal(" " + slot + ". (no effects)").withStyle(ChatFormatting.GRAY));
            } else {
                for (MobEffectInstance effect : effects) {
                    MutableComponent line = Component.literal(" " + slot + ". ").withStyle(ChatFormatting.GRAY)
                            .append(Component.translatable(effect.getDescriptionId()));

                    if (effect.getAmplifier() > 0) {
                        line.append(Component.literal(" " + (effect.getAmplifier() + 1)));
                    }

                    line.append(Component.literal(" (" + formatDuration(effect.getDuration()) + ")")
                            .withStyle(ChatFormatting.DARK_GRAY));

                    tooltip.add(line.withStyle(effect.getEffect().value().getCategory().getTooltipFormatting()));
                }
            }
            slot++;
        }
    }

    private static String formatDuration(int ticks) {
        if (ticks < 0) {
            return "∞";
        }

        int totalSeconds = ticks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return seconds + "s";
        }
    }
}