package necesse.inventory.item;

import java.awt.geom.Line2D;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public interface SettlerWeaponItem {
   default AINode<HumanMob> getSettlerWeaponChaserAI(HumanMob var1, final InventoryItem var2, final boolean var3) {
      final AtomicReference var4 = new AtomicReference(var2);
      return new ChaserAINode<HumanMob>(50, false, false) {
         public AINodeResult tickNode(HumanMob var1, Blackboard<HumanMob> var2x) {
            if (var3) {
               Inventory var3x = var1.getInventory();
               InventoryItem var4x = var3x.getItem(6);
               if (var4x != null && var4x.item == SettlerWeaponItem.this) {
                  var4.set(var4x);
                  this.attackDistance = SettlerWeaponItem.this.getSettlerAttackRange(var1, var4x);
                  this.stoppingDistance = this.attackDistance - 20;
                  this.minimumAttackDistance = SettlerWeaponItem.this.getSettlerMinimumAttackRAnge(var1, var4x);
                  return super.tickNode(var1, var2x);
               } else {
                  return AINodeResult.FAILURE;
               }
            } else {
               this.attackDistance = SettlerWeaponItem.this.getSettlerAttackRange(var1, var2);
               this.stoppingDistance = this.attackDistance - 20;
               return super.tickNode(var1, var2x);
            }
         }

         public boolean isTargetWithinAttackRange(HumanMob var1, Mob var2x, float var3x) {
            return var3x >= (float)this.minimumAttackDistance && isTargetHitboxWithinRange(var1, var1.x, var1.y, var2x, (float)Math.max(this.attackDistance - 20, 10));
         }

         public ChaserAINode.ChaseDirection getDirectChaseDirection(HumanMob var1, Mob var2x, float var3x, boolean var4x) {
            return var4x ? ChaserAINode.ChaseDirection.STAY : super.getDirectChaseDirection(var1, var2x, var3x, var4x);
         }

         public boolean canHitTarget(HumanMob var1, float var2x, float var3x, Mob var4x) {
            return SettlerWeaponItem.this.canSettlerHitTarget(var1, var2x, var3x, var4x, (InventoryItem)var4.get());
         }

         public boolean attackTarget(HumanMob var1, Mob var2x) {
            if (var1.canAttack()) {
               int var3x = Item.getRandomAttackSeed(GameRandom.globalRandom);
               if (var3) {
                  Inventory var4x = var1.getInventory();
                  var4x.setItem(6, SettlerWeaponItem.this.onSettlerAttack(var1.getLevel(), var1, var2x, var1.getCurrentAttackHeight(), var3x, (InventoryItem)var4.get()));
               } else {
                  SettlerWeaponItem.this.onSettlerAttack(var1.getLevel(), var1, var2x, var1.getCurrentAttackHeight(), var3x, (InventoryItem)var4.get());
               }

               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1, Mob var2x) {
            return this.attackTarget((HumanMob)var1, var2x);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean canHitTarget(Mob var1, float var2x, float var3x, Mob var4x) {
            return this.canHitTarget((HumanMob)var1, var2x, var3x, var4x);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public ChaserAINode.ChaseDirection getDirectChaseDirection(Mob var1, Mob var2x, float var3x, boolean var4x) {
            return this.getDirectChaseDirection((HumanMob)var1, var2x, var3x, var4x);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isTargetWithinAttackRange(Mob var1, Mob var2x, float var3x) {
            return this.isTargetWithinAttackRange((HumanMob)var1, var2x, var3x);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tickNode(Mob var1, Blackboard var2x) {
            return this.tickNode((HumanMob)var1, var2x);
         }
      };
   }

   default GameMessage getSettlerCanUseError(HumanMob var1, InventoryItem var2) {
      return null;
   }

   default boolean shouldUpdateSettlerChaserAI(HumanMob var1, InventoryItem var2, InventoryItem var3) {
      return false;
   }

   default int getSettlerAttackRange(HumanMob var1, InventoryItem var2) {
      return this instanceof ToolItem ? ((ToolItem)this).getAttackRange(var2) : 50;
   }

   default float getSettlerWeaponValue(HumanMob var1, InventoryItem var2) {
      if (this instanceof ToolItem) {
         ToolItem var3 = (ToolItem)this;
         ToolItemEnchantment var4 = var3.getEnchantment(var2);
         return (float)var3.getEnchantCost(var2) * (var4 == null ? 1.0F : var4.getEnchantCostMod());
      } else {
         return 0.0F;
      }
   }

   default int getSettlerMinimumAttackRAnge(HumanMob var1, InventoryItem var2) {
      return 0;
   }

   default boolean canSettlerHitTarget(HumanMob var1, float var2, float var3, Mob var4, InventoryItem var5) {
      return !var1.getLevel().collides((Line2D)(new Line2D.Float(var2, var3, var4.x, var4.y)), (CollisionFilter)(new CollisionFilter()).projectileCollision());
   }

   InventoryItem onSettlerAttack(Level var1, HumanMob var2, Mob var3, int var4, int var5, InventoryItem var6);
}
