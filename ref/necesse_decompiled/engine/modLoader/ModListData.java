package necesse.engine.modLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;

public class ModListData {
   public final String id;
   public final String name;
   public final ModLoadLocation loadLocation;
   public boolean enabled;

   public ModListData(LoadedMod var1) {
      this.id = var1.id;
      this.name = var1.name;
      this.loadLocation = var1.loadLocation;
      this.enabled = var1.listData == null || var1.listData.enabled;
   }

   public ModListData(LoadData var1) {
      this.id = var1.getUnsafeString("id", (String)null, false);
      if (this.id == null) {
         throw new LoadDataException("Missing mod id");
      } else {
         this.name = var1.getUnsafeString("name", this.id, false);
         this.loadLocation = (ModLoadLocation)var1.getEnum(ModLoadLocation.class, "loadLocation", (Enum)null, false);
         if (this.loadLocation == null) {
            throw new LoadDataException("Missing mod load location");
         } else {
            this.enabled = var1.getBoolean("enabled");
         }
      }
   }

   public ModListData(ModNetworkData var1) {
      this.id = var1.id;
      this.name = var1.name;
      this.loadLocation = var1.workshopID == -1L ? ModLoadLocation.MODS_FOLDER : ModLoadLocation.STEAM_WORKSHOP;
      this.enabled = true;
   }

   public ModListData(ModSaveInfo var1, ModLoadLocation var2) {
      this.id = var1.id;
      this.name = var1.name;
      this.loadLocation = var2;
      this.enabled = true;
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("id", this.id);
      var1.addUnsafeString("name", this.name);
      var1.addEnum("loadLocation", this.loadLocation);
      var1.addBoolean("enabled", this.enabled);
   }

   public boolean matchesMod(LoadedMod var1) {
      return this.id.equals(var1.id) && (this.loadLocation == null || this.loadLocation == var1.loadLocation);
   }

   public static List<ModListData> loadList(LoadData var0) {
      ArrayList var1 = new ArrayList();
      Iterator var2 = var0.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();

         try {
            var1.add(new ModListData(var3));
         } catch (LoadDataException var5) {
            System.err.println("Could not load mod list component: " + var5.getMessage());
         } catch (Exception var6) {
            System.err.println("Could not load mod list component");
         }
      }

      return var1;
   }

   public static SaveData getSaveList(List<ModListData> var0) {
      SaveData var1 = new SaveData("");
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         ModListData var3 = (ModListData)var2.next();
         SaveData var4 = new SaveData("");
         var3.addSaveData(var4);
         var1.addSaveData(var4);
      }

      return var1;
   }
}
