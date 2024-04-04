package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.friendly.human.HappinessModifier;

public class RoomSize {
   public final GameMessage displayName;
   public final int minSize;
   public final int happinessIncrease;

   public RoomSize(GameMessage var1, int var2, int var3) {
      this.displayName = var1;
      this.minSize = var2;
      this.happinessIncrease = var3;
   }

   public HappinessModifier getModifier() {
      LocalMessage var1 = (new LocalMessage("settlement", "sizemood")).addReplacement("size", this.displayName);
      return new HappinessModifier(this.happinessIncrease, var1);
   }
}
