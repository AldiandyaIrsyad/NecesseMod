package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SetSettlerEquipmentFilterAction extends SettlementAccessRequiredContainerCustomAction {
   public SetSettlerEquipmentFilterAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSendPreferArmorSets(int var1, boolean var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextMaxValue(0, 1);
      var4.putNextBoolean(var2);
      this.runAndSendAction(var3);
   }

   public void runAndSendChange(int var1, ItemCategoriesFilterChange var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var4.putNextMaxValue(1, 1);
      var2.write(var4);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      LevelSettler var5 = var2.getSettler(var4);
      if (var5 != null) {
         int var6 = var1.getNextMaxValue(1);
         switch (var6) {
            case 0:
               boolean var7 = var1.getNextBoolean();
               if (var5.preferArmorSets != var7) {
                  var5.preferArmorSets = var7;
                  (new SettlementSettlerEquipmentFilterChangedEvent(var4, var5.preferArmorSets, (ItemCategoriesFilterChange)null)).applyAndSendToClientsAt(var3);
               }
               break;
            case 1:
               ItemCategoriesFilterChange var8 = ItemCategoriesFilterChange.fromPacket(var1);
               if (var8.applyTo(var5.equipmentFilter)) {
                  (new SettlementSettlerEquipmentFilterChangedEvent(var4, var5.preferArmorSets, var8)).applyAndSendToClientsAt(var3);
               }
         }
      } else {
         (new SettlementSettlersChangedEvent(var2)).applyAndSendToClient(var3);
      }

   }
}
