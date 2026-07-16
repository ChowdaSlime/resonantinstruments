package net.chowdaslime.resonantinstruments.event;

import net.chowdaslime.resonantinstruments.ResonantInstrumentsConfig;
import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.chowdaslime.resonantinstruments.item.HarmonicOfDivinationItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "resonantinstruments")
public class DivinationStrikeEvents {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof HarmonicOfDivinationItem)) {
            return;
        }

        event.setCanceled(true);
        event.setUseBlock(TriState.FALSE);
        event.setUseItem(TriState.FALSE);

        if (player.level().isClientSide() || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(stack)) {
            return;
        }

        String tunedId = stack.getOrDefault(ModDataComponents.TUNED_BLOCK_ID.get(), "");
        if (tunedId.isEmpty()) {
            player.sendOverlayMessage(Component.literal("Divination not tuned"));
            return;
        }

        Block target = BuiltInRegistries.BLOCK.get(Identifier.parse(tunedId))
                .map(Holder.Reference::value)
                .orElse(null);
        if (target == null) {
            return;
        }

        player.getCooldowns().addCooldown(stack, ResonantInstrumentsConfig.DIVINATION_COOLDOWN_TICKS.get());

        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_HIT,
                SoundSource.PLAYERS, 1.0F, 1.0F);

        int radius = ResonantInstrumentsConfig.DIVINATION_SEARCH_RADIUS.get();
        BlockPos origin = player.blockPosition();
        BlockPos found = findNearestMatch(level, origin, target, radius);

        if (found == null) {
            player.sendOverlayMessage(Component.literal("Block not found"));
            return;
        }

        fireDirectionalParticle(level, player, found);

        double distance = Math.sqrt(origin.distSqr(found));
        player.sendOverlayMessage(
                Component.literal("Found ")
                        .append(target.getName())
                        .append(Component.literal(" (" + (int) distance + "m)")));
    }

    private static BlockPos findNearestMatch(ServerLevel level, BlockPos origin, Block target, int maxRadius) {
        for (int r = 1; r <= maxRadius; r++) {
            BlockPos min = origin.offset(-r, -r, -r);
            BlockPos max = origin.offset(r, r, r);

            for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
                boolean onShell = Math.abs(pos.getX() - origin.getX()) == r
                        || Math.abs(pos.getY() - origin.getY()) == r
                        || Math.abs(pos.getZ() - origin.getZ()) == r;
                if (!onShell) continue;

                BlockState state = level.getBlockState(pos);
                if (state.is(target)) {
                    return pos.immutable();
                }
            }
        }
        return null;
    }

    private static void fireDirectionalParticle(ServerLevel level, Player player, BlockPos target) {
        Vec3 from = player.getEyePosition();
        Vec3 direction = Vec3.atCenterOf(target).subtract(from);

        DivinationGuideTracker.spawn(level, from, direction, 100, 0.25);
    }
}