package net.chowdaslime.resonantinstruments.item;

import net.chowdaslime.resonantinstruments.data.ModDataComponents;
import net.chowdaslime.resonantinstruments.item.resonance.HoningTier;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HarmonicOfResonanceItem extends Item {

    private static final Identifier ATTACK_DAMAGE_ID =
            Identifier.fromNamespaceAndPath("resonantinstruments", "harmonic_of_resonance_attack_damage");

    public HarmonicOfResonanceItem(Properties properties) {
        super(properties);
    }

    private int getMinedCount(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BLOCKS_MINED.get(), 0);
    }

    private HoningTier getHoningTier(ItemStack stack) {
        return HoningTier.fromMinedCount(getMinedCount(stack));
    }

    private float getAttackDamageForTier(HoningTier tier) {
        return switch (tier) {
            case UNHONED -> 5.0F;
            case TIER_1 -> 6.0F;
            case TIER_2 -> 7.0F;
            case TIER_3 -> 8.0F;
            case TIER_4 -> 9.0F;
            case TIER_5 -> 10.0F;
        };
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        float damage = getAttackDamageForTier(getHoningTier(stack));
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.4D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return true;
        }
        return !state.is(getHoningTier(stack).material().incorrectBlocksForDrops());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) {
            return 15.0F;
        }

        boolean isMineableCategory = state.is(BlockTags.MINEABLE_WITH_PICKAXE)
                || state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_SHOVEL)
                || state.is(BlockTags.MINEABLE_WITH_HOE)
                || state.is(BlockTags.SWORD_EFFICIENT);

        if (isMineableCategory && isCorrectToolForDrops(stack, state)) {
            return getHoningTier(stack).material().speed();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!level.isClientSide()) {
            stack.set(ModDataComponents.BLOCKS_MINED.get(), getMinedCount(stack) + 1);
            stack.hurtAndBreak(1, miner, EquipmentSlot.MAINHAND);
        }
        return true;
    }
}