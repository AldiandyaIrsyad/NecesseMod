package necesse.engine.modLoader;

import com.codedisaster.steamworks.SteamNativeHandle;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;

public class ModSaveInfo {
   public final String id;
   public final String name;
   public final String version;
   public final long workshopID;

   public ModSaveInfo(LoadedMod var1) {
      this.id = var1.id;
      this.version = var1.version;
      this.name = var1.name;
      if (var1.workshopFileID != null) {
         this.workshopID = SteamNativeHandle.getNativeHandle(var1.workshopFileID);
      } else {
         this.workshopID = -1L;
      }

   }

   public ModSaveInfo(LoadData var1) {
      this.id = var1.getUnsafeString("id", (String)null, false);
      if (this.id == null) {
         throw new LoadDataException("Missing mod id");
      } else {
         this.name = var1.getSafeString("name", (String)null, false);
         if (this.name == null) {
            throw new LoadDataException("Missing mod name");
         } else {
            this.version = var1.getUnsafeString("version", (String)null, false);
            if (this.version == null) {
               throw new LoadDataException("Missing mod version");
            } else {
               this.workshopID = var1.getLong("workshopID", -1L);
            }
         }
      }
   }

   public SaveData getSaveData() {
      SaveData var1 = new SaveData("");
      var1.addUnsafeString("id", this.id);
      var1.addSafeString("name", this.name);
      var1.addUnsafeString("version", this.version);
      var1.addLong("workshopID", this.workshopID);
      return var1;
   }
}
