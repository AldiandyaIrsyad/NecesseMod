package necesse.inventory.container.settlement.actions;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketPlayerTeamRequestReceive;
import necesse.engine.network.server.ServerClient;
import necesse.engine.team.PlayerTeam;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.level.maps.layers.SettlementLevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class RequestJoinSettlementAction extends BooleanCustomAction {
   public final SettlementContainer container;

   public RequestJoinSettlementAction(SettlementContainer var1) {
      this.container = var1;
   }

   protected void run(boolean var1) {
      if (this.container.client.isServer()) {
         ServerClient var2 = this.container.client.getServerClient();
         SettlementLevelLayer var3 = this.container.getLevelLayer();
         long var4 = var3.getOwnerAuth();
         if (var4 != -1L) {
            int var6 = var3.getTeamID();
            PlayerTeam var7 = var6 == -1 ? null : var2.getServer().world.getTeams().getTeam(var6);
            SettlementLevelData var8;
            if (var7 == null) {
               if (var1) {
                  var8 = this.container.getLevelData();
                  if (var8 != null) {
                     (new SettlementBasicsEvent(var8)).applyAndSendToClient(var2);
                  }
               }

               ServerClient var9 = var2.getServer().getClientByAuth(var4);
               if (var9 != null) {
                  var9.joinRequests.add(var2.authentication);
                  var9.sendPacket(new PacketPlayerTeamRequestReceive(var2.authentication, var2.getName()));
               } else {
                  var2.sendChatMessage((GameMessage)(new LocalMessage("ui", "teamownerisnotonline")));
               }
            } else {
               if (var7.isPublic()) {
                  PlayerTeam.addMember(var2.getServer(), var7, var2.authentication);
               } else {
                  PlayerTeam.addJoinRequest(var2.getServer(), var7, var2.authentication);
               }

               if (var7.isPublic() != var1) {
                  var8 = this.container.getLevelData();
                  if (var8 != null) {
                     (new SettlementBasicsEvent(var8)).applyAndSendToClient(var2);
                  }
               }
            }
         }
      }

   }
}
