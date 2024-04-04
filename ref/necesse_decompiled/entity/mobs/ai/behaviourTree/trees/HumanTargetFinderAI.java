package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.Zoning;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.pathfinding.PathResult;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanAngerTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanCommandAttackTargetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HumanTargetFinderAI<T extends HumanMob> extends SelectorAINode<T> {
   public boolean attackHostiles;
   public boolean ignoreHiding;

   public HumanTargetFinderAI(int var1, boolean var2, boolean var3) {
      this.attackHostiles = var2;
      this.ignoreHiding = var3;
      TargetFinderDistance var4 = new TargetFinderDistance<T>(var1) {
         protected int getSearchDistanceFlat(T var1, Mob var2) {
            short var3 = 0;
            boolean var4 = var1.hasCommandOrders() || var1.home == null || var1.getLevel().isOutside(var1.home.x, var1.home.y);
            if (!var4) {
               var3 = 160;
            }

            if (var1.getLevel().settlementLayer.isRaidActive()) {
               var3 = 320;
            }

            return super.getSearchDistanceFlat(var1, var2) + var3;
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected int getSearchDistanceFlat(Mob var1, Mob var2) {
            return this.getSearchDistanceFlat((HumanMob)var1, var2);
         }
      };
      TargetValidity var5 = new TargetValidity<T>() {
         public boolean isValidTarget(AINode<T> var1, T var2, Mob var3, boolean var4) {
            if (!super.isValidTarget(var1, var2, var3, var4)) {
               return false;
            } else {
               boolean var5 = !var2.isSettlerOnCurrentLevel() || var2.hasCommandOrders() && !var2.isHiding || var2.home == null || var2.getLevel().isOutside(var2.home.x, var2.home.y);
               int var6 = var2.home == null ? 0 : var2.getLevel().getRoomID(var2.home.x, var2.home.y);
               Boolean var7 = (Boolean)HumanTargetFinderAI.this.getBlackboard().getObject(Boolean.class, "isHidingInside");
               ComputedValue var8 = new ComputedValue(() -> {
                  if (var2.levelSettler != null && !var2.hasCommandOrders()) {
                     Zoning var1 = var2.levelSettler.data.getDefendZone();
                     ZoneTester var2x = var2.levelSettler.isTileInRestrictZoneTester();
                     return (var2xx, var3) -> {
                        return var1.containsTile(var2xx, var3) && var2x.containsTile(var2xx, var3);
                     };
                  } else {
                     return (var0, var1x) -> {
                        return true;
                     };
                  }
               });
               return HumanTargetFinderAI.this.isValidTarget(var2, var3, var5, var6, var7 != null && var7, var8, var4);
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean isValidTarget(AINode var1, Mob var2, Mob var3, boolean var4) {
            return this.isValidTarget(var1, (HumanMob)var2, var3, var4);
         }
      };
      this.addChild(new HumanAngerTargetAINode(var4, var5));
      this.addChild(new HumanCommandAttackTargetterAINode());
      TargetFinderAINode var6 = new TargetFinderAINode<T>(var4, var5) {
         public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
            return HumanTargetFinderAI.this.streamHumanAITargets(var1, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
            return this.streamPossibleTargets((HumanMob)var1, var2, var3);
         }
      };
      var6.moveToAttacker = false;
      var6.runOnGlobalTick = true;
      var6.loseTargetMinCooldown = 1000;
      var6.loseTargetMaxCooldown = 3000;
      var6.noTargetFoundMinCooldown = 1000;
      var6.noTargetFoundMaxCooldown = 2000;
      this.addChild(var6);
   }

   protected boolean isTargetSameRoom(Mob var1, int var2) {
      int var3 = var1.getTileX();
      int var4 = var1.getTileY();
      return var1.getLevel().getRoomID(var3, var4) == var2 ? true : var1.getLevel().regionManager.getSemiRegion(var3, var4).adjacentRegions.stream().anyMatch((var1x) -> {
         return var1x.getRoomID() == var2;
      });
   }

   public boolean isValidTarget(T var1, Mob var2, boolean var3, int var4, boolean var5, ComputedValue<ZoneTester> var6, boolean var7) {
      ZoneTester var8;
      if (!var3) {
         if (this.isTargetSameRoom(var2, var4)) {
            return true;
         } else if (var1.isHiding) {
            return false;
         } else if (!this.ignoreHiding && var5 && !var1.getLevel().settlementLayer.isRaidActive()) {
            return false;
         } else {
            var8 = (ZoneTester)var6.get();
            return var8 == null || var8.containsTile(var2.getTileX(), var2.getTileY());
         }
      } else if (var1.hasCommandOrders()) {
         Point var10;
         if (var1.commandFollowMob != null && !var1.commandFollowMob.removed() && var1.isSamePlace(var1.commandFollowMob)) {
            var10 = new Point(var1.commandFollowMob.getTileX(), var1.commandFollowMob.getTileY());
         } else if (var1.commandGuardPoint != null) {
            var10 = new Point(var1.commandGuardPoint.x / 32, var1.commandGuardPoint.y / 32);
         } else {
            var10 = new Point(var1.getTileX(), var1.getTileY());
         }

         int var9 = var1.isHiding ? 10 : 20;
         if (GameMath.squareDistance((float)var10.x, (float)var10.y, (float)var2.getTileX(), (float)var2.getTileY()) <= (float)var9) {
            return !var7 ? true : (Boolean)Performance.record(var1.getLevel().tickManager(), "settlerGuardTarget", (Supplier)(() -> {
               BiPredicate var5 = var1.canBeTargetedFromAdjacentTiles() ? TilePathfinding.isAtOrAdjacentObject(var2.getLevel(), var2.getTileX(), var2.getTileY()) : null;
               TilePathfinding var6 = new TilePathfinding(var1.getLevel().tickManager(), var1.getLevel(), var1, var5, this.getBlackboard().mover.getPathOptions(this));
               PathResult var7 = var6.findPath(var10, new Point(var2.getTileX(), var2.getTileY()), var9 + 5);
               return var7.foundTarget;
            }));
         } else {
            return false;
         }
      } else {
         var8 = (ZoneTester)var6.get();
         if (var8 != null && var8.containsTile(var2.getTileX(), var2.getTileY())) {
            return true;
         } else {
            return var1.home == null || GameMath.squareDistance((float)var1.home.x, (float)var1.home.y, (float)var2.getTileX(), (float)var2.getTileY()) <= 20.0F;
         }
      }
   }

   public GameAreaStream<Mob> streamHumanAITargets(T var1, TargetFinderDistance<T> var2) {
      if (var1.commandFollowMob != null && var1.commandMoveToFollowPoint) {
         return GameAreaStream.empty();
      } else if (var1.isTravelingHuman()) {
         return GameAreaStream.empty();
      } else {
         boolean var3 = var1.hasCommandOrders() || var1.home == null || var1.getLevel().isOutside(var1.home.x, var1.home.y);
         Point var4 = new Point(var1.getX(), var1.getY());
         if (!var3) {
            var4 = new Point(var1.home.x * 32 + 16, var1.home.y * 32 + 16);
         }

         return var2.streamMobsAndPlayersInRange(var4, var1).filter((var1x) -> {
            return var1x.canTakeDamage() && var1x.canBeHit(var1);
         }).filter(var1.filterHumanTargets()).filter((var3x) -> {
            return var3x.getDistance((float)var4.x, (float)var4.y) < (float)var2.getSearchDistance(var1, var3x);
         });
      }
   }
}
