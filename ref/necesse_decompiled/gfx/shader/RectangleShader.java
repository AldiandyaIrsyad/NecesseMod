package necesse.gfx.shader;

import necesse.engine.Screen;

public class RectangleShader extends GameShader {
   public RectangleShader() {
      super("vert", "fragRectangle");
   }

   public void use(int var1, int var2, int var3, int var4) {
      this.use();
      var2 = Math.abs(var2 - Screen.getCurrentBuffer().getHeight() + var4);
      this.pass1i("x", var1);
      this.pass1i("y", var2);
      this.pass1i("w", var3);
      this.pass1i("h", var4);
   }
}
