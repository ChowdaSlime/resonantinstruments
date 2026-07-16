package net.chowdaslime.resonantinstruments.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class HarmonicNodeBlockEntity extends BlockEntity {

    private ItemStack storedPotion = ItemStack.EMPTY;

    public HarmonicNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARMONIC_NODE_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStack interact(ItemStack handStack) {
        boolean handEmpty = handStack.isEmpty();
        boolean nodeHasItem = hasPotion();

        if (!nodeHasItem && handEmpty) {
            return null;
        }

        if (!nodeHasItem) {
            if (!handStack.has(DataComponents.POTION_CONTENTS)) {
                return null;
            }
            tryInsertPotion(handStack);
            return ItemStack.EMPTY;
        }

        ItemStack old = extractPotion();
        if (!handEmpty && handStack.has(DataComponents.POTION_CONTENTS)) {
            tryInsertPotion(handStack);
        }
        return old;
    }

    public ItemStack getStoredPotion() {
        return storedPotion;
    }

    public boolean hasPotion() {
        return !storedPotion.isEmpty();
    }

    public ItemStack extractPotion() {
        ItemStack removed = storedPotion;
        storedPotion = ItemStack.EMPTY;
        setChanged();
        syncToClient();
        return removed;
    }

    public boolean tryInsertPotion(ItemStack stack) {
        if (hasPotion()) return false;
        if (!stack.has(DataComponents.POTION_CONTENTS)) return false;

        storedPotion = stack.copyWithCount(1);
        setChanged();
        syncToClient();
        return true;
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!storedPotion.isEmpty()) {
            output.store("StoredPotion", ItemStack.CODEC, storedPotion);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedPotion = input.read("StoredPotion", ItemStack.CODEC).orElse(ItemStack.EMPTY);
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