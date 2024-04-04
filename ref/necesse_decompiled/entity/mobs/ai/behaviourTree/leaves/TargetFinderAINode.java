package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiPredicate;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.TargetAIEvent;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;

public abstract class TargetFinderAINode<T extends Mob> extends MoveTaskAINode<T> {
   public TargetValidity<T> validity;
   public TargetFinderDistance<T> distance;
   public String baseKey;
   public boolean baseKeyIsJustPreference;
   public int targetNonBaseWithinRange;
   public String focusTargetKey;
   public String currentTargetKey;
   public String newTargetFoundEventType;
   public String lastTargetInvalidEventType;
   public boolean moveToAttacker;
   public boolean runOnGlobalTick;
   public int loseTargetTimer;
   public int noTargetFoundTimer;
   public int loseTargetMinCooldown;
   public int loseTargetMaxCooldown;
   public int noTargetFoundMinCooldown;
   public int noTargetFoundMaxCooldown;

   public TargetFinderAINode(TargetFinderDistance<T> var1, TargetValidity<T> var2, String var3, String var4) {
      this.baseKey = "mobBase";
      this.newTargetFoundEventType = "newTargetFound";
      this.lastTargetInvalidEventType = "lastTargetInvalid";
      this.moveToAttacker = true;
      this.runOnGlobalTick = false;
      this.loseTargetMinCooldown = 3000;
      this.loseTargetMaxCooldown = 6000;
      this.noTargetFoundMinCooldown = 2000;
      this.noTargetFoundMaxCooldown = 4000;
      this.distance = var1;
      this.validity = var2;
      this.focusTargetKey = var3;
      this.currentTargetKey = var4;
      this.startLoseTargetTimer();
   }

   public TargetFinderAINode(TargetFinderDistance<T> var1, TargetValidity<T> var2) {
      this(var1, var2, "focusTarget", "currentTarget");
   }

   public TargetFinderAINode(TargetFinderDistance<T> var1) {
      this(var1, new TargetValidity());
   }

