package necesse.engine.localization.message;

import java.util.HashMap;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class StaticMessage extends GameMessage {
   private String message;
   private boolean updated;

   public StaticMessage() {
      this.message = "null";
   }

   public StaticMessage(String var1) {
      this.setMessage(var1);
   }

   protected void addPacketContent(PacketWriter var1) {
      var1.putNextString(this.message);
   }

   protected void applyPacketContent(PacketReader var1) {
      String var2 = var1.getNextString();
      if (this.message == null || !this.message.equals(var2)) {
         this.updated = true;
      }

      this.message = var2;
   }

   public void addSaveData(SaveData var1) {
      var1.addSafeString("message", this.message);
   }

   public void applyLoadData(LoadData var1) {
      String var2 = var1.getSafeString("message", (String)null);
      if (this.message == null || !this.message.equals(var2)) {
         this.updated = true;
      }

      this.message = var2;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      if (this.message == null || !this.message.equals(var1)) {
         this.updated = true;
      }

      this.message = var1 == null ? "Null" : var1;
   }

   public boolean hasUpdated() {
      return this.updated;
   }

   public String translate() {
      this.updated = false;
      return this.message;
   }

   public String setSteamRichPresence(HashMap<String, String> var1, String var2, int var3) throws DuplicateRichPresenceKeyException {
      if (var2 != null) {
         if (var1.containsKey(var2)) {
            throw new DuplicateRichPresenceKeyException("Duplicate rich presence key: " + var2 + ". Before: " + (String)var1.get(var2) + ", now: " + this.message);
         }

         var1.put(var2, this.message);
      }

      return this.message;
   }

   public boolean isSame(GameMessage var1) {
      return var1.getID() == this.getID() && ((StaticMessage)var1).message.equals(this.message);
   }

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "[" + this.message + "]";
   }
}
