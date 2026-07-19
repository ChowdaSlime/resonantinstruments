package net.chowdaslime.resonantinstruments.patchouli;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class PatchouliCompat {
    public static void openBook(ServerPlayer player) {
        vazkii.patchouli.api.PatchouliAPI.get().openBookGUI(player,
                Identifier.fromNamespaceAndPath("resonantinstruments", "guide"));
    }
}
