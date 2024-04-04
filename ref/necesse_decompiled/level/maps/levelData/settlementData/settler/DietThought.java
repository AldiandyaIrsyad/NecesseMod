package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.friendly.human.HappinessModifier;

public class DietThought {
   public final GameMessage displayName;
   public final int variety;
   public final int happinessIncrease;

   public DietThought(GameMessage var1, int var2, int var3) {
      this.displayName = var1;
      this.variety = var2;
      this.happinessIncrease = var3;
   }

   public HappinessModifier getModifier() {
      return new HappinessModifier(this.happinessIncrease, this.displayName);
   }
}
