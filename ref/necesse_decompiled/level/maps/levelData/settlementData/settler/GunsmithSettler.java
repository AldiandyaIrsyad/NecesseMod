package necesse.level.maps.levelData.settlementData.settler;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class GunsmithSettler extends Settler {
   public GunsmithSettler() {
      super("gunsmithhuman");
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return super.isAvailableForClient(var1, var2) && var2.mob_kills.getKills("evilsprotector") > 0;
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "foundinvillagetip");
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
      if ((var2 || !this.doesSettlementHaveThisSettler(var1)) && var1.hasCompletedQuestTier("evilsprotector")) {
         var3.addObject(75, this.getNewRecruitMob(var1));
      }

   }
}
