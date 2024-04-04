package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.particle.Particle;
import necesse.entity.particle.TopFleshParticle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SandSpiritMob extends FlyingHostileMob {
   public static LootTable lootTable = new LootTable();

   public SandSpiritMob() {
      super(100);
      this.setSpeed(30.0F);
      this.setFriction(0.5F);
      this.setKnockbackModifier(0.2F);
      this.setArmor(10);
      this.moveAccuracy = 10;
      this.collision = new Rectangle(-12, -12, 24, 24);
      this.hitBox = new Rectangle(-16, -16, 32, 32);
      this.selectBox = new Rectangle(-18, -40, 36, 54);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI((Supplier)null, 384, new GameDamage(35.0F), 100, 40000), new FlyingAIMover());
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         int var4 = GameRandom.globalRandom.nextInt(4);
         this.getLevel().entityManager.addParticle((Particle)(new TopFleshParticle(this.getLevel(), MobRegistry.Textures.sandSpirit, var4, 2, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath2, SoundEffect.effect(this));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = (int)(GameUtils.getBobbing(var4.getWorldEntity().getTime(), 1600) * 5.0F);
      int var12 = var8.getDrawX(var5) - 32;
      int var13 = var8.getDrawY(var6) - 48 + var11;
      TextureDrawOptionsEnd var14 = MobRegistry.Textures.sandSpirit.initDraw().sprite(0, 0, 64).mirror(this.moveX < 0.0F, false).alpha(0.7F).light(var10).pos(var12, var13);
      byte var15 = 100;
      TextureDrawOptionsEnd var16 = MobRegistry.Textures.sandSpirit.initDraw().sprite(1, 0, 64).mirror(this.moveX < 0.0F, false).alpha(0.7F).light(var10.minLevelCopy((float)var15)).pos(var12, var13);
      this.addShadowDrawables(var3, var5, var6, var10, var8);
      var3.add((var2x) -> {
         var14.draw();
         var16.draw();
      });
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.human_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2 + 4;
      return var5.initDraw().sprite(0, 0, var6).light(var3).pos(var7, var8);
   }
}
