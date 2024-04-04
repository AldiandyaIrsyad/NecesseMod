package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.Arrays;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormStringIndexEvent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormStringSelectList extends FormSelectedList<StringElement> {
   private FormEventsHandler<FormStringIndexEvent<FormStringSelectList>> onSelect;
   private FormEventsHandler<FormStringIndexEvent<FormStringSelectList>> beforeSelect;

   public FormStringSelectList(int var1, int var2, int var3, int var4, String[] var5) {
      super(var1, var2, var3, var4, 20);
      this.reset(var5);
      this.onSelect = new FormEventsHandler();
      this.beforeSelect = new FormEventsHandler();
   }

   public FormStringSelectList beforeSelect(FormEventListener<FormStringIndexEvent<FormStringSelectList>> var1) {
      this.beforeSelect.addListener(var1);
      return this;
   }

   public FormStringSelectList onSelect(FormEventListener<FormStringIndexEvent<FormStringSelectList>> var1) {
      this.onSelect.addListener(var1);
      return this;
   }

   public void reset(String[] var1) {
      super.reset();
      Arrays.stream(var1).forEach((var1x) -> {
         this.elements.add(new StringElement(var1x));
      });
   }

   public String getSelected() {
      StringElement var1 = (StringElement)this.getSelectedElement();
      return var1 == null ? null : var1.str;
   }

   public class StringElement extends FormSelectedElement<FormStringSelectList> {
      public String str;

      public StringElement(String var2) {
         this.str = var2;
      }

      void draw(FormStringSelectList var1, TickManager var2, PlayerMob var3, int var4) {
         Color var5 = Settings.UI.activeTextColor;
         if (this.isSelected()) {
            var5 = Settings.UI.highlightTextColor;
         }

         FontOptions var6 = (new FontOptions(16)).color(var5);
         String var7 = GameUtils.maxString(this.str, var6, var1.width - 10);
         int var8 = FontManager.bit.getWidthCeil(var7, var6);
         FontManager.bit.drawString((float)(var1.width / 2 - var8 / 2), 2.0F, var7, var6);
         if (this.isMouseOver(var1) && !this.str.equals(var7)) {
            Screen.addTooltip(new StringTooltips(this.str), TooltipLocation.FORM_FOCUS);
         }

      }

      void onClick(FormStringSelectList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100) {
            FormStringIndexEvent var5 = new FormStringIndexEvent(var1, this.str, var2);
            var1.beforeSelect.onEvent(var5);
            if (!var5.hasPreventedDefault()) {
               super.onClick((FormSelectedList)var1, var2, var3, var4);
               FormStringIndexEvent var6 = new FormStringIndexEvent(var1, this.str, var2);
               var1.onSelect.onEvent(var6);
               if (!var6.hasPreventedDefault()) {
                  FormStringSelectList.this.playTickSound();
               }
            }

         }
      }

      void onControllerEvent(FormStringSelectList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            FormStringIndexEvent var6 = new FormStringIndexEvent(var1, this.str, var2);
            var1.beforeSelect.onEvent(var6);
            if (!var6.hasPreventedDefault()) {
               super.onControllerEvent((FormSelectedList)var1, var2, var3, var4, var5);
               FormStringIndexEvent var7 = new FormStringIndexEvent(var1, this.str, var2);
               var1.onSelect.onEvent(var7);
               if (!var7.hasPreventedDefault()) {
                  FormStringSelectList.this.playTickSound();
               }
            }

            var3.use();
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormSelectedList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormStringSelectList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormSelectedList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormStringSelectList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormStringSelectList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormStringSelectList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormStringSelectList)var1, var2, var3, var4);
      }
   }
}
