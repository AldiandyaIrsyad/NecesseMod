package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import necesse.engine.control.InputEvent;
import necesse.engine.util.FloatDimension;

public abstract class FairButtonGlyph implements FairGlyph {
   public final int width;
   public final int height;
   private boolean isHovering;

   public FairButtonGlyph(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public FloatDimension getDimensions() {
      return new FloatDimension((float)this.width, (float)this.height);
   }

   public void updateDimensions() {
   }

   public void draw(float var1, float var2, Color var3) {
   }

   public FairGlyph getTextBoxCharacter() {
      return this;
   }

   public void handleInputEvent(float var1, float var2, InputEvent var3) {
      if (var3.isMouseMoveEvent()) {
         Dimension var4 = this.getDimensions().toInt();
         this.isHovering = (new Rectangle((int)var1 + 2, (int)var2 - var4.height - 2, var4.width, var4.height)).contains(var3.pos.hudX, var3.pos.hudY);
      }

      if (this.isHovering) {
         this.handleEvent(var1, var2, var3);
      }

   }

   public abstract void handleEvent(float var1, float var2, InputEvent var3);

   public boolean isHovering() {
      return this.isHovering;
   }
}
