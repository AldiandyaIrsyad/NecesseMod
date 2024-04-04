package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiPredicate;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.ComputedObjectValue;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.friendly.human.HumanMob;

public class HumanAngerTargetAINode<T extends HumanMob> extends MoveTaskAINode<T> {
   public TargetValidity<T> validity;
   public TargetFinderDistance<T> distance;
   public String currentTargetKey;
   public ArrayList<Mob> enemies;
   public float anger;

   public HumanAngerTargetAINode(TargetFinderDistance<T> var1, TargetValidity<T> var2, String var3) {
      this.enemies = new ArrayList();
      this.anger = 0.0F;
      this.distance = var1;
      this.validity = var2;
      this.currentTargetKey = var3;
   }

   public HumanAngerTargetAINode(TargetFinderDistance<T> var1, TargetValidity<T> var2) {
      this(var1, var2, "currentTarget");
   }

   public HumanAngerTargetAINode(TargetFinderDistance<T> var1) {
      this(var1, new TargetValidity());
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.put("humanAngerHandler", this);
      var3.onWasHit((var2x) -> {
         if (!var2.isClient() && !var2x.event.wasPrevented) {
            Mob var3 = var2x.event.attacker != null ? var2x.event.attacker.getAttackOwner() : null;
            if (var3 != null && var3.isPlayer) {
               if (!var3.buffManager.hasBuff(BuffRegistry.BOSS_NEARBY)) {
                  if (!var2.isFriendlyClient(((PlayerMob)var3).getNetworkClient())) {
                     float var4 = Math.abs((float)var2.getHealth() / (float)var2.getMaxHealth() - 1.0F);
                     float var5 = this.anger;
                     this.anger += var4 * 2.5F;
                     GameMessage var6;
                     if (this.anger >= 1.0F) {
                        if (var5 < 1.0F && !var2.removed()) {
                           var6 = var2.getRandomAttackMessage();
                           if (var6 != null) {
                              var2.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobChat(var2.getUniqueID(), var6), var2);
                           }
                        }

                        this.addEnemy(var3, this.anger);
                        var2.getLevel().entityManager.mobs.getInRegionByTileRange(var2.getX() / 32, var2.getY() / 32, 25).stream().filter((var1) -> {
                           return var1.isSameTeam(var2) && var1 instanceof HumanMob;
                        }).forEach((var2xx) -> {
                           HumanAngerTargetAINode var3x = (HumanAngerTargetAINode)var2xx.ai.blackboard.getObject(HumanAngerTargetAINode.class, "humanAngerHandler");
                           if (var3x != null) {
                              var3x.addEnemy(var3, this.anger);
                           }

                        });
                     } else if (!var2.removed()) {
                        var6 = var2.getRandomAngryMessage();
                        if (var6 != null) {
                           var2.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobChat(var2.getUniqueID(), var6), var2);
                        }
                     }

                  }
               }
            }
         }
      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      this.enemies.removeIf((var1x) -> {
         return var1x.removed() || var1x.isPlayer && var1.isFriendlyClient(((PlayerMob)var1x).getNetworkClient());
      });
      Mob var7;
      if (this.anger > 0.0F) {
         if (!var1.buffManager.hasBuff(BuffRegistry.HUMAN_ANGRY) || var1.buffManager.getBuff(BuffRegistry.HUMAN_ANGRY).getDurationLeft() < 500) {
            ActiveBuff var3 = new ActiveBuff(BuffRegistry.HUMAN_ANGRY, var1, 5.0F, (Attacker)null);
            var1.addBuff(var3, true);
         }

         var7 = (Mob)var2.getObject(Mob.class, this.currentTargetKey);
         float var4 = 0.2F;
         this.anger -= var4 / 20.0F;
         Point var5;
         if (var1.home != null && !var1.hasCommandOrders()) {
            var5 = new Point(var1.home.x * 32 + 16, var1.home.y * 32 + 16);
         } else {
            var5 = new Point(var1.getX(), var1.getY());
         }

         Mob var6 = (Mob)this.enemies.stream().filter((var2x) -> {
            return this.validity.isValidTarget(this, var1, var2x, true);
         }).map((var2x) -> {
            return new ComputedObjectValue(var2x, () -> {
               return this.distance.getDistance(var5, var2x);
            });
         }).filter((var2x) -> {
            return (Float)var2x.get() < (float)(this.distance.getTargetLostDistance(var1, (Mob)var2x.object) * 3);
         }).min(Comparator.comparingDouble((var0) -> {
            return (double)(Float)var0.get();
         })).map((var0) -> {
            return (Mob)var0.object;
         }).orElse((Object)null);
         if (var6 != null) {
            var2.put(this.currentTargetKey, var6);
            return AINodeResult.SUCCESS;
         }

         if (var7 != null && this.enemies.contains(var7)) {
            var2.put(this.currentTargetKey, (Object)null);
            if (var1.home != null && !var1.hasCommandOrders()) {
               return this.moveToTileTask(var1.home.x, var1.home.y, (BiPredicate)null, (var0) -> {
                  var0.move((Runnable)null);
                  return AINodeResult.SUCCESS;
               });
            }

            return AINodeResult.SUCCESS;
         }
      } else if (this.anger <= 0.0F && !this.enemies.isEmpty()) {
         var7 = (Mob)var2.getObject(Mob.class, this.currentTargetKey);
         this.anger = 0.0F;
         if (var1.buffManager.hasBuff(BuffRegistry.HUMAN_ANGRY)) {
            var1.buffManager.removeBuff(BuffRegistry.HUMAN_ANGRY, true);
         }

         if (this.enemies.contains(var7)) {
            var2.put(this.currentTargetKey, (Object)null);
            var2.mover.stopMoving(var1);
         }

         this.enemies.clear();
      }

      return AINodeResult.FAILURE;
   }

   public void addEnemy(Mob var1, float var2) {
      if (!this.enemies.contains(var1)) {
         this.enemies.add(var1);
      }

      this.anger = var2;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tickNode(Mob var1, Blackboard var2) {
      return this.tickNode((HumanMob)var1, var2);
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
}