   public TargetFinderAINode(int var1) {
      this(new TargetFinderDistance(var1));
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onGlobalTick((var3x) -> {
         if (this.runOnGlobalTick) {
            this.tickTargetFinder(var2, var3);
         }

      });
      var3.onWasHit((var1x) -> {
         this.noTargetFoundTimer = 0;
      });
      var3.onEvent("resetTarget", (var2x) -> {
         this.noTargetFoundTimer = 0;
         this.loseTargetTimer = 0;
         var3.put(this.currentTargetKey, (Object)null);
         var3.submitEvent(this.newTargetFoundEventType, new TargetAIEvent((Mob)null));
      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickTargetFinder(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.currentTargetKey);
      Mob var4 = var3;
      boolean var5 = false;
      int var6 = this.distance.searchDistance;

      AINodeResult var7;
      try {
         if (var4 != null || this.noTargetFoundTimer < 0) {
            if (var4 != null && this.loseTargetMinCooldown >= 0 && this.loseTargetMaxCooldown >= 0) {
               this.loseTargetTimer -= 50;
               if (this.loseTargetTimer <= 0) {
                  var4 = null;
                  var5 = true;
                  this.startLoseTargetTimer();
               }
            }

            Point var15 = (Point)var2.getObject(Point.class, this.baseKey);
            Point var8 = var15 != null ? var15 : new Point(var1.getX(), var1.getY());
            if (var4 != null && (var4.removed() || !this.validity.isValidTarget(this, var1, var4, false) || this.distance.getDistance(var8, var4) > (float)this.distance.getTargetLostDistance(var1, var4))) {
               var2.submitEvent(this.lastTargetInvalidEventType, new TargetAIEvent(var4));
               var4 = null;
               var5 = true;
            }

            if (var5) {
               this.distance.searchDistance += this.distance.targetLostAddedDistance;
            }

            Mob var9 = (Mob)var2.getObject(Mob.class, this.focusTargetKey);
            if (var9 != null && var4 != var9 && this.validity.isValidTarget(this, var1, var9, true) && this.distance.getDistance(var8, var9) < (float)this.distance.getSearchDistance(var1, var9) && var1.estimateCanMoveTo(var9.getTileX(), var9.getTileY(), var9.canBeTargetedFromAdjacentTiles())) {
               var4 = var9;
            }

            if (var4 == null) {
               GameAreaStream var10;
               GameAreaStream var10000;
               if (var15 != null && this.baseKeyIsJustPreference && this.targetNonBaseWithinRange > 0) {
                  var10 = this.streamPossibleTargets(var1, new Point(var1.getX(), var1.getY()), this.distance);
                  if (var10 != null) {
                     var10000 = var10.filter((var2x) -> {
                        return this.validity.isValidTarget(this, var1, var2x, true) && var1.getDistance(var2x) <= (float)this.targetNonBaseWithinRange * this.distance.getSearchDistanceMod(var1, var2x);
                     }).filter((var1x) -> {
                        return var1.estimateCanMoveTo(var1x.getTileX(), var1x.getTileY(), var1x.canBeTargetedFromAdjacentTiles());
                     });
                     Objects.requireNonNull(var1);
                     var4 = (Mob)var10000.findBestDistance(0, Comparator.comparingDouble(var1::getDistance)).orElse((Object)null);
                  }

                  if (var4 == null) {
                     GameAreaStream var11 = this.streamPossibleTargets(var1, var8, this.distance);
                     if (var11 != null) {
                        var4 = (Mob)var11.filter((var3x) -> {
                           return this.validity.isValidTarget(this, var1, var3x, true) && this.distance.getDistance(var8, var3x) < (float)this.distance.getSearchDistance(var1, var3x);
                        }).filter((var1x) -> {
                           return var1.estimateCanMoveTo(var1x.getTileX(), var1x.getTileY(), var1x.canBeTargetedFromAdjacentTiles());
                        }).findBestDistance(0, Comparator.comparingDouble((var2x) -> {
                           return (double)this.distance.getDistance(var8, var2x);
                        })).orElse((Object)null);
                     }
                  }
               } else {
                  var10 = this.streamPossibleTargets(var1, var8, this.distance);
                  if (var10 != null) {
                     var10000 = var10.filter((var3x) -> {
                        return this.validity.isValidTarget(this, var1, var3x, true) && this.distance.getDistance(var8, var3x) < (float)this.distance.getSearchDistance(var1, var3x);
                     }).filter((var1x) -> {
                        return var1.estimateCanMoveTo(var1x.getTileX(), var1x.getTileY(), var1x.canBeTargetedFromAdjacentTiles());
                     });
                     Objects.requireNonNull(var1);
                     var4 = (Mob)var10000.findBestDistance(0, Comparator.comparingDouble(var1::getDistance)).orElse((Object)null);
                  }
               }
            }

            if (var4 == null && this.noTargetFoundMinCooldown >= 0 && this.noTargetFoundMaxCooldown >= 0) {
               this.startNoTargetFoundCooldown();
            }

            if (var3 != var4) {
               var2.put(this.currentTargetKey, var4);
               var2.submitEvent(this.newTargetFoundEventType, new TargetAIEvent(var4));
            }

            AINodeResult var16 = var4 != null ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
            return var16;
         }

         this.noTargetFoundTimer -= 50;
         var7 = AINodeResult.FAILURE;
      } finally {
         this.distance.searchDistance = var6;
      }

      return var7;
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.currentTargetKey);
      if (this.moveToAttacker && var3 == null) {
         Iterator var4 = var2.getLastHits().iterator();

         while(var4.hasNext()) {
            AIWasHitEvent var5 = (AIWasHitEvent)var4.next();
            Mob var6 = var5.event.attacker != null ? var5.event.attacker.getAttackOwner() : null;
            Point var7 = (Point)var2.getObject(Point.class, this.baseKey);
            if (var6 != null && this.validity.isValidTarget(this, var1, var6, true) && var1.estimateCanMoveTo(var6.getTileX(), var6.getTileY(), var6.canBeTargetedFromAdjacentTiles())) {
               Point var8 = var7 != null ? var7 : new Point(var1.getX(), var1.getY());
               if (var7 == null || !(this.distance.getDistance(var8, var6) > (float)this.distance.getTargetLostDistance(var1, var6))) {
                  return this.moveToTileTask(var6.getTileX(), var6.getTileY(), (BiPredicate)null, (var3x) -> {
                     var3x.moveIfWithin(-1, 1, (Runnable)null);
                     var2.put(this.currentTargetKey, var6);
                     return AINodeResult.SUCCESS;
                  });
               }
            }
         }
      }

      if (!this.runOnGlobalTick) {
         return this.tickTargetFinder(var1, var2);
      } else {
         return AINodeResult.SUCCESS;
      }
   }

   public void startLoseTargetTimer() {
      this.loseTargetTimer = GameRandom.globalRandom.getIntBetween(this.loseTargetMinCooldown, this.loseTargetMaxCooldown);
   }

   public void startNoTargetFoundCooldown() {
      this.noTargetFoundTimer = GameRandom.globalRandom.getIntBetween(this.noTargetFoundMinCooldown, this.noTargetFoundMaxCooldown);
   }

   public abstract GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3);

   public static <T extends Mob> GameAreaStream<PlayerMob> streamPlayers(T var0, Point var1, TargetFinderDistance<T> var2) {
      return var2.streamPlayersInRange(var1, var0).filter((var1x) -> {
         return var1x != null && var1x != var0 && !var1x.removed() && var1x.isVisible();
      });
   }

   public static <T extends Mob> GameAreaStream<Mob> streamPlayersAndHumans(T var0, Point var1, TargetFinderDistance<T> var2) {
      return var2.streamMobsAndPlayersInRange(var1, var0).filter((var1x) -> {
         return var1x != null && var1x != var0 && !var1x.removed() && var1x.isVisible() && (var1x.isHuman && var1x.getTeam() != -1 || var1x.isPlayer);
      });
   }
}
