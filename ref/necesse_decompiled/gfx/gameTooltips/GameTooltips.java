package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;

public interface GameTooltips {
   int getHeight();

   int getWidth();

   default int getDrawXOffset() {
      return 0;
   }

   void draw(int var1, int var2, Supplier<Color> var3);

   int getDrawOrder();
}
