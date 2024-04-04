package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerChaserAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabySpiderkinArcher extends AttackingFollowingMob {
   protected int shotsRemaining;

   public BabySpiderkinArcher() {
      super(10);
      this.setSpeed(55.0F);
      this.setFriction(3.0F);
      this.moveAccuracy = 10;
      this.attackAnimTime = 250;
      this.attackCooldown = 1000;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-13, -30, 26, 40);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerFollowerChaserAI<BabySpiderkinArcher>(576, 320, false, false, 640, 64) {
         public boolean attackTarget(BabySpiderkinArcher var1, Mob var2) {
            if (var1.canAttack()) {
               if (BabySpiderkinArcher.this.shotsRemaining <= 0) {
                  BabySpiderkinArcher.this.shotsRemaining = 3;
               }

               --BabySpiderkinArcher.this.shotsRemaining;
               var1.attack(var2.getX(), var2.getY(), false);
               Projectile var3 = ProjectileRegistry.getProjectile("spideritearrow", var1.getLevel(), var1.x, var1.y, var2.x, var2.y, 160.0F, 640, BabySpiderkinArcher.this.damage, var1);
               var3.setTargetPrediction(var2, -20.0F);
               var3.moveDist(20.0);
               var1.getLevel().entityManager.projectiles.add(var3);
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.attackTarget((BabySpiderkinArcher)var1, var2);
         }
      });
   }

   public boolean canAttack() {
      if ((Boolean)this.buffManager.getModifier(BuffModifiers.INTIMIDATED)) {
         return false;
      } else {
         return super.canAttack() || this.shotsRemaining > 0 && this.getTimeSinceLastAttack() >= 150L;
      }
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.babySpiderkinArcher.body, var3, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

      this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.babySpiderkinArcher.body, (Integer)GameRandom.globalRandom.getOneOf((Object[])(0, 1)), 9, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      float var14 = this.getAttackAnimProgress();
      HumanDrawOptions var15 = (new HumanDrawOptions(var4, MobRegistry.Textures.babySpiderkinArcher)).sprite(var13).dir(this.dir).light(var10);
      if (this.isAttacking) {
         ItemAttackDrawOptions var16 = ItemAttackDrawOptions.start(this.dir).itemSprite(MobRegistry.Textures.babySpiderkinArcher.body, 2, 9, 32).itemRotatePoint(20, 20).itemEnd().armSprite(MobRegistry.Textures.babySpiderkinArcher.body, 0, 8, 32).pointRotation(this.attackDir.x, this.attackDir.y).light(var10);
         var15.attackAnim(var16, var14);
      }

      final DrawOptions var17 = var15.pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.human_baby_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(this.dir, 0, var6).light(var3).pos(var7, var8);
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.isClient()) {
         Screen.playSound(GameResources.bow, SoundEffect.effect(this).volume(0.3F));
      }

   }
}
