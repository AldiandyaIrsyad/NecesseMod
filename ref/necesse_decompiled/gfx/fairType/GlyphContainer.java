package necesse.gfx.fairType;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.control.InputEvent;

public class GlyphContainer {
   public final FairGlyph glyph;
   public final int index;
   public final int line;
   public final float lineHeight;
   public final float x;
   public final float y;
   public final Supplier<Color> currentColor;

   public GlyphContainer(FairGlyph var1, int var2, int var3, float var4, float var5, float var6, Supplier<Color> var7) {
      this.glyph = var1;
      this.index = var2;
      this.line = var3;
      this.lineHeight = var4;
      this.x = var5;
      this.y = var6;
      this.currentColor = var7;
   }

   public void draw(int var1, int var2, Color var3) {
      this.glyph.draw((float)var1 + this.x, (float)var2 + this.y, this.currentColor == null ? var3 : (Color)this.currentColor.get());
   }

   public void handleInputEvent(int var1, int var2, InputEvent var3) {
      this.glyph.handleInputEvent((float)var1 + this.x, (float)var2 + this.y, var3);
   }
}
