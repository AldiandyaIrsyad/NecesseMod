package necesse.level.maps.presets.trialRoomPresets;

import java.util.Iterator;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.CoinPileObject;
import necesse.level.maps.Level;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.set.TrialRoomSet;

public class TrialRoomPreset extends Preset {
   public int exitTileX;
   public int exitTileY;

   public TrialRoomPreset(int var1, int var2) {
      super(50, 50);
      this.exitTileX = var1;
      this.exitTileY = var2;
   }

   public void replaceSet(TrialRoomSet var1) {
      this.replaceSet(var1, this);
   }

   public void replaceSet(TrialRoomSet var1, Preset var2) {
      TrialRoomSet var3 = TrialRoomSet.stone;
      TrialRoomSet var4 = var1 == null ? TrialRoomSet.stone : var1;
      var4.replacePreset(var3, var2);
   }

   public void addCoinPilesToPreset(GameRandom var1) {
      CoinPileObject var2 = (CoinPileObject)ObjectRegistry.getObject("coin");
      this.addCustomApplyAreaEach(0, 0, this.width, this.height, 0, (var2x, var3, var4, var5) -> {
         if (var2x.getObjectID(var3, var4) == var2.getID()) {
            int var6 = GameRandom.getIntBetween(var1, 50, 100);
            var2.setCoins(var6, var2x, var3, var4);
         }

         return null;
      });
   }

   public void addLoot(Iterable<InventoryItem> var1, Level var2, int var3, int var4) {
      if (var1 == null) {
         GameLog.warn.println("Could not find loot items for " + var2.getIdentifier() + " at tile " + var3 + "x" + var4);
      } else {
         ObjectEntity var5 = var2.entityManager.getObjectEntity(var3, var4);
         if (var5 != null && var5.implementsOEInventory()) {
            Iterator var6 = var1.iterator();

            while(var6.hasNext()) {
               InventoryItem var7 = (InventoryItem)var6.next();
               ((OEInventory)var5).getInventory().addItem((Level)null, (PlayerMob)null, var7, "addloot", (InventoryAddConsumer)null);
            }
         }

      }
   }

   public void addLoot(Supplier<? extends Iterable<InventoryItem>> var1, int var2, int var3) {
      this.addCustomApply(var2, var3, 0, (var2x, var3x, var4, var5) -> {
         Iterable var6 = (Iterable)var1.get();
         this.addLoot(var6, var2x, var3x, var4);
         return null;
      });
   }
}
