package necesse.inventory.container.settlement.actions.storage;

import java.util.function.BiConsumer;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class PriorityLimitSettlementStorageAction extends ContainerCustomAction {
   public final SettlementDependantContainer container;

   public PriorityLimitSettlementStorageAction(SettlementDependantContainer var1) {
      this.container = var1;
   }

   public void runAndSendPriority(int var1, int var2, int var3) {
      Packet var4 = new Packet();
      PacketWriter var5 = new PacketWriter(var4);
      var5.putNextShortUnsigned(var1);
      var5.putNextShortUnsigned(var2);
      var5.putNextBoolean(true);
      var5.putNextInt(var3);
      this.runAndSendAction(var4);
   }

   public void runAndSendLimit(int var1, int var2, ItemCategoriesFilter.ItemLimitMode var3, int var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextShortUnsigned(var1);
      var6.putNextShortUnsigned(var2);
      var6.putNextBoolean(false);
      var6.putNextEnum(var3);
      var6.putNextInt(var4);
      this.runAndSendAction(var5);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();
      int var3 = var1.getNextShortUnsigned();
      boolean var4 = var1.getNextBoolean();
      BiConsumer var5;
      if (var4) {
         int var6 = var1.getNextInt();
         var5 = (var3x, var4x) -> {
            var4x.priority = var6;
            (new SettlementStoragePriorityLimitEvent(var2, var3, var6)).applyAndSendToClientsAtExcept(var3x);
         };
      } else {
         ItemCategoriesFilter.ItemLimitMode var9 = (ItemCategoriesFilter.ItemLimitMode)var1.getNextEnum(ItemCategoriesFilter.ItemLimitMode.class);
         int var7 = var1.getNextInt();
         var5 = (var4x, var5x) -> {
            var5x.filter.limitMode = var9;
            var5x.filter.maxAmount = var7;
            (new SettlementStoragePriorityLimitEvent(var2, var3, var9, var7)).applyAndSendToClientsAtExcept(var4x);
         };
      }

      if (this.container.client.isServer()) {
         SettlementLevelData var10 = this.container.getLevelData();
         if (var10 != null) {
            ServerClient var11 = this.container.client.getServerClient();
            if (!this.container.getLevelLayer().doesClientHaveAccess(var11)) {
               (new SettlementBasicsEvent(var10)).applyAndSendToClient(var11);
               return;
            }

            SettlementInventory var8 = var10.getStorage(var2, var3);
            if (var8 != null) {
               var5.accept(var11, var8);
            } else {
               (new SettlementSingleStorageEvent(var10, var2, var3)).applyAndSendToClient(var11);
            }
         }
      }

   }
}
