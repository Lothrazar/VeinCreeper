package com.lothrazar.veincreeper.recipe.jei;

import java.util.List;
import org.joml.Quaternionf;
import com.google.common.collect.Lists;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3f;

public class ExplosionCatalyst implements IRecipeCategory<ExplosionRecipe> {

  public static final Identifier ID = CreeperRegistry.EXPLOSION_RECIPE.getId();
  static final RecipeType<ExplosionRecipe> TYPE = new RecipeType<>(ID, ExplosionRecipe.class);
  private IDrawable gui;
  private IDrawable icon;

  public ExplosionCatalyst(IGuiHelper helper) {
    gui = helper.drawableBuilder(Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "textures/gui/jei_explosion.png"), 0, 0, 169, 69).setTextureSize(169, 69).build();
    icon = helper.drawableBuilder(Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "textures/block/trap.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public int getWidth() {
    return 169;
  }

  @Override
  public int getHeight() {
    return 69;
  }

  @Override
  public Component getTitle() {
    return Component.translatable(VeinCreeperMod.MODID + ".explosion.jei");
  }

  @Override
  public void draw(ExplosionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor ms, double mouseX, double mouseY) {
    gui.draw(ms, 0, 0);
    var font = Minecraft.getInstance().font;
    final int FONT = 0xEEEEEE;
    if (recipe.hasBonus()) {
      ms.text(font, recipe.getBonus().getChance() + "%", 148, 46, FONT);
    }
    try {
      EntityType<?> entityType = EntityType.byString(recipe.getEntityType().toString()).orElse(null);
      LivingEntity fakeEntity = (LivingEntity) entityType.create(Minecraft.getInstance().level, EntitySpawnReason.COMMAND);
//      Quaternionf ANGLE = (new Quaternionf()).rotationXYZ(0.43633232F, 2.1F, (float) Math.PI);

//      Vector3f vector3f = new Vector3f(0.0F, fakeEntity.getBbHeight() / 2.0F + p_275604_ * fakeEntity.getScale(), 0.0F);
      int x1=30, y1=64, x2=x1+20, y2=y1+20, scale=20; // TODO: test this

      InventoryScreen.extractEntityInInventoryFollowsMouse(ms, x1,x2,y1,y2,scale,0.0625F,
          (float) mouseX, (float) mouseY,Minecraft.getInstance().player);


      if (fakeEntity instanceof VeinCreeper creeper) {
        ms.text(font, creeper.getDisplayName(), 2, 2, FONT);
      }
    }
    catch (Exception e) {
      VeinCreeperMod.LOGGER.error("Creeper rendering in jei plugin failed ", e);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, ExplosionRecipe recipe, IFocusGroup focuses) {
    TagKey<Block> tag = recipe.getReplace();
    List<Block> list = Lists.newArrayList();
    for (var holder : BuiltInRegistries.BLOCK.listElements().toList()) {
      Block block = holder.value();
      if (block.defaultBlockState().is(tag)) {
        list.add(block);
      }
    }
    builder.addSlot(RecipeIngredientRole.INPUT, 64, 29).addIngredients(Ingredient.of(list.stream()));
    builder.addSlot(RecipeIngredientRole.OUTPUT, 129, 19).addItemStack(recipe.getResultItem());
    if (recipe.hasBonus()) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, 129, 39).addItemStack(new ItemStack(recipe.getBonus().getBlock()));
    }
  }

  @Override
  public RecipeType<ExplosionRecipe> getRecipeType() {
    return TYPE;
  }
}
