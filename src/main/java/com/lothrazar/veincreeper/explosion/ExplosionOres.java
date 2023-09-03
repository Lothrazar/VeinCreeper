package com.lothrazar.veincreeper.explosion;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.veincreeper.CreeperRegistry;
import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.conf.CreeperConfigManager;
import com.lothrazar.veincreeper.entity.VeinCreeper;
import com.lothrazar.veincreeper.recipe.ExplosionRecipe;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class ExplosionOres extends Explosion {

  private final RandomSource random = RandomSource.create();
  private final Level level;
  private final boolean fire;
  private final float radius;
  private final double x;
  private final double y;
  private final double z;
  private final Explosion.BlockInteraction blockInteraction;

  public ExplosionOres(Level level, Entity entity, DamageSource src, ExplosionDamageCalculator calc, double x, double y, double z, float radius, boolean f, Explosion.BlockInteraction bi) {
    super(level, entity, src, calc, x, y, z, radius, f, bi);
    this.level = level;
    this.radius = radius;
    this.x = x;
    this.y = y;
    this.z = z;
    this.fire = f;
    this.blockInteraction = bi;
  }

  @Override
  public void finalizeExplosion(boolean p_46076_) {
    if (this.level.isClientSide) {
      this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
    }
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
      boolean flag1 = this.getIndirectSourceEntity() instanceof Player;
      Util.shuffle(this.toBlow, this.level.random);
      for (BlockPos blockpos : this.toBlow) {
        BlockState blockstate = this.level.getBlockState(blockpos);
        if (!blockstate.isAir()) {
          BlockPos blockpos1 = blockpos.immutable();
          this.level.getProfiler().push("explosion_blocks");
          //overrides 
          final String key = CreeperConfigManager.getKeyFromEntity(this.getExploder());
          if (!CreeperRegistry.CREEPERS.containsKey(key)) {
            VeinCreeperMod.LOGGER.info("Missing type from explosion " + key);
            return;
          }
          var type = CreeperRegistry.CREEPERS.get(key);
          //itsa valid entity, so NOW check recipe
          boolean recipeFound = false;
          for (ExplosionRecipe recipe : level.getRecipeManager().getAllRecipesFor(CreeperRegistry.EXPLOSION_RECIPE.get())) {
            if (!recipe.matches(this.getExploder(), blockstate)) {
              continue;
            }
            recipeFound = true;
            //BONUS? or normal
            if (recipe.hasBonus()
                && (recipe.getBonus().getChance() / 100F) > level.random.nextDouble()) {
              //ok
              toReplace.put(blockpos1, recipe.getBonus().getBlock().defaultBlockState());
              VeinCreeperMod.LOGGER.info("Explosion recipe applied BONUS " + recipe.getId());
            }
            else {
              //default to always replace to non-bonus
              toReplace.put(blockpos1, recipe.getOre().getBlock().defaultBlockState());
              VeinCreeperMod.LOGGER.info("Explosion recipe applied to world " + recipe.getId());
            }
            break; // found a matching recipe for this block state, AND did a replacement
          }
          if (!recipeFound && this.getExploder() instanceof VeinCreeper) {
            VeinCreeperMod.LOGGER.error(blockstate + "No recipe found. Make sure to create your own recipes when creepers are added to the con: " + key);
            //            VeinCreeperMod.LOGGER.error(" __________  " + blockstate);
            //            VeinCreeperMod.LOGGER.error(" __________  " + key);
            VeinCreeperMod.LOGGER.error(" __________  " + blockpos);
          }
          //          else
          //            VeinCreeperMod.LOGGER.error("ERROR! no valid oreconfigs found for mob " + key);
          //
          if (!recipeFound && type.isDestructive() && blockstate.canDropFromExplosion(this.level, blockpos, this)) {
            //regular explosm stuff on non-converted blocks
            if (this.level instanceof ServerLevel serverlevel) {
              BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
              LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY,
                  this.getExploder());
              if (this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
              }
              blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1);
              blockstate.getDrops(lootparams$builder).forEach((p_46074_) -> {
                addBlockDrops(objectarraylist, p_46074_, blockpos1);
              });
            }
          }
          if (!recipeFound && type.isDestructive()) blockstate.onBlockExploded(this.level, blockpos, this);
          this.level.getProfiler().pop();
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
}
