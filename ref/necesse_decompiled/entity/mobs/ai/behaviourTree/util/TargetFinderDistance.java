package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;

public class TargetFinderDistance<T extends Mob> {
   public int searchDistance;
   public int targetLostAddedDistance;

   public TargetFinderDistance(int var1) {
      this.searchDistance = var1;
      this.targetLostAddedDistance = 64;
   }

   public float getSearchDistanceMod(T var1, Mob var2) {
      float var3 = (Float)var1.buffManager.getModifier(BuffModifiers.CHASER_RANGE);
      float var4 = var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.TARGET_RANGE);
      return var1.getLevel().entityManager.getChaserDistanceMod() * var3 * var4;
   }

   protected int getSearchDistanceFlat(T var1, Mob var2) {
      return this.searchDistance;
   }

   protected int getTargetLostDistanceFlat(T var1, Mob var2) {
      return this.getSearchDistanceFlat(var1, var2) + this.targetLostAddedDistance;
   }

   public final float getDistance(Point var1, Mob var2) {
      return this.getDistance(var1, var2.x, var2.y);
   }

   public float getDistance(Point var1, float var2, float var3) {
      return (float)var1.distance((double)var2, (double)var3);
   }

   public int getSearchDistance(T var1, Mob var2) {
      return (int)((float)this.getSearchDistanceFlat(var1, var2) * this.getSearchDistanceMod(var1, var2));
   }

   public int getTargetLostDistance(T var1, Mob var2) {
      return (int)((float)this.getTargetLostDistanceFlat(var1, var2) * this.getSearchDistanceMod(var1, var2));
   }

   public GameAreaStream<Mob> streamMobsAndPlayersInRange(Point var1, T var2) {
      int var3 = (int)((float)this.getSearchDistance(var2, (Mob)null) * BuffModifiers.MAX_TARGET_RANGE_MODIFIER);
      float var10001 = (float)var1.x;
      float var10002 = (float)var1.y;
      return var2.getLevel().entityManager.streamAreaMobsAndPlayers(var10001, var10002, var3);
   }

   public GameAreaStream<PlayerMob> streamPlayersInRange(Point var1, T var2) {
      int var3 = (int)((float)this.getSearchDistance(var2, (Mob)null) * BuffModifiers.MAX_TARGET_RANGE_MODIFIER);
      float var10001 = (float)var1.x;
      float var10002 = (float)var1.y;
      return var2.getLevel().entityManager.players.streamArea(var10001, var10002, var3);
   }

   public GameAreaStream<Mob> streamMobsInRange(Point var1, T var2) {
      int var3 = (int)((float)this.getSearchDistance(var2, (Mob)null) * BuffModifiers.MAX_TARGET_RANGE_MODIFIER);
      float var10001 = (float)var1.x;
      float var10002 = (float)var1.y;
      return var2.getLevel().entityManager.mobs.streamArea(var10001, var10002, var3);
   }
}
