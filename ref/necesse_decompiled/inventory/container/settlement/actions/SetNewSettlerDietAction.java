package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SetNewSettlerDietAction extends SettlementAccessRequiredContainerCustomAction {
   public SetNewSettlerDietAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSendChange(ItemCategoriesFilterChange var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var1.write(var3);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      ItemCategoriesFilterChange var4 = ItemCategoriesFilterChange.fromPacket(var1);
      ItemCategoriesFilter var5 = var2.getNewSettlerDiet();
      if (var4.applyTo(var5)) {
         (new SettlementNewSettlerDietChangedEvent(var4)).applyAndSendToClientsAt(var3);
      }

   }
}
