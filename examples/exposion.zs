



var explosions = <recipetype:veincreeper:explosion>;

// remove the recipe where diamond creepers upgrade smooth stone
// with this, diamond creepers will still convert deepslate since thats a different recipe 

explosions.removeRecipe("veincreeper:explosion/diamond_stone");
explosions.removeRecipe("veincreeper:explosion/emerald_deepslate"); 

// make coal creepers replace dirt

explosions.addRecipe("dirt_custom", "coal_creeper", "minecraft:dirt", "minecraft:coal_ore");


