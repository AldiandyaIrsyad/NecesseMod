package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenDragonHead extends BossWormMobHead<FallenDragonBody, FallenDragonHead> {
   public static LootTable lootTable = new LootTable();
   public static float lengthPerBodyPart = 40.0F;
   public static float waveLength = 800.0F;
   public static final int totalBodyParts = 8;
   public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(150, 300, 400, 500, 650);
   public Point2D.Float centerPosition;
   public FallenWizardMob master;
   public float circlingAngleOffset;

   public FallenDragonHead() {
      super(100, waveLength, 100.0F, 8, 10.0F, -40.0F);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.moveAccuracy = 100;
      this.movementUpdateCooldown = 2000;
      this.movePosTolerance = 700.0F;
      this.setSpeed(180.0F);
      this.setArmor(35);
      this.accelerationMod = 1.0F;
      this.decelerationMod = 1.0F;
      this.collision = new Rectangle(-18, -15, 36, 30);
      this.hitBox = new Rectangle(-25, -20, 50, 40);
      this.selectBox = new Rectangle(-32, -80, 64, 84);
   }

   protected float getDistToBodyPart(FallenDragonBody var1, int var2, float var3) {
      return var2 == 1 ? lengthPerBodyPart : lengthPerBodyPart;
   }

   protected FallenDragonBody createNewBodyPart(int var1) {
      FallenDragonBody var2 = new FallenDragonBody();
      byte var3 = 3;
      if (var1 == 1) {
         var2.spriteY = 1;
      } else if (var1 == 8 - var3 - 1) {
         var2.spriteY = 1;
      } else if (var1 >= 8 - var3) {
         int var4 = Math.abs(8 - var1 - var3);
         var2.spriteY = 3 + var4;
      } else {
         var2.spriteY = 2;
      }

      var2.spawnsParticles = true;
      return var2;
   }

   protected void playMoveSound() {
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return FallenWizardMob.dragonHeadDamage;
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new FallenDragonAI(10000, 800, 2560, 500, 20), new FlyingAIMover());
   }

   public void serverTick() {
      super.serverTick();
      if (this.master != null && (this.master.removed() || !this.master.isHostile)) {
         this.remove();
      }

   }

   public int getFlyingHeight() {
      return 20;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.fallenWizardDragon, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(this.x) - 32;
         int var12 = var8.getDrawY(this.y);
         float var13 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
         final MobDrawable var14 = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.fallenWizardDragon, 0, 0, 64), (GameTexture)null, var10, (int)this.height, var13, var11, var12, 96);
         new ComputedObjectValue((Object)null, () -> {
            return 0.0;
         });
         ComputedObjectValue var16 = WormMobHead.moveDistance(this.moveLines.getFirstElement(), 35.0);
         final MobDrawable var15;
         if (var16.object != null) {
            Point2D.Double var17 = WormMobHead.linePos(var16);
            GameLight var18 = var4.getLightLevel((int)(var17.x / 32.0), (int)(var17.y / 32.0));
            int var19 = var8.getDrawX((float)var17.x) - 32;
            int var20 = var8.getDrawY((float)var17.y);
            float var21 = this.getWaveHeight(((WormMoveLine)((GameLinkedList.Element)var16.object).object).movedDist + ((Double)var16.get()).floatValue());
            float var22 = GameMath.fixAngle((float)GameMath.getAngle(new Point2D.Double((double)this.x - var17.x, (double)(this.y - this.height) - (var17.y - (double)var21))));
            var15 = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.fallenWizardDragon, 0, 2, 64), (GameTexture)null, var18, (int)var21, var22, var19, var20, 96);
         } else {
            var15 = null;
         }

         var3.add(new MobDrawable() {
            public void draw(TickManager var1) {
               if (var15 != null) {
                  var15.draw(var1);
               }

               var14.draw(var1);
            }
         });
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.swampGuardian_shadow;
      int var6 = var5.getHeight();
      int var7 = var4.getDrawX(var1) - var6 / 2;
      int var8 = var4.getDrawY(var2) - var6 / 2;
      var8 += this.getBobbing(var1, var2);
      return var5.initDraw().sprite(2, 0, var6).light(var3).pos(var7, var8);
   }

   public boolean shouldDrawOnMap() {
      return this.isVisible();
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 16;
      int var5 = var3 - 16;
      float var6 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
      MobRegistry.Textures.fallenWizardDragon.initDraw().sprite(0, 6, 64).rotate(var6 + 90.0F, 16, 16).size(32, 32).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-12, -12, 24, 24);
   }

   public GameTooltips getMapTooltips() {
      return !this.isVisible() ? null : new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.POISON_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FROST_DAMAGE_FLAT, 0.0F)).max(0.0F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE, 0.0F)).max(0.0F));
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected WormMobBody createNewBodyPart(int var1) {
      return this.createNewBodyPart(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected float getDistToBodyPart(WormMobBody var1, int var2, float var3) {
      return this.getDistToBodyPart((FallenDragonBody)var1, var2, var3);
   }

   public static class FallenDragonAI<T extends FallenDragonHead> extends SequenceAINode<T> {
      public FallenDragonAI(int var1, int var2, int var3, int var4, int var5) {
         this.addChild(new ArenaCirclingAINode(var1, var2));
         this.addChild(new TargetFinderAINode<T>(var3) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayersAndHumans(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((FallenDragonHead)var1, var2, var3);
            }
         });
         this.addChild(new ChargingCirclingChaserAINode(var4, var5));
      }
   }

   public static class ArenaCirclingAINode<T extends FallenDragonHead> extends AINode<T> {
      public int circlingTime;
      public int circlingRange;
      public int timer;

      public ArenaCirclingAINode(int var1, int var2) {
         this.circlingTime = var1;
         this.circlingRange = var2;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (this.timer < this.circlingTime) {
            if (!var2.mover.isCurrentlyMovingFor(this)) {
               Point2D.Float var3;
               if (var1.centerPosition != null) {
                  var3 = var1.centerPosition;
               } else {
                  var3 = new Point2D.Float(var1.x, var1.y);
               }

               float var4 = MobMovementCircle.convertToRotSpeed(this.circlingRange, var1.getSpeed());
               var2.mover.setCustomMovement(this, new MobMovementCircleLevelPos(var1, var3.x, var3.y, this.circlingRange, var4, var1.circlingAngleOffset, false));
            }

            this.timer += 50;
            return AINodeResult.FAILURE;
         } else {
            return AINodeResult.SUCCESS;
         }
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((FallenDragonHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((FallenDragonHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (FallenDragonHead)var2, var3);
      }
   }
}
