package necesse.gfx.forms.presets;

import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.world.WorldSettings;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPasswordInput;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public abstract class HostSettingsForm extends Form {
   protected FormDropdownSelectionButton<ServerSettings.SteamLobbyType> lobbyTypeSelect;
   protected FormComponentList portInputHide;
   protected FormTextInput passwordInput;
   protected FormTextInput portInput;
   protected FormSlider slotsSlider;
   protected FormCheckBox allowConnectByIP;
   protected FormCheckBox allowOutsideCharacters;
   protected FormCheckBox forcedPvP;

   public HostSettingsForm(GameMessage var1, Runnable var2) {
      super((String)"hostSettingsForm", 320, 400);
      FormFlow var3 = new FormFlow(4);
      this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "steamlobbytype", new FontOptions(16), -1, 4, 0), 5));
      this.lobbyTypeSelect = (FormDropdownSelectionButton)this.addComponent((FormDropdownSelectionButton)var3.nextY(new FormDropdownSelectionButton(5, 0, FormInputSize.SIZE_20, ButtonColor.BASE, this.getWidth() - 10), 5));
      ServerSettings.SteamLobbyType[] var4 = ServerSettings.SteamLobbyType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ServerSettings.SteamLobbyType var7 = var4[var6];
         this.lobbyTypeSelect.options.add(var7, var7.displayName);
      }

      this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "playerslots", new FontOptions(16), -1, 4, 0)));
      this.slotsSlider = (FormSlider)this.addComponent((FormSlider)var3.nextY(new FormSlider("", 10, 0, 10, 1, 25, this.getWidth() - 20, new FontOptions(12)), 5));
      this.slotsSlider.drawValueInPercent = false;
      this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "password", new FontOptions(16), -1, 4, 0)));
      this.passwordInput = (FormTextInput)this.addComponent((FormPasswordInput)var3.nextY(new FormPasswordInput(4, 0, FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, 50), 8));
      this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "passwordtip", new FontOptions(12), -1, 4, 0), 10));
      this.allowOutsideCharacters = (FormCheckBox)this.addComponent((FormLocalCheckBox)var3.nextY(new FormLocalCheckBox("ui", "allowoutsidecharactersbox", 5, 0), 5));
      this.allowOutsideCharacters.handleClicksIfNoEventHandlers = true;
      this.forcedPvP = (FormCheckBox)this.addComponent((FormLocalCheckBox)var3.nextY(new FormLocalCheckBox("ui", "forcedpvpbox", 5, 0), 5));
      this.forcedPvP.handleClicksIfNoEventHandlers = true;
      var3.next(5);
      this.allowConnectByIP = (FormCheckBox)this.addComponent((FormLocalCheckBox)var3.nextY(new FormLocalCheckBox("ui", "allowconnectbyip", 5, 0), 5));
      this.allowConnectByIP.onClicked((var1x) -> {
         this.portInputHide.setHidden(!((FormCheckBox)var1x.from).checked);
      });
      this.portInputHide = (FormComponentList)this.addComponent(new FormComponentList());
      this.portInputHide.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "hostport", new FontOptions(16), -1, 4, 0)));
      this.portInput = (FormTextInput)this.portInputHide.addComponent((FormTextInput)var3.nextY(new FormTextInput(4, 0, FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, 6), 10));
      this.portInput.setRegexMatchFull("[0-9]+");
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "hoststart", 4, this.getHeight() - 40, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.host();
      });
      if (var1 != null) {
         ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var1, this.getWidth() / 2 + 2, this.getHeight() - 40, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
            var2.run();
         });
      }

      this.reset((WorldSettings)null);
   }

   public void reset(WorldSettings var1) {
      this.lobbyTypeSelect.setSelected(ServerSettings.SteamLobbyType.Open, ServerSettings.SteamLobbyType.Open.displayName);
      this.allowConnectByIP.checked = true;
      this.portInputHide.setHidden(false);
      this.portInput.setText(Integer.toString(Settings.serverPort));
      this.slotsSlider.setValue(Settings.serverSlots);
      this.allowOutsideCharacters.checked = var1 != null && var1.allowOutsideCharacters;
      this.forcedPvP.checked = var1 != null && var1.forcedPvP;
   }

   protected void host() {
      int var1 = 14159;

      int var2;
      try {
         var2 = Integer.parseInt(this.portInput.getText());
         if (var2 >= 0 && var2 <= 65535) {
            var1 = var2;
         } else {
            GameLog.warn.println("Port must be between 0 and 65535. Using default " + var1);
         }
      } catch (Exception var6) {
         GameLog.warn.println("Could not parse port " + this.portInput.getText() + ", using default " + var1);
      }

      var2 = this.slotsSlider.getValue();
      boolean var3 = this.allowConnectByIP.checked;
      ServerSettings.SteamLobbyType var4 = (ServerSettings.SteamLobbyType)this.lobbyTypeSelect.getSelected();
      ServerHostSettings var5 = new ServerHostSettings();
      var5.allowOutsideCharacters = this.allowOutsideCharacters.checked;
      var5.forcedPvP = this.forcedPvP.checked;
      var5.password = this.passwordInput.getText();
      this.onHosted(var2, var1, var3, var4, var5);
   }

   public abstract void onHosted(int var1, int var2, boolean var3, ServerSettings.SteamLobbyType var4, ServerHostSettings var5);
}
