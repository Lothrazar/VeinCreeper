package com.lothrazar.veincreeper.recipe.zen;

import org.openzen.zencode.java.ZenCodeType;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.recipe.TrapRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;

@ZenRegister
@ZenCodeType.Name("mods.veincreeper.trap")
public class TrapRecipeZen implements IRecipeManager<TrapRecipe> {

  @Override
  public RecipeType<TrapRecipe> getRecipeType() {
    return CreeperRegistry.TRAP_RECIPE.get();
  }

  @ZenCodeType.Method
  public void addRecipe(String name, IIngredient input, String entityType, String entityOut) {
    name = fixRecipeName(name);
    TrapRecipe m = new TrapRecipe(new ResourceLocation("crafttweaker", name),
        input.asVanillaIngredient(),
        new ResourceLocation(entityType),
        new ResourceLocation(entityOut),
        null, null);
    CraftTweakerAPI.apply(new ActionAddRecipe<TrapRecipe>(this, m, ""));
    VeinCreeperMod.LOGGER.info("zs trap: Recipe loaded " + m.getId().toString());
  }

  @ZenCodeType.Method
  public void removeRecipe(String... names) {
    removeByName(names);
    VeinCreeperMod.LOGGER.info("zs trap: Recipe removed " + names);
  }
}
