package necesse.entity.mobs;

import java.util.ArrayList;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;

public class ActiveTrinketBuff {
   public final InventoryItem item;
   public final TrinketBuff[] buffs;
   public final ActiveBuff[] activeBuffs;
   public final EquipmentItemEnchant enchant;
   public final ArrayList<Integer> disables;

   public ActiveTrinketBuff(InventoryItem var1, TrinketBuff[] var2, EquipmentItemEnchant var3, ArrayList<Integer> var4) {
      this.item = var1;
      this.buffs = var2;
      this.activeBuffs = new ActiveBuff[var2.length];
      this.enchant = var3;
      this.disables = var4;
   }
}
