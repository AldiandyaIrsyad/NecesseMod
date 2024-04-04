package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerEnchantSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.mob.MageContainer;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class MageContainerForm<T extends MageContainer> extends ShopContainerForm<T> {
   public Form enchantForm;
   public FormLabel costText;
   public FormLocalTextButton enchantButton;
   public FormLocalLabel costLabel;
   public FormItemPreview preview;

   public MageContainerForm(Client var1, T var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      FormFlow var6 = new FormFlow(5);
      this.enchantForm = (Form)this.addComponent(new Form("enchant", var3, var4), (var1x, var2x) -> {
         var2.setIsEnchanting.runAndSend(var2x);
      });
      ((FormLocalTextButton)this.enchantForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.enchantForm.getWidth() - 104, 4, 100, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var1x) -> {
         this.makeCurrent(this.dialogueForm);
      });
      this.enchantForm.addComponent(new FormLocalLabel("ui", "mageenchant", new FontOptions(20), -1, 4, var6.next(40)));
      int var7 = var6.next(50);
      this.enchantForm.addComponent(new FormContainerEnchantSlot(var1, var2, var2.ENCHANT_SLOT, 40, var7));
      this.enchantButton = (FormLocalTextButton)this.enchantForm.addComponent(new FormLocalTextButton("ui", "mageconfirm", 90, var7 + 10, 150, FormInputSize.SIZE_24, ButtonColor.BASE));
      this.enchantButton.onClicked((var1x) -> {
         var2.enchantButton.runAndSend();
      });
      this.costLabel = (FormLocalLabel)this.enchantForm.addComponent(new FormLocalLabel("ui", "magecost", new FontOptions(16), -1, 260, var7 - 4));
      this.preview = (FormItemPreview)this.enchantForm.addComponent(new FormItemPreview(250, var7 + 10, "coin"));
      this.costText = (FormLabel)this.enchantForm.addComponent(new FormLabel("x " + var2.getEnchantCost(), new FontOptions(16), -1, this.preview.getX() + 30, var7 + 20));
      this.enchantForm.addComponent((FormFairTypeLabel)var6.nextY((new FormFairTypeLabel(new LocalMessage("ui", "mageenchanttip"), this.enchantForm.getWidth() / 2, 0)).setFontOptions(new FontOptions(16)).setTextAlign(FairType.TextAlign.CENTER).setMaxWidth(this.enchantForm.getWidth() - 20), 10));
      if (var2.mageMob.isSettler()) {
         GameMessageBuilder var8 = (new GameMessageBuilder()).append(Settler.getMood(var2.settlerHappiness).getDescription()).append(" (").append(var2.settlerHappiness >= 0 ? "+" : "").append(Integer.toString(var2.settlerHappiness)).append(")");
         this.enchantForm.addComponent((FormFairTypeLabel)var6.nextY((new FormFairTypeLabel(var8, this.enchantForm.getWidth() / 2, 0)).setFontOptions(new FontOptions(16)).setTextAlign(FairType.TextAlign.CENTER).setMaxWidth(this.enchantForm.getWidth() - 20), 5));
         FormContentIconButton var9 = (FormContentIconButton)this.enchantForm.addComponent(new FormContentIconButton(this.enchantForm.getWidth() / 2 - 10, var6.next(30), FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[]{new LocalMessage("ui", "mageenchantbiastip")}));
         var9.handleClicksIfNoEventHandlers = true;
      }

      this.enchantForm.setHeight(var6.next());
      this.updateEnchantActive();
   }

   public MageContainerForm(Client var1, T var2) {
      this(var1, var2, 408, 170, 240);
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            MageContainerForm.this.preview.setX(MageContainerForm.this.costLabel.getX() + MageContainerForm.this.costLabel.getBoundingBox().width);
            MageContainerForm.this.costText.setX(MageContainerForm.this.preview.getX() + 30);
         }

         public boolean isDisposed() {
            return MageContainerForm.this.isDisposed();
         }
      });
   }

   protected void setupExtraDialogueOptions() {
      super.setupExtraDialogueOptions();
      if (((MageContainer)this.container).humanShop instanceof MageHumanMob && ((MageContainer)this.container).items != null) {
         this.dialogueForm.addDialogueOption(new LocalMessage("ui", "magewantenchant"), () -> {
            this.makeCurrent(this.enchantForm);
         });
      }

   }

   private void updateEnchantActive() {
      this.costText.setText("x " + ((MageContainer)this.container).getEnchantCost());
      this.enchantButton.setActive(((MageContainer)this.container).canEnchant());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isCurrent(this.enchantForm)) {
         this.updateEnchantActive();
      }

      super.draw(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.enchantForm);
   }
}
