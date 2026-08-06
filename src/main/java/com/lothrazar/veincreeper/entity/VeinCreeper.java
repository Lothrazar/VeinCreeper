package com.lothrazar.veincreeper.entity;

import com.lothrazar.library.util.SoundUtil;
import com.lothrazar.veincreeper.config.VeinCreeperData;
import com.lothrazar.veincreeper.config.VeinCreeperType;
import com.lothrazar.veincreeper.explosion.ExplosionOres;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.neoforge.event.EventHooks;

public class VeinCreeper extends Creeper {

  private VeinCreeperType creeperType;

  public VeinCreeper(EntityType<VeinCreeper> t, Level level) {
    super(t, level);
    this.creeperType = VeinCreeperData.getCreepType(t);
  }

  @Override
  public boolean shouldDropExperience() {
    return creeperType.shouldDropExperience();
  }

  @Override
  public Component getDisplayName() {
    return this.creeperType.getDisplayName();
  }

  @Override
  public void explodeCreeper() {
    if (!(this.level() instanceof ServerLevel level)) {
      return;
    }
    final float radius = creeperType.getRadius();
    final boolean fire = creeperType.doesFire();
    this.dead = true;
    var bi = level.getGameRules().get(GameRules.MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
    // instead of this.level().explode(this,...) we instead create our own custom explosion
    ExplosionOres explosion = new ExplosionOres(level, this, this.getX(), this.getY(), this.getZ(), radius, fire, bi);
    if (!EventHooks.onExplosionStart(level, explosion)) { // returns true if expl cancelled
      explosion.explode();
      level.addParticle(ParticleTypes.EXPLOSION_EMITTER, explosion.x(), explosion.y(), explosion.z(), 1.0D, 0.0D, 0.0D);
      //sound
      SoundUtil.playSoundFromServer(level, this.blockPosition(), SoundEvents.GENERIC_EXPLODE.value());
    }
    //end of level.explode mirror
    this.discard();
    this.spawnLingeringCloud();
  }
}
