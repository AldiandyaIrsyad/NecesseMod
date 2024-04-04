package necesse.gfx.forms.components.containerSlot;

import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentForm;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public class FormContainerSettlerArmorSlot extends FormContainerArmorSlot {
   private EquipmentForm equipmentForm;

   public FormContainerSettlerArmorSlot(Client var1, Container var2, int var3, int var4, int var5, ArmorItem.ArmorType var6, boolean var7, EquipmentForm var8) {
      super(var1, var3, var4, var5, var6, var7);
      this.equipmentForm = var8;
   }

   public Mob getEquippedMob(PlayerMob var1) {
      return this.equipmentForm.getMob();
   }

   public ListGameTooltips getSetBonusTooltips(GameBlackboard var1) {
      return this.equipmentForm.getEquipmentManager().getSetBonusBuffTooltip(var1);
   }
}
