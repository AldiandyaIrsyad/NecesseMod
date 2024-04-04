package necesse.engine;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public enum GameDeathPenalty {
   NONE(new LocalMessage("ui", "dpnone"), (GameMessage)null),
   DROP_MATS(new LocalMessage("ui", "dpdropmats"), new LocalMessage("ui", "dpdropmatstip")),
   DROP_MAIN_INVENTORY(new LocalMessage("ui", "dpdropmain"), new LocalMessage("ui", "dpdropmaintip")),
   DROP_FULL_INVENTORY(new LocalMessage("ui", "dpdropfull"), new LocalMessage("ui", "dpdropfulltip")),
   HARDCORE(new LocalMessage("ui", "dphardcore"), new LocalMessage("ui", "dphardcoretip"));

   public final GameMessage displayName;
   public final GameMessage description;

   private GameDeathPenalty(GameMessage var3, GameMessage var4) {
      this.displayName = var3;
      this.description = var4;
   }

   // $FF: synthetic method
   private static GameDeathPenalty[] $values() {
      return new GameDeathPenalty[]{NONE, DROP_MATS, DROP_MAIN_INVENTORY, DROP_FULL_INVENTORY, HARDCORE};
   }
}
