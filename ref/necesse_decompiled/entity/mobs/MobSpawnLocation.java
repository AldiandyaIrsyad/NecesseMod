package necesse.entity.mobs;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.buffs.BuffModifiers;

public class MobSpawnLocation {
   public final Mob mob;
   public final int x;
   public final int y;
   private boolean valid;

   public MobSpawnLocation(Mob var1, int var2, int var3) {
      this.mob = var1;
      this.x = var2;
      this.y = var3;
      this.valid = true;
   }

   public void apply() {
      this.mob.setPos((float)this.x, (float)this.y, true);
   }

   public boolean valid() {
      return this.valid;
   }

   public boolean validAndApply() {
      if (this.valid()) {
         this.apply();
         return true;
      } else {
         return false;
      }
   }

   public MobSpawnLocation checkMobSpawnLocation() {
      return this.mob.checkSpawnLocation(this);
   }

   public MobSpawnLocation checkMaxHostilesAround(int var1, int var2, ServerClient var3) {
      return this.checkMaxMobsAround(var1, var2, (var0) -> {
         return var0.isHostile;
      }, var3);
   }

   public MobSpawnLocation checkMaxMobsAround(int var1, int var2, Predicate<Mob> var3, ServerClient var4) {
      if (this.valid) {
         long var5 = this.mob.getLevel().entityManager.mobs.streamInRegionsInTileRange(this.x, this.y, var2).filter((var2x) -> {
            return GameMath.squareDistance((float)this.x, (float)this.y, var2x.x, var2x.y) <= (float)(var2 * 32);
         }).filter(var3).count();
         float var7 = 1.0F;
         if (var4 != null) {
            var7 = (Float)var4.playerMob.buffManager.getModifier(BuffModifiers.MOB_SPAWN_CAP);
         }

         this.valid = (float)var5 < (float)var1 * var7;
      }

      return this;
   }

   public MobSpawnLocation checkLocation(BiFunction<Integer, Integer, Boolean> var1) {
      this.valid = this.valid && (Boolean)var1.apply(this.x, this.y);
      return this;
   }

   public MobSpawnLocation checkTile(BiFunction<Integer, Integer, Boolean> var1) {
      this.valid = this.valid && (Boolean)var1.apply(this.x / 32, this.y / 32);
      return this;
   }

   public MobSpawnLocation checkLightThreshold(ServerClient var1) {
      Modifier var3 = BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD;
      int var2;
      if (var1 != null) {
         var2 = (Integer)var1.playerMob.buffManager.getAndApplyModifiers(var3, new ModifierValue[]{this.mob.spawnLightThreshold});
      } else {
         var2 = (Integer)this.mob.spawnLightThreshold.limits.applyModifierLimits(var3, (Integer)var3.finalLimit((Integer)var3.appendManager((Integer)var3.defaultBuffManagerValue, (Integer)this.mob.spawnLightThreshold.value)));
      }

      return this.checkMaxLightThreshold(var2);
   }

   public MobSpawnLocation checkStaticLightThreshold(ServerClient var1) {
      Modifier var3 = BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD;
      int var2;
      if (var1 != null) {
         var2 = (Integer)var1.playerMob.buffManager.getAndApplyModifiers(var3, new ModifierValue[]{this.mob.spawnLightThreshold});
      } else {
         var2 = (Integer)this.mob.spawnLightThreshold.limits.applyModifierLimits(var3, (Integer)var3.finalLimit((Integer)var3.appendManager((Integer)var3.defaultBuffManagerValue, (Integer)this.mob.spawnLightThreshold.value)));
      }

      return this.checkMaxLightThreshold(var2);
   }

   public MobSpawnLocation checkMaxLightThreshold(int var1) {
      return this.checkTile((var2, var3) -> {
         return this.mob.getLevel().lightManager.getAmbientAndStaticLightLevel(var2, var3).getLevel() <= (float)var1;
      });
   }

   public MobSpawnLocation checkMinLightThreshold(int var1) {
      return this.checkTile((var2, var3) -> {
         return this.mob.getLevel().lightManager.getAmbientAndStaticLightLevel(var2, var3).getLevel() >= (float)var1;
      });
   }

   public MobSpawnLocation checkMaxStaticLightThreshold(int var1) {
      return this.checkTile((var2, var3) -> {
         return this.mob.getLevel().lightManager.getStaticLight(var2, var3).getLevel() <= (float)var1;
      });
   }

   public MobSpawnLocation checkMinStaticLightThreshold(int var1) {
      return this.checkTile((var2, var3) -> {
         return this.mob.getLevel().lightManager.getStaticLight(var2, var3).getLevel() >= (float)var1;
      });
   }

   public MobSpawnLocation checkNotLevelCollides() {
      return this.checkLocation((var1, var2) -> {
         return !this.mob.collidesWith(this.mob.getLevel(), var1, var2);
      });
   }

   public MobSpawnLocation checkNotOnSurfaceInsideOnFloor() {
      return this.checkTile((var1, var2) -> {
         return this.mob.getLevel().isCave || this.mob.getLevel().isOutside(var1, var2) || !this.mob.getLevel().getTile(var1, var2).isFloor;
      });
   }

   public MobSpawnLocation checkNotInsideOnFloor() {
      return this.checkTile((var1, var2) -> {
         return this.mob.getLevel().isOutside(var1, var2) || !this.mob.getLevel().getTile(var1, var2).isFloor;
      });
   }

   public MobSpawnLocation checkNotSolidTile() {
      return this.checkTile((var1, var2) -> {
         return !this.mob.getLevel().isSolidTile(var1, var2);
      });
   }

   public MobSpawnLocation checkNotInLiquid() {
      return this.checkTile((var1, var2) -> {
         return !this.mob.getLevel().getTile(var1, var2).isLiquid;
      });
   }

   public MobSpawnLocation checkInLiquid() {
      return this.checkTile((var1, var2) -> {
         return this.mob.getLevel().getTile(var1, var2).isLiquid;
      });
   }
}
