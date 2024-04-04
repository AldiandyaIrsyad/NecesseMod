package necesse.entity.mobs.friendly.critters;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.BirdCritterAI;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BirdMob extends CritterMob {
   public static LootTable lootTable = new LootTable();
   public static final int[] peckingAnimationTimes = new int[]{1000, 50, 50, 50, 50, 800, 50, 50, 50, 50, 200, 800};
   public static final int[] peckingFrames = new int[]{0, 1, 2, 3, 4, -1, 1, 2, 3, 4, -1, 0};
   protected float height;
   protected long peckStartTime;
   public final EmptyMobAbility peckAbility;

   public BirdMob() {
      this.setSpeed(50.0F);
      this.setFriction(3.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -24, 32, 32);
      this.peckAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            BirdMob.this.peckStartTime = BirdMob.this.getWorldEntity().getLocalTime();
         }
      });
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new BirdCritterAI());
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextFloat(this.height);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.height = var1.getNextFloat();
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (this.isRunning()) {
         this.height += Math.abs(this.dx) / 3.0F * var1 / 250.0F;
         this.height += Math.abs(this.dy) / 3.0F * var1 / 250.0F;
         this.height = Math.min(300.0F, this.height);
      } else {
         this.height = 0.0F;
      }

   }

   public boolean canPushMob(Mob var1) {
      return this.height > 80.0F ? false : super.canPushMob(var1);
   }

   protected GameTexture getTexture() {
      return MobRegistry.Textures.bird;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), this.getTexture(), var3, 4, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 24;
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final TextureDrawOptionsEnd var13;
      int var16;
      if (this.isRunning()) {
         float var14 = Math.min(45.0F, this.dx / 3.0F);
         int var15 = this.moveX < 0.0F ? 0 : 1;
         var16 = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400) + 1;
         var13 = this.getTexture().initDraw().sprite(var16, var15, 32).rotate(var14, 16, 20).light(var10).pos(var11, var12 - (int)this.height);
      } else {
         long var20 = this.getWorldEntity().getLocalTime() - this.peckStartTime;
         var16 = this.dir % 2;
         int var17 = 0;
         int var18 = GameUtils.getAnim(var20, peckingAnimationTimes);
         if (var18 != -1) {
            int var19 = peckingFrames[var18];
            if (var19 != -1) {
               var17 = var19;
               var16 += 2;
            }
         }

         var13 = this.getTexture().initDraw().sprite(var17, var16, 32).light(var10).pos(var11, var12);
      }

      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var13.draw();
         }
      });
      int var21 = this.isAccelerating() ? (this.moveX < 0.0F ? 0 : 1) : this.dir % 2;
      TextureDrawOptionsEnd var22 = MobRegistry.Textures.bird_shadow.initDraw().sprite(0, var21, 32).light(var10).pos(var11, var12);
      if (this.height > 0.0F) {
         var3.add((var1x) -> {
            var22.draw();
         });
      } else {
         var2.add((var1x) -> {
            var22.draw();
         });
      }

   }

   public boolean canTakeDamage() {
      return this.height > 80.0F ? false : super.canTakeDamage();
   }

   public Rectangle getSelectBox(int var1, int var2) {
      Rectangle var3 = super.getSelectBox(var1, var2);
      var3.y = (int)((float)var3.y - this.height);
      return var3;
   }

   public int getFlyingHeight() {
      return this.isAccelerating() ? (int)this.height : super.getFlyingHeight();
   }

   public CollisionFilter getLevelCollisionFilter() {
      return this.isFlying() ? null : super.getLevelCollisionFilter();
   }

   protected Stream<ModifierValue<?>> getRunningModifiers() {
      return Stream.of(new ModifierValue(BuffModifiers.SPEED));
   }
}
