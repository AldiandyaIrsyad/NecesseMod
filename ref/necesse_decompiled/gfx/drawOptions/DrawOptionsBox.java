package necesse.gfx.drawOptions;

import java.awt.Rectangle;

public interface DrawOptionsBox extends DrawOptions {
   Rectangle getBoundingBox();

   static DrawOptionsBox concat(final DrawOptionsBox... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Must have at least one argument");
      } else {
         return new DrawOptionsBox() {
            public void draw() {
               DrawOptionsBox[] var1 = var0;
               int var2 = var1.length;

               for(int var3 = 0; var3 < var2; ++var3) {
                  DrawOptionsBox var4 = var1[var3];
                  var4.draw();
               }

            }

            public Rectangle getBoundingBox() {
               Rectangle var1 = null;
               DrawOptionsBox[] var2 = var0;
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  DrawOptionsBox var5 = var2[var4];
                  if (var1 == null) {
                     var1 = var5.getBoundingBox();
                  } else {
                     var1 = var1.union(var5.getBoundingBox());
                  }
               }

               return var1;
            }
         };
      }
   }
}
