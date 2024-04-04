package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormFairTypeDraw extends FormComponent implements FormPositionContainer {
   protected FormPosition position;
   public FairTypeDrawOptions drawOptions;
   public Supplier<Color> colorSupplier;

   public FormFairTypeDraw(int var1, int var2) {
      this.setPosition(new FormFixedPosition(var1, var2));
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (this.drawOptions != null) {
         this.drawOptions.handleInputEvent(this.getX(), this.getY(), var1);
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.drawOptions != null) {
         Color var4 = this.colorSupplier == null ? Settings.UI.textBoxTextColor : (Color)this.colorSupplier.get();
         this.drawOptions.draw(this.getX(), this.getY(), var4);
      }

   }

   public List<Rectangle> getHitboxes() {
      return this.drawOptions != null ? singleBox(this.drawOptions.getBoundingBox(this.getX(), this.getY())) : singleBox(new Rectangle(this.getX(), this.getY()));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
