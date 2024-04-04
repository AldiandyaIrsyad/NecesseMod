package necesse.engine.util.gameAreaSearch;

public abstract class GameAreaSearch<T> {
   public final int startX;
   public final int startY;
   protected int maxDistance;
   protected int minX;
   protected int minY;
   protected int maxX;
   protected int maxY;
   protected boolean isDone;
   protected int currentTile;
   protected int currentDistance;
   protected int currentDir;
   protected int dirsHandled;

   public GameAreaSearch(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.startX = var1;
      this.startY = var2;
      this.setLimit(var3, var4, var5, var6);
      this.setMaxDistance(var7);
   }

   public GameAreaSearch(int var1, int var2, int var3) {
      this(var1, var2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, var3);
   }

   public int getMaxDistance() {
      return this.maxDistance;
   }

   public void setMaxDistance(int var1) {
      this.maxDistance = var1;
      if (this.currentDistance > var1) {
         this.isDone = true;
      }

   }

   public void setLimit(int var1, int var2, int var3, int var4) {
      this.minX = var1;
      this.minY = var2;
      this.maxX = var3;
      this.maxY = var4;
   }

   public void shrinkLimit(int var1, int var2, int var3, int var4) {
      this.minX = Math.max(var1, this.minX);
      this.minY = Math.max(var2, this.minY);
      this.maxX = Math.min(var3, this.maxX);
      this.maxY = Math.min(var4, this.maxY);
   }

   private static int getOffset(int var0) {
      return var0 % 2 == 0 ? var0 / 2 : -(var0 + 1) / 2;
   }

   protected FoundElement<T> next() {
      if (this.isDone) {
         return null;
      } else {
         FoundElement var1 = null;
         int var2 = this.currentDistance * 2 + 1;
         if (this.currentDistance == 0) {
            var1 = new FoundElement(this.currentDistance, this.currentTile, this.get(this.startX, this.startY));
         } else {
            if (this.currentDir >= 4) {
               if (this.dirsHandled == 0) {
                  this.isDone = true;
                  return null;
               }

               this.dirsHandled = 0;
               this.currentDir = 0;
            }

            ++this.currentDir;
            int var3;
            int var4;
            switch (this.currentDir) {
               case 1:
                  var3 = this.startY - this.currentDistance;
                  if (var3 < this.minY) {
                     return null;
                  }

                  var4 = this.startX + getOffset(this.currentTile);
                  if (var4 >= this.minX && var4 <= this.maxX) {
                     ++this.dirsHandled;
                     return new FoundElement(this.currentDistance, this.currentTile, this.get(var4, var3));
                  }

                  return null;
               case 2:
                  var3 = this.startY + this.currentDistance;
                  if (var3 > this.maxY) {
                     return null;
                  }

                  var4 = this.startX - getOffset(this.currentTile);
                  if (var4 >= this.minX && var4 <= this.maxX) {
                     ++this.dirsHandled;
                     return new FoundElement(this.currentDistance, this.currentTile, this.get(var4, var3));
                  }

                  return null;
               case 3:
                  var3 = this.startX - this.currentDistance;
                  if (var3 < this.minX) {
                     return null;
                  }

                  var4 = this.startY - getOffset(this.currentTile);
                  if (var4 >= this.minY && var4 <= this.maxY) {
                     ++this.dirsHandled;
                     return new FoundElement(this.currentDistance, this.currentTile, this.get(var3, var4));
                  }

                  return null;
               case 4:
                  var3 = this.startX + this.currentDistance;
                  if (var3 <= this.maxX) {
                     var4 = this.startY + getOffset(this.currentTile);
                     if (var4 >= this.minY && var4 <= this.maxY) {
                        ++this.dirsHandled;
                        var1 = new FoundElement(this.currentDistance, this.currentTile, this.get(var3, var4));
                     }
                  }
            }
         }

         ++this.currentTile;
         if (this.currentTile >= var2 - 1) {
            ++this.currentDistance;
            this.currentTile = 0;
            if (this.currentDistance > this.maxDistance) {
               this.isDone = true;
            }
         }

         return var1;
      }
   }

   protected abstract T get(int var1, int var2);

   public int getCurrentDistance() {
      return this.currentDistance;
   }

   public boolean isDone() {
      return this.isDone;
   }

   public GameAreaStream<T> stream() {
      return new GameAreaPipeline(this);
   }

   public static class FoundElement<T> {
      public final int distance;
      public final int tile;
      public final T element;

      public FoundElement(int var1, int var2, T var3) {
         this.distance = var1;
         this.tile = var2;
         this.element = var3;
      }
   }
}
