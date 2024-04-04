package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CaveSpiderSpitEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class CaveSpiderSpitProjectile extends Projectile {
   public GiantCaveSpiderMob.Variant variant;

   public CaveSpiderSpitProjectile() {
      this.variant = GiantCaveSpiderMob.Variant.NORMAL;
   }

   public CaveSpiderSpitProjectile(GiantCaveSpiderMob.Variant var1, float var2, float var3, float var4, float var5, GameDamage var6, Mob var7, int var8) {
      this.variant = GiantCaveSpiderMob.Variant.NORMAL;
      this.variant = var1;
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = 80.0F;
      this.setDamage(var6);
      this.setOwner(var7);
      this.setDistance(var8);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.variant.ordinal());
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.variant = GiantCaveSpiderMob.Variant.values()[var1.getNextShortUnsigned()];
   }

   public void init() {
      super.init();
      this.canHitMobs = false;
      this.height = 16.0F;
   }

   public Color getParticleColor() {
      return this.variant.particleColor;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 16.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         Mob var5 = this.getOwner();
         if (var5 != null && !var5.removed()) {
            CaveSpiderSpitEvent var6 = new CaveSpiderSpitEvent(var5, (int)var3, (int)var4, GameRandom.globalRandom, this.variant, this.getDamage(), Integer.MAX_VALUE);
            this.getLevel().entityManager.addLevelEvent(var6);
         }

      }
   }
}
