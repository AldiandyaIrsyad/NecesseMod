package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CallHelpAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CurrentTargetTalkerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PirateEscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.pirates.PirateMob;

public class PirateAITree<T extends PirateMob> extends SelectorAINode<T> {
   protected int startPassiveCounter;

   public PirateAITree(int var1, int var2, int var3, int var4, int var5) {
      final String var6 = "mobBase";
      this.addChild(new AINode<T>() {
         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            var2.put(var6, new Point(var1.baseTile.x * 32 + 16, var1.baseTile.y * 32 + 16));
            return AINodeResult.FAILURE;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((PirateMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((PirateMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (PirateMob)var2, var3);
         }
      });
      final String var7 = "chaserTarget";
      this.addChild(new AINode<T>() {
         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            var3.onEvent("refreshBossDespawn", (var1x) -> {
               PirateAITree.this.startPassiveCounter = 0;
            });
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            Mob var3 = (Mob)var2.getObject(Mob.class, var7);
            boolean var4 = var3 == null;
            if (var1.buffManager.hasBuff(BuffRegistry.PIRATE_PASSIVE) != var4) {
               if (var4) {
                  ++PirateAITree.this.startPassiveCounter;
                  if (PirateAITree.this.startPassiveCounter > 60) {
                     var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.PIRATE_PASSIVE, var1, 0, (Attacker)null), true);
                  }
               } else {
                  var1.buffManager.removeBuff(BuffRegistry.PIRATE_PASSIVE, true);
                  PirateAITree.this.startPassiveCounter = 0;
               }
            }

            if (!var4) {
               PirateAITree.this.startPassiveCounter = 0;
            }

            if (var3 == null && var1.buffManager.hasBuff(BuffRegistry.PIRATE_ESCAPE)) {
               var1.buffManager.removeBuff(BuffRegistry.PIRATE_ESCAPE, true);
            }

            return AINodeResult.FAILURE;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((PirateMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((PirateMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (PirateMob)var2, var3);
         }
      });
      this.addChild(new FailerAINode(new CallHelpAINode("piratehelp", 5, 8000)));
      this.addChild(new FailerAINode(new CurrentTargetTalkerAINode<T>(true, 8000) {
         public void talk(T var1, Mob var2) {
            PirateAITree.this.sendRandomAttackMessage(var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void talk(Mob var1, Mob var2) {
            this.talk((PirateMob)var1, var2);
         }
      }));
      this.addChild(this.getChaserNode(var1, var2, var3, var4));
      this.addChild(new PirateEscapeAINode<T>(384) {
         public void onEscape(T var1) {
            super.onEscape(var1);
            PirateAITree.this.sendRandomEscapeMessage(var1);
         }
      });
      WandererAINode var8 = new WandererAINode<T>(var5) {
         public Point getBase(T var1) {
            return var1.baseTile;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Point getBase(Mob var1) {
            return this.getBase((PirateMob)var1);
         }
      };
      var8.searchRadius = 5;
      this.addChild(var8);
   }

   protected AINode<T> getChaserNode(int var1, int var2, int var3, int var4) {
      return new PirateChaserAI(var1, var2, var3, var4);
   }

   private void sendRandomAttackMessage(T var1) {
      if (var1.isServer()) {
         var1.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobChat(var1.getUniqueID(), "mobmsg", this.getRandomAttackKey()), var1);
      }
   }

   private String getRandomAttackKey() {
      int var1 = GameRandom.globalRandom.nextInt(7) + 1;
      return "pirateattack" + var1;
   }

   private void sendRandomEscapeMessage(T var1) {
      if (var1.isServer()) {
         var1.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobChat(var1.getUniqueID(), "mobmsg", this.getRandomEscapeKey()), var1);
      }
   }

   private String getRandomEscapeKey() {
      int var1 = GameRandom.globalRandom.nextInt(3) + 1;
      return "pirateescape" + var1;
   }
}
