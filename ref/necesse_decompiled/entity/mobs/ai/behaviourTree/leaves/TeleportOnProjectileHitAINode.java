package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.projectile.Projectile;

public abstract class TeleportOnProjectileHitAINode<T extends Mob> extends AINode<T> {
   protected boolean isInvulnerable;
   public int radius;
   public int cooldown;
   public long next;

   public TeleportOnProjectileHitAINode(int var1, int var2) {
      this.cooldown = var1;
      this.radius = var2;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onGlobalTick((var1x) -> {
         this.isInvulnerable = false;
      });
      var3.onBeforeHit((var3x) -> {
         if (!var2.isClient()) {
            if (this.isInvulnerable || var3x.event.attacker instanceof Projectile && this.next <= var2.getWorldEntity().getTime() && this.findNewPosition(var2)) {
               this.isInvulnerable = true;
               var3x.event.prevent();
               var3x.event.showDamageTip = false;
               var3x.event.playHitSound = false;
               this.next = var2.getWorldEntity().getTime() + (long)this.cooldown;
               var3.submitEvent("resetPathTime", new AIEvent());
            }

         }
      });
   }

   public boolean findNewPosition(T var1) {
      int var2 = var1.getX() / 32;
      int var3 = var1.getY() / 32;
      Point var4 = var1.getPathMoveOffset();
      ArrayList var5 = new ArrayList();

      int var6;
      for(var6 = var2 - this.radius; var6 <= var2 + this.radius; ++var6) {
         for(int var7 = var3 - this.radius; var7 <= var3 + this.radius; ++var7) {
            int var8 = var6 * 32 + var4.x;
            int var9 = var7 * 32 + var4.y;
            if (!var1.collidesWith(var1.getLevel(), var8, var9)) {
               var5.add(new Point(var8, var9));
            }
         }
      }

      Point var10;
      do {
         if (var5.isEmpty()) {
            return false;
         }

         var6 = GameRandom.globalRandom.nextInt(var5.size());
         var10 = (Point)var5.get(var6);
      } while(!this.teleport(var1, var10.x, var10.y));

      return true;
   }

   public abstract boolean teleport(T var1, int var2, int var3);

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      return AINodeResult.SUCCESS;
   }
}
