package net.chowdaslime.resonantinstruments.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.chowdaslime.resonantinstruments.ResonantInstruments;
import net.chowdaslime.resonantinstruments.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class AttunementInfoCategory implements IRecipeCategory<AttunementInfo> {

    public static final IRecipeType<AttunementInfo> TYPE =
            IRecipeType.create(
                    Identifier.fromNamespaceAndPath(ResonantInstruments.MODID, "attunement_info"),
                    AttunementInfo.class
            );

    public static final int WIDTH = 180;
    public static final int HEIGHT = 130;

    private final IDrawable icon;
    private final IDrawable background;
    private final Component title;

    public AttunementInfoCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.UNATTUNED_FORK.get()));
        this.background = guiHelper.createDrawable(
                Identifier.fromNamespaceAndPath("jei", "textures/gui/gui_vanilla.png"),
                0, 0, WIDTH, HEIGHT
        );
        this.title = Component.translatable("jei.resonantinstruments.category.attunement_info");
    }

    @Override
    public IRecipeType<AttunementInfo> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AttunementInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 20, 4)
                .add(recipe.icon());
    }

    @Override
    public void draw(AttunementInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        int textWidth = WIDTH - 30;
        int textColor = 0xFF7E7E7E;

        int y = 6;
        graphics.textWithWordWrap(font, recipe.howToObtain(), 6, y, textWidth, textColor, false);
        y += font.wordWrapHeight(recipe.howToObtain(), textWidth) + 8;

        for (Component line : recipe.description()) {
            graphics.textWithWordWrap(font, line, 6, y, textWidth, textColor, false);
            y += font.wordWrapHeight(line, textWidth) + 4;
        }
    }
}