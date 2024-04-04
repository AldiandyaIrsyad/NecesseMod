package necesse.entity.mobs.hostile;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.FlyingTargetMob;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class FlyingHostileMob extends FlyingTargetMob {
   public FlyingHostileMob(int var1) {
      super(var1);
      this.isHostile = true;
      this.setTeam(-2);
      this.canDespawn = true;
   }

   public boolean shouldSave() {
      return this.shouldSave && !this.canDespawn();
   }

   public MobSpawnLocation checkSpawnLocation(MobSpawnLocation var1) {
      return var1.checkNotSolidTile().checkNotInsideOnFloor();
   }

   public boolean isValidSpawnLocation(Server var1, ServerClient var2, int var3, int var4) {
      return (new MobSpawnLocation(this, var3, var4)).checkLightThreshold(var2).checkMobSpawnLocation().checkMaxHostilesAround(4, 8, var2).validAndApply();
   }

   public float getOutgoingDamageModifier() {
      float var1 = super.getOutgoingDamageModifier();
      if (this.getLevel() != null) {
         var1 *= (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_DAMAGE);
      }

      return var1;
   }

   public float getSpeedModifier() {
      float var1 = super.getSpeedModifier();
      if (this.getLevel() != null) {
         var1 *= (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_SPEED);
      }

      return var1;
   }

   public float getMaxHealthModifier() {
      float var1 = super.getMaxHealthModifier();
      if (this.getLevel() != null) {
         var1 *= (Float)this.getLevel().buffManager.getModifier(LevelModifiers.ENEMY_MAX_HEALTH);
      }

      return var1;
   }
}
