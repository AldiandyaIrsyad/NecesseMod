package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.followingProjectile.MageSlimeBoltProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MageSlimeMob extends JumpingHostileMob {
   public static LootTable lootTable = new LootTable();
   public static GameDamage damage = new GameDamage(70.0F);

   public MageSlimeMob() {
      super(350);
      this.setSpeed(50.0F);
      this.setFriction(2.0F);
      this.setArmor(30);
      this.jumpStats.setJumpStrength(150.0F);
      this.jumpStats.setJumpCooldown(50);
      this.spawnLightThreshold = (new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0)).min(150, Integer.MAX_VALUE);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-15, -32, 30, 40);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<MageSlimeMob>((Supplier)null, 512, 384, 40000, false, false) {
         public boolean attackTarget(MageSlimeMob var1, Mob var2) {
            if (var1.canAttack() && MageSlimeMob.this.dx == 0.0F && MageSlimeMob.this.dy == 0.0F && !MageSlimeMob.this.isAccelerating() && !MageSlimeMob.this.hasCurrentMovement()) {
               var1.attack(var2.getX(), var2.getY(), false);
               MageSlimeBoltProjectile var3 = new MageSlimeBoltProjectile(var1.getLevel(), var1, var1.x, var1.y, var2.x, var2.y, 60.0F, 768, MageSlimeMob.damage, 50);
               var3.moveDist(15.0);
               var3.target = var2;
               var1.getLevel().entityManager.projectiles.add(var3);
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.attackTarget((MageSlimeMob)var1, var2);
         }
      });
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.mageSlime.body, var3, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 32;
      int var12 = var8.getDrawY(var6) - 50;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.mageSlime.body.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      float var15 = this.getAttackAnimProgress();
      final DrawOptions var16;
      if (this.isAttacking) {
         var16 = ItemAttackDrawOptions.start(this.dir).itemSprite(MobRegistry.Textures.mageSlime.body, 0, 9, 32).itemRotatePoint(6, 6).itemEnd().armSprite(MobRegistry.Textures.mageSlime.body, 0, 8, 32).armRotatePoint(8, 15).swingRotation(var15).light(var10).pos(var11, var12 + 10);
      } else {
         var16 = null;
      }

      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
            if (var16 != null) {
               var16.draw();
            }

         }
      });
      TextureDrawOptionsEnd var17 = MobRegistry.Textures.mageSlime.shadow.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var17.draw();
      });
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      return this.inLiquid(var1, var2) ? new Point(6, var3) : new Point(this.getJumpAnimationFrame(6), var3);
   }

   public boolean isSlimeImmune() {
      return true;
   }
}
