package necesse.entity.mobs.friendly.critters;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.IdleAnimationAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CritterAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrogMob extends CritterJumpingMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("frogleg")});
   protected long idleAnimTime;
   public final EmptyMobAbility startIdleAnimationAbility;

   public LootTable getLootTable() {
      return lootTable;
   }

   public FrogMob() {
      this.setSpeed(20.0F);
      this.setFriction(2.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -28, 32, 34);
      this.startIdleAnimationAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            FrogMob.this.idleAnimTime = FrogMob.this.getWorldEntity().getLocalTime();
         }
      });
   }

   public void init() {
      super.init();
      CritterAI var1 = new CritterAI();
      var1.addChildFirst(new IdleAnimationAINode<FrogMob>() {
         public int getIdleAnimationCooldown(GameRandom var1) {
            return var1.getIntBetween(40, 100);
         }

         public void runIdleAnimation(FrogMob var1) {
            var1.startIdleAnimationAbility.runAndSend();
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void runIdleAnimation(Mob var1) {
            this.runIdleAnimation((FrogMob)var1);
         }
      });
      this.ai = new BehaviourTreeAI(this, var1);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.frog.body, 12, var3, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 31;
      int var12 = var8.getDrawY(var6) - 54;
      boolean var13 = this.inLiquid(var5, var6);
      int var14;
      if (var13) {
         var14 = 5;
      } else {
         var14 = this.getJumpAnimationFrame(5);
      }

      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      int var15 = this.dir;
      int var16 = var14;
      if (!this.isAccelerating() && var14 == 0) {
         long var17 = this.getWorldEntity().getLocalTime() - this.idleAnimTime;
         if (var17 <= 500L) {
            var15 += 4;
            var14 = (int)(var17 / 100L);
         }
      }

      final TextureDrawOptionsEnd var19 = MobRegistry.Textures.frog.body.initDraw().sprite(var14, var15, 64).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var19.draw();
         }
      });
      TextureDrawOptionsEnd var18 = MobRegistry.Textures.frog.shadow.initDraw().sprite(var16, this.dir, 64).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var18.draw();
      });
   }

   public MobSpawnLocation checkSpawnLocation(MobSpawnLocation var1) {
      return super.checkSpawnLocation(var1).checkTile((var1x, var2) -> {
         int var3 = this.getLevel().getTileID(var1x, var2);
         return var3 == TileRegistry.swampGrassID || var3 == TileRegistry.mudID || var3 == TileRegistry.swampRockID || var3 == TileRegistry.deepSwampRockID;
      });
   }
}
