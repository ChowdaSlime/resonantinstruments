package net.chowdaslime.resonantinstruments.block.entity;

import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.chowdaslime.resonantinstruments.data.StoredPotionsData;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.chowdaslime.resonantinstruments.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class ResonanceChamberBlockEntity extends BlockEntity {

    private ItemStack storedItem = ItemStack.EMPTY;

    private static final float MAX_LIFT = 1.3f;
    public static final int GATHER_DURATION = 160;
    private static final int RITUAL_DURATION = 200;
    private static final int HOLD_DURATION = 20;
    private static final int DESCEND_DURATION = 30;
    private static final int RITUAL_VISUAL_SOUND_DELAY = 45;
    private boolean ritualVisualSoundPlayed = false;

    public enum Phase { IDLE, GATHER, RITUAL, HOLD, DESCEND }

    private Phase phase = Phase.IDLE;
    private long phaseStartTime = 0L;
    private int phaseElapsedTicks = 0;

    private List<PotionContents> pendingPotions = null;
    private final List<BlockPos> activeNodes = new ArrayList<>();

    private static final BlockPos[] NODE_OFFSETS = new BlockPos[]{
            new BlockPos(3, 0, 0),
            new BlockPos(-3, 0, 0),
            new BlockPos(0, 0, 3),
            new BlockPos(0, 0, -3)
    };

    private static final String[] FLOOR_PATTERN = {
            "   MMM   ",
            "  MMRMM  ",
            " MMMMMMM ",
            "MMMMMMMMM",
            "MRMMEMMRM",
            "MMMMMMMMM",
            " MMMMMMM ",
            "  MMRMM  ",
            "   MMM   "
    };

    private static final int FLOOR_CENTER_ROW = 4;
    private static final int FLOOR_CENTER_COL = 4;

    private boolean isMarbleFamily(BlockState state) {
        return state.is(ModBlocks.MARBLE.get())
                || state.is(ModBlocks.RUNED_MARBLE.get())
                || state.is(ModBlocks.ENGRAVED_MARBLE.get());
    }

    private boolean isMultiblockValid() {
        if (level == null) return false;
        BlockPos floorY = worldPosition.below();

        for (int row = 0; row < FLOOR_PATTERN.length; row++) {
            String line = FLOOR_PATTERN[row];
            for (int col = 0; col < line.length(); col++) {
                char ch = line.charAt(col);
                if (ch == ' ') continue;

                int dx = col - FLOOR_CENTER_COL;
                int dz = row - FLOOR_CENTER_ROW;
                BlockPos checkPos = floorY.offset(dx, 0, dz);
                BlockState state = level.getBlockState(checkPos);

                switch (ch) {
                    case 'E' -> {
                        if (!state.is(ModBlocks.ENGRAVED_MARBLE.get()))
                            return false;
                    }
                    case 'R' -> {
                        if (!state.is(ModBlocks.RUNED_MARBLE.get()))
                            return false;
                    }
                    case 'M' -> {
                        if (!isMarbleFamily(state))
                            return false;
                    }
                    default -> {}
                }
            }
        }
        return true;
    }

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
        if (!stack.is(ModItems.UNATTUNED_FORK.get()))
            return false;

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
        if (level == null)
            return nodes;

        for (BlockPos offset : NODE_OFFSETS) {
            BlockPos nodePos = worldPosition.offset(offset);
            if (level.getBlockEntity(nodePos) instanceof HarmonicNodeBlockEntity node) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public List<BlockPos> getActiveNodes() {
        return this.activeNodes;
    }

    public boolean tryStartRitual() {
        if (level == null || level.isClientSide())
            return false;
        if (phase != Phase.IDLE)
            return false;
        if (!hasItem())
            return false;
        if (!storedItem.is(ModItems.UNATTUNED_FORK.get()))
            return false;
        if (!isMultiblockValid())
            return false;

        List<HarmonicNodeBlockEntity> nodes = findNodes();
        List<PotionContents> potions = new ArrayList<>();
        this.activeNodes.clear();

        for (HarmonicNodeBlockEntity node : nodes) {
            if (node.hasPotion() && level.getBlockState(node.getBlockPos().below()).is(ModBlocks.RUNED_MARBLE.get())) {
                ItemStack potionStack = node.getStoredPotion();
                PotionContents contents = potionStack.get(DataComponents.POTION_CONTENTS);
                if (contents != null) {
                    potions.add(contents);
                    this.activeNodes.add(node.getBlockPos());
                }
            }
        }

        if (potions.isEmpty())
            return false;

        this.pendingPotions = potions;
        setPhase(Phase.GATHER);

        level.playSound(null, worldPosition, ModSounds.RITUAL_START.get(), SoundSource.BLOCKS, 0.4f, 1.0f);

        return true;
    }

    private void setPhase(Phase newPhase) {
        boolean isIdle = (newPhase == Phase.IDLE);

        this.phase = newPhase;
        this.phaseStartTime = level != null ? level.getGameTime() : 0L;
        this.phaseElapsedTicks = 0;
        this.ritualVisualSoundPlayed = false;

        setChanged();
        syncToClient();

        if (level != null && !level.isClientSide()) {
            BlockState currentState = getBlockState();
            if (currentState.hasProperty(BlockStateProperties.LIT)) {
                level.setBlockAndUpdate(worldPosition, currentState.setValue(BlockStateProperties.LIT, !isIdle));
            }

            for (BlockPos offset : NODE_OFFSETS) {
                BlockPos nodePos = worldPosition.offset(offset);
                BlockState nodeState = level.getBlockState(nodePos);

                if (nodeState.is(ModBlocks.HARMONIC_NODE.get()) && nodeState.hasProperty(BlockStateProperties.LIT)) {
                    boolean nodeActive = !isIdle && this.activeNodes.contains(nodePos);
                    level.setBlockAndUpdate(nodePos, nodeState.setValue(BlockStateProperties.LIT, nodeActive));
                }
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ResonanceChamberBlockEntity be) {
        if (be.phase == Phase.IDLE)
            return;

        if ((be.phase == Phase.GATHER || be.phase == Phase.RITUAL) && be.pendingPotions == null) {
            be.setPhase(Phase.IDLE);
            return;
        }

        be.phaseElapsedTicks++;
        long elapsed = be.phaseElapsedTicks;

        switch (be.phase) {
            case GATHER -> {
                if (elapsed >= GATHER_DURATION) {
                    be.setPhase(Phase.RITUAL);
                }
            }
            case RITUAL -> {
                if (!be.ritualVisualSoundPlayed && elapsed >= RITUAL_VISUAL_SOUND_DELAY) {
                    be.ritualVisualSoundPlayed = true;
                    level.playSound(null, pos, ModSounds.RITUAL.get(), SoundSource.BLOCKS, 0.4f, 1.0f);
                }
                if (elapsed >= RITUAL_DURATION) {
                    be.completeRitual();
                }
            }
            case HOLD -> {
                if (elapsed >= HOLD_DURATION) {
                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                net.minecraft.core.particles.ParticleTypes.FIREWORK,
                                pos.getX() + 0.5, pos.getY() + 1.15 + MAX_LIFT, pos.getZ() + 0.5, 20, 0.3, 0.3, 0.3, 0.05);
                    }
                    level.playSound(null, pos, ModSounds.RITUAL_COMPLETION.get(), SoundSource.BLOCKS, 0.2f, 1.0f);
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

    public int getPhaseElapsedTicks() {
        return phaseElapsedTicks;
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
        output.putInt("PhaseElapsedTicks", phaseElapsedTicks);
        if (!this.activeNodes.isEmpty()) {
            output.store("ActiveNodes", BlockPos.CODEC.listOf(), this.activeNodes);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedItem = input.read("StoredItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        phase = Phase.valueOf(input.getStringOr("Phase", Phase.IDLE.name()));
        phaseStartTime = input.getLongOr("PhaseStartTime", 0L);
        phaseElapsedTicks = input.getIntOr("PhaseElapsedTicks", 0);
        this.activeNodes.clear();
        input.read("ActiveNodes", BlockPos.CODEC.listOf()).ifPresent(this.activeNodes::addAll);
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