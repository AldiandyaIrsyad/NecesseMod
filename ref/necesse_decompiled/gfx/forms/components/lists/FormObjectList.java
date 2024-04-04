package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.gameObject.GameObject;

public abstract class FormObjectList extends FormGeneralList<ObjectElement> {
   private String filter;
   private ArrayList<ObjectElement> allElements;

   public FormObjectList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 20);
      this.setFilter("");
   }

   public void reset() {
      super.reset();
      this.allElements = new ArrayList();
      this.setFilter(this.filter);
   }

   public void populateIfNotAlready() {
      if (this.allElements.isEmpty()) {
         Iterator var1 = ObjectRegistry.getObjects().iterator();

         while(var1.hasNext()) {
            GameObject var2 = (GameObject)var1.next();
            this.allElements.add(new ObjectElement(var2));
         }

         this.setFilter(this.filter);
         this.resetScroll();
      }

   }

   public void setFilter(String var1) {
      if (var1 != null) {
         this.filter = var1;
         this.elements = new ArrayList();
         Iterator var2 = this.allElements.iterator();

         while(true) {
            ObjectElement var3;
            do {
               if (!var2.hasNext()) {
                  this.limitMaxScroll();
                  return;
               }

               var3 = (ObjectElement)var2.next();
            } while(!var3.object.getDisplayName().toLowerCase().contains(var1) && !var3.object.getStringID().toLowerCase().contains(var1));

            this.elements.add(var3);
         }
      }
   }

   public abstract void onClicked(GameObject var1);

   public class ObjectElement extends FormListElement<FormObjectList> {
      public GameObject object;

      public ObjectElement(GameObject var2) {
         this.object = var2;
      }

      void draw(FormObjectList var1, TickManager var2, PlayerMob var3, int var4) {
         Color var5 = this.isHovering() ? Settings.UI.highlightTextColor : Settings.UI.activeTextColor;
         String var6 = this.object.getID() + ":" + this.object.getDisplayName();
         FontOptions var7 = (new FontOptions(16)).color(var5);
         String var8 = GameUtils.maxString(var6, var7, var1.width - 20);
         FontManager.bit.drawString(10.0F, 2.0F, var8, var7);
         if (this.isMouseOver(var1) && !var8.equals(var6)) {
            Screen.addTooltip(new StringTooltips(var6), TooltipLocation.FORM_FOCUS);
         }

      }

      void onClick(FormObjectList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100) {
            FormObjectList.this.playTickSound();
            FormObjectList.this.onClicked(this.object);
         }
      }

      void onControllerEvent(FormObjectList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            FormObjectList.this.playTickSound();
            FormObjectList.this.onClicked(this.object);
            var3.use();
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormObjectList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormObjectList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormObjectList)var1, var2, var3, var4);
      }
   }
}
