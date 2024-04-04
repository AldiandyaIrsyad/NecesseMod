package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class OrbOfSlimesFollowingMob extends FlyingAttackingFollowingMob {
   public OrbOfSlimesFollowingMob() {
      super(10);
      this.moveAccuracy = 5;
      this.setSpeed(200.0F);
      this.setFriction(2.0F);
      this.collision = new Rectangle(-30, -30, 60, 60);
      this.hitBox = new Rectangle(-30, -30, 60, 60);
      this.selectBox = new Rectangle();
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.damage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 105;
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
      this.collisionHitCooldowns.hitCooldown = 500;
      this.ai = new BehaviourTreeAI(this, new PlayerFlyingFollowerCollisionAI(576, (GameDamage)null, 800, 100, 1024, 64), new FlyingAIMover());
   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).lifeTime(1000).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).movesConstant(this.dx / 10.0F, this.dy / 10.0F).color(new Color(70, 178, 170));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 30; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).movesConstantAngle((float)GameRandom.globalRandom.nextInt(360), (float)GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(70, 178, 170));
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath2, SoundEffect.effect(this).volume(0.5F));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 10;
      int var13 = GameUtils.getAnim(this.getTime(), 6, 600);
      TextureDrawOptionsEnd var14 = MobRegistry.Textures.orbOfSlimesSlime.body.initDraw().sprite(var13, 0, 32, 32).light(var10).pos(var11, var12);
      var3.add((var1x) -> {
         var14.draw();
      });
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.orbOfSlimesSlime.shadow.initDraw().sprite(var13, 0, 32, 32).light(var10).pos(var11, var12 + 10);
      var2.add((var1x) -> {
         var15.draw();
      });
   }
}
