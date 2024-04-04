package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.BeePollinateAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowMobAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ReturnToApiaryAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.friendly.HoneyBeeMob;

public class HoneyBeeAI<T extends HoneyBeeMob> extends SelectorAINode<T> {
   public final FollowMobAINode<T> followQueenAINode = new FollowMobAINode<T>() {
      public Mob getFollowingMob(T var1) {
         return var1.followingQueen.get(var1.getLevel());
      }

      // $FF: synthetic method
      // $FF: bridge method
      public Mob getFollowingMob(Mob var1) {
         return this.getFollowingMob((HoneyBeeMob)var1);
      }
   };
   public final ReturnToApiaryAINode<T> returnToApiaryAINode;
   public final BeePollinateAINode<T> pollinateAINode;
   public final WandererAINode<T> wandererAINode;

   public HoneyBeeAI(int var1) {
      this.followQueenAINode.tileRadius = 1;
      this.followQueenAINode.minChangePositionCooldown = 500;
      this.followQueenAINode.maxChangePositionCooldown = 1000;
      this.addChild(this.followQueenAINode);
      this.addChild(this.returnToApiaryAINode = new ReturnToApiaryAINode());
      this.addChild(this.pollinateAINode = new BeePollinateAINode());
      this.addChild(this.wandererAINode = new WandererAINode<T>(var1) {
         public Point getBase(T var1) {
            return var1.apiaryHome;
         }

         public int getBaseRadius(T var1) {
            return 15;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public int getBaseRadius(Mob var1) {
            return this.getBaseRadius((HoneyBeeMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Point getBase(Mob var1) {
            return this.getBase((HoneyBeeMob)var1);
         }
      });
   }
}
