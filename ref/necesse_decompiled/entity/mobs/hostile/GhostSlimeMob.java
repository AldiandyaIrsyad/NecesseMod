package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.tickManager.TicksPerSecond;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GhostSlimeMob extends JumpingHostileMob {
   public static LootTable lootTable = new LootTable();
   public static GameDamage damage = new GameDamage(70.0F);
   public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(5);
   public ParticleTypeSwitcher particleTypes;

   public GhostSlimeMob() {
      super(400);
      this.particleTypes = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      this.setSpeed(50.0F);
      this.setFriction(2.0F);
      this.setArmor(30);
      this.jumpStats.setJumpStrength(150.0F);
      this.jumpStats.setJumpCooldown(75);
      this.spawnLightThreshold = (new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0)).min(150, Integer.MAX_VALUE);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-15, -32, 30, 40);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI((Supplier)null, 512, damage, 100, 40000), new FlyingAIMover());
   }

   public void clientTick() {
      super.clientTick();
      this.particleTicks.gameTick();

      while(this.particleTicks.shouldTick()) {
         this.getLevel().entityManager.addTopParticle(this.x + GameRandom.globalRandom.floatGaussian() * 6.0F, this.y + GameRandom.globalRandom.floatGaussian() * 8.0F, this.particleTypes.next()).movesConstant(this.dx / 5.0F, this.dy / 5.0F).color(new Color(41, 52, 55, 150)).height(10.0F).lifeTime(1000);
      }

   }

   public boolean canHitThroughCollision() {
      return true;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return null;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.ghostSlime.body, var3, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this));
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
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.ghostSlime.body.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      float var15 = this.getAttackAnimProgress();
      final DrawOptions var16;
      if (this.isAttacking) {
         var16 = ItemAttackDrawOptions.start(this.dir).armSprite(MobRegistry.Textures.ghostSlime.body, 0, 8, 32).armRotatePoint(8, 15).swingRotation(var15).light(var10).pos(var11, var12 + 10);
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
      TextureDrawOptionsEnd var17 = MobRegistry.Textures.ghostSlime.shadow.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
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
