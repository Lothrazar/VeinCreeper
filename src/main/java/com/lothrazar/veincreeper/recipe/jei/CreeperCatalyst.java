package com.lothrazar.veincreeper.recipe.jei;

import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CreeperCatalyst implements IRecipeCategory<ExplosionRecipe> {

  public static final ResourceLocation ID = new ResourceLocation(CreeperRegistry.EXPLOSION_RECIPE.getId().toString());
  static final RecipeType<ExplosionRecipe> TYPE = new RecipeType<>(ID, ExplosionRecipe.class);
  private IDrawable gui;
  private IDrawable icon;

  public CreeperCatalyst(IGuiHelper helper) {
    //    gui = helper.drawableBuilder(new ResourceLocation(VeinCreeperMod.MODID, "textures/gui/jei.png"), 0, 0, 169, 69).setTextureSize(169, 69).build();
    //    icon = helper.drawableBuilder(new ResourceLocation(VeinCreeperMod.MODID, "textures/block/grinder_top.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
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
    return Component.translatable(EntityType.CREEPER.getDescriptionId());
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ExplosionRecipe recipe, IFocusGroup focuses) {
    TagKey<Block> tag = recipe.getReplace();
    // 
    //         builder.addSlot(RecipeIngredientRole.INPUT, 4, 19).addItemStacks(null)
    builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 19).addItemStack(new ItemStack(recipe.getOreOutput().asItem()));
    if (recipe.getBonus() != null)
      builder.addSlot(RecipeIngredientRole.OUTPUT, 138, 19).addItemStack(new ItemStack(recipe.getBonus().asItem()));
  }

  @Override
  public RecipeType<ExplosionRecipe> getRecipeType() {
    return TYPE;
  }
}
