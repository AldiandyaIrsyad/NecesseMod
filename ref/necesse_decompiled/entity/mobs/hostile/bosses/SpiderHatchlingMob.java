package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderHatchlingMob extends BossMob {
   public static LootTable lootTable = new LootTable();
   public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(15, 20, 25, 30, 35);
   public long deathTime;

   public SpiderHatchlingMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.isSummoned = true;
      this.setSpeed(40.0F);
      this.setFriction(2.0F);
      this.attackCooldown = 500;
      this.collision = new Rectangle(-11, -8, 22, 16);
      this.hitBox = new Rectangle(-14, -16, 28, 28);
      this.selectBox = new Rectangle(-14, -16, 28, 28);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI((Supplier)null, 1600, QueenSpiderMob.hatchlingDamage, 100, 10000));
      this.deathTime = this.getWorldEntity().getTime() + 20000L;
   }

   public void serverTick() {
      super.serverTick();
      if (this.deathTime <= this.getWorldEntity().getTime()) {
         this.setHealth(0);
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.spiderHatchling.body, 12, var3, 16, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 15;
      int var12 = var8.getDrawY(var6) - 22;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      if (this.inLiquid(var5, var6)) {
         var12 -= 6;
      }

      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.spiderHatchling.body.initDraw().sprite(var13.x, var13.y, 32).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.spiderHatchling.shadow.initDraw().sprite(var13.x, var13.y, 32).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var15.draw();
      });
   }

   public int getRockSpeed() {
      return 7;
   }
}
