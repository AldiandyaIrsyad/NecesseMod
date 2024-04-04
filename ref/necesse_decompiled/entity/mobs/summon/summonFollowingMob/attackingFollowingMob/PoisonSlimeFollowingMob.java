package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PoisonSlimeFollowingMob extends AttackingFollowingJumpingMob {
   public int lifeTime = 8000;

   public PoisonSlimeFollowingMob() {
      super(10);
      this.setSpeed(60.0F);
      this.setFriction(2.0F);
      this.jumpStats.setJumpAnimationTime(250);
      this.jumpStats.setJumpStrength(150.0F);
      this.jumpStats.setJumpCooldown(50);
      this.jumpStats.jumpStrengthUseSpeedMod = false;
      this.collision = new Rectangle(-8, -6, 16, 12);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-12, -16, 26, 24);
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

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerFollowerCollisionChaserAI(384, (GameDamage)null, 30, 500, 640, 64));
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
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.poisonSlime, var3, 2, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void playDeathSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.npcdeath, SoundEffect.effect(this).volume(0.1F).pitch(var1));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 26;
      boolean var13 = this.inLiquid(var5, var6);
      int var14;
      if (var13) {
         var14 = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 1000);
      } else {
         var14 = this.getJumpAnimationFrame(6);
      }

      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var15 = MobRegistry.Textures.poisonSlime.initDraw().sprite(var14, var13 ? 1 : 0, 32).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
         }
      });
      if (!var13) {
         TextureDrawOptionsEnd var16 = MobRegistry.Textures.poisonSlime_shadow.initDraw().sprite(var14, 0, 32).light(var10).pos(var11, var12);
         var2.add((var1x) -> {
            var16.draw();
         });
      }

   }
}
