package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SetNewSettlerEquipmentFilterAction extends SettlementAccessRequiredContainerCustomAction {
   public SetNewSettlerEquipmentFilterAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSendSelfManageEquipment(boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(0, 2);
      var3.putNextBoolean(var1);
      this.runAndSendAction(var2);
   }

   public void runAndSendPreferArmorSets(boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(1, 2);
      var3.putNextBoolean(var1);
      this.runAndSendAction(var2);
   }

   public void runAndSendChange(ItemCategoriesFilterChange var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(2, 2);
      var1.write(var3);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextMaxValue(2);
      switch (var4) {
         case 0:
            boolean var5 = var1.getNextBoolean();
            if (var5 != var2.newSettlerSelfManageEquipment) {
               var2.newSettlerSelfManageEquipment = var5;
               (new SettlementNewSettlerEquipmentFilterChangedEvent(var2.newSettlerSelfManageEquipment, var2.newSettlerEquipmentPreferArmorSets, (ItemCategoriesFilterChange)null)).applyAndSendToClientsAt(var3);
            }
            break;
         case 1:
            boolean var6 = var1.getNextBoolean();
            if (var6 != var2.newSettlerEquipmentPreferArmorSets) {
               var2.newSettlerEquipmentPreferArmorSets = var6;
               (new SettlementNewSettlerEquipmentFilterChangedEvent(var2.newSettlerSelfManageEquipment, var2.newSettlerEquipmentPreferArmorSets, (ItemCategoriesFilterChange)null)).applyAndSendToClientsAt(var3);
            }
            break;
         case 2:
            ItemCategoriesFilterChange var7 = ItemCategoriesFilterChange.fromPacket(var1);
            ItemCategoriesFilter var8 = var2.getNewSettlerEquipmentFilter();
            if (var7.applyTo(var8)) {
               (new SettlementNewSettlerEquipmentFilterChangedEvent(var2.newSettlerSelfManageEquipment, var2.newSettlerEquipmentPreferArmorSets, var7)).applyAndSendToClientsAt(var3);
            }
      }

   }
}
