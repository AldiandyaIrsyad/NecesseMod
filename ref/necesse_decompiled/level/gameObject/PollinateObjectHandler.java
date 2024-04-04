package necesse.level.gameObject;

import necesse.engine.util.GameObjectReservable;

public abstract class PollinateObjectHandler {
   public int tileX;
   public int tileY;
   public GameObjectReservable reservable;

   public PollinateObjectHandler(int var1, int var2, GameObjectReservable var3) {
      this.tileX = var1;
      this.tileY = var2;
      this.reservable = var3;
   }

   public abstract boolean canPollinate();

   public abstract void pollinate();

   public abstract boolean isValid();
}
