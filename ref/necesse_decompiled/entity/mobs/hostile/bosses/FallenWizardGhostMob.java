package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenWizardGhostMob extends BossMob {
   public FallenWizardGhostMob() {
      super(100);
      this.setSpeed(60.0F);
      this.setFriction(3.0F);
      this.setArmor(40);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-22, -18, 44, 36);
      this.selectBox = new Rectangle(-19, -52, 38, 64);
   }

   public void moveToPos(int var1, int var2) {
      this.ai = new BehaviourTreeAI(this, new GhostAINode(var1, var2));
   }

   public boolean canBeHit(Attacker var1) {
      return false;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 48;
      int var12 = var8.getDrawY(var6) - 75;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      HumanDrawOptions var14 = (new HumanDrawOptions(var4, MobRegistry.Textures.fallenWizard)).sprite(var13, 96).size(96, 96).alpha(0.7F).dir(this.dir).light(var10);
      final DrawOptions var15 = var14.pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var15.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.human_big_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(this.dir, 0, var6).light(var3).pos(var7, var8);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-12, -28, 24, 34);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 24;
      int var5 = var3 - 34;
      Point var6 = this.getAnimSprite(this.getDrawX(), this.getDrawY(), this.dir);
      (new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.fallenWizard)).sprite(var6, 96).alpha(0.5F).dir(this.dir).size(48, 48).draw(var4, var5);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), new ModifierValue(BuffModifiers.ATTACK_MOVEMENT_MOD, 0.0F));
   }

   public static class GhostAINode<T extends Mob> extends MoveTaskAINode<T> {
      public final int tileX;
      public final int tileY;
      public boolean hasStartedMoving;

      public GhostAINode(int var1, int var2) {
         this.tileX = var1;
         this.tileY = var2;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         this.moveToTileTask(this.tileX, this.tileY, (BiPredicate)null, (var1x) -> {
            var1x.moveIfWithin(-1, -1, (Runnable)null);
            this.hasStartedMoving = true;
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
