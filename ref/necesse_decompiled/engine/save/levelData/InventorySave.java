package necesse.engine.save.levelData;

import java.util.Iterator;
import java.util.List;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class InventorySave {
   public InventorySave() {
   }

   public static Inventory loadSave(LoadData var0) {
      Inventory var1 = new Inventory(Integer.parseInt(var0.getFirstDataByName("size"))) {
         public boolean canLockItem(int var1) {
            return true;
         }
      };
      List var2 = var0.getLoadDataByName("ITEM");
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         LoadData var4 = (LoadData)var3.next();

         try {
            boolean var5 = var4.getFirstLoadDataByName("locked") != null;
            int var9 = var4.getInt("slot", -1, false);
            if (var9 == -1) {
               throw new NullPointerException();
            }

            InventoryItem var7 = InventoryItem.fromLoadData(var4);
            if (var7 == null) {
               throw new NullPointerException();
            }

            var1.setItem(var9, var7);
            var1.setItemLocked(var9, var5);
         } catch (Exception var8) {
            String var6 = var4.getUnsafeString("stringID", "N/A");
            System.err.println("Could not load inventory item: " + var6);
         }
      }

      return var1;
   }

   public static SaveData getSave(Inventory var0, String var1) {
      SaveData var2 = new SaveData(var1);
      var2.addInt("size", var0.getSize());

      for(int var3 = 0; var3 < var0.getSize(); ++var3) {
         if (!var0.isSlotClear(var3) && var0.getItemSlot(var3) != null) {
            SaveData var4 = new SaveData("ITEM");
            var4.addInt("slot", var3);
            if (var0.isItemLocked(var3)) {
               var4.addBoolean("locked", true);
            }

            var0.getItem(var3).addSaveData(var4);
            var2.addSaveData(var4);
         }
      }

      return var2;
   }

   public static SaveData getSave(Inventory var0) {
      return getSave(var0, "INVENTORY");
   }
}
