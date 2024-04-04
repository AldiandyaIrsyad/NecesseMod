package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.BirdCritterAI;
import necesse.entity.mobs.friendly.critters.BirdMob;
import necesse.entity.mobs.mobMovement.MobMovementConstant;

public class BirdCritterRunAINode<T extends BirdMob> extends AINode<T> {
   public int flyAwayFromPlayers;
   protected Point2D.Float runDir;
   public int flyAwayIn;

   public BirdCritterRunAINode(int var1) {
      this.flyAwayFromPlayers = var1;
      this.flyAwayIn = GameRandom.globalRandom.getIntBetween(40, 2400);
   }

   public BirdCritterRunAINode() {
      this(150);
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Iterator var3 = var2.getLastHits().iterator();
      float var5;
      if (var3.hasNext()) {
         AIWasHitEvent var4 = (AIWasHitEvent)var3.next();
         Mob var7 = var4.event.attacker != null ? var4.event.attacker.getAttackOwner() : null;
         float var6;
         if (var7 != null) {
            var5 = (float)(var1.getX() - var7.getX());
            var6 = (float)(var1.getY() - var7.getY());
         } else {
            var5 = (float)GameRandom.globalRandom.nextGaussian();
            var6 = (float)GameRandom.globalRandom.nextGaussian();
         }

         this.flyAway(var5, var6);
      }

      if (var1.isRunning()) {
         if (var2.mover.hasMovingNode()) {
            var2.mover.stopMoving(var1);
         }

         if (this.runDir != null) {
            var1.setMovement(new MobMovementConstant(this.runDir.x, this.runDir.y));
         }

         if (!(new Rectangle(96, 96, (var1.getLevel().width - 6) * 32, (var1.getLevel().height - 6) * 32)).contains(var1.getCollision())) {
            var1.remove();
         }
      } else {
         --this.flyAwayIn;
         float var10;
         if (this.flyAwayIn <= 0) {
            ServerClient var8 = (ServerClient)GameRandom.globalRandom.getOneOf((List)GameUtils.streamServerClients(var1.getLevel()).collect(Collectors.toList()));
            if (var8 != null) {
               int var11 = GameRandom.globalRandom.getIntOffset(var8.playerMob.getX(), 200);
               int var12 = GameRandom.globalRandom.getIntOffset(var8.playerMob.getY(), 200);
               var10 = (float)var11 - var1.x;
               var5 = (float)var12 - var1.y;
            } else {
               var10 = (float)GameRandom.globalRandom.nextGaussian();
               var5 = (float)GameRandom.globalRandom.nextGaussian();
            }

            this.flyAway(var10, var5);
         } else if (this.flyAwayFromPlayers > 0) {
            CritterRunAINode.TempDistance var9 = (CritterRunAINode.TempDistance)var1.getLevel().entityManager.players.getInRegionByTileRange(var1.getTileX(), var1.getTileY(), this.flyAwayFromPlayers / 32 + 2).stream().map((var1x) -> {
               return new CritterRunAINode.TempDistance(var1x, var1);
            }).min((var0, var1x) -> {
               return Float.compare(var0.distance, var1x.distance);
            }).orElse((Object)null);
            if (var9 != null && var9.distance <= (float)this.flyAwayFromPlayers) {
               var10 = var1.x - var9.player.x;
               var5 = var1.y - var9.player.y;
               this.flyAway(var10, var5);
            }
         }
      }

      return AINodeResult.SUCCESS;
   }

   public <T extends BirdMob> void flyAway(float var1, float var2) {
      this.runDir = GameMath.normalize(var1, var2);
      if (Math.abs(this.runDir.y) > 0.5F) {
         this.runDir = GameMath.normalize(this.runDir.x == 0.0F ? 1.0F : Math.signum(this.runDir.x), Math.signum(this.runDir.y) * 0.5F);
      }

      ((BirdMob)this.mob()).setRunning(true);
      ((BirdMob)this.mob()).getLevel().entityManager.mobs.streamInRegionsInTileRange(((BirdMob)this.mob()).getX(), ((BirdMob)this.mob()).getY(), 5).filter((var0) -> {
         return var0 instanceof BirdMob && var0.ai.tree instanceof BirdCritterAI;
      }).filter((var1x) -> {
         return var1x.getDistance(this.mob()) < 100.0F;
      }).forEach((var2x) -> {
         BirdCritterRunAINode var3 = ((BirdCritterAI)var2x.ai.tree).runNode;
         if (var3.runDir == null) {
            var3.flyAway(var1, var2);
         }

      });
   }

   // $FF: synthetic method
   // $FF: bridge method
   public AINodeResult tick(Mob var1, Blackboard var2) {
      return this.tick((BirdMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public void init(Mob var1, Blackboard var2) {
      this.init((BirdMob)var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
      this.onRootSet(var1, (BirdMob)var2, var3);
   }
}
