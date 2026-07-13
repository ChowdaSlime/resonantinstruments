package net.chowdaslime.resonantinstruments.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ChronosAcceleratorEntity extends Entity {

    private static final EntityDataAccessor<Integer> MULTIPLIER = SynchedEntityData.defineId(ChronosAcceleratorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_REMAINING = SynchedEntityData.defineId(ChronosAcceleratorEntity.class, EntityDataSerializers.INT);

    public ChronosAcceleratorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public ChronosAcceleratorEntity(EntityType<?> entityType, Level level, BlockPos pos) {
        this(entityType, level);
        this.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(MULTIPLIER, 1);
        builder.define(TICKS_REMAINING, 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            int remaining = getTicksRemaining() - 1;
            setTicksRemaining(remaining);

            if (remaining <= 0) {
                this.discard();
                return;
            }

            BlockPos pos = this.blockPosition();
            BlockState state = this.level().getBlockState(pos);
            BlockEntity blockEntity = this.level().getBlockEntity(pos);

            if (blockEntity == null) {
                this.discard();
                return;
            }

            @SuppressWarnings("unchecked")
            BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) state.getTicker(this.level(), blockEntity.getType());
            if (ticker != null) {
                int extraTicks = getMultiplier() - 1;
                for (int i = 0; i < extraTicks; i++) {
                    ticker.tick(this.level(), pos, state, blockEntity);
                }
            }
        } else {
            if (this.level().getRandom().nextInt(3) == 0) {
                this.level().addParticle(
                        net.minecraft.core.particles.ParticleTypes.NOTE,
                        this.getX() + (this.level().getRandom().nextDouble() - 0.5),
                        this.getY() + (this.level().getRandom().nextDouble() - 0.5) + 0.5,
                        this.getZ() + (this.level().getRandom().nextDouble() - 0.5),
                        this.level().getRandom().nextDouble() * 24.0D / 24.0D,
                        0.0D, 0.0D
                );
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        this.setMultiplier(valueInput.getIntOr("Multiplier", 1));
        this.setTicksRemaining(valueInput.getIntOr("TicksRemaining", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        valueOutput.putInt("Multiplier", this.getMultiplier());
        valueOutput.putInt("TicksRemaining", this.getTicksRemaining());
    }

    public int getMultiplier() {
        return this.entityData.get(MULTIPLIER);
    }

    public void setMultiplier(int multiplier) {
        this.entityData.set(MULTIPLIER, multiplier);
    }

    public int getTicksRemaining() {
        return this.entityData.get(TICKS_REMAINING);
    }

    public void setTicksRemaining(int ticks) {
        this.entityData.set(TICKS_REMAINING, ticks);
    }

    public void resetDuration() {
        this.setTicksRemaining(600);
    }
}