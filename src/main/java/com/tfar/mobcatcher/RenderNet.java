package com.tfar.mobcatcher;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.util.ResourceLocation;

public class RenderNet<T extends ProjectileItemEntity> extends SpriteRenderer<T> {

  private final ResourceLocation entityTexture;

  public RenderNet(EntityRendererManager render, ItemRenderer itemRendererIn, ResourceLocation entityTexture) {
    super(render, itemRendererIn);
    this.entityTexture = entityTexture;
  }
}
