package necesse.gfx.forms.components.lists;

import java.awt.Rectangle;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocusHandler;

public abstract class FormListElement<E extends FormGeneralList> implements ControllerFocusHandler {
   private InputEvent moveEvent;

   public FormListElement() {
   }

   public void setMoveEvent(InputEvent var1) {
      this.moveEvent = var1;
   }

   public InputEvent getMoveEvent() {
      return this.moveEvent;
   }

   public boolean isHovering() {
      return this.moveEvent != null;
   }

   abstract void draw(E var1, TickManager var2, PlayerMob var3, int var4);

   abstract void onClick(E var1, int var2, InputEvent var3, PlayerMob var4);

   public final void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      throw new UnsupportedOperationException("Use onControllerEvent method instead");
   }

   abstract void onControllerEvent(E var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5);

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      return false;
   }

   public boolean isMouseOver(E var1) {
      if (var1.isControllerFocus(this)) {
         return true;
      } else {
         InputEvent var2 = this.getMoveEvent();
         return var2 == null ? false : (new Rectangle(0, 0, var1.width, var1.elementHeight)).contains(var2.pos.hudX, var2.pos.hudY);
      }
   }
}
