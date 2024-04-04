package necesse.entity.levelEvent.mobAbilityLevelEvent;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.level.maps.LevelObjectHit;

public abstract class WeaponShockWaveLevelEvent extends ShockWaveLevelEvent {
   protected boolean canDamageObjects;
   protected GameDamage damage;
   protected float resilienceGain;
   protected float knockback;

   public WeaponShockWaveLevelEvent(float var1, float var2, float var3) {
      super(var1, 100.0F, 100.0F, var2, var3);
   }

   public WeaponShockWaveLevelEvent(Mob var1, int var2, int var3, GameRandom var4, float var5, float var6, float var7, float var8, GameDamage var9, float var10, float var11, float var12, float var13) {
      super(var1, var2, var3, var4, var5, var6, var11, var13, var7, var8);
      this.damage = var9;
      this.resilienceGain = var10;
      this.knockback = var12;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.expandSpeed);
      var1.putNextFloat(this.maxDistance);
      this.damage.writePacket(var1);
      var1.putNextFloat(this.resilienceGain);
      var1.putNextFloat(this.knockback);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.expandSpeed = var1.getNextFloat();
      this.maxDistance = var1.getNextFloat();
      this.damage = GameDamage.fromReader(var1);
      this.resilienceGain = var1.getNextFloat();
      this.knockback = var1.getNextFloat();
   }

   public void init() {
      super.init();
      this.canDamageObjects = this.owner != null && this.owner.isPlayer;
   }

   public void damageTarget(Mob var1) {
      var1.isServerHit(this.damage, (float)(var1.getX() - this.x), (float)(var1.getY() - this.y), this.knockback, this.owner);
      if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F) {
         this.owner.addResilience(this.resilienceGain);
         this.resilienceGain = 0.0F;
      }

   }

   public void hitObject(LevelObjectHit var1) {
      if (this.canDamageObjects) {
         var1.getLevelObject().attackThrough(this.damage, this.owner);
      }

   }
}
