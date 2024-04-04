package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.MigrateToApiaryAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.friendly.QueenBeeMob;

public class QueenBeeAI<T extends QueenBeeMob> extends SelectorAINode<T> {
   public final MigrateToApiaryAINode<T> migrateToApiaryAINode;
   public final WandererAINode<T> wandererAINode;

   public QueenBeeAI(int var1) {
      this.addChild(this.migrateToApiaryAINode = new MigrateToApiaryAINode());
      this.addChild(this.wandererAINode = new WandererAINode<T>(var1) {
         public Point getBase(T var1) {
            return var1.migrationApiary;
         }

         public int getBaseRadius(T var1) {
            return 5;
         }

         public boolean forceFindAroundBase(T var1) {
            return var1.migrationApiary != null;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean forceFindAroundBase(Mob var1) {
            return this.forceFindAroundBase((QueenBeeMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public int getBaseRadius(Mob var1) {
            return this.getBaseRadius((QueenBeeMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Point getBase(Mob var1) {
            return this.getBase((QueenBeeMob)var1);
         }
      });
   }
}
