package necesse.inventory.container.settlement.events;

import necesse.engine.GameAuth;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SettlementBasicsEvent extends ContainerEvent {
   public final boolean isPrivate;
   public final GameMessage settlementName;
   public final long ownerAuth;
   public final String ownerName;
   public final int teamID;
   public final boolean isTeamPublic;

   public SettlementBasicsEvent(SettlementLevelData var1) {
      SettlementLevelLayer var2 = var1.getLevel().settlementLayer;
      this.isPrivate = var2.isPrivate();
      this.settlementName = var2.getSettlementName();
      this.ownerAuth = var2.getOwnerAuth();
      this.ownerName = var2.getOwnerName();
      this.teamID = var2.getTeamID();
      if (this.teamID != -1 && var1.isServer()) {
         PlayerTeam var3 = var1.getLevel().getServer().world.getTeams().getTeam(this.teamID);
         this.isTeamPublic = var3 != null && var3.isPublic();
      } else {
         this.isTeamPublic = false;
      }

   }

   public SettlementBasicsEvent(PacketReader var1) {
      super(var1);
      this.settlementName = GameMessage.fromContentPacket(var1.getNextContentPacket());
      this.isPrivate = var1.getNextBoolean();
      this.ownerAuth = var1.getNextLong();
      this.ownerName = var1.getNextString();
      this.teamID = var1.getNextInt();
      this.isTeamPublic = var1.getNextBoolean();
   }

   public void write(PacketWriter var1) {
      var1.putNextContentPacket(this.settlementName.getContentPacket());
      var1.putNextBoolean(this.isPrivate);
      var1.putNextLong(this.ownerAuth);
      var1.putNextString(this.ownerName);
      var1.putNextInt(this.teamID);
      var1.putNextBoolean(this.isTeamPublic);
   }

   public boolean hasOwner() {
      return this.ownerAuth != -1L;
   }

   public boolean isOwner(ServerClient var1) {
      return this.ownerAuth == var1.authentication;
   }

   public boolean hasAccess(ServerClient var1) {
      return !this.isPrivate || !this.hasOwner() || this.isOwner(var1) || this.teamID != -1 && this.teamID == var1.getTeamID();
   }

   public boolean isOwner(Client var1) {
      return this.ownerAuth == GameAuth.getAuthentication();
   }

   public boolean hasAccess(Client var1) {
      return !this.isPrivate || !this.hasOwner() || this.isOwner(var1) || this.teamID != -1 && this.teamID == var1.getTeam();
   }
}
