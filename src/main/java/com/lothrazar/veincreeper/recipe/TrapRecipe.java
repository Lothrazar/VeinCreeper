package com.lothrazar.veincreeper.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class TrapRecipe implements Recipe<Container> {

  private final ResourceLocation id;
  private final Ingredient input;
  public final EntityIngredient inputEntity;
  public final EntityIngredient outputEntity;

  public TrapRecipe(ResourceLocation id, Ingredient ing, ResourceLocation entityType, ResourceLocation entityOut, CompoundTag tag, CompoundTag tago) {
    super();
    this.id = id;
    this.input = ing;
    this.inputEntity = new EntityIngredient(entityType, tag);
    this.outputEntity = new EntityIngredient(entityOut, tago);
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
    return CreeperRegistry.TRAP_RECIPE.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return CreeperRegistry.TRAP_SERIALIZER.get();
  }

  @Override
  public String toString() {
    return "TrapRecipe [id=" + id + ", input=" + getInput() + ", inputEntity=" + inputEntity + ", outputEntity=" + outputEntity + "]";
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
        Ingredient itemOnGround = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
        String trappedEntity = json.get("trappedEntity").getAsJsonObject().get("entity").getAsString();
        String transformedEntity = json.get("result").getAsJsonObject().get("entity").getAsString();
        VeinCreeperMod.LOGGER.debug(" loading trap recipe  " + recipeId);
        CompoundTag inputTags = new CompoundTag();
        if (json.get("trappedEntity").getAsJsonObject().has("nbt")) {
          var jsonMatch = json.get("trappedEntity").getAsJsonObject().get("nbt").getAsJsonObject();
          this.mapJsonOntoTag(jsonMatch, inputTags);
        }
        CompoundTag outputTags = new CompoundTag();
        if (json.get("result").getAsJsonObject().has("nbt")) {
          var jsonMatch = json.get("result").getAsJsonObject().get("nbt").getAsJsonObject();
          this.mapJsonOntoTag(jsonMatch, outputTags);
        }
        return new TrapRecipe(recipeId, itemOnGround, new ResourceLocation(trappedEntity), new ResourceLocation(transformedEntity), inputTags, outputTags);
      }
      catch (Exception e) {
        VeinCreeperMod.LOGGER.error("Error loading trap recipe  " + recipeId, e);
        return null;
      }
    }

    private void mapJsonOntoTag(JsonObject json, CompoundTag tagMutable) {
      //if there are no keys, or no checks put anything into the mutable tag, then it remains empty, so check .isEmpty()
      for (String key : json.keySet()) {
        JsonElement el = json.get(key);
        if (el instanceof JsonPrimitive p) {
          if (p.isBoolean()) {
            tagMutable.putBoolean(key, p.getAsBoolean());
          }
          else if (p.isNumber()) {
            tagMutable.putInt(key, p.getAsInt());
          }
          else if (p.isString()) {
            tagMutable.putString(key, p.getAsString());
          }
          else {
            VeinCreeperMod.LOGGER.error("Sorry, nested NBT/complex values currently not supported yet. use boolean true/false, or whole numbers or strings");
            VeinCreeperMod.LOGGER.error(key + "=" + el);
          }
        }
      }
    }

    @Override
    public TrapRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      var inputIngredient = Ingredient.fromNetwork(buffer);
      var inputEnt = buffer.readResourceLocation();
      var inputTag = buffer.readNbt();
      var outEnt = buffer.readResourceLocation();
      var outTag = buffer.readNbt();
      TrapRecipe r = new TrapRecipe(recipeId, inputIngredient, inputEnt, outEnt, inputTag, outTag);
      //server reading recipe from client or vice/versa 
      return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, TrapRecipe recipe) {
      recipe.getInput().toNetwork(buffer);
      buffer.writeResourceLocation(recipe.inputEntity.getEntityId());
      buffer.writeNbt(recipe.inputEntity.getNbt());
      buffer.writeResourceLocation(recipe.outputEntity.getEntityId());
      buffer.writeNbt(recipe.outputEntity.getNbt());
    }
  }

  public boolean matches(Level level, ItemStack dyeFound, Entity entity) {
    var trapped = ForgeRegistries.ENTITY_TYPES.getValue(inputEntity.getEntityId());
    boolean matches = (trapped == entity.getType() && this.getInput().test(dyeFound));
    if (matches && !this.inputEntity.getNbt().isEmpty()) {
      boolean tagMatch = false;
      CompoundTag entityData = new CompoundTag(); // bullshit what it isentity.getPersistentData();
      entity.saveWithoutId(entityData);
      var inputTags = this.inputEntity.getNbt();
      for (String key : inputTags.getAllKeys()) {
        if (inputTags.getTagType(key) == Tag.TAG_INT) {
          tagMatch = (inputTags.getInt(key) == entityData.getInt(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED integer tagmatch from recipe " + entityData);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_SHORT) {
          VeinCreeperMod.LOGGER.info("getShort" + inputTags.getShort(key));
          tagMatch = (inputTags.getShort(key) == entityData.getShort(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED getShort tagmatch from recipe " + inputTags);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_BYTE) {
          VeinCreeperMod.LOGGER.info("boolinput tags  " + inputTags.getBoolean(key) + "  vs " + entityData.getBoolean(key));
          tagMatch = (inputTags.getBoolean(key) == entityData.getBoolean(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info(id + "FAILED boolean tagmatch from recipe " + inputTags);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_STRING) {
          VeinCreeperMod.LOGGER.info("STR " + inputTags.getString(key));
          tagMatch = (inputTags.getString(key).equalsIgnoreCase(entityData.getString(key)));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED string tagmatch from recipe " + inputTags);
          }
        }
      }
    }
    return matches;
  }

  public void spawnEntityResult(ServerLevel level, BlockPos pos, Entity entityToKill) {
    //TODO: target "minecraft:player" ???
    var entityFromRecipe = ForgeRegistries.ENTITY_TYPES.getValue(this.outputEntity.getEntityId());
    if (entityFromRecipe == null) {
      VeinCreeperMod.LOGGER.error("Recipe spawn failed, entity not registered " + entityFromRecipe);
    }
    // 
    if (this.inputEntity.getEntityId().equals(this.outputEntity.getEntityId())) {
      VeinCreeperMod.LOGGER.info("haha haxor keep same entity dont make a new one duh");
    }
    else {
      //ok normal flow
      if (entityToKill instanceof Player == false) {
        VeinCreeperMod.LOGGER.info("kill and remove enitty" + entityToKill);
        entityToKill.remove(RemovalReason.KILLED);
      }
      VeinCreeperMod.LOGGER.info("spawn New entity from type  " + entityFromRecipe);
      entityToKill = entityFromRecipe.spawn(level, pos, MobSpawnType.CONVERSION);
    }
    VeinCreeperMod.LOGGER.info("spawnEntityResult " + this.outputEntity);
    var inputTags = this.inputEntity.getNbt();
    var outputTags = this.outputEntity.getNbt();
    if (!outputTags.isEmpty()) {
      //extract data. edit and push it back in
      CompoundTag entityData = new CompoundTag(); // bullshit what it isentity.getPersistentData();
      entityToKill.save(entityData); //save WITH ID?
      for (String key : outputTags.getAllKeys()) {
        VeinCreeperMod.LOGGER.info(" outputTags = " + outputTags);
        if (inputTags.getTagType(key) == Tag.TAG_INT) {
          VeinCreeperMod.LOGGER.info("WRITE int//short spawning " + outputTags.getInt(key));
          entityData.putInt(key, outputTags.getInt(key));
        }
        else if (inputTags.getTagType(key) == Tag.TAG_BYTE) {
          VeinCreeperMod.LOGGER.info("WRITE bool " + outputTags.getBoolean(key));
          entityData.putBoolean(key, outputTags.getBoolean(key));
        }
        else if (inputTags.getTagType(key) == Tag.TAG_STRING) {
          VeinCreeperMod.LOGGER.info("WRITE STR " + outputTags.getString(key));
          entityData.putString(key, outputTags.getString(key));
        }
        else {
          VeinCreeperMod.LOGGER.info(this.id + "unknown type!!!!!!!!!!!!!!!!!! " + inputTags.getTagType(key));
        }
      }
      VeinCreeperMod.LOGGER.info("actually load nbt into entityData=" + entityData);
      entityToKill.load(entityData);
    }
    else {
      VeinCreeperMod.LOGGER.info("output tags empty for recipe " + this.id);
    }
  }

  public Ingredient getInput() {
    return input;
  }
}
