package necesse.inventory.item.armorItem.ancientFossil;

import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.ArmorModifiers;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class AncientFossilMaskArmorItem extends SetHelmetArmorItem {
   public AncientFossilMaskArmorItem() {
      super(14, DamageTypeRegistry.SUMMON, 1300, Item.Rarity.UNCOMMON, "ancientfossilmask", "ancientfossilchestplate", "ancientfossilboots", "ancientfossilmasksetbonus");
      this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
      this.headArmorBackTextureName = "ancientfossilmaskback";
   }

   public ArmorModifiers getArmorModifiers(InventoryItem var1, Mob var2) {
      return new ArmorModifiers(new ModifierValue[]{new ModifierValue(BuffModifiers.MAX_SUMMONS, 1), new ModifierValue(BuffModifiers.SUMMON_DAMAGE, 0.15F)});
   }
}
