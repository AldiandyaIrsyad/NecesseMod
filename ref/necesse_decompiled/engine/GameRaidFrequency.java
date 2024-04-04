package necesse.engine;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public enum GameRaidFrequency {
   OFTEN(new LocalMessage("ui", "raidsoften"), (GameMessage)null),
   OCCASIONALLY(new LocalMessage("ui", "raidsoccasionally"), (GameMessage)null),
   RARELY(new LocalMessage("ui", "raidsrarely"), (GameMessage)null),
   NEVER(new LocalMessage("ui", "raidsnever"), (GameMessage)null);

   public final GameMessage displayName;
   public final GameMessage description;

   private GameRaidFrequency(GameMessage var3, GameMessage var4) {
      this.displayName = var3;
      this.description = var4;
   }

   // $FF: synthetic method
   private static GameRaidFrequency[] $values() {
      return new GameRaidFrequency[]{OFTEN, OCCASIONALLY, RARELY, NEVER};
   }
}
