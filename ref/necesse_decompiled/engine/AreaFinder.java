package necesse.engine;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;

public abstract class AreaFinder {
   public final int startX;
   public final int startY;
   public final int maxDistance;
   public final boolean breakOnFind;
   private HashSet<Point> found;
   private boolean isDone;
   private int cTile;
   private int cDistance;

   public AreaFinder(Mob var1, int var2, boolean var3) {
      this(var1.getTileX(), var1.getTileY(), var2, var3);
   }

   public AreaFinder(Mob var1, int var2) {
      this(var1, var2, true);
   }

   public AreaFinder(int var1, int var2, int var3) {
      this(var1, var2, var3, true);
   }

   public AreaFinder(int var1, int var2, int var3, boolean var4) {
      this.found = new HashSet();
      this.startX = var1;
      this.startY = var2;
      this.maxDistance = var3;
      this.breakOnFind = var4;
      this.reset();
   }

   public AreaFinder(LoadData var1, boolean var2) throws RuntimeException {
      this.found = new HashSet();

      try {
         this.startX = var1.getInt("startX");
         this.startY = var1.getInt("startY");
         this.maxDistance = var1.getInt("maxDistance");
         this.breakOnFind = var1.getBoolean("breakOnFind");
         Iterator var3 = var1.getFirstLoadDataByName("found").getLoadData().iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();
            this.found.add(LoadData.getPoint(var4));
         }

         this.isDone = this.breakOnFind && !this.found.isEmpty();
         this.cTile = var1.getInt("cTile");
         this.cDistance = var1.getInt("cDistance");
      } catch (Exception var5) {
         if (var2) {
            GameLog.warn.println("Could not load area finder from " + var1.getName());
         }

         throw new RuntimeException(var5);
      }
   }

   public SaveData getSave(String var1) {
      SaveData var2 = new SaveData(var1);
      var2.addInt("startX", this.startX);
      var2.addInt("startY", this.startY);
      var2.addInt("maxDistance", this.maxDistance);
      var2.addBoolean("breakOnFind", this.breakOnFind);
      SaveData var3 = new SaveData("found");
      Iterator var4 = this.found.iterator();

      while(var4.hasNext()) {
         Point var5 = (Point)var4.next();
         var3.addPoint("point", var5);
      }

      var2.addSaveData(var3);
      var2.addInt("cTile", this.cTile);
      var2.addInt("cDistance", this.cDistance);
      return var2;
   }

   public void reset() {
      this.found = new HashSet();
      this.cDistance = 0;
      this.cTile = 0;
      this.isDone = false;
   }

   public void runFinder() {
      this.tickFinder(Integer.MAX_VALUE);
   }

   public void tickFinder(int var1) {
      if (!this.isDone) {
         for(int var2 = 0; var2 < var1; ++var2) {
            int var3 = this.cDistance * 2 + 1;
            if (this.cDistance == 0) {
               if (this.computePoint(this.startX, this.startY)) {
                  break;
               }
            } else if (this.cTile < var3 - 1 && (this.computePoint(this.startX + this.cTile - this.cDistance, this.startY - this.cDistance) || this.computePoint(this.startX - this.cTile + this.cDistance, this.startY + this.cDistance) || this.computePoint(this.startX - this.cDistance, this.startY - this.cTile + this.cDistance) || this.computePoint(this.startX + this.cDistance, this.startY + this.cTile - this.cDistance))) {
               break;
            }

            ++this.cTile;
            if (this.cTile >= var3) {
               ++this.cDistance;
               this.cTile = 0;
               if (this.cDistance > this.maxDistance) {
                  this.isDone = true;
                  break;
               }
            }
         }

      }
   }

   private boolean computePoint(int var1, int var2) {
      return this.checkPoint(var1, var2) && this.addPoint(var1, var2);
   }

   public int getMaxTicks() {
      return (this.maxDistance + 1) * (this.maxDistance + 1);
   }

   public int getCurrentTickCount() {
      return this.cDistance * this.cDistance + this.cTile;
   }

   public int getRemainingTicks() {
      return this.getMaxTicks() - this.getCurrentTickCount();
   }

   private boolean addPoint(int var1, int var2) {
      this.found.add(new Point(var1, var2));
      if (this.breakOnFind) {
         this.isDone = true;
         return true;
      } else {
         return false;
      }
   }

   public Point getFirstFind() {
      return (Point)this.found.stream().findFirst().orElse((Object)null);
   }

   public Point[] getFound() {
      return (Point[])this.found.toArray(new Point[0]);
   }

   public int getFoundSize() {
      return this.found.size();
   }

   public boolean hasFound() {
      return !this.found.isEmpty();
   }

   public boolean isDone() {
      return this.isDone;
   }

   public abstract boolean checkPoint(int var1, int var2);
}
