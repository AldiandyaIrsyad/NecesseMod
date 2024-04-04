package necesse.inventory.recipe;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;

public class Tech {
   public final IDData data = new IDData();
   public final GameMessage displayName;
   public final GameMessage craftingMatTip;

   public String getStringID() {
      return this.data.getStringID();
   }

   public int getID() {
      return this.data.getID();
   }

   public Tech(GameMessage var1, GameMessage var2) {
      this.displayName = var1;
      this.craftingMatTip = var2;
   }
}
