package necesse.engine.quest;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class HaveKilledMobsSettlementQuest extends HaveKilledMobsQuest {
   public HaveKilledMobsSettlementQuest() {
   }

   public HaveKilledMobsSettlementQuest(HaveKilledMobsQuest.HaveKilledObjective var1, HaveKilledMobsQuest.HaveKilledObjective... var2) {
      super(var1, var2);
   }

   public HaveKilledMobsSettlementQuest(String var1, int var2) {
      super(var1, var2);
   }

   public HaveKilledMobsSettlementQuest(int var1, int var2) {
      super(var1, var2);
   }

   public GameMessage getTitle() {
      return new LocalMessage("quests", "settlementquest");
   }

   public boolean canShare() {
      return false;
   }
}
