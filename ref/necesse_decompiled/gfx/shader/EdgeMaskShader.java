package necesse.gfx.shader;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import necesse.gfx.drawOptions.texture.ShaderTexture;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.shaderVariable.ShaderBooleanVariable;

public class EdgeMaskShader extends GameShader {
   public EdgeMaskShader() {
      super("vertMask", "fragEdgeMask");
      this.addVariable(new ShaderBooleanVariable("maskDebug"));
   }

   public void use() {
      this.use(0, 0);
   }

   public void use(int var1, int var2) {
      super.use();
      this.pass1i("maskTexture", 1);
      this.pass2i("maskOffset", var1, var2);
   }

   public ShaderState addMaskOffset(final int var1, final int var2) {
      final AtomicReference var3 = new AtomicReference();
      return new ShaderState() {
         public void use() {
            int[] var1x = EdgeMaskShader.this.get2i("maskOffset");
            var3.set(new Point(var1x[0], var1x[1]));
            EdgeMaskShader.this.pass2i("maskOffset", var1x[0] + var1, var1x[1] + var2);
         }

         public void stop() {
            Point var1x = (Point)var3.get();
            EdgeMaskShader.this.pass2i("maskOffset", var1x.x, var1x.y);
         }
      };
   }

   public ShaderState setup(TextureDrawOptionsEnd var1, GameSprite var2, int var3, int var4) {
      int var5 = var1.getWidth() - var2.width;
      float var6 = TextureDrawOptions.pixel(var2.spriteX * var2.spriteWidth - var3, var2.texture.getWidth());
      float var7 = TextureDrawOptions.pixel((var2.spriteX + 1) * var2.spriteWidth - var3 + var5, var2.texture.getWidth());
      int var8 = var1.getHeight() - var2.height;
      float var9 = TextureDrawOptions.pixel(var2.spriteY * var2.spriteHeight - var4, var2.texture.getHeight());
      float var10 = TextureDrawOptions.pixel((var2.spriteY + 1) * var2.spriteHeight - var4 + var8, var2.texture.getHeight());
      var1.addShaderTexture(new ShaderTexture(1, var2.texture, var6, var7, var9, var10));
      return new ShaderState() {
         public void use() {
            EdgeMaskShader.this.use();
         }

         public void stop() {
            EdgeMaskShader.this.stop();
         }
      };
   }

   public ShaderState setupCenterX(TextureDrawOptionsEnd var1, GameSprite var2, int var3, int var4) {
      var3 += (var1.getWidth() - var2.width) / 2;
      return this.setup(var1, var2, var3, var4);
   }

   public ShaderState setup(TextureDrawOptionsEnd var1, GameTexture var2, int var3, int var4) {
      return this.setup(var1, new GameSprite(var2), var3, var4);
   }

   public ShaderState setupCenterX(TextureDrawOptionsEnd var1, GameTexture var2, int var3, int var4) {
      return this.setupCenterX(var1, new GameSprite(var2), var3, var4);
   }
}
