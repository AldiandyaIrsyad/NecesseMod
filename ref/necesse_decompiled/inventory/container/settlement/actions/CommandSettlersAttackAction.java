package necesse.inventory.container.settlement.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public class CommandSettlersAttackAction extends CommandSettlersCustomAction {
   public CommandSettlersAttackAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(Collection<Integer> var1, Mob var2) {
      PacketWriter var3 = this.setupPacket(var1);
      var3.putNextInt(var2.getUniqueID());
      this.runAndSendAction(var3.getPacket());
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3, ArrayList<CommandMob> var4) {
      int var5 = var1.getNextInt();
      Mob var6 = GameUtils.getLevelMob(var5, var3.getLevel());
      if (var6 != null) {
         Iterator var7 = var4.iterator();

         while(var7.hasNext()) {
            CommandMob var8 = (CommandMob)var7.next();
            var8.commandAttack(var3, var6);
         }
      } else {
         var3.sendPacket(new PacketRemoveMob(var5));
      }

   }
}
