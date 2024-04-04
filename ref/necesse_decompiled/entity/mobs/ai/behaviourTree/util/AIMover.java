package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.path.FinalPath;
import necesse.entity.mobs.ai.path.FinalPathPoint;
import necesse.entity.mobs.ai.path.PathOptions;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class AIMover {
   protected AINode<?> callerNode;
   protected FinalPath path;
   protected Runnable pathInvalidated;
   protected float targetX;
   protected float targetY;
   protected Mob targetMob;
   protected boolean targetStopWhenColliding;
   protected MobMovement custom;
   public int defaultMaxPathIterations = 1000;
   protected float lastX;
   protected float lastY;
   protected int stuck;

   public AIMover() {
   }

   public AIMover(int var1) {
      this.defaultMaxPathIterations = var1;
   }

   public boolean tick(Mob var1) {
      if (this.callerNode == null) {
         return false;
      } else if (this.custom != null) {
         var1.setMovement(this.custom);
         return true;
      } else if (this.targetMob != null) {
         var1.setMovement(new MobMovementRelative(this.targetMob, this.targetStopWhenColliding));
         return true;
      } else {
         return this.tickPath(var1);
      }
   }

   public void resetStuck() {
      this.stuck = 0;
   }

   private boolean tickPath(Mob var1) {
      this.updatePath(var1);
      if (this.targetX != 0.0F && this.targetY != 0.0F) {
         var1.setMovement(new MobMovementLevelPos(this.targetX, this.targetY));
         if (Math.abs(this.lastX - var1.x) < 0.1F && Math.abs(this.lastY - var1.y) < 0.1F) {
            ++this.stuck;
            if (this.stuck > 40) {
               this.stopMoving(var1);
               this.callerNode = null;
            }
         }

         this.lastX = var1.x;
         this.lastY = var1.y;
         if (var1.hasArrivedAtTarget()) {
            this.stuck = 0;
            this.targetX = 0.0F;
            this.targetY = 0.0F;
            if (this.path != null) {
               if (this.path.size() != 0) {
                  this.path.removeFirst();
               } else {
                  this.path = null;
               }

               return this.tickPath(var1);
            }
         }

         return true;
      } else {
         this.stuck = 0;
         var1.stopMoving();
         return false;
      }
   }

   private void updatePath(Mob var1) {
      if (this.path != null) {
         if (this.path.size() == 0) {
            this.path = null;
         } else {
            Point var2 = var1.getPathMoveOffset();
            FinalPathPoint var3 = this.path.getFirst();
            if (this.pathInvalidated != null && !(Boolean)var3.checkValid.get()) {
               this.pathInvalidated.run();
               this.path = null;
               this.targetX = 0.0F;
               this.targetY = 0.0F;
            } else {
               this.targetX = (float)(var3.x * 32 + var2.x);
               this.targetY = (float)(var3.y * 32 + var2.y);
            }
         }
      }

   }

   public boolean isCurrentlyMovingFor(AINode<?> var1) {
      return this.isMoving() && this.callerNode == var1;
   }

   public AINode<?> getMovingFor() {
      return this.callerNode;
   }

   public boolean hasMovingNode() {
      return this.callerNode != null;
   }

   public void setMovingFor(AINode<?> var1) {
      this.callerNode = var1;
   }

   public boolean isMoving() {
      return this.custom != null || this.targetMob != null || this.targetX != 0.0F && this.targetY != 0.0F || this.path != null;
   }

   public boolean hasMobTarget() {
      return this.targetMob != null;
   }

   public Mob getTargetMob() {
      return this.targetMob;
   }

   protected void resetMoving() {
      this.custom = null;
      this.path = null;
      this.targetMob = null;
      this.targetX = 0.0F;
      this.targetY = 0.0F;
   }

   public void stopMoving(Mob var1) {
      this.resetMoving();
      this.callerNode = null;
      var1.stopMoving();
   }

   public void setCustomMovement(AINode<?> var1, MobMovement var2) {
      this.resetMoving();
      this.callerNode = var1;
      this.custom = var2;
   }

   public void setMobTarget(AINode<?> var1, Mob var2, boolean var3) {
      this.resetMoving();
      this.callerNode = var1;
      this.targetMob = var2;
      this.targetStopWhenColliding = var3;
   }

   public void setMobTarget(AINode<?> var1, Mob var2) {
      this.setMobTarget(var1, var2, false);
   }

   public void directMoveTo(AINode<?> var1, int var2, int var3) {
      this.resetMoving();
      this.callerNode = var1;
      this.targetX = (float)var2;
      this.targetY = (float)var3;
   }

   public void setPath(AINode<?> var1, FinalPath var2, Runnable var3) {
      this.resetMoving();
      this.callerNode = var1;
      if (this.path != var2) {
         this.path = var2;
         if (this.path.size() > 1) {
            this.path.removeFirst();
         }
      }

      this.pathInvalidated = var3;
      this.updatePath(var1.mob());
   }

   public MoveToTileAITask moveToTileTaskBare(AINode<?> var1, int var2, int var3, BiPredicate<Point, Point> var4, int var5, Function<MoveToTileAITask.AIPathResult, AINodeResult> var6) {
      return MoveToTileAITask.pathToTile(this, var1, var2, var3, var4, var5, this.getPathOptions(var1), var6);
   }

   public MoveToTileAITask moveToTileTask(AINode<?> var1, int var2, int var3, BiPredicate<Point, Point> var4, int var5, Function<MoveToTileAITask.AIPathResult, AINodeResult> var6) {
      MoveToTileAITask var7 = this.moveToTileTaskBare(var1, var2, var3, var4, var5, var6);
      var7.runConcurrently();
      return var7;
   }

   public final MoveToTileAITask moveToTileTask(AINode<?> var1, int var2, int var3, BiPredicate<Point, Point> var4, Function<MoveToTileAITask.AIPathResult, AINodeResult> var5) {
      return this.moveToTileTask(var1, var2, var3, var4, this.defaultMaxPathIterations, var5);
   }

   public PathOptions getPathOptions(AINode<?> var1) {
      Blackboard var2 = var1.getBlackboard();
      PathOptions var3 = new PathOptions();
      if (var2 != null) {
         var3 = (PathOptions)var2.getObject(PathOptions.class, "pathOptions", var3);
      }

      return var3;
   }

   public boolean hasPath() {
      return this.path != null;
   }

   public Point getCurrentDestination() {
      return this.path != null ? this.path.getFirst() : null;
   }

   public boolean isCurrentDestination(int var1, int var2) {
      Point var3 = this.getCurrentDestination();
      return var3 != null && var3.x == var1 && var3.y == var2;
   }

   public boolean hasDestinationInPath(int var1, int var2) {
      return this.path == null ? false : this.path.streamPathPoints().anyMatch((var2x) -> {
         return var2x.x == var1 && var2x.y == var2;
      });
   }

   public Point getFinalDestination() {
      return this.path != null ? this.path.getLast() : null;
   }

   public boolean isFinalDestination(int var1, int var2) {
      Point var3 = this.getFinalDestination();
      return var3 != null && var3.x == var1 && var3.y == var2;
   }

   public FinalPath getPath() {
      return this.path;
   }
}
