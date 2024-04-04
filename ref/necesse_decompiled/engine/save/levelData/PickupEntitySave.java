package necesse.engine.save.levelData;

import necesse.engine.registries.PickupRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.pickup.PickupEntity;

public class PickupEntitySave {
   public PickupEntitySave() {
   }

   public static PickupEntity loadSave(LoadData var0) {
      try {
         String var1 = var0.getUnsafeString("stringID");
         PickupEntity var2 = PickupRegistry.getPickup(var1);
         var2.applyLoadData(var0);
         return var2;
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static SaveData getSave(PickupEntity var0) {
      SaveData var1 = new SaveData("PICKUP");
      var0.addSaveData(var1);
      var1.addUnsafeString("stringID", var0.getStringID());
      return var1;
   }
}
