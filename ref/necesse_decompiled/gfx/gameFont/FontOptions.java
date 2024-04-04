package necesse.gfx.gameFont;

import java.awt.Color;

public class FontOptions extends FontBasicOptions {
   private boolean outline;

   public FontOptions(int var1) {
      super(var1);
   }

   public FontOptions(FontOptions var1) {
      super(var1);
      this.outline = var1.outline;
   }

   public FontOptions copy() {
      return new FontOptions(this);
   }

   public FontOptions size(int var1) {
      super.size(var1);
      return this;
   }

   public FontOptions forcePixelFont() {
      super.forcePixelFont();
      return this;
   }

   public FontOptions forceNonPixelFont() {
      super.forceNonPixelFont();
      return this;
   }

   public FontOptions color(float[] var1) {
      super.color(var1);
      return this;
   }

   public FontOptions colorf(float var1, float var2, float var3, float var4) {
      super.colorf(var1, var2, var3, var4);
      return this;
   }

   public FontOptions colorf(float var1, float var2, float var3) {
      super.colorf(var1, var2, var3);
      return this;
   }

   public FontOptions color(Color var1) {
      super.color(var1);
      return this;
   }

   public FontOptions color(int var1, int var2, int var3, int var4) {
      super.color(var1, var2, var3, var4);
      return this;
   }

   public FontOptions color(int var1, int var2, int var3) {
      super.color(var1, var2, var3);
      return this;
   }

   public FontOptions alphaf(float var1) {
      super.alphaf(var1);
      return this;
   }

   public FontOptions alpha(int var1) {
      super.alpha(var1);
      return this;
   }

   public FontOptions defaultColor(float[] var1) {
      super.defaultColor(var1);
      return this;
   }

   public FontOptions defaultColorf(float var1, float var2, float var3, float var4) {
      super.defaultColorf(var1, var2, var3, var4);
      return this;
   }

   public FontOptions defaultColorf(float var1, float var2, float var3) {
      super.defaultColorf(var1, var2, var3);
      return this;
   }

   public FontOptions defaultColor(Color var1) {
      super.defaultColor(var1);
      return this;
   }

   public FontOptions defaultColor(int var1, int var2, int var3, int var4) {
      super.defaultColor(var1, var2, var3, var4);
      return this;
   }

   public FontOptions defaultColor(int var1, int var2, int var3) {
      super.defaultColor(var1, var2, var3);
      return this;
   }

   public boolean getOutline() {
      return this.outline;
   }

   public FontOptions outline(boolean var1) {
      this.outline = var1;
      return this;
   }

   public FontOptions outline() {
      return this.outline(true);
   }

   public FontOptions clearOutline() {
      return this.outline(false);
   }

   public FontOptions outline(float[] var1) {
      super.outline(var1);
      return this;
   }

   public FontOptions clearOutlineColor() {
      super.clearOutlineColor();
      return this;
   }

   public FontOptions outlinef(float var1, float var2, float var3, float var4) {
      super.outlinef(var1, var2, var3, var4);
      return this;
   }

   public FontOptions outlinef(float var1, float var2, float var3) {
      super.outlinef(var1, var2, var3);
      return this;
   }

   public FontOptions outline(Color var1) {
      super.outline(var1);
      return this;
   }

   public FontOptions outline(int var1, int var2, int var3, int var4) {
      super.outline(var1, var2, var3, var4);
      return this;
   }

   public FontOptions outline(int var1, int var2, int var3) {
      super.outline(var1, var2, var3);
      return this;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions outline(int var1, int var2, int var3) {
      return this.outline(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions outline(int var1, int var2, int var3, int var4) {
      return this.outline(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions outline(Color var1) {
      return this.outline(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions outlinef(float var1, float var2, float var3) {
      return this.outlinef(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions outlinef(float var1, float var2, float var3, float var4) {
      return this.outlinef(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions clearOutlineColor() {
      return this.clearOutlineColor();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions outline(float[] var1) {
      return this.outline(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions defaultColor(int var1, int var2, int var3) {
      return this.defaultColor(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions defaultColor(int var1, int var2, int var3, int var4) {
      return this.defaultColor(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions defaultColor(Color var1) {
      return this.defaultColor(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions defaultColorf(float var1, float var2, float var3) {
      return this.defaultColorf(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions defaultColorf(float var1, float var2, float var3, float var4) {
      return this.defaultColorf(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions defaultColor(float[] var1) {
      return this.defaultColor(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions alpha(int var1) {
      return this.alpha(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions alphaf(float var1) {
      return this.alphaf(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions color(int var1, int var2, int var3) {
      return this.color(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions color(int var1, int var2, int var3, int var4) {
      return this.color(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions color(Color var1) {
      return this.color(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions colorf(float var1, float var2, float var3) {
      return this.colorf(var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions colorf(float var1, float var2, float var3, float var4) {
      return this.colorf(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions color(float[] var1) {
      return this.color(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions forceNonPixelFont() {
      return this.forceNonPixelFont();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions forcePixelFont() {
      return this.forcePixelFont();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions size(int var1) {
      return this.size(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FontBasicOptions copy() {
      return this.copy();
   }
}
