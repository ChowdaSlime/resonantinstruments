package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.ResonantInstrumentsConfig;
import net.chowdaslime.resonantinstruments.entity.ChronosAcceleratorEntity;
import net.chowdaslime.resonantinstruments.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public class HarmonicOfChronosItem extends Item {

    public HarmonicOfChronosItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {

        if (level.isClientSide() || !(livingEntity instanceof Player player)) {
            return false;
        }

        int ticksCharged = this.getUseDuration(stack, livingEntity) - timeLeft;

        if (ticksCharged < 10) {
            return false;
        }

        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (!(hit instanceof BlockHitResult blockHit)) {
            return false;
        }

        BlockPos pos = blockHit.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }

        BlockState state = level.getBlockState(pos);

        if (state.is(ModTags.Blocks.CHRONOS_BLACKLIST)) {
            player.sendOverlayMessage(Component.literal("Block Blacklisted"));
            return false;
        }

        int secondsCharged = ticksCharged / 20;
        int maxMultiplier = ResonantInstrumentsConfig.CHRONOS_MAX_MULTIPLIER.get();

        int nextMultiplier = (int) Math.pow(2, secondsCharged + 1);
        nextMultiplier = Math.min(nextMultiplier, maxMultiplier);

        AABB searchBox = new AABB(pos);
        Optional<ChronosAcceleratorEntity> existingEntity = level.getEntitiesOfClass(ChronosAcceleratorEntity.class, searchBox).stream().findFirst();

        if (existingEntity.isPresent()) {
            ChronosAcceleratorEntity accelerator = existingEntity.get();
            int currentMultiplier = accelerator.getMultiplier();

            if (nextMultiplier <= currentMultiplier) {
                return false;
            }

            if (!payHungerCost(player, nextMultiplier)) {
                return false;
            }

            accelerator.setMultiplier(nextMultiplier);

        } else {
            if (!payHungerCost(player, nextMultiplier)) {
                return false;
            }

            ChronosAcceleratorEntity newAccelerator = new ChronosAcceleratorEntity(ModEntities.CHRONOS_ACCELERATOR.get(), level, pos);
            newAccelerator.setMultiplier(nextMultiplier);
            newAccelerator.resetDuration();
            level.addFreshEntity(newAccelerator);
        }

        float pitch = 1.0F + ((float) nextMultiplier / maxMultiplier);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, pitch);

        return true;
    }

    private boolean payHungerCost(Player player, int multiplierTier) {
        if (player.isCreative()) {
            return true;
        }

        int foodCost = Math.max(1, multiplierTier / 2);

        if (player.getFoodData().getFoodLevel() >= foodCost) {
            player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - foodCost);
            return true;
        } else {
            return false;
        }
    }
}