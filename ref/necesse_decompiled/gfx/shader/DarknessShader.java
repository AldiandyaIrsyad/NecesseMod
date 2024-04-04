package necesse.gfx.shader;

import necesse.engine.Screen;
import necesse.gfx.shader.shaderVariable.ShaderFloatVariable;
import necesse.gfx.shader.shaderVariable.ShaderIntVariable;

public class DarknessShader extends GameShader {
   public int midScreenX;
   public int midScreenY;

   public DarknessShader() {
      super("vert", "fragDarkness");
      this.addVariable(new ShaderFloatVariable("intensity", 0.0F, 1.0F, 100));
      this.addVariable(new ShaderIntVariable("range", 0, 1000));
   }

   public void use() {
      super.use();
      this.pass1i("screenWidth", Screen.getFrameWidth());
      this.pass1i("screenHeight", Screen.getFrameHeight());
      this.pass1i("midX", this.midScreenX);
      this.pass1i("midY", this.midScreenY);
   }
}
