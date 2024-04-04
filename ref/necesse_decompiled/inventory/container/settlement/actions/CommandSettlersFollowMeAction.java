package necesse.inventory.container.settlement.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class CommandSettlersFollowMeAction extends CommandSettlersCustomAction {
   public CommandSettlersFollowMeAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(Collection<Integer> var1) {
      PacketWriter var2 = this.setupPacket(var1);
      this.runAndSendAction(var2.getPacket());
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3, ArrayList<CommandMob> var4) {
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         CommandMob var6 = (CommandMob)var5.next();
         var6.commandFollow(var3, var3.playerMob);
      }

   }
}
