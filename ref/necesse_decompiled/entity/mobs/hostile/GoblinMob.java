package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionPlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ConditionLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GoblinMob extends HostileMob {
   public static LootTable lootTable;

   public GoblinMob() {
      super(50);
      this.setSpeed(30.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -32, 32, 38);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionPlayerChaserWandererAI(() -> {
         return !this.getLevel().isCave && !this.getLevel().getServer().world.worldEntity.isNight();
      }, 384, new GameDamage(16.0F), 100, 40000));
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("generic", 5);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.goblin, var3, 4, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 30;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var14 = MobRegistry.Textures.goblin.initDraw().sprite(var13.x, var13.y, 32).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.human_baby_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(this.dir, 0, var6).light(var3).pos(var7, var8);
   }

   public int getRockSpeed() {
      return 10;
   }

   static {
      lootTable = new LootTable(new LootItemInterface[]{new ConditionLootItemList((var0, var1) -> {
         Mob var2 = (Mob)LootTable.expectExtra(Mob.class, var1, 0);
         return var2 == null || var2.getLevel().isCave;
      }, new LootItemInterface[]{ZombieMob.belowSurfaceDrop}), randomMapDrop, LootItem.between("coin", 5, 15)});
   }
}
