package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampSlimeMob extends JumpingHostileMob {
   public static LootTable lootTable;

   public SwampSlimeMob() {
      super(200);
      this.setSpeed(25.0F);
      this.setFriction(2.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -24, 32, 32);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI((Supplier)null, 160, new GameDamage(40.0F), 100, 40000));
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.swampSlime, var3, 2, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public boolean isValidSpawnLocation(Server var1, ServerClient var2, int var3, int var4) {
      MobSpawnLocation var5 = (new MobSpawnLocation(this, var3, var4)).checkMobSpawnLocation();
      if (this.getLevel().isCave) {
         var5 = var5.checkLightThreshold(var2);
      } else {
         var5 = var5.checkMaxStaticLightThreshold(10);
      }

      return var5.validAndApply();
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 26;
      boolean var13 = this.inLiquid(var5, var6);
      int var14;
      if (var13) {
         var14 = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 1000);
      } else {
         var14 = this.getJumpAnimationFrame(6);
      }

      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var15 = MobRegistry.Textures.swampSlime.initDraw().sprite(var14, var13 ? 1 : 0, 32).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
         }
      });
      if (!var13) {
         TextureDrawOptionsEnd var16 = MobRegistry.Textures.swampSlime_shadow.initDraw().sprite(var14, 0, 32).light(var10).pos(var11, var12);
         var2.add((var1x) -> {
            var16.draw();
         });
      }

   }

   public boolean isSlimeImmune() {
      return true;
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return this.getLevel() != null && this.getLevel().isCave ? Stream.of(new ModifierValue(BuffModifiers.SPEED, 1.0F), new ModifierValue(BuffModifiers.CHASER_RANGE, 2.0F)) : super.getDefaultModifiers();
   }

   static {
      lootTable = new LootTable(new LootItemInterface[]{HostileMob.randomMapDrop});
   }
}
