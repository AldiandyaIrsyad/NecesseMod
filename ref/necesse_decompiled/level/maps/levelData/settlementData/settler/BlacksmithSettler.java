package necesse.level.maps.levelData.settlementData.settler;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class BlacksmithSettler extends Settler {
   public BlacksmithSettler() {
      super("blacksmithhuman");
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return super.isAvailableForClient(var1, var2) && var2.ladders_used.get() > 0 && (var2.items_obtained.isItemObtained("ironbar") || var2.items_obtained.isItemObtained("copperbar") || var2.items_obtained.isItemObtained("goldbar"));
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "foundinvillagetip");
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
      if (var2 || !this.doesSettlementHaveThisSettler(var1)) {
         var3.addObject(75, this.getNewRecruitMob(var1));
      }

   }
}
