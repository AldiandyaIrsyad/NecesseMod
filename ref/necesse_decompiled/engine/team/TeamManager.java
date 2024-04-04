package necesse.engine.team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.HashMapSet;
import necesse.inventory.container.teams.PvPJoinRequestUpdateEvent;

public class TeamManager {
   private final Server server;
   private int nextTeamID = 0;
   private final HashMap<Integer, PlayerTeam> teams = new HashMap();
   final HashMap<Long, Integer> authToTeamID = new HashMap();
   final HashMapSet<Long, Integer> authToTeamIDJoinRequests = new HashMapSet();

   public TeamManager(Server var1) {
      this.server = var1;
   }

   public void cleanupEmptyTeams() {
      HashSet var1 = new HashSet();
      this.teams.forEach((var1x, var2) -> {
         if (!var2.hasMembers()) {
            var1.add(var1x);
         }

      });
      HashMap var10001 = this.teams;
      Objects.requireNonNull(var10001);
      var1.forEach(var10001::remove);
   }

   public void addSaveData(SaveData var1) {
      Iterator var2 = this.teams.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         PlayerTeam var4 = (PlayerTeam)var3.getValue();
         if (var4.hasMembers()) {
            SaveData var5 = new SaveData("TEAM");
            var4.addSaveData(var5);
            var1.addSaveData(var5);
         }
      }

   }

   public void applySaveData(LoadData var1) {
      this.teams.clear();
      Iterator var2 = var1.getLoadDataByName("TEAM").iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();

         try {
            PlayerTeam var4 = new PlayerTeam(this, this.getNextTeamID(), var3);
            this.teams.put(var4.teamID, var4);

            Long var6;
            for(Iterator var5 = var4.getMembers().iterator(); var5.hasNext(); this.authToTeamID.put(var6, var4.teamID)) {
               var6 = (Long)var5.next();
               int var7 = (Integer)this.authToTeamID.getOrDefault(var6, -1);
               if (var7 != -1) {
                  PlayerTeam var8 = this.getTeam(var7);
                  if (var8 != null) {
                     GameLog.warn.println(var6 + " was part of multiple teams. Removed from old team ID " + var8.teamID + ": " + var8.getName());
                     var8.removeMember(var6);
                  }
               }
            }
         } catch (Exception var9) {
            GameLog.warn.println("Error loading player team");
            var9.printStackTrace(GameLog.warn);
         }
      }

   }

   public void clearJoinRequests(long var1) {
      HashSet var3 = (HashSet)this.authToTeamIDJoinRequests.clear(var1);
      if (var3 != null) {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Integer var5 = (Integer)var4.next();
            PlayerTeam var6 = this.getTeam(var5);
            if (var6 != null) {
               var6.joinRequests.remove(var1);
               (new PvPJoinRequestUpdateEvent(var1, false, (String)null)).applyAndSendToClients(this.server, (var1x) -> {
                  return var1x.getTeamID() == var6.teamID;
               });
            }
         }
      }

   }

   private int getNextTeamID() {
      int var1 = this.nextTeamID++;
      if (this.nextTeamID < 0) {
         this.nextTeamID = 0;
      }

      return this.teams.containsKey(var1) ? this.getNextTeamID() : var1;
   }

   public PlayerTeam getTeam(int var1) {
      return (PlayerTeam)this.teams.get(var1);
   }

   public int getPlayerTeamID(long var1) {
      return (Integer)this.authToTeamID.getOrDefault(var1, -1);
   }

   public PlayerTeam getPlayerTeam(long var1) {
      int var3 = this.getPlayerTeamID(var1);
      return var3 == -1 ? null : this.getTeam(var3);
   }

   public PlayerTeam createNewTeam(ServerClient var1) {
      PlayerTeam var2 = var1.getPlayerTeam();
      if (var2 != null) {
         PlayerTeam.removeMember(this.server, var2, var1.authentication, false);
      }

      PlayerTeam var3 = new PlayerTeam(this, this.getNextTeamID(), var1);
      this.teams.put(var3.teamID, var3);
      Iterator var4 = var1.joinRequests.iterator();

      while(var4.hasNext()) {
         Long var5 = (Long)var4.next();
         var3.addJoinRequest(var5);
      }

      var1.joinRequests.clear();
      PlayerTeam.addMember(this.server, var3, var1.authentication);
      return var3;
   }

   public Iterable<PlayerTeam> getTeams() {
      return this.teams.values();
   }

   public Stream<PlayerTeam> streamTeams() {
      return this.teams.values().stream();
   }
}
