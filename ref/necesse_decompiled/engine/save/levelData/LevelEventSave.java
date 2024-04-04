package necesse.engine.save.levelData;

import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public class LevelEventSave {
   public LevelEventSave() {
   }

   public static LevelEvent loadSaveData(LoadData var0, Level var1) {
      try {
         String var2 = var0.getUnsafeString("stringID");
         LevelEvent var3 = LevelEventRegistry.getEvent(var2);
         var3.level = var1;
         var3.applyLoadData(var0);
         return var3;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static SaveData getSave(LevelEvent var0) {
      SaveData var1 = new SaveData("EVENT");
      var0.addSaveData(var1);
      var1.addUnsafeString("stringID", var0.getStringID());
      return var1;
   }
}
