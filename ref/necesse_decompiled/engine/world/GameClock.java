package necesse.engine.world;

public interface GameClock {
   long getTime();

   long getWorldTime();

   long getLocalTime();

   static GameClock offsetClock(final GameClock var0, final long var1) {
      return new GameClock() {
         public long getTime() {
            return var0.getTime() + var1;
         }

         public long getWorldTime() {
            return var0.getWorldTime() + var1;
         }

         public long getLocalTime() {
            return var0.getLocalTime() + var1;
         }
      };
   }

   static GameClock staticClock(final long var0) {
      return new GameClock() {
         public long getTime() {
            return var0;
         }

         public long getWorldTime() {
            return var0;
         }

         public long getLocalTime() {
            return var0;
         }
      };
   }
}
