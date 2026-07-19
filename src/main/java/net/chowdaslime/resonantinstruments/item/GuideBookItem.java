package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.patchouli.PatchouliCompat;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

public class GuideBookItem extends Item {
    public GuideBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (ModList.get().isLoaded("patchouli")) {
                PatchouliCompat.openBook(serverPlayer);
            } else {
                player.sendSystemMessage(Component.literal("Install Patchouli to view the guide book."));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
