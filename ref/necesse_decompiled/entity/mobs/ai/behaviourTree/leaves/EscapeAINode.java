package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.function.BiPredicate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.level.maps.Level;

public abstract class EscapeAINode<T extends Mob> extends MoveTaskAINode<T> {
   public long nextPathFind;
   public Point escapePoint;

   public EscapeAINode() {
   }

   public abstract boolean shouldEscape(T var1, Blackboard<T> var2);

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      if (this.shouldEscape(var1, var2)) {
         if (!(new Rectangle(160, 160, var1.getLevel().width * 32 - 320, var1.getLevel().height * 32 - 320)).contains(var1.getPositionPoint())) {
            var1.remove();
         }

         if (this.escapePoint == null) {
            ServerClient var3 = (ServerClient)GameUtils.streamServerClients(var1.getLevel()).min(Comparator.comparing((var1x) -> {
               return var1.getDistance(var1x.playerMob);
            })).orElse((Object)null);
            Level var4 = var1.getLevel();
            if (var3 != null) {
               float var5 = var1.x - var3.playerMob.x;
               float var6 = var1.y - var3.playerMob.y;
               Point2D.Float var7 = GameMath.normalize(var5, var6);
               Rectangle var8 = new Rectangle(2, 2, var4.width - 4, var4.height - 4);
               Line2D.Float var9 = new Line2D.Float(var1.x / 32.0F, var1.y / 32.0F, var1.x / 32.0F + var7.x * (float)var8.width * 4.0F, var1.y / 32.0F + var7.y * (float)var8.height * 4.0F);
               IntersectionPoint var10 = GameMath.getIntersectionPoint((Object)null, var9, var8, true);
               if (var10 != null) {
                  this.escapePoint = new Point((int)var10.x, (int)var10.y);
               }
            }

            if (this.escapePoint == null) {
               this.escapePoint = (Point)GameRandom.globalRandom.getOneOf((Object[])(new Point(GameRandom.globalRandom.getIntBetween(2, var4.width - 2), 2), new Point(GameRandom.globalRandom.getIntBetween(2, var4.width - 2), var4.height - 2), new Point(2, GameRandom.globalRandom.getIntBetween(2, var4.height - 2)), new Point(var4.width - 2, GameRandom.globalRandom.getIntBetween(2, var4.height - 2))));
            }
         }

         if (this.nextPathFind < var1.getWorldEntity().getLocalTime()) {
            this.nextPathFind = var1.getWorldEntity().getLocalTime() + 1000L;
            return this.moveToTileTask(this.escapePoint.x, this.escapePoint.y, (BiPredicate)null, (var2x) -> {
               if (var2x.moveIfWithin(-1, -1, () -> {
                  this.nextPathFind = 0L;
               })) {
                  int var3 = var2x.getNextPathTimeBasedOnPathTime(var1.getSpeed(), 1.5F, 2000, 0.1F);
                  this.nextPathFind = var1.getWorldEntity().getLocalTime() + (long)var3;
               }

               return AINodeResult.SUCCESS;
            });
         } else {
            return AINodeResult.SUCCESS;
         }
      } else {
         return AINodeResult.FAILURE;
      }
   }
}
