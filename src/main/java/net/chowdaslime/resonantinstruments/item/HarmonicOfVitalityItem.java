package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.ResonantInstrumentsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HarmonicOfVitalityItem extends Item {

    public HarmonicOfVitalityItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos center = player.blockPosition();
        int radius = ResonantInstrumentsConfig.VITALITY_AREA_RADIUS.get();

        boolean affectedAny = false;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -1, -radius),
                center.offset(radius, 1, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof BonemealableBlock bonemealable
                    && bonemealable.isValidBonemealTarget(serverLevel, pos, state)
                    && bonemealable.isBonemealSuccess(serverLevel, level.getRandom(), pos, state)) {
                if (level.getRandom().nextFloat() < 0.45F) {
                    bonemealable.performBonemeal(serverLevel, level.getRandom(), pos, state);
                    affectedAny = true;
                }
            }
        }

        AABB searchBox = new AABB(center).inflate(radius, radius, radius);
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, searchBox);
        for (Animal animal : animals) {
            if (animal.isBaby()) {
                animal.ageUp(ResonantInstrumentsConfig.VITALITY_GROWTH_ACCELERATION_SECONDS.get(), true);
                affectedAny = true;
            } else if (!animal.isInLove() && animal.getAge() == 0) {
                animal.setInLove(null);
                affectedAny = true;
            }
        }

        if (affectedAny) {
            level.playSound(null, center, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.2F);
        }

        player.getCooldowns().addCooldown(player.getItemInHand(hand), 10);
        return InteractionResult.SUCCESS;
    }
}