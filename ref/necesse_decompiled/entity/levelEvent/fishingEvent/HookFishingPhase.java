package necesse.entity.levelEvent.fishingEvent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.entity.mobs.GameDamage;
import necesse.entity.particle.FishingHookParticle;
import necesse.entity.projectile.FishingHookProjectile;
import necesse.entity.trails.FishingTrail;

public class HookFishingPhase extends FishingPhase {
   private ArrayList<HookLineParticles> lines;
   private int landed;

   public HookFishingPhase(FishingEvent var1) {
      super(var1);
      this.lines = new ArrayList(var1.getLines());

      for(int var2 = 0; var2 < var1.getLines(); ++var2) {
         this.lines.add(new HookLineParticles(var2));
      }

      var1.getFishingMob().showFishingWaitAnimation(var1.fishingRod, var1.getTarget().x, var1.getTarget().y);
   }

   public void tickMovement(float var1) {
      this.landed = 0;
      Iterator var2 = this.lines.iterator();

      while(var2.hasNext()) {
         HookLineParticles var3 = (HookLineParticles)var2.next();
         var3.tickLanded();
         if (var3.landed) {
            ++this.landed;
         }
      }

   }

   public void clientTick() {
      if (this.landed == this.lines.size()) {
         this.event.setPhase(new WaitFishingPhase(this.event, this.getHookPositions()));
      } else {
         this.event.getFishingMob().showFishingWaitAnimation(this.event.fishingRod, this.event.getTarget().x, this.event.getTarget().y);
      }

   }

   public void serverTick() {
      if (this.landed == this.lines.size()) {
         this.event.setPhase(new WaitFishingPhase(this.event, this.getHookPositions()));
      } else {
         this.event.getFishingMob().showFishingWaitAnimation(this.event.fishingRod, this.event.getTarget().x, this.event.getTarget().y);
         this.event.checkOutsideRange();
      }

   }

   private Point[] getHookPositions() {
      Point[] var1 = new Point[this.lines.size()];

      for(int var2 = 0; var2 < this.lines.size(); ++var2) {
         var1[var2] = ((HookLineParticles)this.lines.get(var2)).targetPos;
      }

      return var1;
   }

   public void end() {
      this.lines.forEach(HookLineParticles::remove);
   }

   public void over() {
      this.event.getFishingMob().stopFishing();
      this.lines.forEach(HookLineParticles::remove);
   }

   public class HookLineParticles {
      public FishingTrail line;
      public FishingHookProjectile hook;
      public FishingHookParticle hookParticle;
      public boolean landed;
      public final Point targetPos;

      public HookLineParticles(int var2) {
         this.hook = new FishingHookProjectile(HookFishingPhase.this.event.level, HookFishingPhase.this.event);
         this.targetPos = HookFishingPhase.this.event.getRandomTarget(var2);
         float var10003 = (float)this.targetPos.x;
         float var10004 = (float)this.targetPos.y;
         this.hook.applyData(HookFishingPhase.this.event.getMob().x, HookFishingPhase.this.event.getMob().y, var10003, var10004, (float)HookFishingPhase.this.event.fishingRod.hookSpeed, (int)HookFishingPhase.this.event.getMob().getPositionPoint().distance((double)this.targetPos.x, (double)this.targetPos.y), new GameDamage(0.0F), HookFishingPhase.this.event.getMob());
         this.hook.setStartHeight(30);
         HookFishingPhase.this.event.level.entityManager.projectiles.addHidden(this.hook);
         if (!HookFishingPhase.this.event.level.isServer()) {
            HookFishingPhase.this.event.level.entityManager.addTrail(this.line = new FishingTrail(HookFishingPhase.this.event.getMob(), HookFishingPhase.this.event.level, this.hook, HookFishingPhase.this.event.fishingRod));
         }

      }

      public void tickLanded() {
         if (this.hookParticle != null) {
            this.hookParticle.refreshSpawnTime();
         }

         if (this.line != null) {
            this.line.update();
         }

         if (this.hook != null) {
            if (this.hook.removed()) {
               this.landed = true;
               this.targetPos.x = this.hook.getX();
               this.targetPos.y = this.hook.getY();
               this.hook = null;
               if (this.line != null && !this.line.isRemoved()) {
                  this.line.remove();
               }

               HookFishingPhase.this.event.level.entityManager.particles.add(this.hookParticle = new FishingHookParticle(HookFishingPhase.this.event.level, (float)this.targetPos.x, (float)this.targetPos.y, HookFishingPhase.this.event.fishingRod));
               HookFishingPhase.this.event.level.entityManager.addTrail(this.line = new FishingTrail(HookFishingPhase.this.event.getMob(), HookFishingPhase.this.event.level, this.hookParticle, HookFishingPhase.this.event.fishingRod));
            } else {
               float var1 = Math.abs(this.hook.getLifeTime() - 1.0F);
               this.hook.speed = (float)HookFishingPhase.this.event.fishingRod.hookSpeed * (var1 / 2.0F + 0.5F);
            }
         }

      }

      public void remove() {
         if (this.line != null && !this.line.isRemoved()) {
            this.line.remove();
         }

         if (this.hook != null && !this.hook.removed()) {
            this.hook.remove();
         }

         if (this.hookParticle != null && !this.hookParticle.removed()) {
            this.hookParticle.remove();
         }

      }
   }
}
