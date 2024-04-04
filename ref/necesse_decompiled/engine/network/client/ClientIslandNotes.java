package necesse.engine.network.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import necesse.engine.GameCache;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class ClientIslandNotes {
   private final long worldUniqueID;
   private HashMap<String, String> notes = new HashMap();

   public ClientIslandNotes(long var1) {
      this.worldUniqueID = var1;
      this.loadNotes();
   }

   private String getCachePath() {
      byte[] var1 = (this.worldUniqueID + "IslandNotes").getBytes();
      return "/client/" + UUID.nameUUIDFromBytes(var1);
   }

   private void saveNotes() {
      SaveData var1 = new SaveData("IslandNotes");
      Iterator var2 = this.notes.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         String var4 = (String)this.notes.get(var3);
         if (var4 != null && var4.length() != 0) {
            var1.addSafeString(var3, var4);
         }
      }

      GameCache.cacheSave(var1, this.getCachePath());
   }

   private void loadNotes() {
      LoadData var1 = GameCache.getSave(this.getCachePath());
      if (var1 != null) {
         this.notes.clear();
         Iterator var2 = var1.getLoadData().iterator();

         while(var2.hasNext()) {
            LoadData var3 = (LoadData)var2.next();
            if (var3.isData()) {
               this.notes.put(var3.getName(), LoadData.getSafeString(var3));
            }
         }

      }
   }

   public void set(int var1, int var2, String var3) {
      this.notes.put(var1 + "x" + var2, var3);
      this.saveNotes();
   }

   public String get(int var1, int var2) {
      return (String)this.notes.get(var1 + "x" + var2);
   }
}
