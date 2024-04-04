package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.CameraShake;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class GiantSwampSlimeMob extends JumpingHostileMob {
   public static LootTable lootTable = new LootTable();
   public static GameDamage baseDamage = new GameDamage(65.0F);
   public static GameDamage incursionDamage = new GameDamage(70.0F);
   public IntMobAbility startPounceAbility;
   public CoordinateMobAbility pounceAbility;
   public Mob pounceTarget;
   public long pounceStartTime;
   public int pounceChargeTime;
   public CameraShake pounceShake;

   public GiantSwampSlimeMob() {
      super(450);
      this.setSpeed(50.0F);
      this.setFriction(2.0F);
      this.setKnockbackModifier(0.6F);
      this.setArmor(25);
      this.jumpStats.setJumpStrength(150.0F);
      this.jumpStats.setJumpCooldown(150);
      this.collision = new Rectangle(-20, -10, 40, 16);
      this.hitBox = new Rectangle(-22, -20, 44, 28);
      this.selectBox = new Rectangle(-24, -40, 48, 48);
      this.startPounceAbility = (IntMobAbility)this.registerAbility(new IntMobAbility() {
         protected void run(int var1) {
            if (var1 >= 0) {
               GiantSwampSlimeMob.this.pounceStartTime = GiantSwampSlimeMob.this.getWorldEntity().getLocalTime();
               GiantSwampSlimeMob.this.pounceChargeTime = var1;
            } else {
               GiantSwampSlimeMob.this.pounceStartTime = 0L;
            }

         }
      });
      this.pounceAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            float var3 = Math.min(512.0F, GiantSwampSlimeMob.this.getDistance((float)var1, (float)var2));
            Point2D.Float var4 = GameMath.normalize((float)(var1 - GiantSwampSlimeMob.this.getX()), (float)(var2 - GiantSwampSlimeMob.this.getY()));
            GiantSwampSlimeMob.this.runJump(var4.x * var3 * 2.6F, var4.y * var3 * 2.6F);
            GiantSwampSlimeMob.this.pounceStartTime = 0L;
         }
      });
   }

   public void init() {
      super.init();
      GameDamage var1;
      if (this.getLevel() instanceof IncursionLevel) {
         this.setMaxHealth(500);
         this.setHealthHidden(this.getMaxHealth());
         this.setArmor(30);
         var1 = incursionDamage;
      } else {
         var1 = baseDamage;
      }

      this.ai = new BehaviourTreeAI(this, new GiantSwampSlimeAI(512, var1, 100, CooldownAttackTargetAINode.CooldownTimer.CAN_ATTACK, 3500, 480, 40000));
   }

   public boolean isPouncing() {
      return this.pounceStartTime > 0L;
   }

   protected void calcAcceleration(float var1, float var2, float var3, float var4, float var5) {
      if (this.isPouncing()) {
         if (this.isServer()) {
            if (this.pounceTarget != null && this.isSamePlace(this.pounceTarget)) {
               long var6 = this.getWorldEntity().getLocalTime() - this.pounceStartTime;
               if (var6 >= (long)this.pounceChargeTime) {
                  if (!this.getLevel().collides((Shape)(new MovedRectangle(this, this.pounceTarget.getX(), this.pounceTarget.getY())), (CollisionFilter)this.getLevelCollisionFilter())) {
                     this.ai.blackboard.put("currentTarget", this.pounceTarget);
                     this.ai.blackboard.put("chaserTarget", this.pounceTarget);
                     this.pounceAbility.runAndSend(this.pounceTarget.getX(), this.pounceTarget.getY());
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
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.giantSwampSlime, var3, 4, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public Point getPathMoveOffset() {
      return new Point(32, 32);
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
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
      final TextureDrawOptionsEnd var17 = MobRegistry.Textures.giantSwampSlime.initDraw().sprite(var14, var13 ? 1 : 0, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
         }
      });
      if (!var13) {
         TextureDrawOptionsEnd var16 = MobRegistry.Textures.giantSwampSlime_shadow.initDraw().sprite(var14, 0, 64).light(var10).pos(var11, var12);
         var2.add((var1x) -> {
            var16.draw();
         });
      }

   }

   public boolean isSlimeImmune() {
      return true;
   }

   public static class GiantSwampSlimeAI extends SelectorAINode<GiantSwampSlimeMob> {
      public GiantSwampSlimeAI(int var1, GameDamage var2, int var3, CooldownAttackTargetAINode.CooldownTimer var4, int var5, int var6, int var7) {
         this.addChild(new EscapeAINode<GiantSwampSlimeMob>() {
            public boolean shouldEscape(GiantSwampSlimeMob var1, Blackboard<GiantSwampSlimeMob> var2) {
               return var1.isHostile && !var1.isSummoned && (Boolean)var1.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean shouldEscape(Mob var1, Blackboard var2) {
               return this.shouldEscape((GiantSwampSlimeMob)var1, var2);
            }
         });
         CollisionPlayerChaserAI var8 = new CollisionPlayerChaserAI(var1, var2, var3);
         CooldownAttackTargetAINode var9 = new CooldownAttackTargetAINode<GiantSwampSlimeMob>(var4, var5, var6) {
            public boolean canAttackTarget(GiantSwampSlimeMob var1, Mob var2) {
               if (!var1.inLiquid() && !var1.isPouncing()) {
                  if (var1.getDistance(var2) < 64.0F) {
                     return false;
                  } else {
                     return !var1.getLevel().collides((Shape)(new MovedRectangle(var1, var2.getX(), var2.getY())), (CollisionFilter)var1.getLevelCollisionFilter());
                  }
               } else {
                  return false;
               }
            }

            public boolean attackTarget(GiantSwampSlimeMob var1, Mob var2) {
               var1.pounceTarget = var2;
               var1.startPounceAbility.runAndSend(1000);
               return true;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean attackTarget(Mob var1, Mob var2) {
               return this.attackTarget((GiantSwampSlimeMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean canAttackTarget(Mob var1, Mob var2) {
               return this.canAttackTarget((GiantSwampSlimeMob)var1, var2);
            }
         };
         var9.randomizeAttackTimer();
         var8.addChild(var9);
         this.addChild(var8);
         this.addChild(new WandererAINode(var7));
      }
   }
}
