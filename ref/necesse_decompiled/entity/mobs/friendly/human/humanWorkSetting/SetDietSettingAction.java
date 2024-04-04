package necesse.entity.mobs.friendly.human.humanWorkSetting;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SetDietSettingAction extends ContainerCustomAction {
   public final ShopContainer container;

   public SetDietSettingAction(ShopContainer var1) {
      this.container = var1;
   }

   public void runAndSendChange(ItemCategoriesFilterChange var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var1.write(var3);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      if (this.container.client.isServer()) {
         LevelSettler var2 = this.container.humanShop.levelSettler;
         if (var2 != null) {
            ItemCategoriesFilterChange var3 = ItemCategoriesFilterChange.fromPacket(var1);
            if (var3.applyTo(var2.dietFilter)) {
               (new SettlementSettlerDietChangedEvent(this.container.humanShop.getUniqueID(), var3)).applyAndSendToClientsAt(this.container.client.getServerClient());
            }
         }
      }

   }
}
