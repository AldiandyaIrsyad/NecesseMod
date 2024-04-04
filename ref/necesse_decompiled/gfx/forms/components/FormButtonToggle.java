package necesse.gfx.forms.components;

import java.util.Objects;
import necesse.engine.MouseDraggingElement;
import necesse.engine.Screen;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.ui.ButtonState;

public abstract class FormButtonToggle extends FormButton implements ToggleButton<FormButtonToggle> {
   private FormEventsHandler<FormInputEvent<FormButtonToggle>> onToggle;
   private boolean toggled;

   public FormButtonToggle(boolean var1) {
      this.handleClicksIfNoEventHandlers = true;
      this.onToggle = new FormEventsHandler();
      this.toggled = var1;
   }

   public FormButtonToggle() {
      this(false);
      this.onToggle = new FormEventsHandler();
   }

   public FormButtonToggle onToggled(FormEventListener<FormInputEvent<FormButtonToggle>> var1) {
      this.onToggle.addListener(var1);
      return this;
   }

   public void setupDragToOtherButtons(Object var1, boolean var2) {
      this.onDragStarted((var3) -> {
         final int var4 = var3.draggingStartedEvent.isUsed() ? var3.draggingStartedEvent.getLastID() : var3.draggingStartedEvent.getID();
         Screen.setMouseDraggingElement(new FormButtonToggleDraggingElement(this, var1) {
            public boolean isKeyDown(Input var1) {
               return var1.isKeyDown(var4);
            }
         });
         this.setToggled(!this.isToggled());
         if (var2) {
            this.onToggle.onEvent(new FormInputEvent(this, var3.event));
            this.playTickSound();
         }

      });
      this.onChangedHover((var3) -> {
         if (this.isHovering() && this.isActive() && !this.isOnCooldown()) {
            MouseDraggingElement var4 = Screen.getMouseDraggingElement();
            if (var4 instanceof FormButtonToggleDraggingElement) {
               FormButtonToggleDraggingElement var5 = (FormButtonToggleDraggingElement)var4;
               if (var5.component != this && this.isToggled() != var5.component.isToggled() && Objects.equals(var5.sameObject, var1)) {
                  this.setToggled(var5.component.isToggled());
                  if (var2) {
                     this.onToggle.onEvent(new FormInputEvent(this, var3.event));
                  }

                  this.playTickSound();
               }
            }
         }

      });
   }

   public void setupDragToOtherButtons(Object var1) {
      this.setupDragToOtherButtons(var1, true);
   }

   protected boolean isDraggingThis() {
      MouseDraggingElement var1 = Screen.getMouseDraggingElement();
      if (var1 instanceof FormButtonToggleDraggingElement) {
         FormButtonToggleDraggingElement var2 = (FormButtonToggleDraggingElement)var1;
         return var2.component == this;
      } else {
         return false;
      }
   }

   public ButtonState getButtonState() {
      if (this.isActive() && !this.isOnCooldown()) {
         return !this.isToggled() && !this.isHovering() ? ButtonState.ACTIVE : ButtonState.HIGHLIGHTED;
      } else {
         return ButtonState.INACTIVE;
      }
   }

   protected void pressed(InputEvent var1) {
      if (!this.isDraggingThis()) {
         this.toggled = !this.toggled;
         super.pressed(var1);
         this.onToggle.onEvent(new FormInputEvent(this, var1));
      }
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

   public void drawDraggingElement(int var1, int var2) {
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FormComponent onToggled(FormEventListener var1) {
      return this.onToggled(var1);
   }

   protected static class FormButtonToggleDraggingElement implements MouseDraggingElement {
      public final FormButtonToggle component;
      public final Object sameObject;

      public FormButtonToggleDraggingElement(FormButtonToggle var1, Object var2) {
         this.component = var1;
         this.sameObject = var2;
      }

      public boolean draw(int var1, int var2) {
         this.component.drawDraggingElement(var1, var2);
         return true;
      }
   }
}
