package necesse.gfx.forms.presets;

import java.util.function.Consumer;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketOpenPvPTeams;
import necesse.engine.network.packet.PacketServerLevelStats;
import necesse.engine.network.packet.PacketServerWorldStats;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.state.MainGame;
import necesse.engine.util.GameUtils;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.lists.FormSteamFriendsInviteList;
import necesse.gfx.forms.components.lists.FormSteamFriendsList;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.PvPTeamsContainerForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelectorForm;
import necesse.gfx.ui.ButtonColor;

public class PauseMenuForm extends FormSwitcher {
   protected boolean hidden;
   public SettingsForm settings;
   public FormComponentList main;
   public Form mainForm;
   public Form mainSideButtons;
   public HostSettingsForm hostWorldForm;
   public Form inviteFriendsForm;
   public FormSteamFriendsList friendsList;
   public PlayerStatsSelectorForm stats;
   public Consumer<PlayerStats> onServerLevelStatsLoad;
   public Consumer<PlayerStats> onServerWorldStatsLoad;
   public FeedbackForm feedback;
   public MainGame mainGame;
   public Client client;

   public PauseMenuForm(MainGame var1, final Client var2) {
      this.mainGame = var1;
      this.client = var2;
      this.main = (FormComponentList)this.addComponent(new FormComponentList());
      this.mainForm = (Form)this.main.addComponent(new Form("main", 400, 200));
      this.setupMainForm();
      this.mainSideButtons = (Form)this.main.addComponent(new Form("mainButtons", 32, 32));
      this.mainSideButtons.setPosition(new FormRelativePosition(this.mainForm, () -> {
         return this.mainForm.getWidth() + Settings.UI.formSpacing;
      }, () -> {
         return this.mainForm.getHeight() - this.mainSideButtons.getHeight();
      }));
      FormFlow var4 = new FormFlow();
      ((FormContentIconButton)this.mainSideButtons.addComponent(new FormContentIconButton(0, var4.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.steam_logo, new GameMessage[]{new LocalMessage("misc", "followsteamnews")}))).onClicked((var0) -> {
         GameUtils.openURL("https://store.steampowered.com/news/app/1169040");
      });
      ((FormContentIconButton)this.mainSideButtons.addComponent(new FormContentIconButton(0, var4.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.discord_logo, new GameMessage[]{new LocalMessage("misc", "joindiscord")}))).onClicked((var0) -> {
         GameUtils.openURL("https://discord.com/invite/FAFgrKD");
      });
      ((FormContentIconButton)this.mainSideButtons.addComponent(new FormContentIconButton(0, var4.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.world_icon, new GameMessage[]{new LocalMessage("settingsui", "language")}))).onClicked((var1x) -> {
         this.makeCurrent(this.settings);
         this.settings.makeLanguageCurrent();
      });
      this.mainSideButtons.setHeight(var4.next());
      this.hostWorldForm = (HostSettingsForm)this.addComponent(new HostSettingsForm(new LocalMessage("ui", "backbutton"), () -> {
         this.makeCurrent(this.main);
      }) {
         public void onHosted(int var1, int var2x, boolean var3, ServerSettings.SteamLobbyType var4, ServerHostSettings var5) {
            Server var6 = var2.getLocalServer();
            if (var6 != null) {
               var6.startHostFromSingleplayer(var1, var2x, var3, var4, var5);
               PauseMenuForm.this.makeCurrent(PauseMenuForm.this.main);
               PauseMenuForm.this.setupMainForm();
            } else {
               System.err.println("Could not find local server for hosting");
            }

         }
      });
      this.inviteFriendsForm = (Form)this.addComponent(new Form(300, 400));
      this.friendsList = (FormSteamFriendsList)this.inviteFriendsForm.addComponent(new FormSteamFriendsInviteList(var2, 0, 0, this.inviteFriendsForm.getWidth(), this.inviteFriendsForm.getHeight() - 40));
      ((FormLocalTextButton)this.inviteFriendsForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.inviteFriendsForm.getHeight() - 40, this.inviteFriendsForm.getWidth() - 8))).onClicked((var1x) -> {
         this.makeCurrent(this.main);
      });
      this.stats = (PlayerStatsSelectorForm)this.addComponent(new PlayerStatsSelectorForm(true) {
         public void backPressed() {
            PauseMenuForm.this.makeCurrent(PauseMenuForm.this.main);
         }
      });
      this.stats.addStatsOption(new LocalMessage("ui", "characterstats"), new LocalMessage("ui", "characterstatstip"), (PlayerStats)var2.characterStats);
      if (GlobalData.isDevMode()) {
         this.onServerLevelStatsLoad = this.stats.addStatsOption(new StaticMessage("DEBUGGING: Level stats"), new StaticMessage("Player stats happened on this level"), (Runnable)(() -> {
            var2.network.sendPacket(new PacketServerLevelStats());
         }));
      }

      this.onServerWorldStatsLoad = this.stats.addStatsOption(new LocalMessage("ui", "worldstats"), new LocalMessage("ui", "worldstatstip"), (Runnable)(() -> {
         var2.network.sendPacket(new PacketServerWorldStats());
      }));
      if (!var2.worldSettings.achievementsEnabled()) {
         this.disableAchievements();
      }

      this.settings = (SettingsForm)this.addComponent(new SettingsForm(var2) {
         public void backPressed() {
            PauseMenuForm.this.makeCurrent(PauseMenuForm.this.main);
            Settings.loadClientSettings();
         }
      }, (var0, var1x) -> {
         if (var1x) {
            var0.load();
         }

      });
      this.feedback = (FeedbackForm)this.addComponent(new FeedbackForm("MainGame", 400, 200) {
         public void backPressed() {
            PauseMenuForm.this.makeCurrent(PauseMenuForm.this.main);
         }
      });
      this.onWindowResized();
   }

   public void setupMainForm() {
      this.mainForm.clearComponents();
      FormFlow var1 = new FormFlow();
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "resumegame", 4, var1.next(40), this.mainForm.getWidth() - 8))).onClicked((var1x) -> {
         this.mainGame.setRunning(true);
      });
      if (this.client.isSingleplayer()) {
         ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "hostworld", 4, var1.next(40), this.mainForm.getWidth() - 8))).onClicked((var1x) -> {
            this.hostWorldForm.reset(this.client.worldSettings);
            this.makeCurrent(this.hostWorldForm);
         });
      } else {
         ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "pvpandteams", 4, var1.next(40), this.mainForm.getWidth() - 8))).onClicked((var1x) -> {
            ((FormButton)var1x.from).startCooldown(500);
            this.mainGame.setRunning(true);
            this.client.network.sendPacket(new PacketOpenPvPTeams());
            PvPTeamsContainerForm.pauseGameOnClose = true;
         });
         ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "invitefriends", 4, var1.next(40), this.mainForm.getWidth() - 8))).onClicked((var1x) -> {
            this.friendsList.reset();
            this.makeCurrent(this.inviteFriendsForm);
         });
      }

      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "achievementsandstats", 4, var1.next(40), this.mainForm.getWidth() - 8))).onClicked((var1x) -> {
         this.makeCurrent(this.stats);
         this.stats.reset();
      });
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("settingsui", "front", 4, var1.next(40), this.mainForm.getWidth() - 8))).onClicked((var1x) -> {
         this.makeCurrent(this.settings);
      });
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "givefeedback", 4, var1.next(40), this.mainForm.getWidth() - 8))).onClicked((var1x) -> {
         this.makeCurrent(this.feedback);
      });
      FormLocalTextButton var2 = (FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "tomain", 4, var1.next(40), this.mainForm.getWidth() - 8));
      var2.onClicked((var1x) -> {
         this.mainGame.disconnect("Quit");
      });
      var2.controllerDownFocus = var2;
      this.mainForm.setHeight(var1.next());
   }

   public void submitEscapeEvent(InputEvent var1) {
      if (!var1.isUsed()) {
         if (this.isCurrent(this.settings)) {
            this.settings.submitEscapeEvent(var1);
            if (var1.isUsed()) {
               return;
            }

            this.makeCurrent(this.main);
            Settings.loadClientSettings();
            var1.use();
         } else if (this.isCurrent(this.stats)) {
            this.stats.submitEscapeEvent(var1);
            if (var1.isUsed()) {
               return;
            }

            this.makeCurrent(this.main);
            var1.use();
         } else if (this.isCurrent(this.hostWorldForm)) {
            this.makeCurrent(this.main);
            var1.use();
         } else if (this.isCurrent(this.inviteFriendsForm)) {
            this.makeCurrent(this.main);
            var1.use();
         } else if (this.isCurrent(this.feedback)) {
            this.feedback.submitEscapeEvent(var1);
         } else if (this.isCurrent(this.main)) {
            this.mainGame.setRunning(true);
            Settings.loadClientSettings();
            var1.use();
         }

      }
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.hostWorldForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.inviteFriendsForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.mainForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public boolean shouldDraw() {
      return super.shouldDraw() && !this.isHidden();
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean var1) {
      this.hidden = var1;
      if (!var1) {
         this.makeCurrent(this.main);
      }

   }

   public void disableAchievements() {
      this.stats.disableAchievements();
   }

   public void applyServerLevelStatsPacket(PacketServerLevelStats var1) {
      this.onServerLevelStatsLoad.accept(var1.stats);
   }

   public void applyServerWorldStatsPacket(PacketServerWorldStats var1) {
      this.onServerWorldStatsLoad.accept(var1.stats);
   }
}
