package net.chowdaslime.resonantinstruments.item.resonance;

import net.chowdaslime.resonantinstruments.util.ModTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ToolMaterial;

public class ModToolMaterials {
    public static final ToolMaterial TIER_1 = ToolMaterial.IRON;
    public static final ToolMaterial TIER_2 = ToolMaterial.DIAMOND;
    public static final ToolMaterial TIER_3 = ToolMaterial.NETHERITE;


    public static final ToolMaterial TIER_4 = new ToolMaterial(
            ModTags.Blocks.INCORRECT_FOR_RESONANCE_TIER_4, 4000, 10.0F, 5.0F, 20, ItemTags.NETHERITE_TOOL_MATERIALS);
    public static final ToolMaterial TIER_5 = new ToolMaterial(
            ModTags.Blocks.INCORRECT_FOR_RESONANCE_TIER_5, Integer.MAX_VALUE, 12.0F, 6.0F, 25, ItemTags.NETHERITE_TOOL_MATERIALS);
}