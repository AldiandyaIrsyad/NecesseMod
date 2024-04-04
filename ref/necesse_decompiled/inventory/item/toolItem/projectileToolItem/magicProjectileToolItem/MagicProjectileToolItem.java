package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;

public class MagicProjectileToolItem extends ProjectileToolItem {
   public MagicProjectileToolItem(int var1) {
      super(var1);
      this.setItemCategory(new String[]{"equipment", "weapons", "magicweapons"});
      this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "magicweapons"});
      this.damageType = DamageTypeRegistry.MAGIC;
      this.enchantCost.setUpgradedValue(1.0F, 2050);
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addAttackSpeedTip(var1, var2, var3, var4);
      this.addResilienceGainTip(var1, var2, var3, var4, var5);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
      this.addManaCostTip(var1, var2, var3, var4);
   }
}
