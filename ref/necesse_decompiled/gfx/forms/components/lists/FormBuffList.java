package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormBuffList extends FormGeneralList<BuffElement> {
   private String filter;
   private ArrayList<BuffElement> allElements;

   public FormBuffList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 20);
      this.setFilter("");
   }

   public void reset() {
      super.reset();
      this.allElements = new ArrayList();
   }

   public void populateIfNotAlready() {
      if (this.allElements.isEmpty()) {
         Iterator var1 = BuffRegistry.getBuffs().iterator();

         while(var1.hasNext()) {
            Buff var2 = (Buff)var1.next();
            if (var2 != null && !var2.isPassive()) {
               this.allElements.add(new BuffElement(var2));
            }
         }

         this.setFilter(this.filter);
         this.resetScroll();
      }

   }

   public void setFilter(String var1) {
      if (var1 != null) {
         this.filter = var1.toLowerCase();
         this.elements = new ArrayList();
         Stream var10000 = this.allElements.stream().filter((var1x) -> {
            return var1x.buff.getStringID().toLowerCase().contains(var1) || var1x.buff.getDisplayName().toLowerCase().contains(var1);
         });
         List var10001 = this.elements;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
         this.limitMaxScroll();
      }
   }

   public abstract void onClicked(Buff var1);

   public class BuffElement extends FormListElement<FormBuffList> {
      public Buff buff;

      public BuffElement(Buff var2) {
         this.buff = var2;
      }

      void draw(FormBuffList var1, TickManager var2, PlayerMob var3, int var4) {
         Color var5 = this.isHovering() ? Settings.UI.highlightTextColor : Settings.UI.activeTextColor;
         String var6 = this.buff.getID() + ":" + this.buff.getDisplayName();
         FontOptions var7 = (new FontOptions(16)).color(var5);
         String var8 = GameUtils.maxString(var6, var7, var1.width - 20);
         FontManager.bit.drawString(10.0F, 2.0F, var8, var7);
         if (this.isMouseOver(var1) && !var8.equals(var6)) {
            Screen.addTooltip(new StringTooltips(var6), TooltipLocation.FORM_FOCUS);
         }

      }

      void onClick(FormBuffList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100) {
            FormBuffList.this.playTickSound();
            FormBuffList.this.onClicked(this.buff);
         }
      }

      void onControllerEvent(FormBuffList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            FormBuffList.this.playTickSound();
            FormBuffList.this.onClicked(this.buff);
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormBuffList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormBuffList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormBuffList)var1, var2, var3, var4);
      }
   }
}
