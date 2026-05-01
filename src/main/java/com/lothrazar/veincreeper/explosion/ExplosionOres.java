package com.lothrazar.veincreeper.explosion;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.config.CreeperConfigManager;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ExplosionOres extends Explosion {

  private final RandomSource random = RandomSource.create();
  private final Level level;
  private final boolean fire;
  private final float radius;
  private final double x;
  private final double y;
   private final double z;
// old     super(level, entity, src, calc, x, y, z, radius, f, bi);
  public ExplosionOres(Level level, Entity source, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction bi) {
    super(level, source, x, y, z, radius, fire, bi);
    this.level = level;
    this.radius = radius;
    this.x = x;
    this.y = y;
    this.z = z;
    this.fire = fire;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    return z;
  }

  @Override
  public void finalizeExplosion(boolean p_46076_) {
    boolean flag = this.interactsWithBlocks();
    if (p_46076_) {
      if (!(this.radius < 2.0F) && flag) {
        this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      }
      else {
        this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      }
    }
    Map<BlockPos, BlockState> toReplace = new HashMap<>();
    if (flag) {
      ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
      Util.shuffle(this.toBlow, this.level.random);
      for (BlockPos blockpos : this.toBlow) {
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir()) {
          BlockPos blockpos1 = blockpos.immutable();
          //overrides
          final String key = CreeperConfigManager.getKeyFromEntity(this.getDirectSourceEntity());
          if (!CreeperRegistry.CREEPERS.containsKey(key)) {
            VeinCreeperMod.LOGGER.error("Missing type from explosion " + key);
            return;
          }
          var type = CreeperRegistry.CREEPERS.get(key);
          //itsa valid entity, so NOW check recipe
          boolean recipeFound = false;
          for (RecipeHolder<ExplosionRecipe> holder : level.getRecipeManager().getAllRecipesFor(CreeperRegistry.EXPLOSION_RECIPE.get())) {
            ExplosionRecipe recipe = holder.value();
            if (!recipe.matches(this.getDirectSourceEntity(), blockstate)) {
              continue;
            }
            recipeFound = true;
            if (recipe.hasBonus()
                && (recipe.getBonus().getChance() / 100F) > level.random.nextDouble()) {
              toReplace.put(blockpos1, recipe.getBonus().getBlock().defaultBlockState());
              VeinCreeperMod.LOGGER.info("Explosion recipe applied BONUS " + holder.id());
            }
            else {
              toReplace.put(blockpos1, recipe.getOre().getBlock().defaultBlockState());
              VeinCreeperMod.LOGGER.info("Explosion recipe applied to world " + holder.id());
            }
            break;
          }
          if (!recipeFound && type.isDestructive()) {
            if (this.level instanceof ServerLevel serverlevel) {
              blockstate.onExplosionHit(serverlevel, blockpos, this, (drop, dropPos) -> {
                addBlockDropsLocal(objectarraylist, drop, dropPos);
              });
            }
          }
        }
      }
      for (Pair<ItemStack, BlockPos> pair : objectarraylist) {
        Block.popResource(this.level, pair.getSecond(), pair.getFirst());
      }
    }
    if (this.fire) {
      for (BlockPos blockpos2 : this.toBlow) {
        if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
          this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
        }
      }
    }
    for (BlockPos rep : toReplace.keySet()) {
      this.level.setBlockAndUpdate(rep, toReplace.get(rep));
    }
  }

  private static void addBlockDropsLocal(ObjectArrayList<Pair<ItemStack, BlockPos>> drops, ItemStack stack, BlockPos pos) {
    for (int i = 0; i < drops.size(); i++) {
      Pair<ItemStack, BlockPos> pair = drops.get(i);
      ItemStack existing = pair.getFirst();
      if (ItemEntity.areMergable(existing, stack)) {
        ItemStack merged = ItemEntity.merge(existing, stack, 16);
        drops.set(i, Pair.of(merged, pair.getSecond()));
        if (stack.isEmpty()) {
          return;
        }
      }
    }
    drops.add(Pair.of(stack, pos));
  }
}
