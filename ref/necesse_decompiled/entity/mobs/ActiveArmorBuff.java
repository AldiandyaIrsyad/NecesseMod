package necesse.entity.mobs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;

public class ActiveArmorBuff {
   public final InventoryItem item;
   public final ArmorBuff[] buffs;
   public final ActiveBuff[] activeBuffs;
   public final EquipmentItemEnchant enchant;

   public ActiveArmorBuff(InventoryItem var1, ArmorBuff[] var2, EquipmentItemEnchant var3) {
      this.item = var1;
      this.buffs = var2;
      this.activeBuffs = new ActiveBuff[var2.length];
      this.enchant = var3;
   }
}
