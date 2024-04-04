package necesse.inventory.item.armorItem;

import necesse.inventory.item.Item;

public class BootsArmorItem extends ArmorItem {
   public BootsArmorItem(int var1, int var2, String var3) {
      super(ArmorItem.ArmorType.FEET, var1, var2, var3);
   }

   public BootsArmorItem(int var1, int var2, Item.Rarity var3, String var4) {
      this(var1, var2, var4);
      this.rarity = var3;
   }
}
