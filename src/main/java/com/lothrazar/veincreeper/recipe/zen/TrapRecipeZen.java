package com.lothrazar.veincreeper.recipe.zen;

import org.openzen.zencode.java.ZenCodeType;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.recipe.TrapRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
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
    TrapRecipe m = new TrapRecipe(input.asVanillaIngredient(),
        Identifier.parse(entityType),
        Identifier.parse(entityOut),
        null, null);
    Identifier id = Identifier.fromNamespaceAndPath(CraftTweakerConstants.MOD_ID, name);
    CraftTweakerAPI.apply(new ActionAddRecipe<>(this, new RecipeHolder<>(id, m)));
    VeinCreeperMod.LOGGER.info("zs trap: Recipe loaded " + id);
  }

  @ZenCodeType.Method
  public void removeRecipe(String... names) {
    removeByName(names);
    VeinCreeperMod.LOGGER.info("zs trap: Recipe removed " + java.util.Arrays.toString(names));
  }
}
