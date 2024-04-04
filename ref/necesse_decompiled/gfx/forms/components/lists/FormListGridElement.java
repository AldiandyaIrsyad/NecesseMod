package necesse.gfx.forms.components.lists;

import java.awt.Rectangle;
import necesse.engine.control.InputEvent;

public abstract class FormListGridElement<E extends FormGeneralGridList> extends FormListElement<E> {
   public FormListGridElement() {
   }

   public boolean isMouseOver(E var1) {
      if (var1.isControllerFocus(this)) {
         return true;
      } else {
         InputEvent var2 = this.getMoveEvent();
         return var2 == null ? false : (new Rectangle(0, 0, var1.elementWidth, var1.elementHeight)).contains(var2.pos.hudX, var2.pos.hudY);
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public boolean isMouseOver(FormGeneralList var1) {
      return this.isMouseOver((FormGeneralGridList)var1);
   }
}
