package necesse.engine.util;

import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;

public class GameObjectReservable {
   private long reserveTick;
   private Object reserveWorker;

   public GameObjectReservable() {
   }

   public void reserve(Object var1, WorldEntity var2) {
      this.reserveWorker = var1;
      this.reserveTick = var2.getGameTicks();
   }

   public void reserve(Entity var1) {
      this.reserve(var1, var1.getWorldEntity());
   }

   public boolean isAvailable(Object var1, WorldEntity var2) {
      return this.reserveWorker == var1 || this.reserveTick < var2.getGameTicks() - 2L;
   }

   public final boolean isAvailable(Entity var1) {
      return this.isAvailable(var1, var1.getWorldEntity());
   }

   public void printReserveWorker() {
      System.out.println(this.reserveWorker);
   }
}
