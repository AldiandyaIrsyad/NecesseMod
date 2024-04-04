package necesse.entity.levelEvent.fishingEvent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.FishingHookProjectile;
import necesse.entity.trails.FishingTrail;
import necesse.inventory.InventoryItem;

public class ReelFishingPhase extends FishingPhase {
   private ArrayList<ReelParticles> lines;

   public ReelFishingPhase(FishingEvent var1, ArrayList<WaitFishingPhase.FishingLure> var2) {
      super(var1);
      this.lines = new ArrayList(var2.size());
      int var3 = 0;
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         WaitFishingPhase.FishingLure var5 = (WaitFishingPhase.FishingLure)var4.next();
         InventoryItem var6 = var5.getCatch();
         this.lines.add(new ReelParticles(var5.hookPosition, var6));
         if (var6 != null) {
            ++var3;
         }
      }

      if (var1.level.isServer() && var3 >= 2 && var1.getMob().isPlayer) {
         ServerClient var7 = ((PlayerMob)var1.getMob()).getServerClient();
         if (var7 != null && var7.achievementsLoaded()) {
            var7.achievements().DOUBLE_CATCH.markCompleted(var7);
         }
      }

   }

   public void tickMovement(float var1) {
      boolean var2 = true;
      Iterator var3 = this.lines.iterator();

      while(var3.hasNext()) {
         ReelParticles var4 = (ReelParticles)var3.next();
         var4.tickMovement();
         if (!var4.isOver) {
            var2 = false;
         }
      }

      if (var2) {
         this.event.over();
      }

   }

   public void clientTick() {
   }

   public void serverTick() {
   }

   public void end() {
      this.lines.forEach(ReelParticles::remove);
   }

   public void over() {
      if (!this.event.isClient()) {
         boolean var1 = true;
         Iterator var2 = this.lines.iterator();

         while(var2.hasNext()) {
            ReelParticles var3 = (ReelParticles)var2.next();
            if (!var3.spawnedItem) {
               var3.spawnItem();
            }

            if (var3.spawnedItem) {
               var1 = false;
            }
         }

         if (var1) {
            this.event.giveBaitBack();
         }
      }

      this.event.getFishingMob().stopFishing();
      this.lines.forEach(ReelParticles::remove);
   }

   private class ReelParticles {
      public FishingTrail line;
      public FishingHookProjectile hook;
      public final InventoryItem caught;
      public boolean spawnedItem;
      public boolean isOver;

      public ReelParticles(Point var2, InventoryItem var3) {
         this.caught = var3;
         this.hook = new FishingHookProjectile(ReelFishingPhase.this.event.level, ReelFishingPhase.this.event, ReelFishingPhase.this.event.getMob(), var3 == null ? null : var3.item);
         this.hook.applyData((float)var2.x, (float)var2.y, (float)ReelFishingPhase.this.event.getMob().getX(), (float)ReelFishingPhase.this.event.getMob().getY(), (float)ReelFishingPhase.this.event.fishingRod.hookSpeed * 1.25F, ReelFishingPhase.this.event.fishingRod.lineLength * 5, new GameDamage(0.0F), ReelFishingPhase.this.event.getMob());
         ReelFishingPhase.this.event.level.entityManager.projectiles.addHidden(this.hook);
         if (!ReelFishingPhase.this.event.level.isServer()) {
            ReelFishingPhase.this.event.level.entityManager.addTrail(this.line = new FishingTrail(ReelFishingPhase.this.event.getMob(), ReelFishingPhase.this.event.level, this.hook, ReelFishingPhase.this.event.fishingRod));
         }

      }

      public void tickMovement() {
         if (this.line != null) {
            this.line.update();
         }

         if (!this.isOver && this.hook.removed()) {
            if (!ReelFishingPhase.this.event.isClient() && !this.spawnedItem) {
               this.spawnItem();
            }

            if (this.line != null && !this.line.isRemoved()) {
               this.line.remove();
            }

            this.isOver = true;
         }

      }

      public void spawnItem() {
         if (this.caught != null) {
            ReelFishingPhase.this.event.getFishingMob().giveCaughtItem(ReelFishingPhase.this.event, this.caught);
            if (ReelFishingPhase.this.event.getMob().isPlayer) {
               ServerClient var1 = ((PlayerMob)ReelFishingPhase.this.event.getMob()).getServerClient();
               if (var1 != null) {
                  var1.newStats.fish_caught.increment(1);
               }
            }

            this.spawnedItem = true;
         }

      }

      public void remove() {
         if (this.line != null && !this.line.isRemoved()) {
            this.line.remove();
         }

         if (!this.hook.removed()) {
            this.hook.remove();
         }

      }
   }
}
