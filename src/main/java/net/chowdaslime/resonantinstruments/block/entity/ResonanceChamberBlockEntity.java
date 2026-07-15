package net.chowdaslime.resonantinstruments.block.entity;

import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.chowdaslime.resonantinstruments.data.StoredPotionsData;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class ResonanceChamberBlockEntity extends BlockEntity {

    private ItemStack storedItem = ItemStack.EMPTY;

    private static final int RITUAL_DURATION = 200;
    private static final int HOLD_DURATION = 20;
    private static final int DESCEND_DURATION = 30;

    public enum Phase { IDLE, RITUAL, HOLD, DESCEND }

    private Phase phase = Phase.IDLE;
    private long phaseStartTime = 0L;

    private List<PotionContents> pendingPotions = null;

    private static final BlockPos[] NODE_OFFSETS = new BlockPos[]{
            new BlockPos(3, 0, 0),
            new BlockPos(-3, 0, 0),
            new BlockPos(0, 0, 3),
            new BlockPos(0, 0, -3)
    };

    public ResonanceChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESONANCE_CHAMBER_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack interact(ItemStack handStack) {
        boolean handEmpty = handStack.isEmpty();
        boolean chamberHasItem = hasItem();

        if (!chamberHasItem && handEmpty) {
            return null;
        }

        if (!chamberHasItem) {
            if (!handStack.is(ModItems.UNATTUNED_FORK.get())) {
                return null;
            }
            tryInsertFork(handStack);
            return ItemStack.EMPTY;
        }

        ItemStack old = extractItem();
        if (!handEmpty && handStack.is(ModItems.UNATTUNED_FORK.get())) {
            tryInsertFork(handStack);
        }
        return old;
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public boolean hasItem() {
        return !storedItem.isEmpty();
    }

    public boolean tryInsertFork(ItemStack stack) {
        if (hasItem()) return false;
        if (!stack.is(ModItems.UNATTUNED_FORK.get())) return false;

        storedItem = stack.copyWithCount(1);
        setChanged();
        syncToClient();
        return true;
    }

    public ItemStack extractItem() {
        ItemStack removed = storedItem;
        storedItem = ItemStack.EMPTY;
        setChanged();
        syncToClient();
        return removed;
    }

    private List<HarmonicNodeBlockEntity> findNodes() {
        List<HarmonicNodeBlockEntity> nodes = new ArrayList<>();
        if (level == null) return nodes;

        for (BlockPos offset : NODE_OFFSETS) {
            BlockPos nodePos = worldPosition.offset(offset);
            if (level.getBlockEntity(nodePos) instanceof HarmonicNodeBlockEntity node) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public boolean tryStartRitual() {
        if (level == null || level.isClientSide()) return false;
        if (phase != Phase.IDLE) return false;
        if (!hasItem()) return false;
        if (!storedItem.is(ModItems.UNATTUNED_FORK.get())) return false;

        List<HarmonicNodeBlockEntity> nodes = findNodes();
        List<PotionContents> potions = new ArrayList<>();

        for (HarmonicNodeBlockEntity node : nodes) {
            if (node.hasPotion()) {
                ItemStack potionStack = node.getStoredPotion();
                PotionContents contents = potionStack.get(DataComponents.POTION_CONTENTS);
                if (contents != null) {
                    potions.add(contents);
                }
            }
        }

        if (potions.isEmpty()) return false;

        this.pendingPotions = potions;
        setPhase(Phase.RITUAL);

        return true;
    }

    private void setPhase(Phase newPhase) {
        this.phase = newPhase;
        this.phaseStartTime = level != null ? level.getGameTime() : 0L;
        setChanged();
        syncToClient();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ResonanceChamberBlockEntity be) {
        if (be.phase == Phase.IDLE) return;

        long elapsed = level.getGameTime() - be.phaseStartTime;

        switch (be.phase) {
            case RITUAL -> {
                if (elapsed >= RITUAL_DURATION) {
                    be.completeRitual();
                }
            }
            case HOLD -> {
                if (elapsed >= HOLD_DURATION) {
                    be.setPhase(Phase.DESCEND);
                }
            }
            case DESCEND -> {
                if (elapsed >= DESCEND_DURATION) {
                    be.setPhase(Phase.IDLE);
                }
            }
            default -> {}
        }
    }

    private void completeRitual() {
        if (level == null || level.isClientSide()) return;

        List<HarmonicNodeBlockEntity> nodes = findNodes();
        for (HarmonicNodeBlockEntity node : nodes) {
            if (node.hasPotion()) {
                node.extractPotion();
            }
        }

        ItemStack result = new ItemStack(ModItems.HARMONIC_OF_ALCHEMY.get());
        result.set(ModDataComponents.STORED_POTIONS.get(), new StoredPotionsData(List.copyOf(pendingPotions)));

        storedItem = result;
        pendingPotions = null;

        setPhase(Phase.HOLD);

        level.playSound(null, worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public Phase getPhase() {
        return phase;
    }

    public static int getHoldDuration() {
        return HOLD_DURATION;
    }

    public long getPhaseStartTime() {
        return phaseStartTime;
    }

    public static int getRitualDuration() {
        return RITUAL_DURATION;
    }

    public static int getDescendDuration() {
        return DESCEND_DURATION;
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!storedItem.isEmpty()) {
            output.store("StoredItem", ItemStack.CODEC, storedItem);
        }
        output.putString("Phase", phase.name());
        output.putLong("PhaseStartTime", phaseStartTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedItem = input.read("StoredItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        phase = Phase.valueOf(input.getStringOr("Phase", Phase.IDLE.name()));
        phaseStartTime = input.getLongOr("PhaseStartTime", 0L);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}