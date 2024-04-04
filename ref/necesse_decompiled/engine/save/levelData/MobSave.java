package necesse.engine.save.levelData;

import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class MobSave {
   public MobSave() {
   }

   public static Mob loadSave(LoadData var0, Level var1) {
      String var3;
      try {
         String var2 = var0.getUnsafeString("stringID");
         if (var2.startsWith("mob.")) {
            var2 = var2.substring(4);
         }

         if (!MobRegistry.mobExists(var2)) {
            var3 = VersionMigration.tryFixStringID(var2, VersionMigration.oldMobStringIDs);
            if (!var2.equals(var3)) {
               System.out.println("Migrated mob from " + var2 + " to " + var3);
               var2 = var3;
            }
         }

         Mob var5 = MobRegistry.getMob(var2, var1);
         if (var5 == null) {
            System.err.println("Loaded mob of type " + var2 + " on level " + (var1 == null ? "NULL" : var1.getIdentifier()) + " was invalid.");
            return null;
         } else {
            var5.applyLoadData(var0);
            return var5;
         }
      } catch (Exception var4) {
         var3 = var0.getUnsafeString("stringID", "N/A");
         System.err.println("Could not load mob with type " + var3 + ", error:");
         var4.printStackTrace();
         return null;
      }
   }

   public static SaveData getSave(String var0, Mob var1) {
      SaveData var2 = new SaveData(var0);
      var2.addUnsafeString("stringID", var1.getStringID());
      var1.addSaveData(var2);
      return var2;
   }
}
