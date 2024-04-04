package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanCommandMoveToAINode<T extends HumanMob> extends CommandMoveToAINode<T> {
   public boolean isBeforeChaser;

   public HumanCommandMoveToAINode(boolean var1) {
      this.isBeforeChaser = var1;
   }

   public Point getLevelPosition(T var1) {
      if (var1.isHiding && var1.isSettlerOnCurrentLevel()) {
         return null;
      } else if (this.isBeforeChaser && !var1.commandMoveToGuardPoint) {
         return null;
      } else if (var1.commandGuardPoint != null) {
         if (var1.objectUser != null) {
            var1.objectUser.stopUsing();
         }

         return var1.commandGuardPoint;
      } else {
         return null;
      }
   }

   public void onArrived(T var1) {
      var1.commandMoveToGuardPoint = false;
   }

   public void tickMoving(T var1) {
      var1.resetCommandsBuffer = Math.max(180000, var1.resetCommandsBuffer);
   }

   public void tickStandingStill(T var1) {
   }

   public void onCannotMoveTo(T var1, int var2, int var3) {
      Point var4 = findClosestMoveToTile(var1, var2, var3);
      if (var4 != null) {
         var1.commandGuardPoint = new Point(var4.x * 32 + 16, var4.y * 32 + 16);
         this.nextPathFindTime = 0L;
         this.nextCheckArrivedTime = 0L;
      } else {
         var1.commandGuardPoint = new Point(var1.getX(), var1.getY());
      }

   }

   public void updateActivityDescription(T var1, GameMessage var2) {
      if (var2 == null) {
         var1.clearActivity("command");
      } else {
         var1.setActivity("command", 15000, var2);
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public void updateActivityDescription(Mob var1, GameMessage var2) {
      this.updateActivityDescription((HumanMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void onCannotMoveTo(Mob var1, int var2, int var3) {
      this.onCannotMoveTo((HumanMob)var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void tickStandingStill(Mob var1) {
      this.tickStandingStill((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void tickMoving(Mob var1) {
      this.tickMoving((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void onArrived(Mob var1) {
      this.onArrived((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Point getLevelPosition(Mob var1) {
      return this.getLevelPosition((HumanMob)var1);
   }
}
