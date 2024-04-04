package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormCursorPreview extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private Color color;
   private int size;

   public FormCursorPreview(int var1, int var2, Color var3, int var4) {
      this.position = new FormFixedPosition(var1, var2);
      this.setColor(var3);
      this.setSize(var4);
   }

   public void setColor(Color var1) {
      this.color = var1;
   }

   public void setSize(int var1) {
      this.size = var1;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      float var4 = Screen.getCursorSizeZoom(this.size);
      int var5 = (int)(32.0F * var4);
      GameResources.cursors.initDraw().sprite(0, 0, 32).size(var5, var5).color(this.color).draw(this.getX(), this.getY());
   }

   public List<Rectangle> getHitboxes() {
      float var1 = Screen.getCursorSizeZoom(this.size);
      int var2 = (int)(32.0F * var1);
      return singleBox(new Rectangle(this.getX(), this.getY(), var2, var2));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
