package necesse.engine.team;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.network.packet.PacketPlayerJoinedTeam;
import necesse.engine.network.packet.PacketPlayerLeftTeam;
import necesse.engine.network.packet.PacketPlayerTeamInviteReceive;
import necesse.engine.network.packet.PacketPlayerTeamRequestReceive;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.levelCache.SettlementCache;
import necesse.inventory.container.teams.PvPCurrentTeamUpdateEvent;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;
import necesse.inventory.container.teams.PvPMemberUpdateEvent;
import necesse.inventory.container.teams.PvPOwnerUpdateEvent;
import necesse.inventory.container.teams.PvPPublicUpdateEvent;
import necesse.inventory.container.teams.PvPTeamsContainer;
import necesse.level.maps.Level;

public class PlayerTeam {
   public final TeamManager manager;
   public final int teamID;
   private String name;
   private long owner;
   private boolean isPublic = true;
   private HashSet<Long> members = new HashSet();
   HashSet<Long> joinRequests = new HashSet();

   PlayerTeam(TeamManager var1, int var2, ServerClient var3) {
      this.manager = var1;
      this.teamID = var2;
      this.name = var3.getName() + "'s team";
      this.owner = var3.authentication;
      this.members.add(this.owner);
   }

   PlayerTeam(TeamManager var1, int var2, LoadData var3) {
      this.manager = var1;
      this.teamID = var2;
      this.name = var3.getSafeString("name", "Unknown team");
      this.owner = var3.getLong("owner", -1L);
      this.isPublic = var3.getBoolean("isPublic", this.isPublic, false);
      long[] var4 = var3.getLongArray("members", new long[0]);
      long[] var5 = var4;
      int var6 = var4.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         long var8 = var5[var7];
         this.members.add(var8);
      }

   }

   public void addSaveData(SaveData var1) {
      var1.addInt("teamID", this.teamID);
      var1.addSafeString("name", this.name);
      var1.addLong("owner", this.owner);
      var1.addBoolean("isPublic", this.isPublic);
      var1.addLongArray("members", this.members.stream().filter(Objects::nonNull).mapToLong(Long::longValue).toArray());
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public boolean hasMembers() {
      return !this.members.isEmpty();
   }

   public int getMemberCount() {
      return this.members.size();
   }

   public boolean isMember(long var1) {
      return this.members.contains(var1);
   }

   void addMember(long var1) {
      this.manager.authToTeamID.put(var1, this.teamID);
      this.members.add(var1);
      if (this.owner == -1L) {
         this.owner = var1;
      }

      this.joinRequests.remove(var1);
      this.manager.clearJoinRequests(var1);
   }

   void removeMember(long var1) {
      this.manager.authToTeamID.remove(var1);
      this.members.remove(var1);
      if (this.owner == var1) {
         this.owner = -1L;
         Iterator var3 = this.members.iterator();
         if (var3.hasNext()) {
            long var4 = (Long)var3.next();
            this.owner = var4;
         }
      }

   }

   void addJoinRequest(long var1) {
      this.manager.authToTeamIDJoinRequests.add(var1, this.teamID);
      this.joinRequests.add(var1);
   }

   void removeJoinRequest(long var1) {
      ((HashSet)this.manager.authToTeamIDJoinRequests.get(var1)).remove(this.teamID);
      this.joinRequests.remove(var1);
   }

   public int getJoinRequestsCount() {
      return this.joinRequests.size();
   }

   public long getOwner() {
      return this.owner;
   }

   private void changeOwner(long var1) {
      this.owner = var1;
   }

   private void changePublic(boolean var1) {
      this.isPublic = var1;
   }

   public boolean isPublic() {
      return this.isPublic;
   }

   public Iterable<Long> getMembers() {
      return this.members;
   }

   public Stream<Long> streamMembers() {
      return this.members.stream();
   }

   public boolean hasJoinRequested(long var1) {
      return this.joinRequests.contains(var1);
   }

   public Iterable<Long> getJoinRequests() {
      return this.joinRequests;
   }

   public Stream<Long> streamJoinRequests() {
      return this.joinRequests.stream();
   }

   public Stream<ServerClient> streamOnlineMembers(Server var1) {
      if (var1 == null) {
         return Stream.empty();
      } else {
         Stream var10000 = this.streamMembers();
         Objects.requireNonNull(var1);
         return var10000.map(var1::getClientByAuth).filter(Objects::nonNull);
      }
   }

   public static void addMember(Server var0, PlayerTeam var1, long var2) {
      PlayerTeam var4 = var0.world.getTeams().getPlayerTeam(var2);
      if (var4 != null) {
         removeMember(var0, var4, var2, false);
      }

      ServerClient var5 = var0.getClientByAuth(var2);
      var1.addMember(var2);
      if (var5 != null) {
         var5.setTeamID(var1.teamID);
         var5.joinRequests.clear();
         PvPTeamsContainer.sendSingleUpdate(var5);
         var5.getLevel().settlementLayer.markDirty();
      }

      PacketPlayerJoinedTeam var6 = new PacketPlayerJoinedTeam(var0, var2);
      (new PvPMemberUpdateEvent(var2, true, var6.name)).applyAndSendToClients(var0, (var1x) -> {
         return var1x.getTeamID() == var1.teamID;
      });
      var0.network.sendToAllClients(var6);
      onUpdateTeam(var0, var2, var1.teamID);
   }

   public static void removeMember(Server var0, PlayerTeam var1, long var2, boolean var4) {
      ServerClient var5 = var0.getClientByAuth(var2);
      boolean var6 = var1.owner == var2;
      var1.removeMember(var2);
      if (var5 != null) {
         var5.setTeamID(-1);
         if (var6) {
            PvPTeamsContainer.sendUpdates(var5, var1);
         } else {
            (new PvPCurrentTeamUpdateEvent(var5)).applyAndSendToClient(var5);
         }
      } else if (var6) {
         PvPTeamsContainer.sendUpdates(var0, var1);
      }

      (new PvPMemberUpdateEvent(var2, false, (String)null)).applyAndSendToClients(var0, (var1x) -> {
         return var1x.getTeamID() == var1.teamID;
      });
      var0.network.sendToAllClients(new PacketPlayerLeftTeam(var0, var2, var1, var4));
      onUpdateTeam(var0, var2, -1);
   }

   public static void addJoinRequest(Server var0, PlayerTeam var1, long var2) {
      String var4 = var0.getNameByAuth(var2, "N/A");
      var1.addJoinRequest(var2);
      (new PvPJoinRequestUpdateEvent(var2, true, var4)).applyAndSendToClients(var0, (var1x) -> {
         return var1x.getTeamID() == var1.teamID;
      });
      var1.streamOnlineMembers(var0).forEach((var3) -> {
         var3.sendPacket(new PacketPlayerTeamRequestReceive(var2, var4));
      });
   }

   public static void removeJoinRequest(Server var0, PlayerTeam var1, long var2) {
      var1.removeJoinRequest(var2);
      (new PvPJoinRequestUpdateEvent(var2, false, (String)null)).applyAndSendToClients(var0, (var1x) -> {
         return var1x.getTeamID() == var1.teamID;
      });
   }

   public static void changeOwner(Server var0, PlayerTeam var1, long var2) {
      var1.changeOwner(var2);
      (new PvPOwnerUpdateEvent(var2)).applyAndSendToClients(var0, (var1x) -> {
         return var1x.getTeamID() == var1.teamID;
      });
   }

   public static void changePublic(Server var0, PlayerTeam var1, boolean var2) {
      var1.changePublic(var2);
      (new PvPPublicUpdateEvent(var2)).applyAndSendToClients(var0, (var1x) -> {
         return var1x.getTeamID() == var1.teamID;
      });
   }

   public static void invitePlayer(Server var0, PlayerTeam var1, ServerClient var2) {
      var2.teamInvites.add(var1.teamID);
      var2.sendPacket(new PacketPlayerTeamInviteReceive(var1.teamID, var1.getName()));
      PvPTeamsContainer.sendSingleUpdate(var2);
   }

   public static void onUpdateTeam(Server var0, long var1, int var3) {
      Iterator var4 = var0.levelCache.getSettlements().iterator();

      while(var4.hasNext()) {
         SettlementCache var5 = (SettlementCache)var4.next();
         if (var5.ownerAuth == var1) {
            var5.teamID = var3;
            LevelIdentifier var6 = new LevelIdentifier(var5.islandX, var5.islandY, 0);
            if (var0.world.levelManager.isLoaded(var6)) {
               Level var7 = var0.world.levelManager.getLevel(var6);
               var7.settlementLayer.updateOwnerVariables();
               var7.settlementLayer.markDirty();
            }
         }
      }

   }
}
