package necesse.engine.save.levelData;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class ObjectEntitySave {
   public ObjectEntitySave() {
   }

   public static ObjectEntity loadSave(LoadData var0, Level var1) {
      try {
         int var2 = var0.getInt("x");
         int var3 = var0.getInt("y");
         ObjectEntity var4 = var1.getLevelObject(var2, var3).getNewObjectEntity();
         if (var4 == null) {
            System.err.println("Loaded object entity at " + var2 + ", " + var3 + " on level " + var1.getIdentifier() + " was invalid.");
            return null;
         } else if (!var4.type.equals(var0.getUnsafeString("stringID"))) {
            System.err.println("Loaded object entity type at " + var2 + ", " + var3 + " on level " + var1.getIdentifier() + " was invalid.");
            return null;
         } else {
            var4.applyLoadData(var0);
            return var4;
         }
      } catch (Exception var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public static SaveData getSave(ObjectEntity var0) {
      SaveData var1 = new SaveData("OBJENT");
      var0.addSaveData(var1);
      var1.addUnsafeString("stringID", var0.type);
      var1.addInt("x", var0.getX());
      var1.addInt("y", var0.getY());
      return var1;
   }
}
