package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;

public class SpacerGameTooltip implements GameTooltips {
   public final int drawOrder;
   public final int height;

   public SpacerGameTooltip(int var1, int var2) {
      this.drawOrder = var1;
      this.height = var2;
   }

   public SpacerGameTooltip(int var1) {
      this(0, var1);
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return 0;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
   }

   public int getDrawOrder() {
      return this.drawOrder;
   }
}
