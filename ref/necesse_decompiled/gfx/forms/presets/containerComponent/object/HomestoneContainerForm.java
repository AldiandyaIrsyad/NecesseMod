package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.object.HomestoneContainer;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.level.maps.levelData.settlementData.Waystone;

public class HomestoneContainerForm<T extends HomestoneContainer> extends ContainerForm<T> {
   private FormContentBox content;
   private ArrayList<WaystoneComponents> buttons;
   private FormLocalLabel emptyLabel;

   public HomestoneContainerForm(Client var1, T var2) {
      super(var1, 408, 200, var2);
      this.addComponent(new FormLocalLabel("object", "homestone", new FontOptions(20), -1, 4, 4, 390));
      this.emptyLabel = (FormLocalLabel)this.addComponent(new FormLocalLabel(new StaticMessage(""), new FontOptions(16), -1, 4, 24, 390));
      this.addComponent(new FormContentIconButton(this.getWidth() - 24, 4, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[0]) {
         public GameTooltips getTooltips(PlayerMob var1) {
            return (new StringTooltips()).add(Localization.translate("ui", "waystonehelp"), 400);
         }
      });
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, 45, this.getWidth(), this.getHeight() - 45));
      this.content.alwaysShowVerticalScrollBar = true;
      this.updateWaystones();
      var2.onEvent(HomestoneUpdateEvent.class, (var1x) -> {
         this.updateWaystones();
      });
   }

   public void updateWaystones() {
      byte var1 = 28;
      int var2 = ((HomestoneContainer)this.container).maxWaystones * var1 + 2;
      int var3 = Math.max(Math.min(Screen.getHudHeight() - 400, var2), 200);
      this.content.setHeight(var3);
      this.setHeight(this.content.getY() + var3);
      int var4 = ((HomestoneContainer)this.container).maxWaystones - ((HomestoneContainer)this.container).waystones.size();
      this.emptyLabel.setLocalization(new LocalMessage("ui", "emptywaystones", new Object[]{"count", var4}));
      if (this.buttons != null) {
         Iterator var5 = this.buttons.iterator();

         while(var5.hasNext()) {
            WaystoneComponents var6 = (WaystoneComponents)var5.next();
            var6.dispose();
         }
      }

      this.buttons = new ArrayList();

      for(int var7 = 0; var7 < ((HomestoneContainer)this.container).maxWaystones; ++var7) {
         if (var7 < ((HomestoneContainer)this.container).waystones.size()) {
            Waystone var9 = (Waystone)((HomestoneContainer)this.container).waystones.get(var7);
            this.buttons.add(new WaystoneComponents(this, var7, var9));
         } else {
            FormTextButton var10 = (FormTextButton)this.content.addComponent(new FormLocalTextButton("ui", "waystoneempty", 4, var7 * var1 + 2, this.getWidth() - 16, FormInputSize.SIZE_24, ButtonColor.BASE));
            var10.setActive(false);
         }
      }

      Rectangle var8 = this.content.getContentBoxToFitComponents().union(new Rectangle(0, 0, this.getWidth(), this.content.getHeight()));
      this.content.setContentBox(var8);
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateWaystones();
   }

   public boolean shouldOpenInventory() {
      return false;
   }

   public boolean shouldShowToolbar() {
      return false;
   }

   private static class WaystoneComponents {
      public final HomestoneContainerForm<?> form;
      public final int index;
      public final Waystone waystone;
      public FormTextButton button;
      public FormTextInput renameInput;
      public FormContentIconButton renameButton;
      public FormIconButton moveUpButton;
      public FormIconButton moveDownButton;
      public int buttonX;

      public WaystoneComponents(HomestoneContainerForm<?> var1, int var2, Waystone var3) {
         this.form = var1;
         this.index = var2;
         this.waystone = var3;
         this.buttonX = var1.getWidth() - 8 - 28;
         int var4 = var2 * 28 + 2;
         this.moveUpButton = (FormIconButton)var1.content.addComponent(new FormIconButton(4, var4 - 1, Settings.UI.button_moveup, 16, 13, new GameMessage[]{new LocalMessage("ui", "moveupbutton")}));
         this.moveUpButton.onClicked((var2x) -> {
            ((HomestoneContainer)var1.container).moveWaystoneUp.runAndSend(var2, Screen.input().isKeyDown(340) || Screen.input().isKeyDown(344));
            var1.updateWaystones();
         });
         this.moveUpButton.setActive(var2 > 0);
         this.moveDownButton = (FormIconButton)var1.content.addComponent(new FormIconButton(4, var4 + 12, Settings.UI.button_movedown, 16, 13, new GameMessage[]{new LocalMessage("ui", "movedownbutton")}));
         this.moveDownButton.onClicked((var2x) -> {
            ((HomestoneContainer)var1.container).moveWaystoneDown.runAndSend(var2, Screen.input().isKeyDown(340) || Screen.input().isKeyDown(344));
            var1.updateWaystones();
         });
         this.moveDownButton.setActive(var2 < ((HomestoneContainer)var1.container).waystones.size() - 1);
         this.renameButton = (FormContentIconButton)var1.content.addComponent(new FormContentIconButton(this.buttonX, var4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[]{new LocalMessage("ui", "renamebutton")}));
         this.setupButton();
         this.renameInput = null;
         this.renameButton.onClicked((var3x) -> {
            if (this.renameInput == null) {
               var1.content.removeComponent(this.button);
               this.renameButton.setIcon(Settings.UI.container_rename_save);
               this.renameButton.setTooltips(new LocalMessage("ui", "savebutton"));
               this.renameInput = (FormTextInput)var1.content.addComponent(new FormTextInput(24, var4, FormInputSize.SIZE_24, this.buttonX - 28, 25));
               this.renameInput.setText(this.button.getText());
               this.renameInput.onSubmit((var2) -> {
                  var1.playTickSound();
                  this.submitRename(((FormTextInput)var2.from).getText());
               });
               this.renameInput.setTyping(true);
               this.button = null;
            } else {
               this.submitRename(this.renameInput.getText());
            }

         });
      }

      public void submitRename(String var1) {
         this.waystone.name = var1;
         if (this.renameInput != null) {
            this.form.content.removeComponent(this.renameInput);
         }

         if (this.button != null) {
            this.form.content.removeComponent(this.button);
         }

         this.renameInput = null;
         this.setupButton();
         this.renameButton.setIcon(Settings.UI.container_rename);
         this.renameButton.setTooltips(new LocalMessage("ui", "renamebutton"));
         ((HomestoneContainer)this.form.container).renameWaystone.runAndSend(this.index, var1);
      }

      private void setupButton() {
         this.button = (FormTextButton)this.form.content.addComponent(new FormTextButton("", 24, this.index * 28 + 2, this.buttonX - 28, FormInputSize.SIZE_24, ButtonColor.BASE));
         this.updateName();
         this.button.onClicked((var1) -> {
            ((HomestoneContainer)this.form.container).useWaystone.runAndSend(this.index);
         });
      }

      public void updateName() {
         if (this.button != null) {
            if (this.waystone.name.isEmpty()) {
               this.button.setText(Localization.translate("ui", "waystonenum", "num", (Object)(this.index + 1)));
            } else {
               this.button.setText(this.waystone.name);
            }
         }

      }

      public void dispose() {
         if (this.button != null) {
            this.form.content.removeComponent(this.button);
         }

         if (this.renameInput != null) {
            this.form.content.removeComponent(this.renameInput);
         }

         if (this.renameButton != null) {
            this.form.content.removeComponent(this.renameButton);
         }

      }
   }
}
