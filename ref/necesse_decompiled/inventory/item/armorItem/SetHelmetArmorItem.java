package necesse.inventory.item.armorItem;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SetHelmetArmorItem extends HelmetArmorItem {
   public final String setChestStringID;
   public final String setBootsStringID;
   public final String buffType;

   public SetHelmetArmorItem(int var1, DamageType var2, int var3, String var4, String var5, String var6, String var7) {
      super(var1, var2, var3, var4);
      this.setChestStringID = var5;
      this.setBootsStringID = var6;
      this.buffType = var7;
   }

   public SetHelmetArmorItem(int var1, DamageType var2, int var3, Item.Rarity var4, String var5, String var6, String var7, String var8) {
      this(var1, var2, var3, var5, var6, var7, var8);
      this.rarity = var4;
   }

   public boolean hasSet(InventoryItem var1, InventoryItem var2, InventoryItem var3) {
      if (var1.item != this) {
         return false;
      } else if (this.setChestStringID != null && !var2.item.getStringID().equals(this.setChestStringID)) {
         return false;
      } else {
         return this.setBootsStringID == null || var3.item.getStringID().equals(this.setBootsStringID);
      }
   }

   public SetBonusBuff getSetBuff(InventoryItem var1, Mob var2, boolean var3) {
      return (SetBonusBuff)BuffRegistry.getBuff(this.buffType);
   }
}
