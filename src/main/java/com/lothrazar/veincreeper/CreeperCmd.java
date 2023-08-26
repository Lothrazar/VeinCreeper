package com.lothrazar.veincreeper;

import com.lothrazar.veincreeper.entity.PartyCreeperRender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreeperCmd {

  private static final int PERM_EVERYONE = 0; // no restrictions
  private static final int PERM_ELEVATED = 2; // player with perms/creative OR function OR command block

  @SubscribeEvent
  public void onRegisterCommandsEvent(RegisterCommandsEvent event) {
    CommandDispatcher<CommandSourceStack> r = event.getDispatcher();
    r.register(LiteralArgumentBuilder.<CommandSourceStack> literal(VeinCreeperMod.MODID)
        // cyclic home teleport @p
        // cyclic home reset @p
        // cyclic home save @p
        // cyclic home set @p x y z
        .then(Commands.literal("colortest")
            .requires((p) -> {
              return p.hasPermission(PERM_ELEVATED);
            })
            .then(Commands.argument("creeper", StringArgumentType.word())
                .then(Commands.argument("r", IntegerArgumentType.integer())
                    .then(Commands.argument("g", IntegerArgumentType.integer())
                        .then(Commands.argument("b", IntegerArgumentType.integer())
                            .executes(x -> {
                              return execute(x, StringArgumentType.getString(x, "creeper"), IntegerArgumentType.getInteger(x, "r"), IntegerArgumentType.getInteger(x, "g"), IntegerArgumentType.getInteger(x, "b"));
                            }))))))
    //
    );
  }

  private int execute(CommandContext<CommandSourceStack> x, String string, int r, int g, int b) {
    r = parseCol(r);
    g = parseCol(g);
    b = parseCol(b);
    for (var creeperId : PartyCreeperRegistry.CREEPERS.keySet()) {
      if (creeperId.equals(string)) {
        PartyCreeperRegistry.CREEPERS.get(creeperId).setColor(new int[] { r, g, b });
        PartyCreeperRender.doRefresh = true;//hakxor
        return 0;
      }
    }
    return 1;
  }

  private int parseCol(int r) {
    return Math.max(Math.min(r, 255), 0);
  }
}
