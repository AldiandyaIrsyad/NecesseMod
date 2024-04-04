package necesse.inventory.container.teams;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.customAction.LongCustomAction;
import necesse.inventory.container.customAction.StringCustomAction;

public class PvPTeamsContainer extends Container {
   public PvPCurrentTeamUpdateEvent data;
   public final LongCustomAction kickMemberAction;
   public final LongCustomAction passOwnershipAction;
   public final InviteMembersAction inviteMembersAction;
   public final StringCustomAction changeTeamNameAction;
   public final BooleanCustomAction setPublicAction;
   public final EmptyCustomAction askForExistingTeams;
   public final IntCustomAction requestToJoinTeam;
   public final EmptyCustomAction leaveTeamButton;
   public final EmptyCustomAction createTeamButton;

   public PvPTeamsContainer(final NetworkClient var1, int var2, Packet var3) {
      super(var1, var2);
      PacketReader var4 = new PacketReader(var3);
      this.data = new PvPCurrentTeamUpdateEvent(var4);
      this.subscribeEvent(PvPCurrentTeamUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(PvPCurrentTeamUpdateEvent.class, (var1x) -> {
         this.data = var1x;
      });
      this.subscribeEvent(PvPOwnerUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(PvPOwnerUpdateEvent.class, (var1x) -> {
         if (this.data.currentTeam != null) {
            this.data.currentTeam.owner = var1x.ownerAuth;
         }

      });
      this.subscribeEvent(PvPPublicUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(PvPPublicUpdateEvent.class, (var1x) -> {
         if (this.data.currentTeam != null) {
            this.data.currentTeam.isPublic = var1x.isPublic;
         }

      });
      this.subscribeEvent(PvPMemberUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(PvPMemberUpdateEvent.class, (var1x) -> {
         if (var1x.added) {
            MemberData var2 = (MemberData)this.data.members.stream().filter((var1) -> {
               return var1.auth == var1x.auth;
            }).findFirst().orElse((Object)null);
            if (var2 != null) {
               var2.name = var1x.name;
            } else {
               this.data.members.add(new MemberData(var1x.auth, var1x.name));
            }
         } else {
            this.data.members.removeIf((var1) -> {
               return var1.auth == var1x.auth;
            });
         }

      });
      this.subscribeEvent(PvPJoinRequestUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.onEvent(PvPJoinRequestUpdateEvent.class, (var1x) -> {
         if (var1x.added) {
            MemberData var2 = (MemberData)this.data.joinRequests.stream().filter((var1) -> {
               return var1.auth == var1x.auth;
            }).findFirst().orElse((Object)null);
            if (var2 != null) {
               var2.name = var1x.name;
            } else {
               this.data.joinRequests.add(new MemberData(var1x.auth, var1x.name));
            }
         } else {
            this.data.joinRequests.removeIf((var1) -> {
               return var1.auth == var1x.auth;
            });
         }

      });
      this.subscribeEvent(PvPAllTeamsUpdateEvent.class, (var0) -> {
         return true;
      }, () -> {
         return true;
      });
      this.kickMemberAction = (LongCustomAction)this.registerAction(new LongCustomAction() {
         protected void run(long var1x) {
            if (var1.isServer()) {
               ServerClient var3 = var1.getServerClient();
               PlayerTeam var4 = var3.getPlayerTeam();
               if (var4 != null && var4.getOwner() == var3.authentication) {
                  PlayerTeam.removeMember(var3.getServer(), var4, var1x, true);
               }
            }

         }
      });
      this.passOwnershipAction = (LongCustomAction)this.registerAction(new LongCustomAction() {
         protected void run(long var1x) {
            if (var1.isServer()) {
               ServerClient var3 = var1.getServerClient();
               PlayerTeam var4 = var3.getPlayerTeam();
               if (var4 != null && var4.getOwner() == var3.authentication) {
                  PlayerTeam.changeOwner(var3.getServer(), var4, var1x);
               }
            }

         }
      });
      this.inviteMembersAction = (InviteMembersAction)this.registerAction(new InviteMembersAction(this));
      this.changeTeamNameAction = (StringCustomAction)this.registerAction(new StringCustomAction() {
         protected void run(String var1x) {
            if (var1.isServer()) {
               ServerClient var2 = var1.getServerClient();
               PlayerTeam var3 = var2.getPlayerTeam();
               if (var3 != null && var3.getOwner() == var2.authentication && !var3.getName().equals(var1x)) {
                  var3.setName(var1x);
                  PvPTeamsContainer.sendUpdates(var2, var3);
               }
            }

         }
      });
      this.setPublicAction = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1x) {
            if (var1.isServer()) {
               ServerClient var2 = var1.getServerClient();
               PlayerTeam var3 = var2.getPlayerTeam();
               if (var3 != null && var3.getOwner() == var2.authentication) {
                  PlayerTeam.changePublic(var2.getServer(), var3, var1x);
               }
            }

         }
      });
      this.askForExistingTeams = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               ServerClient var1x = var1.getServerClient();
               (new PvPAllTeamsUpdateEvent(var1x.getServer())).applyAndSendToClient(var1x);
            }

         }
      });
      this.requestToJoinTeam = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            if (var1.isServer()) {
               ServerClient var2 = var1.getServerClient();
               PlayerTeam var3 = var2.getServer().world.getTeams().getTeam(var1x);
               if (var3 != null) {
                  if (var3.isPublic()) {
                     PlayerTeam.addMember(var2.getServer(), var3, var2.authentication);
                  } else {
                     PlayerTeam.addJoinRequest(var2.getServer(), var3, var2.authentication);
                  }
               }
            }

         }
      });
      this.leaveTeamButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               ServerClient var1x = var1.getServerClient();
               PlayerTeam.removeMember(var1x.getServer(), var1x.getPlayerTeam(), var1x.authentication, false);
            }

         }
      });
      this.createTeamButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
         protected void run() {
            if (var1.isServer()) {
               ServerClient var1x = var1.getServerClient();
               if (var1x.getTeamID() == -1) {
                  Server var2 = var1x.getServer();
                  var2.world.getTeams().createNewTeam(var1x);
               }
            }

         }
      });
   }

   public static Packet getContainerContent(ServerClient var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      (new PvPCurrentTeamUpdateEvent(var0)).write(var2);
      return var1;
   }

   public static void sendSingleUpdate(ServerClient var0) {
      (new PvPCurrentTeamUpdateEvent(var0)).applyAndSendToClient(var0);
   }

   public static void sendUpdates(ServerClient var0, PlayerTeam var1) {
      var0.getServer().streamClients().filter((var2) -> {
         return var2 == var0 || var2.getTeamID() == var1.teamID;
      }).forEach((var0x) -> {
         (new PvPCurrentTeamUpdateEvent(var0x)).applyAndSendToClient(var0x);
      });
   }

   public static void sendUpdates(Server var0, PlayerTeam var1) {
      var0.streamClients().filter((var1x) -> {
         return var1x.getTeamID() == var1.teamID;
      }).forEach((var0x) -> {
         (new PvPCurrentTeamUpdateEvent(var0x)).applyAndSendToClient(var0x);
      });
   }

   public static class MemberData {
      public long auth;
      public String name;

      public MemberData(PacketReader var1) {
         this.auth = var1.getNextLong();
         this.name = var1.getNextString();
      }

      public MemberData(long var1, String var3) {
         this.auth = var1;
         this.name = var3;
      }

      public MemberData(Server var1, long var2) {
         this.auth = var2;
         this.name = var1.getNameByAuth(var2, "Unknown");
      }

      public void writeToPacket(PacketWriter var1) {
         var1.putNextLong(this.auth);
         var1.putNextString(this.name);
      }
   }

   public static class TeamData {
      public int teamID;
      public String name;
      public long owner;
      public boolean isPublic;
      public int memberCount;

      public TeamData(PlayerTeam var1) {
         this.teamID = var1.teamID;
         this.name = var1.getName();
         this.owner = var1.getOwner();
         this.isPublic = var1.isPublic();
         this.memberCount = var1.getMemberCount();
      }

      public TeamData(PacketReader var1) {
         this.teamID = var1.getNextShort();
         this.name = var1.getNextString();
         this.owner = var1.getNextLong();
         this.isPublic = var1.getNextBoolean();
         this.memberCount = var1.getNextInt();
      }

      public void writeToPacket(PacketWriter var1) {
         var1.putNextShort((short)this.teamID);
         var1.putNextString(this.name);
         var1.putNextLong(this.owner);
         var1.putNextBoolean(this.isPublic);
         var1.putNextInt(this.memberCount);
      }
   }

   public static class InviteData {
      public int teamID;
      public String teamName;

      public InviteData(PlayerTeam var1) {
         this.teamID = var1.teamID;
         this.teamName = var1.getName();
      }

      public InviteData(PacketReader var1) {
         this.teamID = var1.getNextShort();
         this.teamName = var1.getNextString();
      }

      public void writeToPacket(PacketWriter var1) {
         var1.putNextShort((short)this.teamID);
         var1.putNextString(this.teamName);
      }
   }
}
