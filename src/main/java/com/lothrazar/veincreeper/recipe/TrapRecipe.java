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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class TrapRecipe implements Recipe<Container> {

  private final ResourceLocation id;
  private Ingredient input; // Items.RED_DYE 
  private ResourceLocation inputEntity;
  private ResourceLocation outputEntity;
  CompoundTag inputTags = new CompoundTag();
  CompoundTag outputTags = new CompoundTag();

  public TrapRecipe(ResourceLocation id, Ingredient ing, ResourceLocation entityType, ResourceLocation entityOut, CompoundTag tag, CompoundTag tago) {
    super();
    this.id = id;
    this.input = ing;
    this.inputEntity = entityType;
    this.outputEntity = entityOut;
    this.inputTags = (tag == null ? new CompoundTag() : tag); //non-null
    this.outputTags = (tago == null ? new CompoundTag() : tago); //non-null
    VeinCreeperMod.LOGGER.error(id + " intag" + inputTags + "          ot  " + this.outputTags);
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

  @Override
  public String toString() {
    return "TrapRecipe [id=" + id + ", input=" + input + ", inputEntity=" + inputEntity + ", outputEntity=" + outputEntity + ", inputTags=" + inputTags + ", outputTags=" + outputTags + "]";
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
            System.out.println("Sorry, nested NBT/complex values currently not supported");
          }
        }
      }
    }

    @Override
    public TrapRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      var in = Ingredient.fromNetwork(buffer);
      var target = buffer.readResourceLocation();
      var entity = buffer.readResourceLocation();
      var nbt = buffer.readNbt();
      TrapRecipe r = new TrapRecipe(recipeId, in, target, entity, buffer.readNbt(), buffer.readNbt());
      //server reading recipe from client or vice/versa 
      return r;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, TrapRecipe recipe) {
      recipe.input.toNetwork(buffer);
      buffer.writeResourceLocation(recipe.inputEntity);
      buffer.writeResourceLocation(recipe.outputEntity);
      buffer.writeNbt(recipe.inputTags);
      buffer.writeNbt(recipe.outputTags);
    }
  }

  public boolean matches(Level level, ItemStack dyeFound, Entity entity) {
    var trapped = ForgeRegistries.ENTITY_TYPES.getValue(inputEntity);
    boolean matches = (trapped == entity.getType() && this.input.test(dyeFound));
    if (matches && !this.inputTags.isEmpty()) {
      boolean tagMatch = false;
      CompoundTag entityData = new CompoundTag(); // bullshit what it isentity.getPersistentData();
      entity.saveWithoutId(entityData);
      for (String key : this.inputTags.getAllKeys()) {
        if (inputTags.getTagType(key) == Tag.TAG_INT) {
          System.out.println(key + "COMPARE input int  = " + inputTags.getInt(key) + " VS " + entityData.getInt(key));
          tagMatch = (inputTags.getInt(key) == entityData.getInt(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            System.out.println("FAILED integer tagmatch from recipe " + entityData);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_SHORT) {
          System.out.println("getShort" + inputTags.getShort(key));
          tagMatch = (inputTags.getShort(key) == entityData.getShort(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            System.out.println("FAILED getShort tagmatch from recipe " + inputTags);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_BYTE) {
          System.out.println("boolinput tags  " + inputTags.getBoolean(key) + "  vs " + entityData.getBoolean(key));
          tagMatch = (inputTags.getBoolean(key) == entityData.getBoolean(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            System.out.println("FAILED boolean tagmatch from recipe " + inputTags);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_STRING) {
          System.out.println("STR " + inputTags.getString(key));
          tagMatch = (inputTags.getString(key).equalsIgnoreCase(entityData.getString(key)));
          matches = matches && tagMatch;
          if (!tagMatch) {
            System.out.println("FAILED string tagmatch from recipe " + inputTags);
          }
        }
      }
    }
    return matches;
  }

  public void spawnEntityResult(ServerLevel level, BlockPos pos, Entity entityToKill) {
    //gogogo 
    //          c.setPos(pos.getCenter());
    //          level.addFreshEntity(c);
    //does the recipe match? yes? ok
    //is it single use?
    //          level.destroyBlock(pos, true);
    //
    var trapped = ForgeRegistries.ENTITY_TYPES.getValue(this.outputEntity);
    //
    Entity entity;
    if (this.inputEntity.equals(this.outputEntity)) {
      System.out.println("haha haxor keep same entity dont make a new one duh");
      entity = entityToKill;// just edit the fucker
    }
    else {
      //ok normal flow
      entity = trapped.spawn(level, pos, MobSpawnType.CONVERSION);
    }
    System.out.println("spawnEntityResult " + this.outputTags);
    if (!this.outputTags.isEmpty()) {
      CompoundTag entityData = new CompoundTag(); // bullshit what it isentity.getPersistentData();
      entity.saveWithoutId(entityData);
      for (String key : this.outputTags.getAllKeys()) {
        if (inputTags.getTagType(key) == Tag.TAG_INT) {
          System.out.println("WRITE int//short spawning " + outputTags.getInt(key));
          entity.getPersistentData().putInt(key, outputTags.getInt(key));
        }
        if (inputTags.getTagType(key) == Tag.TAG_BYTE) {
          System.out.println("WRITE bool " + outputTags.getBoolean(key));
          entity.getPersistentData().putBoolean(key, outputTags.getBoolean(key));
        }
        if (inputTags.getTagType(key) == Tag.TAG_STRING) {
          System.out.println("WRITE STR " + outputTags.getString(key));
          entity.getPersistentData().putString(key, outputTags.getString(key));
        }
      }
      System.out.println("actually load nbt");
      entity.load(entityData);
    }
    else {
      System.out.println("output tags empty" + this.id);
    }
  }
}
