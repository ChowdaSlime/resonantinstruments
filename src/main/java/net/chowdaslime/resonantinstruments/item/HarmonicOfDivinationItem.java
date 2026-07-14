package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.data.component.ModDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class HarmonicOfDivinationItem extends Item {

    public HarmonicOfDivinationItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Block target = level.getBlockState(context.getClickedPos()).getBlock();
        Identifier id = BuiltInRegistries.BLOCK.getKey(target);

        ItemStack stack = context.getItemInHand();
        stack.set(ModDataComponents.TUNED_BLOCK_ID.get(), id.toString());

        player.sendOverlayMessage(
                Component.literal("Tuned to ").append(target.getName()));
        level.playSound(null, context.getClickedPos(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.PLAYERS, 1.0F, 1.4F);

        return InteractionResult.CONSUME;
    }
}