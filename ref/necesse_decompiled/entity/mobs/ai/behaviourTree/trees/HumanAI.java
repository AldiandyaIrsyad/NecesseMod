package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanCommandFollowMobAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanCommandMoveToAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanDoJobAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanInteractingAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanJobMoveToAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanJobSearchingAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanMoveToAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanSleepAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanWanderHomeLowHealthAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WanderHomeAtConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.SettlerMission;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class HumanAI<T extends HumanMob> extends SelectorAINode<T> {
   public final HumanMoveToAINode<T> humanMoveToAINode;
   public final HumanInteractingAINode<T> humanInteractingAINode;
   public final HumanWanderHomeLowHealthAINode<T> wanderHomeLowHealthAINode;
   public final HumanTargetFinderAI<T> humanTargetFinderAI;
   public final HumanChaserAINode<T> chaserAINode;
   public final HumanJobSearchingAINode<T> humanJobSearchingAINode;
   public final HumanJobMoveToAINode<T> humanJobMoveToAINode;
   public final HumanDoJobAINode<T> humanDoJobAINode;
   public final WanderHomeAtConditionAINode<T> wanderHomeAtRaidAINode;
   public final WandererAINode<T> wandererAINode;

   public HumanAI(int var1, boolean var2, boolean var3, int var4) {
      this.addChild(new AINode<T>() {
         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            SettlerMission var3 = var1.getCurrentMission();
            return var3 != null && var3.isMobIdle(var1) ? AINodeResult.RUNNING : AINodeResult.FAILURE;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (HumanMob)var2, var3);
         }
      });
      this.addChild(this.humanInteractingAINode = new HumanInteractingAINode());
      this.addChild(this.humanMoveToAINode = new HumanMoveToAINode());
      this.addChild(new AINode<T>() {
         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            var3.onGlobalTick((var1x) -> {
               if (var2.isSettlerOnCurrentLevel() && (Boolean)var2.hideOnLowHealth.get()) {
                  float var2x = (float)var2.getHealth() / (float)var2.getMaxHealth();
                  if (var2x <= 0.4F) {
                     var2.isHiding = true;
                  } else if (var2x >= 0.8F) {
                     var2.isHiding = false;
                  }
               } else {
                  var2.isHiding = false;
               }

            });
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            return AINodeResult.FAILURE;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (HumanMob)var2, var3);
         }
      });
      this.addChild(this.wanderHomeLowHealthAINode = new HumanWanderHomeLowHealthAINode());
      this.addChild(new HumanCommandMoveToAINode(true));
      SequenceAINode var5 = new SequenceAINode();
      var5.addChild(this.humanTargetFinderAI = new HumanTargetFinderAI(var1, var2, var3));
      var5.addChild(this.chaserAINode = new HumanChaserAINode());
      var5.addChild(new AINode<T>() {
         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            Mob var3 = (Mob)var2.getObject(Mob.class, "chaserTarget");
            if (var3 != null) {
               LocalMessage var4 = new LocalMessage("activities", "attacking", "target", var3.getLocalization());
               var1.setActivity("chaser", 20000, var4);
            } else {
               var1.clearActivity("chaser");
            }

            return AINodeResult.SUCCESS;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (HumanMob)var2, var3);
         }
      });
      this.addChild(var5);
      this.addChild(new HumanCommandFollowMobAINode());
      this.addChild(new HumanCommandMoveToAINode(false));
      var5 = new SequenceAINode();
      var5.addChild(this.humanJobSearchingAINode = new HumanJobSearchingAINode());
      var5.addChild(this.humanJobMoveToAINode = new HumanJobMoveToAINode());
      var5.addChild(this.humanDoJobAINode = new HumanDoJobAINode());
      this.addChild(var5);
      this.addChild(this.wanderHomeAtRaidAINode = new WanderHomeAtConditionAINode<T>() {
         public boolean shouldGoHome(T var1) {
            return var1.getLevel().settlementLayer.isRaidActive() && !var1.isTravelingHuman();
         }

         public Point getHomeTile(T var1) {
            return !var1.isSettlerOnCurrentLevel() ? null : var1.home;
         }

         public boolean isHomeRoom(T var1) {
            return var1.isSettlerOnCurrentLevel();
         }

         public boolean isHomeHouse(T var1) {
            return true;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isHomeHouse(Mob var1) {
            return this.isHomeHouse((HumanMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isHomeRoom(Mob var1) {
            return this.isHomeRoom((HumanMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Point getHomeTile(Mob var1) {
            return this.getHomeTile((HumanMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean shouldGoHome(Mob var1) {
            return this.shouldGoHome((HumanMob)var1);
         }
      });
      this.addChild(new HumanSleepAINode());
      this.wandererAINode = new WandererAINode<T>(var4) {
         public Point getBase(T var1) {
            if (var1.isSettler() && !var1.isSettlerOnCurrentLevel()) {
               return null;
            } else {
               if (var1.isOnStrike() && var1.getLevel().settlementLayer.isActive()) {
                  SettlementLevelData var2 = SettlementLevelData.getSettlementData(var1.getLevel());
                  if (var2 != null) {
                     Point var3 = var2.getObjectEntityPos();
                     if (var3 != null) {
                        return var3;
                     }
                  }
               }

               return var1.home;
            }
         }

         public int getBaseRadius(T var1) {
            return var1.isOnStrike() ? 5 : super.getBaseRadius(var1);
         }

         public boolean forceFindAroundBase(T var1) {
            return var1.isOnStrike();
         }

         public boolean isBaseHouse(T var1, Point var2) {
            return true;
         }

         public boolean isBaseRoom(T var1, Point var2) {
            return var1.isSettlerOnCurrentLevel();
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isBaseRoom(Mob var1, Point var2) {
            return this.isBaseRoom((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isBaseHouse(Mob var1, Point var2) {
            return this.isBaseHouse((HumanMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean forceFindAroundBase(Mob var1) {
            return this.forceFindAroundBase((HumanMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public int getBaseRadius(Mob var1) {
            return this.getBaseRadius((HumanMob)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Point getBase(Mob var1) {
            return this.getBase((HumanMob)var1);
         }
      };
      this.wandererAINode.runAwayFromAttacker = true;
      this.wandererAINode.runAwayFromAttackerToBase = true;
      this.wandererAINode.hideInside = (var0) -> {
         return var0.getWorldEntity().isNight() || var0.isHiding;
      };
      this.wandererAINode.getZoneTester = (var0) -> {
         if (var0.isOnStrike()) {
            return null;
         } else {
            return var0.levelSettler != null ? var0.levelSettler.isTileInRestrictZoneTester() : null;
         }
      };
      this.addChild(this.wandererAINode);
   }

   public boolean shootSimpleProjectile(T var1, Mob var2, String var3, int var4, int var5, int var6) {
      return this.shootSimpleProjectile(var1, var2, var3, DamageTypeRegistry.NORMAL, var4, var5, var6);
   }

   public boolean shootSimpleProjectile(T var1, Mob var2, String var3, DamageType var4, int var5, int var6, int var7) {
      if (var1.canAttack()) {
         var1.attack(var2.getX(), var2.getY(), false);
         GameDamage var8 = new GameDamage(var4, (float)var5);
         Projectile var9 = ProjectileRegistry.getProjectile(var3, var1.getLevel(), var1.x, var1.y, var2.x, var2.y, (float)var6, var7, var8, var1);
         var9.setTargetPrediction(var2, -10.0F);
         var9.moveDist(10.0);
         var1.getLevel().entityManager.projectiles.add(var9);
         return true;
      } else {
         return false;
      }
   }
}
