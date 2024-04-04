package necesse.engine.quest;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class DeliverItemsSettlementQuest extends DeliverItemsQuest {
   public DeliverItemsSettlementQuest() {
   }

   public DeliverItemsSettlementQuest(String var1, int var2) {
      super(var1, var2);
   }

   public GameMessage getTitle() {
      return new LocalMessage("quests", "settlementquest");
   }

   public boolean canShare() {
      return false;
   }
}
