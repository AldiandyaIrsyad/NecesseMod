package necesse.engine.modLoader;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public abstract class ModSettings {
   public ModSettings() {
   }

   public abstract void addSaveData(SaveData var1);

   public abstract void applyLoadData(LoadData var1);
}
