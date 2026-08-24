package com.lothrazar.veincreeper.recipe.jei;

import java.util.List;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.common.Internal;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class PluginJEI implements IModPlugin {

  private static final Identifier ID = Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "jei");

  @Override
  public Identifier getPluginUid() {
    return ID;
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(new ItemStack(CreeperRegistry.TRAP.get()), TrapCatalyst.TYPE);
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    registry.addRecipeCategories(new ExplosionCatalyst(guiHelper));
    registry.addRecipeCategories(new TrapCatalyst(guiHelper));
  }

  // Full Recipe objects are no longer synced to the client via vanilla's own protocol (only recipe-book
  // display data is). JEI fills that gap itself: when JEI is installed on the server with a matching mod
  // loader, it syncs the full RecipeManager - every recipe type, not just vanilla's - into a client-side
  // RecipeMap.
  // TODO: probably change this if Internal.getClientSyncedRecipes ever gets access in jei's-API
  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    var clientSyncedRecipes = Internal.getClientSyncedRecipes();
    registry.addRecipes(ExplosionCatalyst.TYPE, List.copyOf(clientSyncedRecipes.byType(CreeperRegistry.EXPLOSION_RECIPE.get()).stream().map(h -> h.value()).toList()));
    registry.addRecipes(TrapCatalyst.TYPE, List.copyOf(clientSyncedRecipes.byType(CreeperRegistry.TRAP_RECIPE.get()).stream().map(h -> h.value()).toList()));
  }
}
