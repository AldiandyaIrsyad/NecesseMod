package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.VampireProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.light.GameLight;

public class VampireMob extends HostileMob {
   public static LootTable lootTable;
   private boolean isBat;

   public VampireMob() {
      super(75);
      this.setSpeed(60.0F);
      this.setFriction(1.0F);
      this.moveAccuracy = 20;
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new PlayerChaserWandererAI<VampireMob>((Supplier)null, 384, 320, 40000, true, false) {
         public boolean attackTarget(VampireMob var1, Mob var2) {
            if (var1.canAttack() && !var1.isBat) {
               GameDamage var3 = new GameDamage(16.0F);
               var1.attack(var2.getX(), var2.getY(), false);
               var1.getLevel().entityManager.projectiles.add(new VampireProjectile(var1.x, var1.y, var2.x, var2.y, var3, var1));
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.attackTarget((VampireMob)var1, var2);
         }
      });
   }

   private void tickIsBat() {
      boolean var1 = this.isAccelerating() || this.hasCurrentMovement();
      if (this.isBat != var1) {
         this.isBat = var1;
         if (this.isClient()) {
            this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), this.x, this.y)), Particle.GType.IMPORTANT_COSMETIC);
         }
      }

   }

   public void clientTick() {
      super.clientTick();
      this.tickIsBat();
   }

   public void serverTick() {
      super.serverTick();
      this.tickIsBat();
   }

   public int getFlyingHeight() {
      return this.isBat ? 20 : super.getFlyingHeight();
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.vampire.body, var3, 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
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
      HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.vampire)).sprite(var13).dir(this.dir).light(var10);
      float var15 = this.getAttackAnimProgress();
      if (this.isAttacking) {
         ItemAttackDrawOptions var16 = ItemAttackDrawOptions.start(this.dir).armSprite(MobRegistry.Textures.vampire.body, 0, 8, 32).swingRotation(var15).light(var10);
         var14.attackAnim(var16, var15);
      }

      final DrawOptions var17 = var14.pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var17.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      Point var4 = new Point(0, var3);
      if (!this.isBat) {
         if (!this.inLiquid(var1, var2)) {
            var4.x = 0;
         } else {
            var4.x = 5;
         }
      } else {
         var4.x = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400) + 1;
      }

      return var4;
   }

   public int getTileWanderPriority(TilePosition var1) {
      return var1.tileID() == TileRegistry.cryptAshID ? 1000 : super.getTileWanderPriority(var1);
   }

   public int getRockSpeed() {
      return 25;
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("vamp", 3);
   }

   static {
      lootTable = new LootTable(new LootItemInterface[]{LootItem.between("batwing", 1, 3), HostileMob.randomMapDrop});
   }
}
