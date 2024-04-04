package necesse.inventory.item.armorItem.mycelium;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class MyceliumScarfArmorItem extends SetHelmetArmorItem {
   public MyceliumScarfArmorItem() {
      super(14, DamageTypeRegistry.SUMMON, 1250, Item.Rarity.UNCOMMON, "myceliumscarf", "myceliumchestplate", "myceliumboots", "myceliumscarfsetbonus");
      this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
      this.headArmorBackTextureName = "myceliumscarfback";
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue(BuffModifiers.SUMMONS_SPEED, 0.1F)});
   }
}
