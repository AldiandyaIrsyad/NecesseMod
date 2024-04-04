package necesse.gfx.forms.events;

import necesse.engine.control.InputEvent;
import necesse.gfx.forms.components.FormComponent;

public class FormDraggingEvent<T extends FormComponent> extends FormInputEvent<T> {
   public final InputEvent draggingStartedEvent;

   public FormDraggingEvent(T var1, InputEvent var2, InputEvent var3) {
      super(var1, var2);
      this.draggingStartedEvent = var3;
   }
}
