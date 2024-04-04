package necesse.entity.projectile.modifiers;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.LevelObjectHit;

public class ResilienceOnHitProjectileModifier extends ProjectileModifier {
   private float resilienceGain;
   private boolean hasGained = false;

   public ResilienceOnHitProjectileModifier() {
   }

   public ResilienceOnHitProjectileModifier(float var1) {
      this.resilienceGain = var1;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.resilienceGain);
      var1.putNextBoolean(this.hasGained);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.resilienceGain = var1.getNextFloat();
      this.hasGained = var1.getNextBoolean();
   }

   public void initChildProjectile(Projectile var1, float var2, int var3) {
      super.initChildProjectile(var1, var2, var3);
      var1.setModifier(new ResilienceOnHitProjectileModifier(this.resilienceGain / (float)var3));
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (!this.hasGained) {
         Mob var5 = this.projectile.getOwner();
         if (var1 != null && var5 != null && var1.canGiveResilience(var5)) {
            var5.addResilience(this.resilienceGain);
            this.hasGained = true;
         }
      }

   }
}
