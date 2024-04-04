package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.BloodGrimoireParticleLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class ReturnLifeOnHitLevelEvent extends MouseBeamLevelEvent {
   public int lifeSteal = 0;

   public ReturnLifeOnHitLevelEvent() {
   }

   public ReturnLifeOnHitLevelEvent(Mob var1, int var2, int var3, int var4, float var5, float var6, GameDamage var7, int var8, int var9, float var10, int var11, float var12, Color var13, int var14) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.lifeSteal = var14;
   }

   public void serverHit(Mob var1, Packet var2, boolean var3) {
      this.player.toolHits.put(var1.getHitCooldownUniqueID(), this.player.getWorldEntity().getTime());
      var1.isServerHit(this.damage, var1.x - this.player.x, var1.y - this.player.y, (float)this.knockback, this.player);
      if (var1.canGiveResilience(this.owner) && this.resilienceGain != 0.0F && var1.getTime() >= this.lastResilienceGainTime + (long)this.hitCooldown) {
         this.owner.addResilience(this.resilienceGain);
         this.lastResilienceGainTime = var1.getTime();
      }

      int var4 = this.player.getHealth() + this.lifeSteal;
      this.player.setHealth(var4, 0.0F, 0.0F, var1);
      short var5 = 256;
      if (var1.buffManager.hasBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_MARKED_DEBUFF)) {
         GameUtils.streamTargetsRange(this.player, var1.getX(), var1.getY(), var5).filter((var1x) -> {
            return var1x != var1;
         }).filter((var0) -> {
            return var0.buffManager.hasBuff(BuffRegistry.Debuffs.BLOOD_GRIMOIRE_MARKED_DEBUFF);
         }).filter((var2x) -> {
            return var2x.getDistance(var1.x, var1.y) <= (float)var5;
         }).forEach((var2x) -> {
            var2x.isServerHit(this.damage.modFinalMultiplier(0.5F), var2x.x - this.player.x, var2x.y - this.player.y, (float)this.knockback, this.player);
            this.level.entityManager.addLevelEvent(new BloodGrimoireParticleLevelEvent(var2x, var1));
         });
      }

   }
}
