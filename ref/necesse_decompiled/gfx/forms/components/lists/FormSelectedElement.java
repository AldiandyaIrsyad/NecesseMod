package necesse.gfx.forms.components.lists;

import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;

public abstract class FormSelectedElement<E extends FormSelectedList> extends FormListElement<E> {
   private boolean selected;

   public FormSelectedElement() {
   }

   public final boolean isSelected() {
      return this.selected;
   }

   public final void clearSelected() {
      this.selected = false;
   }

   final void makeSelected(FormSelectedList var1) {
      if (var1 != null) {
         this.selected = true;
      }
   }

   protected boolean onlyAcceptLeftClick() {
      return true;
   }

   void onClick(E var1, int var2, InputEvent var3, PlayerMob var4) {
      if (!this.onlyAcceptLeftClick() || var3.getID() == -100) {
         var1.setSelected(var2);
      }
   }

   void onControllerEvent(E var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
      if (!this.onlyAcceptLeftClick() || var3.getState() == ControllerInput.MENU_SELECT) {
         var1.setSelected(var2);
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
      this.onControllerEvent((FormSelectedList)var1, var2, var3, var4, var5);
   }

   // $FF: synthetic method
   // $FF: bridge method
   void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
      this.onClick((FormSelectedList)var1, var2, var3, var4);
   }
}
