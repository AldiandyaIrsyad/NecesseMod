package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.PollinateObject;
import necesse.level.gameObject.PollinateObjectHandler;

public class BeePollinateAINode<T extends HoneyBeeMob> extends MoveTaskAINode<T> {
   public PollinateObjectHandler target;

   public BeePollinateAINode() {
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (this.target != null) {
         this.target.reservable.reserve(var1);
      }

      return super.tick(var1, var2);
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (var1.pollinateTime == 0L) {
         return AINodeResult.FAILURE;
      } else if (var2.mover.isCurrentlyMovingFor(this)) {
         return AINodeResult.SUCCESS;
      } else {
         if (this.target == null && var1.pollinateTime <= var1.getTime()) {
            Point var3 = var1.apiaryHome != null ? var1.apiaryHome : new Point(var1.getTileX(), var1.getTileY());
            GameTileRange var4 = AbstractBeeHiveObjectEntity.pollinateTileRange;
            ArrayList var5 = new ArrayList();
            Point var6 = var1.getPathMoveOffset();
            Iterator var7 = var4.getValidTiles(var3.x, var3.y).iterator();

            while(var7.hasNext()) {
               Point var8 = (Point)var7.next();
               GameObject var9 = var1.getLevel().getObject(var8.x, var8.y);
               if (!var1.getLevel().isSolidTile(var8.x, var8.y) && var9.isSeed && !var1.collidesWith(var1.getLevel(), var8.x * 32 + var6.x, var8.y * 32 + var6.y) && var1.estimateCanMoveTo(var8.x, var8.y, false) && var9 instanceof PollinateObject) {
                  PollinateObjectHandler var10 = ((PollinateObject)var9).getPollinateHandler(var1.getLevel(), var8.x, var8.y);
                  if (var10 != null && var10.canPollinate() && var10.reservable.isAvailable(var1)) {
                     var5.add(var10);
                  }
               }
            }

            if (!var5.isEmpty()) {
               this.target = (PollinateObjectHandler)GameRandom.globalRandom.getOneOf((List)var5);
               this.target.reservable.reserve(var1);
               return this.moveToTileTask(this.target.tileX, this.target.tileY, (BiPredicate)null, (var0) -> {
                  return var0.moveIfWithin(-1, 0, (Runnable)null) ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
               });
            }
         }

         if (this.target != null && this.target.isValid()) {
            if (this.target.canPollinate() && !var1.isPollinating() && GameMath.diagonalMoveDistance(this.target.tileX, this.target.tileY, var1.getTileX(), var1.getTileY()) <= 1.0) {
               var1.pollinateAbility.runAndSend(this.target, 10000);
            }

            if (var1.isPollinating()) {
               return AINodeResult.SUCCESS;
            } else {
               this.target = null;
               var1.pollinateTime = 0L;
               return AINodeResult.SUCCESS;
            }
         } else {
            this.target = null;
            return AINodeResult.FAILURE;
         }
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((HoneyBeeMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tick(Mob var1, Blackboard var2) {
      return this.tick((HoneyBeeMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void init(Mob var1, Blackboard var2) {
      this.init((HoneyBeeMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (HoneyBeeMob)var2, var3);
   }
}
