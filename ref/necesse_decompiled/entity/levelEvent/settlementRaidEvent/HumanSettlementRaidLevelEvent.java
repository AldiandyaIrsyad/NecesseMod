package necesse.entity.levelEvent.settlementRaidEvent;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.RaiderMob;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;

public class HumanSettlementRaidLevelEvent extends SettlementRaidLevelEvent {
   public static MobSpawnTable spawnTable = (new MobSpawnTable()).add(100, (MobSpawnTable.MobProducer)((var0, var1, var2) -> {
      SettlementLevelData var3 = SettlementLevelData.getSettlementData(var0);
      RaiderMob var4 = null;
      int var5;
      SettlementQuestTier var6;
      if (var3 != null) {
         for(var5 = var3.getQuestTiersCompleted(); var5 >= 0; --var5) {
            var6 = SettlementQuestTier.getTier(var5);
            if (var6 != null) {
               var4 = var6.getRandomRaider(var0, var2);
               if (var4 != null) {
                  break;
               }
            }
         }
      } else {
         for(var5 = 0; var5 < SettlementQuestTier.questTiers.size(); ++var5) {
            var6 = (SettlementQuestTier)SettlementQuestTier.questTiers.get(var5);
            var4 = var6.getRandomRaider(var0, var2);
            if (var4 != null) {
               break;
            }
         }
      }

      return (Mob)var4;
   }));

   public HumanSettlementRaidLevelEvent() {
      super(120, 60, 10, 300);
   }

   public GameMessage getApproachMessage(GameMessage var1, boolean var2) {
      return new LocalMessage("misc", "humanraidapproaching", new Object[]{"settlement", var1, "direction", this.direction.displayName});
   }

   public GameMessage getPreparingMessage(GameMessage var1) {
      return new LocalMessage("misc", "humanraidpreparing", "settlement", var1);
   }

   public GameMessage getStartMessage(GameMessage var1) {
      return new LocalMessage("misc", "humanraidattacking", "settlement", var1);
   }

   public GameMessage getDefeatedMessage() {
      return new LocalMessage("misc", "humanraiddefeated");
   }

   public GameMessage getLeavingMessage() {
      return new LocalMessage("misc", "humanraidended");
   }

   public MobSpawnTable getSpawnTable() {
      return spawnTable;
   }

   public float getSpawnWaves() {
      return 6.0F;
   }

   public float getSpawnsPerWave() {
      int var1 = 1 + this.getCurrentSettlers() + Math.max(this.level.presentPlayers - 1, 0);
      double var2 = (Math.pow((double)var1 / 5.0, 0.35) * 15.0 - 12.0) / 2.0;
      if (var2 > 10.0) {
         var2 = 10.0;
      }

      return (float)var2 * 6.0F;
   }
}
