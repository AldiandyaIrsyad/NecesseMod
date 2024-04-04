package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainMenu;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormServerList;
import necesse.gfx.forms.components.lists.FormSteamFriendsJoinList;
import necesse.gfx.forms.components.lists.FormSteamFriendsList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelectorForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class MainMenuForm extends FormSwitcher {
   protected boolean hidden;
   public FeedbackForm feedback;
   public SettingsForm settings;
   public NewSaveForm newWorld;
   public HostSelectForm host;
   public FormComponentList main;
   public Form mainForm;
   public Form mainSideButtons;
   public Form multiplayer;
   public Form multiplayerAdd;
   public Form multiplayerDirect;
   public Form joinFriend;
   public FormSteamFriendsList friendsList;
   public PlayerStatsSelectorForm stats;
   public ModsForm mods;
   public ConfirmationForm confirmForm;
   public MainMenu menu;
   public WorldSelectForm loadForm;
   protected FormServerList serversList;
   protected FormLocalTextButton continueButton;
   protected Runnable continueLast;
   protected FormLocalTextButton mDelete;
   protected FormLocalTextButton mJoin;
   protected FormTextInput mDirectIP;
   protected FormTextInput mDirectPort;
   protected FormTextInput mAddName;
   protected FormTextInput mAddIP;
   protected FormTextInput mAddPort;

   public MainMenuForm(final MainMenu var1) {
      this.menu = var1;
      this.main = (FormComponentList)this.addComponent(new FormComponentList(), (var1x, var2) -> {
         if (var2) {
            this.updateContinueButton();
         }

      });
      this.mainForm = (Form)this.main.addComponent(new Form("main", 400, 40));
      int var3 = this.mainForm.getWidth() - 8;
      FormFlow var4 = new FormFlow();
      this.continueButton = (FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "continueworld", 4, var4.next(40), var3));
      this.continueButton.onClicked((var1x) -> {
         if (this.continueLast != null) {
            this.continueLast.run();
         }

      });
      this.continueButton.prioritizeControllerFocus();
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "newworld", 4, var4.next(40), var3))).onClicked((var1x) -> {
         this.makeCurrent(this.newWorld);
         this.newWorld.onStarted();
      });
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "loadworld", 4, var4.next(40), var3))).onClicked((var1x) -> {
         this.makeCurrent(this.loadForm);
      });
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "hostworld", 4, var4.next(40), var3))).onClicked((var1x) -> {
         this.makeCurrent(this.host);
      });
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "multiplayer", 4, var4.next(40), var3))).onClicked((var1x) -> {
         this.serversList.loadServerList();
         this.makeCurrent(this.multiplayer);
      });
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "settings", 4, var4.next(40), var3))).onClicked((var1x) -> {
         this.makeCurrent(this.settings);
      });
      if (GlobalData.isDevMode() || !ModLoader.getAllMods().isEmpty()) {
         ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "mods", 4, var4.next(40), var3))).onClicked((var1x) -> {
            this.mods.resetModsList();
            this.makeCurrent(this.mods);
         });
      }

      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "achievementsandstats", 4, var4.next(40), var3))).onClicked((var1x) -> {
         this.makeCurrent(this.stats);
         this.stats.reset();
      });
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "givefeedback", 4, var4.next(40), var3))).onClicked((var1x) -> {
         this.makeCurrent(this.feedback);
      });
      FormLocalTextButton var5 = (FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "exit", 4, var4.next(40), var3));
      var5.onClicked((var0) -> {
         Screen.requestClose();
      });
      var5.controllerDownFocus = var5;
      this.mainForm.setHeight(var4.next());
      this.mainSideButtons = (Form)this.main.addComponent(new Form("mainButtons", 32, 32));
      this.mainSideButtons.setPosition(new FormRelativePosition(this.mainForm, () -> {
         return this.mainForm.getWidth() + Settings.UI.formSpacing;
      }, () -> {
         return this.mainForm.getHeight() - this.mainSideButtons.getHeight();
      }));
      FormFlow var6 = new FormFlow();
      ((FormContentIconButton)this.mainSideButtons.addComponent(new FormContentIconButton(0, var6.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.camera_pan, new GameMessage[]{new LocalMessage("ui", "camerapantoggle")}))).onClicked((var1x) -> {
         var1.toggleCameraPanSetting();
      });
      ((FormContentIconButton)this.mainSideButtons.addComponent(new FormContentIconButton(0, var6.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.steam_logo, new GameMessage[]{new LocalMessage("misc", "followsteamnews")}))).onClicked((var0) -> {
         GameUtils.openURL("https://store.steampowered.com/news/app/1169040");
      });
      ((FormContentIconButton)this.mainSideButtons.addComponent(new FormContentIconButton(0, var6.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.discord_logo, new GameMessage[]{new LocalMessage("misc", "joindiscord")}))).onClicked((var0) -> {
         GameUtils.openURL("https://discord.com/invite/FAFgrKD");
      });
      ((FormContentIconButton)this.mainSideButtons.addComponent(new FormContentIconButton(0, var6.next(32), FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.world_icon, new GameMessage[]{new LocalMessage("settingsui", "language")}))).onClicked((var1x) -> {
         this.makeCurrent(this.settings);
         this.settings.makeLanguageCurrent();
      });
      this.mainSideButtons.setHeight(var6.next());
      this.newWorld = (NewSaveForm)this.addComponent(new NewSaveForm() {
         public void createPressed(ServerSettings var1x) {
            var1.startSingleplayer((WorldSave)null, var1x, (MainMenu.ConnectFrom)null);
         }

         public void error(String var1x) {
            var1.notice(var1x);
         }

         public void backPressed() {
            MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
         }
      });
      this.loadForm = (WorldSelectForm)this.addComponent(new WorldSelectForm(var1, new LocalMessage("ui", "backbutton")) {
         public void onSelected(WorldSave var1x, boolean var2) {
            if (var1x != null) {
               ServerSettings var3 = ServerSettings.SingleplayerServer(var1x.filePath);
               if (var1x.serverSettings != null) {
                  var3.spawnSeed = var1x.serverSettings.spawnSeed;
                  var3.spawnIsland = var1x.serverSettings.spawnIsland;
                  var3.spawnGuide = var1x.serverSettings.spawnGuide;
               }

               var1.startSingleplayer(var1x, var3, MainMenu.ConnectFrom.Load);
            }
         }

         public void onBackPressed() {
            MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
         }
      }, (var1x, var2) -> {
         if (var2) {
            this.loadForm.loadWorlds();
         }

      });
      this.host = (HostSelectForm)this.addComponent(new HostSelectForm(var1, new LocalMessage("ui", "backbutton")) {
         public void onHosted(WorldSave var1, ServerSettings var2, ServerHostSettings var3) {
            try {
               this.mainMenu.host(var1, var2, var3, MainMenu.ConnectFrom.Host);
            } catch (Exception var5) {
               this.mainMenu.notice("Error hosting " + var1.filePath.getName());
               var5.printStackTrace();
            }

         }

         public void onBackPressed() {
            MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
         }
      }, (var1x, var2) -> {
         if (var2) {
            this.host.loadWorlds();
         }

      });
      this.multiplayer = (Form)this.addComponent(new Form("multiplayer", 400, 460));
      FormFlow var7 = new FormFlow();
      this.serversList = (FormServerList)this.multiplayer.addComponent((FormServerList)var7.nextY(new FormServerList(0, 0, this.multiplayer.getWidth(), 280)));
      this.serversList.onDoubleSelect((var2) -> {
         this.serversList.connect(var1);
      });
      ((FormLocalTextButton)this.multiplayer.addComponent(new FormLocalTextButton("ui", "joinfriend", 4, var7.next(40), this.multiplayer.getWidth() - 8))).onClicked((var1x) -> {
         this.makeCurrent(this.joinFriend);
         this.friendsList.reset();
      });
      int var8 = var7.next(40);
      this.mJoin = (FormLocalTextButton)this.multiplayer.addComponent(new FormLocalTextButton("ui", "joinserver", 4, var8, this.multiplayer.getWidth() / 2 - 6));
      this.mJoin.onClicked((var2) -> {
         this.serversList.connect(var1);
      });
      ((FormLocalTextButton)this.multiplayer.addComponent(new FormLocalTextButton("ui", "directjoin", this.multiplayer.getWidth() / 2 + 2, var8, this.multiplayer.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.multiplayerDirect);
         this.mDirectIP.setText("");
         this.mDirectPort.setText(Integer.toString(14159));
         this.mDirectIP.setTyping(true);
      });
      var8 = var7.next(40);
      ((FormLocalTextButton)this.multiplayer.addComponent(new FormLocalTextButton("ui", "addserver", 4, var8, this.multiplayer.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.multiplayerAdd);
         this.mAddName.setText("");
         this.mAddIP.setText("");
         this.mAddPort.setText(Integer.toString(14159));
         this.mAddName.setTyping(true);
      });
      this.mDelete = (FormLocalTextButton)this.multiplayer.addComponent(new FormLocalTextButton("ui", "deleteserver", this.multiplayer.getWidth() / 2 + 2, var8, this.multiplayer.getWidth() / 2 - 6));
      this.mDelete.onClicked((var1x) -> {
         if (this.serversList.hasSelected()) {
            this.confirmForm.setupConfirmation((GameMessage)(new LocalMessage("ui", "confirmdeleteserver", "server", this.serversList.getSelectedName())), () -> {
               this.serversList.deleteSelected();
               this.makeCurrent(this.multiplayer);
            }, () -> {
               this.makeCurrent(this.multiplayer);
            });
            this.makeCurrent(this.confirmForm);
         }

      });
      var8 = var7.next(40);
      ((FormLocalTextButton)this.multiplayer.addComponent(new FormLocalTextButton("ui", "refreshservers", 4, var8, this.multiplayer.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.serversList.refresh();
         this.serversList.startLanSearch();
      });
      ((FormLocalTextButton)this.multiplayer.addComponent(new FormLocalTextButton("ui", "backbutton", this.multiplayer.getWidth() / 2 + 2, var8, this.multiplayer.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.main);
      });
      this.multiplayer.setHeight(var7.next());
      this.multiplayerDirect = (Form)this.addComponent(new Form("multiplayerDirect", 320, 200));
      ((FormLocalTextButton)this.multiplayerDirect.addComponent(new FormLocalTextButton("ui", "backbutton", this.multiplayerDirect.getWidth() / 2 + 2, this.multiplayerDirect.getHeight() - 40, this.multiplayerDirect.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.multiplayer);
      });
      ((FormLocalTextButton)this.multiplayerDirect.addComponent(new FormLocalTextButton("ui", "connectserver", 4, this.multiplayerDirect.getHeight() - 40, this.multiplayerDirect.getWidth() / 2 - 6))).onClicked((var2) -> {
         int var3;
         try {
            var3 = Integer.parseInt(this.mDirectPort.getText());
            if (var3 < 0 || var3 > 65535) {
               throw new Exception("Invalid port");
            }
         } catch (Exception var5) {
            var3 = 14159;
            GameLog.warn.println("Invalid port, used default");
         }

         String var4 = this.mDirectIP.getText();
         var1.connect((String)null, var4, var3, MainMenu.ConnectFrom.Multiplayer);
      });
      this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "ipdesc", new FontOptions(20), -1, 10, 0));
      this.mDirectIP = (FormTextInput)this.multiplayerDirect.addComponent(new FormTextInput(4, 20, FormInputSize.SIZE_32_TO_40, this.multiplayerDirect.getWidth() - 8, 50));
      this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "ipexample", new FontOptions(12), -1, 10, 60));
      this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "portdesc", new FontOptions(20), -1, 10, 80));
      this.mDirectPort = (FormTextInput)this.multiplayerDirect.addComponent(new FormTextInput(4, 100, FormInputSize.SIZE_32_TO_40, this.multiplayerDirect.getWidth() - 8, 7));
      this.mDirectPort.setRegexMatchFull("[0-9]+");
      this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "portexample", new FontOptions(12), -1, 10, 140));
      this.mDirectIP.tabTypingComponent = this.mDirectPort;
      this.mDirectPort.tabTypingComponent = this.mDirectIP;
      this.multiplayerAdd = (Form)this.addComponent(new Form("multiplayerAdd", 320, 280));
      ((FormLocalTextButton)this.multiplayerAdd.addComponent(new FormLocalTextButton("ui", "backbutton", this.multiplayerAdd.getWidth() / 2 + 2, this.multiplayerAdd.getHeight() - 40, this.multiplayerAdd.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.makeCurrent(this.multiplayer);
      });
      ((FormLocalTextButton)this.multiplayerAdd.addComponent(new FormLocalTextButton("ui", "addconfirm", 4, this.multiplayerAdd.getHeight() - 40, this.multiplayerAdd.getWidth() / 2 - 6))).onClicked((var1x) -> {
         int var2;
         try {
            var2 = Integer.parseInt(this.mAddPort.getText());
            if (var2 < 0 || var2 > 65535) {
               throw new Exception("Invalid port");
            }
         } catch (Exception var6) {
            var2 = 14159;
            GameLog.warn.println("Invalid port, used default.");
         }

         String var3 = this.mAddIP.getText();

         try {
            this.serversList.addServer(this.mAddName.getText(), var3, var2);
         } catch (IllegalArgumentException var5) {
            System.err.println("Could not add server.");
            var5.printStackTrace();
         }

         this.makeCurrent(this.multiplayer);
      });
      this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "namedesc", new FontOptions(20), -1, 10, 0));
      this.mAddName = (FormTextInput)this.multiplayerAdd.addComponent(new FormTextInput(4, 20, FormInputSize.SIZE_32_TO_40, this.multiplayerAdd.getWidth() - 8, 50));
      this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "nameexample", new FontOptions(12), -1, 10, 60));
      this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "ipdesc", new FontOptions(20), -1, 10, 80));
      this.mAddIP = (FormTextInput)this.multiplayerAdd.addComponent(new FormTextInput(4, 100, FormInputSize.SIZE_32_TO_40, this.multiplayerAdd.getWidth() - 8, 50));
      this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "ipexample", new FontOptions(12), -1, 10, 140));
      this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "portdesc", new FontOptions(20), -1, 10, 160));
      this.mAddPort = (FormTextInput)this.multiplayerAdd.addComponent(new FormTextInput(4, 180, FormInputSize.SIZE_32_TO_40, this.multiplayerAdd.getWidth() - 8, 7));
      this.mAddPort.setRegexMatchFull("[0-9]+");
      this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "portexample", new FontOptions(12), -1, 10, 220));
      this.mAddName.tabTypingComponent = this.mAddIP;
      this.mAddIP.tabTypingComponent = this.mAddPort;
      this.mAddPort.tabTypingComponent = this.mAddName;
      this.joinFriend = (Form)this.addComponent(new Form(300, 400));
      FormLocalLabel var9 = (FormLocalLabel)this.joinFriend.addComponent(new FormLocalLabel("ui", "joinfriendhow", new FontOptions(16), 0, this.joinFriend.getWidth() / 2, 10, this.joinFriend.getWidth() - 20));
      int var10 = var9.getHeight() + 20;
      this.friendsList = (FormSteamFriendsList)this.joinFriend.addComponent(new FormSteamFriendsJoinList(var1, 0, var10, this.joinFriend.getWidth(), this.joinFriend.getHeight() - 40 - var10));
      ((FormLocalTextButton)this.joinFriend.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.joinFriend.getHeight() - 40, this.joinFriend.getWidth() - 8))).onClicked((var1x) -> {
         this.makeCurrent(this.multiplayer);
      });
      this.confirmForm = (ConfirmationForm)this.addComponent(new ConfirmationForm("confirm"));
      this.settings = (SettingsForm)this.addComponent(new SettingsForm((Client)null) {
         public void backPressed() {
            Settings.loadClientSettings();
            MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
         }
      }, (var0, var1x) -> {
         if (var1x) {
            var0.load();
         }

      });
      this.mods = (ModsForm)this.addComponent(new ModsForm(var1, "mods") {
         public void backPressed() {
            MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
         }
      }, (var0, var1x) -> {
         if (var1x) {
            var0.resetCurrent();
         }

      });
      this.stats = (PlayerStatsSelectorForm)this.addComponent(new PlayerStatsSelectorForm(true) {
         public void backPressed() {
            MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
         }
      });
      this.feedback = (FeedbackForm)this.addComponent(new FeedbackForm("MainMenu", 400, 200) {
         public void backPressed() {
            MainMenuForm.this.makeCurrent(MainMenuForm.this.main);
         }
      });
      this.onWindowResized();
      this.makeCurrent(this.main);
   }

   public void submitEscapeEvent(InputEvent var1) {
      if (!var1.isUsed()) {
         if (this.isCurrent(this.settings)) {
            this.makeCurrent(this.main);
            Settings.loadClientSettings();
            var1.use();
         } else if (this.isCurrent(this.loadForm)) {
            this.makeCurrent(this.main);
            var1.use();
         } else if (!this.isCurrent(this.multiplayerAdd) && !this.isCurrent(this.multiplayerDirect) && !this.isCurrent(this.joinFriend)) {
            if (this.isCurrent(this.stats)) {
               this.stats.submitEscapeEvent(var1);
               if (var1.isUsed()) {
                  return;
               }

               this.makeCurrent(this.main);
               var1.use();
            } else if (this.isCurrent(this.feedback)) {
               this.feedback.submitEscapeEvent(var1);
            } else {
               this.makeCurrent(this.main);
               var1.use();
            }
         } else {
            this.makeCurrent(this.multiplayer);
            var1.use();
         }

      }
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isCurrent(this.multiplayer)) {
         this.mDelete.setActive(this.serversList.canDeleteSelected());
         this.mJoin.setActive(this.serversList.hasSelected());
      }

      super.draw(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.mainForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.multiplayer.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.multiplayerAdd.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.multiplayerDirect.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.joinFriend.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.confirmForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void updateContinueButton() {
      ObjectValue var1 = this.menu.loadContinueCacheSave();
      if (var1 == null) {
         this.continueButton.setLocalization(new LocalMessage("ui", "continueworld"));
         this.continueButton.setActive(false);
         this.continueLast = null;
      } else {
         this.continueButton.setLocalization((GameMessage)var1.object);
         this.continueButton.setActive(true);
         this.continueLast = (Runnable)var1.value;
      }

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
         this.setConnectedFromCurrent();
      }

   }

   public void setConnectedFromCurrent() {
      if (this.menu.connectedFrom != null) {
         switch (this.menu.connectedFrom) {
            case Load:
               this.makeCurrent(this.loadForm);
               break;
            case Multiplayer:
               this.makeCurrent(this.multiplayer);
               break;
            case Host:
               this.makeCurrent(this.host);
               break;
            default:
               this.makeCurrent(this.main);
         }
      } else {
         this.makeCurrent(this.main);
      }

   }
}
