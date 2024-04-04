package necesse.gfx.forms.position;

import java.util.function.Supplier;

public class FormPositionDynamic implements FormPosition {
   public int x;
   public int y;
   public Supplier<Integer> xOffset;
   public Supplier<Integer> yOffset;

   public FormPositionDynamic(int var1, int var2, Supplier<Integer> var3, Supplier<Integer> var4) {
      this.x = var1;
      this.y = var2;
      this.xOffset = var3;
      this.yOffset = var4;
   }

   public FormPositionDynamic(Supplier<Integer> var1, Supplier<Integer> var2) {
      this(0, 0, var1, var2);
   }

   public int getX() {
      return this.x + (Integer)this.xOffset.get();
   }

   public int getY() {
      return this.y + (Integer)this.yOffset.get();
   }
}
