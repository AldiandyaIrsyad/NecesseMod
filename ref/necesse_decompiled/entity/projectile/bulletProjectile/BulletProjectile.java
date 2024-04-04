package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class BulletProjectile extends Projectile {
   public BulletProjectile() {
      this.height = 18.0F;
   }

   public BulletProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this();
      this.setLevel(var9.getLevel());
      this.applyData(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void init() {
      super.init();
      this.givesLight = true;
      this.trailOffset = 0.0F;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.height);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.height = var1.getNextFloat();
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(150, 50, 0), 22.0F, 100, this.getHeight());
      var1.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
      return var1;
   }

   protected Color getWallHitColor() {
      return new Color(150, 50, 0);
   }

   public void refreshParticleLight() {
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 0.0F, this.lightSaturation);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.gunhit, SoundEffect.effect(var1, var2));
   }
}
