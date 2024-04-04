package necesse.engine.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class PriorityMap<T> {
   private HashMapArrayList<Integer, T> map = new HashMapArrayList();
   private TreeSet<Integer> priorities = new TreeSet();

   public PriorityMap() {
   }

   public boolean isEmpty() {
      return this.priorities.isEmpty();
   }

   public void add(int var1, T var2) {
      this.priorities.add(var1);
      this.map.add(var1, var2);
   }

   public boolean hasBetter(int var1) {
      if (this.priorities.isEmpty()) {
         return false;
      } else {
         return (Integer)this.priorities.last() > var1;
      }
   }

   public boolean addIfHasNoBetter(int var1, T var2) {
      if (this.hasBetter(var1)) {
         return false;
      } else {
         this.add(var1, var2);
         return true;
      }
   }

   public ArrayList<T> getBestObjectsList() {
      Iterator var1 = this.priorities.descendingSet().iterator();

      ArrayList var3;
      do {
         if (!var1.hasNext()) {
            return new ArrayList();
         }

         int var2 = (Integer)var1.next();
         var3 = (ArrayList)this.map.get(var2);
      } while(var3.isEmpty());

      return new ArrayList(var3);
   }

   public ArrayList<T> getBestObjects(int var1) {
      ArrayList var2 = new ArrayList(var1);
      Iterator var3 = this.priorities.descendingSet().iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         ArrayList var5 = (ArrayList)this.map.get(var4);
         int var6 = var1 - var2.size();
         if (var5.size() >= var6) {
            if (var2.isEmpty()) {
               return new ArrayList(var5);
            }

            var2.addAll(var5);
            break;
         }

         var2.addAll(var5);
      }

      return var2;
   }

   public T getRandomBestObject(GameRandom var1, int var2) {
      return var1.getOneOf((List)this.getBestObjects(var2));
   }

   public void printDebug() {
      Iterator var1 = this.priorities.descendingSet().iterator();

      while(var1.hasNext()) {
         int var2 = (Integer)var1.next();
         ArrayList var3 = (ArrayList)this.map.get(var2);
         System.out.println(var3.size() + " with priority " + var2);
      }

   }
}
