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
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.PhantomBoltProjectile;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.GraveyardIncursionBiome;
import necesse.level.maps.light.GameLight;

public class PhantomMob extends FlyingHostileMob {
   public static LootTable lootTable;
   public static GameDamage damage;
   public Trail trail;
   public TicksPerSecond particleTicks = TicksPerSecond.ticksPerSecond(20);
   public ParticleTypeSwitcher particleTypes;
   public final EmptyMobAbility playBoltSoundAbility;

   public PhantomMob() {
      super(350);
      this.particleTypes = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      this.setSpeed(70.0F);
      this.setFriction(0.5F);
      this.setArmor(30);
      this.setKnockbackModifier(0.0F);
      this.moveAccuracy = 20;
      this.attackCooldown = 4000;
      this.spawnLightThreshold = (new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0)).min(150, Integer.MAX_VALUE);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-16, -40, 32, 32);
      this.playBoltSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (PhantomMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt2, SoundEffect.globalEffect().volume(0.5F).pitch(1.1F));
            }

         }
      });
   }

   public void init() {
      super.init();
      PlayerChaserWandererAI var1 = new PlayerChaserWandererAI<PhantomMob>((Supplier)null, 512, 512, 40000, true, false) {
         public boolean canHitTarget(PhantomMob var1, float var2, float var3, Mob var4) {
            return true;
         }

         public boolean attackTarget(PhantomMob var1, Mob var2) {
            if (var1.canAttack()) {
               var1.attack(var2.getX(), var2.getY(), false);
               PhantomBoltProjectile var3 = new PhantomBoltProjectile(var1.getLevel(), var1, var1.x, var1.y, var2.x, var2.y, 60.0F, 768, PhantomMob.damage, 50);
               var3.moveDist(15.0);
               var1.getLevel().entityManager.projectiles.add(var3);
               PhantomMob.this.playBoltSoundAbility.runAndSend();
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.attackTarget((PhantomMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean canHitTarget(Mob var1, float var2, float var3, Mob var4) {
            return this.canHitTarget((PhantomMob)var1, var2, var3, var4);
         }
      };
      var1.playerChaserAI.chaserAINode.changePositionConstantly = true;
      this.ai = new BehaviourTreeAI(this, var1, new FlyingAIMover());
      if (this.isClient()) {
         this.trail = new Trail(this, this.getLevel(), new Color(15, 24, 35), 24.0F, 1000, 20.0F);
         this.trail.drawOnTop = true;
         this.trail.removeOnFadeOut = false;
         this.getLevel().entityManager.addTrail(this.trail);
      }

   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (this.trail != null) {
         this.trail.addPoint(new TrailVector(this.x, this.y, this.dx, this.dy, this.trail.thickness, 20.0F));
      }

   }

   public void clientTick() {
      super.clientTick();
      this.particleTicks.gameTick();

      while(this.particleTicks.shouldTick()) {
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 5.0F, this.y + GameRandom.globalRandom.floatGaussian() * 5.0F, this.particleTypes.next()).movesConstant(this.dx / 5.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, this.dy / 5.0F + GameRandom.globalRandom.floatGaussian() * 4.0F).color(new Color(25, 41, 58)).height(20.0F).lifeTime(1000);
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 15; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, this.particleTypes.next()).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 10.0F, (float)GameRandom.globalRandom.nextGaussian() * 10.0F).height(20.0F).color(new Color(25, 41, 58)).lifeTime(500);
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this));
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32).minLevelCopy(100.0F);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 40;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      float var14 = GameUtils.getBobbing(this.getWorldEntity().getTime(), 1000) * 5.0F;
      var12 = (int)((float)var12 + var14);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var15 = MobRegistry.Textures.phantom.body.initDraw().sprite(var13.x, var13.y, 32).light(var10).pos(var11, var12);
      var3.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
         }
      });
      TextureDrawOptionsEnd var16 = MobRegistry.Textures.phantom.shadow.initDraw().sprite(var13.x, var13.y, 32).light(var10).pos(var8.getDrawX(var5) - 16, var8.getDrawY(var6) - 16);
      var3.add((var1x) -> {
         var16.draw();
      });
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      return new Point(GameUtils.getAnim(this.getWorldEntity().getTime(), 6, 400), 0);
   }

   public void dispose() {
      super.dispose();
      if (this.trail != null) {
         this.trail.removeOnFadeOut = true;
      }

   }

   static {
      lootTable = new LootTable(new LootItemInterface[]{GraveyardIncursionBiome.graveyardMobDrops});
      damage = new GameDamage(65.0F);
   }
}
