package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.lists.FormModListSimpleElement;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;

public abstract class ModSaveListMismatchForm extends FormSwitcher {
   private ConfirmationForm intro = (ConfirmationForm)this.addComponent(new ConfirmationForm("intro"));
   private Form review = (Form)this.addComponent(new Form("review", 640, 320));
   private ModSaveConfirmationForm useSavesConfirmation;
   private FormContentBox saveModsContent;
   private FormContentBox myModsContent;
   private FormTextButton useSaves;
   private List<ModListData> modsList;

   public ModSaveListMismatchForm() {
      this.review.addComponent(new FormLocalLabel("ui", "worldmods", new FontOptions(20), 0, this.review.getWidth() / 4, 2));
      this.saveModsContent = (FormContentBox)this.review.addComponent(new FormContentBox(4, 24, this.review.getWidth() / 2 - 8, this.review.getHeight() - 64));
      this.review.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, this.review.getWidth() / 2, 4, this.review.getHeight() - 40 - 8, false));
      this.review.addComponent(new FormLocalLabel("ui", "mymods", new FontOptions(20), 0, this.review.getWidth() - this.review.getWidth() / 4, 2));
      this.myModsContent = (FormContentBox)this.review.addComponent(new FormContentBox(this.review.getWidth() / 2 + 4, 24, this.review.getWidth() / 2 - 8, this.review.getHeight() - 64));
      this.useSaves = (FormTextButton)this.review.addComponent(new FormLocalTextButton("ui", "useworldmods", 4, this.review.getHeight() - 40, this.review.getWidth() / 2 - 6));
      this.useSaves.onClicked((var1) -> {
         if (this.modsList != null) {
            ModLoader.saveModListSettings(this.modsList);
         } else {
            System.err.println("Error saving mod list");
         }

         this.makeCurrent(this.useSavesConfirmation);
      });
      ((FormLocalTextButton)this.review.addComponent(new FormLocalTextButton("ui", "backbutton", this.review.getWidth() / 2 + 2, this.review.getHeight() - 40, this.review.getWidth() / 2 - 6))).onClicked((var1) -> {
         this.backPressed();
      });
      this.useSavesConfirmation = (ModSaveConfirmationForm)this.addComponent(new ModSaveConfirmationForm("useserverconf"));
      this.useSavesConfirmation.setupModSaveConfirmation(this::backPressed);
      this.makeCurrent(this.intro);
   }

   public void setup(List<ModSaveInfo> var1) {
      this.intro.setupConfirmation((GameMessage)(new LocalMessage("ui", "modmismatchworld")), new LocalMessage("ui", "modloadsaveanyway"), new LocalMessage("ui", "modreview"), this::loadAnywayPressed, () -> {
         this.makeCurrent(this.review);
      });
      this.saveModsContent.clearComponents();
      this.myModsContent.clearComponents();
      FormFlow var2 = new FormFlow();
      FormFlow var3 = new FormFlow();
      ArrayList var4 = new ArrayList(ModLoader.getAllMods());
      Comparator var5 = Comparator.comparingInt((var0) -> {
         return var0.isEnabled() ? -1000 : var0.loadLocation.ordinal();
      });
      var5 = var5.thenComparing((var0) -> {
         return var0.isEnabled() ? "" : var0.name;
      });
      var4.sort(var5);
      boolean var6 = true;
      this.modsList = new ArrayList();
      if (var1.isEmpty()) {
         var2.next(20);
         this.saveModsContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "nomodsused", new FontOptions(16), 0, this.saveModsContent.getWidth() / 2, 0, this.saveModsContent.getWidth() - 20), 10));
      }

      int var7 = 0;
      int var8 = 0;

      while(true) {
         while(true) {
            LoadedMod var9 = var7 < var4.size() ? (LoadedMod)var4.get(var7) : null;
            if (var9 != null && !var9.isEnabled()) {
               this.myModsContent.addComponent((FormModListSimpleElement)var3.nextY(new FormModListSimpleElement(var9.name + " " + var9.version, this.myModsContent.getMinContentWidth(), var9.loadLocation, false, Settings.UI.inactiveTextColor, Localization.translate("ui", "moddisabled"))));
               ModListData var16 = new ModListData(var9);
               var16.enabled = false;
               this.modsList.add(var16);
               ++var7;
            } else {
               ModSaveInfo var10 = var8 < var1.size() ? (ModSaveInfo)var1.get(var8) : null;
               ModListData var11;
               if (var10 == null) {
                  if (var9 == null) {
                     this.useSaves.setActive(var6);
                     if (!var6) {
                        this.useSaves.setTooltip(Localization.translate("ui", "modmissingsome"));
                        this.modsList = null;
                     } else {
                        this.useSaves.setTooltip((String)null);
                     }

                     this.myModsContent.setContentBox(new Rectangle(this.myModsContent.getWidth(), var3.next()));
                     this.saveModsContent.setContentBox(new Rectangle(this.saveModsContent.getWidth(), var2.next()));
                     this.makeCurrent(this.intro);
                     return;
                  }

                  this.myModsContent.addComponent((FormModListSimpleElement)var3.nextY(new FormModListSimpleElement(var9.name + " " + var9.version, this.myModsContent.getMinContentWidth(), var9.loadLocation, true, Settings.UI.activeTextColor)));
                  var11 = new ModListData(var9);
                  var11.enabled = false;
                  this.modsList.add(var11);
                  ++var7;
               } else {
                  if (var9 != null) {
                     if (this.modsList.stream().noneMatch((var1x) -> {
                        return var1x.id.equals(var9.id);
                     })) {
                        var11 = new ModListData(var9);
                        var11.enabled = false;
                        this.modsList.add(var11);
                     }

                     this.myModsContent.addComponent((FormModListSimpleElement)var3.nextY(new FormModListSimpleElement(var9.name + " " + var9.version, this.myModsContent.getMinContentWidth(), var9.loadLocation, true, Settings.UI.activeTextColor)));
                     ++var7;
                  }

                  boolean var17 = var4.stream().anyMatch((var1x) -> {
                     return var1x.id.equals(var10.id);
                  });
                  boolean var12 = var17 && var4.stream().anyMatch((var1x) -> {
                     return var1x.id.equals(var10.id) && var1x.version.equals(var10.version);
                  });
                  Color var13 = Settings.UI.activeTextColor;
                  StringTooltips var14 = null;
                  if (!var17) {
                     var13 = Settings.UI.errorTextColor;
                     var14 = new StringTooltips(Localization.translate("ui", "modmissing"));
                     var6 = false;
                  } else if (!var12) {
                     var13 = Settings.UI.errorTextColor;
                     var14 = new StringTooltips(Localization.translate("ui", "modinversion"));
                     var6 = false;
                  } else {
                     if (var9 == null || !var9.id.equals(var10.id) || !var9.version.equals(var10.version)) {
                        var13 = Settings.UI.warningTextColor;
                        var14 = new StringTooltips(Localization.translate("ui", "modinposition"));
                     }

                     LoadedMod var15 = (LoadedMod)var4.stream().filter((var1x) -> {
                        return var1x.id.equals(var10.id);
                     }).filter((var1x) -> {
                        return var1x.version.equals(var10.version);
                     }).findFirst().orElse((Object)null);
                     if (var15 != null) {
                        this.modsList.removeIf((var1x) -> {
                           return var1x.id.equals(var15.id);
                        });
                        this.modsList.add(new ModListData(var10, var15.loadLocation));
                     } else {
                        var6 = false;
                     }
                  }

                  this.saveModsContent.addComponent((FormModListSimpleElement)var2.nextY(new FormModListSimpleElement(var10.name + " " + var10.version, this.saveModsContent.getMinContentWidth(), var10.workshopID == -1L ? ModLoadLocation.MODS_FOLDER : ModLoadLocation.STEAM_WORKSHOP, true, var13, var14)));
                  ++var8;
               }
            }
         }
      }
   }

   public abstract void loadAnywayPressed();

   public abstract void backPressed();

   public void onWindowResized() {
      super.onWindowResized();
      this.intro.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.review.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.useSavesConfirmation.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }
}
