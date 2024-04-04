package necesse.entity.mobs;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;

public class OpenedDoors implements Iterable<OpenedDoor> {
   private final Mob mob;
   private HashMap<Point, OpenedDoor> openedDoors = new HashMap();

   public OpenedDoors(Mob var1) {
      this.mob = var1;
   }

   public boolean isEmpty() {
      return this.openedDoors.isEmpty();
   }

   public Iterator<OpenedDoor> iterator() {
      return new OpenedDoorsIterator();
   }

   public void add(int var1, int var2, int var3, int var4, boolean var5) {
      this.openedDoors.put(new Point(var1, var2), new OpenedDoor(var1, var2, var3, var4, var5));
      this.mob.getLevel().entityManager.serverOpenedDoors.put(new Point(var1, var2), this.mob);
   }

   public OpenedDoor get(int var1, int var2) {
      return (OpenedDoor)this.openedDoors.get(new Point(var1, var2));
   }

   public void clear() {
      Iterator var1 = this.openedDoors.values().iterator();

      while(var1.hasNext()) {
         OpenedDoor var2 = (OpenedDoor)var1.next();
         this.mob.getLevel().entityManager.serverOpenedDoors.remove(new Point(var2.tileX, var2.tileY), this.mob);
      }

      this.openedDoors.clear();
   }

   public boolean hasMobServerOpened(int var1, int var2) {
      Mob var3 = (Mob)this.mob.getLevel().entityManager.serverOpenedDoors.get(new Point(var1, var2));
      return var3 != null && !var3.removed();
   }

   private class OpenedDoorsIterator implements Iterator<OpenedDoor> {
      private final Iterator<OpenedDoor> iterator;
      private OpenedDoor last;

      public OpenedDoorsIterator() {
         this.iterator = OpenedDoors.this.openedDoors.values().iterator();
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public OpenedDoor next() {
         this.last = (OpenedDoor)this.iterator.next();
         return this.last;
      }

      public void remove() {
         if (this.last != null) {
            OpenedDoors.this.mob.getLevel().entityManager.serverOpenedDoors.remove(new Point(this.last.tileX, this.last.tileY), OpenedDoors.this.mob);
            this.last = null;
         }

         this.iterator.remove();
      }

      // $FF: synthetic method
      // $FF: bridge method
      public Object next() {
         return this.next();
      }
   }
}
