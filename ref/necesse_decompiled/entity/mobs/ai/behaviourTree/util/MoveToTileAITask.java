package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.engine.DisposableExecutorService;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.pathfinding.PathResult;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.path.FinalPath;
import necesse.entity.mobs.ai.path.FinalPathPoint;
import necesse.entity.mobs.ai.path.PathOptions;
import necesse.entity.mobs.ai.path.TilePathfinding;

public class MoveToTileAITask extends FutureAITask<AIPathResult> {
   private MoveToTileAITask(Callable<AIPathResult> var1, Function<AIPathResult, AINodeResult> var2) {
      super((DisposableExecutorService)null, var1, var2);
   }

   private MoveToTileAITask(TickManager var1, AIMover var2, AINode<?> var3, int var4, int var5, BiPredicate<Point, Point> var6, int var7, PathOptions var8, Function<AIPathResult, AINodeResult> var9) {
      super(var3.mob().getLevel(), () -> {
         PathResult var8x = TilePathfinding.findPath(var1, var3.mob(), var4, var5, var8, var6, var7);
         return new AIPathResult(var2, var3, var8x);
      }, var9);
   }

   public static MoveToTileAITask pathToTile(AIMover var0, AINode<?> var1, int var2, int var3, BiPredicate<Point, Point> var4, int var5, PathOptions var6, Function<AIPathResult, AINodeResult> var7) {
      TickManager var8 = var1.mob().getLevel().tickManager();
      return new MoveToTileAITask(var8 == null ? null : var8.getChild(), var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static MoveToTileAITask directMoveToTile(AIMover var0, AINode<?> var1, int var2, int var3, Function<AIPathResult, AINodeResult> var4) {
      return new MoveToTileAITask(() -> {
         return new DirectAIPathResult(var0, var1, var2, var3);
      }, var4);
   }

   private static class DirectAIPathResult extends AIPathResult {
      private final int tileX;
      private final int tileY;

      public DirectAIPathResult(AIMover var1, AINode<?> var2, int var3, int var4) {
         super(var1, var2, (PathResult)null, null);
         this.tileX = var3;
         this.tileY = var4;
      }

      public boolean isMobWithinStart(int var1) {
         return true;
      }

      public boolean isResultWithin(int var1) {
         return true;
      }

      public FinalPath getFinalPath() {
         if (this.finalPath == null) {
            Mob var1 = this.node.mob();
            List var2 = Arrays.asList(new FinalPathPoint(var1.getTileX(), var1.getTileY(), () -> {
               return true;
            }), new FinalPathPoint(this.tileX, this.tileY, () -> {
               return true;
            }));
            this.finalPath = new FinalPath(new ArrayList(var2));
         }

         return this.finalPath;
      }

      public float getFullPathLength() {
         return this.node.mob().getDistance((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16));
      }

      public float getCurrentPathLength() {
         return this.node.mob().getDistance((float)(this.tileX * 32 + 16), (float)(this.tileY * 32 + 16));
      }

      public boolean moveIfWithin(int var1, int var2, Runnable var3) {
         this.move(var3);
         return true;
      }

      public void move(Runnable var1) {
         this.mover.directMoveTo(this.node, this.tileX * 32 + 16, this.tileY * 32 + 16);
      }
   }

   public static class AIPathResult {
      protected final AIMover mover;
      protected final AINode<?> node;
      public final PathResult<Point, TilePathfinding> result;
      protected FinalPath finalPath;

      private AIPathResult(AIMover var1, AINode<?> var2, PathResult<Point, TilePathfinding> var3) {
         this.mover = var1;
         this.node = var2;
         this.result = var3;
      }

      public boolean isMobWithinStart(int var1) {
         if (this.result.start == null) {
            return false;
         } else if (var1 < 0) {
            return true;
         } else {
            return GameMath.squareDistance((float)((Point)this.result.start).x, (float)((Point)this.result.start).y, (float)(this.node.mob().getX() + ((TilePathfinding)this.result.finder).moveOffsetX) / 32.0F, (float)(this.node.mob().getY() + ((TilePathfinding)this.result.finder).moveOffsetY) / 32.0F) <= (float)var1;
         }
      }

      public boolean isResultWithin(int var1) {
         if (this.result.target == null) {
            return false;
         } else {
            return var1 < 0 ? true : TilePathfinding.isResultWithin(this.result, ((Point)this.result.target).x, ((Point)this.result.target).y, var1);
         }
      }

      public FinalPath getFinalPath() {
         if (this.finalPath == null) {
            ArrayList var1 = TilePathfinding.reducePathPoints((TilePathfinding)this.result.finder, this.result.path);
            this.finalPath = new FinalPath(var1);
         }

         return this.finalPath;
      }

      public float getFullPathLength() {
         return this.getFinalPath().getFullLength();
      }

      public float getCurrentPathLength() {
         return this.getFinalPath().getCurrentLength();
      }

      public float estimateMillisToFullPathWithSpeed(float var1) {
         return Entity.getTravelTimeMillis(var1, this.getFullPathLength());
      }

      public int getNextPathTimeBasedOnPathTime(float var1, float var2, int var3, float var4) {
         float var5 = this.estimateMillisToFullPathWithSpeed(var1);
         int var6 = (int)(var5 / var2);
         int var7 = (int)((float)var6 * var4);
         return Math.max(var3, GameRandom.globalRandom.getIntOffset(var6, var7));
      }

      public boolean moveIfWithin(int var1, int var2, Runnable var3) {
         if (this.isMobWithinStart(var1) && this.isResultWithin(var2)) {
            this.move(var3);
            return true;
         } else {
            return false;
         }
      }

      public void move(Runnable var1) {
         this.mover.setPath(this.node, this.getFinalPath(), var1);
      }

      // $FF: synthetic method
      AIPathResult(AIMover var1, AINode var2, PathResult var3, Object var4) {
         this(var1, var2, var3);
      }
   }
}
