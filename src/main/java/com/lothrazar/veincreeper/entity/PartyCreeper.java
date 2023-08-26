package com.lothrazar.veincreeper.entity;

import com.lothrazar.veincreeper.ConfigManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class PartyCreeper extends Creeper {

  public PartyCreeper(EntityType<PartyCreeper> t, Level level) {
    super(t, level);
  }

  @Override
  public Component getDisplayName() {
    return ConfigManager.getDisplayName(this);
    //    return super.getDisplayName();
  }

  @Override
  public void explodeCreeper() {
    if (!this.level().isClientSide) {
      float radius = this.isPowered() ? 2.0F : 1.0F + 1; // hardcoded large size
      this.dead = true;
      var interactionVal = this.level().getGameRules().getBoolean(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
      boolean fire = false;
      //start of level.explode
      ExplosionParty explosion = new ExplosionParty(this.level(), this, (DamageSource) null, (ExplosionDamageCalculator) null, this.getX(), this.getY(), this.getZ(), radius, fire,
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
