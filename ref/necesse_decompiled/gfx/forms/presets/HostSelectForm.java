package necesse.gfx.forms.presets;

import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainMenu;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;

public abstract class HostSelectForm extends WorldSelectForm {
   private HostSettingsForm hostSettingsForm = (HostSettingsForm)this.addComponent(new HostSettingsForm(new LocalMessage("ui", "cancelbutton"), () -> {
      this.makeCurrent(this.selectForm);
   }) {
      public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
         super.handleInputEvent(var1, var2, var3);
         if (!var1.isUsed() && var1.state && var1.getID() == 256 && HostSelectForm.this.isCurrent(HostSelectForm.this.hostSettingsForm)) {
            HostSelectForm.this.makeCurrent(HostSelectForm.this.selectForm);
            var1.use();
         }

      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
         super.handleControllerEvent(var1, var2, var3);
         if (!var1.isUsed() && var1.buttonState && (var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && HostSelectForm.this.isCurrent(HostSelectForm.this.hostSettingsForm)) {
            HostSelectForm.this.makeCurrent(HostSelectForm.this.selectForm);
            var1.use();
         }

      }

      public void onHosted(int var1, int var2, boolean var3, ServerSettings.SteamLobbyType var4, ServerHostSettings var5) {
         ServerSettings var6 = ServerSettings.HostServer(HostSelectForm.this.selectedWorldSave.filePath, var1, var2);
         if (HostSelectForm.this.selectedWorldSave.serverSettings != null) {
            var6.spawnSeed = HostSelectForm.this.selectedWorldSave.serverSettings.spawnSeed;
            var6.spawnIsland = HostSelectForm.this.selectedWorldSave.serverSettings.spawnIsland;
            var6.spawnGuide = HostSelectForm.this.selectedWorldSave.serverSettings.spawnGuide;
         }

         var6.allowConnectByIP = var3;
         var6.steamLobbyType = var4;
         HostSelectForm.this.onHosted(HostSelectForm.this.selectedWorldSave, var6, var5);
      }
   });
   private WorldSave selectedWorldSave;

   public HostSelectForm(MainMenu var1, GameMessage var2) {
      super(var1, var2);
      this.onWindowResized();
   }

   public void onSelected(WorldSave var1, boolean var2) {
      this.selectedWorldSave = var1;
      this.makeCurrent(this.hostSettingsForm);
      this.hostSettingsForm.reset(var1.worldSettings());
   }

   public abstract void onHosted(WorldSave var1, ServerSettings var2, ServerHostSettings var3);

   public void onWindowResized() {
      super.onWindowResized();
      if (this.hostSettingsForm != null) {
         this.hostSettingsForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      }

   }
}
