package necesse.inventory.recipe;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ItemRegistry;

public class GlobalIngredient {
   public final IDData idData = new IDData();
   public final GameMessage displayName;
   public final GameMessage craftingMatTip;
   private ArrayList<Integer> registeredItemIDs;
   private ArrayList<Integer> obtainableRegisteredItemIDs;

   public final int getID() {
      return this.idData.getID();
   }

   public String getStringID() {
      return this.idData.getStringID();
   }

   public GlobalIngredient(GameMessage var1, GameMessage var2) {
      this.displayName = var1;
      this.craftingMatTip = var2;
      this.registeredItemIDs = new ArrayList();
      this.obtainableRegisteredItemIDs = new ArrayList();
   }

   public void onGlobalIngredientRegistryClosed() {
   }

   public void registerItemID(int var1) {
      if (!this.registeredItemIDs.contains(var1)) {
         this.registeredItemIDs.add(var1);
         if (ItemRegistry.isObtainable(var1)) {
            this.obtainableRegisteredItemIDs.add(var1);
         }

      }
   }

   public ArrayList<Integer> getRegisteredItemIDs() {
      return this.registeredItemIDs;
   }

   public ArrayList<Integer> getObtainableRegisteredItemIDs() {
      return this.obtainableRegisteredItemIDs;
   }
}
