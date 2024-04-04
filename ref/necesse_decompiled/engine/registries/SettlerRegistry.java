package necesse.engine.registries;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.modLoader.LoadedMod;
import necesse.level.maps.levelData.settlementData.settler.AlchemistSettler;
import necesse.level.maps.levelData.settlementData.settler.AnglerSettler;
import necesse.level.maps.levelData.settlementData.settler.AnimalKeeperSettler;
import necesse.level.maps.levelData.settlementData.settler.BlacksmithSettler;
import necesse.level.maps.levelData.settlementData.settler.ElderSettler;
import necesse.level.maps.levelData.settlementData.settler.ExplorerSettler;
import necesse.level.maps.levelData.settlementData.settler.FarmerSettler;
import necesse.level.maps.levelData.settlementData.settler.GenericSettler;
import necesse.level.maps.levelData.settlementData.settler.GuardSettler;
import necesse.level.maps.levelData.settlementData.settler.GunsmithSettler;
import necesse.level.maps.levelData.settlementData.settler.HunterSettler;
import necesse.level.maps.levelData.settlementData.settler.LockedNoSettler;
import necesse.level.maps.levelData.settlementData.settler.MageSettler;
import necesse.level.maps.levelData.settlementData.settler.MinerSettler;
import necesse.level.maps.levelData.settlementData.settler.PawnBrokerSettler;
import necesse.level.maps.levelData.settlementData.settler.PirateSettler;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.StylistSettler;

public class SettlerRegistry extends GameRegistry<SettlerRegistryElement> {
   public static final SettlerRegistry instance = new SettlerRegistry();
   public static final LockedNoSettler SETTLER_LOCKED = new LockedNoSettler();

   private SettlerRegistry() {
      super("Settler", 32767);
   }

   public void registerCore() {
      registerSettler("elder", new ElderSettler());
      registerSettler("farmer", new FarmerSettler());
      registerSettler("blacksmith", new BlacksmithSettler());
      registerSettler("mage", new MageSettler());
      registerSettler("hunter", new HunterSettler());
      registerSettler("gunsmith", new GunsmithSettler());
      registerSettler("alchemist", new AlchemistSettler());
      registerSettler("angler", new AnglerSettler());
      registerSettler("pawnbroker", new PawnBrokerSettler());
      registerSettler("explorer", new ExplorerSettler());
      registerSettler("miner", new MinerSettler());
      registerSettler("animalkeeper", new AnimalKeeperSettler());
      registerSettler("stylist", new StylistSettler());
      registerSettler("pirate", new PirateSettler());
      registerSettler("guard", new GuardSettler());
      registerSettler("generic", new GenericSettler());
   }

   protected void onRegister(SettlerRegistryElement var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         SettlerRegistryElement var2 = (SettlerRegistryElement)var1.next();
         var2.settler.onSettlerRegistryClosed();
      }

   }

   public static void loadSettlerIconTextures() {
      instance.streamElements().map((var0) -> {
         return var0.settler;
      }).forEach(Settler::loadTextures);
   }

   public static int registerSettler(String var0, Settler var1) {
      if (LoadedMod.isRunningModClientSide()) {
         throw new IllegalStateException("Client/server only mods cannot register settlers");
      } else {
         return instance.register(var0, new SettlerRegistryElement(var1));
      }
   }

   public static int replaceSettler(String var0, Settler var1) {
      return instance.replace(var0, new SettlerRegistryElement(var1));
   }

   public static Settler getSettler(int var0) {
      if (var0 >= 0 && var0 < instance.size()) {
         SettlerRegistryElement var1 = (SettlerRegistryElement)instance.getElement(var0);
         return var1 == null ? null : var1.settler;
      } else {
         return null;
      }
   }

   public static Settler getSettler(String var0) {
      SettlerRegistryElement var1 = (SettlerRegistryElement)instance.getElement(var0);
      return var1 == null ? null : var1.settler;
   }

   public static int getSettlerID(String var0) {
      return instance.getElementID(var0);
   }

   public static String getSettlerStringID(int var0) {
      return instance.getElementStringID(var0);
   }

   public static List<Settler> getSettlers() {
      return (List)instance.streamElements().map((var0) -> {
         return var0.settler;
      }).collect(Collectors.toList());
   }

   public static List<Integer> getSettlerIDs() {
      return (List)instance.streamElements().map((var0) -> {
         return var0.settler.getID();
      }).collect(Collectors.toList());
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((SettlerRegistryElement)var1, var2, var3, var4);
   }

   protected static class SettlerRegistryElement implements IDDataContainer {
      public final Settler settler;

      public SettlerRegistryElement(Settler var1) {
         this.settler = var1;
      }

      public IDData getIDData() {
         return this.settler.idData;
      }
   }
}
