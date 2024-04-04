package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class MoodDescription {
   public final GameMessage displayName;
   public final int minHappiness;

   public MoodDescription(GameMessage var1, int var2) {
      this.displayName = var1;
      this.minHappiness = var2;
   }

   public GameMessage getDescription() {
      return (new LocalMessage("settlement", "moodhappiness")).addReplacement("happiness", this.displayName);
   }
}
