package necesse.engine.save.levelData;

import necesse.engine.quest.Quest;
import necesse.engine.registries.QuestRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class QuestSave {
   public QuestSave() {
   }

   public static Quest loadSave(LoadData var0) {
      try {
         String var1 = var0.getUnsafeString("stringID");
         Quest var4 = QuestRegistry.getNewQuest(var1);
         var4.applyLoadData(var0);
         return var4;
      } catch (Exception var3) {
         String var2 = var0.getUnsafeString("stringID", "N/A", false);
         System.err.println("Could not load quest with stringID " + var2);
         var3.printStackTrace();
         return null;
      }
   }

   public static SaveData getSave(Quest var0) {
      SaveData var1 = new SaveData("QUEST");
      var1.addUnsafeString("stringID", var0.getStringID());
      var0.addSaveData(var1);
      return var1;
   }
}
