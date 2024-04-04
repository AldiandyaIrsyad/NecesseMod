package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.BiPredicate;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.VoidWizardCloneProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidWizardClone extends BossMob {
   protected Mob original;
   protected long despawnTime;

   public VoidWizardClone() {
      super(100);
      this.isSummoned = true;
      this.setSpeed(50.0F);
      this.setFriction(3.0F);
      this.setArmor(10);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-18, -15, 36, 30);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.shouldSave = false;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      if (this.original == null) {
         var1.putNextInt(-1);
      } else {
         var1.putNextInt(this.original.getUniqueID());
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      int var2 = var1.getNextInt();
      if (var2 == -1) {
         this.original = null;
      } else {
         this.original = GameUtils.getLevelMob(var2, this.getLevel());
      }

   }

   public void setOriginal(VoidWizard var1) {
      this.original = var1;
      this.setMaxHealth(var1.getMaxHealthFlat());
      this.setHealth(var1.getHealth());
   }

   public void init() {
      super.init();
      this.despawnTime = this.getWorldEntity().getTime() + 2000L;
   }

   public void moveToPos(int var1, int var2) {
      this.ai = new BehaviourTreeAI(this, new CloneAINode(var1, var2));
      this.despawnTime = -1L;
   }

   public void serverTick() {
      super.serverTick();
      if (this.despawnTime > 0L && this.despawnTime <= this.getWorldEntity().getTime()) {
         this.remove();
      }

   }

   protected void doWasHitLogic(MobWasHitEvent var1) {
      super.doWasHitLogic(var1);
      if (this.isServer() && !var1.wasPrevented && this.original != null) {
         if (this.original instanceof VoidWizard && ((VoidWizard)this.original).canAddProjectile()) {
            VoidWizardCloneProjectile var2 = new VoidWizardCloneProjectile(this.getLevel(), this.x, this.y, this.original, VoidWizard.cloneProjectile);
            this.getLevel().entityManager.projectiles.add(var2);
            ((VoidWizard)this.original).addProjectile(var2);
         }

         this.remove();
      }

   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public void spawnDeathParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), (float)this.getX(), (float)this.getY(), 80, VoidWizard.getWizardColor(this.original))), Particle.GType.CRITICAL);
   }

   public void spawnRemoveParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), (float)this.getX(), (float)this.getY(), 80, VoidWizard.getWizardColor(this.original))), Particle.GType.CRITICAL);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      final DrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.voidWizard)).sprite(var13).dir(this.dir).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public boolean isHealthBarVisible() {
      return this.original != null ? this.original.isHealthBarVisible() : super.isHealthBarVisible();
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-8, -22, 16, 25);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 16;
      int var5 = var3 - 26;
      Point var6 = this.getAnimSprite(this.getDrawX(), this.getDrawY(), this.dir);
      (new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.voidWizard)).sprite(var6).dir(this.dir).size(32, 32).draw(var4, var5);
   }

   public static class CloneAINode<T extends Mob> extends MoveTaskAINode<T> {
      public final int tileX;
      public final int tileY;
      public boolean hasStartedMoving;

      public CloneAINode(int var1, int var2) {
         this.tileX = var1;
         this.tileY = var2;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         this.moveToTileTask(this.tileX, this.tileY, (BiPredicate)null, (var1x) -> {
            this.hasStartedMoving = true;
            var1x.moveIfWithin(-1, -1, (Runnable)null);
            return AINodeResult.SUCCESS;
         });
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tickNode(T var1, Blackboard<T> var2) {
         if (this.hasStartedMoving && !var2.mover.isMoving()) {
            var1.remove();
         }

         return AINodeResult.SUCCESS;
      }
   }
}
