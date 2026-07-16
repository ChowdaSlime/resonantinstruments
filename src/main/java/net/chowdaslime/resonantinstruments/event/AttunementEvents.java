package net.chowdaslime.resonantinstruments.event;

import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ResonantInstruments.MODID)
public class AttunementEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.is(ModItems.UNATTUNED_FORK.get())) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            attune(player, hand, stack, ModItems.HARMONIC_OF_VITALITY.get());
            event.setCanceled(true);
            return;
        }

        if (state.is(Blocks.BEDROCK) && pos.getY() == level.getMinY()) {
            attune(player, hand, stack, ModItems.HARMONIC_OF_RESONANCE.get());
            event.setCanceled(true);
            return;
        }

        if (state.is(Blocks.ENCHANTING_TABLE) && level.getOverworldClockTime() % 24000L == 18000L) {
            attune(player, hand, stack, ModItems.HARMONIC_OF_CHRONOS.get());
            event.setCanceled(true);
            return;
        }

        if (state.is(Blocks.BUDDING_AMETHYST)) {
            attune(player, hand, stack, ModItems.HARMONIC_OF_DIVINATION.get());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.is(ModItems.UNATTUNED_FORK.get())) return;

        if (event.getTarget() instanceof Animal) {
            attune(player, hand, stack, ModItems.HARMONIC_OF_VITALITY.get());
            event.setCanceled(true);
        }
    }

    private static void attune(Player player, InteractionHand hand, ItemStack oldStack, Item newItem) {
        ItemStack newStack = new ItemStack(newItem);
        player.setItemInHand(hand, newStack);
    }
}