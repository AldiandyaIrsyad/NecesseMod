package necesse.gfx.shader;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicReference;
import necesse.gfx.shader.shaderVariable.ShaderBooleanVariable;

public class MaskShader extends GameShader {
   public MaskShader() {
      super("vertMask", "fragMask");
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
            int[] var1x = MaskShader.this.get2i("maskOffset");
            var3.set(new Point(var1x[0], var1x[1]));
            MaskShader.this.pass2i("maskOffset", var1x[0] + var1, var1x[1] + var2);
         }

         public void stop() {
            Point var1x = (Point)var3.get();
            MaskShader.this.pass2i("maskOffset", var1x.x, var1x.y);
         }
      };
   }
}
