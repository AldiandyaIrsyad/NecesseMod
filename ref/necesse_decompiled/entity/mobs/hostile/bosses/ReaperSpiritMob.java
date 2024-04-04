package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerCirclingChaserAI;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperSpiritMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable();
   public static MaxHealthGetter BASE_MAX_HEALTH = new MaxHealthGetter(25, 40, 50, 60, 85);
   public static MaxHealthGetter INCURSION_MAX_HEALTH = new MaxHealthGetter(50, 75, 90, 105, 130);
   public ReaperMob owner;
   public Trail trail;
   public float moveAngle;
   private float toMove;
   public GameDamage collisionDamage;
   public static GameDamage baseCollisionDamage = new GameDamage(60.0F);
   public static GameDamage incursionCollisionDamage = new GameDamage(90.0F);

   public ReaperSpiritMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
      this.isSummoned = true;
      this.moveAccuracy = 30;
      this.setSpeed(110.0F);
      this.setArmor(20);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-18, -15, 36, 36);
      this.selectBox = new Rectangle(-20, -18, 40, 36);
   }

   public void init() {
      if (this.getLevel() instanceof IncursionLevel) {
         this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
         this.setHealth(this.getMaxHealth());
         this.collisionDamage = incursionCollisionDamage;
      } else {
         this.collisionDamage = baseCollisionDamage;
      }

      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerCirclingChaserAI(1600, 350, 30));
      if (this.isClient()) {
         this.trail = new Trail(this, this.getLevel(), new Color(14, 131, 121, 150), 16.0F, 500, 0.0F);
         this.trail.drawOnTop = true;
         this.trail.removeOnFadeOut = false;
         this.getLevel().entityManager.addTrail(this.trail);
      }

   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 50;
   }

   public void tickMovement(float var1) {
      this.toMove += var1;

      while(this.toMove > 4.0F) {
         float var2 = this.x;
         float var3 = this.y;
         super.tickMovement(4.0F);
         this.toMove -= 4.0F;
         Point2D.Float var4 = GameMath.normalize(var2 - this.x, var3 - this.y);
         this.moveAngle = (float)Math.toDegrees(Math.atan2((double)var4.y, (double)var4.x)) - 90.0F;
         if (this.trail != null) {
            float var5 = 5.0F;
            this.trail.addPoint(new TrailVector(this.x + var4.x * var5, this.y + var4.y * var5, -var4.x, -var4.y, this.trail.thickness, 0.0F));
         }
      }

   }

   public void clientTick() {
      super.clientTick();
      this.getLevel().entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 4.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.dx / 10.0F, this.dy / 10.0F).color(new Color(14, 131, 121, 150));
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 30; ++var3) {
         this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).movesConstantAngle((float)GameRandom.globalRandom.nextInt(360), (float)GameRandom.globalRandom.getIntBetween(5, 20)).color(new Color(14, 131, 121, 150));
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath1, SoundEffect.effect(this).volume(0.5F));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 16;
      TextureDrawOptionsEnd var13 = MobRegistry.Textures.reaperSpirit.initDraw().sprite(0, 0, 32).light(var10).rotate(this.moveAngle, 16, 16).pos(var11, var12);
      byte var14 = 100;
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.reaperSpirit.initDraw().sprite(1, 0, 32).light(var10.minLevelCopy((float)var14)).rotate(this.moveAngle, 16, 16).pos(var11, var12);
      var3.add((var2x) -> {
         var13.draw();
         var15.draw();
      });
   }

   public void dispose() {
      super.dispose();
      if (this.trail != null) {
         this.trail.removeOnFadeOut = true;
      }

   }
}
