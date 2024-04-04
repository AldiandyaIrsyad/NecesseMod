package necesse.engine.control;

import necesse.engine.Screen;

public class MouseWheelBuffer {
   public boolean convertYToXOnShift;
   public double xBuffer;
   public double yBuffer;

   public MouseWheelBuffer(boolean var1) {
      this.convertYToXOnShift = var1;
   }

   public void add(InputEvent var1, double var2) {
      double var4 = var1.getMouseWheelX();
      double var6 = var1.getMouseWheelY();
      if (this.convertYToXOnShift && Screen.isKeyDown(340)) {
         var4 = var6;
         var6 = 0.0;
      }

      this.xBuffer += var4 * var2;
      this.yBuffer += var6 * var2;
   }

   public void add(InputEvent var1) {
      this.add(var1, 1.0);
   }

   public int useAllScrollX() {
      int var1 = (int)this.xBuffer;
      this.xBuffer -= (double)var1;
      return var1;
   }

   public int useAllScrollY() {
      int var1 = (int)this.yBuffer;
      this.yBuffer -= (double)var1;
      return var1;
   }

   public void useScrollX(ScrollUser var1) {
      int var2 = this.useAllScrollX();
      boolean var3 = var2 > 0;
      int var4 = Math.abs(var2);

      for(int var5 = 0; var5 < var4; ++var5) {
         var1.use(var3);
      }

   }

   public void useScrollY(ScrollUser var1) {
      int var2 = this.useAllScrollY();
      boolean var3 = var2 > 0;
      int var4 = Math.abs(var2);

      for(int var5 = 0; var5 < var4; ++var5) {
         var1.use(var3);
      }

   }

   public interface ScrollUser {
      void use(boolean var1);
   }
}
