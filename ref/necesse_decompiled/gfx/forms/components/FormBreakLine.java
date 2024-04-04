package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormBreakLine extends FormComponent implements FormPositionContainer {
   public static int ALIGN_BEGINNING = -1;
   public static int ALIGN_MID = 0;
   public static int ALIGN_END = 1;
   private FormPosition position;
   public int length;
   public int align;
   public boolean horizontal;
   public Color color;

   public FormBreakLine(int var1, int var2, int var3, int var4, boolean var5) {
      this.color = Settings.UI.activeTextColor;
      this.align = var1;
      this.position = new FormFixedPosition(var2, var3);
      this.length = var4;
      this.horizontal = var5;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      int var4 = this.length / 2;
      if (this.align == ALIGN_BEGINNING) {
         var4 = 0;
      } else if (this.align == ALIGN_END) {
         var4 = this.length;
      }

      if (this.horizontal) {
         Screen.initQuadDraw(this.length, 2).color(this.color).draw(this.getX() - var4, this.getY());
      } else {
         Screen.initQuadDraw(2, this.length).color(this.color).draw(this.getX(), this.getY() - var4);
      }

   }

   public List<Rectangle> getHitboxes() {
      int var1 = this.length / 2;
      if (this.align == ALIGN_BEGINNING) {
         var1 = 0;
      } else if (this.align == ALIGN_END) {
         var1 = this.length;
      }

      return this.horizontal ? singleBox(new Rectangle(this.getX() - var1, this.getY(), this.length, 2)) : singleBox(new Rectangle(this.getX(), this.getY() - var1, 2, this.length));
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
