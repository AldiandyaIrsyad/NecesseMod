package necesse.inventory.container.teams;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.events.ContainerEvent;

public class PvPCurrentTeamUpdateEvent extends ContainerEvent {
   public PvPTeamsContainer.TeamData currentTeam;
   public final ArrayList<PvPTeamsContainer.MemberData> members = new ArrayList();
   public final ArrayList<PvPTeamsContainer.MemberData> joinRequests = new ArrayList();
   public final ArrayList<PvPTeamsContainer.InviteData> invites = new ArrayList();

   public PvPCurrentTeamUpdateEvent(ServerClient var1) {
      PlayerTeam var2 = var1.getPlayerTeam();
      Iterator var3;
      if (var2 != null) {
         this.currentTeam = new PvPTeamsContainer.TeamData(var2);
         this.members.ensureCapacity(var2.getMemberCount());
         var3 = var2.getMembers().iterator();

         Long var4;
         while(var3.hasNext()) {
            var4 = (Long)var3.next();
            this.members.add(new PvPTeamsContainer.MemberData(var1.getServer(), var4));
         }

         this.joinRequests.ensureCapacity(var2.getJoinRequestsCount());
         var3 = var2.getJoinRequests().iterator();

         while(var3.hasNext()) {
            var4 = (Long)var3.next();
            this.joinRequests.add(new PvPTeamsContainer.MemberData(var1.getServer(), var4));
         }
      }

      this.invites.ensureCapacity(var1.teamInvites.size());
      var3 = var1.teamInvites.iterator();

      while(var3.hasNext()) {
         Integer var6 = (Integer)var3.next();
         PlayerTeam var5 = var1.getServer().world.getTeams().getTeam(var6);
         if (var5 != null) {
            this.invites.add(new PvPTeamsContainer.InviteData(var5));
         }
      }

   }

   public PvPCurrentTeamUpdateEvent(PacketReader var1) {
      super(var1);
      int var2;
      int var3;
      if (var1.getNextBoolean()) {
         this.currentTeam = new PvPTeamsContainer.TeamData(var1);
         var2 = var1.getNextShortUnsigned();

         for(var3 = 0; var3 < var2; ++var3) {
            this.members.add(new PvPTeamsContainer.MemberData(var1));
         }

         var3 = var1.getNextShortUnsigned();

         for(int var4 = 0; var4 < var3; ++var4) {
            this.joinRequests.add(new PvPTeamsContainer.MemberData(var1));
         }
      } else {
         this.currentTeam = null;
      }

      var2 = var1.getNextShortUnsigned();

      for(var3 = 0; var3 < var2; ++var3) {
         this.invites.add(new PvPTeamsContainer.InviteData(var1));
      }

   }

   public void write(PacketWriter var1) {
      Iterator var2;
      if (this.currentTeam != null) {
         var1.putNextBoolean(true);
         this.currentTeam.writeToPacket(var1);
         var1.putNextShortUnsigned(this.members.size());
         var2 = this.members.iterator();

         PvPTeamsContainer.MemberData var3;
         while(var2.hasNext()) {
            var3 = (PvPTeamsContainer.MemberData)var2.next();
            var3.writeToPacket(var1);
         }

         var1.putNextShortUnsigned(this.joinRequests.size());
         var2 = this.joinRequests.iterator();

         while(var2.hasNext()) {
            var3 = (PvPTeamsContainer.MemberData)var2.next();
            var3.writeToPacket(var1);
         }
      } else {
         var1.putNextBoolean(false);
      }

      var1.putNextShortUnsigned(this.invites.size());
      var2 = this.invites.iterator();

      while(var2.hasNext()) {
         PvPTeamsContainer.InviteData var4 = (PvPTeamsContainer.InviteData)var2.next();
         var4.writeToPacket(var1);
      }

   }
}
