package com.lothrazar.veincreeper.config;


public record VeinCreeperDTO(
  String id,
  String colour, // hex string, cannot map DTO to awt.Color;
  String displayName, //for display name only
  boolean dropsExp,
  boolean destructive,
  float radius, // > 0
  boolean fire,
  boolean spawnEgg
) {}
