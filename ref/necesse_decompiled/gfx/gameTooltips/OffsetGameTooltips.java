package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;

public class OffsetGameTooltips implements GameTooltips {
   private GameTooltips tooltips;
   private int offset;

   public OffsetGameTooltips(GameTooltips var1, int var2) {
      this.tooltips = var1;
      this.offset = var2;
   }

   public int getHeight() {
      return this.tooltips.getHeight();
   }

   public int getWidth() {
      return this.tooltips.getWidth();
   }

   public int getDrawXOffset() {
      return this.offset + this.tooltips.getDrawXOffset();
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      this.tooltips.draw(var1, var2, var3);
   }

   public int getDrawOrder() {
      return this.tooltips.getDrawOrder();
   }
}
