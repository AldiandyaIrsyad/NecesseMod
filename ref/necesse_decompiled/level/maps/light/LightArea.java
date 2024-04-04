package necesse.level.maps.light;

public class LightArea {
   public final int startX;
   public final int startY;
   public final int endX;
   public final int endY;
   public final int width;
   protected GameLight[] lights;

   public LightArea(int var1, int var2, int var3, int var4) {
      this.startX = var1;
      this.startY = var2;
      this.endX = var3;
      this.endY = var4;
      this.width = var3 - var1 + 1;
   }

   public void initLights() {
      if (this.lights == null) {
         int var1 = this.endY - this.startY + 1;
         this.lights = new GameLight[this.width * var1];
      }

   }

   public boolean isOutsideArea(int var1, int var2) {
      return var1 < this.startX || var2 < this.startY || var1 > this.endX || var2 > this.endY;
   }

   protected int getIndex(int var1, int var2) {
      return var1 - this.startX + (var2 - this.startY) * this.width;
   }

   protected void overwriteArea(LightArea var1) {
      for(int var2 = this.startX; var2 <= this.endX; ++var2) {
         for(int var3 = this.startY; var3 <= this.endY; ++var3) {
            GameLight var4 = this.lights[this.getIndex(var2, var3)];
            if (var4 != null) {
               var1.lights[var1.getIndex(var2, var3)] = var4;
            }
         }
      }

   }
}
