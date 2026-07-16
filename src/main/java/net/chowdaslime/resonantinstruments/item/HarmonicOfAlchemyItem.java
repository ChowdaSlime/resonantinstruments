package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.chowdaslime.resonantinstruments.data.StoredPotionsData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import java.util.List;

public class HarmonicOfAlchemyItem extends Item {

    private static final int COOLDOWN_TICKS = 1200;

    public HarmonicOfAlchemyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this.getDefaultInstance())) {
            return InteractionResult.PASS;
        }

        StoredPotionsData data = stack.getOrDefault(ModDataComponents.STORED_POTIONS.get(), StoredPotionsData.EMPTY);
        if (data.potions().isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        applyPotions(data, player);
        player.getCooldowns().addCooldown(this.getDefaultInstance(), COOLDOWN_TICKS);
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this.getDefaultInstance())) {
            return InteractionResult.PASS;
        }

        StoredPotionsData data = stack.getOrDefault(ModDataComponents.STORED_POTIONS.get(), StoredPotionsData.EMPTY);
        if (data.potions().isEmpty()) {
            return InteractionResult.PASS;
        }

        if (target.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        applyPotions(data, target);
        player.getCooldowns().addCooldown(this.getDefaultInstance(), COOLDOWN_TICKS);
        target.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);

        return InteractionResult.CONSUME;
    }

    private void applyPotions(StoredPotionsData data, LivingEntity target) {
        for (PotionContents contents : data.potions()) {
            List<MobEffectInstance> effects = (List<MobEffectInstance>) contents.getAllEffects();
            for (MobEffectInstance effect : effects) {
                target.addEffect(new MobEffectInstance(effect));
            }
        }
    }
}