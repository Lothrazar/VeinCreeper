package com.lothrazar.veincreeper.entity;

import com.lothrazar.veincreeper.conf.VeinCreeperType;
import com.lothrazar.veincreeper.conf.CreeperConfigManager;
import com.lothrazar.veincreeper.explosion.ExplosionOres;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class VeinCreeper extends Creeper {

  private VeinCreeperType creeperType;

  public VeinCreeper(EntityType<VeinCreeper> t, Level level) {
    super(t, level);
    this.creeperType = CreeperConfigManager.getCreepType(t);
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
    if (!this.level().isClientSide) {
      float radius = 2.8F; //this.isPowered() ? 2.0F : 1.0F + 1; //TODO config? hardcoded large size
      this.dead = true;
      var interactionVal = this.level().getGameRules().getBoolean(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
      boolean fire = false;
      //start of level.explode
      ExplosionOres explosion = new ExplosionOres(this.level(), this, (DamageSource) null, (ExplosionDamageCalculator) null, this.getX(), this.getY(), this.getZ(), radius, fire,
          interactionVal);
      if (!net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.level(), explosion)) { // returns true if cancelled
        explosion.explode();
        explosion.finalizeExplosion(true);
      }
      //end of level.explode
      this.discard();
      this.spawnLingeringCloud();
    }
  }
}
