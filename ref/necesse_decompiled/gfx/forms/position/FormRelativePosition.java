package necesse.gfx.forms.position;

import java.util.function.Supplier;

public class FormRelativePosition implements FormPosition {
   private final FormPositionContainer parent;
   public Supplier<Integer> x;
   public Supplier<Integer> y;

   public FormRelativePosition(FormPositionContainer var1, Supplier<Integer> var2, Supplier<Integer> var3) {
      this.parent = var1;
      this.x = var2;
      this.y = var3;
   }

   public FormRelativePosition(FormPositionContainer var1, int var2, int var3) {
      this(var1, () -> {
         return var2;
      }, () -> {
         return var3;
      });
   }

   public int getX() {
      return this.parent.getX() + (Integer)this.x.get();
   }

   public int getY() {
      return this.parent.getY() + (Integer)this.y.get();
   }
}
