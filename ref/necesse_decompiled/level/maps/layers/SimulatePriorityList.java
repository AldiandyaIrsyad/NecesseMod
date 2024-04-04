package necesse.level.maps.layers;

import java.awt.Point;
import java.util.HashMap;
import necesse.engine.util.GameLinkedList;

public class SimulatePriorityList {
   protected final GameLinkedList<SimulateLogic> queue = new GameLinkedList();
   protected final HashMap<Point, SimulateLogic> map = new HashMap();

   public SimulatePriorityList() {
   }

   public void run() {
      while(!this.queue.isEmpty()) {
         ((SimulateLogic)this.queue.removeFirst()).logic.run();
      }

   }

   public void add(Point var1, long var2, Runnable var4) {
      this.map.compute(var1, (var5, var6) -> {
         if (var6 == null) {
            var6 = new SimulateLogic(var1.x, var1.y, var2, var4);
            this.updateQueuePosition(var6);
         } else if (var6.remainingTicks > var2) {
            var6.logic = var4;
            var6.remainingTicks = var2;
            this.updateQueuePosition(var6);
         }

         return var6;
      });
   }

   public void add(int var1, int var2, long var3, Runnable var5) {
      this.add(new Point(var1, var2), var3, var5);
   }

   public boolean queueContains(Point var1) {
      SimulateLogic var2 = (SimulateLogic)this.map.get(var1);
      return var2 != null && !var2.queueElement.isRemoved();
   }

   public boolean queueContains(int var1, int var2) {
      return this.queueContains(new Point(var1, var2));
   }

   public boolean contains(Point var1) {
      return this.map.containsKey(var1);
   }

   public boolean contains(int var1, int var2) {
      return this.contains(new Point(var1, var2));
   }

   protected void updateQueuePosition(SimulateLogic var1) {
      if (var1.queueElement != null && !var1.queueElement.isRemoved()) {
         var1.queueElement.remove();
      }

      if (this.queue.isEmpty()) {
         var1.queueElement = this.queue.addFirst(var1);
      } else {
         GameLinkedList.Element var2;
         for(var2 = this.queue.getFirstElement(); var1.remainingTicks <= ((SimulateLogic)var2.object).remainingTicks; var2 = var2.next()) {
            if (!var2.hasNext()) {
               var1.queueElement = var2.insertAfter(var1);
               return;
            }
         }

         var1.queueElement = var2.insertBefore(var1);
      }

   }

   protected static class SimulateLogic {
      public final int tileX;
      public final int tileY;
      public long remainingTicks;
      public Runnable logic;
      public GameLinkedList<SimulateLogic>.Element queueElement;

      public SimulateLogic(int var1, int var2, long var3, Runnable var5) {
         this.tileX = var1;
         this.tileY = var2;
         this.remainingTicks = var3;
         this.logic = var5;
      }
   }
}
