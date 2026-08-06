package com.lothrazar.veincreeper.explosion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.config.VeinCreeperData;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

// Explosion is an interface now; the real block-selection + per-block-destroy algorithm lives on
// ServerExplosion, which we extend directly. Its block-selection algorithm (calculateExplodedPositions)
// and entity-damage pass (hurtEntities) are reused unmodified from the vanilla explode() call - only
// interactWithBlocks (the per-block ore-replace-vs-vanilla-destroy dispatch) is overridden, via an
// access transformer widening it from private to protected (see accesstransformer.cfg).
public class ExplosionOres extends ServerExplosion {

  private final double x;
  private final double y;
  private final double z;

  public ExplosionOres(ServerLevel level, Entity source, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction bi) {
    super(level, source, null, null, new Vec3(x, y, z), radius, fire, bi);
    this.x = x;
    this.y = y;
    this.z = z;
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
  protected void interactWithBlocks(List<BlockPos> targetBlocks) {
    ServerLevel level = this.level();
    Entity exploder = this.getDirectSourceEntity();
    final String key = VeinCreeperData.getKeyFromEntity(exploder);
    if (!VeinCreeperData.CREEPERS.containsKey(key)) {
      VeinCreeperMod.LOGGER.error("Missing type from explosion " + key);
      return;
    }
    var type = VeinCreeperData.CREEPERS.get(key);

    Map<BlockPos, BlockState> toReplace = new HashMap<>();
    List<BlockPos> vanillaBlocks = new ArrayList<>();
    for (BlockPos blockpos : targetBlocks) {
      BlockState blockstate = level.getBlockState(blockpos);
      if (blockstate.isAir()) {
        continue;
      }
      BlockPos blockpos1 = blockpos.immutable();
      boolean recipeFound = false;
      for (RecipeHolder<ExplosionRecipe> holder : level.recipeAccess().recipeMap().byType(CreeperRegistry.EXPLOSION_RECIPE.get())) {
        ExplosionRecipe recipe = holder.value();
        if (!recipe.matches(exploder, blockstate)) {
          continue;
        }
        recipeFound = true;
        if (recipe.hasBonus() && (recipe.getBonus().getChance() / 100F) > level.getRandom().nextDouble()) {
          toReplace.put(blockpos1, recipe.getBonus().getBlock().defaultBlockState());
          VeinCreeperMod.LOGGER.debug("Explosion recipe applied BONUS " + holder.id() + " at " + blockpos1);
        }
        else {
          toReplace.put(blockpos1, recipe.getOre().getBlock().defaultBlockState());
          VeinCreeperMod.LOGGER.debug("Explosion recipe applied to world " + holder.id() + " at " + blockpos1);
        }
        break;
      }
      if (!recipeFound && type.isDestructive()) {
        vanillaBlocks.add(blockpos);
      }
    }
    if (!vanillaBlocks.isEmpty()) {
      super.interactWithBlocks(vanillaBlocks);
    }
    for (var entry : toReplace.entrySet()) {
      level.setBlockAndUpdate(entry.getKey(), entry.getValue());
    }
  }
}
