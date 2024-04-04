package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipError;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainMenu;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormWorldSaveComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public abstract class WorldSelectForm extends FormSwitcher {
   protected MainMenu mainMenu;
   protected Form selectForm;
   protected FormContentBox charactersBox;
   protected Thread loadThread;
   protected NewSaveForm createNewForm;

   public WorldSelectForm(final MainMenu var1, GameMessage var2) {
      this.mainMenu = var1;
      FormFlow var3 = new FormFlow(10);
      this.selectForm = (Form)this.addComponent(new Form("selectCharacter", 400, 10));
      this.selectForm.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "selectworld", new FontOptions(20), 0, this.selectForm.getWidth() / 2, 0), 10));
      this.charactersBox = (FormContentBox)this.selectForm.addComponent((FormContentBox)var3.nextY(new FormContentBox(0, 0, this.selectForm.getWidth(), 350), 4));
      ((FormLocalTextButton)this.selectForm.addComponent((FormLocalTextButton)var3.nextY(new FormLocalTextButton("ui", "createnewworld", 4, 0, this.selectForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         this.makeCurrent(this.createNewForm);
         this.createNewForm.onStarted();
      });
      ((FormLocalTextButton)this.selectForm.addComponent((FormLocalTextButton)var3.nextY(new FormLocalTextButton(var2, 4, 0, this.selectForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         this.onBackPressed();
      });
      this.selectForm.setHeight(var3.next());
      this.createNewForm = (NewSaveForm)this.addComponent(new NewSaveForm() {
         public void createPressed(ServerSettings var1x) {
            try {
               WorldSelectForm.this.onSelected(new WorldSave(var1x), true);
            } catch (ZipError | IOException var3) {
               var1.notice(Localization.translate("misc", "createworldfailed") + "\n\n\"" + var3.getMessage() + "\"");
               var3.printStackTrace();
            } catch (FileSystemClosedException var4) {
               var1.notice(Localization.translate("misc", "createworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
               var4.printStackTrace();
            }

         }

         public void error(String var1x) {
            var1.notice(var1x);
         }

         public void backPressed() {
            WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
         }
      });
      this.onWindowResized();
      this.makeCurrent(this.selectForm);
   }

   public abstract void onSelected(WorldSave var1, boolean var2);

   public abstract void onBackPressed();

   protected void init() {
      synchronized(this) {
         super.init();
      }
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      synchronized(this) {
         super.handleInputEvent(var1, var2, var3);
         if (!var1.isUsed() && var1.state && var1.getID() == 256 && !this.isCurrent(this.selectForm)) {
            this.makeCurrent(this.selectForm);
            var1.use();
         }

      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      synchronized(this) {
         super.handleControllerEvent(var1, var2, var3);
         if (!var1.isUsed() && var1.buttonState && (var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && !this.isCurrent(this.selectForm)) {
            this.makeCurrent(this.selectForm);
            var1.use();
         }

      }
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      synchronized(this) {
         super.addNextControllerFocus(var1, var2, var3, var4, var5, var6);
      }
   }

   public void loadWorlds() {
      synchronized(this) {
         if (this.loadThread != null) {
            this.loadThread.interrupt();
         }

         this.charactersBox.clearComponents();
         final FormFlow var2 = new FormFlow(0);
         final AtomicBoolean var3 = new AtomicBoolean(false);
         this.loadThread = new Thread("CharacterLoader") {
            public void run() {
               WorldSave.loadSaves((var3x) -> {
                  synchronized(WorldSelectForm.this) {
                     FormWorldSaveComponent var5 = new FormWorldSaveComponent(WorldSelectForm.this.charactersBox.getMinContentWidth() - 5, var3x) {
                        public void onSelectPressed() {
                           WorldSelectForm.this.onSelected(this.worldSave, false);
                        }

                        public void onRenamePressed() {
                           WorldSelectForm.this.startRename(this.worldSave);
                        }

                        public void onBackupPressed(boolean var1) {
                           WorldSelectForm.this.startBackup(this.worldSave, var1);
                        }

                        public void onDeletePressed() {
                           WorldSelectForm.this.startDeleteConfirm(this.worldSave);
                        }
                     };
                     var5.setX(5);
                     WorldSelectForm.this.charactersBox.addComponent((FormWorldSaveComponent)var2.nextY(var5));
                     WorldSelectForm.this.charactersBox.setContentBox(new Rectangle(WorldSelectForm.this.charactersBox.getWidth(), var2.next()));
                     var3.set(true);
                  }
               }, this::isInterrupted, (var2x) -> {
                  if (!var2x) {
                     if (!var3.get()) {
                        WorldSelectForm.this.charactersBox.addComponent(new FormLocalLabel("ui", "nosavestip", new FontOptions(16), 0, WorldSelectForm.this.charactersBox.getWidth() / 2, 10, WorldSelectForm.this.charactersBox.getMinContentWidth() - 10));
                     }

                  }
               }, -1);
            }
         };
         this.loadThread.start();
      }
   }

   protected void startRename(final WorldSave var1) {
      LabeledTextInputForm var2 = new LabeledTextInputForm("renameInput", new LocalMessage("ui", "renameworld"), false, GameUtils.validFileNamePattern, new LocalMessage("ui", "renamebutton"), new LocalMessage("ui", "backbutton")) {
         public GameMessage getInputError(String var1x) {
            if (var1x.isEmpty()) {
               return new StaticMessage("");
            } else {
               return World.worldExistsWithName(var1x) == null && !WorldSave.isLatestBackup(var1x) ? null : new LocalMessage("ui", "renametaken");
            }
         }

         public void onConfirmed(String var1x) {
            String var2 = GameUtils.getFileExtension(var1.filePath.getName());
            String var3 = var1x + (var2 == null ? "" : "." + var2);
            if (!var3.equals(var1.filePath.getName())) {
               try {
                  World.moveWorld(var1.filePath, GameUtils.resolveFile(var1.filePath.getParentFile(), var3));
               } catch (IOException var5) {
                  WorldSelectForm.this.mainMenu.notice((GameMessage)(new LocalMessage("misc", "renamefailed")));
                  System.err.println("Failed to rename world :(");
                  var5.printStackTrace();
               }
            }

            WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
            WorldSelectForm.this.loadWorlds();
         }

         public void onCancelled() {
            WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
         }
      };
      var2.setInput(var1.displayName);
      this.addComponent(var2, (var1x, var2x) -> {
         if (!var2x) {
            this.removeComponent(var1x);
         }

      });
      this.makeCurrent(var2);
      var2.selectAllAndSetTyping();
   }

   protected void startBackup(final WorldSave var1, boolean var2) {
      LabeledTextInputForm var3 = new LabeledTextInputForm("backupInput", new LocalMessage("ui", "chooseworldname"), false, GameUtils.validFileNamePattern, new LocalMessage("ui", var2 ? "restoresave" : "backupsave"), new LocalMessage("ui", "backbutton")) {
         public GameMessage getInputError(String var1x) {
            if (var1x.isEmpty()) {
               return new StaticMessage("");
            } else {
               return World.worldExistsWithName(var1x) == null && !WorldSave.isLatestBackup(var1x) ? null : new LocalMessage("ui", "renametaken");
            }
         }

         public void onConfirmed(String var1x) {
            String var2 = GameUtils.getFileExtension(var1.filePath.getName());
            if (var2 == null) {
               var2 = "";
            }

            try {
               File var3 = GameUtils.resolveFile(var1.filePath.getParentFile(), var1x + "." + var2);
               World.copyWorld(var1.filePath, var3, true);

               try {
                  WorldSave var4 = new WorldSave(var3, true, false);
                  WorldEntity var5 = var4.getWorldEntity();
                  var5.resetUniqueID();
                  var4.getWorld().saveWorldEntity();
                  var4.closeWorldFileSystem();
                  System.out.println("Successfully backed up " + var1.filePath + " and changed uniqueID");
               } catch (ZipError | IOException var6) {
                  WorldSelectForm.this.mainMenu.notice(Localization.translate("misc", "backupworldfailed") + "\n\n\"" + var6.getMessage() + "\"");
                  var6.printStackTrace();
               } catch (FileSystemClosedException var7) {
                  WorldSelectForm.this.mainMenu.notice(Localization.translate("misc", "backupworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
                  var7.printStackTrace();
               }
            } catch (IOException var8) {
               System.err.println("Error copying world " + var1.filePath.getName() + " to " + var1x + var2);
            }

            WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
            WorldSelectForm.this.loadWorlds();
         }

         public void onCancelled() {
            WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
         }
      };
      var3.setInput(var1.displayName + (var2 ? " - Restored" : " - Backup"));
      this.addComponent(var3, (var1x, var2x) -> {
         if (!var2x) {
            this.removeComponent(var1x);
         }

      });
      this.makeCurrent(var3);
      var3.selectAllAndSetTyping();
   }

   protected void startDeleteConfirm(WorldSave var1) {
      ConfirmationForm var2 = new ConfirmationForm("deleteCharacter");
      var2.setupConfirmation((GameMessage)(new LocalMessage("ui", "confirmdeleteworld", "world", var1.filePath.getName())), () -> {
         System.out.println("Deleting world: " + var1.filePath.getName());
         if (World.deleteWorld(var1.filePath)) {
            this.loadWorlds();
         } else {
            System.err.println("Error deleting world " + var1.filePath.getName());
         }

         this.makeCurrent(this.selectForm);
      }, () -> {
         this.makeCurrent(this.selectForm);
      });
      this.addComponent(var2, (var1x, var2x) -> {
         if (!var2x) {
            this.removeComponent(var1x);
         }

      });
      this.makeCurrent(var2);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      synchronized(this) {
         super.draw(var1, var2, var3);
      }
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.selectForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.createNewForm.onWindowResized();
   }

   public void dispose() {
      super.dispose();
      if (this.loadThread != null) {
         this.loadThread.interrupt();
      }

      this.loadThread = null;
   }
}
