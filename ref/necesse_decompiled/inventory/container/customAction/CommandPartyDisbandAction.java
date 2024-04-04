package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.world.worldData.SettlersWorldData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;

public class CommandPartyDisbandAction extends CommandPartyCustomAction {
   public CommandPartyDisbandAction(Container var1) {
      super(var1);
   }

   public void runAndSend(Collection<Integer> var1) {
      PacketWriter var2 = this.setupPacket(var1);
      this.runAndSendAction(var2.getPacket());
   }

   public void executePacket(PacketReader var1, ServerClient var2, ArrayList<HumanMob> var3) {
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         HumanMob var5 = (HumanMob)var4.next();
         var5.adventureParty.clear(true);
         if (var5.isSettlerOnCurrentLevel()) {
            var5.clearCommandsOrders(var2);
         } else {
            var5.commandGuard((ServerClient)null, var5.getX(), var5.getY());
            if (var5.getHealthPercent() <= 0.5F) {
               SettlersWorldData var6 = SettlersWorldData.getSettlersData(var5.getLevel().getServer());
               var6.returnToSettlement(var5, false);
            }
         }
      }

   }
}
