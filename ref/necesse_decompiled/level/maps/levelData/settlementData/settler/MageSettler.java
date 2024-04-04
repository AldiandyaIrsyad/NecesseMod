package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class MageSettler extends Settler {
   public static double spawnChance = GameMath.getAverageSuccessRuns(120.0);

   public MageSettler() {
      super("magehuman");
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return super.isAvailableForClient(var1, var2) && var2.biomes_visited.getBiomesKnown(BiomeRegistry.getBiome("dungeon")) > 0 && var2.mob_kills.getKills("voidwizard") > 0;
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "magetip");
   }

   public double getSpawnChance(Server var1, ServerClient var2, Level var3) {
      return var3.getIslandDimension() == -100 ? spawnChance : 0.0;
   }

   public void spawnAtClient(Server var1, ServerClient var2, Level var3) {
      if (!var3.entityManager.mobs.stream().anyMatch((var1x) -> {
         return var1x.getStringID().equals(this.mobStringID);
      })) {
         MageHumanMob var4 = (MageHumanMob)MobRegistry.getMob(this.mobStringID, var3);
         var4.setTrapped(true);
         Point var5 = this.getSpawnLocation(var2, var3, var4, SETTLER_SPAWN_AREA);
         if (var5 != null) {
            var3.entityManager.addMob(var4, (float)var5.x, (float)var5.y);
         }

      }
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
      if ((var2 || !this.doesSettlementHaveThisSettler(var1)) && var1.hasCompletedQuestTier("voidwizard")) {
         var3.addObject(100, this.getNewRecruitMob(var1));
      }

   }
}
