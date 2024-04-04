package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerChargeChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChargingPhantomFollowingMob extends FlyingAttackingFollowingMob {
   public Trail trail;
   public float moveAngle;
   private float toMove;

   public ChargingPhantomFollowingMob() {
      super(10);
      this.moveAccuracy = 15;
      this.setSpeed(200.0F);
      this.setFriction(2.0F);
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-18, -15, 36, 36);
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
      }

   }

   public void init() {
      super.init();
      this.collisionHitCooldowns.hitCooldown = 750;
      this.ai = new BehaviourTreeAI(this, new PlayerFlyingFollowerChargeChaserAI(576, CooldownAttackTargetAINode.CooldownTimer.TICK, 800, 100, 1024, 64), new FlyingAIMover());
      if (this.isClient()) {
         this.trail = new Trail(this, this.getLevel(), new Color(22, 20, 45), 16.0F, 200, 0.0F);
         this.trail.drawOnTop = true;
         this.trail.removeOnFadeOut = false;
         this.getLevel().entityManager.addTrail(this.trail);
      }

   }

   public void tickMovement(float var1) {
      this.toMove += var1;

      while(this.toMove > 4.0F) {
         float var2 = this.x;
         float var3 = this.y;
         super.tickMovement(4.0F);
         this.toMove -= 4.0F;
         Point2D.Float var4 = GameMath.normalize(var2 - this.x, var3 - this.y);
         this.moveAngle = (float)Math.toDegrees(Math.atan2((double)var4.y, (double)var4.x)) - 90.0F;
         if (this.trail != null) {
            float var5 = 5.0F;
            this.trail.addPoint(new TrailVector(this.x + var4.x * var5, this.y + var4.y * var5, -var4.x, -var4.y, this.trail.thickness, 0.0F));
         }
      }

   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.dx / 10.0F, this.dy / 10.0F).color(new Color(36, 33, 75));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 30; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstantAngle((float)GameRandom.globalRandom.nextInt(360), (float)GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(36, 33, 75));
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this).volume(0.5F));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 10;
      int var13 = GameUtils.getAnim(this.getTime(), 6, 400);
      TextureDrawOptionsEnd var14 = MobRegistry.Textures.chargingPhantom.body.initDraw().sprite(var13, 0, 32, 64).light(var10).rotate(this.moveAngle, 16, 10).pos(var11, var12);
      var3.add((var1x) -> {
         var14.draw();
      });
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.chargingPhantom.shadow.initDraw().sprite(var13, 1, 32, 64).light(var10).rotate(this.moveAngle, 16, 10).pos(var11, var12 + 10);
      var2.add((var1x) -> {
         var15.draw();
      });
   }

   public void dispose() {
      super.dispose();
      if (this.trail != null) {
         this.trail.removeOnFadeOut = true;
      }

   }
}
