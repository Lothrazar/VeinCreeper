package com.lothrazar.veincreeper.recipe;

import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class TrapRecipe implements Recipe<RecipeInput> {

  private final Ingredient input;
  public final EntityIngredient inputEntity;
  public final EntityIngredient outputEntity;

  public TrapRecipe(Ingredient ing, ResourceLocation entityType, ResourceLocation entityOut, CompoundTag tag, CompoundTag tago) {
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
  public ItemStack assemble(RecipeInput c, HolderLookup.Provider provider) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canCraftInDimensions(int x, int y) {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider provider) {
    return ItemStack.EMPTY;
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
    return "TrapRecipe [input=" + getInput() + ", inputEntity=" + inputEntity + ", outputEntity=" + outputEntity + "]";
  }

  private record MobData(ResourceLocation entity, CompoundTag nbt) {

    static final MapCodec<MobData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        ResourceLocation.CODEC.fieldOf("entity").forGetter(MobData::entity),
        CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(MobData::nbt)).apply(inst, MobData::new));
  }

  public static class SerializeTrapRecipe implements RecipeSerializer<TrapRecipe> {

    public static final MapCodec<TrapRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(TrapRecipe::getInput),
        MobData.CODEC.fieldOf("mob").forGetter(r -> new MobData(r.inputEntity.getEntityId(), r.inputEntity.getNbt())),
        MobData.CODEC.fieldOf("result").forGetter(r -> new MobData(r.outputEntity.getEntityId(), r.outputEntity.getNbt()))).apply(inst, (ingredient, mob, result) -> new TrapRecipe(ingredient, mob.entity(), result.entity(), mob.nbt(), result.nbt())));
    public static final StreamCodec<RegistryFriendlyByteBuf, TrapRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, TrapRecipe::getInput,
        ResourceLocation.STREAM_CODEC, r -> r.inputEntity.getEntityId(),
        ByteBufCodecs.COMPOUND_TAG, r -> r.inputEntity.getNbt(),
        ResourceLocation.STREAM_CODEC, r -> r.outputEntity.getEntityId(),
        ByteBufCodecs.COMPOUND_TAG, r -> r.outputEntity.getNbt(),
        (ing, inId, inNbt, outId, outNbt) -> new TrapRecipe(ing, inId, outId, inNbt, outNbt));

    public SerializeTrapRecipe() {}

    @Override
    public MapCodec<TrapRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TrapRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }

  public boolean matches(Level level, ItemStack dyeFound, Entity entity) {
    var trapped = BuiltInRegistries.ENTITY_TYPE.get(inputEntity.getEntityId());
    boolean matches = (trapped == entity.getType() && this.getInput().test(dyeFound));
    if (matches && !this.inputEntity.getNbt().isEmpty()) {
      boolean tagMatch = false;
      CompoundTag entityData = new CompoundTag();
      entity.saveWithoutId(entityData);
      var inputTags = this.inputEntity.getNbt();
      for (String key : inputTags.getAllKeys()) {
        if (inputTags.getTagType(key) == Tag.TAG_INT) {
          tagMatch = (inputTags.getInt(key) == entityData.getInt(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED integer tagmatch from recipe " + entityData + "!!" + inputTags);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_SHORT) {
          tagMatch = (inputTags.getShort(key) == entityData.getShort(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED getShort tagmatch from recipe " + inputTags);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_BYTE) {
          tagMatch = (inputTags.getBoolean(key) == entityData.getBoolean(key));
          matches = matches && tagMatch;
          if (!tagMatch) {
            VeinCreeperMod.LOGGER.info("FAILED boolean tagmatch from recipe " + inputTags);
          }
        }
        if (inputTags.getTagType(key) == Tag.TAG_STRING) {
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
    var entityFromRecipe = BuiltInRegistries.ENTITY_TYPE.get(this.outputEntity.getEntityId());
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
      entityToKill = entityFromRecipe.spawn(level, pos, MobSpawnType.CONVERSION);
    }
    var inputTags = this.inputEntity.getNbt();
    var outputTags = this.outputEntity.getNbt();
    if (!outputTags.isEmpty() && entityToKill != null) {
      CompoundTag entityData = new CompoundTag();
      entityToKill.save(entityData);
      for (String key : outputTags.getAllKeys()) {
        if (inputTags.getTagType(key) == Tag.TAG_INT) {
          entityData.putInt(key, outputTags.getInt(key));
        }
        else if (inputTags.getTagType(key) == Tag.TAG_BYTE) {
          entityData.putBoolean(key, outputTags.getBoolean(key));
        }
        else if (inputTags.getTagType(key) == Tag.TAG_STRING) {
          entityData.putString(key, outputTags.getString(key));
        }
        else {
          VeinCreeperMod.LOGGER.error("NBT unsupported type, more may come in future versions" + inputTags.getTagType(key));
        }
      }
      entityToKill.load(entityData);
    }
  }

  public Ingredient getInput() {
    return input;
  }
}
