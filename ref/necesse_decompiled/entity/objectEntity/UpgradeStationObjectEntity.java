package necesse.entity.objectEntity;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.level.maps.Level;

public class UpgradeStationObjectEntity extends ObjectEntity implements OEInventory {
   public Inventory inventory = new Inventory(1);

   public UpgradeStationObjectEntity(Level var1, int var2, int var3) {
      super(var1, "upgradestation", var2, var3);
      this.inventory.filter = (var0, var1x) -> {
         if (var1x == null) {
            return true;
         } else {
            return var1x.item instanceof UpgradableItem && ((UpgradableItem)var1x.item).getCanBeUpgradedError(var1x) == null;
         }
      };
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSaveData(InventorySave.getSave(this.inventory, "INVENTORY"));
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.inventory.override(InventorySave.loadSave(var1.getFirstLoadDataByName("INVENTORY")));
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      this.inventory.writeContent(var1);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.inventory.override(Inventory.getInventory(var1));
   }

   public ArrayList<InventoryItem> getDroppedItems() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.inventory.getSize(); ++var2) {
         if (!this.inventory.isSlotClear(var2)) {
            var1.add(this.inventory.getItem(var2));
         }
      }

      return var1;
   }

   public void clientTick() {
      super.clientTick();
      this.inventory.tickItems(this);
   }

   public void serverTick() {
      super.serverTick();
      this.inventory.tickItems(this);
      this.serverTickInventorySync(this.getLevel().getServer(), this);
   }

   public void markClean() {
      super.markClean();
      this.inventory.clean();
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public GameMessage getInventoryName() {
      return this.getObject().getLocalization();
   }

   public boolean canSetInventoryName() {
      return false;
   }

   public boolean canQuickStackInventory() {
      return false;
   }

   public boolean canRestockInventory() {
      return false;
   }

   public boolean canSortInventory() {
      return false;
   }

   public boolean canUseForNearbyCrafting() {
      return false;
   }

   public InventoryRange getSettlementStorage() {
      return null;
   }
}
