package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;

public class FairTypeTooltip implements GameTooltips {
   public FairTypeDrawOptions drawOptions;
   public int xOffset;

   public FairTypeTooltip(FairTypeDrawOptions var1, int var2) {
      Objects.requireNonNull(var1);
      this.drawOptions = var1;
      this.xOffset = var2;
   }

   public FairTypeTooltip(FairTypeDrawOptions var1) {
      this((FairTypeDrawOptions)var1, 0);
   }

   public FairTypeTooltip(FairType var1, int var2) {
      this(var1.getDrawOptions(FairType.TextAlign.LEFT, 400, false, true), var2);
   }

   public FairTypeTooltip(FairType var1) {
      this((FairType)var1, 0);
   }

   public int getHeight() {
      return this.drawOptions.getBoundingBox().height;
   }

   public int getWidth() {
      return this.drawOptions.getBoundingBox().width + this.xOffset;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      Rectangle var4 = this.drawOptions.getBoundingBox();
      this.drawOptions.draw(var1 - var4.x + this.xOffset, var2 - var4.y, (Color)var3.get());
   }

   public int getDrawOrder() {
      return 0;
   }
}
