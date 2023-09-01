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

  public static final ResourceLocation ID = new ResourceLocation(CreeperRegistry.TRAP_RECIPE.getId().toString());
  static final RecipeType<TrapRecipe> TYPE = new RecipeType<>(ID, TrapRecipe.class);
  private IDrawable gui;
  private IDrawable icon;

  public TrapCatalyst(IGuiHelper helper) {
    gui = helper.drawableBuilder(new ResourceLocation(VeinCreeperMod.MODID, "textures/gui/jei_trap.png"), 0, 0, 169, 69).setTextureSize(169, 69).build();
    icon = helper.drawableBuilder(new ResourceLocation(VeinCreeperMod.MODID, "textures/block/trap.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

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
    //    TagKey<Block> tag = recipe.getReplace();
    //    //addIngredients
    builder.addSlot(RecipeIngredientRole.INPUT, 4, 19).addIngredients(recipe.getInput());
    //INPUT mob and output mob write
    //    builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 19).addItemStack(new ItemStack(recipe.getOreOutput().asItem()));
    //    if (recipe.getBonus() != null)
    //      builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 19).addItemStack(new ItemStack(recipe.getBonus().asItem()));
  }

  @Override
  public RecipeType<TrapRecipe> getRecipeType() {
    return TYPE;
  }
}
