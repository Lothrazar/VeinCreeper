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
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
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

  // Full Recipe objects are no longer synced to the client at all (only recipe-book display data is).
  // Reading the local integrated server's RecipeManager directly is the only way to get real recipe
  // instances here; this only works in singleplayer/LAN-hosted worlds, so a JEI-connected dedicated-server
  // client simply won't see these categories populated.
  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    MinecraftServer server = Minecraft.getInstance().getSingleplayerServer();
    if (server == null) {
      return;
    }
    registry.addRecipes(ExplosionCatalyst.TYPE, List.copyOf(server.getRecipeManager().recipeMap().byType(CreeperRegistry.EXPLOSION_RECIPE.get()).stream().map(h -> h.value()).toList()));
    registry.addRecipes(TrapCatalyst.TYPE, List.copyOf(server.getRecipeManager().recipeMap().byType(CreeperRegistry.TRAP_RECIPE.get()).stream().map(h -> h.value()).toList()));
  }
}
