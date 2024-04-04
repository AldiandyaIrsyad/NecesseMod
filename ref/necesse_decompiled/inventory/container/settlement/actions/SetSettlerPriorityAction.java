package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobType;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SetSettlerPriorityAction extends SettlementAccessRequiredContainerCustomAction {
   public SetSettlerPriorityAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1, JobType var2, int var3, boolean var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextInt(var1);
      var6.putNextShortUnsigned(var2.getID());
      var6.putNextInt(var3);
      var6.putNextBoolean(var4);
      this.runAndSendAction(var5);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      int var5 = var1.getNextShortUnsigned();
      int var6 = var1.getNextInt();
      boolean var7 = var1.getNextBoolean();
      Mob var8 = (Mob)var2.getLevel().entityManager.mobs.get(var4, false);
      if (var8 instanceof EntityJobWorker) {
         EntityJobWorker var9 = (EntityJobWorker)var8;
         JobTypeHandler.TypePriority var10 = var9.getJobTypeHandler().getPriority(var5);
         if (var10 != null) {
            var10.priority = var6;
            var10.disabledByPlayer = var7;
            (new SettlementSettlerPrioritiesChangedEvent(var4, var10.type, var10.priority, var10.disabledByPlayer)).applyAndSendToClientsAt(var3);
         }
      } else {
         (new SettlementSettlersChangedEvent(var2)).applyAndSendToClient(var3);
      }

   }
}
