package necesse.inventory.item.armorItem.spider;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.ChestArmorItem;

public class SpiderChestplateArmorItem extends ChestArmorItem {
   public SpiderChestplateArmorItem() {
      super(5, 400, Item.Rarity.UNCOMMON, "spiderchest", "spiderarms");
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.KNOCKBACK_INCOMING_MOD, 0.5F)});
   }

   public void addModifierTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4) {
      var1.add(200, new ItemStatTip() {
         public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
            return new LocalMessage("itemtooltip", "spiderchest");
         }
      });
   }
}
