package necesse.gfx.forms.presets.containerComponent.mob;

import java.util.Objects;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.mob.ShopContainer;

public class ShopEquipmentForm extends EquipmentForm {
   private ShopContainer shopContainer;

   public ShopEquipmentForm(Client var1, ShopContainer var2, FormEventListener<FormInputEvent<FormButton>> var3) {
      String var10003 = MobRegistry.getLocalization(var2.humanShop.getID()).translate();
      int var10004 = var2.EQUIPMENT_COSM_HEAD_SLOT;
      int var10005 = var2.EQUIPMENT_COSM_CHEST_SLOT;
      int var10006 = var2.EQUIPMENT_COSM_FEET_SLOT;
      int var10007 = var2.EQUIPMENT_HEAD_SLOT;
      int var10008 = var2.EQUIPMENT_CHEST_SLOT;
      int var10009 = var2.EQUIPMENT_FEET_SLOT;
      int var10010 = var2.EQUIPMENT_WEAPON_SLOT;
      BooleanCustomAction var10011 = var2.setSelfManageEquipment;
      Objects.requireNonNull(var10011);
      super(var1, var2, var10003, var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011::runAndSend, var3);
      this.shopContainer = var2;
   }

   public HumanMob getMob() {
      return this.shopContainer.humanShop;
   }

   public EquipmentBuffManager getEquipmentManager() {
      return this.shopContainer.humanShop.equipmentBuffManager;
   }
}
