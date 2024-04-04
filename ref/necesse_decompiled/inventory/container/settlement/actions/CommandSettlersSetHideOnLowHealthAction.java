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

public class CommandSettlersSetHideOnLowHealthAction extends CommandSettlersCustomAction {
   public CommandSettlersSetHideOnLowHealthAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(Collection<Integer> var1, boolean var2) {
      PacketWriter var3 = this.setupPacket(var1);
      var3.putNextBoolean(var2);
      this.runAndSendAction(var3.getPacket());
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3, ArrayList<CommandMob> var4) {
      boolean var5 = var1.getNextBoolean();
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         CommandMob var7 = (CommandMob)var6.next();
         var7.setHideOnLowHealth(var5);
      }

   }
}
