package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SetSettlerSelfManageEquipmentAction extends SettlementAccessRequiredContainerCustomAction {
   public SetSettlerSelfManageEquipmentAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSend(int var1, boolean var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextBoolean(var2);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      LevelSettler var5 = var2.getSettler(var4);
      if (var5 != null) {
         SettlerMob var6 = var5.getMob();
         if (var6 instanceof HumanMob) {
            ((HumanMob)var6).selfManageEquipment.set(var1.getNextBoolean());
         }
      } else {
         (new SettlementSettlersChangedEvent(var2)).applyAndSendToClient(var3);
      }

   }
}
