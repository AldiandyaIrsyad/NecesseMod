package necesse.inventory.item.toolItem.shovelToolItem;

import necesse.inventory.item.Item;

public class CustomShovelToolItem extends ShovelToolItem {
   public CustomShovelToolItem(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(var7);
      this.attackAnimTime.setBaseValue(var1);
      this.toolDps.setBaseValue(var2);
      this.toolTier.setBaseValue(var3);
      this.attackDamage.setBaseValue((float)var4);
      this.attackRange.setBaseValue(var5);
      this.knockback.setBaseValue(var6);
      this.enchantCost.setUpgradedValue(1.0F, 1200);
   }

   public CustomShovelToolItem(int var1, int var2, int var3, int var4, int var5, int var6, int var7, Item.Rarity var8) {
      this(var1, var2, var3, var4, var5, var6, var7);
      this.rarity = var8;
   }

   public CustomShovelToolItem(int var1, int var2, int var3, int var4, int var5, int var6, int var7, Item.Rarity var8, int var9) {
      this(var1, var2, var3, var4, var5, var6, var7);
      this.rarity = var8;
      this.addedRange = var9;
   }
}
