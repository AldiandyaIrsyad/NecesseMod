package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.function.BiPredicate;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class CritterRunAINode<T extends CritterMob> extends MoveTaskAINode<T> {
   public boolean runsFromPlayers = true;
   public int runDistance = 150;
   public long nextRunCooldown;

   public CritterRunAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (var1.isRunning() && !var1.getLevel().isCave && !(new Rectangle(96, 96, (var1.getLevel().width - 6) * 32, (var1.getLevel().height - 6) * 32)).contains(var1.getCollision())) {
         var1.remove();
         return AINodeResult.SUCCESS;
      } else {
         if (var1.isRunning() && !var2.mover.isMoving()) {
            var1.setRunning(false);
         }

         Iterator var3 = var2.getLastHits().iterator();

         while(var3.hasNext()) {
            AIWasHitEvent var4 = (AIWasHitEvent)var3.next();
            Mob var5 = var4.event.attacker != null ? var4.event.attacker.getAttackOwner() : null;
            AINodeResult var6;
            if (var5 != null) {
               var6 = this.startRun(var1, var1.getTileX(), var1.getTileY(), var5);
               if (var6 != null) {
                  return var6;
               }
            } else {
               var6 = this.startRun(var1, var1.getTileX(), var1.getTileY(), var4.event.knockbackX, var4.event.knockbackY, (ZoneTester)null);
               if (var6 != null) {
                  return var6;
               }
            }
         }

         if (this.runsFromPlayers && this.nextRunCooldown < var1.getWorldEntity().getTime()) {
            TempDistance var7 = (TempDistance)var1.getLevel().entityManager.players.getInRegionByTileRange(var1.getTileX(), var1.getTileY(), this.runDistance / 32 + 2).stream().map((var1x) -> {
               return new TempDistance(var1x, var1);
            }).min((var0, var1x) -> {
               return Float.compare(var0.distance, var1x.distance);
            }).orElse((Object)null);
            if (var7 != null && var7.distance <= (float)this.runDistance) {
               AINodeResult var8 = this.startRun(var1, var7.player.getTileX(), var7.player.getTileY(), var7.player);
               if (var8 != null) {
                  return var8;
               }
            }
         }

         return AINodeResult.FAILURE;
      }
   }

   public AINodeResult startRun(T var1, int var2, int var3, float var4, float var5, ZoneTester var6) {
      int var7 = (int)Math.ceil((double)((float)this.runDistance / 32.0F));
      Point2D.Float var8 = GameMath.normalize(var4, var5);
      float var9;
      if (Math.abs(var8.x) > Math.abs(var8.y)) {
         var9 = 1.0F / Math.abs(var8.x);
      } else {
         var9 = 1.0F / Math.abs(var8.y);
      }

      var8.x *= var9;
      var8.y *= var9;
      int var10 = 8 + var7;
      Point var11 = WandererAINode.findWanderingPointAround(var1, var2 + (int)(var8.x * (float)var10), var3 + (int)(var8.y * (float)var10), var10, var6, 20, 5);
      this.nextRunCooldown = var1.getWorldEntity().getTime() + 2000L;
      if (var11 != null) {
         return this.moveToTileTask(var11.x, var11.y, (BiPredicate)null, (var1x) -> {
            var1.setRunning(var1x.moveIfWithin(-1, -1, (Runnable)null));
            return AINodeResult.FAILURE;
         });
      } else {
         var11 = WandererAINode.findWanderingPointAround(var1, var2, var3, var10 * 2, var6, 20, 5);
         return var11 != null ? this.moveToTileTask(var11.x, var11.y, (BiPredicate)null, (var1x) -> {
            var1.setRunning(var1x.moveIfWithin(-1, -1, (Runnable)null));
            return AINodeResult.FAILURE;
         }) : null;
      }
   }

   public AINodeResult startRun(T var1, int var2, int var3, float var4, float var5, Mob var6) {
      int var7 = (int)Math.ceil((double)((float)this.runDistance / 32.0F));
      ZoneTester var8 = (var2x, var3x) -> {
         double var4 = GameMath.diagonalMoveDistance(var2x, var3x, var6.getTileX(), var6.getTileY());
         return var4 >= (double)(var7 + 2);
      };
      return this.startRun(var1, var2, var3, var4, var5, var8);
   }

   public AINodeResult startRun(T var1, int var2, int var3, Mob var4) {
      return this.startRun(var1, var2, var3, var1.x - var4.x, var1.y - var4.y, var4);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((CritterMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void init(Mob var1, Blackboard var2) {
      this.init((CritterMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (CritterMob)var2, var3);
   }

   protected static class TempDistance {
      public final PlayerMob player;
      public final float distance;

      public TempDistance(PlayerMob var1, Mob var2) {
         this.player = var1;
         this.distance = var1.getDistance(var2);
      }
   }
}
