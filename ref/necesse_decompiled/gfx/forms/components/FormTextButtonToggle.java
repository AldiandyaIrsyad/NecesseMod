package necesse.gfx.forms.components;

import necesse.engine.control.InputEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public class FormTextButtonToggle extends FormTextButton implements ToggleButton<FormTextButtonToggle> {
   private FormEventsHandler<FormInputEvent<FormTextButtonToggle>> onToggle;
   protected boolean toggled;

   public FormTextButtonToggle(String var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.handleClicksIfNoEventHandlers = true;
      this.onToggle = new FormEventsHandler();
      this.toggled = false;
   }

   public FormTextButtonToggle(String var1, int var2, int var3, int var4, FormInputSize var5, ButtonColor var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.handleClicksIfNoEventHandlers = true;
      this.onToggle = new FormEventsHandler();
      this.toggled = false;
   }

   public FormTextButtonToggle onToggled(FormEventListener<FormInputEvent<FormTextButtonToggle>> var1) {
      this.onToggle.addListener(var1);
      return this;
   }

   public ButtonState getButtonState() {
      if (!this.isActive()) {
         return ButtonState.INACTIVE;
      } else {
         return !this.isToggled() && !this.isHovering() ? ButtonState.ACTIVE : ButtonState.HIGHLIGHTED;
      }
   }

   protected void pressed(InputEvent var1) {
      this.toggled = !this.toggled;
      super.pressed(var1);
      this.onToggle.onEvent(new FormInputEvent(this, var1));
   }

   public boolean isToggled() {
      return this.toggled;
   }

   public void setToggled(boolean var1) {
      this.toggled = var1;
   }

   public void reset() {
      this.toggled = false;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FormComponent onToggled(FormEventListener var1) {
      return this.onToggled(var1);
   }
}
