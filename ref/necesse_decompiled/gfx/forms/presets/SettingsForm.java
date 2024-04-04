package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import necesse.engine.DisplayMode;
import necesse.engine.GameCache;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameDifficulty;
import necesse.engine.GameRaidFrequency;
import necesse.engine.GameWindow;
import necesse.engine.SceneColorSetting;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.WindowUtils;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketSettings;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.world.World;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ButtonOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCursorPreview;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.lists.FormControlList;
import necesse.gfx.forms.components.lists.FormLanguageList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.GameInterfaceStyle;
import necesse.level.maps.Level;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.system.Platform;

public class SettingsForm extends FormSwitcher {
   private boolean hidden;
   private Form mainMenu;
   private Form world;
   private Form general;
   private Form language;
   private Form controls;
   private Form interf;
   private Form graphics;
   private Form sound;
   private DifficultySelectForm difficultyForm;
   private ConfirmationForm confirmation;
   private long timerConfirmEnd;
   private Runnable checkConfirmTimerComplete;
   private FormLocalTextButton mainMenuWorld;
   private Runnable customSave;
   private Runnable customLoad;
   private boolean saveActive;
   private FormContentBox worldContent;
   private int worldContentHeight;
   private FormLocalTextButton worldSave;
   private FormLocalTextButton worldBack;
   private FormLocalLabel difficultyLabel;
   private FormDropdownSelectionButton<GameDeathPenalty> deathPenalty;
   private FormDropdownSelectionButton<GameRaidFrequency> raidFrequency;
   private FormLocalCheckBox survivalMode;
   private FormLocalCheckBox playerHunger;
   private FormLocalCheckBox allowOutsideCharacters;
   private FormLocalCheckBox forcedPvP;
   private FormContentBox generalContent;
   private int generalContentHeight;
   private FormLocalTextButton generalSave;
   private FormLocalTextButton generalBack;
   private FormSlider sceneSize;
   private FormLocalCheckBox dynamicSceneSize;
   private FormLocalCheckBox limitCameraToLevelBounds;
   private FormLocalCheckBox pauseOnFocusLoss;
   private FormLocalCheckBox savePerformanceOnFocusLoss;
   private FormLocalCheckBox alwaysSkipTutorial;
   private FormLocalCheckBox showSettlerHeadArmor;
   private FormLocalTextButton clearCache;
   private FormLocalTextButton languageHelp;
   private FormLocalTextButton languageSave;
   private FormLocalTextButton languageBack;
   private Language prevLanguage;
   private FormLanguageList languageList;
   private FormLocalTextButton controlsSave;
   private FormLocalTextButton controlsBack;
   private FormControlList controlList;
   private FormContentBox interfaceContent;
   private int interfaceContentHeight;
   private FormLocalTextButton interfaceSave;
   private FormLocalTextButton interfaceBack;
   private FormHorizontalScroll<Float> interfaceSize;
   private FormDropdownSelectionButton<GameInterfaceStyle> interfaceStyle;
   private FormLocalCheckBox dynamicInterfaceSize;
   private FormLocalCheckBox pixelFont;
   private FormLocalCheckBox showDebugInfo;
   private FormLocalCheckBox showQuestMarkers;
   private FormLocalCheckBox showTeammateMarkers;
   private FormLocalCheckBox showPickupText;
   private FormLocalCheckBox showDamageText;
   private FormLocalCheckBox showDoTText;
   private FormLocalCheckBox showBossHealthBars;
   private FormLocalCheckBox showMobHealthBars;
   private FormLocalCheckBox showIngredientsAvailable;
   private FormLocalCheckBox showItemTooltipBackground;
   private FormLocalCheckBox showBasicTooltipBackground;
   private FormLocalCheckBox bigTooltipText;
   private FormLocalCheckBox showControlTips;
   private FormLocalCheckBox showLogicGateTooltips;
   private FormLocalCheckBox alwaysShowQuickbar;
   public boolean reloadedInterface;
   private FormLocalSlider cursorRed;
   private FormLocalSlider cursorGreen;
   private FormLocalSlider cursorBlue;
   private FormHorizontalIntScroll cursorSize;
   private boolean changedCursorColor;
   private boolean changedCursorSize;
   private FormCursorPreview cursorPreview;
   private FormContentBox graphicsContent;
   private int graphicsContentHeight;
   private FormLocalTextButton graphicsSave;
   private FormLocalTextButton graphicsBack;
   private FormLocalCheckBox smoothLighting;
   private FormLocalCheckBox wavyGrass;
   private FormLocalCheckBox denseGrass;
   private FormLocalCheckBox cameraShake;
   private FormLocalCheckBox vSyncEnabled;
   private FormLocalCheckBox reduceUIFramerate;
   private FormDropdownSelectionButton<DisplayMode> displayMode;
   private FormHorizontalIntScroll monitorScroll;
   private FormDropdownSelectionButton<SceneColorSetting> sceneColors;
   private FormLocalSlider brightness;
   private FormDropdownSelectionButton<Settings.LightSetting> lights;
   private FormDropdownSelectionButton<Settings.ParticleSetting> particles;
   private FormDropdownSelectionButton<Integer> maxFPS;
   private FormDropdownSelectionButton<Dimension> displayScroll;
   private boolean displayChanged;
   private int displayScrollY;
   private FormContentBox soundContent;
   private int soundContentHeight;
   private FormLocalTextButton soundSave;
   private FormLocalTextButton soundBack;
   private FormDropdownSelectionButton<String> outputDevice;
   private List<String> outputDeviceNames;
   private FormLocalSlider masterVolume;
   private FormLocalSlider effectsVolume;
   private FormLocalSlider weatherVolume;
   private FormLocalSlider UIVolume;
   private FormLocalSlider musicVolume;
   private FormLocalCheckBox muteOnFocusLoss;
   private Client client;

   public SettingsForm(Client var1) {
      this.client = var1;
      this.setupMenuForm();
      this.updateWorldForm();
      this.updateGeneralForm();
      this.updateLanguageForm();
      this.updateControlsForm();
      this.updateInterfaceForm();
      this.updateGraphicsForm();
      this.updateSoundForm();
      this.confirmation = (ConfirmationForm)this.addComponent(new ConfirmationForm("confirmation"));
      this.onWindowResized();
      this.makeCurrent(this.mainMenu);
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            SettingsForm.this.updateWorldForm();
            SettingsForm.this.updateGeneralForm();
            SettingsForm.this.updateLanguageForm();
            SettingsForm.this.updateControlsForm();
            SettingsForm.this.updateInterfaceForm();
            SettingsForm.this.updateGraphicsForm();
            SettingsForm.this.updateSoundForm();
            SettingsForm.this.updateComponents();
            SettingsForm.this.onWindowResized();
         }

