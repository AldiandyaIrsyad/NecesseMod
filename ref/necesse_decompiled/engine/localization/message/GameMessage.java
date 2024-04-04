package necesse.engine.localization.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.EmptyConstructorGameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameFont.FontOptions;

public abstract class GameMessage implements IDDataContainer {
   public final IDData idData = new IDData();
   public static final EmptyConstructorGameRegistry<GameMessage> registry = new EmptyConstructorGameRegistry<GameMessage>("GameMessage", 32767) {
      public void registerCore() {
         this.registerClass("static", StaticMessage.class);
         this.registerClass("local", LocalMessage.class);
         this.registerClass("builder", GameMessageBuilder.class);
      }

      protected void onRegistryClose() {
      }
   };

   public IDData getIDData() {
      return this.idData;
   }

   public GameMessage() {
      registry.applyIDData(this.getClass(), this.idData);
   }

   public abstract boolean hasUpdated();

   public abstract String translate();

   public abstract String setSteamRichPresence(HashMap<String, String> var1, String var2, int var3) throws DuplicateRichPresenceKeyException;

   public abstract boolean isSame(GameMessage var1);

   public static boolean isSame(GameMessage var0, GameMessage var1) {
      return var0 == var1 || var0 != null && var1 != null && var0.isSame(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof GameMessage ? this.isSame((GameMessage)var1) : super.equals(var1);
      }
   }

   protected abstract void addPacketContent(PacketWriter var1);

   protected abstract void applyPacketContent(PacketReader var1);

   public abstract void addSaveData(SaveData var1);

   public abstract void applyLoadData(LoadData var1);

   public final void writePacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.getID());
      this.addPacketContent(var1);
   }

   public static GameMessage fromPacket(PacketReader var0) {
      int var1 = var0.getNextShortUnsigned();
      GameMessage var2 = getNewMessage(var1);
      if (var2 != null) {
         var2.applyPacketContent(var0);
      }

      return var2;
   }

   public final Packet getContentPacket() {
      Packet var1 = new Packet();
      this.writePacket(new PacketWriter(var1));
      return var1;
   }

   public static GameMessage fromContentPacket(Packet var0) {
      return fromPacket(new PacketReader(var0));
   }

   public final SaveData getSaveData(String var1) {
      SaveData var2 = new SaveData(var1);
      var2.addUnsafeString("stringID", this.getStringID());
      this.addSaveData(var2);
      return var2;
   }

   public static GameMessage loadSave(LoadData var0) {
      String var1 = var0.getUnsafeString("stringID");
      GameMessage var2 = getNewMessage(var1);
      if (var2 != null) {
         var2.applyLoadData(var0);
      }

      return var2;
   }

   public static GameMessage loadSave(LoadData var0, String var1, boolean var2) {
      LoadData var3 = var0.getFirstLoadDataByName(var1);
      if (var3 != null) {
         GameMessage var4 = loadSave(var3);
         if (var4 == null && var2) {
            GameLog.warn.println("Could not load " + var1 + " GameMessage because from " + (var0.getName().isEmpty() ? "null" : var0.getName()) + " because of error.");
         }

         return var4;
      } else {
         if (var2) {
            GameLog.warn.println("Could not load " + var1 + " GameMessage because from " + (var0.getName().isEmpty() ? "null" : var0.getName()) + ".");
         }

         return null;
      }
   }

   public ArrayList<GameMessage> breakMessage(FontOptions var1, int var2) {
      ArrayList var3 = GameUtils.breakString(this.translate(), var1, var2);
      return (ArrayList)var3.stream().map(StaticMessage::new).collect(Collectors.toCollection(ArrayList::new));
   }

   public static int registerClass(String var0, Class<? extends GameMessage> var1) {
      return registry.registerClass(var0, var1);
   }

   public static GameMessage getNewMessage(int var0) {
      return (GameMessage)registry.getNewInstance(var0);
   }

   public static GameMessage getNewMessage(String var0) {
      return (GameMessage)registry.getNewInstance(var0);
   }

   static {
      registry.registerCore();
   }
}
