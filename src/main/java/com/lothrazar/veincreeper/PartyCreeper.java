package com.lothrazar.veincreeper;
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
  public void explodeCreeper() {

    if (!this.level().isClientSide) {
      float  radius = this.isPowered() ? 2.0F : 1.0F;
      this.dead = true;

      ExplosionParty explosion = new ExplosionParty(this.level(), this, (DamageSource)null, (ExplosionDamageCalculator)null, this.getX(), this.getY(), this.getZ(), radius,true, Explosion.BlockInteraction.KEEP);
      if (!net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.level(), explosion)) {

        explosion.explode();
        explosion.finalizeExplosion(true);
      }
      this.discard();
      this.spawnLingeringCloud();

    }

  }
}
