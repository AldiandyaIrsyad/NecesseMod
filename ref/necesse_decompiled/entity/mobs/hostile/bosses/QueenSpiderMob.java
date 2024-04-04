package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GamePoint3D;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.InverseKinematics;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.IntMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToOppositeDirectionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.FlyToRandomPositionAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.QueenSpiderEggProjectile;
import necesse.entity.projectile.QueenSpiderSpitProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class QueenSpiderMob extends FlyingBossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("cavespidergland", 10, 20, MultiTextureMatItem.getGNDData(1)), new ChanceLootItem(0.2F, "queenspidersdancevinyl")});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("spidercharm"), new LootItem("spiderclaw"), new LootItemList(new LootItemInterface[]{new LootItem("webbedgun"), LootItem.between("simplebullet", 50, 100)}), new LootItem("frostpiercer"));
   public static LootTable privateLootTable;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   private ArrayList<SpiderLeg> frontLegs;
   private ArrayList<SpiderLeg> backLegs;
   public float currentHeight;
   public EmptyMobAbility roarAbility;
   public IntMobAbility startLaunchAnimation;
   public long launchStartTime;
   public int launchAnimationTime;
   public EmptyMobAbility playSpitSoundAbility;
   public int jumpStartX;
   public int jumpStartY;
   public int jumpEndX;
   public int jumpEndY;
   public boolean isJumping;
   public CoordinateMobAbility startJumpAbility;
   public static GameDamage collisionDamage;
   public static GameDamage spitDamage;
   public static GameDamage landDamage;
   public static GameDamage hatchlingDamage;
   public static MaxHealthGetter MAX_HEALTH;
   public static float SPIT_LINGER_SECONDS;

   public QueenSpiderMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.moveAccuracy = 20;
      this.setSpeed(75.0F);
      this.setArmor(5);
      this.setFriction(2.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-60, -60, 120, 90);
      this.hitBox = new Rectangle(-60, -60, 120, 90);
      this.selectBox = new Rectangle(-70, -45, 140, 100);
      this.roarAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (QueenSpiderMob.this.isClient()) {
               Screen.playSound(GameResources.roar, SoundEffect.globalEffect().volume(0.7F).pitch(1.3F));
            }

         }
      });
      this.startLaunchAnimation = (IntMobAbility)this.registerAbility(new IntMobAbility() {
         protected void run(int var1) {
            QueenSpiderMob.this.launchStartTime = QueenSpiderMob.this.getWorldEntity().getLocalTime();
            QueenSpiderMob.this.launchAnimationTime = var1;
            if (QueenSpiderMob.this.isClient()) {
               Screen.playSound(GameResources.spit, SoundEffect.effect(QueenSpiderMob.this));
            }

         }
      });
      this.playSpitSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (QueenSpiderMob.this.isClient()) {
               Screen.playSound(GameResources.spit, SoundEffect.effect(QueenSpiderMob.this));
            }

         }
      });
      this.startJumpAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            QueenSpiderMob.this.jumpStartX = QueenSpiderMob.this.getX();
            QueenSpiderMob.this.jumpStartY = QueenSpiderMob.this.getY();
            QueenSpiderMob.this.jumpEndX = var1;
            QueenSpiderMob.this.jumpEndY = var2;
            QueenSpiderMob.this.isJumping = true;
         }
      });
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isJumping);
      if (this.isJumping) {
         var1.putNextInt(this.jumpStartX);
         var1.putNextInt(this.jumpStartY);
         var1.putNextInt(this.jumpEndX);
         var1.putNextInt(this.jumpEndY);
      }

   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isJumping = var1.getNextBoolean();
      if (this.isJumping) {
         this.jumpStartX = var1.getNextInt();
         this.jumpStartY = var1.getNextInt();
         this.jumpEndX = var1.getNextInt();
         this.jumpEndY = var1.getNextInt();
      }

   }

   public void setupHealthPacket(PacketWriter var1, boolean var2) {
      this.scaling.setupHealthPacket(var1, var2);
      super.setupHealthPacket(var1, var2);
   }

   public void applyHealthPacket(PacketReader var1, boolean var2) {
      this.scaling.applyHealthPacket(var1, var2);
      super.applyHealthPacket(var1, var2);
   }

   public void setMaxHealth(int var1) {
      super.setMaxHealth(var1);
      if (this.scaling != null) {
         this.scaling.updatedMaxHealth();
      }

   }

   public void setPos(float var1, float var2, boolean var3) {
      super.setPos(var1, var2, var3);
      if (var3 && this.backLegs != null && this.frontLegs != null) {
         Iterator var4 = this.backLegs.iterator();

         SpiderLeg var5;
         while(var4.hasNext()) {
            var5 = (SpiderLeg)var4.next();
            var5.snapToPosition();
         }

         var4 = this.frontLegs.iterator();

         while(var4.hasNext()) {
            var5 = (SpiderLeg)var4.next();
            var5.snapToPosition();
         }
      }

   }

   public void init() {
      super.init();
      this.frontLegs = new ArrayList();
      this.backLegs = new ArrayList();
      byte var1 = 8;
      this.currentHeight = this.getDesiredHeight();
      float[] var2 = new float[]{65.0F, 95.0F, 125.0F, 155.0F, -65.0F, -95.0F, -125.0F, -155.0F};

      for(int var3 = 0; var3 < var1; ++var3) {
         final float var4 = var2[var3] - 90.0F;
         float var5 = (float)(var3 + (var3 % 2 == 0 ? 4 : 0)) / (float)var1 % 1.0F;
         final Point2D.Float var6 = GameMath.getAngleDir(var4);
         float var7 = 170.0F;
         float var8 = 170.0F;
         if (var6.x < 0.0F) {
            var8 = 0.0F;
         } else if (var6.x > 0.0F) {
            var7 = 0.0F;
         }

         SpiderLeg var9 = new SpiderLeg(this, 125.0F, var5, var7, var8) {
            public GamePoint3D getCenterPosition() {
               byte var1 = 50;
               return new GamePoint3D((float)QueenSpiderMob.this.getDrawX() + var6.x * (float)var1, (float)QueenSpiderMob.this.getDrawY() + var6.y * (float)var1 * 0.5F, (float)QueenSpiderMob.this.getFlyingHeight());
            }

            public GamePoint3D getDesiredPosition() {
               Point2D.Float var1 = GameMath.getAngleDir(var4);
               short var2 = 130;
               float var3 = Math.min(QueenSpiderMob.this.getCurrentSpeed() / 250.0F, 1.0F);
               Point2D.Float var4x = GameMath.normalize(QueenSpiderMob.this.dx, QueenSpiderMob.this.dy);
               if (var4x.y < 0.0F) {
                  var3 *= 1.0F + -var4x.y * 30.0F / QueenSpiderMob.this.getSpeed();
               }

               return new GamePoint3D((float)QueenSpiderMob.this.getDrawX() + var1.x * (float)var2 + var4x.x * (float)var2 * var3, (float)QueenSpiderMob.this.getDrawY() + var1.y * (float)var2 + var4x.y * (float)var2 * var3, 0.0F);
            }

            public float getJumpHeight() {
               return QueenSpiderMob.this.getCurrentJumpHeight();
            }
         };
         if (var6.y < 0.0F) {
            this.backLegs.add(var9);
         } else {
            this.frontLegs.add(var9);
         }
      }

      this.frontLegs.sort(Comparator.comparingDouble((var0) -> {
         return (double)var0.y;
      }));
      this.backLegs.sort(Comparator.comparingDouble((var0) -> {
         return (double)var0.y;
      }));
      this.ai = new BehaviourTreeAI(this, new SpiderMotherAI(), new FlyingAIMover());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect());
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 150;
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void tickMovement(float var1) {
      float var2 = this.getDesiredHeight();
      float var3 = var2 - this.currentHeight;
      float var4 = Math.abs(var3) * 2.0F + 10.0F;
      float var5 = var4 * var1 / 250.0F;
      if (Math.abs(var3) < var5) {
         this.currentHeight = var2;
      } else {
         this.currentHeight += Math.signum(var3) * var5;
      }

      if (this.isJumping) {
         float var6 = this.getDistance((float)this.jumpEndX, (float)this.jumpEndY);
         float var7 = this.getSpeed() * 1.3F * var1 / 250.0F;
         if (!(var6 < var7)) {
            Point2D.Float var16 = GameMath.normalize((float)this.jumpEndX - this.x, (float)this.jumpEndY - this.y);
            this.x += var16.x * var7;
            this.y += var16.y * var7;
         } else {
            short var8 = 200;
            float var9 = 360.0F / (float)var8;

            int var11;
            for(int var10 = 0; var10 < var8; ++var10) {
               var11 = (int)((float)var10 * var9 + GameRandom.globalRandom.nextFloat() * var9);
               float var12 = (float)Math.sin(Math.toRadians((double)var11)) * (float)GameRandom.globalRandom.getIntBetween(100, 200);
               float var13 = (float)Math.cos(Math.toRadians((double)var11)) * (float)GameRandom.globalRandom.getIntBetween(100, 200) * 0.8F;
               this.getLevel().entityManager.addParticle(this.x, this.y, var10 % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(var12, var13, 0.8F).color(new Color(50, 50, 50)).heightMoves(0.0F, 30.0F).lifeTime(1000);
            }

            this.x = (float)this.jumpEndX;
            this.y = (float)this.jumpEndY;
            this.isJumping = false;
            this.stopMoving();
            if (this.isServer()) {
               short var19 = 300;
               var11 = var19 / 2;
               Ellipse2D.Float var20 = new Ellipse2D.Float(this.x - (float)var11, this.y - (float)var11 * 0.8F, (float)var19, (float)var19 * 0.8F);
               GameUtils.streamTargets(this, GameUtils.rangeTileBounds(this.getX(), this.getY(), 8)).filter((var2x) -> {
                  return var2x.canBeHit(this) && var20.intersects(var2x.getHitBox());
               }).forEach((var1x) -> {
                  var1x.isServerHit(landDamage, (float)var1x.getX() - this.x, (float)var1x.getY() - this.y, 150.0F, this);
               });
            }
         }

         this.calcNetworkSmooth(var1);
         Iterator var17 = this.backLegs.iterator();

         SpiderLeg var18;
         while(var17.hasNext()) {
            var18 = (SpiderLeg)var17.next();
            var18.snapToPosition();
         }

         var17 = this.frontLegs.iterator();

         while(var17.hasNext()) {
            var18 = (SpiderLeg)var17.next();
            var18.snapToPosition();
         }
      } else {
         super.tickMovement(var1);
         Iterator var14 = this.backLegs.iterator();

         SpiderLeg var15;
         while(var14.hasNext()) {
            var15 = (SpiderLeg)var14.next();
            var15.tickMovement(var1);
         }

         var14 = this.frontLegs.iterator();

         while(var14.hasNext()) {
            var15 = (SpiderLeg)var14.next();
            var15.tickMovement(var1);
         }
      }

   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.QueenSpidersDance, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(70.0F + var1 * 40.0F);
   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(70.0F + var1 * 40.0F);
   }

   public int getFlyingHeight() {
      return (int)this.currentHeight;
   }

   public float getDesiredHeight() {
      float var1 = GameUtils.getAnimFloat(this.getWorldEntity().getTime(), 1000);
      float var2 = GameMath.sin(var1 * 360.0F) * 10.0F;
      long var3 = this.getWorldEntity().getLocalTime();
      if (this.isJumping) {
         var2 = 0.0F;
      } else if (var3 < this.launchStartTime + (long)this.launchAnimationTime) {
         float var5 = (float)(var3 - this.launchStartTime) / (float)this.launchAnimationTime;
         float var6 = 0.4F;
         float var7 = 1.5F;
         var2 = 10.0F - (float)Math.pow(Math.sin(Math.pow((double)var5 * Math.PI, (double)var6) / Math.pow(Math.PI, (double)(var6 - 1.0F))), (double)var7) * 25.0F;
      }

      return (float)(20 + (int)var2);
   }

   public float getCurrentJumpHeight() {
      if (!this.isJumping) {
         return 0.0F;
      } else {
         float var1 = (float)(new Point2D.Float((float)this.jumpStartX, (float)this.jumpStartY)).distance((double)this.jumpEndX, (double)this.jumpEndY);
         float var2 = (float)(new Point2D.Float(this.x, this.y)).distance((double)this.jumpEndX, (double)this.jumpEndY);
         float var3 = var2 / var1;
         return GameMath.sin(var3 * 180.0F) * var1 / 1.2F;
      }
   }

   public Rectangle getSelectBox(int var1, int var2) {
      Rectangle var3 = super.getSelectBox(var1, var2);
      var3.y -= this.getFlyingHeight();
      return var3;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 7; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.queenSpiderDebris, var3, 0, 32, this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 15.0F, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5);
      int var12 = var8.getDrawY(var6);
      var12 -= this.getFlyingHeight();
      var12 = (int)((float)var12 - this.getCurrentJumpHeight());
      float var13 = GameMath.limit(this.dx / 10.0F, -10.0F, 10.0F);
      TextureDrawOptionsEnd var14 = MobRegistry.Textures.queenSpiderBody.initDraw().light(var10).rotate(var13, MobRegistry.Textures.queenSpiderBody.getWidth() / 2, (int)((float)MobRegistry.Textures.queenSpiderBody.getHeight() * 0.6F)).posMiddle(var11, var12);
      TextureDrawOptionsEnd var15 = MobRegistry.Textures.queenSpiderHead.initDraw().light(var10).rotate(var13, MobRegistry.Textures.queenSpiderHead.getWidth() / 2, (int)((float)MobRegistry.Textures.queenSpiderHead.getHeight() * 0.6F)).posMiddle(var11, var12 + 24);
      DrawOptionsList var16 = new DrawOptionsList();
      DrawOptionsList var17 = new DrawOptionsList();
      DrawOptionsList var18 = new DrawOptionsList();
      DrawOptionsList var19 = new DrawOptionsList();
      DrawOptionsList var20 = new DrawOptionsList();
      Iterator var21 = this.backLegs.iterator();

      SpiderLeg var22;
      while(var21.hasNext()) {
         var22 = (SpiderLeg)var21.next();
         var22.addDrawOptions(var16, var17, var18, var4, var8);
      }

      var21 = this.frontLegs.iterator();

      while(var21.hasNext()) {
         var22 = (SpiderLeg)var21.next();
         var22.addDrawOptions(var16, var19, var20, var4, var8);
      }

      TextureDrawOptions var23 = this.getShadowDrawOptions(var5, var6, var10, var8);
      var3.add((var8x) -> {
         var23.draw();
         var16.draw();
         var17.draw();
         var18.draw();
         var14.draw();
         var19.draw();
         var20.draw();
         var15.draw();
      });
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.queenSpider_shadow;
      int var6 = var4.getDrawX(var1) - var5.getWidth() / 2;
      int var7 = var4.getDrawY(var2) - var5.getHeight() / 2 + 24;
      return var5.initDraw().light(var3).pos(var6, var7);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      MobRegistry.Textures.queenSpiderHead.initDraw().size(MobRegistry.Textures.queenSpiderHead.getWidth() / 2, MobRegistry.Textures.queenSpiderHead.getHeight() / 2).posMiddle(var2, var3).draw();
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-16, -16, 32, 32);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F));
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var1x) -> {
         this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), (ServerClient)((PlayerMob)var1x).getServerClient());
      });
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{uniqueDrops});
      collisionDamage = new GameDamage(28.0F);
      spitDamage = new GameDamage(24.0F);
      landDamage = new GameDamage(40.0F);
      hatchlingDamage = new GameDamage(23.0F);
      MAX_HEALTH = new MaxHealthGetter(3500, 4000, 4500, 5000, 6000);
      SPIT_LINGER_SECONDS = 60.0F;
   }

   public abstract static class SpiderLeg {
      public final Mob mob;
      public float startX;
      public float startY;
      public float x;
      public float y;
      public float nextX;
      public float nextY;
      public boolean isMoving;
      private InverseKinematics ik;
      private List<Line2D.Float> shadowLines;
      public float maxLeftAngle;
      public float maxRightAngle;
      private float moveAtDist;
      private float checkX;
      private float checkY;
      private float distBuffer;

      public SpiderLeg(Mob var1, float var2, float var3, float var4, float var5) {
         this.mob = var1;
         this.moveAtDist = var2;
         this.distBuffer = var2 * var3;
         this.maxLeftAngle = var4;
         this.maxRightAngle = var5;
         this.snapToPosition();
         this.checkX = this.x;
         this.checkY = this.y;
      }

      public void snapToPosition() {
         GamePoint3D var1 = this.getDesiredPosition();
         this.x = var1.x;
         this.y = var1.y;
         this.nextX = var1.x;
         this.nextY = var1.y;
         this.updateIK();
      }

      public void tickMovement(float var1) {
         GamePoint3D var2 = this.getCenterPosition();
         double var3 = (new Point2D.Float(var2.x, var2.y)).distance((double)this.checkX, (double)this.checkY);
         this.distBuffer = (float)((double)this.distBuffer + var3);
         this.checkX = var2.x;
         this.checkY = var2.y;
         if (var3 == 0.0) {
            this.distBuffer += var1 / (this.moveAtDist / 20.0F);
         }

         if (!this.isMoving) {
            GamePoint3D var5 = this.getDesiredPosition();
            double var6 = (new Point2D.Float(var5.x, var5.y)).distance((double)this.x, (double)this.y);
            if (var6 > 175.0) {
               this.distBuffer += this.moveAtDist;
            }

            if (this.distBuffer >= this.moveAtDist) {
               this.distBuffer -= this.moveAtDist;
               if (this.x != var5.x || this.y != var5.y) {
                  this.startX = this.x;
                  this.startY = this.y;
                  this.nextX = var5.x;
                  this.nextY = var5.y;
                  this.isMoving = true;
               }
            }
         }

         if (this.isMoving) {
            double var15 = (new Point2D.Float(this.x, this.y)).distance((double)this.nextX, (double)this.nextY);
            float var7 = (float)var15 * 2.0F + this.mob.getSpeed() * 1.2F;
            float var8 = var7 * var1 / 250.0F;
            if (var15 < (double)var8) {
               if (this.mob.isClient()) {
                  byte var9 = 20;
                  float var10 = 360.0F / (float)var9;

                  for(int var11 = 0; var11 < var9; ++var11) {
                     int var12 = (int)((float)var11 * var10 + GameRandom.globalRandom.nextFloat() * var10);
                     float var13 = (float)Math.sin(Math.toRadians((double)var12)) * (float)GameRandom.globalRandom.getIntBetween(10, 40);
                     float var14 = (float)Math.cos(Math.toRadians((double)var12)) * (float)GameRandom.globalRandom.getIntBetween(10, 40) * 0.8F;
                     this.mob.getLevel().entityManager.addParticle(this.x, this.y, var11 % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(var13, var14, 0.8F).color(new Color(50, 50, 50)).heightMoves(0.0F, 10.0F).lifeTime(500);
                  }

                  Screen.playSound(GameResources.punch, SoundEffect.effect(this.x, this.y).volume(0.4F).pitch(0.8F));
               }

               this.x = this.nextX;
               this.y = this.nextY;
               this.isMoving = false;
            } else {
               Point2D.Float var16 = GameMath.normalize(this.nextX - this.x, this.nextY - this.y);
               this.x += var16.x * var8;
               this.y += var16.y * var8;
            }
         }

         this.updateIK();
      }

      public void updateIK() {
         if (!this.mob.isServer()) {
            GamePoint3D var1 = this.getCenterPosition();
            Point2D.Float var2 = GameMath.normalize(this.x - var1.x, this.y - var1.y);
            float var3 = 0.5F;
            float var4 = (float)(new Point2D.Float(var1.x, var1.y)).distance((double)this.x, (double)this.y) * var3;
            float var5 = 40.0F;
            GamePoint3D var6 = var1.dirFromLength(var1.x + var2.x * var4, var1.y + var2.y * var4, var5, 80.0F);
            GamePoint3D var7 = var6.dirFromLength(this.x, this.y, 0.0F, 100.0F);
            float var8 = this.getJumpHeight();
            float var9 = 0.6F;
            synchronized(this) {
               this.shadowLines = Collections.synchronizedList(new LinkedList());
               this.shadowLines.add(new Line2D.Float(var1.x, var1.y + 18.0F, var6.x, var6.y - var6.height * var9));
               this.shadowLines.add(new Line2D.Float(var6.x, var6.y - var6.height * var9, this.x, this.y));
            }

            this.ik = InverseKinematics.startFromPoints(var1.x, var1.y - var1.height * var9 - var8, var6.x, var6.y - var6.height * var9 - var8, this.maxLeftAngle, this.maxRightAngle);
            this.ik.addJointPoint(var7.x, var7.y - var7.height * var9 - var8);
            this.ik.apply(this.x, this.y - this.getCurrentLegLift() - Math.max(var8 - 150.0F, var8 / 4.0F), 0.0F, 2.0F, 500);
         }
      }

      public float getCurrentLegLift() {
         double var1 = (new Point2D.Float(this.startX, this.startY)).distance((double)this.nextX, (double)this.nextY);
         float var3 = Math.min((float)var1 / 40.0F, 1.0F) * 20.0F + 5.0F;
         double var4 = (new Point2D.Float(this.x, this.y)).distance((double)this.nextX, (double)this.nextY);
         double var6 = GameMath.limit(var4 / var1, 0.0, 1.0);
         return GameMath.sin((float)var6 * 180.0F) * var3;
      }

      public abstract float getJumpHeight();

      public abstract GamePoint3D getDesiredPosition();

      public abstract GamePoint3D getCenterPosition();

      private static Shape generateShadowShape(Iterable<Line2D.Float> var0, float var1) {
         LinkedList var2 = new LinkedList();
         Path2D.Float var3 = new Path2D.Float();
         Line2D.Float var4 = null;
         Line2D.Float var5 = null;
         Iterator var6 = var0.iterator();

         Line2D.Float var7;
         Point2D.Float var8;
         Point2D.Float var9;
         Point2D.Float var10;
         Point2D.Float var11;
         Point2D.Float var12;
         Point2D.Float var13;
         Point2D var14;
         while(var6.hasNext()) {
            var7 = (Line2D.Float)var6.next();
            if (var4 == null) {
               var4 = var7;
            }

            var8 = GameMath.normalize(var7.x1 - var7.x2, var7.y1 - var7.y2);
            var9 = GameMath.getPerpendicularPoint(var7.x1, var7.y1, var1, var8);
            var10 = GameMath.getPerpendicularPoint(var7.x2, var7.y2, var1, var8);
            if (var5 != null) {
               var11 = GameMath.normalize(var5.x1 - var5.x2, var5.y1 - var5.y2);
               var12 = GameMath.getPerpendicularPoint(var5.x1, var5.y1, var1, var11);
               var13 = GameMath.getPerpendicularPoint(var5.x2, var5.y2, var1, var11);
               var14 = GameMath.getIntersectionPoint(new Line2D.Float(var9, var10), new Line2D.Float(var12, var13), false);
               if (var14 != null) {
                  var3.lineTo(var14.getX(), var14.getY());
               } else {
                  var3.lineTo(var13.x, var13.y);
                  var3.lineTo(var9.x, var9.y);
               }
            } else {
               var3.moveTo(var9.x, var9.y);
            }

            var5 = var7;
            var2.addFirst(var7);
         }

         Point2D.Float var15;
         Point2D.Float var16;
         if (var5 != null) {
            var15 = GameMath.normalize(var5.x1 - var5.x2, var5.y1 - var5.y2);
            var16 = GameMath.getPerpendicularPoint(var5.x2, var5.y2, var1, var15);
            var3.lineTo(var16.x, var16.y);
         }

         var5 = null;

         for(var6 = var2.iterator(); var6.hasNext(); var5 = var7) {
            var7 = (Line2D.Float)var6.next();
            var8 = GameMath.normalize(var7.x2 - var7.x1, var7.y2 - var7.y1);
            var9 = GameMath.getPerpendicularPoint(var7.x1, var7.y1, var1, var8);
            var10 = GameMath.getPerpendicularPoint(var7.x2, var7.y2, var1, var8);
            if (var5 != null) {
               var11 = GameMath.normalize(var5.x2 - var5.x1, var5.y2 - var5.y1);
               var12 = GameMath.getPerpendicularPoint(var5.x1, var5.y1, var1, var11);
               var13 = GameMath.getPerpendicularPoint(var5.x2, var5.y2, var1, var11);
               var14 = GameMath.getIntersectionPoint(new Line2D.Float(var10, var9), new Line2D.Float(var13, var12), false);
               if (var14 != null) {
                  var3.lineTo(var14.getX(), var14.getY());
               } else {
                  var3.lineTo(var12.x, var12.y);
                  var3.lineTo(var10.x, var10.y);
               }
            } else {
               var3.lineTo(var10.x, var10.y);
            }
         }

         if (var5 != null) {
            var15 = GameMath.normalize(var5.x2 - var5.x1, var5.y2 - var5.y1);
            var16 = GameMath.getPerpendicularPoint(var5.x1, var5.y1, var1, var15);
            var3.lineTo(var16.x, var16.y);
         }

         var3.closePath();
         return var3;
      }

      private static LinkedList<Point2D.Float> generateShadowTriangles(Iterable<Line2D.Float> var0, float var1) {
         LinkedList var2 = new LinkedList();
         Line2D.Float var3 = null;

         Line2D.Float var5;
         Point2D.Float var6;
         for(Iterator var4 = var0.iterator(); var4.hasNext(); var3 = var5) {
            var5 = (Line2D.Float)var4.next();
            var6 = GameMath.normalize(var5.x1 - var5.x2, var5.y1 - var5.y2);
            Point2D.Float var7 = GameMath.getPerpendicularPoint(var5.x1, var5.y1, var1, var6);
            Point2D.Float var8 = GameMath.getPerpendicularPoint(var5.x2, var5.y2, var1, var6);
            Point2D.Float var9 = GameMath.getPerpendicularPoint(var5.x1, var5.y1, -var1, var6);
            Point2D.Float var10 = GameMath.getPerpendicularPoint(var5.x2, var5.y2, -var1, var6);
            if (var3 != null) {
               Point2D.Float var11 = GameMath.normalize(var3.x1 - var3.x2, var3.y1 - var3.y2);
               Point2D.Float var12 = GameMath.getPerpendicularPoint(var3.x1, var3.y1, var1, var11);
               Point2D.Float var13 = GameMath.getPerpendicularPoint(var3.x2, var3.y2, var1, var11);
               Point2D var14 = GameMath.getIntersectionPoint(new Line2D.Float(var7, var8), new Line2D.Float(var12, var13), false);
               Point2D.Float var15 = GameMath.getPerpendicularPoint(var3.x1, var3.y1, -var1, var11);
               Point2D.Float var16 = GameMath.getPerpendicularPoint(var3.x2, var3.y2, -var1, var11);
               Point2D var17 = GameMath.getIntersectionPoint(new Line2D.Float(var9, var10), new Line2D.Float(var15, var16), false);
               if (var14 != null) {
                  var2.add(new Point2D.Float((float)var14.getX(), (float)var14.getY()));
                  var2.add(var16);
                  var2.add(new Point2D.Float((float)var14.getX(), (float)var14.getY()));
                  var2.add(var9);
               } else if (var17 != null) {
                  var2.add(var13);
                  var2.add(new Point2D.Float((float)var17.getX(), (float)var17.getY()));
                  var2.add(var7);
                  var2.add(new Point2D.Float((float)var17.getX(), (float)var17.getY()));
               } else {
                  var2.add(var7);
                  var2.add(var9);
               }
            } else {
               var2.add(var7);
               var2.add(var9);
            }
         }

         if (var3 != null) {
            Point2D.Float var18 = GameMath.normalize(var3.x1 - var3.x2, var3.y1 - var3.y2);
            Point2D.Float var19 = GameMath.getPerpendicularPoint(var3.x2, var3.y2, var1, var18);
            var6 = GameMath.getPerpendicularPoint(var3.x2, var3.y2, -var1, var18);
            var2.add(var19);
            var2.add(var6);
         }

         return var2;
      }

      public void addDrawOptions(DrawOptionsList var1, DrawOptionsList var2, DrawOptionsList var3, Level var4, GameCamera var5) {
         if (!this.mob.isServer()) {
            int var6 = MobRegistry.Textures.queenSpiderLeg_shadow.getWidth();
            synchronized(this) {
               Iterator var8 = this.shadowLines.iterator();

               while(true) {
                  if (!var8.hasNext()) {
                     break;
                  }

                  Line2D.Float var9 = (Line2D.Float)var8.next();
                  GameLight var10 = var4.getLightLevel((int)((var9.x1 + var9.x2) / 2.0F / 32.0F), (int)((var9.y1 + var9.y2) / 2.0F / 32.0F));
                  float var11 = GameMath.getAngle(new Point2D.Float(var9.x1 - var9.x2, var9.y1 - var9.y2));
                  float var12 = (float)var9.getP1().distance(var9.getP2());
                  TextureDrawOptionsEnd var13 = MobRegistry.Textures.queenSpiderLeg_shadow.initDraw().rotate(var11 + 90.0F, var6 / 2, 6).light(var10).size(var6, (int)var12 + 16).pos(var5.getDrawX(var9.x1 - (float)var6 / 2.0F), var5.getDrawY(var9.y1));
                  var1.add(var13);
               }
            }

            float var7 = this.getJumpHeight();
            int var16 = MobRegistry.Textures.queenSpiderLeg.getWidth();
            Iterator var17 = this.ik.limbs.iterator();

            while(var17.hasNext()) {
               InverseKinematics.Limb var18 = (InverseKinematics.Limb)var17.next();
               GameLight var19 = var4.getLightLevel((int)((var18.inboundX + var18.outboundX) / 2.0F / 32.0F), (int)(((var18.inboundY + var18.outboundY) / 2.0F + var7) / 32.0F));
               TextureDrawOptionsEnd var20 = MobRegistry.Textures.queenSpiderLeg.initDraw().rotate(var18.angle - 90.0F, var16 / 2, 4).light(var19).size(var16, (int)var18.length + 16).pos(var5.getDrawX(var18.inboundX - (float)var16 / 2.0F), var5.getDrawY(var18.inboundY) - 8);
               if (this.ik.limbs.getLast() == var18) {
                  var2.add(var20);
               } else {
                  var3.add(var20);
               }
            }

            if (GlobalData.debugActive()) {
               var3.add(() -> {
                  Screen.drawCircle(var5.getDrawX(this.x), var5.getDrawY(this.y - this.getCurrentLegLift()), 4, 12, 1.0F, 0.0F, 0.0F, 1.0F, false);
               });
               var3.add(() -> {
                  this.ik.drawDebug(var5, Color.RED, Color.GREEN);
               });
            }

         }
      }
   }

   public static class SpiderMotherAI<T extends QueenSpiderMob> extends SequenceAINode<T> {
      public SpiderMotherAI() {
         this.addChild(new RemoveOnNoTargetNode(100));
         this.addChild(new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((QueenSpiderMob)var1, var2, var3);
            }
         });
         AttackStageManagerNode var1 = new AttackStageManagerNode();
         this.addChild(new IsolateRunningAINode(var1));
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 300));
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var1.addChild(new LaunchEggsStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000);
         }));
         var1.addChild(new FlyToOppositeDirectionAttackStage(true, 300.0F, 20.0F));
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 300));
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var1.addChild(new SpitStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 500);
         }));
         var1.addChild(new FlyToRandomPositionAttackStage(true, 300));
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000);
         }));
         var1.addChild(new ChargeTargetStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000);
         }));
         var1.addChild(new ChargeTargetStage());
         var1.addChild(new IdleTimeAttackStage((var1x) -> {
            return this.getIdleTime(var1x, 1000);
         }));
         var1.addChild(new ChargeTargetStage());
      }

      private int getIdleTime(T var1, int var2) {
         float var3 = (float)var1.getHealth() / (float)var1.getMaxHealth();
         return (int)((float)var2 * var3);
      }
   }

   public static class JumpTargetStage<T extends QueenSpiderMob> extends AINode<T> implements AttackStageInterface<T> {
      public boolean jumped = false;

      public JumpTargetStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.jumped = false;
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 == null) {
            return AINodeResult.SUCCESS;
         } else {
            if (!this.jumped) {
               var1.startJumpAbility.runAndSend((int)(var3.x + GameRandom.globalRandom.floatGaussian() * 30.0F), (int)(var3.y + GameRandom.globalRandom.floatGaussian() * 30.0F));
               this.jumped = true;
            }

            return var1.isJumping ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
         }
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (QueenSpiderMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((QueenSpiderMob)var1, var2);
      }
   }

   public static class SpitStage<T extends QueenSpiderMob> extends AINode<T> implements AttackStageInterface<T> {
      private int timer;
      private float shootBuffer;
      private boolean reversed;
      private int radius;

      public SpitStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         this.timer += 50;
         if (var3 != null) {
            float var4 = (float)var1.getHealth() / (float)var1.getMaxHealth();
            float var5 = 0.25F + var4 * 0.4F;
            this.shootBuffer += 1.0F / var5 / 20.0F;
            if (this.shootBuffer >= 1.0F) {
               --this.shootBuffer;
               float var6 = Math.abs(var4 - 1.0F);
               float var7 = GameRandom.globalRandom.nextFloat();
               int var8 = (int)(140.0F * var7);
               float var9 = (float)GameRandom.globalRandom.nextInt(360);
               Point2D.Float var10 = GameMath.getAngleDir(var9);
               Point2D.Float var11 = new Point2D.Float(var3.x + var10.x * (float)var8, var3.y + var10.y * (float)var8);
               int var12 = Math.min((int)var11.distance((double)var1.x, (double)var1.y), 960);
               float var13 = 60.0F + var6 * 40.0F;
               var1.getLevel().entityManager.projectiles.add(new QueenSpiderSpitProjectile(var1.getLevel(), var1, var1.x, var1.y, var11.x, var11.y, var13, var12, QueenSpiderMob.spitDamage, 50));
               var1.playSpitSoundAbility.runAndSend();
            }
         }

         if (this.timer >= 4000) {
            var2.mover.stopMoving(var1);
            return AINodeResult.SUCCESS;
         } else {
            return AINodeResult.RUNNING;
         }
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.timer = 0;
         this.shootBuffer = 0.0F;
         this.reversed = !this.reversed;
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         if (var3 != null) {
            this.radius = GameMath.limit((int)var1.getDistance(var3), 200, 400);
            float var4 = (float)var1.getHealth() / (float)var1.getMaxHealth();
            float var5 = 0.5F + Math.abs(var4 - 1.0F) / 2.0F;
            float var6 = MobMovementCircleLevelPos.convertToRotSpeed(this.radius, var1.getSpeed() * var5);
            var2.mover.setCustomMovement(this, new MobMovementCircleLevelPos(var1, var3.x, var3.y, this.radius, var6, this.reversed));
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (QueenSpiderMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((QueenSpiderMob)var1, var2);
      }
   }

   public static class ChargeTargetStage<T extends QueenSpiderMob> extends FlyToOppositeDirectionAttackStage<T> {
      public ChargeTargetStage() {
         super(true, 250.0F, 0.0F);
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         super.onStarted(var1, var2);
         if (var2.mover.isMoving()) {
            var1.roarAbility.runAndSend();
            var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.SPIDER_CHARGE, var1, 5.0F, (Attacker)null), true);
         }

      }

      public void onEnded(T var1, Blackboard<T> var2) {
         super.onEnded(var1, var2);
         var1.buffManager.removeBuff(BuffRegistry.SPIDER_CHARGE, true);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((QueenSpiderMob)var1, var2);
      }
   }

   public static class LaunchEggsStage<T extends QueenSpiderMob> extends AINode<T> implements AttackStageInterface<T> {
      public float buffer;
      public float eggsPerLaunch;
      public int launchCounter;

      public LaunchEggsStage() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public void onStarted(T var1, Blackboard<T> var2) {
         this.buffer = 0.0F;
         this.eggsPerLaunch = 1.0F;
         this.launchCounter = 0;
      }

      public void onEnded(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         float var3 = 0.7F;
         this.buffer += 1.0F / var3 / 20.0F;
         if (this.buffer >= 1.0F) {
            var1.startLaunchAnimation.runAndSend((int)(var3 * 1000.0F));

            float var5;
            for(int var4 = 0; var4 < (int)this.eggsPerLaunch; ++var4) {
               var5 = GameRandom.globalRandom.getFloatBetween(0.7F, 1.0F);
               int var6 = (int)(300.0F * var5);
               float var7 = (float)GameRandom.globalRandom.nextInt(360);
               Point2D.Float var8 = GameMath.getAngleDir(var7);
               Point2D.Float var9 = new Point2D.Float(var1.x + var8.x * (float)var6, var1.y + var8.y * (float)var6);
               var1.getLevel().entityManager.projectiles.add(new QueenSpiderEggProjectile(var1.getLevel(), var1, var1.x, var1.y, var9.x, var9.y, 30.0F, var6, new GameDamage(0.0F), 50));
            }

            --this.buffer;
            float var10 = Math.abs(GameMath.limit((float)var1.getHealth() / (float)var1.getMaxHealth(), 0.0F, 1.0F) - 1.0F);
            var5 = var10 * 1.3F;
            long var11 = GameUtils.streamServerClients(var1.getLevel()).filter((var1x) -> {
               return !var1x.isDead() && var1.getDistance(var1x.playerMob) < 1280.0F;
            }).count();
            float var12 = Math.min(1.0F + (float)(var11 - 1L) / 2.0F, 4.0F);
            this.eggsPerLaunch += var5 * var12;
            ++this.launchCounter;
            if (this.launchCounter >= 4) {
               return AINodeResult.SUCCESS;
            }
         }

         return AINodeResult.RUNNING;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (QueenSpiderMob)var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onEnded(Mob var1, Blackboard var2) {
         this.onEnded((QueenSpiderMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onStarted(Mob var1, Blackboard var2) {
         this.onStarted((QueenSpiderMob)var1, var2);
      }
   }
}
