package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DuskMoonDiscFollowingMob extends FlyingAttackingFollowingMob {
   public int lifeTime = 5000;
   private int currentRotation;
   private float toMove;
   private float particleBuffer;

   public DuskMoonDiscFollowingMob() {
      super(10);
      this.moveAccuracy = 15;
      this.setSpeed(160.0F);
      this.setFriction(2.0F);
      this.collision = new Rectangle(-16, -16, 36, 36);
      this.hitBox = new Rectangle(-16, -16, 36, 36);
      this.selectBox = new Rectangle();
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
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public void serverTick() {
      super.serverTick();
      this.lifeTime -= 50;
      if (this.lifeTime <= 0) {
         this.remove(0.0F, 0.0F, (Attacker)null, true);
      }

   }

   public void tickMovement(float var1) {
      this.toMove += var1;

      while(this.toMove > 4.0F) {
         super.tickMovement(4.0F);
         this.toMove -= 4.0F;
         this.currentRotation -= 4;
         if (this.isClient()) {
            this.spawnTrailParticles();
         }
      }

   }

   private void spawnTrailParticles() {
      if (this.particleBuffer > 10.0F) {
         GameRandom var1 = GameRandom.globalRandom;
         this.getLevel().entityManager.addParticle(this.x + (float)var1.getIntBetween(-8, 8), this.y + (float)var1.getIntBetween(-8, 8), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.magicSparkParticles.sprite(var1.nextInt(3), 0, 22)).sizeFades(10, 20).movesConstant(-this.dx / (float)var1.getIntBetween(5, 10), -this.dy / (float)var1.getIntBetween(5, 10)).height(10.0F).color(new Color(220, 212, 255)).givesLight(0.0F, 0.0F).lifeTime(2000);
         this.particleBuffer = 0.0F;
      }

      ++this.particleBuffer;
   }

   public void spawnDeathParticles(float var1, float var2) {
      super.spawnDeathParticles(var1, var2);

      for(int var3 = 0; var3 < 30; ++var3) {
         float var4 = GameRandom.globalRandom.getFloatBetween(-30.0F, 30.0F);
         float var5 = GameRandom.globalRandom.getFloatBetween(-30.0F, 30.0F);
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).sizeFades(10, 20).movesConstant(var4, var5).height(10.0F).givesLight(0.0F, 0.0F).lifeTime(2000);
      }

   }

   protected void playDeathSound() {
      super.playDeathSound();
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerFlyingFollowerCollisionChaserAI(576, (GameDamage)null, 15, 500, 640, 64), new FlyingAIMover());
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 20;
      TextureDrawOptionsEnd var13 = MobRegistry.Textures.duskMoonDisc.initDraw().light(var10).rotate((float)this.currentRotation, 16, 16).pos(var11, var12);
      var3.add((var1x) -> {
         var13.draw();
      });
   }
}
