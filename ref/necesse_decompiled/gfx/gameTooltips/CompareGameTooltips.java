package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;

public class CompareGameTooltips implements GameTooltips {
   private GameTooltips left;
   private GameTooltips right;
   private int margin;
   private boolean snapToBottom;

   public CompareGameTooltips(GameTooltips var1, GameTooltips var2, int var3, boolean var4) {
      this.left = var1;
      this.right = var2;
      this.margin = var3;
      this.snapToBottom = var4;
   }

   public int getHeight() {
      return Math.max(this.left.getHeight(), this.right.getHeight());
   }

   public int getWidth() {
      return this.left.getWidth() + this.right.getWidth() + this.margin;
   }

   public int getDrawXOffset() {
      return -this.left.getWidth() - this.margin;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      if (this.snapToBottom) {
         int var4 = this.left.getHeight();
         int var5 = this.right.getHeight();
         int var6 = Math.max(var4, var5);
         this.left.draw(var1, var2 + var6 - var4, var3);
         this.right.draw(var1 + this.left.getWidth() + this.margin, var2 + var6 - var5, var3);
      } else {
         this.left.draw(var1, var2, var3);
         this.right.draw(var1 + this.left.getWidth() + this.margin, var2, var3);
      }

   }

   public int getDrawOrder() {
      return 0;
   }
}
