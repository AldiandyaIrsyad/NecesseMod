package necesse.engine.localization.message;

import java.util.HashMap;
import java.util.function.Function;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class LocalReplacement {
   private static GameMessage NULL_MESSAGE = new StaticMessage("Null");
   private String key;
   private Function<LocalMessage, GameMessage> replacement;
   private GameMessage lastReplacement;

   public LocalReplacement(Packet var1) {
      PacketReader var2 = new PacketReader(var1);
      this.key = var2.getNextString();
      GameMessage var3 = GameMessage.fromContentPacket(var2.getNextContentPacket());
      this.replacement = (var1x) -> {
         return var3;
      };
   }

   public Packet getContentPacket(LocalMessage var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      String var4 = this.getKey();
      GameMessage var5 = this.getReplacement(var1);
      var3.putNextString(var4);
      var3.putNextContentPacket(var5.getContentPacket());
      return var2;
   }

   public LocalReplacement(LoadData var1) {
      this.key = var1.getUnsafeString("key");
      GameMessage var2 = GameMessage.loadSave(var1.getFirstLoadDataByName("replace"));
      this.replacement = (var1x) -> {
         return var2;
      };
   }

   public SaveData getSaveData(String var1, LocalMessage var2) {
      SaveData var3 = new SaveData(var1);
      var3.addUnsafeString("key", this.key);
      var3.addSaveData(this.getReplacement(var2).getSaveData("replace"));
      return var3;
   }

   public LocalReplacement(String var1, Function<LocalMessage, GameMessage> var2) {
      this.key = var1;
      this.replacement = var2;
   }

   public LocalReplacement(String var1, GameMessage var2) {
      this(var1, (var1x) -> {
         return var2;
      });
   }

   public LocalReplacement(String var1, String var2) {
      this.key = var1;
      StaticMessage var3 = new StaticMessage(var2);
      this.replacement = (var1x) -> {
         return var3;
      };
   }

   public String getKey() {
      return this.key;
   }

   public GameMessage getReplacement(LocalMessage var1) {
      GameMessage var2 = (GameMessage)this.replacement.apply(var1);
      return var2 == null ? NULL_MESSAGE : var2;
   }

   public String replace(LocalMessage var1, String var2) {
      this.lastReplacement = this.getReplacement(var1);
      return var2.replace("<" + this.getKey() + ">", this.lastReplacement.translate());
   }

   public String setSteamRichPresence(HashMap<String, String> var1, LocalMessage var2, String var3, int var4) throws DuplicateRichPresenceKeyException {
      String var5 = this.getReplacement(var2).setSteamRichPresence(var1, (String)null, var4);
      if (var3 != null) {
         if (var1.containsKey(var3)) {
            throw new DuplicateRichPresenceKeyException("Duplicate rich presence key: " + var3 + ". Before: " + (String)var1.get(var3) + ", now: " + var5);
         }

         var1.put(var3, var5);
      }

      return var5;
   }

   public boolean hasUpdated(LocalMessage var1) {
      GameMessage var2 = this.getReplacement(var1);
      return this.lastReplacement == null || var2.hasUpdated();
   }

   public boolean isSame(LocalMessage var1, LocalMessage var2, LocalReplacement var3) {
      return var3.key.equals(this.key) && var3.getReplacement(var2).isSame(this.getReplacement(var1));
   }
}
