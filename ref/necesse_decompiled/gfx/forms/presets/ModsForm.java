package necesse.gfx.forms.presets;

import com.codedisaster.steamworks.SteamAPICall;
import com.codedisaster.steamworks.SteamNativeHandle;
import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUGC;
import com.codedisaster.steamworks.SteamUGCCallback;
import com.codedisaster.steamworks.SteamUGCDetails;
import com.codedisaster.steamworks.SteamUGCQuery;
import com.codedisaster.steamworks.SteamUGCUpdateHandle;
import com.codedisaster.steamworks.SteamRemoteStorage.WorkshopFileType;
import com.codedisaster.steamworks.SteamUGC.ItemUpdateStatus;
import com.codedisaster.steamworks.SteamUGC.MatchingUGCType;
import com.codedisaster.steamworks.SteamUGC.UserUGCList;
import com.codedisaster.steamworks.SteamUGC.UserUGCListSortOrder;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.MouseDraggingElement;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNextListData;
import necesse.engine.state.MainMenu;
import necesse.engine.steam.SteamData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsStart;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormDropAtElement;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.lists.FormModListElement;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class ModsForm extends FormSwitcher {
   private MainMenu menu;
   private ConfirmationForm confirmationForm;
   private NoticeForm updateProgressForm;
   private SteamUGCQuery publishedQuery;
   private int publishedQueryPage;
   private ArrayList<SteamUGCDetails> publishedItemsDetails = new ArrayList();
   private SteamUGCUpdateHandle updateHandle;
   private Consumer<SteamUGCUpdateHandle> createItemUpdateHandleConsumer;
   private long nextUpdateCheck;
   private Form main;
   private FormContentBox modListContent;
   private FormContentBox infoContent;
   private LoadedMod currentInfoMod;
   private List<FormModListElement> modList;
   private FormLocalTextButton saveButton;
   private FormLocalTextButton uploadButton;
   private SteamUGC steamUGC;

   public ModsForm(MainMenu var1, String var2) {
      this.menu = var1;
      this.main = (Form)this.addComponent(new Form(var2, 800, 500));
      this.main.addComponent(new FormLocalLabel("ui", "mods", new FontOptions(20), 0, this.main.getWidth() / 2, 10));
      this.main.addComponent(new FormLocalLabel("ui", "modsalpha", (new FontOptions(12)).color(Settings.UI.errorTextColor), -1, 28, 8));
      this.main.addComponent(new FormContentIconButton(5, 4, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_help_20, new GameMessage[]{new LocalMessage("ui", "modsalphatip")}));
      this.main.addComponent(new FormLocalLabel(new LocalMessage("ui", "modinfogameversion", "version", "0.24.2"), new FontOptions(12), -1, 5, 25));
      this.main.addComponent(new FormLocalLabel(new LocalMessage("ui", "modsloadorder"), new FontOptions(16), -1, 5, 40));
      this.modListContent = (FormContentBox)this.main.addComponent(new FormContentBox(5, 60, this.main.getWidth() / 2 - 10, this.main.getHeight() - 100));
      this.infoContent = (FormContentBox)this.main.addComponent(new FormContentBox(this.main.getWidth() / 2 + 5, 40, this.main.getWidth() / 2 - 10, this.main.getHeight() - 80));
      this.main.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, this.main.getWidth() / 2 - 1, 40, this.main.getHeight() - 80, false));
      ((FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "backbutton", this.main.getWidth() / 2 + 2, this.main.getHeight() - 40, this.main.getWidth() / 2 - 6))).onClicked((var1x) -> {
         if (this.saveButton.isActive()) {
            this.confirmationForm.setupConfirmation((GameMessage)(new LocalMessage("ui", "confirmnosave")), new LocalMessage("ui", "savebutton"), new LocalMessage("ui", "dontsavebutton"), () -> {
               this.savePressed(true);
            }, () -> {
               this.backPressed();
            });
            this.makeCurrent(this.confirmationForm);
         } else {
            this.backPressed();
         }

      });
      this.saveButton = (FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.main.getHeight() - 40, this.main.getWidth() / 2 - 6));
      this.saveButton.onClicked((var1x) -> {
         this.savePressed(false);
      });
      this.saveButton.setActive(false);
      this.confirmationForm = (ConfirmationForm)this.addComponent(new ConfirmationForm("confirm"));
      this.resetModsList();
      this.resetCurrent();
   }

   protected void setUpdateProgress(GameMessage var1) {
      if (this.updateProgressForm != null) {
         this.updateProgressForm.applyContinue();
      }

      this.updateProgressForm = new NoticeForm("updateprogress", 400, 400) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            if (ModsForm.this.updateHandle != null && System.currentTimeMillis() >= ModsForm.this.nextUpdateCheck) {
               SteamUGC.ItemUpdateInfo var4 = new SteamUGC.ItemUpdateInfo();
               SteamUGC.ItemUpdateStatus var5 = ModsForm.this.steamUGC.getItemUpdateProgress(ModsForm.this.updateHandle, var4);
               ModsForm.this.onUpdateProgress(var5, var4);
               ModsForm.this.nextUpdateCheck = System.currentTimeMillis() + 250L;
            }

            super.draw(var1, var2, var3);
         }
      };
      this.updateProgressForm.setButtonCooldown(-2);
      this.updateProgressForm.setupNotice(var1);
      this.menu.continueForm(this.updateProgressForm);
   }

   protected void onUpdateProgress(SteamUGC.ItemUpdateStatus var1, SteamUGC.ItemUpdateInfo var2) {
      if (var1 != ItemUpdateStatus.Invalid) {
         double var3 = (double)var2.getBytesProcessed() * 100.0 / (double)var2.getBytesTotal();
         GameMessageBuilder var5 = (new GameMessageBuilder()).append(var1.name()).append("... " + (int)var3 + "%");
         this.updateProgressForm.setupNotice((GameMessage)var5);
      }

   }

   protected void init() {
      super.init();
      this.steamUGC = new SteamUGC(new SteamUGCCallback() {
         public void onUGCQueryCompleted(SteamUGCQuery var1, int var2, int var3, boolean var4, SteamResult var5) {
            if (var5 == SteamResult.OK) {
               if (ModsForm.this.publishedQuery != null && ModsForm.this.publishedQuery.equals(var1)) {
                  int var6;
                  for(var6 = 0; var6 < var2; ++var6) {
                     SteamUGCDetails var7 = new SteamUGCDetails();
                     if (ModsForm.this.steamUGC.getQueryUGCResult(var1, var6, var7)) {
                        ModsForm.this.publishedItemsDetails.add(var7);
                     }
                  }

                  var6 = (ModsForm.this.publishedQueryPage - 1) * 50 + var3;
                  if (var3 > var6) {
                     ModsForm.this.publishedQueryPage++;
                     System.out.println("Requesting public query page " + ModsForm.this.publishedQueryPage);
                     ModsForm.this.publishedQuery = ModsForm.this.steamUGC.createQueryUserUGCRequest(SteamData.getSteamID().getAccountID(), UserUGCList.Published, MatchingUGCType.ItemsReadyToUse, UserUGCListSortOrder.LastUpdatedDesc, 1169040, 1169040, ModsForm.this.publishedQueryPage);
                     ModsForm.this.steamUGC.setReturnLongDescription(ModsForm.this.publishedQuery, true);
                     ModsForm.this.steamUGC.sendQueryUGCRequest(ModsForm.this.publishedQuery);
                  } else {
                     System.out.println("Done requesting published query");
                     ModsForm.this.startUploadSelection();
                  }
               }
            } else {
               NoticeForm var8 = (NoticeForm)ModsForm.this.addComponent(new NoticeForm("requesterror"), (var1x, var2x) -> {
                  if (!var2x) {
                     ModsForm.this.removeComponent(var1x);
                  }

               });
               var8.setupNotice((GameMessage)(new StaticMessage("Error loading: " + var5.name())));
               var8.onContinue(() -> {
                  ModsForm.this.makeCurrent(ModsForm.this.main);
               });
               var8.setButtonCooldown(2000);
               ModsForm.this.makeCurrent(var8);
            }

            ModsForm.this.steamUGC.releaseQueryUserUGCRequest(var1);
         }

         public void onRequestUGCDetails(SteamUGCDetails var1, SteamResult var2) {
            System.out.println("onRequestUGCDetails steamUGCDetails = " + var1 + ", steamResult = " + var2);
         }

         public void onCreateItem(SteamPublishedFileID var1, boolean var2, SteamResult var3) {
            if (!var2 && var3 == SteamResult.OK) {
               System.out.println("Created item with file handle: " + SteamNativeHandle.getNativeHandle(var1));
               ModsForm.this.setUpdateProgress(new LocalMessage("ui", "moduploadupdating"));
               ModsForm.this.updateHandle = ModsForm.this.steamUGC.startItemUpdate(1169040, var1);
               System.out.println("Started item update with update handle: " + ModsForm.this.updateHandle);
               ModsForm.this.createItemUpdateHandleConsumer.accept(ModsForm.this.updateHandle);
               SteamAPICall var5 = ModsForm.this.steamUGC.submitItemUpdate(ModsForm.this.updateHandle, "Mod version " + ModsForm.this.currentInfoMod.version + " for game version " + ModsForm.this.currentInfoMod.gameVersion);
               System.out.println("Updating item with call handle: " + var5);
            } else {
               NoticeForm var4 = (NoticeForm)ModsForm.this.addComponent(new NoticeForm("modcreatefailed"));
               if (var2) {
                  var4.setupNotice((GameMessage)(new LocalMessage("ui", "moduploadnotaccepted")));
               } else {
                  var4.setupNotice((GameMessage)(new LocalMessage("ui", "moduploadcreatefailed", "message", var3.toString())));
               }

               var4.onContinue(() -> {
                  ModsForm.this.makeCurrent(ModsForm.this.main);
                  ModsForm.this.removeComponent(var4);
               });
               ModsForm.this.makeCurrent(var4);
               if (ModsForm.this.updateProgressForm != null) {
                  ModsForm.this.updateProgressForm.applyContinue();
                  ModsForm.this.updateProgressForm = null;
                  ModsForm.this.updateHandle = null;
               }
            }

         }

         public void onSubmitItemUpdate(SteamPublishedFileID var1, boolean var2, SteamResult var3) {
            NoticeForm var4;
            if (!var2 && var3 == SteamResult.OK) {
               var4 = (NoticeForm)ModsForm.this.addComponent(new NoticeForm("modupdatesuccess"));
               var4.setupNotice((GameMessage)(new LocalMessage("ui", "moduploadsuccess")));
               var4.onContinue(() -> {
                  ModsForm.this.makeCurrent(ModsForm.this.main);
                  ModsForm.this.removeComponent(var4);
                  SteamData.activateGameOverlayToWebPage("steam://url/CommunityFilePage/" + SteamNativeHandle.getNativeHandle(var1));
               });
               ModsForm.this.makeCurrent(var4);
            } else {
               var4 = (NoticeForm)ModsForm.this.addComponent(new NoticeForm("modcreatefailed"));
               if (var2) {
                  var4.setupNotice((GameMessage)(new LocalMessage("ui", "moduploadnotaccepted")));
               } else {
                  var4.setupNotice((GameMessage)(new LocalMessage("ui", "moduploadcreatefailed", "message", var3.toString())));
               }

               var4.onContinue(() -> {
                  ModsForm.this.makeCurrent(ModsForm.this.main);
                  ModsForm.this.removeComponent(var4);
               });
               ModsForm.this.makeCurrent(var4);
            }

            if (ModsForm.this.updateProgressForm != null) {
               ModsForm.this.updateProgressForm.applyContinue();
               ModsForm.this.updateProgressForm = null;
               ModsForm.this.updateHandle = null;
            }

         }
      });
   }

   public void resetModsList() {
      this.infoContent.clearComponents();
      this.currentInfoMod = null;
      this.modList = new ArrayList();
      this.modListContent.clearComponents();
      List var1 = ModLoader.getAllModsSortedByCurrentList();
      Comparator var2 = Comparator.comparingInt((var0) -> {
         return var0.enabled ? -1000 : var0.mod.loadLocation.ordinal();
      });
      var2 = var2.thenComparing((var0) -> {
         return var0.enabled ? "" : var0.mod.name;
      });
      var1.sort(var2);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         ModNextListData var4 = (ModNextListData)var3.next();
         FormModListElement var5 = (FormModListElement)this.modListContent.addComponent(new FormModListElement(var4, this.modListContent.getMinContentWidth()) {
            public void onEnabledChanged(boolean var1) {
               ModsForm.this.saveButton.setActive(true);
               this.moveUpButton.setActive(var1 && this.getCurrentIndex() > 0);
               this.moveDownButton.setActive(var1 && this.getCurrentIndex() < ModsForm.this.modList.size() - 1);
               this.updateDepends(ModsForm.this.modList);
               if (var1) {
                  Iterator var2 = ModsForm.this.modList.iterator();

                  while(var2.hasNext()) {
                     FormModListElement var3 = (FormModListElement)var2.next();
                     if (var3 != this && var3.mod.id.equals(this.mod.id) && var3.listData.enabled) {
                        var3.listData.enabled = false;
                        var3.enabledCheckbox.checked = false;
                     }
                  }
               }

               if (ModsForm.this.currentInfoMod == this.mod) {
                  ModsForm.this.setupInfo(this.mod, this.dependsMet, this.optionalDependsMet);
               }

            }

            public void onMovedUp() {
               if (!Screen.input().isKeyDown(340) && !Screen.input().isKeyDown(344)) {
                  ModsForm.this.moveUp(this.getCurrentIndex());
               } else {
                  ModsForm.this.move(this.getCurrentIndex(), 0);
               }

               ModsForm.this.saveButton.setActive(true);
               Rectangle var1 = this.getBoundingBox();
               byte var2 = 30;
               Rectangle var3 = new Rectangle(var1.x - var2, var1.y - var2, var1.width + var2 * 2, var1.height + var2 * 2);
               ModsForm.this.modListContent.scrollToFit(var3);
            }

            public void onMovedDown() {
               if (!Screen.input().isKeyDown(340) && !Screen.input().isKeyDown(344)) {
                  ModsForm.this.moveDown(this.getCurrentIndex());
               } else {
                  ModsForm.this.move(this.getCurrentIndex(), ModsForm.this.modList.size());
               }

               ModsForm.this.saveButton.setActive(true);
               Rectangle var1 = this.getBoundingBox();
               byte var2 = 30;
               Rectangle var3 = new Rectangle(var1.x - var2, var1.y - var2, var1.width + var2 * 2, var1.height + var2 * 2);
               ModsForm.this.modListContent.scrollToFit(var3);
            }

            public void onStartDragged() {
               Screen.setMouseDraggingElement(new ModListDraggingElement(this));
            }

            public void onSelected() {
               ModsForm.this.setupInfo(this.mod, this.dependsMet, this.optionalDependsMet);
            }

            public boolean isCurrentlySelected() {
               return ModsForm.this.currentInfoMod == this.mod;
            }
         });
         this.modList.add(var5);
      }

      if (this.modList.isEmpty()) {
         this.modListContent.addComponent(new FormLocalLabel("ui", "modsempty", new FontOptions(20), 0, this.modListContent.getWidth() / 2, 10, this.modListContent.getWidth() - 20));
      }

      this.infoContent.addComponent(new FormLocalLabel("ui", "modsinfoselect", new FontOptions(16), 0, this.infoContent.getWidth() / 2, 5, this.infoContent.getWidth() - 10));
      this.saveButton.setActive(false);
      this.updateModsList();
   }

   public void updateModsList() {
      FormFlow var1 = new FormFlow(5);
      this.modListContent.removeComponentsIf((var0) -> {
         return var0 instanceof FormDropAtElement;
      });
      if (this.modList.isEmpty()) {
         this.modListContent.setContentBox(new Rectangle(this.modListContent.getWidth(), this.modListContent.getHeight()));
      } else {
         for(final int var2 = 0; var2 < this.modList.size(); ++var2) {
            final FormModListElement var3 = (FormModListElement)this.modList.get(var2);
            FormModListElement var4 = var2 <= 0 ? null : (FormModListElement)this.modList.get(var2 - 1);
            int var5;
            int var6;
            if (var4 == null) {
               var5 = var1.next();
               var6 = var3.getHeight() / 2;
            } else {
               var5 = var1.next() - var4.getHeight() / 2;
               var6 = var4.getHeight() / 2 + var3.getHeight() / 2;
            }

            this.modListContent.addComponent(new FormDropAtElement(0, var5, this.modListContent.getWidth(), var6) {
               public void onReleasedAt(InputEvent var1) {
                  MouseDraggingElement var2x = Screen.getMouseDraggingElement();
                  if (var2x instanceof ModListDraggingElement) {
                     Screen.setMouseDraggingElement((MouseDraggingElement)null);
                     ModListDraggingElement var3x = (ModListDraggingElement)var2x;
                     ModsForm.this.move(var3x.element.getCurrentIndex(), var2);
                     this.playTickSound();
                  }

               }

               public void draw(TickManager var1, PlayerMob var2x, Rectangle var3x) {
                  if (this.isHovering() && Screen.getMouseDraggingElement() instanceof ModListDraggingElement) {
                     Screen.initQuadDraw(this.width, 2).color(Settings.UI.activeTextColor).draw(this.getX(), var3.getY() - 1);
                  }

               }
            }, 1000);
            if (var2 == this.modList.size() - 1) {
               this.modListContent.addComponent(new FormDropAtElement(0, var1.next() + var3.getHeight() / 2, this.modListContent.getWidth(), var3.getHeight() / 2) {
                  public void onReleasedAt(InputEvent var1) {
                     MouseDraggingElement var2x = Screen.getMouseDraggingElement();
                     if (var2x instanceof ModListDraggingElement) {
                        Screen.setMouseDraggingElement((MouseDraggingElement)null);
                        ModListDraggingElement var3x = (ModListDraggingElement)var2x;
                        ModsForm.this.move(var3x.element.getCurrentIndex(), var2 + 1);
                        this.playTickSound();
                     }

                  }

                  public void draw(TickManager var1, PlayerMob var2x, Rectangle var3x) {
                     if (this.isHovering() && Screen.getMouseDraggingElement() instanceof ModListDraggingElement) {
                        Screen.initQuadDraw(this.width, 2).color(Settings.UI.activeTextColor).draw(this.getX(), var3.getY() + var3.getHeight() - 1);
                     }

                  }
               }, 1000);
            }

            var3.setPosition(0, var1.next(var3.getHeight()));
            var3.setCurrentIndex(this.modList, var2);
            var3.updateDepends(this.modList);
         }

         this.modListContent.setContentBox(new Rectangle(this.modListContent.getWidth(), var1.next() + 5));
      }

   }

   public void moveUp(int var1) {
      if (var1 > 0 && var1 < this.modList.size()) {
         FormModListElement var2 = (FormModListElement)this.modList.remove(var1);
         int var3 = Math.max(0, var1 - 1);
         this.modList.add(var3, var2);
         if (this.currentInfoMod == var2.mod) {
            this.setupInfo(var2.mod, var2.dependsMet, var2.optionalDependsMet);
         }
      }

      this.updateModsList();
   }

   public void moveDown(int var1) {
      if (var1 >= 0 && var1 < this.modList.size() - 1) {
         FormModListElement var2 = (FormModListElement)this.modList.remove(var1);
         int var3 = Math.min(this.modList.size(), var1 + 1);
         this.modList.add(var3, var2);
         if (this.currentInfoMod == var2.mod) {
            this.setupInfo(var2.mod, var2.dependsMet, var2.optionalDependsMet);
         }
      }

      this.updateModsList();
   }

   public void move(int var1, int var2) {
      var1 = GameMath.limit(var1, 0, this.modList.size() - 1);
      var2 = GameMath.limit(var2, 0, this.modList.size());
      FormModListElement var3 = (FormModListElement)this.modList.get(var1);
      this.modList.add(var2, var3);
      this.modList.remove(var1 + (var2 <= var1 ? 1 : 0));
      this.saveButton.setActive(true);
      if (this.currentInfoMod == var3.mod) {
         this.setupInfo(var3.mod, var3.dependsMet, var3.optionalDependsMet);
      }

      this.updateModsList();
   }

   public void setupInfo(LoadedMod var1, boolean[] var2, boolean[] var3) {
      if (this.uploadButton != null) {
         this.main.removeComponent(this.uploadButton);
         this.uploadButton = null;
      }

      this.currentInfoMod = var1;
      this.infoContent.clearComponents();
      FormFlow var4 = new FormFlow(5);
      int var6;
      if (var1.preview != null) {
         final TextureDrawOptionsStart var5 = var1.preview.initDraw();
         var5.shrinkHeight(128, false);
         if (var5.getWidth() > this.infoContent.getWidth() - 20) {
            var5.shrinkWidth(this.infoContent.getWidth() - 20, false);
         }

         var6 = var5.getWidth();
         int var7 = var5.getHeight();
         this.infoContent.addComponent(new FormCustomDraw(this.infoContent.getMinContentWidth() / 2 - var6 / 2, var4.next(var7 + 5), var6, var7) {
            public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
               var5.draw(this.getX(), this.getY());
            }
         });
      }

      this.infoContent.addComponent((FormLabel)var4.nextY(new FormLabel(var1.name, new FontOptions(20), -1, 5, this.infoContent.getWidth() / 2, this.infoContent.getMinContentWidth() - 10), 5));
      this.addInfoContent(var4, Localization.translate("ui", "modinfoid", "id", var1.id), new FontOptions(12));
      this.addInfoContent(var4, var1.loadLocation.message.translate(), new FontOptions(12));
      if (var1.loadLocation == ModLoadLocation.STEAM_WORKSHOP && var1.workshopFileID != null) {
         ((FormLocalTextButton)this.infoContent.addComponent((FormLocalTextButton)var4.nextY(new FormLocalTextButton("ui", "modopenworkshop", 5, 10, Math.min(350, this.infoContent.getMinContentWidth() - 10), FormInputSize.SIZE_24, ButtonColor.BASE), 5))).onClicked((var1x) -> {
            SteamData.activateGameOverlayToWebPage("steam://url/CommunityFilePage/" + SteamNativeHandle.getNativeHandle(var1.workshopFileID));
         });
      }

      this.addInfoContent(var4, Localization.translate("ui", "modinfoversion", "version", var1.version));
      Color var8 = var1.gameVersion.equals("0.24.2") ? Settings.UI.activeTextColor : Settings.UI.errorTextColor;
      this.addInfoContent(var4, Localization.translate("ui", "modinfogameversion", "version", GameColor.getCustomColorCode(var8) + var1.gameVersion));
      if (var1.clientside) {
         this.addInfoContent(var4, Localization.translate("ui", "modclientside"), new FontOptions(12));
      }

      Color var9;
      if (var1.depends.length > 0) {
         this.addInfoContent(var4, Localization.translate("ui", "modinfodep"));

         for(var6 = 0; var6 < var1.depends.length; ++var6) {
            var9 = var2[var6] ? Settings.UI.activeTextColor : Settings.UI.errorTextColor;
            this.addInfoContent(var4, 20, GameColor.getCustomColorCode(var9) + ModLoader.getModName(var1.depends[var6]), new FontOptions(12));
         }
      }

      if (var1.optionalDepends.length > 0) {
         this.addInfoContent(var4, Localization.translate("ui", "modinfooptdep"));

         for(var6 = 0; var6 < var1.optionalDepends.length; ++var6) {
            var9 = var3[var6] ? Settings.UI.activeTextColor : Settings.UI.warningTextColor;
            this.addInfoContent(var4, 20, GameColor.getCustomColorCode(var9) + ModLoader.getModName(var1.optionalDepends[var6]), new FontOptions(12));
         }
      }

      this.addInfoContent(var4, Localization.translate("ui", "modauthor", "author", var1.author));
      this.addInfoContent(var4, Localization.translate("ui", "moddescription", "description", var1.description));
      Iterator var10 = var1.modInfo.keySet().iterator();

      while(var10.hasNext()) {
         String var11 = (String)var10.next();
         this.addInfoContent(var4, var11 + ": " + (String)var1.modInfo.get(var11));
      }

      this.infoContent.setHeight(this.main.getHeight() - this.infoContent.getY() - 40);
      if (var1.hasLoaded() && var1.devModFolder != null && var1.preview != null) {
         this.infoContent.setHeight(this.infoContent.getHeight() - 28);
         this.uploadButton = (FormLocalTextButton)this.main.addComponent(new FormLocalTextButton("ui", "modupload", this.main.getWidth() / 2 + 4, this.main.getHeight() - 40 - 24, this.main.getWidth() / 2 - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
         if (var1.hasExampleModPackageClasses()) {
            this.uploadButton.setActive(false);
            this.uploadButton.setLocalTooltip("ui", "moduploadexamplepackage");
         }

         this.uploadButton.onClicked((var1x) -> {
            NoticeForm var2 = (NoticeForm)this.addComponent(new NoticeForm("loadpublished"), (var1, var2x) -> {
               if (!var2x) {
                  this.removeComponent(var1);
               }

            });
            var2.setupNotice((GameMessage)(new LocalMessage("ui", "loadingdotdot")), new LocalMessage("ui", "cancelbutton"));
            var2.onContinue(() -> {
               this.publishedQuery = null;
               this.makeCurrent(this.main);
            });
            var2.setButtonCooldown(5000);
            this.makeCurrent(var2);
            System.out.println("Requesting public query first page");
            this.publishedQueryPage = 1;
            this.publishedItemsDetails.clear();
            this.publishedQuery = this.steamUGC.createQueryUserUGCRequest(SteamData.getSteamID().getAccountID(), UserUGCList.Published, MatchingUGCType.ItemsReadyToUse, UserUGCListSortOrder.LastUpdatedDesc, 1169040, 1169040, this.publishedQueryPage);
            this.steamUGC.setReturnLongDescription(this.publishedQuery, true);
            this.steamUGC.sendQueryUGCRequest(this.publishedQuery);
         });
      }

      this.infoContent.setContentBox(new Rectangle(this.infoContent.getWidth(), var4.next()));
      this.infoContent.setScrollY(0);
   }

   protected void addInfoContent(FormFlow var1, int var2, String var3, FontOptions var4) {
      this.infoContent.addComponent((FormFairTypeLabel)var1.nextY((new FormFairTypeLabel(var3, var2, 5)).setFontOptions(var4).setMaxWidth(this.infoContent.getMinContentWidth() - 5 - var2), 5));
   }

   protected void addInfoContent(FormFlow var1, String var2, FontOptions var3) {
      this.addInfoContent(var1, 5, var2, var3);
   }

   protected void addInfoContent(FormFlow var1, String var2) {
      this.addInfoContent(var1, var2, new FontOptions(16));
   }

   protected void startUploadSelection() {
      Form var1 = (Form)this.addComponent(new Form("selectuploadtype", 500, 400));
      FormFlow var2 = new FormFlow(5);
      var1.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "moduploadselect", new FontOptions(16), 0, var1.getWidth() / 2, 10, var1.getWidth() - 20), 10));
      FormDropdownSelectionButton var3 = (FormDropdownSelectionButton)var1.addComponent(new FormDropdownSelectionButton(10, var2.next(30), FormInputSize.SIZE_24, ButtonColor.BASE, var1.getWidth() - 20));
      var3.setSelected(-1, new LocalMessage("ui", "moduploadcreatenew"));
      var3.options.add(-1, new LocalMessage("ui", "moduploadcreatenew"));

      for(int var4 = 0; var4 < this.publishedItemsDetails.size(); ++var4) {
         SteamUGCDetails var5 = (SteamUGCDetails)this.publishedItemsDetails.get(var4);
         var3.options.add(var4, new StaticMessage(var5.getTitle() + " (" + SteamNativeHandle.getNativeHandle(var5.getPublishedFileID()) + ")"));
      }

      var2.next(5);
      FormLocalCheckBox var16 = (FormLocalCheckBox)var1.addComponent((FormLocalCheckBox)var2.nextY(new FormLocalCheckBox(new LocalMessage("ui", "moduploadupdatedesc"), 5, 10, true), 5));
      var16.handleClicksIfNoEventHandlers = true;
      short var17 = 200;
      FormContentBox var6 = (FormContentBox)var1.addComponent(new FormContentBox(4, var2.next(var17) + 4, var1.getWidth() - 8, var17 - 8, GameBackground.textBox));
      FormTextBox var7 = (FormTextBox)var6.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, var6.getMinContentWidth(), 100, 5000));
      var7.allowTyping = true;
      var7.setEmptyTextSpace(new Rectangle(var6.getX(), var6.getY(), var6.getWidth(), var6.getHeight()));
      var7.setText(this.currentInfoMod.description);
      var7.onChange((var2x) -> {
         Rectangle var3 = var6.getContentBoxToFitComponents();
         var6.setContentBox(var3);
         var6.scrollToFit(var7.getCaretBoundingBox());
      });
      var7.onCaretMove((var2x) -> {
         if (!var2x.causedByMouse) {
            var6.scrollToFit(var7.getCaretBoundingBox());
         }

      });
      var2.next(5);
      var1.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "moduploadselecttags", new FontOptions(16), -1, 5, 10, var1.getWidth() - 20), 10));
      String[] var8 = new String[]{"Translation", "Texture pack", "Interface", "New features", "New content", "Tweaks", "Miscellaneous"};
      ArrayList var9 = new ArrayList();
      if (this.currentInfoMod.clientside) {
         var9.add("Client mod");
      }

      HashMap var10 = new HashMap();
      String[] var11 = var8;
      int var12 = var8.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         String var14 = var11[var13];
         FormCheckBox var15 = (FormCheckBox)var1.addComponent((FormCheckBox)var2.nextY(new FormCheckBox(var14, 5, 10, var1.getWidth() - 10), 5));
         var15.handleClicksIfNoEventHandlers = true;
         var10.put(var14, var15);
      }

      var3.onSelected((var3x) -> {
         var10.values().forEach((var0) -> {
            var0.checked = false;
         });
         int var4 = (Integer)var3x.value;
         if (var4 != -1) {
            SteamUGCDetails var5 = (SteamUGCDetails)this.publishedItemsDetails.get(var4);
            var7.setText(var5.getDescription());
            String[] var6 = var5.getTags().split(",");
            System.out.println("Selected mod with tags " + Arrays.toString(var6));
            String[] var7x = var6;
            int var8 = var6.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               String var10x = var7x[var9];
               FormCheckBox var11 = (FormCheckBox)var10.get(var10x);
               if (var11 != null) {
                  var11.checked = true;
               }
            }
         } else {
            var7.setText(this.currentInfoMod.description);
         }

      });
      int var18 = var2.next(40);
      ((FormLocalTextButton)var1.addComponent(new FormLocalTextButton("ui", "continuebutton", 4, var18, var1.getWidth() / 2 - 6))).onClicked((var7x) -> {
         int var8 = (Integer)var3.getSelected();
         if (var8 == -1) {
            this.startUploadConfirm(() -> {
               if (this.currentInfoMod.validateDevFolder()) {
                  File var5 = this.currentInfoMod.preview.saveTextureImage(this.currentInfoMod.devModFolder.getAbsolutePath() + "/preview.png");
                  this.createItemUpdateHandleConsumer = (var6x) -> {
                     this.steamUGC.setItemTitle(var6x, this.currentInfoMod.name);
                     if (var16.checked) {
                        this.steamUGC.setItemDescription(var6x, var7.getText());
                     }

                     System.out.println("Setting mod content to " + this.currentInfoMod.devModFolder.getAbsolutePath());
                     this.steamUGC.setItemContent(var6x, this.currentInfoMod.devModFolder.getAbsolutePath());
                     System.out.println("Setting mod preview to " + var5.getAbsolutePath());
                     this.steamUGC.setItemPreview(var6x, var5.getAbsolutePath());
                     String[] var7x = (String[])Stream.concat(var10.entrySet().stream().filter((var0) -> {
                        return ((FormCheckBox)var0.getValue()).checked;
                     }).map(Map.Entry::getKey), var9.stream()).toArray((var0) -> {
                        return new String[var0];
                     });
                     System.out.println("Setting mod tags to " + Arrays.toString(var7x));
                     this.steamUGC.setItemTags(var6x, var7x);
                  };
                  this.setUpdateProgress(new LocalMessage("ui", "moduploadcreating"));
                  this.makeCurrent(this.main);
                  SteamAPICall var6 = this.steamUGC.createItem(1169040, WorkshopFileType.Community);
                  System.out.println("Creating community file with call handle " + SteamNativeHandle.getNativeHandle(var6));
               } else {
                  NoticeForm var7x = (NoticeForm)this.addComponent(new NoticeForm("invalidmodfolder"));
                  var7x.setupNotice((GameMessage)(new LocalMessage("ui", "moduploadfolderinvalid")));
                  var7x.onContinue(() -> {
                     this.makeCurrent(this.main);
                     this.removeComponent(var7x);
                  });
                  this.makeCurrent(var7x);
               }

            });
         } else {
            this.startUploadConfirm(() -> {
               if (this.currentInfoMod.validateDevFolder()) {
                  SteamUGCDetails var6 = (SteamUGCDetails)this.publishedItemsDetails.get(var8);
                  this.setUpdateProgress(new LocalMessage("ui", "moduploadupdating"));
                  this.updateHandle = this.steamUGC.startItemUpdate(1169040, var6.getPublishedFileID());
                  System.out.println("Started item update with update handle: " + this.updateHandle);
                  if (var16.checked) {
                     this.steamUGC.setItemDescription(this.updateHandle, var7.getText());
                  }

                  System.out.println("Setting mod content to " + this.currentInfoMod.devModFolder.getAbsolutePath());
                  this.steamUGC.setItemContent(this.updateHandle, this.currentInfoMod.devModFolder.getAbsolutePath());
                  File var7x = this.currentInfoMod.preview.saveTextureImage(this.currentInfoMod.devModFolder.getAbsolutePath() + "/preview.png");
                  System.out.println("Setting mod preview to " + var7x.getAbsolutePath());
                  this.steamUGC.setItemPreview(this.updateHandle, var7x.getAbsolutePath());
                  String[] var8x = (String[])Stream.concat(var10.entrySet().stream().filter((var0) -> {
                     return ((FormCheckBox)var0.getValue()).checked;
                  }).map(Map.Entry::getKey), var9.stream()).toArray((var0) -> {
                     return new String[var0];
                  });
                  System.out.println("Setting mod tags to " + Arrays.toString(var8x));
                  this.steamUGC.setItemTags(this.updateHandle, var8x);
                  SteamAPICall var9x = this.steamUGC.submitItemUpdate(this.updateHandle, "Mod version " + this.currentInfoMod.version + " for game version " + this.currentInfoMod.gameVersion);
                  System.out.println("Updating item with call handle: " + var9x);
               } else {
                  NoticeForm var10x = (NoticeForm)this.addComponent(new NoticeForm("invalidmodfolder"));
                  var10x.setupNotice((GameMessage)(new LocalMessage("ui", "moduploadfolderinvalid")));
                  var10x.onContinue(() -> {
                     this.makeCurrent(this.main);
                     this.removeComponent(var10x);
                  });
                  this.makeCurrent(var10x);
               }

            });
         }

         this.removeComponent(var1);
      });
      ((FormLocalTextButton)var1.addComponent(new FormLocalTextButton("ui", "backbutton", var1.getWidth() / 2 + 2, var18, var1.getWidth() / 2 - 6))).onClicked((var2x) -> {
         this.makeCurrent(this.main);
         this.removeComponent(var1);
      });
      this.makeCurrent(var1);
      var1.setHeight(var2.next());
      var1.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   protected void startUploadConfirm(Runnable var1) {
      ConfirmationForm var2 = (ConfirmationForm)this.addComponent(new ConfirmationForm("confirmagreement", 400, 600));
      var2.setupConfirmation((Consumer)((var0) -> {
         FormFlow var1 = new FormFlow(10);
         String var2 = "https://steamcommunity.com/sharedfiles/workshoplegalagreement";
         GameMessageBuilder var3 = (new GameMessageBuilder()).append("ui", "moduploadtermsagree").append("\n\n" + var2);
         var0.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(var3, new FontOptions(20), 0, var0.getWidth() / 2, 10, var0.getWidth() - 20), 10));
         ((FormLocalTextButton)var0.addComponent((FormLocalTextButton)var1.nextY(new FormLocalTextButton(new LocalMessage("ui", "moduploadtermsopen"), new LocalMessage("misc", "openurl", "url", var2), 20, 10, var0.getWidth() - 40, FormInputSize.SIZE_24, ButtonColor.BASE), 10))).onClicked((var1x) -> {
            GameUtils.openURL(var2);
         });
      }), new LocalMessage("ui", "continuebutton"), new LocalMessage("ui", "backbutton"), () -> {
         ConfirmationForm var3 = (ConfirmationForm)this.addComponent(new ConfirmationForm("confirmmine", 400, 600));
         var3.setupConfirmation((GameMessage)(new LocalMessage("ui", "moduploadmine")), () -> {
            this.makeCurrent(this.main);
            this.removeComponent(var3);
            var1.run();
         }, () -> {
            this.makeCurrent(this.main);
            this.removeComponent(var3);
         });
         var3.startConfirmCooldown(5000, true);
         this.makeCurrent(var3);
         this.removeComponent(var2);
      }, () -> {
         this.makeCurrent(this.main);
         this.removeComponent(var2);
      });
      var2.startConfirmCooldown(5000, true);
      this.makeCurrent(var2);
   }

   private void savePressed(boolean var1) {
      List var2 = (List)this.modList.stream().map((var0) -> {
         return var0.listData;
      }).collect(Collectors.toList());
      ModLoader.saveModListSettings(var2);
      this.confirmationForm.setupConfirmation((GameMessage)(new LocalMessage("ui", "modssavenotice")), new LocalMessage("ui", "modsquit"), new LocalMessage("ui", "modslater"), () -> {
         Screen.requestClose();
      }, () -> {
         this.makeCurrent(this.main);
         if (var1) {
            this.backPressed();
         }

      });
      this.saveButton.setActive(false);
      this.makeCurrent(this.confirmationForm);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.main.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.confirmationForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void resetCurrent() {
      this.makeCurrent(this.main);
   }

   public void backPressed() {
   }

   public void dispose() {
      super.dispose();
      if (this.steamUGC != null) {
         this.steamUGC.dispose();
      }

   }

   public static class ModListDraggingElement implements MouseDraggingElement {
      public final FormModListElement element;

      public ModListDraggingElement(FormModListElement var1) {
         this.element = var1;
      }

      public boolean draw(int var1, int var2) {
         FontManager.bit.drawString((float)var1, (float)(var2 - 20), this.element.mod.name, (new FontOptions(16)).outline().alphaf(0.8F));
         return true;
      }
   }
}
