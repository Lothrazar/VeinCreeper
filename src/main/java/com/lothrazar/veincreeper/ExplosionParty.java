package com.lothrazar.veincreeper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ExplosionParty extends Explosion {
  public ExplosionParty(Level level,Entity entity,   DamageSource src,   ExplosionDamageCalculator calc, double x, double y, double z, float size, boolean f, Explosion.BlockInteraction bi) {
  super(level,entity,src,calc,x,y,z,size,f,bi);
  }
  }
