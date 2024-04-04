package necesse.engine.quest;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class KillMobsSettlementQuest extends KillMobsQuest {
   public KillMobsSettlementQuest() {
   }

   public KillMobsSettlementQuest(KillMobsQuest.KillObjective var1, KillMobsQuest.KillObjective... var2) {
      super(var1, var2);
   }

   public KillMobsSettlementQuest(String var1, int var2) {
      super(var1, var2);
   }

   public KillMobsSettlementQuest(int var1, int var2) {
      super(var1, var2);
   }

   public GameMessage getTitle() {
      return new LocalMessage("quests", "settlementquest");
   }

   public boolean canShare() {
      return false;
   }
}
