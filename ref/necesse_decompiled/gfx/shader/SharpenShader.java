package necesse.gfx.shader;

public class SharpenShader extends GameShader {
   public SharpenShader() {
      super("vert", "fragSharpen");
   }

   public void use(int var1, int var2, float var3) {
      this.use();
      this.pass1i("renderWidth", var1);
      this.pass1i("renderHeight", var2);
      this.pass1f("amount", var3);
   }
}
