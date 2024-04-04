package necesse.level.maps.levelData.settlementData.settler;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class GuardSettler extends Settler {
   public GuardSettler() {
      super("guardhuman");
   }

   public boolean isAvailableForClient(SettlementLevelData var1, PlayerStats var2) {
      return super.isAvailableForClient(var1, var2) && var2.damage_dealt.get() >= 10000;
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "foundinvillagetip");
   }

   public void addNewRecruitSettler(SettlementLevelData var1, boolean var2, TicketSystemList<Supplier<HumanMob>> var3) {
      var3.addObject(var2 ? 50 : 25, this.getNewRecruitMob(var1));
   }
}
