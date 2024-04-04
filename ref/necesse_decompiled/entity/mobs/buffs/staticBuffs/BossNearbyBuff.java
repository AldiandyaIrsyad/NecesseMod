package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.Level;

public class BossNearbyBuff extends VicinityBuff {
   public BossNearbyBuff() {
      this.isVisible = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setMaxModifier(BuffModifiers.MOB_SPAWN_RATE, 0.0F);
      var1.setMaxModifier(BuffModifiers.MOB_SPAWN_CAP, 0.0F);
   }

   public static void applyAround(Level var0, float var1, float var2, int var3) {
      var0.entityManager.players.streamInRegionsShape(GameUtils.rangeBounds(var1, var2, var3), 0).filter((var3x) -> {
         return var3x.getDistance(var1, var2) <= (float)var3;
      }).forEach((var0x) -> {
         ActiveBuff var1 = new ActiveBuff(BuffRegistry.BOSS_NEARBY, var0x, 100, (Attacker)null);
         var0x.buffManager.addBuff(var1, false);
      });
   }

   public static void applyAround(Mob var0, int var1) {
      applyAround(var0.getLevel(), var0.x, var0.y, var1);
   }

   public static void applyAround(Mob var0) {
      applyAround(var0, 1600);
   }
}
