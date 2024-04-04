package necesse.inventory.item.armorItem.ivy;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class IvyCircletArmorItem extends SetHelmetArmorItem {
   public IvyCircletArmorItem() {
      super(7, DamageTypeRegistry.SUMMON, 800, Item.Rarity.UNCOMMON, "ivycirclet", "ivychestplate", "ivyboots", "ivycircletsetbonus");
      this.headArmorBackTextureName = "ivycirclet_behind";
      this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.2F)});
   }
}
