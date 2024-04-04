package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SlimeGreatswordFollowingMob extends PouncingSlimeFollowingMob {
   public int lifeTime = 8000;

   public SlimeGreatswordFollowingMob() {
   }

   public void handleCollisionHit(Mob var1, GameDamage var2, int var3) {
      super.handleCollisionHit(var1, var2, var3);
      this.remove(0.0F, 0.0F, (Attacker)null, true);
   }

   public void serverTick() {
      super.serverTick();
      this.lifeTime -= 50;
      if (this.lifeTime <= 0) {
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.greatswordSlime, var3, 4, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 26 - 32;
      boolean var13 = this.inLiquid(var5, var6);
      int var14;
      if (var13) {
         var14 = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 1000);
      } else {
         var14 = this.getJumpAnimationFrame(6);
         if (var14 == 0 && this.isPouncing()) {
            if (this.pounceShake == null || this.pounceShake.isOver(this.getWorldEntity().getLocalTime())) {
               this.pounceShake = new CameraShake(this.getWorldEntity().getLocalTime(), 1000, 50, 2.0F, 2.0F, true);
            }

            Point2D.Float var15 = this.pounceShake.getCurrentShake(this.getWorldEntity().getLocalTime());
            var11 = (int)((float)var11 + var15.x);
            var12 = (int)((float)var12 + var15.y);
            var14 = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 200);
         }
      }

      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var17 = MobRegistry.Textures.greatswordSlime.initDraw().sprite(var14, var13 ? 1 : 0, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
         }
      });
      if (!var13) {
         TextureDrawOptionsEnd var16 = MobRegistry.Textures.greatswordSlime_shadow.initDraw().sprite(var14, 0, 64).light(var10).pos(var11, var12);
         var2.add((var1x) -> {
            var16.draw();
         });
      }

   }
}
