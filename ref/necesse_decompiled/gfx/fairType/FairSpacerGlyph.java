package necesse.gfx.fairType;

import java.awt.Color;
import necesse.engine.control.InputEvent;
import necesse.engine.util.FloatDimension;

public class FairSpacerGlyph implements FairGlyph {
   public final float width;
   public final float height;

   public FairSpacerGlyph(float var1, float var2) {
      this.width = var1;
      this.height = var2;
   }

   public FloatDimension getDimensions() {
      return new FloatDimension(this.width, this.height);
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
}
