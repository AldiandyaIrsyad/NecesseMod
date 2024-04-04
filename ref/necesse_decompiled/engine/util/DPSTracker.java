package necesse.engine.util;

import java.util.ArrayList;
import java.util.Iterator;

public class DPSTracker {
   public int DPS_TRACKING_TIME = 5000;
   public int DPS_RESET_TIME = 2000;
   private ArrayList<DamageHit> hits = new ArrayList();
   private boolean updateDPS = false;
   private int dps = 0;

   public DPSTracker() {
   }

   public void tick(long var1) {
      this.tickHits(var1);
   }

   public void addHit(long var1, float var3) {
      this.hits.add(new DamageHit(var1, var3));
      this.updateDPS = true;
   }

   private void tickHits(long var1) {
      boolean var3 = false;
      if (this.hits.size() > 0) {
         long var4 = ((DamageHit)this.hits.get(this.hits.size() - 1)).time;
         if (var1 - var4 > (long)this.DPS_RESET_TIME) {
            this.hits.clear();
            var3 = true;
         } else {
            for(long var6 = var1 - (long)this.DPS_TRACKING_TIME; this.hits.size() > 0; var3 = true) {
               DamageHit var8 = (DamageHit)this.hits.get(0);
               if (var8.time >= var6) {
                  break;
               }

               this.hits.remove(0);
            }
         }
      }

      if (var3) {
         this.updateDPS = true;
      }

   }

   public boolean isLastHitBeforeReset(long var1) {
      if (this.hits.isEmpty()) {
         return false;
      } else {
         long var3 = ((DamageHit)this.hits.get(this.hits.size() - 1)).time;
         return var1 - var3 < (long)this.DPS_RESET_TIME;
      }
   }

   public int getDPS(long var1) {
      if (this.updateDPS) {
         this.dps = 0;
         float var3 = 0.0F;

         DamageHit var5;
         for(Iterator var4 = this.hits.iterator(); var4.hasNext(); var3 += var5.damage) {
            var5 = (DamageHit)var4.next();
         }

         long var8 = this.hits.size() > 0 ? ((DamageHit)this.hits.get(0)).time : var1;
         double var6 = (double)(var1 - var8) / 1000.0;
         if (var6 < 1.0) {
            var6 = 1.0;
         }

         this.dps = (int)((double)var3 / var6);
         this.updateDPS = false;
      }

      return this.dps;
   }

   private static class DamageHit {
      public final long time;
      public final float damage;

      public DamageHit(long var1, float var3) {
         this.time = var1;
         this.damage = var3;
      }
   }
}
