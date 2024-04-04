package necesse.entity.manager;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class ClientSubmittedHits {
   private static final int INVALIDATE_TIMEOUT = 2000;
   private final EntityManager manager;
   private final Level level;
   private LinkedList<SubmittedHit<Projectile>> projectileHits = new LinkedList();
   private LinkedList<SubmittedHit<ToolItemEvent>> toolItemEventHits = new LinkedList();
   private LinkedList<SubmittedHit<MobAbilityLevelEvent>> mobAbilityLevelEventHits = new LinkedList();

   public ClientSubmittedHits(EntityManager var1) {
      this.manager = var1;
      this.level = var1.level;
   }

   public void tick() {
      while(true) {
         if (!this.projectileHits.isEmpty()) {
            SubmittedHit var1 = (SubmittedHit)this.projectileHits.getFirst();
            if (!var1.isStillValid()) {
               this.projectileHits.removeFirst();
               if (var1.removedHandler != null) {
                  var1.removedHandler.onRemoved(var1.client, var1.attackerUniqueID, (Projectile)var1.attacker, var1.targetUniqueID, var1.target);
               }
               continue;
            }
         }

         return;
      }
   }

   public void submitProjectileHit(ServerClient var1, int var2, int var3, SubmittedHitHandler<Projectile> var4, RemovedHitHandler<Projectile> var5) {
      SubmittedHit var6 = new SubmittedHit(var2, (Projectile)this.manager.projectiles.get(var2, true), var3, var1, var4, var5);
      if (!var6.handleHit()) {
         this.projectileHits.add(var6);
      }

   }

   public void submitNewProjectile(Projectile var1) {
      this.handleNewAttacker(var1, var1.getUniqueID(), this.projectileHits.iterator());
   }

   public void submitToolItemEventHit(ServerClient var1, int var2, int var3, SubmittedHitHandler<ToolItemEvent> var4, RemovedHitHandler<ToolItemEvent> var5) {
      LevelEvent var6 = this.manager.getLevelEvent(var2, true);
      ToolItemEvent var7 = var6 instanceof ToolItemEvent ? (ToolItemEvent)var6 : null;
      SubmittedHit var8 = new SubmittedHit(var2, var7, var3, var1, var4, var5);
      if (!var8.handleHit()) {
         this.toolItemEventHits.add(var8);
      }

   }

   public void submitNewToolItemEvent(ToolItemEvent var1) {
      this.handleNewAttacker(var1, var1.getUniqueID(), this.toolItemEventHits.iterator());
   }

   public void submitMobAbilityLevelEventHit(ServerClient var1, int var2, int var3, SubmittedHitHandler<MobAbilityLevelEvent> var4, RemovedHitHandler<MobAbilityLevelEvent> var5) {
      LevelEvent var6 = this.manager.getLevelEvent(var2, true);
      MobAbilityLevelEvent var7 = var6 instanceof MobAbilityLevelEvent ? (MobAbilityLevelEvent)var6 : null;
      SubmittedHit var8 = new SubmittedHit(var2, var7, var3, var1, var4, var5);
      if (!var8.handleHit()) {
         this.mobAbilityLevelEventHits.add(var8);
      }

   }

   public void submitNewMobAbilityLevelEvent(MobAbilityLevelEvent var1) {
      this.handleNewAttacker(var1, var1.getUniqueID(), this.mobAbilityLevelEventHits.iterator());
   }

   public void submitNewMob(Mob var1) {
      this.handleNewMob(var1, this.projectileHits.iterator());
      this.handleNewMob(var1, this.toolItemEventHits.iterator());
   }

   private <T> void handleNewAttacker(T var1, int var2, Iterator<SubmittedHit<T>> var3) {
      while(var3.hasNext()) {
         SubmittedHit var4 = (SubmittedHit)var3.next();
         if (var4.attackerUniqueID == var2) {
            var4.attacker = var1;
            if (var4.handleHit()) {
               var3.remove();
            }
         }
      }

   }

   private <T> void handleNewMob(Mob var1, Iterator<SubmittedHit<T>> var2) {
      while(var2.hasNext()) {
         SubmittedHit var3 = (SubmittedHit)var2.next();
         if (var3.targetUniqueID == var1.getUniqueID()) {
            var3.target = var1;
            if (var3.handleHit()) {
               var2.remove();
            }
         }
      }

   }

   private class SubmittedHit<T> {
      public final int attackerUniqueID;
      public final int targetUniqueID;
      public final ServerClient client;
      public final long submitTime;
      private final SubmittedHitHandler<T> hitHandler;
      private final RemovedHitHandler<T> removedHandler;
      public T attacker;
      public Mob target;

      public SubmittedHit(int var2, T var3, int var4, ServerClient var5, SubmittedHitHandler<T> var6, RemovedHitHandler<T> var7) {
         this.attackerUniqueID = var2;
         this.attacker = var3;
         this.targetUniqueID = var4;
         this.client = var5;
         this.submitTime = ClientSubmittedHits.this.level.getWorldEntity().getTime();
         this.hitHandler = var6;
         this.removedHandler = var7;
         this.target = GameUtils.getLevelMob(var4, ClientSubmittedHits.this.level);
      }

      public boolean isStillValid() {
         if (this.client.isDisposed()) {
            return false;
         } else {
            return this.submitTime + 2000L >= ClientSubmittedHits.this.level.getWorldEntity().getTime();
         }
      }

      public boolean handleHit() {
         if (this.attacker != null && this.target != null) {
            this.hitHandler.handleHit(this.client, this.attacker, this.target);
            return true;
         } else {
            return false;
         }
      }
   }
}
