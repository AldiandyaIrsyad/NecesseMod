package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class PawnBrokerSettler extends Settler {
   public PawnBrokerSettler() {
      super("pawnbrokerhuman");
   }

   public GameMessage getAcquireTip() {
      return new LocalMessage("settlement", "pawnbrokertip");
   }
}
