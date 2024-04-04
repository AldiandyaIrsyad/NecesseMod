package necesse.entity.objectEntity.interfaces;

import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public interface OEVicinityBuff {
   default void tickVicinityBuff(ObjectEntity var1) {
      Level var2 = var1.getLevel();
      int var3 = var1.getX() * 32 + 16;
      int var4 = var1.getY() * 32 + 16;
      this.tickVicinityBuff(var2, var3, var4);
   }

   default void tickVicinityBuff(Level var1, int var2, int var3) {
      int var4 = this.getBuffRange();
      if (var4 < 0) {
         throw new IllegalStateException("OEVicinityBuff must have a range higher than 0");
      } else {
         if (this.shouldBuffPlayers()) {
            var1.entityManager.players.streamInRegionsShape(GameUtils.rangeBounds(var2, var3, this.getBuffRange() + 1), 0).filter((var0) -> {
               return !var0.removed();
            }).filter((var3x) -> {
               return var3x.getDistance((float)var2, (float)var3) <= (float)var4;
            }).filter((var1x) -> {
               return this.buffPlayersFilter().test(var1x);
            }).forEach(this::applyBuffs);
         }

         if (this.shouldBuffMobs()) {
            Stream var5 = var1.entityManager.mobs.streamInRegionsShape(GameUtils.rangeBounds(var2, var3, this.getBuffRange() + 1), 0);
            var5.filter((var0) -> {
               return !var0.removed();
            }).filter((var3x) -> {
               return var3x.getDistance((float)var2, (float)var3) <= (float)var4;
            }).filter((var1x) -> {
               return this.buffMobsFilter().test(var1x);
            }).forEach(this::applyBuffs);
         }

      }
   }

   default void applyBuffs(Mob var1) {
      Buff[] var2 = this.getBuffs();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Buff var5 = var2[var4];
         if (var5 != null) {
            ActiveBuff var6 = new ActiveBuff(var5, var1, 100, (Attacker)null);
            var1.buffManager.addBuff(var6, false);
         }
      }

   }

   Buff[] getBuffs();

   int getBuffRange();

   boolean shouldBuffPlayers();

   default Predicate<PlayerMob> buffPlayersFilter() {
      return (var0) -> {
         return true;
      };
   }

   boolean shouldBuffMobs();

   default Predicate<Mob> buffMobsFilter() {
      return (var0) -> {
         return true;
      };
   }
}
