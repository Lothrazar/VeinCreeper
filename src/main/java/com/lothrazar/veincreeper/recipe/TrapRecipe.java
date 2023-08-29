package com.lothrazar.veincreeper.recipe;

import com.google.gson.JsonObject;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class TrapRecipe implements Recipe<Container> {

  private final ResourceLocation id;
  private Ingredient itemOnGround; // Items.RED_DYE 
  private ResourceLocation trappedEntity;
  private ResourceLocation transformedEntity;
  String nbt = null;

  public TrapRecipe(ResourceLocation id, Ingredient ing, ResourceLocation entityType, ResourceLocation entityOut) {
    super();
    this.id = id;
    this.itemOnGround = ing;
    this.trappedEntity = entityType;
    this.transformedEntity = entityOut;
  }

  @Override
  public boolean matches(Container c, Level level) {
    return false; //never match any container
  }

  @Override
  public ItemStack assemble(Container c, RegistryAccess level) {
    return getResultItem(level);
  }

  @Override
  public boolean canCraftInDimensions(int x, int y) {
    return true;
  }

  @Override
  public ItemStack getResultItem(RegistryAccess level) {
    return ItemStack.EMPTY;
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeType<?> getType() {
    return CreeperRegistry.RECIPE_TRAP.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CreeperRegistry.TRAP_SERIALIZER.get();
  }

  public static class SerializeTrapRecipe implements RecipeSerializer<TrapRecipe> {

    public SerializeTrapRecipe() {}

    //    {
    //      "type": "forge:mod_loaded",
    //      "value": "veincreeper"
    //      },
    @Override
    public TrapRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
      try {
        Ingredient itemOnGround = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "itemOnGround"));
        String trappedEntity = json.get("trappedEntity").getAsJsonObject().get("id").getAsString();
        String transformedEntity = json.get("transformedEntity").getAsJsonObject().get("id").getAsString();
        VeinCreeperMod.LOGGER.debug("SUCCESS loading trap recipe  " + recipeId);
        return new TrapRecipe(recipeId, itemOnGround, new ResourceLocation(trappedEntity), new ResourceLocation(transformedEntity));
      }
      catch (Exception e) {
        VeinCreeperMod.LOGGER.error("Error loading trap recipe  " + recipeId, e);
        return null;
      }
    }

    @Override
    public TrapRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      var in = Ingredient.fromNetwork(buffer);
      var target = buffer.readResourceLocation();
      var entity = buffer.readResourceLocation();
      TrapRecipe r = new TrapRecipe(recipeId, in, target, entity);
      //server reading recipe from client or vice/versa 
      return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, TrapRecipe recipe) {
      recipe.itemOnGround.toNetwork(buffer);
      buffer.writeResourceLocation(recipe.trappedEntity);
      buffer.writeResourceLocation(recipe.transformedEntity);
    }
  }
}