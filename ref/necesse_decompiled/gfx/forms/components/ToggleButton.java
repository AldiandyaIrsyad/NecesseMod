package necesse.gfx.forms.components;

import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;

public interface ToggleButton<T extends FormComponent> {
   boolean isToggled();

   void setToggled(boolean var1);

   void reset();

   T onToggled(FormEventListener<FormInputEvent<T>> var1);
}
