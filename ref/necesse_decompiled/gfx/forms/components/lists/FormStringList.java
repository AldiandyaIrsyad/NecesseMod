package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormStringList extends FormGeneralList<StringElement> {
   public FormStringList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 20);
   }

   public void addString(String var1, Color var2, StringTooltips var3) {
      this.elements.add(new StringElement(var1, var2, var3));
   }

   public void addString(String var1, Color var2, String... var3) {
      this.addString(var1, var2, new StringTooltips(var3));
   }

   public void addString(String var1, Color var2) {
      this.addString(var1, var2, (StringTooltips)null);
   }

   public void addString(String var1) {
      this.addString(var1, Settings.UI.activeTextColor);
   }

   public void addString(String var1, StringTooltips var2) {
      this.addString(var1, Settings.UI.activeTextColor, var2);
   }

   public void addString(String var1, String... var2) {
      this.addString(var1, new StringTooltips(var2));
   }

   public class StringElement extends FormListElement<FormStringList> {
      private final String str;
      private final FontOptions fontOptions;
      private final StringTooltips tooltips;

      public StringElement(String var2, Color var3, StringTooltips var4) {
         Objects.requireNonNull(var2);
         Objects.requireNonNull(var3);
         this.str = var2;
         this.fontOptions = (new FontOptions(16)).color(var3);
         this.tooltips = var4;
      }

      void draw(FormStringList var1, TickManager var2, PlayerMob var3, int var4) {
         int var5 = FontManager.bit.getWidthCeil(this.str, this.fontOptions);
         StringTooltips var6 = new StringTooltips();
         if (this.isMouseOver(var1)) {
            if (var1.width < var5) {
               var6.add(this.str);
            }

            if (this.tooltips != null) {
               var6.addAll(this.tooltips);
            }
         }

         if (var6.getSize() != 0) {
            Screen.addTooltip(var6, TooltipLocation.FORM_FOCUS);
         }

         FontManager.bit.drawString(0.0F, 2.0F, this.str, this.fontOptions);
      }

      void onClick(FormStringList var1, int var2, InputEvent var3, PlayerMob var4) {
      }

      void onControllerEvent(FormStringList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormStringList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormStringList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormStringList)var1, var2, var3, var4);
      }
   }
}
