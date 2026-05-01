package com.lothrazar.veincreeper.recipe.jei;

import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.recipe.TrapRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TrapCatalyst implements IRecipeCategory<TrapRecipe> {

  public static final ResourceLocation ID = CreeperRegistry.TRAP_RECIPE.getId();
  static final RecipeType<TrapRecipe> TYPE = new RecipeType<>(ID, TrapRecipe.class);
  private IDrawable gui;
  private IDrawable icon;

  public TrapCatalyst(IGuiHelper helper) {
    gui = helper.drawableBuilder(ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID, "textures/gui/jei_trap.png"), 0, 0, 169, 69).setTextureSize(169, 69).build();
    icon = helper.drawableBuilder(ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID, "textures/block/trap.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @SuppressWarnings("removal")
  @Override
  public IDrawable getBackground() {
    return gui;
  }

  @Override
  public Component getTitle() {
    return Component.translatable(VeinCreeperMod.MODID + ".trap");
  }

  @Override
  public void draw(TrapRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics ms, double mouseX, double mouseY) {
    var font = Minecraft.getInstance().font;
    final int FONT = 14210752;
    ms.drawString(font, recipe.inputEntity.getEntityId() + " ", 0, 2, FONT);
    ms.drawString(font, recipe.outputEntity.getEntityId() + " ", 0, 60, FONT);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, TrapRecipe recipe, IFocusGroup focuses) {
    builder.addSlot(RecipeIngredientRole.INPUT, 4, 19).addIngredients(recipe.getInput());
  }

  @Override
  public RecipeType<TrapRecipe> getRecipeType() {
    return TYPE;
  }
}
