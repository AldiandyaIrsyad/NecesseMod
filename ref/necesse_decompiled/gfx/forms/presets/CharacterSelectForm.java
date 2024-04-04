package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.CharacterSave;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormCharacterSaveComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public abstract class CharacterSelectForm extends FormSwitcher {
   protected boolean worldHasCheatsEnabled;
   protected boolean canEnableCheats;
   protected Form selectForm;
   protected FormContentBox charactersBox;
   protected Thread loadThread;
   protected NewCharacterForm createNewForm;
   protected HashMap<Integer, CharacterSave> extraCharacters = new HashMap();
   protected HashMap<Integer, FormCharacterSaveComponent> addedCharacters = new HashMap();

   public CharacterSelectForm(GameMessage var1, boolean var2, boolean var3) {
      this.worldHasCheatsEnabled = var2;
      this.canEnableCheats = var3;
      FormFlow var4 = new FormFlow(10);
      this.selectForm = (Form)this.addComponent(new Form("selectCharacter", 400, 10));
      this.selectForm.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "selectcharacter", new FontOptions(20), 0, this.selectForm.getWidth() / 2, 0), 5));
      if (var2) {
         this.selectForm.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "characterworldcheats", (new FontOptions(12)).color(Settings.UI.errorTextColor), 0, this.selectForm.getWidth() / 2, 0, this.selectForm.getWidth() - 20), 5));
      }

      var4.next(5);
      this.charactersBox = (FormContentBox)this.selectForm.addComponent((FormContentBox)var4.nextY(new FormContentBox(0, 0, this.selectForm.getWidth(), 350), 4));
      ((FormLocalTextButton)this.selectForm.addComponent((FormLocalTextButton)var4.nextY(new FormLocalTextButton("ui", "createnewcharacter", 4, 0, this.selectForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         this.makeCurrent(this.createNewForm);
      });
      ((FormLocalTextButton)this.selectForm.addComponent((FormLocalTextButton)var4.nextY(new FormLocalTextButton(var1, 4, 0, this.selectForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4))).onClicked((var1x) -> {
         this.onBackPressed();
      });
      this.selectForm.setHeight(var4.next());
      this.createNewForm = (NewCharacterForm)this.addComponent(new NewCharacterForm("createCharacter") {
         public void onCreatePressed(PlayerMob var1) {
            CharacterSave var2 = CharacterSave.newCharacter(var1);
            CharacterSave.saveCharacter(var2, (File)null, false);
            CharacterSelectForm.this.loadCharacters();
            CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
            this.reset();
         }

         public void onCancelPressed() {
            CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
         }
      });
      this.onWindowResized();
      this.makeCurrent(this.selectForm);
   }

   public abstract void onSelected(File var1, CharacterSave var2);

   public abstract void onBackPressed();

   public abstract void onDownloadPressed(int var1);

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

   public void addExtraCharacter(int var1, PlayerMob var2) {
      this.extraCharacters.put(var1, CharacterSave.newCharacter(var1, var2));
   }

   public void onCharacterLoaded(FormFlow var1, File var2, CharacterSave var3) {
      synchronized(this) {
         FormCharacterSaveComponent var5 = (FormCharacterSaveComponent)this.addedCharacters.get(var3.characterUniqueID);
         if (var5 != null && var3.getTimeModified() < var5.character.getTimeModified()) {
            GameLog.warn.println("Found older character file (" + var2.getName() + ") with same uniqueID as newer character (" + (var5.file == null ? null : var5.file.getName()) + ").");
         } else {
            FormCharacterSaveComponent var6 = new FormCharacterSaveComponent(this.charactersBox.getMinContentWidth(), var2, var3, this.worldHasCheatsEnabled, this.canEnableCheats) {
               public void onSelectPressed() {
                  boolean var1 = !CharacterSelectForm.this.extraCharacters.isEmpty() && CharacterSelectForm.this.extraCharacters.keySet().stream().anyMatch((var1x) -> {
                     FormCharacterSaveComponent var2 = (FormCharacterSaveComponent)CharacterSelectForm.this.addedCharacters.get(var1x);
                     return var2.file == null;
                  });
                  Runnable var2;
                  if (var1) {
                     var2 = () -> {
                        ConfirmationForm var1 = new ConfirmationForm("confirmSelect");
                        var1.setupConfirmation((GameMessage)(new LocalMessage("ui", "loadnewchararacterwarning")), () -> {
                           CharacterSelectForm.this.onSelected(this.file, this.character);
                           CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
                        }, () -> {
                           CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
                        });
                        CharacterSelectForm.this.addComponent(var1, (var1x, var2) -> {
                           if (!var2) {
                              this.removeComponent(var1x);
                           }

                        });
                        CharacterSelectForm.this.makeCurrent(var1);
                     };
                  } else {
                     var2 = () -> {
                        CharacterSelectForm.this.onSelected(this.file, this.character);
                     };
                  }

                  ConfirmationForm var3;
                  if (CharacterSelectForm.this.worldHasCheatsEnabled && !this.character.cheatsEnabled) {
                     var3 = new ConfirmationForm("confirmSelect");
                     var3.setupConfirmation((GameMessage)(new LocalMessage("ui", "loadcleancharacterwarning")), var2, () -> {
                        CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
                     });
                     CharacterSelectForm.this.addComponent(var3, (var1x, var2x) -> {
                        if (!var2x) {
                           this.removeComponent(var1x);
                        }

                     });
                     CharacterSelectForm.this.makeCurrent(var3);
                  } else if (!CharacterSelectForm.this.worldHasCheatsEnabled && this.character.cheatsEnabled) {
                     var3 = new ConfirmationForm("confirmSelect");
                     var3.setupConfirmation((GameMessage)(new LocalMessage("ui", "loadcheatscharacterwarning")), var2, () -> {
                        CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
                     });
                     CharacterSelectForm.this.addComponent(var3, (var1x, var2x) -> {
                        if (!var2x) {
                           this.removeComponent(var1x);
                        }

                     });
                     CharacterSelectForm.this.makeCurrent(var3);
                  } else {
                     var2.run();
                  }

               }

               public void onRenamePressed() {
                  CharacterSelectForm.this.startRename(this.file, this.character);
               }

               public void onDeletePressed() {
                  CharacterSelectForm.this.startDeleteConfirm(this.file, this.character);
               }

               public void onDownloadPressed() {
               }
            };
            if (var5 != null) {
               var6.setPosition(var5.getX(), var5.getY());
               this.charactersBox.removeComponent(var5);
               this.charactersBox.addComponent(var6);
               this.addedCharacters.put(var3.characterUniqueID, var6);
            } else if (var1 != null) {
               this.charactersBox.addComponent((FormCharacterSaveComponent)var1.nextY(var6));
               this.charactersBox.setContentBox(new Rectangle(this.charactersBox.getWidth(), var1.next()));
               this.addedCharacters.put(var3.characterUniqueID, var6);
            }

         }
      }
   }

   public void loadCharacters() {
      synchronized(this) {
         if (this.loadThread != null) {
            this.loadThread.interrupt();
         }

         this.charactersBox.clearComponents();
         this.addedCharacters.clear();
         final FormFlow var2 = new FormFlow(0);
         Iterator var3 = this.extraCharacters.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            final int var5 = (Integer)var4.getKey();
            FormCharacterSaveComponent var6 = (FormCharacterSaveComponent)this.charactersBox.addComponent((<undefinedtype>)var2.nextY(new FormCharacterSaveComponent(this.charactersBox.getMinContentWidth(), (File)null, (CharacterSave)var4.getValue(), this.worldHasCheatsEnabled, this.canEnableCheats) {
               public void onSelectPressed() {
                  CharacterSelectForm.this.onSelected(this.file, this.character);
               }

               public void onRenamePressed() {
               }

               public void onDeletePressed() {
               }

               public void onDownloadPressed() {
                  CharacterSelectForm.this.onDownloadPressed(var5);
               }
            }));
            this.addedCharacters.put(var5, var6);
         }

         this.charactersBox.setContentBox(new Rectangle(this.charactersBox.getWidth(), var2.next()));
         this.loadThread = new Thread("CharacterLoader") {
            public void run() {
               CharacterSave.loadCharacters((var2x, var3) -> {
                  CharacterSelectForm.this.onCharacterLoaded(var2, var2x, var3);
               }, this::isInterrupted, (var1) -> {
                  if (!var1) {
                     if (CharacterSelectForm.this.addedCharacters.isEmpty()) {
                        CharacterSelectForm.this.charactersBox.addComponent(new FormLocalLabel("ui", "nocharacterstip", new FontOptions(16), 0, CharacterSelectForm.this.charactersBox.getWidth() / 2, 10, CharacterSelectForm.this.charactersBox.getMinContentWidth() - 10));
                     }

                  }
               }, -1);
            }
         };
         this.loadThread.start();
      }
   }

   protected void startRename(final File var1, final CharacterSave var2) {
      LabeledTextInputForm var3 = new LabeledTextInputForm("renameInput", new LocalMessage("ui", "renamecharacter"), false, GameUtils.playerNameSymbolsPattern, new LocalMessage("ui", "renamebutton"), new LocalMessage("ui", "backbutton")) {
         public GameMessage getInputError(String var1x) {
            return GameUtils.isValidPlayerName(var1x);
         }

         public void onConfirmed(String var1x) {
            if (!var1x.equals(var2.player.playerName)) {
               var2.player.playerName = var1x;
               File var2x = CharacterSave.saveCharacter(var2, (File)null, false);
               if (!var2x.getName().equals(var1.getName())) {
                  CharacterSave.deleteCharacter(var1);
               }
            }

            CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
            CharacterSelectForm.this.loadCharacters();
         }

         public void onCancelled() {
            CharacterSelectForm.this.makeCurrent(CharacterSelectForm.this.selectForm);
         }
      };
      var3.setInput(var2.player.playerName);
      this.addComponent(var3, (var1x, var2x) -> {
         if (!var2x) {
            this.removeComponent(var1x);
         }

      });
      this.makeCurrent(var3);
      var3.selectAllAndSetTyping();
   }

   protected void startDeleteConfirm(File var1, CharacterSave var2) {
      ConfirmationForm var3 = new ConfirmationForm("deleteCharacter");
      var3.setupConfirmation((GameMessage)(new LocalMessage("ui", "confirmdeletecharacter", "character", var2.player.playerName)), () -> {
         System.out.println("Deleting character: " + var1.getName());
         CharacterSave.deleteCharacter(var1);
         this.extraCharacters.remove(var2.characterUniqueID);
         this.makeCurrent(this.selectForm);
         this.loadCharacters();
      }, () -> {
         this.makeCurrent(this.selectForm);
      });
      this.addComponent(var3, (var1x, var2x) -> {
         if (!var2x) {
            this.removeComponent(var1x);
         }

      });
      this.makeCurrent(var3);
   }

   public boolean isCharacterUniqueIDOccupied(int var1) {
      synchronized(this) {
         return this.addedCharacters.containsKey(var1) || this.extraCharacters.containsKey(var1);
      }
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
