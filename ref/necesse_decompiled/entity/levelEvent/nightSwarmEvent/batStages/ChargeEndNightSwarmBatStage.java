package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class ChargeEndNightSwarmBatStage extends NightSwarmBatStage {
   public Mob target;
   public float midX;
   public float midY;
   public int range;
   public long endTime;
   public Point2D.Float hitDir;

   public ChargeEndNightSwarmBatStage(Mob var1, float var2, float var3, int var4, long var5) {
      super(false);
      this.target = var1;
      this.midX = var2;
      this.midY = var3;
      this.range = var4;
      this.endTime = var5;
   }

   public void onStarted(NightSwarmBatMob var1) {
      var1.setMovement(new MobMovementRelative(this.target, 0.0F, 0.0F));
      var1.disableShareCooldown.runAndSend();
   }

   public void serverTick(NightSwarmBatMob var1) {
   }

   public boolean hasCompleted(NightSwarmBatMob var1) {
      return this.hitDir != null || this.target == null || this.target.removed() || !this.target.isVisible() || this.endTime <= var1.getWorldEntity().getTime();
   }

   public void onCompleted(NightSwarmBatMob var1) {
      var1.setMovement((MobMovement)null);
      if (this.hitDir != null) {
         var1.stages.add(0, new CircleEndNightSwarmBatStage(this.midX, this.midY, var1.x - this.hitDir.x * 100.0F, var1.y - this.hitDir.y * 100.0F, this.range, this.endTime, this.endTime));
      } else {
         var1.stages.add(0, new CircleEndNightSwarmBatStage(this.midX, this.midY, this.midX, this.midY, this.range, this.endTime, this.endTime));
      }

   }

   public void onCollisionHit(NightSwarmBatMob var1, Mob var2) {
      super.onCollisionHit(var1, var2);
      this.hitDir = GameMath.normalize(var1.x - var2.x, var1.y - var2.y);
   }

   public void onWasHit(NightSwarmBatMob var1, MobWasHitEvent var2) {
      super.onWasHit(var1, var2);
      Mob var3 = var2.attacker.getAttackOwner();
      if (var3 != null) {
         this.hitDir = GameMath.normalize(var1.x - var3.x, var1.y - var3.y);
      } else if (var2.knockbackX == 0.0F && var2.knockbackY == 0.0F) {
         this.hitDir = GameMath.normalize(var1.x - this.midX, var1.y - this.midY);
      } else {
         this.hitDir = GameMath.normalize(var2.knockbackX, var2.knockbackY);
      }

      float var4 = var1.getSpeed() * 0.8F;
      var1.dx = this.hitDir.x * var4;
      var1.dy = this.hitDir.y * var4;
      var1.sendMovementPacket(false);
   }
}
