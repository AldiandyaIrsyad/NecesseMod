package necesse.entity.mobs.hostile.bosses;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionShooterPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.VultureHatchlingProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VultureHatchling extends FlyingBossMob {
   public static LootTable lootTable = new LootTable();
   public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(50, 80, 100, 120, 160);
   private AncientVultureMob owner;

   public VultureHatchling() {
      this((AncientVultureMob)null);
   }

   public VultureHatchling(AncientVultureMob var1) {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.isSummoned = true;
      this.owner = var1;
      this.moveAccuracy = 10;
      this.setSpeed(60.0F);
      this.setArmor(20);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.3F);
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-18, -15, 36, 36);
      this.selectBox = new Rectangle(-20, -18, 40, 36);
   }

   public boolean canBePushed(Mob var1) {
      return true;
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionShooterPlayerChaserWandererAI<VultureHatchling>((Supplier)null, 1600, AncientVultureMob.hatchlingCollision, 100, CooldownAttackTargetAINode.CooldownTimer.TICK, 2000, 800, 40000) {
         public boolean shootAtTarget(VultureHatchling var1, Mob var2) {
            if (VultureHatchling.this.canAttack() && var1.getDistance(var2) > 96.0F) {
               VultureHatchling.this.getLevel().entityManager.projectiles.add(new VultureHatchlingProjectile(var1, var1.getX(), var1.getY(), var2.getX(), var2.getY(), AncientVultureMob.hatchlingProjectile));
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean shootAtTarget(Mob var1, Mob var2) {
            return this.shootAtTarget((VultureHatchling)var1, var2);
         }
      }, new FlyingAIMover());
   }

   public void setFacingDir(float var1, float var2) {
      if (var1 < 0.0F) {
         this.dir = 0;
      } else if (var1 > 0.0F) {
         this.dir = 1;
      }

   }

   public LootTable getLootTable() {
      return lootTable;
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
