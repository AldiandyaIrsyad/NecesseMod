package necesse.inventory.item.miscItem;

import necesse.engine.network.PacketReader;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class Faunabox extends PouchItem {
   public Faunabox() {
   }

   public boolean isValidPouchItem(InventoryItem var1) {
      return this.isValidRequestItem(var1.item);
   }

   public boolean isValidRequestItem(Item var1) {
      return var1.getStringID().equals("honeybee");
   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      return super.onAttack(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean isValidRequestType(Item.Type var1) {
      return false;
   }

   public int getInternalInventorySize() {
      return 10;
   }
}
