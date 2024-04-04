package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanCommandAttackTargetterAINode<T extends HumanMob> extends CommandAttackTargetterAINode<T> {
   public HumanCommandAttackTargetterAINode() {
   }

   public Mob getTarget(T var1) {
      return var1.commandAttackMob;
   }

   public void resetTarget(T var1) {
      var1.commandAttackMob = null;
   }

   public void tickTargetSet(T var1, Mob var2) {
      if (var1.objectUser != null) {
         var1.objectUser.stopUsing();
      }

      if (var1.commandGuardPoint != null) {
         var1.commandGuardPoint = new Point(var2.getX(), var2.getY());
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public void tickTargetSet(Mob var1, Mob var2) {
      this.tickTargetSet((HumanMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void resetTarget(Mob var1) {
      this.resetTarget((HumanMob)var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Mob getTarget(Mob var1) {
      return this.getTarget((HumanMob)var1);
   }
}
