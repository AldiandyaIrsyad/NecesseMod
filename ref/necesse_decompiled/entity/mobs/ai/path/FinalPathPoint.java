package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.function.Supplier;

public class FinalPathPoint extends Point {
   public Supplier<Boolean> checkValid;

   public FinalPathPoint(int var1, int var2, Supplier<Boolean> var3) {
      super(var1, var2);
      this.checkValid = var3;
   }

   public boolean checkValid() {
      return (Boolean)this.checkValid.get();
   }
}
