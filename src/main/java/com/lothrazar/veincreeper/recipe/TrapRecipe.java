package com.lothrazar.veincreeper.recipe;

import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

public class TrapRecipe implements Recipe<RecipeInput> {

  // not a real craftable recipe (matches() always false, consumed via custom dye/entity-trap logic
  // instead), so the recipe-book-facing methods below are all no-ops/defaults.
  public static final RecipeSerializer<TrapRecipe> SERIALIZER = new RecipeSerializer<>(SerializeTrapRecipe.CODEC, SerializeTrapRecipe.STREAM_CODEC);

  private final Ingredient input;
  public final EntityIngredient inputEntity;
  public final EntityIngredient outputEntity;

  public TrapRecipe(Ingredient ing, Identifier entityType, Identifier entityOut, CompoundTag tag, CompoundTag tago) {
    super();
    this.input = ing;
    this.inputEntity = new EntityIngredient(entityType, tag);
    this.outputEntity = new EntityIngredient(entityOut, tago);
  }

  @Override
  public boolean matches(RecipeInput c, Level level) {
    return false;
  }

  @Override
  public ItemStack assemble(RecipeInput c) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean showNotification() {
    return false;
  }

  @Override
  public String group() {
    return "";
  }

  @Override
  public PlacementInfo placementInfo() {
    return PlacementInfo.NOT_PLACEABLE;
  }

  @Override
  public RecipeBookCategory recipeBookCategory() {
    return RecipeBookCategories.CRAFTING_MISC;
  }

  @Override
  public RecipeType<TrapRecipe> getType() {
    return CreeperRegistry.TRAP_RECIPE.get();
  }

  @Override
  public RecipeSerializer<TrapRecipe> getSerializer() {
    return CreeperRegistry.TRAP_SERIALIZER.get();
  }

  @Override
  public String toString() {
    return "TrapRecipe [input=" + getInput() + ", inputEntity=" + inputEntity + ", outputEntity=" + outputEntity + "]";
  }

  private record MobData(Identifier entity, CompoundTag nbt) {

    static final MapCodec<MobData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        Identifier.CODEC.fieldOf("entity").forGetter(MobData::entity),
        CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(MobData::nbt)).apply(inst, MobData::new));
  }

  private static class SerializeTrapRecipe {

    static final MapCodec<TrapRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        Ingredient.CODEC.fieldOf("input").forGetter(TrapRecipe::getInput),
        MobData.CODEC.fieldOf("mob").forGetter(r -> new MobData(r.inputEntity.getEntityId(), r.inputEntity.getNbt())),
        MobData.CODEC.fieldOf("result").forGetter(r -> new MobData(r.outputEntity.getEntityId(), r.outputEntity.getNbt()))).apply(inst, (ingredient, mob, result) -> new TrapRecipe(ingredient, mob.entity(), result.entity(), mob.nbt(), result.nbt())));
    static final StreamCodec<RegistryFriendlyByteBuf, TrapRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, TrapRecipe::getInput,
        Identifier.STREAM_CODEC, r -> r.inputEntity.getEntityId(),
        ByteBufCodecs.COMPOUND_TAG, r -> r.inputEntity.getNbt(),
        Identifier.STREAM_CODEC, r -> r.outputEntity.getEntityId(),
        ByteBufCodecs.COMPOUND_TAG, r -> r.outputEntity.getNbt(),
        (ing, inId, inNbt, outId, outNbt) -> new TrapRecipe(ing, inId, outId, inNbt, outNbt));
  }

  public boolean matches(Level level, ItemStack dyeFound, Entity entity) {
    var trapped = BuiltInRegistries.ENTITY_TYPE.getValue(inputEntity.getEntityId());
    boolean matches = (trapped == entity.getType() && this.getInput().test(dyeFound));
  //  VeinCreeperMod.LOGGER.debug("[trap] check recipe={} trappedType={} entityType={} typeMatch={} itemMatch={}",
//        inputEntity.getEntityId(), trapped, entity.getType(), trapped == entity.getType(), this.getInput().test(dyeFound));
    if (matches && !this.inputEntity.getNbt().isEmpty()) {
      boolean tagMatch = false;
      TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
      entity.saveWithoutId(output);
      CompoundTag entityData = output.buildResult();
      var inputTags = this.inputEntity.getNbt();
      for (var entry : inputTags.entrySet()) {
        String key = entry.getKey();
        if (entry.getValue() instanceof IntTag) {
          tagMatch = (inputTags.getIntOr(key, 0) == entityData.getIntOr(key, 0));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED integer tagmatch from recipe " + entityData + "!!" + inputTags);
          }
        }
        else if (entry.getValue() instanceof ByteTag) {
          tagMatch = (inputTags.getBooleanOr(key, false) == entityData.getBooleanOr(key, false));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED boolean tagmatch from recipe " + inputTags);
          }
        }
        else if (entry.getValue() instanceof StringTag) {
          tagMatch = (inputTags.getStringOr(key, "").equalsIgnoreCase(entityData.getStringOr(key, "")));
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
    var entityFromRecipe = BuiltInRegistries.ENTITY_TYPE.getValue(this.outputEntity.getEntityId());
    if (entityFromRecipe == null) {
      VeinCreeperMod.LOGGER.error("Recipe spawn failed, entity not registered " + entityFromRecipe);
      return;
    }
    if (this.inputEntity.getEntityId().equals(this.outputEntity.getEntityId())) {
      // same entity, keep
    }
    else {
      if (entityToKill instanceof Player == false) {
        VeinCreeperMod.LOGGER.debug("kill and remove enitty" + entityToKill);
        entityToKill.remove(RemovalReason.KILLED);
      }
      VeinCreeperMod.LOGGER.debug("spawn New entity from type  " + entityFromRecipe);
      entityToKill = entityFromRecipe.spawn(level, pos, EntitySpawnReason.CONVERSION);
    }
    var outputTags = this.outputEntity.getNbt();
    if (!outputTags.isEmpty() && entityToKill != null) {
      TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
      entityToKill.saveWithoutId(output);
      CompoundTag entityData = output.buildResult();
      for (var entry : outputTags.entrySet()) {
        String key = entry.getKey();
        if (entry.getValue() instanceof IntTag) {
          entityData.putInt(key, outputTags.getIntOr(key, 0));
        }
        else if (entry.getValue() instanceof ByteTag) {
          entityData.putBoolean(key, outputTags.getBooleanOr(key, false));
        }
        else if (entry.getValue() instanceof StringTag) {
          entityData.putString(key, outputTags.getStringOr(key, ""));
        }
        else {
          VeinCreeperMod.LOGGER.error("NBT unsupported type, more may come in future versions" + entry.getValue());
        }
      }
      ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), entityData);
      entityToKill.load(input);
    }
  }

  public Ingredient getInput() {
    return input;
  }
}
