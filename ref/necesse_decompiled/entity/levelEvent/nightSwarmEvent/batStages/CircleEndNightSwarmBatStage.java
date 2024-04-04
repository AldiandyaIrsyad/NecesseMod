package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.util.Comparator;
import java.util.Objects;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;

public class CircleEndNightSwarmBatStage extends NightSwarmBatStage {
   public float midX;
   public float midY;
   public float angle;
   public float angleOffsetX;
   public float angleOffsetY;
   public int range;
   public long chargeTime;
   public long endTime;

   public CircleEndNightSwarmBatStage(float var1, float var2, float var3, int var4, long var5, long var7) {
      super(false);
      this.midX = var1;
      this.midY = var2;
      this.angle = var3;
      this.range = var4;
      this.chargeTime = var5;
      this.endTime = var7;
   }

   public CircleEndNightSwarmBatStage(float var1, float var2, float var3, float var4, int var5, long var6, long var8) {
      super(false);
      this.midX = var1;
      this.midY = var2;
      this.angle = Float.NaN;
      this.angleOffsetX = var3;
      this.angleOffsetY = var4;
      this.range = var5;
      this.chargeTime = var6;
      this.endTime = var8;
   }

   public void onStarted(NightSwarmBatMob var1) {
      float var2 = MobMovementCircle.convertToRotSpeed(this.range, var1.getSpeed() * GameRandom.globalRandom.getFloatBetween(0.5F, 1.0F));
      if (Float.isNaN(this.angle)) {
         var1.setMovement(new MobMovementCircleLevelPos(var1, this.midX, this.midY, this.range, var2, this.angleOffsetX, this.angleOffsetY, false));
      } else {
         var1.setMovement(new MobMovementCircleLevelPos(var1, this.midX, this.midY, this.range, var2, this.angle, false));
      }

   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      long var2 = var1.getWorldEntity().getTime();
      return this.chargeTime <= var2 || this.endTime <= var2;
   }

   public void onCompleted(NightSwarmBatMob var1) {
      long var2 = var1.getWorldEntity().getTime();
      if (this.endTime > var2) {
         int var10001 = (int)this.midX;
         int var10002 = (int)this.midY;
         int var10003 = this.range + 50;
         GameAreaStream var10000 = var1.getLevel().entityManager.players.streamAreaTileRange(var10001, var10002, var10003 / 32).filter((var0) -> {
            return var0 != null && !var0.removed() && var0.isVisible();
         }).filter((var1x) -> {
            return var1x.getDistance(this.midX, this.midY) <= (float)this.range;
         });
         Objects.requireNonNull(var1);
         PlayerMob var4 = (PlayerMob)var10000.findBestDistance(0, Comparator.comparingDouble(var1::getDistance)).orElse((Object)null);
         if (var4 != null) {
            var1.stages.add(0, new ChargeEndNightSwarmBatStage(var4, this.midX, this.midY, this.range, this.endTime));
         } else {
            var1.stages.add(0, new CircleEndNightSwarmBatStage(this.midX, this.midY, this.angle, this.range, this.endTime, this.endTime));
         }
      }

   }
}
