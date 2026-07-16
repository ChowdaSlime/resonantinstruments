package net.chowdaslime.resonantinstruments.client;

import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DivinationTooltip {
    public static void addDynamicTooltip(ItemStack stack, List<Component> tooltip) {
        String tunedId = stack.getOrDefault(ModDataComponents.TUNED_BLOCK_ID.get(), "");

        if (tunedId.isEmpty()) {
            tooltip.add(Component.literal("Not tuned").withStyle(ChatFormatting.GRAY));
            return;
        }

        BuiltInRegistries.BLOCK.get(Identifier.parse(tunedId))
                .map(Holder.Reference::value)
                .ifPresentOrElse(
                        block -> tooltip.add(Component.literal("Tuned to: ")
                                .withStyle(ChatFormatting.GRAY)
                                .append(block.getName().copy().withStyle(ChatFormatting.AQUA))),
                        () -> tooltip.add(Component.literal("Tuned to: unknown block")
                                .withStyle(ChatFormatting.RED))
                );
    }
}