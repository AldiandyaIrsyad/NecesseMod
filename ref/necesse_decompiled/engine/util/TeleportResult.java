package necesse.engine.util;

import java.awt.Point;

public class TeleportResult {
   public final boolean isValid;
   public final LevelIdentifier newDestination;
   public final Point targetPosition;

   public TeleportResult(boolean var1, LevelIdentifier var2, Point var3) {
      this.isValid = var1;
      this.newDestination = var2;
      this.targetPosition = var3;
   }

   public TeleportResult(boolean var1, LevelIdentifier var2, int var3, int var4) {
      this(var1, var2, new Point(var3, var4));
   }

   public TeleportResult(boolean var1, Point var2) {
      this(var1, (LevelIdentifier)null, var2);
   }

   public TeleportResult(boolean var1, int var2, int var3) {
      this(var1, new Point(var2, var3));
   }
}
