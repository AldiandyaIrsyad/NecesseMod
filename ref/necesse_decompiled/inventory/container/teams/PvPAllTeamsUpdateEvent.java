package necesse.inventory.container.teams;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.events.ContainerEvent;

public class PvPAllTeamsUpdateEvent extends ContainerEvent {
   public final ArrayList<PvPTeamsContainer.TeamData> teams = new ArrayList();

   public PvPAllTeamsUpdateEvent(Server var1) {
      Iterator var2 = var1.world.getTeams().getTeams().iterator();

      while(var2.hasNext()) {
         PlayerTeam var3 = (PlayerTeam)var2.next();
         if (var3.hasMembers()) {
            this.teams.add(new PvPTeamsContainer.TeamData(var3));
         }
      }

   }

   public PvPAllTeamsUpdateEvent(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.teams.add(new PvPTeamsContainer.TeamData(var1));
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextShortUnsigned(this.teams.size());
      Iterator var2 = this.teams.iterator();

      while(var2.hasNext()) {
         PvPTeamsContainer.TeamData var3 = (PvPTeamsContainer.TeamData)var2.next();
         var3.writeToPacket(var1);
      }

   }
}
