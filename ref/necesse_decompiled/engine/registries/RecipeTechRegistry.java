package necesse.engine.registries;

import java.util.NoSuchElementException;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.inventory.recipe.Tech;

public class RecipeTechRegistry extends GameRegistry<TechRegistryElement> {
   public static Tech ALL;
   public static Tech NONE;
   public static Tech WORKSTATION;
   public static Tech COOKING_POT;
   public static Tech ROASTING_STATION;
   public static Tech FORGE;
   public static Tech CARPENTER;
   public static Tech IRON_ANVIL;
   public static Tech DEMONIC;
   public static Tech ALCHEMY;
   public static Tech COOKING_STATION;
   public static Tech ADVANCED_WORKSTATION;
   public static Tech FALLEN_WORKSTATION;
   public static Tech FALLEN_ALCHEMY;
   public static Tech COMPOST_BIN;
   public static Tech GRAIN_MILL;
   public static Tech CHEESE_PRESS;
   public static final RecipeTechRegistry instance = new RecipeTechRegistry();

   private RecipeTechRegistry() {
      super("Recipe Tech", 65535);
   }

   public void registerCore() {
      ALL = registerTech("all");
      NONE = registerTech("none", new LocalMessage("tech", "inventory"));
      WORKSTATION = registerTech("workstation");
      COOKING_POT = registerTech("cookingpot");
      ROASTING_STATION = registerTech("roastingstation");
      FORGE = registerTech("forge");
      CARPENTER = registerTech("carpenter");
      IRON_ANVIL = registerTech("ironanvil");
      DEMONIC = registerTech("demonic");
      ALCHEMY = registerTech("alchemy");
      COOKING_STATION = registerTech("cookingstation");
      ADVANCED_WORKSTATION = registerTech("advanced");
      FALLEN_WORKSTATION = registerTech("fallen");
      FALLEN_ALCHEMY = registerTech("fallenalchemy");
      COMPOST_BIN = registerTech("compostbin", new LocalMessage("object", "compostbin"), (GameMessage)null);
      GRAIN_MILL = registerTech("grainmill", new LocalMessage("object", "grainmill"), new LocalMessage("itemtooltip", "milltip"));
      CHEESE_PRESS = registerTech("cheesepress", new LocalMessage("object", "cheesepress"), (GameMessage)null);
   }

   protected void onRegister(TechRegistryElement var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
   }

   public static Tech registerTech(String var0, GameMessage var1, GameMessage var2) {
      Tech var3 = new Tech(var1, var2);
      instance.register(var0, new TechRegistryElement(var3));
      return var3;
   }

   public static Tech registerTech(String var0, GameMessage var1) {
      return registerTech(var0, var1, new LocalMessage("itemtooltip", "craftingmat"));
   }

   public static Tech registerTech(String var0) {
      return registerTech(var0, new LocalMessage("tech", var0));
   }

   public static Tech getTech(int var0) {
      return ((TechRegistryElement)instance.getElement(var0)).tech;
   }

   public static Tech getTech(String var0) throws NoSuchElementException {
      int var1 = instance.getElementIDRaw(var0);
      return getTech(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((TechRegistryElement)var1, var2, var3, var4);
   }

   protected static class TechRegistryElement implements IDDataContainer {
      public final Tech tech;

      public TechRegistryElement(Tech var1) {
         this.tech = var1;
      }

      public IDData getIDData() {
         return this.tech.data;
      }
   }
}
