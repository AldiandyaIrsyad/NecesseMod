package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BoulderStaffProjectile extends Projectile {
   protected long spawnTime;
   protected float deltaHeight;
   protected float boulderHeight;

   public BoulderStaffProjectile() {
   }

   public BoulderStaffProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.x = var3;
      this.y = var4;
      this.speed = var7;
      this.setTarget(var5, var6);
      this.setDamage(var9);
      this.knockback = var10;
      this.setDistance(var8);
      this.setOwner(var2);
      this.boulderHeight = 18.0F;
      this.deltaHeight = 50.0F;
   }

   public void setupPositionPacket(PacketWriter var1) {
      super.setupPositionPacket(var1);
      var1.putNextFloat(this.deltaHeight);
      var1.putNextFloat(this.boulderHeight);
   }

   public void applyPositionPacket(PacketReader var1) {
      super.applyPositionPacket(var1);
      this.deltaHeight = var1.getNextFloat();
      this.boulderHeight = var1.getNextFloat();
   }

   public void init() {
      super.init();
      this.spawnTime = this.getWorldEntity().getTime();
      this.isSolid = true;
      this.width = 32.0F;
      this.trailOffset = 0.0F;
      this.piercing = 2;
   }

   public float tickMovement(float var1) {
      float var2 = super.tickMovement(var1);
      float var3 = 50.0F * var1 / 250.0F;
      this.deltaHeight -= var3;
      this.boulderHeight += this.deltaHeight * var1 / 250.0F;
      if (this.boulderHeight < 0.0F) {
         if (this.isClient()) {
            Screen.playSound(GameResources.punch, SoundEffect.effect(this).volume(0.5F).pitch(1.0F));
            ExplosionEvent.spawnExplosionParticles(this.getLevel(), this.x, this.y, 20, 0.0F, 20.0F, (var0, var1x, var2x, var3x, var4, var5, var6) -> {
               var0.entityManager.addParticle(var1x, var2x, Particle.GType.CRITICAL).movesConstant(var3x, var4).color(new Color(50, 50, 50)).lifeTime(var5);
            });
         }

         this.deltaHeight = -this.deltaHeight * 0.95F;
         this.boulderHeight = -this.boulderHeight;
         if (Math.abs(this.deltaHeight) < var3 * 2.0F) {
            this.boulderHeight = -1.0F;
            this.deltaHeight = 0.0F;
         }
      }

      this.height = Math.max(this.boulderHeight, 0.0F);
      return var2;
   }

   public Color getParticleColor() {
      return new Color(59, 38, 77);
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(50, 50, 50), 0.0F, 500, this.getHeight());
      var1.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
      return var1;
   }

   public float getTrailThickness() {
      return 30.0F;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5F;
         if (this.dx < 0.0F) {
            var12 = -var12;
         }

         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9).rotate(var12, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         float var14 = Math.abs(GameMath.limit(this.height / 100.0F, 0.0F, 1.0F) - 1.0F);
         float var15 = Math.abs(GameMath.limit(this.height / 100.0F, 0.0F, 1.0F) - 1.0F);
         int var16 = (int)((float)this.shadowTexture.getWidth() * var15);
         int var17 = (int)((float)this.shadowTexture.getHeight() * var15);
         int var18 = var7.getDrawX(this.x) - var16 / 2;
         int var19 = var7.getDrawY(this.y) - var17 / 2;
         TextureDrawOptionsEnd var20 = this.shadowTexture.initDraw().size(var16, var17).light(var9).alpha(var14).pos(var18, var19);
         var2.add((var1x) -> {
            var20.draw();
         });
      }
   }
}
