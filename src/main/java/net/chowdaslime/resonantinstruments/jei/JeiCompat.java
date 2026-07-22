package net.chowdaslime.resonantinstruments.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.block.ModBlocks;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class JeiCompat implements IModPlugin {

    private static final Identifier PLUGIN_ID =
            Identifier.fromNamespaceAndPath(ResonantInstruments.MODID, "jei_plugin");

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new AttunementInfoCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(AttunementInfoCategory.TYPE, List.of(
                AttunementInfo.of(
                        new ItemStack(ModItems.HARMONIC_OF_VITALITY.get()),
                        "jei.resonantinstruments.attunement.vitality.obtain",
                        "jei.resonantinstruments.attunement.vitality.desc"
                ),
                AttunementInfo.of(
                        new ItemStack(ModItems.HARMONIC_OF_RESONANCE.get()),
                        "jei.resonantinstruments.attunement.resonance.obtain",
                        "jei.resonantinstruments.attunement.resonance.desc"
                ),
                AttunementInfo.of(
                        new ItemStack(ModItems.HARMONIC_OF_CHRONOS.get()),
                        "jei.resonantinstruments.attunement.chronos.obtain",
                        "jei.resonantinstruments.attunement.chronos.desc"
                ),
                AttunementInfo.of(
                        new ItemStack(ModItems.HARMONIC_OF_ALCHEMY.get()),
                        "jei.resonantinstruments.attunement.alchemy.obtain",
                        "jei.resonantinstruments.attunement.alchemy.desc"
                ),
                AttunementInfo.of(
                        new ItemStack(ModItems.HARMONIC_OF_DIVINATION.get()),
                        "jei.resonantinstruments.attunement.divination.obtain",
                        "jei.resonantinstruments.attunement.divination.desc"
                ),
                AttunementInfo.of(
                        new ItemStack(ModItems.CARVING_KNIFE.get()),
                        "jei.resonantinstruments.carving.obtain",
                        "jei.resonantinstruments.carving.desc"
                )
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(AttunementInfoCategory.TYPE, new ItemStack(ModItems.UNATTUNED_FORK.get()));

        registration.addCraftingStation(AttunementInfoCategory.TYPE,
                new ItemStack(ModBlocks.RESONANCE_CHAMBER.get().asItem()),
                new ItemStack(ModBlocks.HARMONIC_NODE.get().asItem())
        );

        registration.addCraftingStation(AttunementInfoCategory.TYPE, new ItemStack(ModItems.CARVING_KNIFE.get()));
    }
}