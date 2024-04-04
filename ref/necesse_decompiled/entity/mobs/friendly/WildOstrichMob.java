package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.trees.FollowerWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
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

public class WildOstrichMob extends FriendlyRopableMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("inefficientfeather")});
   private boolean hide;
   public final CoordinateMobAbility hideAbility;

   public WildOstrichMob() {
      super(200);
      this.setSpeed(65.0F);
      this.setFriction(3.0F);
      this.setSwimSpeed(0.4F);
      this.moveAccuracy = 10;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-16, -12, 32, 24);
      this.selectBox = new Rectangle(-18, -51, 36, 58);
      this.hideAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            WildOstrichMob.this.stopMoving();
            WildOstrichMob.this.setPos((float)var1, (float)var2, false);
            WildOstrichMob.this.hide = true;
            if (var1 < 0) {
               WildOstrichMob.this.dir = 1;
            } else {
               WildOstrichMob.this.dir = 3;
            }

            WildOstrichMob.this.dx = 0.0F;
            WildOstrichMob.this.dy = 0.0F;
         }
      });
   }

   public void init() {
      super.init();
      FollowerWandererAI var1 = new FollowerWandererAI<WildOstrichMob>(320, 64, 30000) {
         protected Mob getFollowingMob(WildOstrichMob var1) {
            return var1.getRopeMob();
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected Mob getFollowingMob(Mob var1) {
            return this.getFollowingMob((WildOstrichMob)var1);
         }
      };
      var1.addChild(new AINode<WildOstrichMob>() {
         protected void onRootSet(AINode<WildOstrichMob> var1, WildOstrichMob var2, Blackboard<WildOstrichMob> var3) {
            var3.onEvent("ranAway", (var1x) -> {
               var2.hideAbility.runAndSend(var2.getX(), var2.getY());
            });
         }

         public void init(WildOstrichMob var1, Blackboard<WildOstrichMob> var2) {
         }

         public AINodeResult tick(WildOstrichMob var1, Blackboard<WildOstrichMob> var2) {
            return AINodeResult.SUCCESS;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((WildOstrichMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((WildOstrichMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (WildOstrichMob)var2, var3);
         }
      });
      this.ai = new BehaviourTreeAI(this, var1, new AIMover());
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (this.dx != 0.0F || this.dy != 0.0F) {
         this.hide = false;
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.ostrich, GameRandom.globalRandom.nextInt(5), 12, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 11;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      if (this.hide && !this.inLiquid(var5, var6)) {
         if (this.dir == 1) {
            var13 = new Point(5, 4);
         } else {
            var13 = new Point(4, 4);
         }
      }

      final TextureDrawOptionsEnd var14;
      if (!this.isMounted()) {
         var14 = MobRegistry.Textures.ostrich.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      } else {
         var14 = MobRegistry.Textures.ostrichMount.initDraw().sprite(var13.x, var13.y, 64).light(var10).pos(var11, var12);
      }

      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public int getRockSpeed() {
      return 10;
   }
}
