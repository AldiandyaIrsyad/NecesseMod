package necesse.inventory.container.settlement.actions.storage;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeLimitsSettlementStorageAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public ChangeLimitsSettlementStorageAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, int var2, Item var3, ItemCategoriesFilter.ItemLimits var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var1);
      var6.putNextShortUnsigned(var2);
      var6.putNextBoolean(true);
      var6.putNextShortUnsigned(var3.getID());
      var4.writePacket(var6);
      this.runAndSendAction(var5);
   }

   public void runAndSend(int var1, int var2, ItemCategoriesFilter.ItemCategoryFilter var3, int var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var1);
      var6.putNextShortUnsigned(var2);
      var6.putNextBoolean(false);
      var6.putNextShortUnsigned(var3.category.id);
      var6.putNextInt(var4);
      this.runAndSendAction(var5);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();
      int var3 = var1.getNextShortUnsigned();
      if (this.container.client.isServer()) {
         SettlementLevelData var4 = this.container.getLevelData();
         if (var4 != null) {
            ServerClient var5 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var5)) {
               (new SettlementBasicsEvent(var4)).applyAndSendToClient(var5);
               return;
            }

            SettlementInventory var6 = var4.getStorage(var2, var3);
            if (var6 != null) {
               boolean var7 = var1.getNextBoolean();
               int var8;
               if (var7) {
                  var8 = var1.getNextShortUnsigned();
                  Item var9 = ItemRegistry.getItem(var8);
                  ItemCategoriesFilter.ItemLimits var10 = new ItemCategoriesFilter.ItemLimits();
                  var10.readPacket(var1);
                  var6.filter.setItemAllowed(var9, var10);
                  (new SettlementStorageLimitsEvent(var2, var3, var9, var10)).applyAndSendToClientsAtExcept(var5);
               } else {
                  var8 = var1.getNextShortUnsigned();
                  int var12 = var1.getNextInt();
                  ItemCategoriesFilter.ItemCategoryFilter var11 = var6.filter.getItemCategory(var8);
                  var11.setMaxItems(var12);
                  (new SettlementStorageLimitsEvent(var2, var3, var11.category, var12)).applyAndSendToClientsAtExcept(var5);
               }
            } else {
               (new SettlementSingleStorageEvent(var4, var2, var3)).applyAndSendToClient(var5);
            }
         }
      }

   }
}
