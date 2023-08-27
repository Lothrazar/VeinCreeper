package com.lothrazar.veincreeper.recipe;

import org.openzen.zencode.java.ZenCodeType;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

@ZenRegister
@ZenCodeType.Name("mods.veincreeper.explosion")
public class CreeperZen implements IRecipeManager<ExplosionRecipe> {

  @Override
  public RecipeType<ExplosionRecipe> getRecipeType() {
    return CreeperRegistry.RECIPE.get();
  }

  @ZenCodeType.Method
  public void addRecipe(String name, String entityType, String target, String blockId) {
    name = fixRecipeName(name);
    TagKey<Block> targetMe = TagKey.create(Registries.BLOCK, new ResourceLocation(target));
    Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
    ExplosionRecipe m = new ExplosionRecipe(new ResourceLocation("crafttweaker", name),
        targetMe,
        ore, entityType);
    CraftTweakerAPI.apply(new ActionAddRecipe<ExplosionRecipe>(this, m, ""));
    VeinCreeperMod.LOGGER.info("crafttweaker: Recipe loaded " + m.getId().toString());
  }

  @ZenCodeType.Method
  public void addRecipe(String name, String entityType, String target, String blockId, String bonusId, float chance) {
    name = fixRecipeName(name);
    TagKey<Block> targetMe = TagKey.create(Registries.BLOCK, new ResourceLocation(target));
    Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
    Block bonus = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(bonusId));
    ExplosionRecipe m = new ExplosionRecipe(new ResourceLocation("crafttweaker", name),
        targetMe,
        ore, entityType, bonus, chance);
    CraftTweakerAPI.apply(new ActionAddRecipe<ExplosionRecipe>(this, m, ""));
    VeinCreeperMod.LOGGER.info("crafttweaker: Recipe loaded " + m.getId().toString());
  }

  @ZenCodeType.Method
  public void removeRecipe(String... names) {
    removeByName(names);
    VeinCreeperMod.LOGGER.info("crafttweaker: Recipe removed " + names);
  }
}
