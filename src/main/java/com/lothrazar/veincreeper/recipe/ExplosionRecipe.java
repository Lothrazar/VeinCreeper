package com.lothrazar.veincreeper.recipe;

import com.google.gson.JsonObject;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.conf.CreeperConfigManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class ExplosionRecipe implements Recipe<Container> {

  private final ResourceLocation id;
  //  "_comment":"veincreepers array. targets array. ore singular. bonus array",
  private TagKey<Block> replace = BlockTags.STONE_ORE_REPLACEABLES;//list of TagIngredient
  private BlockIngredient oreOutput = null; // list of BlockIngredient
  private EntityIngredient entityType;// list
  private BlockIngredient bonus = null;// BonusBlockIngredient

  public ExplosionRecipe(ResourceLocation id, ResourceLocation entityType,
      TagKey<Block> blockReplace,
      Block result,
      Block bonus, Integer chance) {
    super();
    this.id = id;
    this.replace = blockReplace;
    this.oreOutput = new BlockIngredient(result);
    this.entityType = new EntityIngredient(entityType);
    this.bonus = new BlockIngredient(bonus, chance);
  }

  public ExplosionRecipe(ResourceLocation id, ResourceLocation entityType, TagKey<Block> input, Block result) {
    this(id, entityType, input, result, null, null);
  }

  public BlockIngredient getOre() {
    return oreOutput;
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
    return getResultItem();
  }

  public ItemStack getResultItem() {
    return new ItemStack(oreOutput.getBlock());
  }

  @Override
  public ResourceLocation getId() {
    return id;
  }

  @Override
  public RecipeType<?> getType() {
    return CreeperRegistry.EXPLOSION_RECIPE.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CreeperRegistry.R_SERIALIZER.get();
  }

  public TagKey<Block> getReplace() {
    return replace;
  }

  public BlockIngredient getBonus() {
    return bonus;
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
        String creeperId = json.get(VeinCreeperMod.MODID).getAsString();
        ResourceLocation entity = new ResourceLocation(VeinCreeperMod.MODID, creeperId);
        JsonObject oreJson = json.get("ore").getAsJsonObject();
        String blockId = oreJson.get("block").getAsString();
        Block ore = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockId));
        Block bonus = null;
        int bonusChance = 0;
        VeinCreeperMod.LOGGER.debug("loading explosion recipe  " + recipeId);
        if (oreJson.has("bonus") && oreJson.has("bonusChance")) {
          // optional bonus
          String bonusId = oreJson.get("bonus").getAsString();
          bonus = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(bonusId));
          bonusChance = oreJson.get("bonusChance").getAsInt();
          //
          return new ExplosionRecipe(recipeId, entity, targetMe, ore, bonus, bonusChance);
        }
        else {
          return new ExplosionRecipe(recipeId, entity, targetMe, ore);
        }
      }
      catch (Exception e) {
        VeinCreeperMod.LOGGER.error("Error loading recipe  " + recipeId, e);
        return null;
      }
    }

    @Override
    public ExplosionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      var target = buffer.readResourceLocation();
      var block = buffer.readResourceLocation();
      var entity = buffer.readResourceLocation();
      ExplosionRecipe r = new ExplosionRecipe(recipeId,
          entity,
          TagKey.create(Registries.BLOCK, target),
          ForgeRegistries.BLOCKS.getValue(block));
      //server reading recipe from client or vice/versa 
      return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, ExplosionRecipe recipe) {
      // replace, block, entity 
      buffer.writeResourceLocation(recipe.replace.location());
      var key = ForgeRegistries.BLOCKS.getKey(recipe.oreOutput.getBlock());
      buffer.writeResourceLocation(key);
      buffer.writeResourceLocation(recipe.entityType.getEntityId());
    }
  }

  public boolean matches(Entity exploder, BlockState blockstate) {
    final String key = CreeperConfigManager.getKeyFromEntity(exploder);
    var src = entityType.getEntityId().getPath().toString();
    //namespace is always mod id. at least for this recipe
    boolean match = src.equals(key) && blockstate.is(replace);
    return match;
  }

  public ResourceLocation getEntityType() {
    return this.entityType.getEntityId();
  }

  public boolean hasBonus() {
    return this.getBonus() != null && this.getBonus().getBlock() != null && this.getBonus().getChance() > 0;
  }
}
