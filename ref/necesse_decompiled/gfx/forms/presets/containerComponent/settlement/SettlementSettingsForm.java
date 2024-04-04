package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.engine.GameAuth;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;

public class SettlementSettingsForm<T extends SettlementContainer> extends FormSwitcher implements SettlementSubForm {
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   protected Form name;
   protected Form settings;
   protected FormTextInput nameInput;

   public SettlementSettingsForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.settings = (Form)this.addComponent(new Form("settings", 400, 40));
      this.name = (Form)this.addComponent(new Form("name", 400, 80));
      this.nameInput = (FormTextInput)this.name.addComponent(new FormTextInput(4, 0, FormInputSize.SIZE_32_TO_40, this.name.getWidth() - 8, 40));
      this.nameInput.placeHolder = new LocalMessage("settlement", "defname", "biome", var1.getLevel().biome.getLocalization());
      String var4 = var2.basics.settlementName.translate();
      if (!this.nameInput.placeHolder.translate().equals(var4)) {
         this.nameInput.setText(var4);
      }

      this.nameInput.onSubmit((var1x) -> {
         this.playTickSound();
         this.submitName();
         this.makeCurrent(this.settings);
      });
      ((FormLocalTextButton)this.name.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, 40, this.name.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.playTickSound();
         this.submitName();
         this.makeCurrent(this.settings);
      });
      ((FormLocalTextButton)this.name.addComponent(new FormLocalTextButton("ui", "backbutton", this.name.getWidth() / 2 + 2, 40, this.name.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.settings);
      });
      this.update(var2.basics);
      this.makeCurrent(this.settings);
   }

   protected void init() {
      super.init();
      this.container.onEvent(SettlementBasicsEvent.class, this::update);
   }

   protected void update(SettlementBasicsEvent var1) {
      this.settings.clearComponents();
      FormFlow var2 = new FormFlow(5);
      this.settings.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel(this.container.basics.settlementName, new FontOptions(20), 0, this.settings.getWidth() / 2, 0, this.settings.getWidth() - 20), 10));
      FormLocalTextButton var3 = (FormLocalTextButton)this.settings.addComponent(new FormLocalTextButton("ui", "settmentchangename", 40, var2.next(40), this.settings.getWidth() - 80));
      var3.onClicked((var1x) -> {
         this.makeCurrent(this.name);
      });
      var3.setActive(var1.isOwner(this.client));
      if (!var3.isActive()) {
         var3.setLocalTooltip(new LocalMessage("ui", var1.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
      }

      FormLocalTextButton var4 = (FormLocalTextButton)this.settings.addComponent(new FormLocalTextButton("ui", var1.isPrivate ? "settlementmakepub" : "settlementmakepriv", 40, var2.next(40), this.settings.getWidth() - 80));
      var4.onClicked((var1x) -> {
         this.container.changePrivacy.runAndSend(!this.container.basics.isPrivate);
      });
      var4.setActive(var1.isOwner(this.client));
      if (!var4.isActive()) {
         var4.setLocalTooltip(new LocalMessage("ui", var1.hasOwner() ? "settlementowneronly" : "settlementclaimfirst"));
      }

      FormLocalTextButton var5 = (FormLocalTextButton)this.settings.addComponent(new FormLocalTextButton("ui", var1.isOwner(this.client) ? "settlementunclaim" : "settlementclaim", 40, var2.next(40), this.settings.getWidth() - 80));
      var5.onClicked((var1x) -> {
         this.container.changeClaim.runAndSend(this.container.basics.ownerAuth != GameAuth.getAuthentication());
      });
      var5.setActive(!var1.hasOwner() || var1.isOwner(this.client));
      if (!var5.isActive()) {
         var5.setLocalTooltip(new LocalMessage("ui", "settlementowneronly"));
      }

      var2.next(10);
      this.settings.addComponent(new FormLocalLabel(new LocalMessage("ui", "settlementowner", "owner", this.container.basics.ownerName), new FontOptions(16), -1, 5, var2.next(20)));
      this.settings.setHeight(var2.next());
      this.onWindowResized();
      Screen.submitNextMoveEvent();
   }

   protected void submitName() {
      this.container.changeName.runAndSend(this.getCurrentNameInput());
   }

   protected GameMessage getCurrentNameInput() {
      String var1 = this.nameInput.getText();
      return (GameMessage)(var1.isEmpty() ? this.nameInput.placeHolder : new StaticMessage(var1));
   }

   public void onSetCurrent(boolean var1) {
      if (var1) {
         this.makeCurrent(this.settings);
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosInventory(this.settings);
      this.name.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementsettings");
   }

   public String getTypeString() {
      return "settings";
   }
}
