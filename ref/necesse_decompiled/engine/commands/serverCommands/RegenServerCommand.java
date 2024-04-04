package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BiomeParameterHandler;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;

public class RegenServerCommand extends ModularChatCommand {
   public RegenServerCommand() {
      super("regen", "Regenerates the entire level", PermissionLevel.OWNER, true, new CmdParameter("islandX", new IntParameterHandler(Integer.MIN_VALUE), true, new CmdParameter[]{new CmdParameter("islandY", new IntParameterHandler()), new CmdParameter("dimension", new IntParameterHandler())}), new CmdParameter("biome", new BiomeParameterHandler(), true, new CmdParameter[0]), new CmdParameter("seeded", new BoolParameterHandler(true), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      LevelIdentifier var7 = new LevelIdentifier((Integer)var4[0], (Integer)var4[1], (Integer)var4[2]);
      Biome var8 = (Biome)var4[3];
      boolean var9 = (Boolean)var4[4];
      if (var7.getIslandX() == Integer.MIN_VALUE) {
         if (var3 == null) {
            var6.add("Please specify island coordinates and dimension");
            return;
         }

         var7 = var3.getLevelIdentifier();
         if (!var7.isIslandPosition()) {
            var6.add("Could not find island coordinates for your current position");
            return;
         }
      }

      if (var8 != null) {
         int var10 = var7.getIslandX();
         int var11 = var7.getIslandY();
         if (!var9) {
            var10 = GameRandom.globalRandom.nextInt();
            var11 = GameRandom.globalRandom.nextInt();
         }

         Level var12 = var8.getNewLevel(var10, var11, var7.getIslandDimension(), var2, var2.world.worldEntity);
         var12.makeServerLevel(var2);
         var12.biome = var8;
         var12.overwriteIdentifier(var7);
         var2.world.levelManager.overwriteLevel(var12);
      } else if (var9) {
         var2.world.generateNewLevel(var7.getIslandX(), var7.getIslandY(), var7.getIslandDimension());
      } else {
         Biome var13 = var2.levelCache.getBiome(var7.getIslandX(), var7.getIslandY());
         Level var14 = var13.getNewLevel(GameRandom.globalRandom.nextInt(), GameRandom.globalRandom.nextInt(), var7.getIslandDimension(), var2, var2.world.worldEntity);
         var14.makeServerLevel(var2);
         var14.biome = var13;
         var14.overwriteIdentifier(var7);
         var2.world.levelManager.overwriteLevel(var14);
      }

      var2.streamClients().filter((var1x) -> {
         return var1x.isSamePlace(var7);
      }).forEach((var2x) -> {
         var2x.reset();
         var2.network.sendPacket(new PacketPlayerLevelChange(var2x.slot, var7, true), (ServerClient)var2x);
      });
      var6.add("Regenerated level " + var7 + (var8 == null ? "" : " with biome " + var8.getStringID()) + (var9 ? "" : " (not seeded)") + ".");
   }
}
