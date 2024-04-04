package necesse.level.maps.levelData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.registries.LevelDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.Performance;
import necesse.level.maps.Level;

public class LevelDataManager {
   public final Level level;
   private HashMap<String, LevelData> data = new HashMap();

   public LevelDataManager(Level var1) {
      this.level = var1;
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.data.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (((LevelData)var3.getValue()).shouldSave()) {
            SaveData var4 = new SaveData((String)var3.getKey());
            ((LevelData)var3.getValue()).addSaveData(var4);
            var1.addSaveData(var4);
         }
      }

   }

   public void loadSaveData(LoadData var1) {
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();

         try {
            LevelData var4 = LevelDataRegistry.loadLevelData(this.level, var3);
            if (var4 != null) {
               this.level.addLevelData(var3.getName(), var4);
            }
         } catch (Exception var5) {
            System.err.println("Error loading level data:");
            var5.printStackTrace();
         }
      }

   }

   public void tick() {
      Performance.record(this.level.tickManager(), "levelData", (Runnable)(() -> {
         Iterator var1 = this.data.values().iterator();

         while(var1.hasNext()) {
            LevelData var2 = (LevelData)var1.next();
            var2.tick();
         }

      }));
   }

   public LevelData getLevelData(String var1) {
      return (LevelData)this.data.get(var1);
   }

   public void addLevelData(String var1, LevelData var2) {
      if (!var1.matches("[a-zA-Z0-9]+")) {
         throw new IllegalArgumentException("Key \"" + var1 + "\" contains illegal characters");
      } else {
         var2.setLevel(this.level);
         this.data.put(var1, var2);
      }
   }

   public LevelData removeLevelData(String var1) {
      return (LevelData)this.data.remove(var1);
   }
}
