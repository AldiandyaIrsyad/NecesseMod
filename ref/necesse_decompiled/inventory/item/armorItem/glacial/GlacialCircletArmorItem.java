package necesse.inventory.item.armorItem.glacial;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class GlacialCircletArmorItem extends SetHelmetArmorItem {
   public GlacialCircletArmorItem() {
      super(12, DamageTypeRegistry.SUMMON, 1200, Item.Rarity.UNCOMMON, "glacialcirclet", "glacialchestplate", "glacialboots", "glacialcircletsetbonus");
      this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1)});
   }
}
