package necesse.engine.network.server;

import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmaking.LobbyType;
import java.awt.Point;
import java.io.File;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;

public class ServerSettings {
   public final File worldFilePath;
   public int slots;
   public int port;
   public String password = "";
   public boolean allowConnectByIP = true;
   public SteamLobbyType steamLobbyType;
   public int spawnSeed;
   public Point spawnIsland;
   public boolean spawnGuide;

   private ServerSettings(File var1, int var2, int var3) {
      this.steamLobbyType = ServerSettings.SteamLobbyType.Open;
      this.spawnSeed = GameRandom.globalRandom.nextInt();
      this.spawnIsland = null;
      this.spawnGuide = true;
      this.worldFilePath = var1;
      this.slots = var2;
      this.port = var3;
   }

   public static ServerSettings HostServer(File var0, int var1, int var2) {
      if (var0 != null && !var0.getName().isEmpty()) {
         if (var1 >= 0 && var1 <= 250) {
            if (var2 >= 0 && var2 <= 65535) {
               return new ServerSettings(var0, var1, var2);
            } else {
               throw new IllegalArgumentException("Invalid port");
            }
         } else {
            throw new IllegalArgumentException("Invalid slots");
         }
      } else {
         throw new IllegalArgumentException("Invalid world name");
      }
   }

   public static ServerSettings SingleplayerServer(File var0) {
      if (var0 != null && !var0.getName().isEmpty()) {
         return new ServerSettings(var0, 1, -1);
      } else {
         throw new IllegalArgumentException("Invalid world name");
      }
   }

   public static ServerSettings FromSave(File var0, LoadData var1) {
      int var2 = var1.getInt("slots", Settings.serverSlots);
      int var3 = var1.getInt("port", Settings.serverPort);
      ServerSettings var4 = new ServerSettings(var0, var2, var3);
      var4.password = var1.getSafeString("password", var4.password);
      var4.allowConnectByIP = var1.getBoolean("allowConnectByIP", var4.allowConnectByIP);
      var4.steamLobbyType = (SteamLobbyType)var1.getEnum(SteamLobbyType.class, "steamLobbyType", var4.steamLobbyType);
      return var4;
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("slots", this.slots);
      var1.addInt("port", this.port);
      var1.addSafeString("password", this.password);
      var1.addBoolean("allowConnectByIP", this.allowConnectByIP);
      var1.addEnum("steamLobbyType", this.steamLobbyType);
   }

   public static enum SteamLobbyType {
      InviteOnly(LobbyType.Private, new LocalMessage("ui", "steamlobbyinvite")),
      VisibleToFriends(LobbyType.FriendsOnly, new LocalMessage("ui", "steamlobbyfriends")),
      Open(LobbyType.FriendsOnly, new LocalMessage("ui", "steamlobbyopen"));

      public final SteamMatchmaking.LobbyType steamLobbyType;
      public final GameMessage displayName;

      private SteamLobbyType(SteamMatchmaking.LobbyType var3, GameMessage var4) {
         this.steamLobbyType = var3;
         this.displayName = var4;
      }

      // $FF: synthetic method
      private static SteamLobbyType[] $values() {
         return new SteamLobbyType[]{InviteOnly, VisibleToFriends, Open};
      }
   }
}
