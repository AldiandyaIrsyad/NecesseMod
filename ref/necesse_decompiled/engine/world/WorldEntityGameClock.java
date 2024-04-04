package necesse.engine.world;

public interface WorldEntityGameClock extends GameClock {
   WorldEntity getWorldEntity();

   default long getTime() {
      WorldEntity var1 = this.getWorldEntity();
      return var1 == null ? 0L : var1.getTime();
   }

   default long getWorldTime() {
      WorldEntity var1 = this.getWorldEntity();
      return var1 == null ? 0L : var1.getWorldTime();
   }

   default long getLocalTime() {
      WorldEntity var1 = this.getWorldEntity();
      return var1 == null ? 0L : var1.getLocalTime();
   }
}
