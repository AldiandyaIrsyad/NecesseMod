package necesse.engine.localization.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GameMessageBuilder extends GameMessage {
   private ArrayList<GameMessage> messages = new ArrayList();

   public GameMessageBuilder() {
   }

   public boolean hasUpdated() {
      return this.messages.stream().anyMatch(GameMessage::hasUpdated);
   }

   public String translate() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.messages.iterator();

      while(var2.hasNext()) {
         GameMessage var3 = (GameMessage)var2.next();
         var1.append(var3.translate());
      }

      return var1.toString();
   }

   public String setSteamRichPresence(HashMap<String, String> var1, String var2, int var3) throws DuplicateRichPresenceKeyException {
      StringBuilder var4 = new StringBuilder();

      for(int var5 = 0; var5 < this.messages.size(); ++var5) {
         GameMessage var6 = (GameMessage)this.messages.get(var5);
         var4.append(var6.setSteamRichPresence(var1, (String)null, var5));
      }

      String var7 = var4.toString();
      if (var2 != null) {
         if (var1.containsKey(var2)) {
            throw new DuplicateRichPresenceKeyException("Duplicate rich presence key: " + var2 + ". Before: " + (String)var1.get(var2) + ", now: " + var7);
         }

         var1.put(var2, var7);
      }

      return var7;
   }

   public boolean isSame(GameMessage var1) {
      if (var1.getID() != this.getID()) {
         return false;
      } else {
         GameMessageBuilder var2 = (GameMessageBuilder)var1;
         if (var2.messages.size() != this.messages.size()) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.messages.size(); ++var3) {
               if (!((GameMessage)var2.messages.get(var3)).isSame((GameMessage)this.messages.get(var3))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   protected void addPacketContent(PacketWriter var1) {
      var1.putNextShortUnsigned(this.messages.size());
      Iterator var2 = this.messages.iterator();

      while(var2.hasNext()) {
         GameMessage var3 = (GameMessage)var2.next();
         var1.putNextContentPacket(var3.getContentPacket());
      }

   }

   protected void applyPacketContent(PacketReader var1) {
      this.messages.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.messages.add(fromContentPacket(var1.getNextContentPacket()));
      }

   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.messages.iterator();

      while(var2.hasNext()) {
         GameMessage var3 = (GameMessage)var2.next();
         var1.addSaveData(var3.getSaveData(""));
      }

   }

   public void applyLoadData(LoadData var1) {
      this.messages.clear();
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         this.messages.add(loadSave(var3));
      }

   }

   public GameMessageBuilder append(GameMessage var1) {
      this.messages.add(var1);
      return this;
   }

   public GameMessageBuilder append(int var1, GameMessage var2) {
      this.messages.add(var1, var2);
      return this;
   }

   public GameMessageBuilder prepend(GameMessage var1) {
      this.messages.add(0, var1);
      return this;
   }

   public GameMessageBuilder append(String var1) {
      return this.append((GameMessage)(new StaticMessage(var1)));
   }

   public GameMessageBuilder append(String var1, String var2) {
      return this.append((GameMessage)(new LocalMessage(var1, var2)));
   }

   public GameMessageBuilder append(int var1, String var2) {
      return this.append(var1, (GameMessage)(new StaticMessage(var2)));
   }

   public GameMessageBuilder prepend(String var1) {
      return this.prepend((GameMessage)(new StaticMessage(var1)));
   }

   public void clear() {
      this.messages.clear();
   }

   public int size() {
      return this.messages.size();
   }

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "[" + Arrays.toString(this.messages.toArray()) + "]";
   }
}
