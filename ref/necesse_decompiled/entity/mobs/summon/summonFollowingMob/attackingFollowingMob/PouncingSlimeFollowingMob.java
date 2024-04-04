package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PouncingSlimeFollowingMob extends AttackingFollowingJumpingMob {
   public IntMobAbility startPounceAbility;
   public CoordinateMobAbility pounceAbility;
   public Mob pounceTarget;
   public long pounceStartTime;
   public long pounceLastTime;
   public int pounceChargeTime;
   public CameraShake pounceShake;

   public PouncingSlimeFollowingMob() {
      super(10);
      this.setSpeed(60.0F);
      this.setFriction(2.0F);
      this.jumpStats.setJumpAnimationTime(400);
      this.jumpStats.setJumpStrength(200.0F);
      this.jumpStats.setJumpCooldown(50);
      this.jumpStats.jumpStrengthUseSpeedMod = false;
      this.collision = new Rectangle(-8, -6, 16, 12);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-12, -16, 26, 24);
      this.startPounceAbility = (IntMobAbility)this.registerAbility(new IntMobAbility() {
         protected void run(int var1) {
            if (var1 >= 0) {
               PouncingSlimeFollowingMob.this.pounceStartTime = PouncingSlimeFollowingMob.this.getWorldEntity().getLocalTime();
               PouncingSlimeFollowingMob.this.pounceChargeTime = var1;
            } else {
               PouncingSlimeFollowingMob.this.pounceStartTime = 0L;
            }

         }
      });
      this.pounceAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            float var3 = GameMath.limit(PouncingSlimeFollowingMob.this.getDistance((float)var1, (float)var2), 128.0F, 512.0F);
            Point2D.Float var4 = GameMath.normalize((float)(var1 - PouncingSlimeFollowingMob.this.getX()), (float)(var2 - PouncingSlimeFollowingMob.this.getY()));
            PouncingSlimeFollowingMob.this.runJump(var4.x * var3 * 3.0F, var4.y * var3 * 3.0F);
            PouncingSlimeFollowingMob.this.pounceStartTime = 0L;
            PouncingSlimeFollowingMob.this.pounceLastTime = PouncingSlimeFollowingMob.this.getWorldEntity().getLocalTime();
         }
      });
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.damage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 20;
   }

   public void handleCollisionHit(Mob var1, GameDamage var2, int var3) {
      Mob var4 = this.getAttackOwner();
      if (var4 != null && var1 != null) {
         var1.isServerHit(var2, var1.x - var4.x, var1.y - var4.y, (float)var3, this);
         this.collisionHitCooldowns.startCooldown(var1);
      }

   }

   public boolean canCollisionHit(Mob var1) {
      return super.canCollisionHit(var1);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PouncingSlimeAI(576, CooldownAttackTargetAINode.CooldownTimer.CAN_ATTACK, 500, 256, 640, 96));
   }

   public boolean isPouncing() {
      return this.pounceStartTime > 0L;
   }

   protected void calcAcceleration(float var1, float var2, float var3, float var4, float var5) {
      if (this.isPouncing()) {
         if (this.isServer()) {
            if (this.pounceTarget != null && this.isSamePlace(this.pounceTarget) && this.getDistance(this.pounceTarget) <= 288.0F) {
               long var6 = this.getWorldEntity().getLocalTime() - this.pounceStartTime;
               if (var6 >= (long)this.pounceChargeTime) {
                  if (!this.getLevel().collides((Shape)(new MovedRectangle(this, this.pounceTarget.getX(), this.pounceTarget.getY())), (CollisionFilter)this.getLevelCollisionFilter())) {
                     this.ai.blackboard.put("currentTarget", this.pounceTarget);
                     this.ai.blackboard.put("chaserTarget", this.pounceTarget);
                     Point2D.Float var8 = Projectile.getPredictedTargetPos(this.pounceTarget, this.x, this.y, Math.max(16.0F, this.getDistance(this.pounceTarget)), 0.0F);
                     this.pounceAbility.runAndSend((int)var8.x, (int)var8.y);
                  } else {
                     this.startPounceAbility.runAndSend(-1);
                  }
               }
            } else {
               this.startPounceAbility.runAndSend(-1);
            }
         }

         super.calcAcceleration(var1, var2, 0.0F, 0.0F, var5);
      } else {
         super.calcAcceleration(var1, var2, var3, var4, var5);
      }

   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.pouncingSlime, var3, 4, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void playDeathSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.npcdeath, SoundEffect.effect(this).volume(0.1F).pitch(var1));
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
      final TextureDrawOptionsEnd var17 = MobRegistry.Textures.pouncingSlime.initDraw().sprite(var14, var13 ? 1 : 0, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
         }
      });
      if (!var13) {
         TextureDrawOptionsEnd var16 = MobRegistry.Textures.pouncingSlime_shadow.initDraw().sprite(var14, 0, 64).light(var10).pos(var11, var12);
         var2.add((var1x) -> {
            var16.draw();
         });
      }

   }

   public static class PouncingSlimeAI extends SelectorAINode<PouncingSlimeFollowingMob> {
      public PouncingSlimeAI(int var1, CooldownAttackTargetAINode.CooldownTimer var2, int var3, int var4, int var5, int var6) {
         SequenceAINode var7 = new SequenceAINode();
         var7.addChild(new FollowerBaseSetterAINode());
         var7.addChild(new FollowerFocusTargetSetterAINode());
         final SummonTargetFinderAINode var8 = new SummonTargetFinderAINode(var1);
         var7.addChild(var8);
         var7.addChild(new FollowerAINode<PouncingSlimeFollowingMob>(-1, var4 - 100) {
            public Mob getFollowingMob(PouncingSlimeFollowingMob var1) {
               return (Mob)this.getBlackboard().getObject(Mob.class, var8.currentTargetKey);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Mob getFollowingMob(Mob var1) {
               return this.getFollowingMob((PouncingSlimeFollowingMob)var1);
            }
         });
         CooldownAttackTargetAINode var9 = new CooldownAttackTargetAINode<PouncingSlimeFollowingMob>(var2, var3, var4) {
            public boolean canAttackTarget(PouncingSlimeFollowingMob var1, Mob var2) {
               if (!var1.inLiquid() && !var1.isPouncing()) {
                  return !var1.getLevel().collides((Shape)(new MovedRectangle(var1, var2.getX(), var2.getY())), (CollisionFilter)var1.getLevelCollisionFilter());
               } else {
                  return false;
               }
            }

            public boolean attackTarget(PouncingSlimeFollowingMob var1, Mob var2) {
               var1.pounceTarget = var2;
               var1.startPounceAbility.runAndSend(500);
               return true;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean attackTarget(Mob var1, Mob var2) {
               return this.attackTarget((PouncingSlimeFollowingMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean canAttackTarget(Mob var1, Mob var2) {
               return this.canAttackTarget((PouncingSlimeFollowingMob)var1, var2);
            }
         };
         var9.randomizeAttackTimer();
         var7.addChild(var9);
         this.addChild(var7);
         this.addChild(new PlayerFlyingFollowerAINode(var5, var6));
      }
   }
}
