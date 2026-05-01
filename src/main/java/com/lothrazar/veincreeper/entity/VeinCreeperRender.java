package com.lothrazar.veincreeper.entity;

import com.lothrazar.veincreeper.VeinCreeperMod;
import com.lothrazar.veincreeper.config.VeinCreeperData;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

@SuppressWarnings("rawtypes")
public class VeinCreeperRender extends CreeperRenderer {

  public static boolean doRefresh = false;

  public VeinCreeperRender(EntityRendererProvider.Context ctx) {
    super(ctx);
    this.model = new VeinCreeperModel<>(ctx.bakeLayer(ModelLayers.CREEPER));
  }

  private static final ResourceLocation NEW_CREEPER = ResourceLocation.fromNamespaceAndPath(VeinCreeperMod.MODID,
      "textures/entity/creeper.png");

  @Override
  public ResourceLocation getTextureLocation(Creeper entity) {
    var cm = (VeinCreeperModel) this.model;
    if (cm.getColor() == null
        || doRefresh) {
      final String key = VeinCreeperData.getKeyFromEntity(entity);
      var col = VeinCreeperData.getCreeperColor(key);
      cm.setColor(new int[] { col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha() });
    }
    return NEW_CREEPER;
  }
}
