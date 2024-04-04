package necesse.gfx.shader;

public class TextureOffsetShader extends GameShader {
   public TextureOffsetShader() {
      super("vert", "fragTextureOffset");
   }

   public void use(float var1, float var2) {
      this.use();
      this.pass1f("textureXOffset", var1);
      this.pass1f("textureYOffset", var2);
   }
}
