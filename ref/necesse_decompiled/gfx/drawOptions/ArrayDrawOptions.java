package necesse.gfx.drawOptions;

import java.util.List;

public class ArrayDrawOptions implements DrawOptions {
   private DrawOptions[] options;

   public ArrayDrawOptions(DrawOptions... var1) {
      this.options = var1;
   }

   public ArrayDrawOptions(List<DrawOptions> var1) {
      this((DrawOptions[])var1.toArray(new DrawOptions[var1.size()]));
   }

   public void draw() {
      DrawOptions[] var1 = this.options;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DrawOptions var4 = var1[var3];
         var4.draw();
      }

   }
}
