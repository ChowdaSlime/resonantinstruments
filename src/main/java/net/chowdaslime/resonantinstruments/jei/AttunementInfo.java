package net.chowdaslime.resonantinstruments.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record AttunementInfo(ItemStack icon, Component howToObtain, List<Component> description) {

    public static AttunementInfo of(ItemStack icon, String howToObtainKey, String descriptionKey) {
        return new AttunementInfo(
                icon,
                Component.translatable(howToObtainKey),
                List.of(Component.translatable(descriptionKey))
        );
    }
}