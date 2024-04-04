package necesse.engine.util;

import java.util.Iterator;
import java.util.LinkedList;

public class GroundPillarList<T extends GroundPillar> implements Iterable<T> {
   private LinkedList<T> pillars = new LinkedList();

   public GroundPillarList() {
   }

   public void add(T var1) {
      this.pillars.addFirst(var1);
   }

   public void clean(long var1, double var3) {
      while(!this.pillars.isEmpty() && ((GroundPillar)this.pillars.getLast()).shouldRemove(var1, var3)) {
         this.pillars.removeLast();
      }

   }

   public void cleanThorough(long var1, double var3) {
      this.pillars.removeIf((var4) -> {
         return var4.shouldRemove(var1, var3);
      });
   }

   public Iterator<T> iterator() {
      return this.pillars.iterator();
   }

   public int size() {
      return this.pillars.size();
   }

   public boolean isEmpty() {
      return this.pillars.isEmpty();
   }
}
