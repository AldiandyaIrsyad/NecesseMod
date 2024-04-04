package necesse.engine.localization.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class LocalMessage extends GameMessage {
   public static Pattern replaceRegex = Pattern.compile("<(\\w+)>");
   public String category;
   public String key;
   private ArrayList<LocalReplacement> replacements;
   private String lastTranslation;
   private int lastLanguageUnique;
   private boolean updated;

   public LocalMessage() {
      this.category = "null";
      this.key = "null";
      this.replacements = new ArrayList();
   }

   public LocalMessage(String var1, String var2) {
      this.category = var1;
      this.key = var2;
      this.replacements = new ArrayList();
      this.updated = true;
   }

   public LocalMessage(String var1, String var2, ArrayList<LocalReplacement> var3) {
      this(var1, var2);
      this.replacements = var3;
   }

   public LocalMessage(String var1, String var2, String var3, String var4) {
      this(var1, var2);
      this.addReplacement(var3, var4);
   }

   public LocalMessage(String var1, String var2, String var3, GameMessage var4) {
      this(var1, var2);
      this.addReplacement(var3, var4);
   }

   public LocalMessage(String var1, String var2, String var3, Function<LocalMessage, GameMessage> var4) {
      this(var1, var2);
      this.addReplacement(var3, var4);
   }

   public LocalMessage(String var1, String var2, String... var3) {
      this(var1, var2);
      if (var3.length % 2 != 0) {
         throw new IllegalArgumentException("Replacements must have an even amount of parameters");
      } else {
         for(int var4 = 0; var4 < var3.length; var4 += 2) {
            String var5 = var3[var4];
            String var6 = var3[var4 + 1];
            this.addReplacement(var5, var6);
         }

      }
   }

   public LocalMessage(String var1, String var2, Object... var3) {
      this(var1, var2);
      if (var3.length % 2 != 0) {
         throw new IllegalArgumentException("Replacements must have an even amount of parameters");
      } else {
         for(int var4 = 0; var4 < var3.length; var4 += 2) {
            String var5 = var3[var4].toString();
            if (var3[var4 + 1] instanceof GameMessage) {
               this.addReplacement(var5, (GameMessage)var3[var4 + 1]);
            } else {
               this.addReplacement(var5, var3[var4 + 1].toString());
            }
         }

      }
   }

   protected void addPacketContent(PacketWriter var1) {
      var1.putNextString(this.category);
      var1.putNextString(this.key);
      var1.putNextShortUnsigned(this.replacements.size());
      Iterator var2 = this.replacements.iterator();

      while(var2.hasNext()) {
         LocalReplacement var3 = (LocalReplacement)var2.next();
         var1.putNextContentPacket(var3.getContentPacket(this));
      }

   }

   protected void applyPacketContent(PacketReader var1) {
      this.category = var1.getNextString();
      this.key = var1.getNextString();
      int var2 = var1.getNextShortUnsigned();
      this.replacements = new ArrayList();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.replacements.add(new LocalReplacement(var1.getNextContentPacket()));
      }

   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("category", this.category);
      var1.addUnsafeString("key", this.key);
      if (!this.replacements.isEmpty()) {
         SaveData var2 = new SaveData("REPLACES");
         Iterator var3 = this.replacements.iterator();

         while(var3.hasNext()) {
            LocalReplacement var4 = (LocalReplacement)var3.next();
            var2.addSaveData(var4.getSaveData("", this));
         }

         var1.addSaveData(var2);
      }

   }

   public void applyLoadData(LoadData var1) {
      this.category = var1.getUnsafeString("category");
      this.key = var1.getUnsafeString("key");
      this.replacements = new ArrayList();
      LoadData var2 = var1.getFirstLoadDataByName("REPLACES");
      if (var2 != null) {
         Iterator var3 = var2.getLoadData().iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();
            this.replacements.add(new LocalReplacement(var4));
         }
      }

   }

   public boolean hasUpdated() {
      if (this.updated) {
         return true;
      } else if (this.lastLanguageUnique != Localization.getCurrentLang().updateUnique) {
         return true;
      } else {
         Iterator var1 = this.replacements.iterator();

         LocalReplacement var2;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            var2 = (LocalReplacement)var1.next();
         } while(!var2.hasUpdated(this));

         return true;
      }
   }

   protected void update() {
      String var1 = Localization.translate(this.category, this.key);

      LocalReplacement var3;
      for(Iterator var2 = this.replacements.iterator(); var2.hasNext(); var1 = var3.replace(this, var1)) {
         var3 = (LocalReplacement)var2.next();
      }

      this.lastTranslation = var1;
      this.lastLanguageUnique = Localization.getCurrentLang().updateUnique;
      this.updated = false;
   }

   public String translate() {
      if (this.hasUpdated()) {
         this.update();
      }

      return this.lastTranslation;
   }

   public String setSteamRichPresence(HashMap<String, String> var1, String var2, int var3) throws DuplicateRichPresenceKeyException {
      String var4 = "#" + this.category + "_" + this.key;

      for(int var5 = 0; var5 < this.replacements.size(); ++var5) {
         LocalReplacement var6 = (LocalReplacement)this.replacements.get(var5);
         var6.setSteamRichPresence(var1, this, var6.getKey(), var5);
      }

      if (var2 != null) {
         if (var1.containsKey(var2)) {
            throw new DuplicateRichPresenceKeyException("Duplicate rich presence key: " + var2 + ". Before: " + (String)var1.get(var2) + ", now: " + var4);
         }

         var1.put(var2, var4);
      }

      return var4;
   }

   public boolean isSame(GameMessage var1) {
      if (var1.getID() != this.getID()) {
         return false;
      } else {
         LocalMessage var2 = (LocalMessage)var1;
         if (var2.category.equals(this.category) && var2.key.equals(this.key) && var2.replacements.size() == this.replacements.size()) {
            for(int var3 = 0; var3 < this.replacements.size(); ++var3) {
               if (!((LocalReplacement)this.replacements.get(var3)).isSame(this, var2, (LocalReplacement)var2.replacements.get(var3))) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public LocalMessage setReplacements(ArrayList<LocalReplacement> var1) {
      this.replacements = var1;
      return this;
   }

   public ArrayList<LocalReplacement> getReplacements() {
      return this.replacements;
   }

   public LocalMessage addReplacement(String var1, String var2) {
      this.replacements.add(new LocalReplacement(var1, var2));
      this.updated = true;
      return this;
   }

   public LocalMessage addReplacement(String var1, GameMessage var2) {
      this.replacements.add(new LocalReplacement(var1, var2));
      this.updated = true;
      return this;
   }

   public LocalMessage addReplacement(String var1, Function<LocalMessage, GameMessage> var2) {
      this.replacements.add(new LocalReplacement(var1, var2));
      this.updated = true;
      return this;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.replacements.iterator();

      while(var2.hasNext()) {
         LocalReplacement var3 = (LocalReplacement)var2.next();
         var1.append(", ").append(var3.getKey()).append(": ").append(var3.getReplacement(this).toString());
      }

      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "[" + this.category + "." + this.key + var1 + "]";
   }
}
