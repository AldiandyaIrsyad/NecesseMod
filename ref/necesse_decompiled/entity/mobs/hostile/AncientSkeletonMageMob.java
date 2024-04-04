package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.decorators.FailerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TeleportOnProjectileHitAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.AncientSkeletonMageProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AncientSkeletonMageMob extends HostileMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("bone", 1, 3)});
   public final CoordinateMobAbility teleportAbility;

   public AncientSkeletonMageMob() {
      super(225);
      this.attackAnimTime = 200;
      this.setSpeed(50.0F);
      this.setFriction(3.0F);
      this.setKnockbackModifier(0.4F);
      this.setArmor(25);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.teleportAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            if (AncientSkeletonMageMob.this.isClient()) {
               AncientSkeletonMageMob.this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(AncientSkeletonMageMob.this.getLevel(), AncientSkeletonMageMob.this.x, AncientSkeletonMageMob.this.y, new Color(58, 22, 100))), Particle.GType.CRITICAL);
               AncientSkeletonMageMob.this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(AncientSkeletonMageMob.this.getLevel(), (float)var1, (float)var2, new Color(58, 22, 100))), Particle.GType.CRITICAL);
            }

            AncientSkeletonMageMob.this.setPos((float)var1, (float)var2, true);
         }
      });
   }

   public void init() {
      super.init();
      PlayerChaserWandererAI var1 = new PlayerChaserWandererAI<AncientSkeletonMageMob>((Supplier)null, 640, 320, 40000, false, false) {
         public boolean attackTarget(AncientSkeletonMageMob var1, Mob var2) {
            if (var1.canAttack()) {
               var1.attack(var2.getX(), var2.getY(), false);
               var1.getLevel().entityManager.projectiles.add(new AncientSkeletonMageProjectile(var1.getLevel(), var1, var1.x, var1.y, var2.x, var2.y, 120.0F, 640, new GameDamage(65.0F), 50));
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.attackTarget((AncientSkeletonMageMob)var1, var2);
         }
      };
      var1.addChildFirst(new FailerAINode(new TeleportOnProjectileHitAINode<AncientSkeletonMageMob>(3000, 7) {
         public boolean teleport(AncientSkeletonMageMob var1, int var2, int var3) {
            if (var1.isServer()) {
               var1.teleportAbility.runAndSend(var2, var3);
               this.getBlackboard().mover.stopMoving(var1);
            }

            return true;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean teleport(Mob var1, int var2, int var3) {
            return this.teleport((AncientSkeletonMageMob)var1, var2, var3);
         }
      }));
      this.ai = new BehaviourTreeAI(this, var1);
   }

   public void clientTick() {
      super.clientTick();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("skeleton", 3);
   }

   public void playHitSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.crack, SoundEffect.effect(this).volume(1.6F).pitch(var1));
   }

   protected void playDeathSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.crackdeath, SoundEffect.effect(this).volume(0.8F).pitch(var1));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.ancientSkeletonMage.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.ancientSkeletonMage)).sprite(var13).dir(this.dir).light(var10);
      float var15 = this.getAttackAnimProgress();
      if (this.isAttacking) {
         ItemAttackDrawOptions var16 = ItemAttackDrawOptions.start(this.dir).itemSprite(MobRegistry.Textures.ancientSkeletonMage.body, 0, 9, 32).itemRotatePoint(4, 4).itemEnd().armSprite(MobRegistry.Textures.ancientSkeletonMage.body, 0, 8, 32).swingRotation(var15).light(var10);
         var14.attackAnim(var16, var15);
      }

      final DrawOptions var17 = var14.pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public int getRockSpeed() {
      return 20;
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.isClient()) {
         Screen.playSound(GameResources.swing2, SoundEffect.effect(this).volume(0.7F).pitch(1.2F));
      }

   }
}
