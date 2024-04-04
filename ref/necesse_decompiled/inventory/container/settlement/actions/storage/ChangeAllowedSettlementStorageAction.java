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
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class ChangeAllowedSettlementStorageAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public ChangeAllowedSettlementStorageAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSend(int var1, int var2, Item[] var3, boolean var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var1);
      var6.putNextShortUnsigned(var2);
      var6.putNextBoolean(var4);
      var6.putNextBoolean(true);
      var6.putNextShortUnsigned(var3.length);
      Item[] var7 = var3;
      int var8 = var3.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         Item var10 = var7[var9];
         var6.putNextShortUnsigned(var10.getID());
      }

      this.runAndSendAction(var5);
   }

   public void runAndSend(int var1, int var2, ItemCategoriesFilter.ItemCategoryFilter var3, boolean var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var1);
      var6.putNextShortUnsigned(var2);
      var6.putNextBoolean(var4);
      var6.putNextBoolean(false);
      var6.putNextShortUnsigned(var3.category.id);
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
               boolean var8 = var1.getNextBoolean();
               int var9;
               if (var8) {
                  var9 = var1.getNextShortUnsigned();
                  Item[] var10 = new Item[var9];

                  for(int var11 = 0; var11 < var9; ++var11) {
                     int var12 = var1.getNextShortUnsigned();
                     Item var13 = ItemRegistry.getItem(var12);
                     var10[var11] = var13;
                     var6.filter.setItemAllowed(var13, var7);
                  }

                  (new SettlementStorageChangeAllowedEvent(var2, var3, var10, var7)).applyAndSendToClientsAtExcept(var5);
               } else {
                  var9 = var1.getNextShortUnsigned();
                  ItemCategoriesFilter.ItemCategoryFilter var14 = var6.filter.getItemCategory(var9);
                  var14.setAllowed(var7);
                  (new SettlementStorageChangeAllowedEvent(var2, var3, var14.category, var7)).applyAndSendToClientsAtExcept(var5);
               }
            } else {
               (new SettlementSingleStorageEvent(var4, var2, var3)).applyAndSendToClient(var5);
            }
         }
      }

   }
}
