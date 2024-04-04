package necesse.gfx.forms.floatMenu;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;

public class ComponentFloatMenu extends FloatMenu {
   private FormComponent component;

   public ComponentFloatMenu(FormComponent var1, FormComponent var2) {
      super(var1);
      this.setComponent(var2);
   }

   public ComponentFloatMenu(FormComponent var1) {
      this(var1, (FormComponent)null);
   }

   public void setComponent(FormComponent var1) {
      if (this.component != var1) {
         if (this.component != null) {
            this.component.dispose();
         }

         this.component = var1;
         var1.setManager(this.parent.getManager());
      }

   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.wasMouseClickEvent() && var1.state) {
         this.remove();
      }

      this.component.handleInputEvent(var1, var2, var3);
      if (var1.isMouseClickEvent()) {
         if (this.isMouseOver(var1)) {
            var1.use();
         } else if (var1.state) {
            var1.use();
            this.remove();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      this.component.handleControllerEvent(var1, var2, var3);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      this.component.addNextControllerFocus(var1, 0, 0, var4, var5, var6);
   }

   public void draw(TickManager var1, PlayerMob var2) {
      this.component.draw(var1, var2, (Rectangle)null);
   }

   public boolean isMouseOver(InputEvent var1) {
      return this.component.isMouseOver(var1);
   }

   public void dispose() {
      super.dispose();
      this.component.dispose();
   }

   private class FormFloatPosition extends FormFixedPosition {
      public FormFloatPosition(int var2, int var3) {
         super(var2, var3);
      }

      public int getX() {
         return ComponentFloatMenu.this.getDrawX() + super.getX();
      }

      public int getY() {
         return ComponentFloatMenu.this.getDrawY() + super.getY();
      }
   }
}
