package necesse.engine.network.packet;

import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.steam.SteamData;
import necesse.engine.world.WorldSettings;
import necesse.gfx.HumanLook;

public class PacketConnectApproved extends Packet {
   public final long sessionID;
   public final int slot;
   public final int slots;
   public final long uniqueID;
   public final boolean characterSelect;
   public final int serverCharacterUniqueID;
   public final HumanLook serverCharacterAppearance;
   public final Packet serverCharacterLookContent;
   public final String serverCharacterName;
   public final boolean needAppearance;
   public final PermissionLevel permissionLevel;
   public final ServerSettings.SteamLobbyType steamLobbyType;
   public final boolean allowClientsPower;
   public final Packet activeSeasonsContent;
   public final Packet worldSettingsContent;

   public PacketConnectApproved(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.sessionID = var2.getNextLong();
      this.slot = var2.getNextByteUnsigned();
      this.slots = var2.getNextByteUnsigned();
      this.uniqueID = var2.getNextLong();
      this.characterSelect = var2.getNextBoolean();
      this.needAppearance = var2.getNextBoolean();
      if (this.characterSelect && !this.needAppearance) {
         this.serverCharacterUniqueID = var2.getNextInt();
         this.serverCharacterAppearance = new HumanLook(var2);
         this.serverCharacterLookContent = var2.getNextContentPacket();
         this.serverCharacterName = var2.getNextString();
      } else {
         this.serverCharacterUniqueID = 0;
         this.serverCharacterAppearance = null;
         this.serverCharacterLookContent = null;
         this.serverCharacterName = null;
      }

      this.permissionLevel = PermissionLevel.getLevel(var2.getNextByteUnsigned());
      if (var2.getNextBoolean()) {
         this.steamLobbyType = ServerSettings.SteamLobbyType.values()[var2.getNextByteUnsigned()];
      } else {
         this.steamLobbyType = null;
      }

      this.allowClientsPower = var2.getNextBoolean();
      this.activeSeasonsContent = var2.getNextContentPacket();
      this.worldSettingsContent = var2.getNextContentPacket();
   }

   public PacketConnectApproved(Server var1, ServerClient var2) {
      this.sessionID = var2.getSessionID();
      this.slot = var2.slot;
      this.slots = var1.getSlots();
      this.uniqueID = var1.world.getUniqueID();
      this.characterSelect = var1.world.settings.allowOutsideCharacters;
      this.needAppearance = var2.needAppearance();
      if (this.characterSelect && !this.needAppearance) {
         this.serverCharacterUniqueID = var2.getCharacterUniqueID();
         this.serverCharacterAppearance = var2.playerMob.look;
         this.serverCharacterLookContent = new Packet();
         var2.playerMob.getInv().setupLookContentPacket(new PacketWriter(this.serverCharacterLookContent));
         this.serverCharacterName = var2.getName();
      } else {
         this.serverCharacterUniqueID = 0;
         this.serverCharacterAppearance = null;
         this.serverCharacterLookContent = null;
         this.serverCharacterName = null;
      }

      this.permissionLevel = var2.getPermissionLevel();
      this.steamLobbyType = SteamData.isCreated() ? var1.settings.steamLobbyType : null;
      this.allowClientsPower = Settings.giveClientsPower;
      this.activeSeasonsContent = new Packet();
      GameSeasons.writeSeasons(new PacketWriter(this.activeSeasonsContent));
      this.worldSettingsContent = new Packet();
      var1.world.settings.setupContentPacket(new PacketWriter(this.worldSettingsContent));
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextLong(this.sessionID);
      var3.putNextByteUnsigned(this.slot);
      var3.putNextByteUnsigned(this.slots);
      var3.putNextLong(this.uniqueID);
      var3.putNextBoolean(this.characterSelect);
      var3.putNextBoolean(this.needAppearance);
      if (this.characterSelect && !this.needAppearance) {
         var3.putNextInt(this.serverCharacterUniqueID);
         this.serverCharacterAppearance.setupContentPacket(var3, true);
         var3.putNextContentPacket(this.serverCharacterLookContent);
         var3.putNextString(this.serverCharacterName);
      }

      var3.putNextByteUnsigned(this.permissionLevel.getLevel());
      if (this.steamLobbyType != null) {
         var3.putNextBoolean(true);
         var3.putNextByteUnsigned(this.steamLobbyType.ordinal());
      } else {
         var3.putNextBoolean(false);
      }

      var3.putNextBoolean(this.allowClientsPower);
      var3.putNextContentPacket(this.activeSeasonsContent);
      var3.putNextContentPacket(this.worldSettingsContent);
   }

   public WorldSettings getWorldSettings(Client var1) {
      return new WorldSettings(var1, new PacketReader(this.worldSettingsContent));
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.submitConnectionPacket(this);
   }
}
