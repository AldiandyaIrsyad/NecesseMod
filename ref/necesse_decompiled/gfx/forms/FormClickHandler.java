package necesse.gfx.forms;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.gfx.forms.components.FormComponent;

public class FormClickHandler {
   public Consumer<InputEvent> onClick;
   public Function<InputEvent, Boolean> eventAccept;
   public Function<InputEvent, Boolean> eventIDAccept;
   private boolean isDown;
   private InputEvent downEvent;

   public FormClickHandler(Function<InputEvent, Boolean> var1, Function<InputEvent, Boolean> var2, Consumer<InputEvent> var3) {
      this.eventAccept = var1;
      this.eventIDAccept = var2;
      this.onClick = var3;
      this.isDown = false;
   }

   public FormClickHandler(Function<InputEvent, Boolean> var1, int var2, Consumer<InputEvent> var3) {
      this(var1, (var1x) -> {
         return var1x.getID() == var2;
      }, var3);
   }

   public FormClickHandler(FormComponent var1, int var2, Consumer<InputEvent> var3) {
      Objects.requireNonNull(var1);
      this(var1::isMouseOver, (var1x) -> {
         return var1x.getID() == var2;
      }, var3);
   }

   public void handleEvent(InputEvent var1) {
      if ((Boolean)this.eventIDAccept.apply(var1)) {
         boolean var2 = (Boolean)this.eventAccept.apply(var1);
         if (var1.state && var2) {
            this.isDown = true;
            this.downEvent = var1;
            var1.use();
         } else {
            if (this.isDown && var2) {
               this.forceClick(var1);
            }

            this.isDown = false;
            this.downEvent = null;
         }
      }

   }

   public void forceHandleEvent(InputEvent var1) {
      if (var1.state) {
         this.isDown = true;
         this.downEvent = var1;
         var1.use();
      } else {
         if (this.isDown) {
            this.forceClick(var1);
         }

         this.isDown = false;
         this.downEvent = null;
      }

   }

   public void forceClick(InputEvent var1) {
      this.onClick.accept(var1);
      var1.use();
   }

   public void reset() {
      this.isDown = false;
      this.downEvent = null;
   }

   public boolean isDown() {
      return this.isDown;
   }

   public InputEvent getDownEvent() {
      return this.downEvent;
   }

   public InputPosition getDownPosition() {
      return this.downEvent == null ? null : this.downEvent.pos;
   }
}
