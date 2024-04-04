package necesse.engine.save;

import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;

public class WorldEntitySave {
   public WorldEntitySave() {
   }

   public static WorldEntity loadSave(LoadData var0, boolean var1, World var2) {
      try {
         WorldEntity var3 = WorldEntity.getPlainWorldEntity(var2);
         var3.applyLoadData(var0, var1);
         return var3;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static WorldEntity loadSave(LoadData var0, World var1) {
      return loadSave(var0, false, var1);
   }

   public static SaveData getSave(WorldEntity var0) {
      return var0.getSave();
   }
}
