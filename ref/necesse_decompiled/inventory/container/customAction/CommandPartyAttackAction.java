package necesse.inventory.container.customAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.Container;

public class CommandPartyAttackAction extends CommandPartyCustomAction {
   public CommandPartyAttackAction(Container var1) {
      super(var1);
   }

   public void runAndSend(Collection<Integer> var1, Mob var2) {
      PacketWriter var3 = this.setupPacket(var1);
      var3.putNextInt(var2.getUniqueID());
      this.runAndSendAction(var3.getPacket());
   }

   public void executePacket(PacketReader var1, ServerClient var2, ArrayList<HumanMob> var3) {
      int var4 = var1.getNextInt();
      Mob var5 = GameUtils.getLevelMob(var4, var2.getLevel());
      if (var5 != null) {
         Iterator var6 = var3.iterator();

         while(var6.hasNext()) {
            HumanMob var7 = (HumanMob)var6.next();
            var7.commandAttack(var2, var5);
         }
      } else {
         var2.sendPacket(new PacketRemoveMob(var4));
      }

   }
}
