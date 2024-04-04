package necesse.entity.chains;

public class StaticChainLocation implements ChainLocation {
   public int x;
   public int y;

   public StaticChainLocation(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public boolean removed() {
      return false;
   }
}
