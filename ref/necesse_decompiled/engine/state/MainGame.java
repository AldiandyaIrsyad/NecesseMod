package necesse.engine.state;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import necesse.engine.AbstractMusicList;
import necesse.engine.EventStatusBarData;
import necesse.engine.GameCrashLog;
import necesse.engine.GameWindow;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketOpenPartyConfig;
import necesse.engine.network.packet.PacketRequestTravel;
import necesse.engine.network.packet.PacketSettlementOpen;
import necesse.engine.network.packet.PacketSpawnPlayer;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.steam.SteamData;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.camera.MainGameCamera;
import necesse.gfx.camera.MainGameFollowCamera;
import necesse.gfx.camera.MainGameMousePanningCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.ProgressBarDrawOptions;
import necesse.gfx.drawOptions.StringDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.container.PartyConfigContainer;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class MainGame extends State {
   public static DebugForm debugForm;
   private HudDrawElement hudDraw;
   private MainGameCamera defaultCamera;
   private MainGameCamera camera;
   private Client client;
   private boolean showMap;
   private boolean showScoreboard;
   public MainGameFormManager formManager;
   private long nextStatsStoreTime = System.currentTimeMillis() + 120000L;
   private boolean saveZoom;

   public MainGame(Client var1) {
      this.client = var1;
      this.init();
   }

   public void init() {
      this.defaultCamera = new MainGameFollowCamera();
      this.resetCamera();
      ControllerInput.MENU_SET_LAYER.setActive(false);
      FormManager.cleanUpLastControllerFocuses();
      HUD.reset();
      this.setupFormManager();
      this.setRunning(true);
      this.setShowMap(false);
      this.setShowScoreboard(false);
      Settings.hideUI = false;
      this.client.tutorial.updateObjective(this);
      this.setupHudDraw();
      this.client.network.sendPacket(new PacketSpawnPlayer(this.client));
      this.client.hasSentSpawnPacket = true;
      this.isInitialized = true;
   }

   public void frameTick(TickManager var1) {
      if (this.client == null) {
         throw new IllegalStateException("Client is not initiated");
      } else if (GlobalData.getCurrentState() == this) {
         PlayerMob var2 = this.client.getPlayer();
         if (this.nextStatsStoreTime <= System.currentTimeMillis()) {
            SteamData.storeStatsAndAchievements();
            this.nextStatsStoreTime = System.currentTimeMillis() + 120000L;
         }

         AtomicReference var3 = new AtomicReference();
         Performance.record(var1, "controls", (Runnable)(() -> {
            if (Control.SCREENSHOT.isPressed()) {
               Screen.takeScreenshot(this.client.chat);
            }

            InputEvent var4;
            float var5;
            if (Control.ZOOM_IN.isMouseWheel()) {
               if (!this.formManager.isMouseOver()) {
                  var4 = Control.ZOOM_IN.getEvent();
                  var5 = var4 != null && !var4.isUsed() ? (float)var4.getMouseWheelY() : 0.0F;
                  if (var5 != 0.0F) {
                     var4.use();
                     Settings.sceneSize = GameMath.limit(Settings.sceneSize + 0.05F * var5, GameWindow.minSceneSize, GameWindow.maxSceneSize);
                     Screen.updateSceneSize();
                     this.client.setMessage((int)(Settings.sceneSize * 100.0F) + "%", Color.WHITE, 1.0F);
                     this.saveZoom = true;
                  }
               }
            } else if (Control.ZOOM_IN.isDown()) {
               Settings.sceneSize = GameMath.limit(Settings.sceneSize + 0.001F * var1.getDelta(), GameWindow.minSceneSize, GameWindow.maxSceneSize);
               Screen.updateSceneSize();
               this.client.setMessage((int)(Settings.sceneSize * 100.0F) + "%", Color.WHITE, 1.0F);
               this.saveZoom = true;
            }

            if (Control.ZOOM_OUT.isMouseWheel()) {
               if (!this.formManager.isMouseOver()) {
                  var4 = Control.ZOOM_OUT.getEvent();
                  var5 = var4 != null && !var4.isUsed() ? (float)var4.getMouseWheelY() : 0.0F;
                  if (var5 != 0.0F) {
                     var4.use();
                     Settings.sceneSize = GameMath.limit(Settings.sceneSize + 0.05F * var5, GameWindow.minSceneSize, GameWindow.maxSceneSize);
                     Screen.updateSceneSize();
                     this.client.setMessage((int)(Settings.sceneSize * 100.0F) + "%", Color.WHITE, 1.0F);
                     this.saveZoom = true;
                  }
               }
            } else if (Control.ZOOM_OUT.isDown()) {
               Settings.sceneSize = GameMath.limit(Settings.sceneSize - 0.001F * var1.getDelta(), GameWindow.minSceneSize, GameWindow.maxSceneSize);
               Screen.updateSceneSize();
               this.client.setMessage((int)(Settings.sceneSize * 100.0F) + "%", Color.WHITE, 1.0F);
               this.saveZoom = true;
            }

            if (this.saveZoom && var1.isFirstGameTickInSecond()) {
               Settings.saveClientSettings();
               this.saveZoom = false;
            }

            if (this.client.getPermissionLevel().getLevel() >= PermissionLevel.OWNER.getLevel()) {
               var4 = Screen.input().getEvent(299);
               if (var4 != null && !var4.isUsed() && var4.state) {
                  if (this.client.worldSettings.allowCheats) {
                     this.formManager.debugForm.setHidden(!this.formManager.debugForm.isHidden());
                  } else {
                     this.client.chat.addMessage(Localization.translate("misc", "allowcheats"));
                  }
               }
            } else {
               this.formManager.debugForm.setHidden(true);
            }

            if (GlobalData.isDevMode()) {
               var4 = Screen.input().getEvent(297);
               InputEvent var9 = Screen.input().getEvent(298);
               if (var4 != null && var4.state || var9 != null && var9.state) {
                  float[] var6 = new float[]{0.5F, 1.0F, 1.5F, 2.0F, 4.0F, 8.0F, 16.0F, 32.0F};
                  int var7 = 0;

                  for(int var8 = 0; var8 < var6.length; ++var8) {
                     if (TickManager.globalTimeMod == var6[var8]) {
                        var7 = var8;
                        break;
                     }
                  }

                  if (var4 != null) {
                     TickManager.globalTimeMod = var6[Math.floorMod(var7 - 1, var6.length)];
                  }

                  if (var9 != null) {
                     TickManager.globalTimeMod = var6[(var7 + 1) % var6.length];
                  }

                  TickManager.skipDrawIfBehind = TickManager.globalTimeMod > 1.0F;
                  System.out.println("Time modifier: x" + TickManager.globalTimeMod);
                  this.client.setMessage("Time modifier: x" + TickManager.globalTimeMod, Color.WHITE);
               }
            }

            if (this.client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
               var4 = Screen.input().getEvent(296);
               if (var4 != null && !var4.isUsed() && var4.state) {
                  if (this.client.worldSettings.allowCheats) {
                     if (this.camera == this.defaultCamera) {
                        this.camera = new MainGameMousePanningCamera(this.camera.getX(), this.camera.getY(), 100.0F);
                     } else {
                        this.resetCamera();
                     }
                  } else {
                     this.client.chat.addMessage(Localization.translate("misc", "allowcheats"));
                  }
               }
            }

            if (Control.DEBUG_INFO.isPressed()) {
               Debug.submitChange();
            }

            if (Control.DEBUG_HUD.isPressed()) {
               HUD.debugActive = !HUD.debugActive;
            }

            if (Control.HIDE_UI.isPressed()) {
               Settings.hideUI = !Settings.hideUI;
            }

            if (this.showMap() && this.client.isDead) {
               this.setShowMap(false);
            }

            if (this.showScoreboard() && this.client.isDead) {
               this.setShowScoreboard(false);
            }

            var3.set(Screen.input().getEvent(256));
            ControllerEvent var10;
            if (var3.get() == null) {
               var10 = ControllerInput.getEvent(ControllerInput.MENU_BACK);
               if (var10 != null) {
                  var3.set(InputEvent.ControllerButtonEvent(var10, var1));
               }
            }

            if (var3.get() == null) {
               var10 = ControllerInput.getEvent(ControllerInput.MAIN_MENU);
               if (var10 != null) {
                  var3.set(InputEvent.ControllerButtonEvent(var10, var1));
               }
            }

            var4 = (InputEvent)var3.get();
            if (var4 != null && !var4.isUsed() && var4.state) {
               this.formManager.chat.submitEscapeEvent(var4);
            }

            if (Settings.pauseOnFocusLoss && !Screen.isFocused() && this.isRunning() && !this.client.isDead) {
               if (this.formManager.hasFocusForm()) {
                  this.client.closeContainer(true);
               }

               this.setRunning(false);
            }

            if (Settings.pauseOnFocusLoss && SteamData.isOverlayActive() && this.isRunning() && !this.client.isDead) {
               this.setRunning(false);
            }

            if (this.isRunning() && ControllerInput.getEvent(ControllerInput.CONTROLLER_DISCONNECTED_EVENT) != null) {
               this.setRunning(false);
            }

            ControllerInput.MENU_SET_LAYER.setActive(var2 != null && var2.isInventoryExtended() || this.client.hasFocusForm() || this.client.isDead || this.formManager.hasFocusForm() || this.formManager.isControllerKeyboardOpen() || this.formManager.hasFloatMenu() || !this.formManager.travel.isHidden() || this.showMap() || !this.isRunning());
            ControllerInput.setMoveAsMenuNavigation(!this.isRunning());
            Performance.record(var1, "formInput", (Runnable)(() -> {
               if (!Settings.hideUI) {
                  try {
                     Screen.input().getEvents().forEach((var3) -> {
                        if (Debug.isActive()) {
                           Debug.submitInputEvent(var3, this.client);
                        }

                        this.formManager.submitInputEvent(var3, var1, var2);
                     });
                     ControllerInput.getEvents().forEach((var3) -> {
                        this.formManager.submitControllerEvent(var3, var1, var2);
                     });
                  } catch (ConcurrentModificationException var4) {
                     System.err.println("ConcurrentModificationException likely caused by opening of url/file");
                     var4.printStackTrace();
                  }
               }

            }));
         }));
         Performance.record(var1, "controls", (Runnable)(() -> {
            InputEvent var4 = (InputEvent)var3.get();
            if (this.isRunning() && !this.client.isDead) {
               if (!FormTypingComponent.isCurrentlyTyping()) {
                  if (var4 != null && !var4.isUsed() && var4.state) {
                     if (this.showMap()) {
                        this.setShowMap(false);
                        var4.use();
                     } else if (Settings.hideUI) {
                        Settings.hideUI = false;
                        var4.use();
                     } else if (this.camera != this.defaultCamera) {
                        this.resetCamera();
                        var4.use();
                     } else if (this.formManager.hasFocusForm()) {
                        this.client.closeContainer(true);
                        var4.use();
                     } else if (var2 != null && var2.isInventoryExtended()) {
                        var2.setInventoryExtended(false);
                        var4.use();
                     } else {
                        this.setRunning(false);
                        var4.use();
                     }
                  }

                  if (Control.LOOT_ALL.isPressed()) {
                     this.client.getContainer().lootAllControlPressed();
                  }

                  if (Control.SORT_INVENTORY.isPressed()) {
                     this.client.getContainer().sortInventoryControlPressed();
                  }

                  if (Control.QUICK_STACK.isPressed()) {
                     this.client.getContainer().quickStackControlPressed();
                  }

                  if (Control.OPEN_ADVENTURE_PARTY.isPressed()) {
                     if (this.client.getContainer() instanceof PartyConfigContainer) {
                        this.client.closeContainer(true);
                     } else {
                        this.client.network.sendPacket(new PacketOpenPartyConfig());
                     }
                  }

                  if (Control.OPEN_SETTLEMENT.isPressed()) {
                     if (this.client.getContainer() instanceof SettlementContainer) {
                        this.client.closeContainer(true);
                     } else {
                        this.client.network.sendPacket(new PacketSettlementOpen());
                     }
                  }

                  if (Control.SHOW_MAP.isPressed()) {
                     this.setShowMap(!this.showMap());
                  }

                  if (Control.SHOW_WORLD_MAP.isPressed()) {
                     if (this.showMap()) {
                        this.setShowMap(false);
                     }

                     if (this.client.getContainer() instanceof TravelContainer) {
                        this.client.closeContainer(true);
                     } else {
                        this.client.network.sendPacket(new PacketRequestTravel(TravelDir.None));
                     }
                  }

                  if (Control.SCOREBOARD.isPressed()) {
                     this.setShowScoreboard(true);
                  }

                  if (Control.SCOREBOARD.isReleased()) {
                     this.setShowScoreboard(false);
                  }
               }

               if (var2 != null && var2.getLevel() != null && var2.getLevel() == this.client.levelManager.getDrawnLevel()) {
                  if (!this.client.hasSentSpawnPacket) {
                     this.client.network.sendPacket(new PacketSpawnPlayer(this.client));
                     this.client.hasSentSpawnPacket = true;
                  }

                  Performance.record(var1, "playerInput", (Runnable)(() -> {
                     var2.tickControls(this, var1.isGameTick(), this.camera);
                     Screen.input().getEvents().stream().filter((var0) -> {
                        return !var0.isUsed();
                     }).forEach((var2x) -> {
                        var2.submitInputEvent(this, var2x, this.camera);
                     });
                     if (var1.isGameTick() && (!this.formManager.isMouseOver() || Input.lastInputIsController && !ControllerInput.isCursorVisible())) {
                        if (Control.MOUSE1.isDown() && var2.constantAttack) {
                           var2.tryAttack(this.camera);
                        }

                        if (Control.MOUSE2.isDown() && var2.constantInteract) {
                           var2.runInteract(true, this.camera);
                        }
                     }

                  }));
                  if (var2.isInventoryExtended()) {
                     if (ControllerInput.isPressed(ControllerInput.MENU_NEXT)) {
                        var2.setSelectedSlot((var2.getSelectedSlot() + 1) % 10);
                     }

                     if (ControllerInput.isPressed(ControllerInput.MENU_PREV)) {
                        var2.setSelectedSlot((var2.getSelectedSlot() + 9) % 10);
                     }
                  }
               }
            } else if (var4 != null && !var4.isUsed() && var4.state) {
               if (Settings.hideUI) {
                  Settings.hideUI = false;
                  var4.use();
               } else if (this.formManager.chat.isTyping()) {
                  this.formManager.chat.setTyping(false);
                  var4.use();
               } else if (this.camera != this.defaultCamera) {
                  this.resetCamera();
                  var4.use();
               } else if (!this.client.isDead) {
                  this.formManager.pauseMenu.submitEscapeEvent(var4);
               }
            }

         }));
         Performance.record(var1, "formManagerTick", (Runnable)(() -> {
            this.formManager.frameTick(var1);
         }));
         if (var1.isGameTick()) {
            Client var10002 = this.client;
            Objects.requireNonNull(var10002);
            Performance.record(var1, "gameTick", (Runnable)(var10002::tick));
            if (this.isRunning() && this.client.loading.isDone()) {
               Level var4 = this.client.getLevel();
               if (var4 != null) {
                  ClientClient var5 = this.client.getClient();
                  if (var5 != null && var5.hasSpawned()) {
                     AbstractMusicList var6 = var4.biome.getLevelMusic(var4, var2);
                     if (var6 != null) {
                        Screen.setMusic(var6, Screen.MusicPriority.BIOME);
                     }
                  }
               }
            }
         }

         Performance.record(var1, "frameTick", (Runnable)(() -> {
            this.client.frameTick(var1);
         }));
         if (var1.isGameTick()) {
            Performance.recordConstant(var1, "levelEffectTick", (Runnable)(() -> {
               if (this.client.getLevel() != null && !this.client.isPaused()) {
                  this.client.getLevel().tickEffect(this.camera, var2);
               }

            }));
         }

      }
   }

   public void drawScene(TickManager var1, boolean var2) {
      PlayerMob var3 = this.getDrawnPlayer();
      Level var4 = this.getDrawnLevel();
      if (var4 != null) {
         Performance.record(var1, "camera", (Runnable)(() -> {
            if (var4.isServer() || var4 == this.client.levelManager.getLevel()) {
               int var4x = this.camera.getX();
               int var5 = this.camera.getY();
               this.camera.updateToSceneDimensions();
               this.camera.tickCamera(var1, this, this.client);
               if (var4.shouldLimitCameraWithinBounds(var3)) {
                  this.camera.limitToLevel(var4);
               }

               if (var4x != this.camera.getX() || var5 != this.camera.getY()) {
                  Screen.input().submitNextMoveEvent();
               }
            }

         }));
         var4.draw(this.camera, var3, this.client.tickManager(), var2);
      }

      Performance.record(var1, "otherDraw", (Runnable)(() -> {
         if (Settings.hideUI) {
            Screen.setCursor(Screen.CURSOR.INVISIBLE);
         }

      }));
   }

   public void drawSceneOverlay(TickManager var1) {
      PlayerMob var2 = this.getDrawnPlayer();
      Level var3 = this.getDrawnLevel();
      if (var3 != null) {
         var3.drawHud(this.camera, var2, this.client.tickManager());
      }

      if (var2 != null) {
         Rectangle var4 = var2.getSelectBox();
         Screen.setTooltipsPlayer(InputPosition.fromScenePos(Screen.input(), this.camera.getDrawX(var4.x), this.camera.getDrawY(var4.y)));
      }

   }

   public void drawHud(TickManager var1) {
      if (!Settings.hideUI) {
         GameMessage var2 = this.client.loading.getLoadingMessage();
         FontOptions var4;
         int var6;
         int var8;
         if (var2 != null) {
            int var3 = Screen.getHudHeight() / 2 + 100;
            var4 = (new FontOptions(16)).color(Color.WHITE);
            String var5 = var2.translate();
            var6 = 400;
            ArrayList var7 = GameUtils.breakString(var5, var4, var6);
            if (var7.size() <= 1) {
               var6 = FontManager.bit.getWidthCeil(var5, var4);
            }

            var8 = var7.size() * var4.getSize();
            var3 = GameMath.limit(var3, 0, Screen.getHudHeight() - var8);
            GameBackground.itemTooltip.getDrawOptions(Screen.getHudWidth() / 2 - var6 / 2 - 4, var3 - 4, var6 + 8, var8 + 8).draw();

            for(int var9 = 0; var9 < var7.size(); ++var9) {
               String var10 = (String)var7.get(var9);
               int var11 = FontManager.bit.getWidthCeil(var10, var4);
               int var12 = var3 + var9 * var4.getSize();
               int var13 = Screen.getHudWidth() / 2 - var11 / 2;
               FontManager.bit.drawString((float)var13, (float)var12, var10, var4);
            }
         }

         PlayerMob var14 = this.getDrawnPlayer();
         int var15;
         if (Settings.serverPerspective) {
            var15 = FontManager.bit.getWidthCeil("VIEWING SERVER PERSPECTIVE", new FontOptions(20));
            FontManager.bit.drawString((float)(Screen.getHudWidth() / 2 - var15 / 2), 130.0F, "VIEWING SERVER PERSPECTIVE", new FontOptions(20));
         }

         if (this.formManager != null) {
            if (this.client.messageShown()) {
               var4 = (new FontOptions(20)).outline().color(this.client.getMessageColor());
               ArrayList var16 = this.client.getMessage().breakMessage(var4, Screen.getHudWidth() / 2);

               for(var6 = 0; var6 < var16.size(); ++var6) {
                  GameMessage var17 = (GameMessage)var16.get(var6);
                  var8 = FontManager.bit.getWidthCeil(var17.translate(), var4);
                  FontManager.bit.drawString((float)(Screen.getHudWidth() / 2 - var8 / 2), (float)(this.formManager.importantBuffs.getHeight() + this.formManager.importantBuffs.getY() + 10 + var4.getSize() * var6), var17.translate(), var4);
               }
            }

            this.formManager.unimportantBuffs.setPosition(this.getStatusBarWidth() + 6, this.getStatusDrawBox(var14).getBoundingBox().y - 6);
            if (this.formManager.minimap.isMinimized()) {
               this.formManager.unimportantBuffs.columns = (Screen.getHudWidth() - this.getStatusBarWidth() + 6 - 40) / 40;
            } else {
               this.formManager.unimportantBuffs.columns = (Screen.getHudWidth() - this.getStatusBarWidth() - this.formManager.minimap.getWidth() + 6 - 40) / 40;
            }

            this.formManager.unimportantBuffs.columns = Math.max(this.formManager.unimportantBuffs.columns, 2);
            this.formManager.importantBuffs.setPosition(Screen.getHudWidth() / 2, this.getStatusBarHeight());
            var15 = Math.max(this.getStatusBarHeight() + this.formManager.importantBuffs.getHeight() + 10, Screen.getHudHeight() / 4);
            this.formManager.travel.setPosition((Screen.getHudWidth() - this.formManager.travel.getWidth()) / 2, var15);
            this.formManager.chat.setHidden(false);
            if (this.isRunning()) {
               this.drawStatusBar();
               if (Settings.showBossHealthBars) {
                  this.drawMobHealthBars();
               }
            }

            if (ControllerInput.MENU_SET_LAYER.isActive()) {
               Screen.addControllerGlyph(Localization.translate("controls", "navigatetip"), ControllerInput.MENU_UP, ControllerInput.MENU_RIGHT, ControllerInput.MENU_DOWN, ControllerInput.MENU_LEFT);
               Screen.addControllerGlyph(Localization.translate("ui", "backbutton"), ControllerInput.MENU_BACK);
            } else {
               Screen.addControllerGlyph(Localization.translate("controls", "movetip"), ControllerInput.MOVE);
               Screen.addControllerGlyph(Localization.translate("controls", "aimtip"), ControllerInput.AIM);
               Screen.addControllerGlyph(Localization.translate("controls", "hotbartip"), ControllerInput.PREV_HOTBAR, ControllerInput.NEXT_HOTBAR);
               if (this.isRunning() && var14.getSelectedItem() != null) {
                  Screen.addControllerGlyph(Localization.translate("controls", "mouse1"), ControllerInput.ATTACK);
               }

               if (var14.isRiding()) {
                  Screen.addControllerGlyph(Localization.translate("controls", "usemount"), ControllerInput.USE_MOUNT);
               }
            }

            Screen.addControllerGlyph(Localization.translate("controls", "inventory"), ControllerInput.INVENTORY);
            Performance.record(var1, "formsDraw", (Runnable)(() -> {
               this.formManager.draw(var1, var14);
            }));
            if (this.isRunning()) {
               this.client.tutorial.drawOverForms(var14);
            }

            Performance.record(var1, "debugDraw", (Runnable)(() -> {
               Debug.draw(this.client);
            }));
         }
      }
   }

   private Level getDrawnLevel() {
      Level var1 = this.client.levelManager.getDrawnLevel();
      if (Settings.serverPerspective && this.client.getLocalServer() != null) {
         var1 = this.client.getLocalServer().world.getLevel(var1.getIdentifier());
      }

      return var1;
   }

   private PlayerMob getDrawnPlayer() {
      PlayerMob var1 = this.client.getPlayer();
      if (Settings.serverPerspective && this.client.getLocalServer() != null) {
         var1 = this.client.getLocalServer().getPlayer(this.client.getSlot());
      }

      return var1;
   }

   public void setupHudDraw() {
      if (this.hudDraw != null) {
         this.hudDraw.remove();
      }

      this.hudDraw = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            if (Settings.showTeammateMarkers) {
               LinkedList var4 = new LinkedList();
               ClientClient var5 = MainGame.this.client.getClient();
               FontOptions var6 = (new FontOptions(16)).outline().color(200, 200, 200);
               int var7 = Math.min(var2.getWidth(), var2.getHeight());

               for(int var8 = 0; var8 < MainGame.this.client.getSlots(); ++var8) {
                  ClientClient var9 = MainGame.this.client.getClient(var8);
                  if (var9 != null && var9 != var5 && var9.loadedPlayer && var9.hasSpawned() && !var9.isDead() && var9.isSameTeam(var5)) {
                     String var10 = var9.getName();
                     int var11 = FontManager.bit.getWidthCeil(var10, var6);
                     final DrawOptionsList var12 = new DrawOptionsList();
                     if (var9.isSamePlace(this.getLevel())) {
                        DrawOptionsBox var13 = HUD.getDirectionIndicator(var5.playerMob.x, var5.playerMob.y, var9.playerMob.x, var9.playerMob.y, var10, var6, var2);
                        if (var13 != null) {
                           var12.add(var13);
                        } else {
                           int var14 = var2.getDrawX(var9.playerMob.getDrawX());
                           int var15 = var2.getDrawY(var9.playerMob.getDrawY()) - 60;
                           Rectangle var16 = new Rectangle(var14 - var11 / 2, var15, var11, 20);
                           if (var9.playerMob.isHealthBarVisible()) {
                              Rectangle var17 = var9.playerMob.getHealthBarBounds(var9.playerMob.getDrawX(), var9.playerMob.getDrawY());
                              Rectangle var18 = new Rectangle(var2.getDrawX(var17.x), var2.getDrawY(var17.y), var17.width, var17.height);
                              if (var18.intersects(var16)) {
                                 var16.y = var18.y - 16 - 4;
                              }
                           }

                           var12.add((new StringDrawOptions(var6, var10)).pos(var16.x, var16.y));
                           var4.add(var16);
                        }
                     } else {
                        Point2D.Float var24 = new Point2D.Float(0.0F, 1.0F);
                        String var25 = null;
                        LevelIdentifier var26 = var5.getLevelIdentifier();
                        LevelIdentifier var27 = var9.getLevelIdentifier();
                        if (var26.isIslandPosition() && var27.isIslandPosition()) {
                           var24 = GameMath.normalize((float)(var27.getIslandX() - var26.getIslandX()), (float)(var27.getIslandY() - var26.getIslandY()));
                           var25 = "(" + (var27.getIslandX() - var26.getIslandX()) + ", " + (var27.getIslandY() - var26.getIslandY()) + ")";
                           if (var27.getIslandX() == var26.getIslandX() && var27.getIslandY() == var26.getIslandY()) {
                              var24 = new Point2D.Float(0.0F, Math.signum((float)(var26.getIslandDimension() - var27.getIslandDimension())));
                              if (var27.getIslandDimension() - var26.getIslandDimension() < 0) {
                                 var25 = "(" + Localization.translate("ui", "scoredirdown") + ")";
                              } else {
                                 var25 = "(" + Localization.translate("ui", "scoredirup") + ")";
                              }
                           }
                        }

                        int var28 = Screen.getSceneWidth() / 2 + (int)(var24.x * (float)var7 / 3.0F);
                        int var29 = Screen.getSceneHeight() / 2 + (int)(var24.y * (float)var7 / 3.0F);
                        int var19 = var25 == null ? 0 : FontManager.bit.getWidthCeil(var25, var6);
                        int var20 = var25 == null ? var11 : Math.max(var11, var19);
                        Rectangle var21 = new Rectangle(var28 - var20 / 2, var29 - 16, var20, 36);

                        while(true) {
                           Rectangle var22 = new Rectangle(var21);
                           Rectangle var23 = (Rectangle)var4.stream().filter((var1x) -> {
                              return var1x.intersects(var22);
                           }).findFirst().orElse((Object)null);
                           if (var23 == null) {
                              var12.add((new StringDrawOptions(var6, var10)).pos(var28 - var11 / 2, var21.y));
                              if (var25 != null) {
                                 var12.add((new StringDrawOptions(var6, var25)).pos(var28 - var19 / 2, var21.y + 16));
                              }

                              var4.add(var21);
                              break;
                           }

                           var21 = new Rectangle(var28 - var20 / 2, var23.y + var23.height, var20, 36);
                        }
                     }

                     var1.add(new SortedDrawable() {
                        public int getPriority() {
                           return Integer.MAX_VALUE;
                        }

                        public void draw(TickManager var1) {
                           var12.draw();
                        }
                     });
                  }
               }
            }

         }
      };
      this.client.getLevel().hudManager.addElement(this.hudDraw);
   }

   public void onWindowResized() {
      if (this.formManager != null) {
         this.formManager.onWindowResized();
      }

   }

   public void setupFormManager() {
      if (this.formManager != null) {
         this.formManager.dispose();
      }

      this.formManager = new MainGameFormManager(this, this.client);
      this.formManager.setup();
   }

   public FormManager getFormManager() {
      return this.formManager;
   }

   public void onClientDrawnLevelChanged() {
      super.onClientDrawnLevelChanged();
      boolean var1 = this.formManager != null && this.formManager.debugForm.isHidden();
      this.setupFormManager();
      this.formManager.debugForm.setHidden(var1);
      this.client.tutorial.updateObjective(this);
      this.formManager.updateActive(true);
      this.formManager.chat.refreshBoundingBoxes();
      this.setupHudDraw();
   }

   public Stream<Rectangle> streamHudHitboxes() {
      PlayerMob var1 = this.client.getPlayer();
      if (var1 == null) {
         return super.streamHudHitboxes();
      } else {
         Rectangle var2 = this.getStatusDrawBox(var1).getBoundingBox();
         return Stream.concat(super.streamHudHitboxes(), Stream.of(new Rectangle(var2.x, var2.y - 32, var2.width, var2.height + 32)));
      }
   }

   public void reloadInterfaceFromSettings(boolean var1) {
      this.formManager = new MainGameFormManager(this, this.client);
      this.formManager.setup();
      this.formManager.pauseMenu.setHidden(false);
      this.formManager.pauseMenu.makeCurrent(this.formManager.pauseMenu.settings);
      if (var1) {
         this.formManager.pauseMenu.settings.makeInterfaceCurrent();
         this.formManager.pauseMenu.settings.setSaveActive(true);
         this.formManager.pauseMenu.settings.reloadedInterface = true;
      }

      this.client.tutorial.updateObjective(this);
      this.formManager.updateActive(true);
      this.formManager.chat.refreshBoundingBoxes();
   }

   public void setRunning(boolean var1) {
      boolean var2 = this.isRunning();
      super.setRunning(var1);
      if (var2 != this.isRunning()) {
         if (this.client.isSingleplayer()) {
            if (this.isRunning()) {
               this.client.resume();
            } else {
               this.client.pause();
            }
         }

         if (!this.isRunning()) {
            this.setShowScoreboard(false);
         }

         this.formManager.pauseMenu.setHidden(this.isRunning());
      }

   }

   public void drawMobHealthBars() {
      int var1 = Screen.getHudHeight() / 2;
      int var2 = this.formManager.toolbar.getWidth();
      int var3 = this.formManager.toolbar.getX();
      int var4 = this.formManager.toolbar.getY() - 10;
      if (!this.formManager.inventory.isHidden()) {
         var3 = this.formManager.inventory.getX();
         var4 = this.formManager.inventory.getY() - 10;
      }

      Iterator var5 = Screen.getStatusBars().iterator();

      while(var5.hasNext()) {
         EventStatusBarData var6 = (EventStatusBarData)var5.next();
         FontOptions var7 = (new FontOptions(16)).outline();
         EventStatusBarData.StatusAtTime var8 = var6.getLatest();
         boolean var9 = false;
         ProgressBarDrawOptions var10 = new ProgressBarDrawOptions(Settings.UI.healthbar_big_background, var2);
         Color var11 = var6.getBufferColor();
         float var13;
         if (var11 != null) {
            EventStatusBarData.StatusAtTime var12 = var6.getBuffered();
            var13 = var12.getPercent();
            var10 = var10.addBar(Settings.UI.healthbar_big_fill, var13).color(var11).end();
            var9 = true;
         }

         Color var16 = var6.getFillColor();
         if (var16 != null) {
            var13 = var8.getPercent();
            var10.addBar(Settings.UI.healthbar_big_fill, var13).color(var16).end();
            var9 = true;
         }

         GameMessage var17 = var6.getStatusText(var8);
         if (var17 != null) {
            var10.text(var17.translate()).fontOptions(var7);
         }

         if (var9) {
            var4 -= Settings.UI.healthbar_big_background.getHeight();
            var10.draw(var3, var4);
         }

         FairTypeDrawOptions var14 = var6.getDisplayNameDrawOptions();
         if (var14 != null) {
            Rectangle var15 = var14.getBoundingBox();
            var4 -= var15.height + 2;
            var14.draw(var3 + var2 / 2, var4);
         }

         if (var4 < var1 + 100) {
            break;
         }
      }

   }

   public int getStatusBarHeight() {
      if (this.client == null) {
         return 0;
      } else {
         PlayerMob var1 = this.client.getPlayer();
         if (var1 == null) {
            return 0;
         } else {
            Rectangle var2 = this.getStatusDrawBox(var1).getBoundingBox();
            return var2.y + var2.height;
         }
      }
   }

   public int getStatusBarWidth() {
      if (this.client == null) {
         return 0;
      } else {
         PlayerMob var1 = this.client.getPlayer();
         if (var1 == null) {
            return 0;
         } else {
            Rectangle var2 = this.getStatusDrawBox(var1).getBoundingBox();
            return var2.x + var2.width;
         }
      }
   }

   public void drawStatusBar() {
      if (this.client != null) {
         PlayerMob var1 = this.client.getPlayer();
         if (var1 != null) {
            FontOptions var2 = (new FontOptions(16)).outline();
            String var3 = var1.getHealth() + "/" + var1.getMaxHealth();
            int var4 = FontManager.bit.getWidthCeil(var3, var2);
            FontManager.bit.drawString((float)(Screen.getHudWidth() / 2 - var4 / 2), 16.0F, var3, var2);
            if (var1.getMaxResilience() > 0) {
               FontOptions var5 = (new FontOptions(16)).outline().color(new Color(255, 233, 73));
               String var6 = (int)var1.getResilience() + "/" + var1.getMaxResilience();
               int var7 = FontManager.bit.getWidthCeil(var6, var5);
               FontManager.bit.drawString((float)(Screen.getHudWidth() / 2 - var7 / 2 + var4 + 16), 16.0F, var6, var5);
            }

            DrawOptionsBox var8 = this.getStatusDrawBox(var1);
            var8.draw();
         }
      }
   }

   public DrawOptionsBox getStatusDrawBox(PlayerMob var1) {
      int var2 = GameMath.limit((int)Math.ceil((double)((float)var1.getMaxHealthFlat() / 200.0F)), 1, 2);
      int var3 = GameMath.limit(GameMath.ceilToNearest((float)var1.getMaxHealthFlat(), 100) / var2 / 10, 10, 20);
      int var4 = Settings.UI.heart_outline.getWidth() * 10 + 36;
      DrawOptionsBox var5 = drawStatusIconsCentered(Screen.getHudWidth() / 2, 32, Settings.UI.heart_outline, Settings.UI.heart_fill, (float)var1.getHealth(), (float)var1.getMaxHealth(), 10.0F, var3, var2, 0, var4, Settings.UI.heart_outline.getHeight() + 4, new StringTooltips(Localization.translate("ui", "healthbartip", "value", var1.getHealth() + "/" + var1.getMaxHealth())));
      if (var1.getMaxResilience() > 0) {
         Rectangle var6 = var5.getBoundingBox();
         var5 = DrawOptionsBox.concat(var5, drawStatusIconsCentered(Screen.getHudWidth() / 2, var6.y + var6.height, Settings.UI.resilience_outline, Settings.UI.resilience_fill, var1.getResilience(), (float)var1.getMaxResilience(), 10.0F, 10, 1, 0, var4, Settings.UI.resilience_outline.getHeight() + 4, new StringTooltips(Localization.translate("ui", "resiliencebartip", "value", (int)var1.getResilience() + "/" + var1.getMaxResilience()))));
      }

      final float var17;
      if (var1.getLevel() != null && var1.getLevel().getWorldSettings() != null && var1.getLevel().getWorldSettings().playerHunger()) {
         var17 = Math.min(1.0F, var1.hungerLevel);
         Rectangle var7 = var5.getBoundingBox();
         var5 = DrawOptionsBox.concat(var5, drawStatusIconsCentered(Screen.getHudWidth() / 2, var7.y + var7.height, Settings.UI.food_outline, Settings.UI.food_fill, var17, 1.0F, 0.1F, 10, 1, 0, new StringTooltips(Localization.translate("ui", "hungerbartip", "value", (int)Math.ceil((double)(var17 * 100.0F)) + "%"))));
      }

      if (var1.getLevel() != null && var1.usesMana() && var1.isManaBarVisible()) {
         var17 = var1.getMana();
         final int var18 = var1.getMaxMana();
         Rectangle var8 = var5.getBoundingBox();
         String var9 = Math.round(var17) + " / " + var18;
         FontOptions var10 = (new FontOptions(16)).outline();
         float var11 = GameMath.limit(var17 / (float)var18, 0.0F, 1.0F);
         int var12 = Screen.getHudWidth() / 2 - var4 / 2;
         int var13 = var8.y + var8.height;
         final DrawOptions var14 = (new ProgressBarDrawOptions(Settings.UI.healthbar_big_background, var4)).addBar(Settings.UI.healthbar_big_fill, var11).color(new Color(51, 133, 224)).end().text(var9).fontOptions(var10).pos(var12, var13);
         final Rectangle var15 = new Rectangle(var12, var13, var4, Settings.UI.healthbar_big_background.getHeight() + 4);
         DrawOptionsBox var16 = new DrawOptionsBox() {
            public void draw() {
               var14.draw();
               if (this.getBoundingBox().contains(Screen.mousePos().hudX, Screen.mousePos().hudY)) {
                  Screen.addTooltip(new StringTooltips(Localization.translate("ui", "manabartip", "value", (int)Math.ceil((double)var17) + "/" + var18)), TooltipLocation.PLAYER);
               }

            }

            public Rectangle getBoundingBox() {
               return var15;
            }
         };
         var5 = DrawOptionsBox.concat(var16, var5);
      }

      return var5;
   }

   public static DrawOptionsBox drawStatusIconsCentered(int var0, int var1, GameTexture var2, GameTexture var3, float var4, float var5, float var6, int var7, int var8, int var9, GameTooltips var10) {
      return drawStatusIconsCentered(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var2.getWidth() * var7 + 4 * (var7 - 1), var2.getHeight() + 4, var10);
   }

   public static DrawOptionsBox drawStatusIconsCentered(int var0, int var1, GameTexture var2, GameTexture var3, float var4, float var5, float var6, int var7, int var8, int var9, int var10, int var11, GameTooltips var12) {
      return drawStatusIcons(var0 - var10 / 2, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public static DrawOptionsBox drawStatusIcons(int var0, int var1, GameTexture var2, GameTexture var3, float var4, float var5, float var6, int var7, int var8, int var9, GameTooltips var10) {
      return drawStatusIcons(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var2.getWidth() * var7 + 4 * (var7 - 1), var2.getHeight() + 4, var10);
   }

   public static DrawOptionsBox drawStatusIcons(final int var0, final int var1, GameTexture var2, GameTexture var3, float var4, float var5, float var6, int var7, int var8, int var9, final int var10, int var11, final GameTooltips var12) {
      boolean var13 = var4 / var5 < 0.2F;
      int var14 = 0;
      int var15;
      int var16;
      int var17;
      int var21;
      float var23;
      if (var13) {
         var15 = 5000;
         var16 = 3000;
         var17 = 600;
         long var18 = (System.currentTimeMillis() + (long)var9) % (long)var15;
         if (var18 <= (long)var16) {
            int var20 = var17;

            for(var21 = 0; var20 > 0; var20 /= 2) {
               float var22 = (float)(var18 - (long)var21) / (float)var20;
               if (var22 < 0.0F) {
                  break;
               }

               var23 = (float)var20 / (float)var17;
               var14 = (int)(GameMath.sin(var22 * 180.0F) * 10.0F * var23);
               var21 += var20;
            }
         }
      }

      var15 = Math.min(var7 * var8, (int)Math.ceil((double)(var5 / var6)));
      var16 = (var2.getWidth() - var3.getWidth()) / 2;
      var17 = (var2.getHeight() - var3.getHeight()) / 2;
      float var43 = var5 / (float)var15;
      int var19 = var10 - var2.getWidth() * var7;
      float var44 = (float)var19 / (float)(var7 - 1);
      var21 = (var10 + var3.getWidth() / 2) / var7;
      int var10000 = var10 - var16 * 2;
      var23 = 0.0F;
      float var24 = 0.0F;
      int var25 = Math.min(var10 - var16 * 2, var3.getWidth() * var7);
      float var26 = ((float)var25 + Math.min(0.0F, var44 + (float)(var16 * 2))) / (float)var7;
      final DrawOptionsList var27 = new DrawOptionsList();

      final int var28;
      for(var28 = 0; var28 < var15; ++var28) {
         int var29 = var28 % var7;
         int var30 = var28 / var7;
         var23 += (float)var2.getWidth() + var44;
         var24 += var26;
         if (var29 == 0) {
            var23 = 0.0F;
            var24 = 0.0F;
         }

         int var31 = var0 + (int)var23;
         int var32 = var1 + var11 * var30;
         Math.min(var21, var3.getWidth());
         int var34 = Math.min(var15 - var30 * var7, var7);
         int var35 = Math.min(var34 * var3.getWidth(), var10 - var16 * 2);
         float var36 = var4 - (float)var30 * var43 * (float)var7;
         float var37 = (float)var34 * var43;
         int var38 = Math.min((int)(var36 / var37 * (float)var35), var35);
         int var39 = (int)var24;
         int var40 = var38 - var39;
         boolean var41 = var40 > 0;
         var27.add(var2.initDraw().pos(var31, var32 - (var41 ? var14 : 0)));
         if (var41) {
            int var42 = Math.min(var40, var3.getWidth());
            var27.add(var3.initDraw().section(0, var42, 0, var3.getHeight()).pos(var31 + var16, var32 + var17 - var14));
         }
      }

      var28 = (int)Math.ceil((double)((float)var15 / (float)var7)) * var11;
      return new DrawOptionsBox() {
         public void draw() {
            var27.draw();
            if (var12 != null && this.getBoundingBox().contains(Screen.mousePos().hudX, Screen.mousePos().hudY)) {
               Screen.addTooltip(var12, TooltipLocation.PLAYER);
            }

         }

         public Rectangle getBoundingBox() {
            return new Rectangle(var0, var1, var10, var28);
         }
      };
   }

   public boolean canTravel(Client var1, PlayerMob var2) {
      if (var2 != null && var2.getLevel() != null && var1.getLevel() != null && var1.getLevel().isIslandPosition() && var1.getLevel().getIslandDimension() == 0 && this.isRunning() && !var1.isDead) {
         return TravelContainer.getTravelDir(var2) != null;
      } else {
         return false;
      }
   }

   public void updateSteamRichPresence() {
      this.client.updateSteamRichPresence();
   }

   public MainGameCamera getCamera() {
      return this.camera;
   }

   public void setCamera(MainGameCamera var1) {
      this.camera = var1;
   }

   public void resetCamera() {
      this.camera = this.defaultCamera;
   }

   public Client getClient() {
      return this.client;
   }

   public boolean showMap() {
      return this.showMap;
   }

   public void setShowMap(boolean var1) {
      this.showMap = var1;
      if (var1 && this.client.hasOpenContainer()) {
         this.client.closeContainer(true);
      }

      this.formManager.updateActive(false);
   }

   public boolean showScoreboard() {
      return this.showScoreboard;
   }

   public void setShowScoreboard(boolean var1) {
      this.showScoreboard = var1;
      this.formManager.scoreboard.setHidden(!var1);
   }

   public void disconnect(String var1) {
      if (this.client.getLocalServer() != null) {
         this.client.getLocalServer().stop();
      }

      this.client.disconnect(var1);
      GlobalData.setCurrentState(new MainMenu((String)null, this.client));
   }

   public void dispose() {
      super.dispose();
      this.formManager.dispose();
      debugForm = null;
      if (this.hudDraw != null) {
         this.hudDraw.remove();
      }

   }

   public void onClose() {
      if (this.client != null) {
         this.client.saveAndClose("Closed client", PacketDisconnect.Code.SERVER_STOPPED);
      }

   }

   public void onCrash(List<Throwable> var1) {
      GameCrashLog.printCrashLog(var1, this.client, this.client == null ? null : this.client.getLocalServer(), "MainGame", this.client == null);
      if (this.client != null) {
         this.client.error(Localization.translate("disconnect", "clienterror"), true);
      } else {
         Screen.dispose();
      }

   }

   public SoundEmitter getALListener() {
      return this.client.getPlayer();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public GameCamera getCamera() {
      return this.getCamera();
   }
}
