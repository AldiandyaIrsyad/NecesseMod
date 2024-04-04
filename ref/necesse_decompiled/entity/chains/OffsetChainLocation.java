package necesse.entity.chains;

public class OffsetChainLocation implements ChainLocation {
   public ChainLocation location;
   public int xOffset;
   public int yOffset;

   public OffsetChainLocation(ChainLocation var1, int var2, int var3) {
      this.location = var1;
      this.xOffset = var2;
      this.yOffset = var3;
   }

   public int getX() {
      return this.location.getX() + this.xOffset;
   }

   public int getY() {
      return this.location.getY() + this.yOffset;
   }

   public boolean removed() {
      return this.location.removed();
   }
}
