package necesse.engine.registries;

import java.util.Iterator;
import java.util.NoSuchElementException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.inventory.recipe.GlobalIngredient;

public class GlobalIngredientRegistry extends GameRegistry<GlobalIngredientRegistryElement> {
   public static final GlobalIngredientRegistry instance = new GlobalIngredientRegistry();

   private GlobalIngredientRegistry() {
      super("GlobalIngredient", 32767);
   }

   public void registerCore() {
      registerGlobalIngredient("anycoolingfuel", new LocalMessage("item", "anycoolingfuel"), new LocalMessage("itemtooltip", "coolingfueltip"));

      for(int var1 = 0; var1 < 10; ++var1) {
         registerGlobalIngredient("anytier" + var1 + "essence", new LocalMessage("item", "anytieressence", new Object[]{"tier", var1}), (GameMessage)null);
      }

   }

   protected void onRegister(GlobalIngredientRegistryElement var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         GlobalIngredientRegistryElement var2 = (GlobalIngredientRegistryElement)var1.next();
         var2.ingredient.onGlobalIngredientRegistryClosed();
      }

   }

   public static int registerGlobalIngredient(String var0, GameMessage var1, GameMessage var2) {
      GlobalIngredient var3 = new GlobalIngredient(var1, var2);
      return instance.register(var0, new GlobalIngredientRegistryElement(var3));
   }

   public static GlobalIngredient getGlobalIngredient(String var0) {
      try {
         int var1 = instance.getElementIDRaw(var0);
         return ((GlobalIngredientRegistryElement)instance.getElement(var1)).ingredient;
      } catch (NoSuchElementException var3) {
         int var2 = registerGlobalIngredient(var0, new LocalMessage("item", var0), (GameMessage)null);
         return ((GlobalIngredientRegistryElement)instance.getElement(var2)).ingredient;
      }
   }

   public static int getGlobalIngredientID(String var0) {
      return getGlobalIngredient(var0).getID();
   }

   public static GlobalIngredient getGlobalIngredient(int var0) {
      return ((GlobalIngredientRegistryElement)instance.getElement(var0)).ingredient;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((GlobalIngredientRegistryElement)var1, var2, var3, var4);
   }

   protected static class GlobalIngredientRegistryElement implements IDDataContainer {
      public final GlobalIngredient ingredient;

      public GlobalIngredientRegistryElement(GlobalIngredient var1) {
         this.ingredient = var1;
      }

      public IDData getIDData() {
         return this.ingredient.idData;
      }
   }
}
