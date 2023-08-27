package com.lothrazar.veincreeper.recipe;

import com.google.gson.JsonObject;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class ExplosionRecipe implements Recipe<Container> {

  private final ResourceLocation id;
  private TagKey<Block> replace = BlockTags.STONE_ORE_REPLACEABLES;
  private Block oreOutput = null; // Blocks.COAL_ORE;
  private String entityType;
  private Block bonus = null;
  private float bonusChance = 0;

  public ExplosionRecipe(ResourceLocation id, TagKey<Block> input, Block result, String entityType,
      Block bonus, float chance) {
    super();
    this.id = id;
    this.replace = input;
    this.oreOutput = result;
    this.entityType = entityType;
    this.bonus = bonus;
    this.bonusChance = chance;
  }

  public ExplosionRecipe(ResourceLocation id, TagKey<Block> input, Block result, String entityType) {
    this(id, input, result, entityType, null, 0);
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
    return new ItemStack(oreOutput);
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeType<?> getType() {
    return CreeperRegistry.RECIPE.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CreeperRegistry.R_SERIALIZER.get();
  }

  public Block getOreOutput() {
    return oreOutput;
  }

  public void setOreOutput(Block oreOutput) {
    this.oreOutput = oreOutput;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public TagKey<Block> getReplace() {
    return replace;
  }

  public void setReplace(TagKey<Block> replace) {
    this.replace = replace;
  }

  public Block getBonus() {
    return bonus;
  }

  public void setBonus(Block bonus) {
    this.bonus = bonus;
  }

  public float getBonusChance() {
    return bonusChance;
  }

  public void setBonusChance(float bonusChance) {
    this.bonusChance = bonusChance;
  }

  public static class SerializePartyRecipe implements RecipeSerializer<ExplosionRecipe> {

    public SerializePartyRecipe() {}

    //    {
    //      "type": "forge:mod_loaded",
    //      "value": "veincreeper"
    //      },
    @Override
    public ExplosionRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
      try {
        String target = json.get("target").getAsJsonObject().get("tag").getAsString();
        TagKey<Block> targetMe = TagKey.create(Registries.BLOCK, new ResourceLocation(target));
        String entity = json.get(VeinCreeperMod.MODID).getAsString();
        JsonObject oreJson = json.get("ore").getAsJsonObject();
        String blockId = oreJson.get("block").getAsString();
        Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
        Block bonus = null;
        float bonusChance = 0;
        if (oreJson.has("bonus") && oreJson.has("bonusChance")) {
          // optional bonus
          String bonusId = oreJson.get("bonus").getAsString();
          bonus = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(bonusId));
          bonusChance = oreJson.get("bonusChance").getAsFloat();
        }
        VeinCreeperMod.LOGGER.debug("SUCCESS loading recipe  " + recipeId);
        return new ExplosionRecipe(recipeId, targetMe, ore, entity, bonus, bonusChance);
      }
      catch (Exception e) {
        VeinCreeperMod.LOGGER.error("Error loading recipe  " + recipeId, e);
        return null;
      }
    }

    @Override
    public ExplosionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      var target = buffer.readResourceLocation();//Ingredient.fromNetwork(buffer);
      var block = buffer.readResourceLocation();
      var entity = buffer.readUtf();
      ExplosionRecipe r = new ExplosionRecipe(recipeId,
          TagKey.create(Registries.BLOCK, target),
          ForgeRegistries.BLOCKS.getValue(block), entity);
      //server reading recipe from client or vice/versa 
      return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ExplosionRecipe recipe) {
      // replace, block, entity 
      buffer.writeResourceLocation(recipe.replace.location());
      var key = ForgeRegistries.BLOCKS.getKey(recipe.oreOutput);
      buffer.writeResourceLocation(key);
      buffer.writeUtf(recipe.entityType);
    }
  }
}
