package necesse.entity.objectEntity;

import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.objectEntity.interfaces.OEVicinityBuff;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CampfireObjectEntity extends AnyLogFueledInventoryObjectEntity implements OEVicinityBuff {
   public final boolean allowSettlementStorage;

   public CampfireObjectEntity(Level var1, String var2, int var3, int var4, boolean var5, boolean var6) {
      super(var1, var2, var3, var4, var5);
      this.allowSettlementStorage = var6;
   }

   public void clientTick() {
      super.clientTick();
      if (this.isFueled()) {
         this.tickVicinityBuff(this);
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.isFueled()) {
         this.tickVicinityBuff(this);
      }

   }

   public Buff[] getBuffs() {
      return new Buff[]{BuffRegistry.CAMPFIRE};
   }

   public int getBuffRange() {
      return 160;
   }

   public boolean shouldBuffPlayers() {
      return true;
   }

   public boolean shouldBuffMobs() {
      return false;
   }

   public InventoryRange getSettlementStorage() {
      if (this.allowSettlementStorage) {
         Inventory var1 = this.getInventory();
         if (var1 != null) {
            return new InventoryRange(var1);
         }
      }

      return null;
   }

   public boolean isSettlementStorageItemDisabled(Item var1) {
      return !var1.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
   }
}
