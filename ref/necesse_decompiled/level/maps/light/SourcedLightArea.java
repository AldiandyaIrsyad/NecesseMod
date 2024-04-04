package necesse.level.maps.light;

public class SourcedLightArea {
   public final int startX;
   public final int startY;
   public final int endX;
   public final int endY;
   public final int width;
   protected SourcedGameLight[] lights;

   public SourcedLightArea(int var1, int var2, int var3, int var4) {
      this.startX = var1;
      this.startY = var2;
      this.endX = var3;
      this.endY = var4;
      this.width = var3 - var1 + 1;
   }

   public void initLights() {
      if (this.lights == null) {
         int var1 = this.endY - this.startY + 1;
         this.lights = new SourcedGameLight[this.width * var1];
      }

   }

   public boolean isOutsideArea(int var1, int var2) {
      return var1 < this.startX || var2 < this.startY || var1 > this.endX || var2 > this.endY;
   }

   protected int getIndex(int var1, int var2) {
      return var1 - this.startX + (var2 - this.startY) * this.width;
   }
}
