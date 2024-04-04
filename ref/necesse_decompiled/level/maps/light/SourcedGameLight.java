package necesse.level.maps.light;

public class SourcedGameLight {
   public final int sourceX;
   public final int sourceY;
   public final GameLight light;

   public SourcedGameLight(int var1, int var2, GameLight var3) {
      this.sourceX = var1;
      this.sourceY = var2;
      this.light = var3;
   }

   public String toString() {
      return this.light.toString() + "{" + this.sourceX + "," + this.sourceY + "}";
   }
}
