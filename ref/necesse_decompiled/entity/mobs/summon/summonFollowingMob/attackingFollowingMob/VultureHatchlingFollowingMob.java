package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VultureHatchlingFollowingMob extends FlyingAttackingFollowingMob {
   private MobHitCooldowns hitCooldowns = new MobHitCooldowns();

   public VultureHatchlingFollowingMob() {
      super(10);
      this.accelerationMod = 1.0F;
      this.moveAccuracy = 10;
      this.setSpeed(60.0F);
      this.setFriction(1.0F);
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-18, -15, 36, 36);
      this.selectBox = new Rectangle(-20, -18, 40, 36);
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.damage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 15;
   }

   public void handleCollisionHit(Mob var1, GameDamage var2, int var3) {
      Mob var4 = this.getAttackOwner();
      if (var4 != null && var1 != null) {
         var1.isServerHit(var2, var1.x - var4.x, var1.y - var4.y, (float)var3, this);
         this.collisionHitCooldowns.startCooldown(var1);
      }

   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerFlyingFollowerCollisionChaserAI(576, (GameDamage)null, 15, 500, 640, 64), new FlyingAIMover());
   }

   public void setFacingDir(float var1, float var2) {
      if (var1 < 0.0F) {
         this.dir = 0;
      } else if (var1 > 0.0F) {
         this.dir = 1;
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.vultureHatchling, GameRandom.globalRandom.nextInt(4), 2, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 32;
      long var13 = var4.getWorldEntity().getTime() % 350L;
      byte var15;
      if (var13 < 100L) {
         var15 = 0;
      } else if (var13 < 200L) {
         var15 = 1;
      } else if (var13 < 300L) {
         var15 = 2;
      } else {
         var15 = 3;
      }

      float var16 = this.dx / 10.0F;
      TextureDrawOptionsEnd var17 = MobRegistry.Textures.vultureHatchling.initDraw().sprite(var15, 0, 64).light(var10).mirror(this.dir == 0, false).rotate(var16, 32, 32).pos(var11, var12);
      var3.add((var1x) -> {
         var17.draw();
      });
   }
}
