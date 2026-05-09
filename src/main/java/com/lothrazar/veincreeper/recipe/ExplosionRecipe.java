package com.lothrazar.veincreeper.recipe;

import java.util.Optional;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.config.VeinCreeperData;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ExplosionRecipe implements Recipe<RecipeInput> {

  private TagKey<Block> replace = BlockTags.STONE_ORE_REPLACEABLES;
  private BlockIngredient oreOutput = null;
  private EntityIngredient entityType;
  private BlockIngredient bonus = null;

  public ExplosionRecipe(ResourceLocation entityType,
      TagKey<Block> blockReplace,
      Block result,
      Block bonus, Integer chance) {
    super();
    this.replace = blockReplace;
    this.oreOutput = new BlockIngredient(result);
    this.entityType = new EntityIngredient(entityType);
    this.bonus = new BlockIngredient(bonus, chance);
  }

  public ExplosionRecipe(ResourceLocation entityType, TagKey<Block> input, Block result) {
    this(entityType, input, result, null, null);
  }

  public BlockIngredient getOre() {
    return oreOutput;
  }

  @Override
  public boolean matches(RecipeInput c, Level level) {
    return false;
  }

  @Override
  public ItemStack assemble(RecipeInput c, HolderLookup.Provider provider) {
    return getResultItem(provider);
  }

  @Override
  public boolean canCraftInDimensions(int x, int y) {
    return true;
  }

  @Override
  public ItemStack getResultItem(HolderLookup.Provider provider) {
    return getResultItem();
  }

  public ItemStack getResultItem() {
    return new ItemStack(oreOutput.getBlock());
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

  private record OreData(Block ore, Optional<Block> bonus, Optional<Integer> bonusChance) {

    static final MapCodec<OreData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(OreData::ore),
        BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("bonus").forGetter(OreData::bonus),
        com.mojang.serialization.Codec.INT.optionalFieldOf("bonusChance").forGetter(OreData::bonusChance)).apply(inst, OreData::new));
  }

  private record TargetData(TagKey<Block> tag) {

    static final MapCodec<TargetData> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(TargetData::tag)).apply(inst, TargetData::new));
  }

  public static class SerializePartyRecipe implements RecipeSerializer<ExplosionRecipe> {

    public static final MapCodec<ExplosionRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        com.mojang.serialization.Codec.STRING.fieldOf("veincreeper").forGetter(r -> r.entityType.getEntityId().getPath()),
        TargetData.CODEC.fieldOf("target").forGetter(r -> new TargetData(r.replace)),
        OreData.CODEC.fieldOf("ore").forGetter(r -> new OreData(
            r.oreOutput.getBlock(),
            (r.bonus != null && r.bonus.getBlock() != null) ? Optional.of(r.bonus.getBlock()) : Optional.empty(),
            (r.bonus != null && r.bonus.getChance() != null) ? Optional.of(r.bonus.getChance()) : Optional.empty()))).apply(inst, (creeperId, target, ore) -> {
              ResourceLocation entity = ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID, creeperId);
              VeinCreeperMod.LOGGER.debug("loading explosion recipe for " + entity);
              if (ore.bonus().isPresent() && ore.bonusChance().isPresent()) {
                return new ExplosionRecipe(entity, target.tag(), ore.ore(), ore.bonus().get(), ore.bonusChance().get());
              }
              return new ExplosionRecipe(entity, target.tag(), ore.ore());
            }));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExplosionRecipe> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, r -> r.entityType.getEntityId(),
        ResourceLocation.STREAM_CODEC, r -> r.replace.location(),
        ByteBufCodecs.registry(Registries.BLOCK), r -> r.oreOutput.getBlock(),
        (entityId, tagLoc, block) -> new ExplosionRecipe(entityId, TagKey.create(Registries.BLOCK, tagLoc), block));

    public SerializePartyRecipe() {}

    @Override
    public MapCodec<ExplosionRecipe> codec() {
      return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ExplosionRecipe> streamCodec() {
      return STREAM_CODEC;
    }
  }

  public boolean matches(Entity exploder, BlockState blockstate) {
    final String key = VeinCreeperData.getKeyFromEntity(exploder);
    var src = entityType.getEntityId().getPath().toString();
    boolean match = src.equals(key) && blockstate.is(replace);
    return match;
  }

  public ResourceLocation getEntityType() {
    return this.entityType.getEntityId();
  }

  public boolean hasBonus() {
    return this.getBonus() != null && this.getBonus().getBlock() != null && this.getBonus().getChance() != null && this.getBonus().getChance() > 0;
  }
}
