package com.lothrazar.veincreeper.recipe.jei;

import java.util.List;
import java.util.Objects;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class PluginJEI implements IModPlugin {

  private static final ResourceLocation ID = new ResourceLocation(VeinCreeperMod.MODID, "jei");

  @Override
  public ResourceLocation getPluginUid() {
    return ID;
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(new ItemStack(CreeperRegistry.TRAP.get()), TrapCatalyst.TYPE);
    //    registration.addRecipeCatalyst(new ItemStack(CreeperRegistry.TRAP.get()), ExplosionCatalyst.TYPE);
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    registry.addRecipeCategories(new ExplosionCatalyst(guiHelper));
    registry.addRecipeCategories(new TrapCatalyst(guiHelper));
  }

  @Override
  public void registerRecipes(IRecipeRegistration registry) {
    ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
    registry.addRecipes(ExplosionCatalyst.TYPE, List.copyOf(world.getRecipeManager().getAllRecipesFor(CreeperRegistry.EXPLOSION_RECIPE.get())));
    registry.addRecipes(TrapCatalyst.TYPE, List.copyOf(world.getRecipeManager().getAllRecipesFor(CreeperRegistry.TRAP_RECIPE.get())));
  }
}