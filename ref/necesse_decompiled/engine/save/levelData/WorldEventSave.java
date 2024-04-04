package necesse.engine.save.levelData;

import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.world.worldEvent.WorldEvent;

public class WorldEventSave {
   public WorldEventSave() {
   }

   public static WorldEvent loadSave(LoadData var0) {
      try {
         String var1 = var0.getUnsafeString("stringID");
         WorldEvent var4 = WorldEventRegistry.getEvent(var1);
         var4.applyLoadData(var0);
         return var4;
      } catch (Exception var3) {
         String var2 = var0.getUnsafeString("stringID", "N/A", false);
         System.err.println("Could not load world event with stringID " + var2);
         var3.printStackTrace();
         return null;
      }
   }

   public static SaveData getSave(WorldEvent var0) {
      SaveData var1 = new SaveData("EVENT");
      var1.addUnsafeString("stringID", var0.getStringID());
      var0.addSaveData(var1);
      return var1;
   }
}
