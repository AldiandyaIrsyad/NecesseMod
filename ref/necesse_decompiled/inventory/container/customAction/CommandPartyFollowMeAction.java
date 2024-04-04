package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;

public class CommandPartyFollowMeAction extends CommandPartyCustomAction {
   public CommandPartyFollowMeAction(Container var1) {
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
         var5.commandFollow(var2, var2.playerMob);
      }

   }
}
