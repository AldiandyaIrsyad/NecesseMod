package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChargingCirclingChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SandwormHead extends HostileWormMobHead<SandwormBody, SandwormHead> {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new MobConditionLootItemList((var0) -> {
      return var0.getLevel() == null || !var0.getLevel().isIncursionLevel;
   }, new LootItemInterface[]{LootItem.between("wormcarapace", 1, 2)})});
   public static GameDamage headCollisionDamage = new GameDamage(70.0F);
   public static GameDamage bodyCollisionDamage = new GameDamage(55.0F);
   public static GameDamage incursionHeadCollisionDamage = new GameDamage(80.0F);
   public static GameDamage incursionBodyCollisionDamage = new GameDamage(65.0F);
   public static float lengthPerBodyPart = 20.0F;
   public static float waveLength = 350.0F;
   public static final int totalBodyParts = 20;
   public GameDamage collisionDamage;

   public SandwormHead() {
      super(1200, waveLength, 70.0F, 20, 20.0F, -24.0F);
      this.moveAccuracy = 120;
      this.setSpeed(100.0F);
      this.setArmor(15);
      this.accelerationMod = 1.0F;
      this.decelerationMod = 1.0F;
      this.collision = new Rectangle(-16, -14, 32, 28);
      this.hitBox = new Rectangle(-20, -16, 40, 32);
      this.selectBox = new Rectangle(-20, -35, 40, 40);
   }

   protected float getDistToBodyPart(SandwormBody var1, int var2, float var3) {
      return lengthPerBodyPart;
   }

   protected SandwormBody createNewBodyPart(int var1) {
      Object var2;
      if (var1 == 19) {
         var2 = new SandwormTail();
      } else {
         var2 = new SandwormBody();
      }

      ((SandwormBody)var2).sharesHitCooldownWithNext = var1 % 3 < 2;
      ((SandwormBody)var2).sprite = new Point(0, 1 + var1 % 4);
      return (SandwormBody)var2;
   }

   protected void playMoveSound() {
      Screen.playSound(GameResources.shake, SoundEffect.effect(this).falloffDistance(2000).volume(0.6F));
   }

   public void init() {
      super.init();
      if (this.getLevel() instanceof IncursionLevel) {
         this.setMaxHealth(1400);
         this.setHealthHidden(this.getMaxHealth());
         this.setArmor(20);
         this.collisionDamage = headCollisionDamage;
      } else {
         this.collisionDamage = incursionHeadCollisionDamage;
      }

      this.ai = new BehaviourTreeAI(this, new PlayerChargingCirclingChaserAI((Supplier)null, 2560, 500, 20), new FlyingAIMover());
   }

   public float getTurnSpeed(float var1) {
      return super.getTurnSpeed(var1) * 1.2F;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.collisionDamage;
   }

   public void spawnDeathParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.sandWorm, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(var5) - 32;
         int var12 = var8.getDrawY(var6);
         float var13 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
         addAngledDrawable(var1, new GameSprite(MobRegistry.Textures.sandWorm, 0, 0, 64), MobRegistry.Textures.sandWorm_mask, var10, (int)this.height, var13, var11, var12, 64);
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.sandWorm_shadow;
      int var6 = var4.getDrawX(var1) - var5.getWidth() / 2;
      int var7 = var4.getDrawY(var2) - var5.getHeight() / 2;
      var7 += this.getBobbing(var1, var2);
      return var5.initDraw().light(var3).pos(var6, var7);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F)).max(0.0F));
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected WormMobBody createNewBodyPart(int var1) {
      return this.createNewBodyPart(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected float getDistToBodyPart(WormMobBody var1, int var2, float var3) {
      return this.getDistToBodyPart((SandwormBody)var1, var2, var3);
   }
}
