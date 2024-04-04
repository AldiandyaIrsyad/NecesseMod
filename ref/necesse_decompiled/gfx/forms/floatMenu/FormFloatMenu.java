package necesse.gfx.forms.floatMenu;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;

public class FormFloatMenu extends FloatMenu {
   private Form form;

   public FormFloatMenu(FormComponent var1, Form var2) {
      super(var1);
      this.setForm(var2);
   }

   public FormFloatMenu(FormComponent var1) {
      this(var1, (Form)null);
   }

   public void setForm(Form var1, int var2, int var3) {
      if (var1 == null) {
         var1 = new Form(50, 50);
      }

      if (this.form != var1) {
         if (this.form != null) {
            this.form.dispose();
         }

         this.form = var1;
         var1.setPosition(new FormFloatPosition(var2, var3));
         var1.setManager(this.parent.getManager());
      }

   }

   public void setForm(Form var1) {
      this.setForm(var1, 0, 0);
   }

   public int getDrawX() {
      return Math.min(Screen.getHudWidth() - this.form.getWidth() - 4, super.getDrawX());
   }

   public int getDrawY() {
      return Math.min(Screen.getHudHeight() - this.form.getHeight() - 4, super.getDrawY());
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.wasMouseClickEvent() && var1.state) {
         this.remove();
      }

      this.form.handleInputEvent(var1, var2, var3);
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
      this.form.handleControllerEvent(var1, var2, var3);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      this.form.addNextControllerFocus(var1, 0, 0, var4, var5, var6);
   }

   public void draw(TickManager var1, PlayerMob var2) {
      this.form.draw(var1, var2, (Rectangle)null);
   }

   public boolean isMouseOver(InputEvent var1) {
      return this.form.isMouseOver(var1);
   }

   public void dispose() {
      super.dispose();
      this.form.dispose();
   }

   private class FormFloatPosition extends FormFixedPosition {
      public FormFloatPosition(int var2, int var3) {
         super(var2, var3);
      }

      public int getX() {
         return FormFloatMenu.this.getDrawX() + super.getX();
      }

      public int getY() {
         return FormFloatMenu.this.getDrawY() + super.getY();
      }
   }
}
