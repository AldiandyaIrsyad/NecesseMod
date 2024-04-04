package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.fileLanguage.FileLanguage;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormLanguageList extends FormSelectedList<LanguageElement> {
   protected FormEventsHandler<FormLanguageSelectEvent> languageSelect = new FormEventsHandler();

   public FormLanguageList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 20);
   }

   public void reset() {
      super.reset();
      ArrayList var1 = new ArrayList();
      ArrayList var2 = new ArrayList();
      Language[] var3 = Localization.getLanguages();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Language var6 = var3[var5];
         if (Localization.isOfficial(var6)) {
            var1.add(var6);
         } else {
            var2.add(var6);
         }
      }

      Iterator var7 = var1.iterator();

      while(true) {
         Language var8;
         do {
            if (!var7.hasNext()) {
               if (!var2.isEmpty()) {
                  this.elements.add(new LanguageElement(new StaticMessage("")));
                  this.elements.add(new LanguageElement(new LocalMessage("settingsui", "communitytranslations")));
               }

               var7 = var2.iterator();

               while(true) {
                  do {
                     if (!var7.hasNext()) {
                        return;
                     }

                     var8 = (Language)var7.next();
                  } while(var8.isDebugOnly() && !GlobalData.isDevMode());

                  this.elements.add(new LanguageElement(var8));
                  if (Localization.getCurrentLang() == var8) {
                     this.setSelected(this.elements.size() - 1);
                  }
               }
            }

            var8 = (Language)var7.next();
         } while(var8.isDebugOnly() && !GlobalData.isDevMode());

         this.elements.add(new LanguageElement(var8));
         if (Localization.getCurrentLang() == var8) {
            this.setSelected(this.elements.size() - 1);
         }
      }
   }

   public FormLanguageList onLanguageSelect(FormEventListener<FormLanguageSelectEvent> var1) {
      this.languageSelect.addListener(var1);
      return this;
   }

   public class LanguageElement extends FormSelectedElement<FormLanguageList> {
      public Language language;
      public GameMessage header;

      public LanguageElement(Language var2) {
         this.language = var2;
      }

      public LanguageElement(GameMessage var2) {
         this.header = var2;
      }

      public boolean isHeader() {
         return this.language == null;
      }

      void draw(FormLanguageList var1, TickManager var2, PlayerMob var3, int var4) {
         if (this.isHeader()) {
            String var5 = this.header.translate();
            FontOptions var6 = (new FontOptions(20)).color(Settings.UI.activeTextColor);
            int var7 = FontManager.bit.getWidthCeil(var5, var6);
            FontManager.bit.drawString((float)(var1.width / 2 - var7 / 2), 0.0F, var5, var6);
         } else {
            boolean var11 = this.isMouseOver(var1);
            Color var12 = var11 ? Settings.UI.highlightTextColor : Settings.UI.activeTextColor;
            String var13 = this.language.localDisplayName.equals(this.language.englishDisplayName) ? this.language.localDisplayName : this.language.localDisplayName + " (" + this.language.englishDisplayName + ")";
            if (this.isSelected()) {
               var13 = "> " + var13 + " <";
            }

            FontOptions var8 = (new FontOptions(16)).color(var12);
            int var9 = FontManager.bit.getWidthCeil(var13, var8);
            FontManager.bit.drawString((float)(var1.width / 2 - var9 / 2), 2.0F, var13, var8);
            if (var11) {
               ListGameTooltips var10 = new ListGameTooltips();
               this.language.addTooltips(var10);
               Screen.addTooltip(var10, TooltipLocation.FORM_FOCUS);
            }
         }

      }

      void onClick(FormLanguageList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (!this.isHeader()) {
            if (var3.getID() == -100) {
               if (!Screen.input().isKeyDown(340) && !Screen.input().isKeyDown(344)) {
                  super.onClick((FormSelectedList)var1, var2, var3, var4);
                  FormLanguageList.this.playTickSound();
                  var1.languageSelect.onEvent(new FormLanguageSelectEvent(var1, var2, this.language));
               } else if (this.language != Localization.English && this.language instanceof FileLanguage) {
                  FileLanguage var5 = (FileLanguage)this.language;
                  ((FileLanguage)Localization.English).fixAndPrintLanguageFile(var5, var5.getFilePath(), true);
                  UniqueScreenFloatText var6 = new UniqueScreenFloatText(Screen.mousePos().hudX, Screen.mousePos().hudY, "Fixed language file missing keys", (new FontOptions(16)).outline(), "languageFix");
                  var6.hoverTime = 4000;
                  Screen.hudManager.addElement(var6);
                  FormLanguageList.this.playTickSound();
               }
            }

         }
      }

      void onControllerEvent(FormLanguageList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (!this.isHeader()) {
            if (var3.getState() == ControllerInput.MENU_SELECT) {
               super.onControllerEvent((FormSelectedList)var1, var2, var3, var4, var5);
               FormLanguageList.this.playTickSound();
               var1.languageSelect.onEvent(new FormLanguageSelectEvent(var1, var2, this.language));
               var3.use();
            }

         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormSelectedList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormLanguageList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormSelectedList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormLanguageList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormLanguageList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormLanguageList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormLanguageList)var1, var2, var3, var4);
      }
   }

   public static class FormLanguageSelectEvent extends FormEvent<FormLanguageList> {
      public final int index;
      public final Language language;

      public FormLanguageSelectEvent(FormLanguageList var1, int var2, Language var3) {
         super(var1);
         this.index = var2;
         this.language = var3;
      }
   }
}
