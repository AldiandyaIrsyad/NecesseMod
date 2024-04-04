package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.DecoratorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.SettlerWeaponItem;

public class HumanChaserAINode<T extends HumanMob> extends DecoratorAINode<T> {
   public InventoryItem lastWeaponItem;
   public InventoryItem defaultWeaponItem;

   public HumanChaserAINode(InventoryItem var1) {
      super((AINode)null);
      this.defaultWeaponItem = var1;
   }

   public HumanChaserAINode() {
      this(new InventoryItem("woodsword"));
   }

   public void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      super.onRootSet(var1, var2, var3);
      this.setChild(this.getWeaponAI(var2));
   }

   public AINodeResult tickChild(AINode<T> var1, T var2, Blackboard<T> var3) {
      boolean var4 = false;
      InventoryItem var5 = var2.equipmentInventory.getItem(6);
      if (var5 != null) {
         if (this.lastWeaponItem != null && var5.item == this.lastWeaponItem.item) {
            if (var5.item instanceof SettlerWeaponItem && ((SettlerWeaponItem)var5.item).shouldUpdateSettlerChaserAI(var2, this.lastWeaponItem, var5)) {
               var4 = true;
            }
         } else {
            var4 = true;
         }
      } else if (this.lastWeaponItem != null) {
         var4 = true;
      }

      if (var4) {
         this.lastWeaponItem = var5;
         var1 = this.getWeaponAI(var2);
         this.setChild(var1);
      }

      return var1 == null ? AINodeResult.FAILURE : var1.tick(var2, var3);
   }

   public AINode<T> getWeaponAI(T var1) {
      if (this.lastWeaponItem != null && this.lastWeaponItem.item instanceof SettlerWeaponItem) {
         return ((SettlerWeaponItem)this.lastWeaponItem.item).getSettlerWeaponChaserAI(var1, this.lastWeaponItem, true);
      } else {
         return (AINode)(this.defaultWeaponItem != null && this.defaultWeaponItem.item instanceof SettlerWeaponItem ? ((SettlerWeaponItem)this.defaultWeaponItem.item).getSettlerWeaponChaserAI(var1, this.defaultWeaponItem, false) : new ChaserAINode<T>(50, false, false) {
            public boolean isTargetWithinAttackRange(T var1, Mob var2, float var3) {
               return var3 >= (float)this.minimumAttackDistance && isTargetHitboxWithinRange(var1, var1.x, var1.y, var2, (float)Math.max(this.attackDistance - 20, 10));
            }

            public ChaserAINode.ChaseDirection getDirectChaseDirection(T var1, Mob var2, float var3, boolean var4) {
               return var4 ? ChaserAINode.ChaseDirection.STAY : super.getDirectChaseDirection(var1, var2, var3, var4);
            }

            public boolean attackTarget(T var1, Mob var2) {
               if (var1.canAttack()) {
                  var1.attackItem(var2.getX(), var2.getY(), new InventoryItem("woodsword"));
                  GameDamage var3 = new GameDamage(DamageTypeRegistry.MELEE, 10.0F);
                  var2.isServerHit(var3, var2.x - var1.x, var2.y - var1.y, 75.0F, var1);
                  return true;
               } else {
                  return false;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean attackTarget(Mob var1, Mob var2) {
               return this.attackTarget((HumanMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public ChaserAINode.ChaseDirection getDirectChaseDirection(Mob var1, Mob var2, float var3, boolean var4) {
               return this.getDirectChaseDirection((HumanMob)var1, var2, var3, var4);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean isTargetWithinAttackRange(Mob var1, Mob var2, float var3) {
               return this.isTargetWithinAttackRange((HumanMob)var1, var2, var3);
            }
         });
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickChild(AINode var1, Mob var2, Blackboard var3) {
      return this.tickChild(var1, (HumanMob)var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (HumanMob)var2, var3);
   }
}
