package com.lothrazar.veincreeper.entity;

import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.config.VeinCreeperData;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.monster.Creeper;

// Model#renderToBuffer is final now, so the old "subclass the model to inject a custom tint color"
// pattern is gone. LivingEntityRenderer#getModelTint(state) is the modern, first-party hook for exactly
// this (it multiplies into the tintedColor passed to submitModel) - the color has to be read off the
// render state rather than the live entity, since extractRenderState/getModelTint run at different times.
public class VeinCreeperRender extends CreeperRenderer {

  public VeinCreeperRender(EntityRendererProvider.Context ctx) {
    super(ctx);
  }

  private static final Identifier NEW_CREEPER = Identifier.fromNamespaceAndPath(VeinCreeperMod.MODID, "textures/entity/creeper.png");

  @Override
  public VeinCreeperRenderState createRenderState() {
    return new VeinCreeperRenderState();
  }

  @Override
  public void extractRenderState(Creeper entity, CreeperRenderState state, float partialTicks) {
    super.extractRenderState(entity, state, partialTicks);
    if (state instanceof VeinCreeperRenderState vcState) {
      final String key = VeinCreeperData.getKeyFromEntity(entity);
      var col = VeinCreeperData.getCreeperColor(key);
      vcState.tintColor = ARGB.color(col.getAlpha(), col.getRed(), col.getGreen(), col.getBlue());
    }
  }

  @Override
  protected int getModelTint(CreeperRenderState state) {
    return state instanceof VeinCreeperRenderState vcState ? vcState.tintColor : super.getModelTint(state);
  }

  @Override
  public Identifier getTextureLocation(CreeperRenderState state) {
    return NEW_CREEPER;
  }
}