         public boolean isDisposed() {
            return SettingsForm.this.isDisposed();
         }
      });
   }

   private void setupMenuForm() {
      this.mainMenu = (Form)this.addComponent(new Form("mainMenu", 400, 360));
      this.mainMenu.addComponent(new FormLocalLabel("settingsui", "front", new FontOptions(20), 0, this.mainMenu.getWidth() / 2, 10));
      this.mainMenuWorld = (FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "world", 4, 40, this.mainMenu.getWidth() - 8));
      this.mainMenuWorld.onClicked((var1) -> {
         this.makeWorldCurrent(true);
      });
      this.updateWorldButtonActive();
      ((FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "general", 4, 80, this.mainMenu.getWidth() - 8))).onClicked((var1) -> {
         this.makeGeneralCurrent();
      });
      ((FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "language", 4, 120, this.mainMenu.getWidth() - 8))).onClicked((var1) -> {
         this.makeLanguageCurrent();
      });
      ((FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "controls", 4, 160, this.mainMenu.getWidth() - 8))).onClicked((var1) -> {
         this.makeControlsCurrent();
      });
      ((FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "interface", 4, 200, this.mainMenu.getWidth() - 8))).onClicked((var1) -> {
         this.makeInterfaceCurrent();
      });
      ((FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "graphics", 4, 240, this.mainMenu.getWidth() - 8))).onClicked((var1) -> {
         this.makeGraphicsCurrent();
      });
      ((FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("settingsui", "sound", 4, 280, this.mainMenu.getWidth() - 8))).onClicked((var1) -> {
         this.makeSoundCurrent();
      });
      ((FormLocalTextButton)this.mainMenu.addComponent(new FormLocalTextButton("ui", "backbutton", 4, 320, this.mainMenu.getWidth() - 8))).onClicked((var1) -> {
         this.backPressed();
      });
   }

   public void makeWorldCurrent(boolean var1) {
      this.customSave = () -> {
         if (this.client != null) {
            this.client.worldSettings.difficulty = this.difficultyForm.selectedDifficulty;
            this.client.worldSettings.deathPenalty = (GameDeathPenalty)this.deathPenalty.getSelected();
            this.client.worldSettings.raidFrequency = (GameRaidFrequency)this.raidFrequency.getSelected();
            this.client.worldSettings.playerHunger = this.playerHunger.checked;
            this.client.worldSettings.survivalMode = this.survivalMode.checked;
            this.client.worldSettings.allowOutsideCharacters = this.allowOutsideCharacters.checked;
            this.client.worldSettings.forcedPvP = this.forcedPvP.checked;
            this.client.network.sendPacket(new PacketSettings(this.client.worldSettings));
            if (var1) {
               this.setSaveActive(false);
            }
         }

      };
      this.customLoad = null;
      this.makeCurrent(this.world);
   }

   private void updateWorldForm() {
      if (this.world == null) {
         this.world = (Form)this.addComponent(new Form("world", 400, 40));
      } else {
         this.world.clearComponents();
      }

      this.world.addComponent(new FormLocalLabel("settingsui", "world", new FontOptions(20), 0, this.world.getWidth() / 2, 5));
      this.worldContent = (FormContentBox)this.world.addComponent(new FormContentBox(0, 30, this.world.getWidth(), this.world.getHeight() - 40));
      FormFlow var1 = new FormFlow(5);
      int var2 = Math.min(Math.max(this.worldContent.getWidth() - 100, 300), this.worldContent.getWidth());
      int var3 = this.worldContent.getWidth() / 2 - var2 / 2;
      this.worldContent.addComponent(new FormLocalLabel("ui", "difficulty", new FontOptions(20), 0, this.worldContent.getWidth() / 2, var1.next(24)));
      this.difficultyLabel = (FormLocalLabel)this.worldContent.addComponent(new FormLocalLabel(GameDifficulty.CLASSIC.displayName, new FontOptions(16), 0, this.worldContent.getWidth() / 2, var1.next(20)));
      ((FormLocalTextButton)this.worldContent.addComponent(new FormLocalTextButton("ui", "changedifficulty", var3, var1.next(32), var2, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         this.makeCurrent(this.difficultyForm);
      });
      var1.next(10);
      this.worldContent.addComponent(new FormLocalLabel("ui", "deathpenalty", new FontOptions(20), 0, this.worldContent.getWidth() / 2, var1.next(24)));
      this.deathPenalty = (FormDropdownSelectionButton)this.worldContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, var2));
      GameDeathPenalty[] var4 = GameDeathPenalty.values();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         GameDeathPenalty var7 = var4[var6];
         this.deathPenalty.options.add(var7, var7.displayName, () -> {
            return var7.description;
         });
      }

      this.deathPenalty.setSelected(GameDeathPenalty.NONE, (GameMessage)null);
      this.deathPenalty.onSelected((var1x) -> {
         this.setSaveActive(true);
      });
      var1.next(10);
      this.worldContent.addComponent(new FormLocalLabel("ui", "raidfrequency", new FontOptions(20), 0, this.worldContent.getWidth() / 2, var1.next(24)));
      this.raidFrequency = (FormDropdownSelectionButton)this.worldContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, var2));
      GameRaidFrequency[] var8 = GameRaidFrequency.values();
      var5 = var8.length;

      for(var6 = 0; var6 < var5; ++var6) {
         GameRaidFrequency var9 = var8[var6];
         this.raidFrequency.options.add(var9, var9.displayName, () -> {
            return var9.description;
         });
      }

      this.raidFrequency.onSelected((var1x) -> {
         this.setSaveActive(true);
      });
      var1.next(10);
      this.survivalMode = (FormLocalCheckBox)this.worldContent.addComponent((FormLocalCheckBox)var1.nextY((new FormLocalCheckBox("ui", "survivalmode", 10, 0, this.worldContent.getWidth() - 20)).useButtonTexture(), 4));
      this.survivalMode.onClicked((var1x) -> {
         this.setSaveActive(true);
         this.playerHunger.setActive(!((FormCheckBox)var1x.from).checked);
         if (!this.playerHunger.checked) {
            this.playerHunger.checked = ((FormCheckBox)var1x.from).checked;
         }

      });
      this.raidFrequency.controllerDownFocus = this.survivalMode;
      this.survivalMode.controllerUpFocus = this.raidFrequency;
      this.worldContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "survivalmodetip", new FontOptions(12), -1, 10, 0, this.worldContent.getWidth() - 30), 8));
      this.playerHunger = (FormLocalCheckBox)this.worldContent.addComponent((FormLocalCheckBox)var1.nextY((new FormLocalCheckBox("ui", "playerhungerbox", 10, 0, this.worldContent.getWidth() - 20)).useButtonTexture(), 8));
      this.playerHunger.onClicked((var1x) -> {
         this.setSaveActive(true);
      });
      this.playerHunger.controllerUpFocus = this.survivalMode;
      this.survivalMode.controllerDownFocus = this.playerHunger;
      this.allowOutsideCharacters = (FormLocalCheckBox)this.worldContent.addComponent((FormLocalCheckBox)var1.nextY((new FormLocalCheckBox("ui", "allowoutsidecharactersbox", 10, 0, this.worldContent.getWidth() - 20)).useButtonTexture(), 8));
      this.allowOutsideCharacters.onClicked((var1x) -> {
         this.setSaveActive(true);
      });
      this.forcedPvP = (FormLocalCheckBox)this.worldContent.addComponent((FormLocalCheckBox)var1.nextY((new FormLocalCheckBox("ui", "forcedpvpbox", 10, 0, this.worldContent.getWidth() - 20)).useButtonTexture(), 8));
      this.forcedPvP.onClicked((var1x) -> {
         this.setSaveActive(true);
      });
      var1.next(5);
      this.worldContentHeight = var1.next();
      this.worldSave = (FormLocalTextButton)this.world.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.world.getHeight() - 40, this.world.getWidth() / 2 - 6));
      this.worldSave.onClicked((var1x) -> {
         this.savePressed();
      });
      this.worldBack = (FormLocalTextButton)this.world.addComponent(new FormLocalTextButton("ui", "backbutton", this.world.getWidth() / 2 + 2, this.world.getHeight() - 40, this.world.getWidth() / 2 - 6));
      this.worldBack.onClicked((var1x) -> {
         this.subMenuBackPressed();
      });
      this.updateWorldHeight();
      if (this.difficultyForm == null) {
         this.difficultyForm = (DifficultySelectForm)this.addComponent(new DifficultySelectForm((ButtonOptions)null, ButtonOptions.backButton(() -> {
            this.difficultyLabel.setLocalization(this.difficultyForm.selectedDifficulty.displayName);
            this.makeWorldCurrent(false);
            if (this.client.worldSettings.difficulty != this.difficultyForm.selectedDifficulty) {
               this.setSaveActive(true);
            }

         }), (ButtonOptions)null));
      } else {
         this.difficultyForm.clearComponents();
      }

   }

   public void updateWorldHeight() {
      int var1 = Math.max(100, Screen.getHudHeight() - 100);
      int var2 = Math.min(this.worldContentHeight + 40 + this.worldContent.getY(), var1);
      this.world.setHeight(var2);
      this.worldContent.setHeight(this.world.getHeight() - 40 - this.worldContent.getY());
      this.worldContent.setContentBox(new Rectangle(this.world.getWidth(), this.worldContentHeight));
      this.worldSave.setY(this.world.getHeight() - 40);
      this.worldBack.setY(this.world.getHeight() - 40);
   }

   public void makeGeneralCurrent() {
      this.customSave = null;
      this.customLoad = null;
      this.makeCurrent(this.general);
   }

   private void updateGeneralForm() {
      if (this.general == null) {
         this.general = (Form)this.addComponent(new Form("general", 400, 40));
      } else {
         this.general.clearComponents();
      }

      this.general.addComponent(new FormLocalLabel("settingsui", "general", new FontOptions(20), 0, this.general.getWidth() / 2, 5));
      this.generalContent = (FormContentBox)this.general.addComponent(new FormContentBox(0, 30, this.general.getWidth(), this.general.getHeight() - 40));
      FormFlow var1 = new FormFlow(5);
      this.generalContent.addComponent(new FormLocalLabel("settingsui", "zoomlevel", new FontOptions(16), 0, this.generalContent.getWidth() / 2, var1.next(20)));
      this.sceneSize = (FormSlider)this.generalContent.addComponent((<undefinedtype>)var1.nextY(new FormSlider("", 5, 0, (int)(Settings.sceneSize * 100.0F), (int)(GameWindow.minSceneSize * 100.0F), (int)(GameWindow.maxSceneSize * 100.0F), this.generalContent.getWidth() - 10) {
         public String getValueText() {
            return this.getValue() + "%";
         }
      }));
      this.sceneSize.onChanged((var1x) -> {
         Settings.sceneSize = GameMath.toDecimals((float)((FormSlider)var1x.from).getValue() / 100.0F, 2);
         Screen.updateSceneSize();
         if (this.client != null) {
            this.client.setMessage((int)(Settings.sceneSize * 100.0F) + "%", Color.WHITE, 1.0F);
         }

         this.setSaveActive(true);
      });
      var1.next(15);
      this.dynamicSceneSize = (FormLocalCheckBox)this.generalContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "dynamiczoomlevel", 10, 0, this.generalContent.getWidth() - 20), 8));
      this.dynamicSceneSize.onClicked((var1x) -> {
         Settings.dynamicSceneSize = ((FormCheckBox)var1x.from).checked;
         Screen.updateSceneSize();
         this.setSaveActive(true);
      });
      this.limitCameraToLevelBounds = (FormLocalCheckBox)this.generalContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "limitcamera", 10, 0, this.generalContent.getWidth() - 20), 8));
      this.limitCameraToLevelBounds.onClicked((var1x) -> {
         Settings.limitCameraToLevelBounds = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      var1.next(5);
      this.pauseOnFocusLoss = (FormLocalCheckBox)this.generalContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "pausefocus", 10, 0, this.generalContent.getWidth() - 20), 8));
      this.pauseOnFocusLoss.onClicked((var1x) -> {
         Settings.pauseOnFocusLoss = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.savePerformanceOnFocusLoss = (FormLocalCheckBox)this.generalContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "perffocus", 10, 0, this.generalContent.getWidth() - 20), 8));
      this.savePerformanceOnFocusLoss.onClicked((var1x) -> {
         Settings.savePerformanceOnFocusLoss = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.alwaysSkipTutorial = (FormLocalCheckBox)this.generalContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "skiptutorial", 10, 0, this.generalContent.getWidth() - 20), 8));
      this.alwaysSkipTutorial.onClicked((var1x) -> {
         Settings.alwaysSkipTutorial = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showSettlerHeadArmor = (FormLocalCheckBox)this.generalContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showsettlerheadarmor", 10, 0, this.generalContent.getWidth() - 20), 8));
      this.showSettlerHeadArmor.onClicked((var1x) -> {
         Settings.showSettlerHeadArmor = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      var1.next(40);
      FormLocalTextButton var2 = (FormLocalTextButton)this.generalContent.addComponent(new FormLocalTextButton("settingsui", "opensaves", 10, var1.next(24), this.generalContent.getWidth() - 20, FormInputSize.SIZE_20, ButtonColor.BASE));
      var2.onClicked((var0) -> {
         Thread var1 = new Thread(() -> {
            try {
               File var0 = new File(World.getSavesPath());
               if (!var0.exists()) {
                  var0.mkdirs();
               }

               if (var0.isDirectory() && Desktop.isDesktopSupported()) {
                  Desktop.getDesktop().open(var0);
               }
            } catch (Exception var1) {
               var1.printStackTrace();
            }

         });
         var1.start();
      });
      var2.setCooldown(1000);
      this.clearCache = (FormLocalTextButton)this.generalContent.addComponent(new FormLocalTextButton("settingsui", "resetcache", 10, var1.next(24), this.generalContent.getWidth() - 20, FormInputSize.SIZE_20, ButtonColor.BASE));
      this.clearCache.onClicked((var1x) -> {
         this.confirmation.setupConfirmation((GameMessage)(new LocalMessage("settingsui", "cacheconfirm")), () -> {
            GameCache.clearCacheFolder("client");
            this.clearCache.setActive(false);
            this.makeCurrent(this.general);
         }, () -> {
            this.makeCurrent(this.general);
         });
         this.makeCurrent(this.confirmation);
      });
      if (this.client != null) {
         this.clearCache.setActive(false);
         this.clearCache.setLocalTooltip("settingsui", "onlyinmenu");
      }

      var1.next(5);
      this.generalContentHeight = var1.next();
      this.generalSave = (FormLocalTextButton)this.general.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.general.getHeight() - 40, this.general.getWidth() / 2 - 6));
      this.generalSave.onClicked((var1x) -> {
         this.savePressed();
      });
      this.generalBack = (FormLocalTextButton)this.general.addComponent(new FormLocalTextButton("ui", "backbutton", this.general.getWidth() / 2 + 2, this.general.getHeight() - 40, this.general.getWidth() / 2 - 6));
      this.generalBack.onClicked((var1x) -> {
         this.subMenuBackPressed();
      });
      this.updateGeneralHeight();
   }

   public void updateGeneralHeight() {
      int var1 = Math.max(100, Screen.getHudHeight() - 100);
      int var2 = Math.min(this.generalContentHeight + 40 + this.generalContent.getY(), var1);
      this.general.setHeight(var2);
      this.generalContent.setHeight(this.general.getHeight() - 40 - this.generalContent.getY());
      this.generalContent.setContentBox(new Rectangle(this.general.getWidth(), this.generalContentHeight));
      this.generalSave.setY(this.general.getHeight() - 40);
      this.generalBack.setY(this.general.getHeight() - 40);
   }

   public void makeLanguageCurrent() {
      this.customSave = null;
      this.customLoad = () -> {
         if (this.prevLanguage != null) {
            this.prevLanguage.setCurrent();
            Settings.language = this.prevLanguage.stringID;
         }

      };
      this.makeCurrent(this.language);
      this.languageList.reset();
   }

   private void updateLanguageForm() {
      if (this.language == null) {
         this.language = (Form)this.addComponent(new Form("language", 500, 40));
      } else {
         this.language.clearComponents();
      }

      this.language.addComponent(new FormLocalLabel("settingsui", "language", new FontOptions(20), 0, this.language.getWidth() / 2, 5));
      this.languageList = ((FormLanguageList)this.language.addComponent(new FormLanguageList(0, 35, this.language.getWidth(), 40))).onLanguageSelect((var1) -> {
         var1.language.setCurrent();
         Settings.language = var1.language.stringID;
         this.setSaveActive(true);
      });
      this.languageHelp = (FormLocalTextButton)this.language.addComponent(new FormLocalTextButton("settingsui", "helptranslate", 4, this.language.getHeight() - 60, this.language.getWidth() - 8, FormInputSize.SIZE_20, ButtonColor.BASE));
      this.languageHelp.onClicked((var0) -> {
         GameUtils.openURL("https://steamcommunity.com/app/1169040/discussions/0/4345408777056877000/");
      });
      this.languageSave = (FormLocalTextButton)this.language.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.language.getHeight() - 40, this.language.getWidth() / 2 - 6));
      this.languageSave.onClicked((var1) -> {
         this.savePressed();
      });
      this.languageBack = (FormLocalTextButton)this.language.addComponent(new FormLocalTextButton("ui", "backbutton", this.language.getWidth() / 2 + 2, this.language.getHeight() - 40, this.language.getWidth() / 2 - 6));
      this.languageBack.onClicked((var1) -> {
         this.subMenuBackPressed();
      });
      this.updateLanguageHeight();
   }

   public void updateLanguageHeight() {
      int var1 = Math.max(140, Screen.getHudHeight() - 140);
      int var2 = Math.min(var1, 400);
      int var3 = Math.min(var2 + 60 + 30, var1);
      this.language.setHeight(var3);
      this.languageList.setHeight(this.language.getHeight() - 60 - this.languageList.getY());
      this.languageHelp.setY(this.language.getHeight() - 60);
      this.languageSave.setY(this.language.getHeight() - 40);
      this.languageBack.setY(this.language.getHeight() - 40);
   }

   public void makeControlsCurrent() {
      this.customSave = null;
      this.customLoad = null;
      if (Input.lastInputIsController) {
         ControllerInput.showControllerPanel();
      } else {
         this.makeCurrent(this.controls);
      }

   }

   private void updateControlsForm() {
      if (this.controls == null) {
         this.controls = (Form)this.addComponent(new Form("controls", 400, 40));
      } else {
         this.controls.clearComponents();
      }

      this.controls.addComponent(new FormLocalLabel("settingsui", "controls", new FontOptions(20), 0, this.controls.getWidth() / 2, 5));
      this.controlList = (FormControlList)this.controls.addComponent(new FormControlList(0, 30, this.controls.getWidth(), 40));
      this.controlList.onChanged((var1) -> {
         this.setSaveActive(true);
      });
      this.controlsSave = (FormLocalTextButton)this.controls.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.controls.getHeight() - 40, this.controls.getWidth() / 2 - 6));
      this.controlsSave.onClicked((var1) -> {
         this.savePressed();
      });
      this.controlsBack = (FormLocalTextButton)this.controls.addComponent(new FormLocalTextButton("ui", "backbutton", this.controls.getWidth() / 2 + 2, this.controls.getHeight() - 40, this.controls.getWidth() / 2 - 6));
      this.controlsBack.onClicked((var1) -> {
         this.subMenuBackPressed();
      });
      this.updateControlsHeight();
   }

   public void updateControlsHeight() {
      int var1 = Math.max(120, Screen.getHudHeight() - 120);
      int var2 = Math.min(var1, 400);
      int var3 = Math.min(var2 + 40 + 30, var1);
      this.controls.setHeight(var3);
      this.controlList.setHeight(this.controls.getHeight() - 40 - this.controlList.getY());
      this.controlsSave.setY(this.controls.getHeight() - 40);
      this.controlsBack.setY(this.controls.getHeight() - 40);
   }

   public void makeInterfaceCurrent() {
      this.customSave = null;
      this.customLoad = () -> {
         if (this.reloadedInterface) {
            Screen.reloadInterfaceFromSettings(false);
         }

      };
      this.updateMonitorScroll();
      this.updateDisplayScrollComponent();
      this.makeCurrent(this.interf);
   }

   private void updateInterfaceForm() {
      if (this.interf == null) {
         this.interf = (Form)this.addComponent(new Form("interface", 400, 40));
      } else {
         this.interf.clearComponents();
      }

      this.interf.addComponent(new FormLocalLabel("settingsui", "interface", new FontOptions(20), 0, this.interf.getWidth() / 2, 5));
      this.interfaceContent = (FormContentBox)this.interf.addComponent(new FormContentBox(0, 30, this.interf.getWidth(), this.interf.getHeight() - 40));
      FormFlow var1 = new FormFlow(5);
      int var2 = Math.min(Math.max(this.interfaceContent.getWidth() - 100, 200), this.interfaceContent.getWidth());
      int var3 = this.interfaceContent.getWidth() / 2 - var2 / 2;
      this.interfaceContent.addComponent(new FormLocalLabel("settingsui", "interfacesize", new FontOptions(16), 0, this.interfaceContent.getWidth() / 2, var1.next(20)));
      FormHorizontalScroll.ScrollElement[] var4 = (FormHorizontalScroll.ScrollElement[])IntStream.range(0, GameWindow.interfaceSizes.length).mapToObj((var0) -> {
         return new FormHorizontalScroll.ScrollElement(GameWindow.interfaceSizes[var0], new StaticMessage((int)(GameWindow.interfaceSizes[var0] * 100.0F) + "%"));
      }).toArray((var0) -> {
         return new FormHorizontalScroll.ScrollElement[var0];
      });
      this.interfaceSize = (FormHorizontalScroll)this.interfaceContent.addComponent(new FormHorizontalScroll(this.interfaceContent.getWidth() / 2 - 75, var1.next(30), 150, FormHorizontalScroll.DrawOption.string, 0, var4));
      this.interfaceSize.onChanged((var1x) -> {
         Settings.interfaceSize = (Float)this.interfaceSize.getCurrent().value;
         Screen.updateHudSize();
         this.setSaveActive(true);
      });
      this.dynamicInterfaceSize = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "dynamicinterfacesize", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.dynamicInterfaceSize.onClicked((var1x) -> {
         Settings.dynamicInterfaceSize = ((FormCheckBox)var1x.from).checked;
         Screen.updateHudSize();
         this.setSaveActive(true);
      });
      if (GameInterfaceStyle.styles.size() > 1) {
         this.interfaceContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "interfacestyle", new FontOptions(12), -1, 10, 0, this.interfaceContent.getWidth() - 20), 8));
         this.interfaceStyle = (FormDropdownSelectionButton)this.interfaceContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, var2));
         Iterator var5 = GameInterfaceStyle.styles.iterator();

         while(var5.hasNext()) {
            GameInterfaceStyle var6 = (GameInterfaceStyle)var5.next();
            this.interfaceStyle.options.add(var6, var6.displayName);
         }

         this.interfaceStyle.setSelected(Settings.UI, Settings.UI.displayName);
         this.interfaceStyle.onSelected((var1x) -> {
            Settings.UI = (GameInterfaceStyle)var1x.value;
            Screen.reloadInterfaceFromSettings(true);
            this.setSaveActive(true);
         });
         this.interfaceStyle.controllerUpFocus = this.dynamicInterfaceSize;
         this.dynamicInterfaceSize.controllerDownFocus = this.interfaceStyle;
      }

      this.interfaceSize.controllerDownFocus = this.dynamicInterfaceSize;
      this.pixelFont = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "pixelfont", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.pixelFont.onClicked((var1x) -> {
         Settings.pixelFont = ((FormCheckBox)var1x.from).checked;
         Screen.reloadInterfaceFromSettings(true);
         this.setSaveActive(true);
      });
      if (this.interfaceStyle != null) {
         this.interfaceStyle.controllerDownFocus = this.pixelFont;
         this.pixelFont.controllerUpFocus = this.interfaceStyle;
      }

      this.showItemTooltipBackground = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showitemtooltipsbackground", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showItemTooltipBackground.onClicked((var1x) -> {
         Settings.showItemTooltipBackground = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showItemTooltipBackground.controllerUpFocus = this.pixelFont;
      this.pixelFont.controllerDownFocus = this.showBasicTooltipBackground;
      this.showBasicTooltipBackground = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showbasictooltipsbackground", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showBasicTooltipBackground.onClicked((var1x) -> {
         Settings.showBasicTooltipBackground = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.bigTooltipText = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "bigtooltiptext", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.bigTooltipText.onClicked((var1x) -> {
         Settings.tooltipTextSize = ((FormCheckBox)var1x.from).checked ? 20 : 16;
         this.setSaveActive(true);
      });
      this.showIngredientsAvailable = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showingredientsavailable", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showIngredientsAvailable.onClicked((var1x) -> {
         Settings.showIngredientsAvailable = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showQuestMarkers = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showquest", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showQuestMarkers.onClicked((var1x) -> {
         Settings.showQuestMarkers = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showTeammateMarkers = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showteammates", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showTeammateMarkers.onClicked((var1x) -> {
         Settings.showTeammateMarkers = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showMobHealthBars = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showmobhealthbars", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showMobHealthBars.onClicked((var1x) -> {
         Settings.showMobHealthBars = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showBossHealthBars = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showbosshealthbars", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showBossHealthBars.onClicked((var1x) -> {
         Settings.showBossHealthBars = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showControlTips = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showcontroltips", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showControlTips.onClicked((var1x) -> {
         Settings.showControlTips = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showPickupText = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showpickup", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showPickupText.onClicked((var1x) -> {
         Settings.showPickupText = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showDamageText = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showdamage", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showDamageText.onClicked((var1x) -> {
         Settings.showDamageText = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showDoTText = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showdot", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showDoTText.onClicked((var1x) -> {
         Settings.showDoTText = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showLogicGateTooltips = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showlogicgate", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showLogicGateTooltips.onClicked((var1x) -> {
         Settings.showLogicGateTooltips = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.showDebugInfo = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "showdebug", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.showDebugInfo.onClicked((var1x) -> {
         Settings.showDebugInfo = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.alwaysShowQuickbar = (FormLocalCheckBox)this.interfaceContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "alwaysshowquickbar", 10, 0, this.interfaceContent.getWidth() - 20), 8));
      this.alwaysShowQuickbar.onClicked((var1x) -> {
         Settings.alwaysShowQuickbar = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      FormFlow var8 = var1.split(5);
      FontOptions var9 = new FontOptions(12);
      this.interfaceContent.addComponent(new FormLocalLabel("settingsui", "cursorcolor", new FontOptions(16), -1, 8, var1.next(20)));
      this.cursorRed = (FormLocalSlider)this.interfaceContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("ui", "colorred", 8, 0, Settings.cursorColor.getRed(), 50, 255, this.interfaceContent.getWidth() - 100, var9)));
      this.cursorRed.onChanged((var1x) -> {
         this.changedCursorColor = true;
         Settings.cursorColor = new Color(((FormSlider)var1x.from).getValue(), Settings.cursorColor.getGreen(), Settings.cursorColor.getBlue());
         this.cursorPreview.setColor(Settings.cursorColor);
         this.setSaveActive(true);
      });
      this.cursorGreen = (FormLocalSlider)this.interfaceContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("ui", "colorgreen", 8, 0, Settings.cursorColor.getGreen(), 50, 255, this.interfaceContent.getWidth() - 100, var9)));
      this.cursorGreen.onChanged((var1x) -> {
         this.changedCursorColor = true;
         Settings.cursorColor = new Color(Settings.cursorColor.getRed(), ((FormSlider)var1x.from).getValue(), Settings.cursorColor.getBlue());
         this.cursorPreview.setColor(Settings.cursorColor);
         this.setSaveActive(true);
      });
      this.cursorBlue = (FormLocalSlider)this.interfaceContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("ui", "colorblue", 8, 0, Settings.cursorColor.getBlue(), 50, 255, this.interfaceContent.getWidth() - 100, var9)));
      this.cursorBlue.onChanged((var1x) -> {
         this.changedCursorColor = true;
         Settings.cursorColor = new Color(Settings.cursorColor.getRed(), Settings.cursorColor.getGreen(), ((FormSlider)var1x.from).getValue());
         this.cursorPreview.setColor(Settings.cursorColor);
         this.setSaveActive(true);
      });
      var1.next(5);
      int var7 = var1.next(20);
      ((FormLocalTextButton)this.interfaceContent.addComponent(new FormLocalTextButton("ui", "selectcolor", 8, var7 - 2, this.interfaceContent.getWidth() - 165, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var1x) -> {
         final Color var2 = new Color(Settings.cursorColor.getRGB());
         ((FormButton)var1x.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(var1x.from, var2) {
            public void onApplied(Color var1) {
               if (var1 != null) {
                  var1 = new Color(GameMath.limit(var1.getRed(), 50, 255), GameMath.limit(var1.getGreen(), 50, 255), GameMath.limit(var1.getBlue(), 50, 255));
                  SettingsForm.this.changedCursorColor = true;
                  Settings.cursorColor = var1;
                  SettingsForm.this.cursorRed.setValue(Settings.cursorColor.getRed());
                  SettingsForm.this.cursorGreen.setValue(Settings.cursorColor.getGreen());
                  SettingsForm.this.cursorBlue.setValue(Settings.cursorColor.getBlue());
                  SettingsForm.this.cursorPreview.setColor(Settings.cursorColor);
                  SettingsForm.this.setSaveActive(true);
               } else {
                  Settings.cursorColor = var2;
                  SettingsForm.this.cursorPreview.setColor(Settings.cursorColor);
               }

            }

            public void onSelected(Color var1) {
               var1 = new Color(GameMath.limit(var1.getRed(), 50, 255), GameMath.limit(var1.getGreen(), 50, 255), GameMath.limit(var1.getBlue(), 50, 255));
               Settings.cursorColor = var1;
               SettingsForm.this.cursorPreview.setColor(Settings.cursorColor);
            }
         });
      });
      this.cursorSize = (FormHorizontalIntScroll)this.interfaceContent.addComponent(new FormHorizontalIntScroll(this.interfaceContent.getWidth() - 150, var7, 140, FormHorizontalScroll.DrawOption.string, new LocalMessage("settingsui", "cursorsize"), Settings.cursorSize, -Screen.cursorSizeOffset, Screen.cursorSizes.length - Screen.cursorSizeOffset - 1));
      this.cursorSize.onChanged((var1x) -> {
         this.changedCursorSize = true;
         Settings.cursorSize = (Integer)((FormHorizontalScroll)var1x.from).getCurrent().value;
         this.cursorPreview.setSize(Settings.cursorSize);
         this.setSaveActive(true);
      });
      var8.next(50);
      this.cursorPreview = (FormCursorPreview)this.interfaceContent.addComponent(new FormCursorPreview(this.interfaceContent.getWidth() - 65, var8.next(40), Settings.cursorColor, Settings.cursorSize));
      var1.next(5);
      this.interfaceContentHeight = var1.next();
      this.interfaceSave = (FormLocalTextButton)this.interf.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.interf.getHeight() - 40, this.interf.getWidth() / 2 - 6));
      this.interfaceSave.onClicked((var1x) -> {
         this.savePressed();
      });
      this.interfaceBack = (FormLocalTextButton)this.interf.addComponent(new FormLocalTextButton("ui", "backbutton", this.interf.getWidth() / 2 + 2, this.interf.getHeight() - 40, this.interf.getWidth() / 2 - 6));
      this.interfaceBack.onClicked((var1x) -> {
         this.subMenuBackPressed();
      });
      this.updateInterfaceHeight();
   }

   public void updateInterfaceHeight() {
      int var1 = Math.max(100, Screen.getHudHeight() - 100);
      int var2 = Math.min(this.interfaceContentHeight + 40 + this.interfaceContent.getY(), var1);
      this.interf.setHeight(var2);
      this.interfaceContent.setHeight(this.interf.getHeight() - 40 - this.interfaceContent.getY());
      this.interfaceContent.setContentBox(new Rectangle(this.interf.getWidth(), this.interfaceContentHeight));
      this.interfaceSave.setY(this.interf.getHeight() - 40);
      this.interfaceBack.setY(this.interf.getHeight() - 40);
   }

   public void makeGraphicsCurrent() {
      this.customSave = null;
      this.customLoad = null;
      this.updateMonitorScroll();
      this.updateDisplayScrollComponent();
      this.makeCurrent(this.graphics);
   }

   private void updateGraphicsForm() {
      if (this.graphics == null) {
         this.graphics = (Form)this.addComponent(new Form("graphics", 400, 40));
      } else {
         this.graphics.clearComponents();
         this.displayScroll = null;
      }

      this.graphics.addComponent(new FormLocalLabel("settingsui", "graphics", new FontOptions(20), 0, this.graphics.getWidth() / 2, 5));
      this.graphicsContent = (FormContentBox)this.graphics.addComponent(new FormContentBox(0, 30, this.graphics.getWidth(), this.graphics.getHeight() - 40));
      FormFlow var1 = new FormFlow(5);
      this.graphicsContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "displaymode", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
      int var2 = Math.min(Math.max(this.graphicsContent.getWidth() - 100, 200), this.graphicsContent.getWidth());
      int var3 = this.graphicsContent.getWidth() / 2 - var2 / 2;
      this.displayMode = (FormDropdownSelectionButton)this.graphicsContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, var2));
      this.displayMode.options.add(DisplayMode.Windowed, DisplayMode.Windowed.displayName);
      this.displayMode.options.add(DisplayMode.Borderless, DisplayMode.Borderless.displayName);
      this.displayMode.options.add(DisplayMode.Fullscreen, DisplayMode.Fullscreen.displayName);
      this.displayMode.setSelected(Settings.displayMode, Settings.displayMode.displayName);
      this.displayMode.onSelected((var1x) -> {
         this.displayScroll.setActive(((DisplayMode)var1x.value).canSelectSize);
         this.displayChanged = true;
         this.setSaveActive(true);
      });
      if (Platform.get() == Platform.MACOSX) {
         this.displayMode.setActive(false);
      }

      this.graphicsContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "monitor", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.monitorScroll = (FormHorizontalIntScroll)this.graphicsContent.addComponent(new FormHorizontalIntScroll(var3, var1.next(20), var2, FormHorizontalScroll.DrawOption.value, "", Settings.monitor, 0, 0));
      this.monitorScroll.onChanged((var1x) -> {
         this.displayChanged = true;
         this.updateDisplayScrollComponent();
         this.setSaveActive(true);
      });
      this.updateMonitorScroll();
      this.graphicsContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "displayres", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.displayScrollY = var1.next(24);
      this.updateDisplayScrollComponent();
      this.vSyncEnabled = (FormLocalCheckBox)this.graphicsContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "vsync", 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.vSyncEnabled.onClicked((var1x) -> {
         Settings.vSyncEnabled = ((FormCheckBox)var1x.from).checked;
         Screen.setVSync(Settings.vSyncEnabled);
         this.setSaveActive(true);
      });
      this.displayScroll.controllerDownFocus = this.vSyncEnabled;
      this.vSyncEnabled.controllerUpFocus = this.displayScroll;
      this.graphicsContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "maxfps", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.maxFPS = (FormDropdownSelectionButton)this.graphicsContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, var2));
      this.maxFPS.options.add(30, new StaticMessage("30"));
      this.maxFPS.options.add(60, new StaticMessage("60"));
      this.maxFPS.options.add(120, new StaticMessage("120"));
      this.maxFPS.options.add(144, new StaticMessage("144"));
      this.maxFPS.options.add(0, new LocalMessage("settingsui", "fpsnocap"));
      this.maxFPS.setSelected(Settings.maxFPS, (GameMessage)(Settings.maxFPS == 0 ? new LocalMessage("settingsui", "fpsnocap") : new StaticMessage(String.valueOf(Settings.maxFPS))));
      this.maxFPS.onSelected((var1x) -> {
         Settings.maxFPS = (Integer)var1x.value;
         this.setSaveActive(true);
      });
      this.maxFPS.controllerUpFocus = this.vSyncEnabled;
      this.vSyncEnabled.controllerDownFocus = this.maxFPS;
      this.reduceUIFramerate = (FormLocalCheckBox)this.graphicsContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "reduceuiframerate", 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.reduceUIFramerate.onClicked((var1x) -> {
         Settings.reduceUIFramerate = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.maxFPS.controllerDownFocus = this.reduceUIFramerate;
      this.reduceUIFramerate.controllerUpFocus = this.maxFPS;
      this.graphicsContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "colormode", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.sceneColors = (FormDropdownSelectionButton)this.graphicsContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, var2));
      SceneColorSetting[] var4 = SceneColorSetting.values();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         SceneColorSetting var7 = var4[var6];
         this.sceneColors.options.add(var7, var7.displayName);
      }

      this.sceneColors.setSelected(Settings.sceneColors, Settings.sceneColors.displayName);
      this.sceneColors.onSelected((var1x) -> {
         Settings.sceneColors = (SceneColorSetting)var1x.value;
         this.setSaveActive(true);
      });
      this.reduceUIFramerate.controllerDownFocus = this.sceneColors;
      this.sceneColors.controllerUpFocus = this.reduceUIFramerate;
      this.brightness = (FormLocalSlider)this.graphicsContent.addComponent((<undefinedtype>)var1.nextY(new FormLocalSlider("settingsui", "brightness", 10, 0, 100, 50, 200, this.graphicsContent.getWidth() - 20) {
         public String getValueText() {
            return this.getValue() + "%";
         }
      }, 8));
      this.brightness.onChanged((var1x) -> {
         Settings.brightness = (float)((FormSlider)var1x.from).getValue() / 100.0F;
         if (this.client != null) {
            Level var2 = this.client.getLevel();
            if (var2 != null) {
               var2.lightManager.updateAmbientLight();
            }
         }

         this.setSaveActive(true);
      });
      this.sceneColors.controllerDownFocus = this.smoothLighting;
      this.brightness.controllerUpFocus = this.sceneColors;
      this.smoothLighting = (FormLocalCheckBox)this.graphicsContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "smoothlight", 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.smoothLighting.onClicked((var1x) -> {
         Settings.smoothLighting = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.brightness.controllerDownFocus = this.smoothLighting;
      this.smoothLighting.controllerUpFocus = this.brightness;
      this.graphicsContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "lightmode", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.lights = (FormDropdownSelectionButton)this.graphicsContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, var2));
      Settings.LightSetting[] var8 = Settings.LightSetting.values();
      var5 = var8.length;

      for(var6 = 0; var6 < var5; ++var6) {
         Settings.LightSetting var9 = var8[var6];
         this.lights.options.add(var9, var9.displayName);
      }

      this.lights.setSelected(Settings.lights, Settings.lights.displayName);
      this.lights.onSelected((var1x) -> {
         Settings.lights = (Settings.LightSetting)var1x.value;
         if (this.client != null) {
            Level var2 = this.client.getLevel();
            if (var2 != null) {
               var2.lightManager.ensureSetting(Settings.lights);
               var2.lightManager.updateAmbientLight();
            }
         }

         this.setSaveActive(true);
      });
      this.smoothLighting.controllerDownFocus = this.lights;
      this.lights.controllerUpFocus = this.smoothLighting;
      this.graphicsContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "particles", new FontOptions(12), -1, 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.particles = (FormDropdownSelectionButton)this.graphicsContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, var2));
      this.particles.options.add(Settings.ParticleSetting.Minimal, Settings.ParticleSetting.Minimal.displayName);
      this.particles.options.add(Settings.ParticleSetting.Decreased, Settings.ParticleSetting.Decreased.displayName);
      this.particles.options.add(Settings.ParticleSetting.Maximum, Settings.ParticleSetting.Maximum.displayName);
      this.particles.setSelected(Settings.particles, Settings.particles.displayName);
      this.particles.onSelected((var1x) -> {
         Settings.particles = (Settings.ParticleSetting)var1x.value;
         this.setSaveActive(true);
      });
      this.wavyGrass = (FormLocalCheckBox)this.graphicsContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "wavygrass", 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.wavyGrass.onClicked((var1x) -> {
         Settings.wavyGrass = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.particles.controllerDownFocus = this.wavyGrass;
      this.wavyGrass.controllerUpFocus = this.particles;
      this.denseGrass = (FormLocalCheckBox)this.graphicsContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "densegrass", 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.denseGrass.onClicked((var1x) -> {
         Settings.denseGrass = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.cameraShake = (FormLocalCheckBox)this.graphicsContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "camerashake", 10, 0, this.graphicsContent.getWidth() - 20), 8));
      this.cameraShake.onClicked((var1x) -> {
         Settings.cameraShake = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      var1.next(5);
      this.graphicsContentHeight = var1.next();
      this.graphicsSave = (FormLocalTextButton)this.graphics.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.graphics.getHeight() - 40, this.graphics.getWidth() / 2 - 6));
      this.graphicsSave.onClicked((var1x) -> {
         this.savePressed();
      });
      this.graphicsBack = (FormLocalTextButton)this.graphics.addComponent(new FormLocalTextButton("ui", "backbutton", this.graphics.getWidth() / 2 + 2, this.graphics.getHeight() - 40, this.graphics.getWidth() / 2 - 6));
      this.graphicsBack.onClicked((var1x) -> {
         this.subMenuBackPressed();
      });
      this.updateGraphicsHeight();
   }

   public void updateGraphicsHeight() {
      int var1 = Math.max(100, Screen.getHudHeight() - 100);
      int var2 = Math.min(this.graphicsContentHeight + 40 + this.graphicsContent.getY(), var1);
      this.graphics.setHeight(var2);
      this.graphicsContent.setHeight(this.graphics.getHeight() - 40 - this.graphicsContent.getY());
      this.graphicsContent.setContentBox(new Rectangle(this.graphics.getWidth(), this.graphicsContentHeight));
      this.graphicsSave.setY(this.graphics.getHeight() - 40);
      this.graphicsBack.setY(this.graphics.getHeight() - 40);
   }

   public void makeSoundCurrent() {
      this.customSave = null;
      this.customLoad = null;
      this.updateOutputDevices();
      this.makeCurrent(this.sound);
   }

   private void updateSoundForm() {
      if (this.sound == null) {
         this.sound = (Form)this.addComponent(new Form("sound", 400, 40));
      } else {
         this.sound.clearComponents();
      }

      this.sound.addComponent(new FormLocalLabel("settingsui", "sound", new FontOptions(20), 0, this.sound.getWidth() / 2, 5));
      this.soundContent = (FormContentBox)this.sound.addComponent(new FormContentBox(0, 30, this.sound.getWidth(), this.sound.getHeight() - 40));
      FormFlow var1 = new FormFlow(5);
      this.masterVolume = (FormLocalSlider)this.soundContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("settingsui", "mastervolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
      this.masterVolume.onChanged((var1x) -> {
         Settings.masterVolume = ((FormSlider)var1x.from).getPercentage();
         Screen.updateVolume();
         this.setSaveActive(true);
      });
      this.effectsVolume = (FormLocalSlider)this.soundContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("settingsui", "effectsvolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
      this.effectsVolume.onChanged((var1x) -> {
         Settings.effectsVolume = ((FormSlider)var1x.from).getPercentage();
         Screen.updateVolume();
         this.setSaveActive(true);
      });
      this.weatherVolume = (FormLocalSlider)this.soundContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("settingsui", "weathervolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
      this.weatherVolume.onChanged((var1x) -> {
         Settings.weatherVolume = ((FormSlider)var1x.from).getPercentage();
         Screen.updateVolume();
         this.setSaveActive(true);
      });
      this.UIVolume = (FormLocalSlider)this.soundContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("settingsui", "uivolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
      this.UIVolume.onChanged((var1x) -> {
         Settings.UIVolume = ((FormSlider)var1x.from).getPercentage();
         Screen.updateVolume();
         this.setSaveActive(true);
      });
      this.musicVolume = (FormLocalSlider)this.soundContent.addComponent((FormLocalSlider)var1.nextY(new FormLocalSlider("settingsui", "musicvolume", 10, 0, 50, 0, 100, this.soundContent.getWidth() - 20), 15));
      this.musicVolume.onChanged((var1x) -> {
         Settings.musicVolume = ((FormSlider)var1x.from).getPercentage();
         Screen.updateVolume();
         this.setSaveActive(true);
      });
      this.muteOnFocusLoss = (FormLocalCheckBox)this.soundContent.addComponent((FormLocalCheckBox)var1.nextY(new FormLocalCheckBox("settingsui", "mutefocus", 10, 0, this.soundContent.getWidth() - 20), 8));
      this.muteOnFocusLoss.onClicked((var1x) -> {
         Settings.muteOnFocusLoss = ((FormCheckBox)var1x.from).checked;
         this.setSaveActive(true);
      });
      this.musicVolume.controllerDownFocus = this.muteOnFocusLoss;
      this.muteOnFocusLoss.controllerUpFocus = this.musicVolume;
      this.soundContent.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("settingsui", "outputdevice", new FontOptions(12), -1, 10, 0, this.soundContent.getWidth() - 20), 8));
      int var2 = Math.min(Math.max(this.soundContent.getWidth() - 100, 300), this.soundContent.getWidth());
      int var3 = this.soundContent.getWidth() / 2 - var2 / 2;
      this.outputDevice = (FormDropdownSelectionButton)this.soundContent.addComponent(new FormDropdownSelectionButton(var3, var1.next(24), FormInputSize.SIZE_20, ButtonColor.BASE, var2));
      this.updateOutputDevices();
      this.outputDevice.onSelected((var1x) -> {
         if (!Objects.equals(var1x.value, Settings.outputDevice)) {
            this.setSaveActive(true);
         }

      });
      this.outputDevice.controllerUpFocus = this.muteOnFocusLoss;
      this.muteOnFocusLoss.controllerDownFocus = this.outputDevice;
      var1.next(5);
      this.soundContentHeight = var1.next();
      this.soundSave = (FormLocalTextButton)this.sound.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.sound.getHeight() - 40, this.sound.getWidth() / 2 - 6));
      this.soundSave.onClicked((var1x) -> {
         this.savePressed();
      });
      this.soundBack = (FormLocalTextButton)this.sound.addComponent(new FormLocalTextButton("ui", "backbutton", this.sound.getWidth() / 2 + 2, this.sound.getHeight() - 40, this.sound.getWidth() / 2 - 6));
      this.soundBack.onClicked((var1x) -> {
         this.subMenuBackPressed();
      });
      this.updateSoundHeight();
   }

   public void updateSoundHeight() {
      int var1 = Math.max(100, Screen.getHudHeight() - 100);
      int var2 = Math.min(this.soundContentHeight + 40 + this.soundContent.getY(), var1);
      this.sound.setHeight(var2);
      this.soundContent.setHeight(this.sound.getHeight() - 40 - this.soundContent.getY());
      this.soundContent.setContentBox(new Rectangle(this.sound.getWidth(), this.soundContentHeight));
      this.soundSave.setY(this.sound.getHeight() - 40);
      this.soundBack.setY(this.sound.getHeight() - 40);
   }

   private void savePressed() {
      if (this.customSave != null) {
         this.customSave.run();
      }

      if (this.displayChanged) {
         Dimension var1 = new Dimension(Screen.getWindowWidth(), Screen.getWindowHeight());
         DisplayMode var2 = Settings.displayMode;
         int var3 = Settings.monitor;
         Dimension var4 = new Dimension(var1);
         Dimension var5 = (Dimension)this.displayScroll.getSelected();
         if (var5 != null) {
            var4 = var5;
         }

         DisplayMode var7 = (DisplayMode)this.displayMode.getSelected();
         int var8 = (Integer)this.monitorScroll.getValue();
         if (var7 != var2 || !var4.equals(var1) || var8 != var3) {
            Settings.displaySize = var4;
            Settings.displayMode = var7;
            Settings.monitor = var8;
            Screen.updateDisplayMode();
            Settings.displaySize = var1;
            Settings.displayMode = var2;
            Settings.monitor = var3;
            this.startTimerConfirm(15, () -> {
               Settings.displaySize = var4;
               Settings.displayMode = var7;
               Settings.monitor = var8;
               Settings.saveClientSettings();
               this.makeCurrent(this.graphics);
            }, () -> {
               Screen.updateDisplayMode();
               this.makeCurrent(this.graphics);
               this.displayChanged = true;
               this.setSaveActive(true);
            });
         }

         this.displayChanged = false;
      }

      this.prevLanguage = Localization.getCurrentLang();
      String var9 = (String)this.outputDevice.getSelected();
      if (!Objects.equals(var9, Settings.outputDevice)) {
         Settings.outputDevice = var9;
         Screen.updateAudioDevice();
      }

      Settings.saveClientSettings();
      if (this.changedCursorColor) {
         Screen.setCursorColor(Settings.cursorColor);
         this.changedCursorColor = false;
      }

      if (this.changedCursorSize) {
         Screen.setCursorSize(Settings.cursorSize);
         this.changedCursorSize = false;
      }

      this.setSaveActive(false);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (var1.state && var1.getID() == 256) {
         this.submitEscapeEvent(var1);
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      super.handleControllerEvent(var1, var2, var3);
      if (var1.buttonState && (var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU)) {
         this.submitEscapeEvent(InputEvent.ControllerButtonEvent(var1, var2));
      }

   }

   public void submitEscapeEvent(InputEvent var1) {
      if (!var1.isUsed() && !this.isCurrent(this.mainMenu)) {
         if (this.isCurrent(this.difficultyForm)) {
            this.difficultyLabel.setLocalization(this.difficultyForm.selectedDifficulty.displayName);
            this.makeWorldCurrent(false);
            if (this.client.worldSettings.difficulty != this.difficultyForm.selectedDifficulty) {
               this.setSaveActive(true);
            }
         } else if (this.isCurrent(this.confirmation)) {
            this.confirmation.submitBackEvent();
         } else {
            this.subMenuBackPressed();
         }

         var1.use();
      }

   }

   public boolean shouldDraw() {
      return super.shouldDraw() && !this.isHidden();
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean var1) {
      boolean var2 = this.isHidden();
      this.hidden = var1;
      if (!var1 && var2) {
         this.load();
         this.makeCurrent(this.mainMenu);
         this.updateWorldButtonActive();
         if (this.client != null && !this.client.isSingleplayer()) {
            this.pauseOnFocusLoss.setActive(false);
            this.pauseOnFocusLoss.checked = false;
         }
      }

   }

   public void subMenuBackPressed() {
      if (this.saveActive) {
         this.confirmation.setupConfirmation((GameMessage)(new LocalMessage("settingsui", "saveconfirm")), new LocalMessage("ui", "savebutton"), new LocalMessage("ui", "dontsavebutton"), () -> {
            this.savePressed();
            this.makeCurrent(this.mainMenu);
         }, () -> {
            Settings.loadClientSettings();
            this.load();
            this.makeCurrent(this.mainMenu);
         });
         this.makeCurrent(this.confirmation);
      } else {
         Settings.loadClientSettings();
         this.load();
         this.makeCurrent(this.mainMenu);
      }

   }

   public void load() {
      if (this.customLoad != null) {
         this.customLoad.run();
      }

      this.setSaveActive(false);
      this.updateComponents();
   }

   public void updateComponents() {
      if (this.client != null) {
         this.difficultyForm.selectedDifficulty = this.client.worldSettings.difficulty;
         this.difficultyLabel.setLocalization(this.client.worldSettings.difficulty.displayName);
         this.difficultyForm.updateDifficultyContent();
         this.deathPenalty.setSelected(this.client.worldSettings.deathPenalty, this.client.worldSettings.deathPenalty.displayName);
         this.raidFrequency.setSelected(this.client.worldSettings.raidFrequency, this.client.worldSettings.raidFrequency.displayName);
         this.survivalMode.checked = this.client.worldSettings.survivalMode;
         this.playerHunger.checked = this.client.worldSettings.playerHunger;
         this.playerHunger.setActive(!this.survivalMode.checked);
         this.playerHunger.checked = this.survivalMode.checked || this.client.worldSettings.playerHunger;
         this.allowOutsideCharacters.checked = this.client.worldSettings.allowOutsideCharacters;
         this.forcedPvP.checked = this.client.worldSettings.forcedPvP;
      }

      this.sceneSize.setValue((int)(Settings.sceneSize * 100.0F));
      this.dynamicSceneSize.checked = Settings.dynamicSceneSize;
      this.limitCameraToLevelBounds.checked = Settings.limitCameraToLevelBounds;
      this.pauseOnFocusLoss.checked = Settings.pauseOnFocusLoss;
      this.savePerformanceOnFocusLoss.checked = Settings.savePerformanceOnFocusLoss;
      this.alwaysSkipTutorial.checked = Settings.alwaysSkipTutorial;
      this.showSettlerHeadArmor.checked = Settings.showSettlerHeadArmor;
      this.prevLanguage = Localization.getCurrentLang();
      this.controlList.reset();
      this.interfaceSize.setElement(new FormHorizontalScroll.ScrollElement(Settings.interfaceSize, new StaticMessage((int)(Settings.interfaceSize * 100.0F) + "%")));
      this.dynamicInterfaceSize.checked = Settings.dynamicInterfaceSize;
      if (this.interfaceStyle != null) {
         this.interfaceStyle.setSelected(Settings.UI, Settings.UI.displayName);
      }

      this.pixelFont.checked = Settings.pixelFont;
      this.showDebugInfo.checked = Settings.showDebugInfo;
      this.showIngredientsAvailable.checked = Settings.showIngredientsAvailable;
      this.showQuestMarkers.checked = Settings.showQuestMarkers;
      this.showTeammateMarkers.checked = Settings.showTeammateMarkers;
      this.showPickupText.checked = Settings.showPickupText;
      this.showDamageText.checked = Settings.showDamageText;
      this.showDoTText.checked = Settings.showDoTText;
      this.showMobHealthBars.checked = Settings.showMobHealthBars;
      this.showBossHealthBars.checked = Settings.showBossHealthBars;
      this.showControlTips.checked = Settings.showControlTips;
      this.showItemTooltipBackground.checked = Settings.showItemTooltipBackground;
      this.showBasicTooltipBackground.checked = Settings.showBasicTooltipBackground;
      this.bigTooltipText.checked = Settings.tooltipTextSize >= 20;
      this.showLogicGateTooltips.checked = Settings.showLogicGateTooltips;
      this.alwaysShowQuickbar.checked = Settings.alwaysShowQuickbar;
      this.changedCursorColor = false;
      this.cursorRed.setValue(Settings.cursorColor.getRed());
      this.cursorGreen.setValue(Settings.cursorColor.getGreen());
      this.cursorBlue.setValue(Settings.cursorColor.getBlue());
      this.cursorSize.setValue(Settings.cursorSize);
      this.cursorPreview.setColor(Settings.cursorColor);
      this.cursorPreview.setSize(Settings.cursorSize);
      this.smoothLighting.checked = Settings.smoothLighting;
      this.wavyGrass.checked = Settings.wavyGrass;
      this.denseGrass.checked = Settings.denseGrass;
      this.cameraShake.checked = Settings.cameraShake;
      this.vSyncEnabled.checked = Settings.vSyncEnabled;
      this.maxFPS.setSelected(Settings.maxFPS, (GameMessage)(Settings.maxFPS == 0 ? new LocalMessage("settingsui", "fpsnocap") : new StaticMessage(String.valueOf(Settings.maxFPS))));
      this.reduceUIFramerate.checked = Settings.reduceUIFramerate;

      try {
         this.monitorScroll.setValue(Settings.monitor);
      } catch (NoSuchElementException var2) {
         this.monitorScroll.setElement(new FormHorizontalScroll.ScrollElement(Settings.monitor, ""));
      }

      this.displayMode.setSelected(Settings.displayMode, Settings.displayMode.displayName);
      this.sceneColors.setSelected(Settings.sceneColors, Settings.sceneColors.displayName);
      this.brightness.setValue((int)(Settings.brightness * 100.0F));
      this.lights.setSelected(Settings.lights, Settings.lights.displayName);
      this.particles.setSelected(Settings.particles, Settings.particles.displayName);
      this.updateOutputDevices();
      this.masterVolume.setPercentage(Settings.masterVolume);
      this.effectsVolume.setPercentage(Settings.effectsVolume);
      this.weatherVolume.setPercentage(Settings.weatherVolume);
      this.UIVolume.setPercentage(Settings.UIVolume);
      this.musicVolume.setPercentage(Settings.musicVolume);
      this.muteOnFocusLoss.checked = Settings.muteOnFocusLoss;
   }

   private void updateMonitorScroll() {
      this.monitorScroll.set((String)"", (Integer)this.monitorScroll.getValue(), 0, WindowUtils.getMonitors().length - 1);
   }

   private void updateDisplayScrollComponent() {
      if (this.displayScroll == null) {
         int var1 = Math.min(Math.max(this.graphicsContent.getWidth() - 100, 200), this.graphicsContent.getWidth());
         int var2 = this.graphicsContent.getWidth() / 2 - var1 / 2;
         this.displayScroll = (FormDropdownSelectionButton)this.graphicsContent.addComponent(new FormDropdownSelectionButton(var2, this.displayScrollY, FormInputSize.SIZE_20, ButtonColor.BASE, var1));
         this.displayScroll.onSelected((var1x) -> {
            this.displayScroll.setActive(((DisplayMode)this.displayMode.getSelected()).canSelectSize);
            this.displayChanged = true;
            this.setSaveActive(true);
         });
      }

      long var9 = WindowUtils.getMonitor((Integer)this.monitorScroll.getValue());

      Dimension[] var3;
      try {
         var3 = WindowUtils.getVideoModes(var9);
      } catch (Exception var8) {
         var3 = new Dimension[]{new Dimension(1280, 720), new Dimension(1920, 1080)};
      }

      this.displayScroll.options.clear();
      Dimension[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Dimension var7 = var4[var6];
         this.displayScroll.options.add(new FormDropdownSelectionButton.Option(var7, new StaticMessage(var7.width + "x" + var7.height)));
      }

      this.displayScroll.setActive(((DisplayMode)this.displayMode.getSelected()).canSelectSize);
      this.displayMode.setSelected(Settings.displayMode, Settings.displayMode.displayName);
      this.updateDisplayScroll();
   }

   private void updateDisplayScroll() {
      this.displayScroll.setSelected(new Dimension(Screen.getWindowWidth(), Screen.getWindowHeight()), new StaticMessage(Screen.getWindowWidth() + "x" + Screen.getWindowHeight()));
   }

   private String getOutputDeviceDisplayName(String var1) {
      String var2 = var1;
      if (var1.startsWith("OpenAL Soft on ")) {
         var2 = var1.substring("OpenAL Soft on ".length());
      }

      return var2;
   }

   private void updateOutputDevices() {
      this.outputDeviceNames = ALUtil.getStringList(0L, 4115);
      this.outputDevice.options.clear();
      if (this.outputDeviceNames != null && !this.outputDeviceNames.isEmpty()) {
         this.outputDevice.setActive(true);
         this.outputDevice.options.add((Object)null, new LocalMessage("settingsui", "outputdevicedefault"));
         Iterator var1 = this.outputDeviceNames.iterator();

         while(var1.hasNext()) {
            String var2 = (String)var1.next();
            this.outputDevice.options.add(var2, new StaticMessage(this.getOutputDeviceDisplayName(var2)));
         }

         if (Settings.outputDevice == null) {
            this.outputDevice.setSelected((Object)null, new LocalMessage("settingsui", "outputdevicedefault"));
         } else {
            this.outputDevice.setSelected(Settings.outputDevice, new StaticMessage(this.getOutputDeviceDisplayName(Settings.outputDevice)));
         }
      } else {
         this.outputDevice.setSelected((Object)null, new LocalMessage("settingsui", "outputdevicenone"));
         this.outputDevice.setActive(false);
      }

   }

   public void updateWorldButtonActive() {
      if (this.client == null) {
         this.mainMenuWorld.setActive(false);
         this.mainMenuWorld.setLocalTooltip((GameMessage)null);
      } else if (this.client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
         this.mainMenuWorld.setActive(false);
         this.mainMenuWorld.setLocalTooltip("settingsui", "nopermission");
      } else {
         this.mainMenuWorld.setActive(true);
         this.mainMenuWorld.setLocalTooltip((GameMessage)null);
      }

   }

   public void setSaveActive(boolean var1) {
      this.saveActive = var1;
      this.worldSave.setActive(var1);
      this.generalSave.setActive(var1);
      this.languageSave.setActive(var1);
      this.controlsSave.setActive(var1);
      this.interfaceSave.setActive(var1);
      this.graphicsSave.setActive(var1);
      this.soundSave.setActive(var1);
   }

   public void startTimerConfirm(int var1, Runnable var2, Runnable var3) {
      this.timerConfirmEnd = System.currentTimeMillis() + (long)(var1 * 1000);
      this.confirmation.setupConfirmation((var2x) -> {
         FormLocalLabel var3x = (FormLocalLabel)var2x.addComponent(new FormLocalLabel("settingsui", "confirm", new FontOptions(20), 0, this.confirmation.getWidth() / 2, 10, this.confirmation.getWidth() - 20));
         FormLabel var4 = (FormLabel)var2x.addComponent(new FormLabel("", new FontOptions(20), 0, this.confirmation.getWidth() / 2, 10 + var3x.getHeight() + 5));
         this.checkConfirmTimerComplete = () -> {
            long var3x = this.timerConfirmEnd - System.currentTimeMillis();
            if (var3x < 0L) {
               this.timerConfirmEnd = 0L;
               var3.run();
            } else {
               var4.setText((int)Math.ceil((double)var3x / 1000.0) + "");
            }

         };
         this.checkConfirmTimerComplete.run();
      }, () -> {
         this.timerConfirmEnd = 0L;
         var2.run();
      }, () -> {
         this.timerConfirmEnd = 0L;
         var3.run();
      });
      this.makeCurrent(this.confirmation);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.timerConfirmEnd != 0L && this.isCurrent(this.confirmation)) {
         this.checkConfirmTimerComplete.run();
      }

      super.draw(var1, var2, var3);
   }

   public void backPressed() {
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.mainMenu.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.updateWorldHeight();
      this.world.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.difficultyForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.updateGeneralHeight();
      this.general.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.updateLanguageHeight();
      this.language.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.updateControlsHeight();
      this.controls.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.updateInterfaceHeight();
      this.interf.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.updateGraphicsHeight();
      this.graphics.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.updateSoundHeight();
      this.sound.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.confirmation.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }
}
