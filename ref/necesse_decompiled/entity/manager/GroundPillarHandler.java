package necesse.entity.manager;

import java.util.Iterator;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.level.maps.Level;

public abstract class GroundPillarHandler<T extends GroundPillar> {
   protected Level level;
   private final GroundPillarList<T> pillars;

   public GroundPillarHandler(GroundPillarList<T> var1) {
      this.pillars = var1;
   }

   public boolean tickAndShouldRemove() {
      if (this.canRemove()) {
         synchronized(this.pillars) {
            if (this.pillars.isEmpty()) {
               return true;
            }
         }
      }

      return false;
   }

   protected abstract boolean canRemove();

   public abstract double getCurrentDistanceMoved();

   public long getCurrentTime() {
      return this.level.getWorldEntity().getLocalTime();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, LevelDrawUtils.DrawArea var2, Level var3, TickManager var4, GameCamera var5) {
      long var6 = this.getCurrentTime();
      double var8 = this.getCurrentDistanceMoved();
      synchronized(this.pillars) {
         this.pillars.clean(var6, var8);
         Iterator var11 = this.pillars.iterator();

         while(var11.hasNext()) {
            final GroundPillar var12 = (GroundPillar)var11.next();
            if (var2.isInPos((float)var12.x, (float)var12.y)) {
               final DrawOptions var13 = this.getDrawOptions(var12, var6, var8, var5);
               if (var13 != null) {
                  var1.add(new LevelSortedDrawable(this) {
                     public int getSortY() {
                        return var12.y;
                     }

                     public void draw(TickManager var1) {
                        var13.draw();
                     }
                  });
               }
            }
         }

      }
   }

   public DrawOptions getDrawOptions(T var1, long var2, double var4, GameCamera var6) {
      return var1.getDrawOptions(this.level, var2, var4, var6);
   }
}
