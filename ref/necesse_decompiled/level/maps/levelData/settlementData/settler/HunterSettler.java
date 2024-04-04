package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.friendly.human.humanShop.HunterHumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class HunterSettler extends Settler {
   public static ArrayList<String> spawnBiomes = new ArrayList(Arrays.asList("forest", "plains", "desert", "swamp", "snow"));
   public static double spawnChance = GameMath.getAverageSuccessRuns(120.0);

   public HunterSettler() {
      super("hunterhuman");
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return super.isAvailableForClient(var1, var2) && var2.biomes_visited.getTotalBiomes() >= 10;
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "huntertip");
   }

   public double getSpawnChance(Server var1, ServerClient var2, Level var3) {
      return !var3.isCave && spawnBiomes.contains(var3.biome.getStringID()) ? spawnChance : 0.0;
   }

   public void spawnAtClient(Server var1, ServerClient var2, Level var3) {
      if (!var3.entityManager.mobs.streamInRegionsInTileRange(var2.playerMob.getX() / 32, var2.playerMob.getY() / 32, SETTLER_SPAWN_AREA.maxSpawnDistance * 2).anyMatch((var1x) -> {
         return var1x.getStringID().equals(this.mobStringID);
      })) {
         HunterHumanMob var4 = (HunterHumanMob)MobRegistry.getMob(this.mobStringID, var3);
         var4.setLost(true);
         Point var5 = this.getSpawnLocation(var2, var3, var4, SETTLER_SPAWN_AREA);
         if (var5 != null) {
            var3.entityManager.addMob(var4, (float)var5.x, (float)var5.y);
         }

      }
   }
}
