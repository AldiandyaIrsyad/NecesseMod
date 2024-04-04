package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerShooterChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CryoMissileProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoFlakeFollowingMob extends FlyingAttackingFollowingMob {
   public CryoFlakeFollowingMob() {
      super(10);
      this.accelerationMod = 1.0F;
      this.moveAccuracy = 10;
      this.setSpeed(70.0F);
      this.setFriction(1.0F);
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-18, -15, 36, 36);
      this.selectBox = new Rectangle(-20, -18, 40, 36);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerFlyingFollowerShooterChaserAI<CryoFlakeFollowingMob>(576, CooldownAttackTargetAINode.CooldownTimer.TICK, 800, 480, 640, 64) {
         protected boolean shootAtTarget(CryoFlakeFollowingMob var1, Mob var2) {
            CryoMissileProjectile var3 = new CryoMissileProjectile(var1.getLevel(), var1, var1.x, var1.y, var2.x, var2.y, 150.0F, 800, CryoFlakeFollowingMob.this.damage, 10);
            var3.setTargetPrediction(var2);
            var1.getLevel().entityManager.projectiles.add(var3);
            return true;
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected boolean shootAtTarget(Mob var1, Mob var2) {
            return this.shootAtTarget((CryoFlakeFollowingMob)var1, var2);
         }
      }, new FlyingAIMover());
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 30; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.IMPORTANT_COSMETIC).movesConstant((float)(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1)), (float)(GameRandom.globalRandom.getIntBetween(5, 20) * (GameRandom.globalRandom.nextBoolean() ? -1 : 1))).color(new Color(88, 105, 218));
      }

   }

   protected void playDeathSound() {
      this.playHitSound();
   }

   protected void playHitSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.jinglehit, SoundEffect.effect(this).pitch(var1));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      Point var11 = new Point(MobRegistry.Textures.cryoFlakePet.getWidth() / 2, MobRegistry.Textures.cryoFlakePet.getHeight() / 2);
      int var12 = var8.getDrawX(var5) - var11.x;
      int var13 = var8.getDrawY(var6) - var11.y;
      float var14 = GameUtils.getTimeRotation(var4.getWorldEntity().getTime(), 4);
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.cryoFlakePet.initDraw().rotate(var14, var11.x, var11.y).light(var10).pos(var12, var13);
      var3.add((var1x) -> {
         var15.draw();
      });
   }
}
