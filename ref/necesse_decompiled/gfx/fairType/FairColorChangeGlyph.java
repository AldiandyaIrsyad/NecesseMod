package necesse.gfx.fairType;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.control.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.gfx.GameColor;

public class FairColorChangeGlyph implements FairGlyph {
   private final Supplier<Color> colorSupplier;
   private final String parseString;

   public FairColorChangeGlyph(String var1, Supplier<Color> var2) {
      this.parseString = var1;
      this.colorSupplier = var2;
   }

   public FairColorChangeGlyph(Color var1) {
      this((String)null, () -> {
         return var1;
      });
   }

   public FairColorChangeGlyph(GameColor var1) {
      this(var1.getColorCode(), var1.color);
   }

   public FloatDimension getDimensions() {
      return new FloatDimension();
   }

   public void updateDimensions() {
   }

   public void handleInputEvent(float var1, float var2, InputEvent var3) {
   }

   public void draw(float var1, float var2, Color var3) {
   }

   public FairGlyph getTextBoxCharacter() {
      return this;
   }

   public Supplier<Supplier<Color>> getDefaultColor() {
      return () -> {
         return this.colorSupplier;
      };
   }

   public String getParseString() {
      if (this.parseString != null) {
         return this.parseString;
      } else if (this.colorSupplier == null) {
         return '\u00a7' + String.valueOf(GameColor.NO_COLOR.codeChar);
      } else {
         Color var1 = (Color)this.colorSupplier.get();
         return '\u00a7' + String.format("#%02x%02x%02x", var1.getRed(), var1.getGreen(), var1.getBlue());
      }
   }
}
