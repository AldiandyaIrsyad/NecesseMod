package necesse.entity.mobs.summon;

import java.util.function.Consumer;

public class MinecartLinePos {
   public final MinecartLine line;
   public final float x;
   public final float y;
   public final float distanceAlong;
   public final int dir;
   public final float distanceRemainingToTravel;

   public MinecartLinePos(MinecartLine var1, float var2, float var3, float var4, int var5, float var6) {
      this.line = var1;
      this.x = var2;
      this.y = var3;
      this.distanceAlong = var4;
      this.dir = var5;
      this.distanceRemainingToTravel = var6;
   }

   public MinecartLinePos(MinecartLine var1, float var2, float var3, float var4, int var5) {
      this(var1, var2, var3, var4, var5, 0.0F);
   }

   public MinecartLinePos progressLines(int var1, float var2, Consumer<MinecartLine> var3) {
      return this.line.progressLines(this.line, var1, this.distanceAlong, var2, var3);
   }
}
