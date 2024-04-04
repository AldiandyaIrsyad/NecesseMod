package necesse.gfx.shader;

import necesse.engine.Screen;

public class TestShader extends GameShader {
   public TestShader() {
      super("vert", "fragTest");
   }

   public void use() {
      super.use();
      this.pass1i("displayWidth", Screen.getSceneWidth());
      this.pass1i("displayHeight", Screen.getSceneHeight());
   }

   public void drawTest(int var1, int var2, int var3, int var4) {
      this.use();
      Screen.initQuadDraw(var3, var4).draw(var1, var2);
      this.stop();
   }
}
