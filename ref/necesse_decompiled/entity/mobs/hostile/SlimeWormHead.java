package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChargingCirclingChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SlimeWormHead extends HostileWormMobHead<SlimeWormBody, SlimeWormHead> {
   public static LootTable lootTable = new LootTable();
   public static float lengthPerBodyPart = 20.0F;
   public static float waveLength = 350.0F;
   public static final int totalBodyParts = 30;
   public static GameDamage headCollisionDamage = new GameDamage(70.0F);
   public static GameDamage bodyCollisionDamage = new GameDamage(60.0F);

   public SlimeWormHead() {
      super(1200, waveLength, 70.0F, 30, 0.0F, -24.0F);
      this.moveAccuracy = 120;
      this.setSpeed(50.0F);
      this.setArmor(25);
      this.accelerationMod = 1.0F;
      this.decelerationMod = 1.0F;
      this.collision = new Rectangle(-16, -14, 32, 28);
      this.hitBox = new Rectangle(-20, -16, 40, 32);
      this.selectBox = new Rectangle(-20, -35, 40, 40);
   }

   protected float getDistToBodyPart(SlimeWormBody var1, int var2, float var3) {
      float var4 = lengthPerBodyPart;
      if (var2 >= 24) {
         int var5 = var2 - 24 + 1;
         var4 = Math.max(var4 - (float)(var5 * 2), 5.0F);
      }

      float var7 = 0.0F;
      WormMoveLine var6 = (WormMoveLine)this.moveLines.getFirst();
      if (var6 != null) {
         var7 = var6.movedDist;
      }

      var7 /= 0.77F;
      return var4 - (float)((Math.sin((double)(((float)var2 - var7 / (lengthPerBodyPart * 0.8F)) / 2.0F)) + 1.0) / 2.0) * var4 * 0.6F;
   }

   protected void onUpdatedBodyPartPos(SlimeWormBody var1, int var2, float var3) {
      super.onUpdatedBodyPartPos(var1, var2, var3);
      int var4 = 0;
      if (var2 >= 24) {
         var4 = var2 - 24 + 1;
      }

      float var5 = 0.0F;
      WormMoveLine var6 = (WormMoveLine)this.moveLines.getFirst();
      if (var6 != null) {
         var5 = var6.movedDist;
      }

      var5 /= 0.77F;
      float var7 = 1.0F - (float)((Math.sin((double)(((float)var2 - var5 / (lengthPerBodyPart * 0.8F)) / 2.0F)) + 1.0) / 2.0);
      int var8 = GameMath.limit(GameMath.lerp(var7, 0, 4), var4, Math.max(var4, 4));
      var1.sprite = new Point(0, var8);
   }

   protected SlimeWormBody createNewBodyPart(int var1) {
      SlimeWormBody var2 = new SlimeWormBody();
      var2.sharesHitCooldownWithNext = var1 % 3 < 2;
      var2.bodyIndex = var1;
      if (var1 >= 24) {
         int var3 = var1 - 24 + 1;
         var2.sprite = new Point(0, var3);
      } else {
         var2.sprite = new Point(0, 0);
      }

      return var2;
   }

   protected void playMoveSound() {
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChargingCirclingChaserAI((Supplier)null, 2560, 500, 20), new FlyingAIMover());
   }

   public float getTurnSpeed(float var1) {
      return super.getTurnSpeed(var1) * 1.2F;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return null;
   }

   public void spawnDeathParticles(float var1, float var2) {
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.slimeWorm.shadow;
      int var6 = var4.getDrawX(var1) - 32;
      int var7 = var4.getDrawY(var2) - 32;
      var7 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(0, 1, 64).light(var3).pos(var6, var7);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F)).max(0.0F));
   }

   public boolean isSlimeImmune() {
      return true;
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onUpdatedBodyPartPos(WormMobBody var1, int var2, float var3) {
      this.onUpdatedBodyPartPos((SlimeWormBody)var1, var2, var3);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected WormMobBody createNewBodyPart(int var1) {
      return this.createNewBodyPart(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected float getDistToBodyPart(WormMobBody var1, int var2, float var3) {
      return this.getDistToBodyPart((SlimeWormBody)var1, var2, var3);
   }
}
