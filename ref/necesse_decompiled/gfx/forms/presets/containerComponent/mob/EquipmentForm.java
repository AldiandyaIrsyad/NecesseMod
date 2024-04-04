package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.EquipmentBuffManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerSettlerArmorSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerSettlerWeaponSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public abstract class EquipmentForm extends ContainerFormList<Container> {
   private Form equipmentForm = (Form)this.addComponent(new Form("settlerequipment", 400, 200));
   private Form modifiersForm;
   private FormLabel totalArmorLabel;
   public FormLocalTextButton filterEquipmentButton;
   public Runnable filterEquipmentButtonPressed;

   public EquipmentForm(Client var1, Container var2, String var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Consumer<Boolean> var11, FormEventListener<FormInputEvent<FormButton>> var12) {
      super(var1, var2);
      FormFlow var13 = new FormFlow(5);
      var3 = GameUtils.maxString(var3, new FontOptions(20), this.equipmentForm.getWidth() - 10 - 32);
      this.equipmentForm.addComponent((FormLabel)var13.nextY(new FormLabel(var3, new FontOptions(20), -1, 4, 0), 5));
      int var14 = this.equipmentForm.getWidth() / 2 - 40;
      int var15 = var13.next(40);
      int var16 = var13.next(40);
      int var17 = var13.next(40);
      this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(var1, var2, var4, var14, var15, ArmorItem.ArmorType.HEAD, true, this));
      this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(var1, var2, var5, var14, var16, ArmorItem.ArmorType.CHEST, true, this));
      this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(var1, var2, var6, var14, var17, ArmorItem.ArmorType.FEET, true, this));
      this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(var1, var2, var7, var14 + 40, var15, ArmorItem.ArmorType.HEAD, false, this));
      this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(var1, var2, var8, var14 + 40, var16, ArmorItem.ArmorType.CHEST, false, this));
      this.equipmentForm.addComponent(new FormContainerSettlerArmorSlot(var1, var2, var9, var14 + 40, var17, ArmorItem.ArmorType.FEET, false, this));
      this.equipmentForm.addComponent(new FormContainerSettlerWeaponSlot(var1, var2, var10, var14 - 80, var16, this));
      var13.next(5);
      this.totalArmorLabel = (FormLabel)this.equipmentForm.addComponent(new FormLabel("", new FontOptions(16), -1, 5, var13.next(20), this.equipmentForm.getWidth() - 10));
      var13.next(5);
      FormLocalLabel var18 = (FormLocalLabel)this.equipmentForm.addComponent((FormLocalLabel)var13.nextY(new FormLocalLabel("ui", "settlerselfmanagequipment", new FontOptions(16), -1, 33, 0, this.equipmentForm.getWidth() - 5 - 24 - 4 - 5), 8));
      FormContentIconToggleButton var19 = (FormContentIconToggleButton)this.equipmentForm.addComponent(new FormContentIconToggleButton(5, var18.getY() + var18.getHeight() / 2 - 12, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_checked_20, Settings.UI.button_escaped_20, new GameMessage[0]) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            this.setToggled((Boolean)EquipmentForm.this.getMob().selfManageEquipment.get());
            super.draw(var1, var2, var3);
         }
      });
      var19.onToggled((var2x) -> {
         this.getMob().selfManageEquipment.set(((FormButtonToggle)var2x.from).isToggled());
         var11.accept(((FormButtonToggle)var2x.from).isToggled());
      });
      this.filterEquipmentButton = (FormLocalTextButton)this.equipmentForm.addComponent((<undefinedtype>)var13.nextY(new FormLocalTextButton("ui", "settlerfilterequipment", 20, 0, this.equipmentForm.getWidth() - 40, FormInputSize.SIZE_20, ButtonColor.BASE) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            this.setActive(EquipmentForm.this.filterEquipmentButtonPressed != null && (Boolean)EquipmentForm.this.getMob().selfManageEquipment.get());
            super.draw(var1, var2, var3);
         }
      }, 5));
      this.filterEquipmentButton.onClicked((var1x) -> {
         if (this.filterEquipmentButtonPressed != null) {
            this.filterEquipmentButtonPressed.run();
         }

      });
      FormContentIconToggleButton var20 = (FormContentIconToggleButton)this.equipmentForm.addComponent(new FormContentIconToggleButton(this.equipmentForm.getWidth() - 36, 4, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.quickbar_stats_icon, new GameMessage[]{new LocalMessage("ui", "settlershowmodifiers")}));
      var20.setToggled(false);
      var20.onToggled((var2x) -> {
         this.modifiersForm.setHidden(!var20.isToggled());
      });
      this.modifiersForm = (Form)this.addComponent(new EquipmentModifiersForm("settlermodifiers", 300, 200) {
         public Mob getMob() {
            return EquipmentForm.this.getMob();
         }
      });
      this.modifiersForm.setHidden(true);
      this.modifiersForm.setPosition(new FormRelativePosition(this.equipmentForm, () -> {
         return this.equipmentForm.getWidth() + Settings.UI.formSpacing;
      }, () -> {
         return 0;
      }));
      FormLocalTextButton var21 = (FormLocalTextButton)this.equipmentForm.addComponent((FormLocalTextButton)var13.nextY(new FormLocalTextButton("ui", "backbutton", this.equipmentForm.getWidth() - 154, 0, 150, FormInputSize.SIZE_20, ButtonColor.BASE), 4));
      var21.onClicked(var12);
      this.equipmentForm.setHeight(var13.next());
   }

   public abstract HumanMob getMob();

   public abstract EquipmentBuffManager getEquipmentManager();

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.totalArmorLabel.setText(Localization.translate("ui", "totalarmor", "armor", (Object)Math.round(this.getMob().getArmor())), this.equipmentForm.getWidth() - 10);
      super.draw(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.equipmentForm);
   }

   public boolean shouldOpenInventory() {
      return true;
   }
}
