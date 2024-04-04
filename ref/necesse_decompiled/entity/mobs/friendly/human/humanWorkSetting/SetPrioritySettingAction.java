package necesse.entity.mobs.friendly.human.humanWorkSetting;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.job.JobType;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SetPrioritySettingAction extends ContainerCustomAction {
   public final ShopContainer container;

   public SetPrioritySettingAction(ShopContainer var1) {
      this.container = var1;
   }

   public void runAndSendChange(JobType var1, int var2, boolean var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextShortUnsigned(var1.getID());
      var5.putNextInt(var2);
      var5.putNextBoolean(var3);
      this.runAndSendAction(var4);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();
      int var3 = var1.getNextInt();
      boolean var4 = var1.getNextBoolean();
      if (this.container.client.isServer()) {
         LevelSettler var5 = this.container.humanShop.levelSettler;
         if (var5 != null) {
            JobTypeHandler.TypePriority var6 = this.container.humanShop.jobTypeHandler.getPriority(var2);
            if (var6 != null) {
               var6.priority = var3;
               var6.disabledByPlayer = var4;
               (new SettlementSettlerPrioritiesChangedEvent(this.container.humanShop.getUniqueID(), var6.type, var3, var4)).applyAndSendToClientsAt(this.container.client.getServerClient());
            }
         }
      }

   }
}
