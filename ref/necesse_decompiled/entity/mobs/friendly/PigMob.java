package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.FollowerWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PigMob extends HusbandryMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("rawpork", 1, 2)});

   public PigMob() {
      super(50);
      this.setSpeed(12.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-12, -9, 24, 18);
      this.hitBox = new Rectangle(-16, -12, 32, 24);
      this.selectBox = new Rectangle(-18, -26, 36, 36);
   }

   public GameMessage getLocalization() {
      return (GameMessage)(this.isGrown() ? super.getLocalization() : new LocalMessage("mob", "piglet"));
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new FollowerWandererAI<FriendlyRopableMob>(320, 64, 30000) {
         protected Mob getFollowingMob(FriendlyRopableMob var1) {
            return var1.getRopeMob();
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected Mob getFollowingMob(Mob var1) {
            return this.getFollowingMob((FriendlyRopableMob)var1);
         }
      }, new AIMover());
   }

   public LootTable getLootTable() {
      return !this.isGrown() ? new LootTable() : lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      GameTexture var3 = this.getTexture();

      for(int var4 = 0; var4 < 4; ++var4) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), var3, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      TextureDrawOptionsEnd var14 = this.getShadowTexture().initDraw().sprite(0, this.dir, 64).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var14.draw();
      });
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var15 = this.getTexture().initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
         }
      });
   }

   public int getRockSpeed() {
      return this.isGrown() ? 10 : 7;
   }

   private GameTexture getTexture() {
      return this.isGrown() ? MobRegistry.Textures.pig : MobRegistry.Textures.piglet;
   }

   private GameTexture getShadowTexture() {
      return this.isGrown() ? MobRegistry.Textures.pig_shadow : MobRegistry.Textures.piglet_shadow;
   }
}
