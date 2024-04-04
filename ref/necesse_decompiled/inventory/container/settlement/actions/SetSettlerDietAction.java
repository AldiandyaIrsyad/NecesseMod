package necesse.inventory.container.settlement.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class SetSettlerDietAction extends SettlementAccessRequiredContainerCustomAction {
   public SetSettlerDietAction(SettlementContainer var1) {
      super(var1);
   }

   public void runAndSendChange(int var1, ItemCategoriesFilterChange var2) {
      Packet var3 = new Packet();
      PacketWriter var4 = new PacketWriter(var3);
      var4.putNextInt(var1);
      var2.write(var4);
      this.runAndSendAction(var3);
   }

   public void executePacket(PacketReader var1, SettlementLevelData var2, ServerClient var3) {
      int var4 = var1.getNextInt();
      LevelSettler var5 = var2.getSettler(var4);
      if (var5 != null) {
         ItemCategoriesFilterChange var6 = ItemCategoriesFilterChange.fromPacket(var1);
         if (var6.applyTo(var5.dietFilter)) {
            (new SettlementSettlerDietChangedEvent(var4, var6)).applyAndSendToClientsAt(var3);
         }
      } else {
         (new SettlementSettlersChangedEvent(var2)).applyAndSendToClient(var3);
      }

   }
}
