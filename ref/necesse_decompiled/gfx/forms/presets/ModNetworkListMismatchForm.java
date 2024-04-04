package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
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
import necesse.engine.modLoader.ModNetworkData;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.lists.FormModListSimpleElement;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;

public class ModNetworkListMismatchForm extends FormSwitcher implements ContinueComponent {
   private ConfirmationForm intro = (ConfirmationForm)this.addComponent(new ConfirmationForm("intro"));
   private Form review = (Form)this.addComponent(new Form("review", 640, 320));
   private ModSaveConfirmationForm useServerConfirmation;
   private FormContentBox serverModsContent;
   private FormContentBox myModsContent;
   private FormTextButton useServer;
   private List<ModListData> serverModsList;
   private ArrayList<Runnable> continueEvents = new ArrayList();
   private boolean isContinued;

   public ModNetworkListMismatchForm() {
      this.review.addComponent(new FormLocalLabel("ui", "servermods", new FontOptions(20), 0, this.review.getWidth() / 4, 2));
      this.serverModsContent = (FormContentBox)this.review.addComponent(new FormContentBox(4, 24, this.review.getWidth() / 2 - 8, this.review.getHeight() - 64));
      this.review.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, this.review.getWidth() / 2, 4, this.review.getHeight() - 40 - 8, false));
      this.review.addComponent(new FormLocalLabel("ui", "mymods", new FontOptions(20), 0, this.review.getWidth() - this.review.getWidth() / 4, 2));
      this.myModsContent = (FormContentBox)this.review.addComponent(new FormContentBox(this.review.getWidth() / 2 + 4, 24, this.review.getWidth() / 2 - 8, this.review.getHeight() - 64));
      this.useServer = (FormTextButton)this.review.addComponent(new FormLocalTextButton("ui", "useservermods", 4, this.review.getHeight() - 40, this.review.getWidth() / 2 - 6));
      this.useServer.onClicked((var1) -> {
         if (this.serverModsList != null) {
            ModLoader.saveModListSettings(this.serverModsList);
         } else {
            System.err.println("Error saving server mod list");
         }

         this.makeCurrent(this.useServerConfirmation);
      });
      ((FormLocalTextButton)this.review.addComponent(new FormLocalTextButton("ui", "continuebutton", this.review.getWidth() / 2 + 2, this.review.getHeight() - 40, this.review.getWidth() / 2 - 6))).onClicked((var1) -> {
         this.applyContinue();
      });
      this.useServerConfirmation = (ModSaveConfirmationForm)this.addComponent(new ModSaveConfirmationForm("useserverconf"));
      this.useServerConfirmation.setupModSaveConfirmation(this::applyContinue);
      this.makeCurrent(this.intro);
   }

   public void setup(List<ModNetworkData> var1) {
      this.intro.setupConfirmation((GameMessage)(new LocalMessage("ui", "modmismatch")), new LocalMessage("ui", "continuebutton"), new LocalMessage("ui", "modreview"), this::applyContinue, () -> {
         this.makeCurrent(this.review);
      });
      this.serverModsContent.clearComponents();
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
      this.serverModsList = new ArrayList();
      Iterator var7 = var1.iterator();

      LoadedMod var9;
      while(var7.hasNext()) {
         ModNetworkData var8 = (ModNetworkData)var7.next();
         var9 = (LoadedMod)var4.stream().filter((var1x) -> {
            return var1x.id.equals(var8.id) && var1x.version.equals(var8.version);
         }).findFirst().orElse((Object)null);
         if (var9 != null) {
            ModListData var10 = new ModListData(var9);
            var10.enabled = true;
            this.serverModsList.add(var10);
         } else if (!var8.clientside) {
            var6 = false;
         }
      }

      if (var6) {
         var7 = var4.iterator();

         while(var7.hasNext()) {
            LoadedMod var16 = (LoadedMod)var7.next();
            if (this.serverModsList.stream().noneMatch((var1x) -> {
               return var1x.matchesMod(var16);
            })) {
               ModListData var18 = new ModListData(var16);
               var18.enabled = false;
               this.serverModsList.add(var18);
            }
         }
      }

      int var15 = 0;
      int var17 = 0;

      while(true) {
         while(true) {
            var9 = var15 < var4.size() ? (LoadedMod)var4.get(var15) : null;
            if (var9 != null && (!var9.isEnabled() || var9.clientside)) {
               if (!var9.isEnabled()) {
                  this.myModsContent.addComponent((FormModListSimpleElement)var3.nextY(new FormModListSimpleElement(var9.name + " " + var9.version, this.myModsContent.getMinContentWidth(), var9.loadLocation, false, Settings.UI.inactiveTextColor, Localization.translate("ui", "moddisabled"))));
               } else if (var9.clientside) {
                  this.myModsContent.addComponent((FormModListSimpleElement)var3.nextY(new FormModListSimpleElement(var9.name + " " + var9.version, this.myModsContent.getMinContentWidth(), var9.loadLocation, true, Settings.UI.inactiveTextColor, Localization.translate("ui", "modclientside"))));
               }

               ++var15;
            } else {
               ModNetworkData var19 = var17 < var1.size() ? (ModNetworkData)var1.get(var17) : null;
               if (var19 != null && var19.clientside) {
                  this.serverModsContent.addComponent((FormModListSimpleElement)var2.nextY(new FormModListSimpleElement(var19.name + " " + var19.version, this.serverModsContent.getMinContentWidth(), var19.workshopID == -1L ? ModLoadLocation.MODS_FOLDER : ModLoadLocation.STEAM_WORKSHOP, true, Settings.UI.inactiveTextColor, Localization.translate("ui", "modclientside"))));
                  ++var17;
               } else if (var19 == null) {
                  if (var9 == null) {
                     this.useServer.setActive(var6);
                     if (!var6) {
                        this.useServer.setTooltip(Localization.translate("ui", "modmissingsome"));
                        this.serverModsList = null;
                     } else {
                        this.useServer.setTooltip((String)null);
                     }

                     this.myModsContent.setContentBox(new Rectangle(this.myModsContent.getWidth(), var3.next()));
                     this.serverModsContent.setContentBox(new Rectangle(this.serverModsContent.getWidth(), var2.next()));
                     this.makeCurrent(this.intro);
                     return;
                  }

                  this.myModsContent.addComponent((FormModListSimpleElement)var3.nextY(new FormModListSimpleElement(var9.name + " " + var9.version, this.myModsContent.getMinContentWidth(), var9.loadLocation, true, Settings.UI.activeTextColor)));
                  ++var15;
               } else {
                  boolean var11 = var4.stream().anyMatch((var1x) -> {
                     return var1x.id.equals(var19.id);
                  });
                  boolean var12 = var11 && var4.stream().anyMatch((var1x) -> {
                     return var1x.id.equals(var19.id) && var1x.version.equals(var19.version);
                  });
                  Color var13 = Settings.UI.activeTextColor;
                  StringTooltips var14 = null;
                  if (!var11) {
                     var13 = Settings.UI.errorTextColor;
                     var14 = new StringTooltips(Localization.translate("ui", "modmissing"));
                  } else if (!var12) {
                     var13 = Settings.UI.errorTextColor;
                     var14 = new StringTooltips(Localization.translate("ui", "modinversion"));
                  } else if (var9 == null || !var9.id.equals(var19.id) || !var9.version.equals(var19.version)) {
                     var13 = Settings.UI.warningTextColor;
                     var14 = new StringTooltips(Localization.translate("ui", "modinposition"));
                  }

                  this.serverModsContent.addComponent((FormModListSimpleElement)var2.nextY(new FormModListSimpleElement(var19.name + " " + var19.version, this.serverModsContent.getMinContentWidth(), var19.workshopID == -1L ? ModLoadLocation.MODS_FOLDER : ModLoadLocation.STEAM_WORKSHOP, true, var13, var14)));
                  ++var17;
                  if (var9 != null) {
                     this.myModsContent.addComponent((FormModListSimpleElement)var3.nextY(new FormModListSimpleElement(var9.name + " " + var9.version, this.myModsContent.getMinContentWidth(), var9.loadLocation, true, Settings.UI.activeTextColor)));
                     ++var15;
                  }
               }
            }
         }
      }
   }

   public final void onContinue(Runnable var1) {
      if (var1 != null) {
         this.continueEvents.add(var1);
      }

   }

   public final void applyContinue() {
      if (this.canContinue()) {
         this.continueEvents.forEach(Runnable::run);
         this.isContinued = true;
      }

   }

   public boolean isContinued() {
      return this.isContinued;
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.intro.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.review.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.useServerConfirmation.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }
}
