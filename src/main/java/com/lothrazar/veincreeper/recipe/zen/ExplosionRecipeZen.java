package com.lothrazar.veincreeper.recipe.zen;

import org.openzen.zencode.java.ZenCodeType;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

@ZenRegister
@ZenCodeType.Name("mods.veincreeper.explosion")
public class ExplosionRecipeZen implements IRecipeManager<ExplosionRecipe> {

  @Override
  public RecipeType<ExplosionRecipe> getRecipeType() {
    return CreeperRegistry.EXPLOSION_RECIPE.get();
  }

  @ZenCodeType.Method
  public void addRecipe(String name, String blockTagTarget, String blockResult, String entityType) {
    name = fixRecipeName(name);
    TagKey<Block> targetMe = TagKey.create(Registries.BLOCK, ResourceLocation.parse(blockTagTarget));
    Block ore = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockResult));
    ExplosionRecipe m = new ExplosionRecipe(ResourceLocation.parse(entityType),
        targetMe, ore);
    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CraftTweakerConstants.MOD_ID, name);
    CraftTweakerAPI.apply(new ActionAddRecipe<>(this, new RecipeHolder<>(id, m)));
    VeinCreeperMod.LOGGER.debug("zs explosion: Recipe loaded " + id);
  }

  @ZenCodeType.Method
  public void addRecipe(String name, String blockTagTarget, String blockResult, String entityType, String bonusId, Integer chance) {
    name = fixRecipeName(name);
    TagKey<Block> targetMe = TagKey.create(Registries.BLOCK, ResourceLocation.parse(blockTagTarget));
    Block ore = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockResult));
    Block bonus = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(bonusId));
    ExplosionRecipe m = new ExplosionRecipe(ResourceLocation.parse(entityType),
        targetMe, ore, bonus, chance);
    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CraftTweakerConstants.MOD_ID, name);
    CraftTweakerAPI.apply(new ActionAddRecipe<>(this, new RecipeHolder<>(id, m)));
    VeinCreeperMod.LOGGER.debug("zs explosion: Recipe loaded " + id);
  }

  @ZenCodeType.Method
  public void removeRecipe(String... names) {
    removeByName(names);
    VeinCreeperMod.LOGGER.debug("zs explosion: Recipe removed " + java.util.Arrays.toString(names));
  }
}
