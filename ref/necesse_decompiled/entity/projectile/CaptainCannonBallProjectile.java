package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.entity.levelEvent.explosionEvent.CaptainCannonBallExplosionEvent;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class CaptainCannonBallProjectile extends Projectile {
   private long spawnTime;

   public CaptainCannonBallProjectile() {
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.isSolid);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.isSolid = var1.getNextBoolean();
   }

   public void init() {
      super.init();
      this.setWidth(15.0F);
      this.height = 18.0F;
      this.heightBasedOnDistance = true;
      this.spawnTime = this.getWorldEntity().getTime();
      this.doesImpactDamage = false;
      this.trailOffset = 0.0F;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(30, 30, 30), 14.0F, 250, 18.0F);
   }

   public void clientTick() {
      super.clientTick();
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), this.texture.getHeight() / 2);
      }
   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         CaptainCannonBallExplosionEvent var5 = new CaptainCannonBallExplosionEvent(var3, var4, this.getDamage(), this.getOwner());
         this.getLevel().entityManager.addLevelEvent(var5);
      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("piratecap", 4);
   }
}
