package necesse.engine.screenHudManager;

import java.awt.Rectangle;

public abstract class ScreenFloatText extends ScreenHudElement {
   public ScreenFloatText() {
   }

   public abstract int getX();

   public abstract int getY();

   public abstract int getWidth();

   public abstract int getHeight();

   public Rectangle getCollision() {
      return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
   }

   public boolean collidesWith(ScreenFloatText var1) {
      return this.getCollision().intersects(var1.getCollision());
   }
}
