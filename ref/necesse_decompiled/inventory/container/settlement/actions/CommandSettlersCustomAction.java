package necesse.inventory.container.settlement.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public abstract class CommandSettlersCustomAction extends SettlementAccessRequiredContainerCustomAction {
   public CommandSettlersCustomAction(SettlementContainer var1) {
      super(var1);
   }

   protected PacketWriter setupPacket(Collection<Integer> var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextShortUnsigned(var1.size());
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         int var5 = (Integer)var4.next();
         var3.putNextInt(var5);
      }

      return var3;
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextShortUnsigned();
      ArrayList var5 = new ArrayList(var4);
      boolean var6 = false;

      for(int var7 = 0; var7 < var4; ++var7) {
         int var8 = var1.getNextInt();
         Mob var9 = (Mob)var2.getLevel().entityManager.mobs.get(var8, false);
         if (var9 instanceof CommandMob && ((CommandMob)var9).canBeCommanded(var3)) {
            var5.add((CommandMob)var9);
         } else {
            var6 = true;
         }
      }

      this.executePacket(var1, var2, var3, var5);
      if (var6) {
         (new SettlementSettlersChangedEvent(var2)).applyAndSendToClient(var3);
      }

   }

   public abstract void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3, ArrayList<CommandMob> var4);
}
