package necesse.inventory.item.armorItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class SurgicalMaskArmorItem extends HelmetArmorItem {
   public SurgicalMaskArmorItem() {
      super(0, (DamageType)null, 0, Item.Rarity.COMMON, "surgicalmask");
      this.hairDrawOptions = ArmorItem.HairDrawMode.UNDER_HAIR;
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "surgicalmasktip"));
      return var4;
   }
}
