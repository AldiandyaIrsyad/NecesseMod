package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import java.util.Iterator;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ExpeditionMission extends RunOutMission {
   private SettlerExpedition expedition;
   private boolean success;
   private int ticks;
   private int completeTicks;

   public ExpeditionMission() {
   }

   public ExpeditionMission(SettlerExpedition var1, float var2) {
      this.expedition = var1;
      this.success = GameRandom.globalRandom.getChance(var2);
      this.completeTicks = var1.getTicksToComplete();
      if (!this.success) {
         this.completeTicks /= 2;
      }

   }

   public boolean canStart(HumanMob var1) {
      return true;
   }

   public void start(HumanMob var1) {
      super.start(var1);
      var1.completedMission = false;
      this.ticks = 0;
   }

   public void addSaveData(HumanMob var1, SaveData var2) {
      super.addSaveData(var1, var2);
      if (this.expedition != null) {
         var2.addUnsafeString("expedition", this.expedition.getStringID());
      }

      var2.addBoolean("success", this.success);
      var2.addInt("ticks", this.ticks);
      var2.addInt("completeTicks", this.completeTicks);
   }

   public void applySaveData(HumanMob var1, LoadData var2) {
      super.applySaveData(var1, var2);
      String var3 = var2.getUnsafeString("expedition", (String)null, false);
      if (var3 != null) {
         this.expedition = ExpeditionMissionRegistry.getExpedition(var3);
      }

      this.success = var2.getBoolean("success", this.success, false);
      this.ticks = var2.getInt("ticks", this.ticks, false);
      this.completeTicks = var2.getInt("completeTicks", this.completeTicks, false);
   }

   public void serverTick(HumanMob var1) {
      if (this.expedition == null) {
         this.markOver();
      } else {
         if (this.isOut) {
            ++this.ticks;
            if (this.ticks >= this.completeTicks) {
               this.endMission(var1);
            }
         }

      }
   }

   private void endMission(HumanMob var1) {
      if (!this.isOver()) {
         this.markOver();
         var1.completedMission = true;
         SettlementLevelData var2 = var1.getSettlementLevelData();
         if (var2 != null) {
            var1.missionFailed = !this.success;
            if (var1.missionFailed) {
               var1.missionFailedMessage = new LocalMessage("ui", "explorerexpfail");
            } else {
               Iterator var3 = this.expedition.getRewardItems(var2, var1).iterator();

               while(var3.hasNext()) {
                  InventoryItem var4 = (InventoryItem)var3.next();
                  var1.getWorkInventory().add(var4);
               }

               var1.getWorkInventory().markDirty();
            }
         } else {
            var1.missionFailed = true;
         }

         if (var1.home != null) {
            var1.moveIn(var1.home.x, var1.home.y, true);
         }

         var1.sendMovementPacket(true);
         var1.getLevel().settlementLayer.streamTeamMembers().forEach((var1x) -> {
            var1x.sendChatMessage((GameMessage)(new LocalMessage("ui", "settlerreturning", new Object[]{"settler", var1.getLocalization(), "settlement", var1.getLevel().settlementLayer.getSettlementName()})));
         });
      }
   }
}
