package necesse.inventory.container.mob;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SetElderEquipmentFilterAction extends ContainerCustomAction {
   public final ElderContainer container;

   public SetElderEquipmentFilterAction(ElderContainer var1) {
      this.container = var1;
   }

   public void runAndSendPreferArmorSets(boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(0, 1);
      var3.putNextBoolean(var1);
      this.runAndSendAction(var2);
   }

   public void runAndSendChange(ItemCategoriesFilterChange var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextMaxValue(1, 1);
      var1.write(var3);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      if (this.container.canEditEquipment && this.container.client.isServer()) {
         LevelSettler var2 = this.container.elderMob.levelSettler;
         if (var2 != null) {
            int var3 = var1.getNextMaxValue(1);
            switch (var3) {
               case 0:
                  var2.preferArmorSets = var1.getNextBoolean();
                  (new SettlementSettlerEquipmentFilterChangedEvent(this.container.elderMob.getUniqueID(), var2.preferArmorSets, (ItemCategoriesFilterChange)null)).applyAndSendToClientsAt(this.container.client.getServerClient());
                  break;
               case 1:
                  ItemCategoriesFilterChange var4 = ItemCategoriesFilterChange.fromPacket(var1);
                  if (var4.applyTo(var2.equipmentFilter)) {
                     (new SettlementSettlerEquipmentFilterChangedEvent(this.container.elderMob.getUniqueID(), var2.preferArmorSets, var4)).applyAndSendToClientsAt(this.container.client.getServerClient());
                  }
            }
         }
      }

   }
}
