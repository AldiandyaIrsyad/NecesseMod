package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrapperZombieMob extends HostileMob {
   public static LootTable extraLootTable = new LootTable(new LootItemInterface[]{new ChanceLootItem(0.05F, "trapperhat")});

   public TrapperZombieMob() {
      super(120);
      this.setSpeed(30.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI(() -> {
         return !this.getLevel().isCave && !this.getLevel().getServer().world.worldEntity.isNight();
      }, 384, new GameDamage(22.0F), 100, 40000));
   }

   public LootTable getLootTable() {
      return new LootTable(new LootItemInterface[]{ZombieMob.lootTable, extraLootTable});
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("zombie", 3);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.trapperZombie.body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
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
      final DrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.trapperZombie)).sprite(var13).dir(this.dir).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public int getRockSpeed() {
      return 20;
   }
}
