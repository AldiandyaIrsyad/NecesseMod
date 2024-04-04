package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.WormMoveLineSpawnData;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.FloatMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChargingCirclingChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.RemoveOnNoTargetNode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.mobs.mobMovement.MobMovementSpiral;
import necesse.entity.mobs.mobMovement.MobMovementSpiralLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.inventory.lootTable.lootItem.MobConditionLootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PestWardenHead extends BossWormMobHead<PestWardenBody, PestWardenHead> {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemMultiplierIgnored(new ChanceLootItem(0.2F, "pestwardenschargevinyl"))});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("venomshower"), new LootItem("venomslasher"), new LootItem("livingshotty"), new LootItem("swampsgrasp"));
   public static LootTable privateLootTable;
   public static MaxHealthGetter BASE_MAX_HEALTH;
   public static MaxHealthGetter INCURSION_MAX_HEALTH;
   public static float lengthPerBodyPart;
   public static float waveLength;
   public static final int totalBodyParts = 150;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   protected float temporarySpeed;
   protected FloatMobAbility setTemporarySpeed;
   protected boolean isHardened;
   protected BooleanMobAbility setHardened;
   public GameDamage collisionDamage;
   public static GameDamage baseHeadCollisionDamage;
   public static GameDamage baseBodyCollisionDamage;
   public static GameDamage incursionHeadCollisionDamage;
   public static GameDamage incursionBodyCollisionDamage;

   public PestWardenHead() {
      super(100, waveLength, 80.0F, 150, 0.0F, 0.0F);
      this.difficultyChanges.setMaxHealth(BASE_MAX_HEALTH);
      this.moveAccuracy = 160;
      this.setSpeed(150.0F);
      this.setArmor(30);
      this.accelerationMod = 1.0F;
      this.decelerationMod = 1.0F;
      this.collision = new Rectangle(-35, -20, 70, 40);
      this.hitBox = new Rectangle(-40, -25, 80, 50);
      this.selectBox = new Rectangle(-50, -80, 100, 100);
      this.setTemporarySpeed = (FloatMobAbility)this.registerAbility(new FloatMobAbility() {
         protected void run(float var1) {
            PestWardenHead.this.temporarySpeed = var1;
         }
      });
      this.setHardened = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            PestWardenHead.this.isHardened = var1;
         }
      });
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextFloat(this.temporarySpeed);
      var1.putNextBoolean(this.isHardened);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.temporarySpeed = var1.getNextFloat();
      this.isHardened = var1.getNextBoolean();
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

   protected void onAppearAbility() {
      super.onAppearAbility();
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
      }

   }

   protected float getDistToBodyPart(PestWardenBody var1, int var2, float var3) {
      if (var2 == 0) {
         return Math.max(lengthPerBodyPart - 5.0F, 5.0F);
      } else if (var2 >= 146) {
         int var4 = var2 - 146 + 1;
         return Math.max(lengthPerBodyPart - (float)(var4 * 5), 5.0F);
      } else {
         return lengthPerBodyPart;
      }
   }

   protected PestWardenBody createNewBodyPart(int var1) {
      PestWardenBody var2 = new PestWardenBody();
      var2.index = var1;
      var2.sharesHitCooldownWithNext = var1 % 5 < 4;
      int var3;
      if (var1 >= 146) {
         var3 = var1 - 146 + 2;
         var2.sprite = new Point(var3, 0);
         var2.shadowSprite = var3;
         var2.showLegs = false;
      } else {
         var3 = var1 % 2;
         var2.sprite = new Point(var3, 0);
         var2.shadowSprite = var3;
         var2.showLegs = true;
      }

      return var2;
   }

   protected void playMoveSound() {
      Screen.playSound(GameResources.shake, SoundEffect.effect(this).falloffDistance(1000));
   }

   public void init() {
      if (this.getLevel() instanceof IncursionLevel) {
         this.difficultyChanges.setMaxHealth(INCURSION_MAX_HEALTH);
         this.setHealth(this.getMaxHealth());
         this.collisionDamage = incursionHeadCollisionDamage;
      } else {
         this.collisionDamage = baseHeadCollisionDamage;
      }

      super.init();
      this.ai = new BehaviourTreeAI(this, new PestWardenAI(), new FlyingAIMover());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.2F));
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return this.collisionDamage;
   }

   public int getCollisionKnockback(Mob var1) {
      return 150;
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.PestWardensCharge, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      if (this.temporarySpeed > 0.0F) {
         this.setSpeed(this.temporarySpeed);
      } else {
         float var1 = (float)this.getHealth() / (float)this.getMaxHealth();
         float var2 = Math.abs((float)Math.pow((double)var1, 0.20000000298023224) - 1.0F);
         this.setSpeed(140.0F + var2 * 300.0F);
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      if (this.temporarySpeed > 0.0F) {
         this.setSpeed(this.temporarySpeed);
      } else {
         float var1 = (float)this.getHealth() / (float)this.getMaxHealth();
         float var2 = Math.abs((float)Math.pow((double)var1, 0.20000000298023224) - 1.0F);
         this.setSpeed(140.0F + var2 * 300.0F);
      }

   }

   public WormMoveLine newMoveLine(Point2D var1, Point2D var2, boolean var3, float var4, boolean var5) {
      return new PestWardenMoveLine(var1, var2, var3, var4, var5, this.isHardened);
   }

   public WormMoveLine readMoveLine(PacketReader var1, WormMoveLineSpawnData var2) {
      return new PestWardenMoveLine(var1, var2);
   }

   public void spawnDeathParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.pestWarden, 8 + GameRandom.globalRandom.nextInt(6), 4, 64, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      if (this.isVisible()) {
         GameLight var10 = var4.getLightLevel(this);
         int var11 = var8.getDrawX(var5) - 64;
         int var12 = var8.getDrawY(var6);
         float var13 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
         Point2D.Float var14 = GameMath.getAngleDir(var13);
         int var15 = (int)(var13 / 45.0F);
         var15 = (var15 + 2) % 8;
         float var16 = (float)(var11 + 64 - 32) + var14.x * 8.0F;
         float var17 = (float)(var12 - 96 + 4) + var14.y * 4.0F;
         final Point2D.Float var18 = GameMath.getAngleDir(var13 + 90.0F);
         final Point2D.Float var19 = GameMath.getAngleDir(var13 + 90.0F + 180.0F);
         int var20 = (int)(var16 + var18.x * 16.0F);
         int var21 = (int)(var16 + var19.x * 16.0F);
         int var22 = (int)(var17 + var18.y * 4.0F);
         int var23 = (int)(var17 + var19.y * 4.0F);
         final TextureDrawOptionsEnd var24 = MobRegistry.Textures.pestWarden.initDraw().sprite(var15, 4, 64).light(var10).pos(var20, var22);
         final TextureDrawOptionsEnd var25 = MobRegistry.Textures.pestWarden.initDraw().sprite(var15, 4, 64).light(var10).pos(var21, var23);
         final MobDrawable var26 = WormMobHead.getDrawable(new GameSprite(MobRegistry.Textures.pestWarden, var15, 1, 128), MobRegistry.Textures.pestWarden_mask, var10, (int)this.height, var11, var12, 112);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               float var2 = -0.7F;
               if (var18.y < var2) {
                  var24.draw();
               }

               if (var19.y < var2) {
                  var25.draw();
               }

               var26.draw(var1);
               if (var18.y >= var2) {
                  var24.draw();
               }

               if (var19.y >= var2) {
                  var25.draw();
               }

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
      return var5.initDraw().sprite(2, 0, var6).light(var3).pos(var7, var8 - 40);
   }

   public boolean shouldDrawOnMap() {
      return this.isVisible();
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 20;
      int var5 = var3 - 6;
      float var6 = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(this.dx, this.dy)));
      Point2D.Float var7 = GameMath.getAngleDir(var6);
      int var8 = (int)(var6 / 45.0F);
      var8 = (var8 + 2) % 8;
      float var9 = (float)var4 + 21.333334F - 10.666667F + var7.x * 8.0F / 3.0F;
      float var10 = (float)var5 - 32.0F + 1.3333334F + var7.y * 4.0F / 3.0F + 8.0F;
      Point2D.Float var11 = GameMath.getAngleDir(var6 + 90.0F);
      Point2D.Float var12 = GameMath.getAngleDir(var6 + 90.0F + 180.0F);
      int var13 = (int)(var9 + var11.x * 16.0F / 3.0F);
      int var14 = (int)(var9 + var12.x * 16.0F / 3.0F);
      int var15 = (int)(var10 + var11.y * 4.0F / 3.0F);
      int var16 = (int)(var10 + var12.y * 4.0F / 3.0F);
      TextureDrawOptionsEnd var17 = MobRegistry.Textures.pestWarden.initDraw().sprite(var8, 4, 64).size(21).pos(var13, var15);
      TextureDrawOptionsEnd var18 = MobRegistry.Textures.pestWarden.initDraw().sprite(var8, 4, 64).size(21).pos(var14, var16);
      GameLight var19 = this.getLevel().lightManager.newLight(150.0F);
      MobDrawable var20 = WormMobHead.getDrawable(new GameSprite(MobRegistry.Textures.pestWarden, var8, 1, 128, 42), (GameTexture)null, var19, (int)this.height, var4, var5, 26);
      float var21 = -0.7F;
      if (var11.y < var21) {
         var17.draw();
      }

      if (var12.y < var21) {
         var18.draw();
      }

      var20.draw(var1);
      if (var11.y >= var21) {
         var17.draw();
      }

      if (var12.y >= var21) {
         var18.draw();
      }

   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-15, -25, 30, 36);
   }

   public GameTooltips getMapTooltips() {
      return !this.isVisible() ? null : new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), (new ModifierValue(BuffModifiers.POISON_DAMAGE, 1.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FIRE_DAMAGE, 1.0F)).max(0.2F), (new ModifierValue(BuffModifiers.FROST_DAMAGE, 1.0F)).max(0.2F));
   }

   public float getIncomingDamageModifier() {
      return super.getIncomingDamageModifier() * (this.isHardened ? 0.1F : 1.0F);
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var1x) -> {
         this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), (ServerClient)((PlayerMob)var1x).getServerClient());
      });
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected WormMobBody createNewBodyPart(int var1) {
      return this.createNewBodyPart(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected float getDistToBodyPart(WormMobBody var1, int var2, float var3) {
      return this.getDistToBodyPart((PestWardenBody)var1, var2, var3);
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new MobConditionLootItemList((var0) -> {
         return var0.getLevel() == null || !var0.getLevel().isIncursionLevel;
      }, new LootItemInterface[]{uniqueDrops})});
      BASE_MAX_HEALTH = new MaxHealthGetter(24000, 38000, 45000, 50000, 58000);
      INCURSION_MAX_HEALTH = new MaxHealthGetter(35000, 47000, 52000, 57000, 65000);
      lengthPerBodyPart = 35.0F;
      waveLength = 500.0F;
      baseHeadCollisionDamage = new GameDamage(85.0F);
      baseBodyCollisionDamage = new GameDamage(55.0F);
      incursionHeadCollisionDamage = new GameDamage(100.0F);
      incursionBodyCollisionDamage = new GameDamage(70.0F);
   }

   public static class PestWardenAI<T extends PestWardenHead> extends SelectorAINode<T> {
      public PestWardenAI() {
         SequenceAINode var1 = new SequenceAINode();
         this.addChild(var1);
         var1.addChild(new RemoveOnNoTargetNode(100));
         TargetFinderAINode var2;
         var1.addChild(var2 = new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((PestWardenHead)var1, var2, var3);
            }
         });
         var2.moveToAttacker = false;
         SelectorAINode var3 = new SelectorAINode();
         var1.addChild(var3);
         SpirallingAINode var5 = new SpirallingAINode();
         var3.addChild(var5);
         ChargingCirclingChaserAINode var4;
         var3.addChild(var4 = new ChargingCirclingChaserAINode(800, 40));
         var4.backOffOnReset = false;
         var5.chaser = var4;
         this.addChild(new WandererAINode(0));
      }
   }

   public static class SpirallingAINode<T extends PestWardenHead> extends AINode<T> {
      public int startMoveAccuracy;
      public ChargingCirclingChaserAINode<T> chaser;
      public boolean isActive = true;
      public int activeTicker;
      public boolean isMovingToStartPos = true;
      public Mob spiralTarget;
      public Point2D.Float spiralPos;
      public float spiralRadius = 380.0F;

      public SpirallingAINode() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         this.startMoveAccuracy = var2.moveAccuracy;
         this.activeTicker = 20 * GameRandom.globalRandom.getIntBetween(15, 20);
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         if (!this.isActive) {
            --this.activeTicker;
            if (this.activeTicker < 0) {
               this.isActive = true;
               this.activeTicker = 20 * GameRandom.globalRandom.getIntBetween(15, 20);
               if (this.chaser != null) {
                  this.chaser.fixMoveAccuracy();
               }

               return this.findNewStartPos();
            } else {
               return AINodeResult.FAILURE;
            }
         } else {
            boolean var3 = var2.mover.isCurrentlyMovingFor(this);
            if (this.isMovingToStartPos) {
               if (!var3) {
                  return this.findNewStartPos();
               } else if (var1.hasArrivedAtTarget()) {
                  if (this.spiralTarget != null && !this.spiralTarget.removed() && this.spiralTarget.isSamePlace(var1)) {
                     this.startSpiralling(this.spiralTarget);
                     return AINodeResult.SUCCESS;
                  } else {
                     return this.findNewStartPos();
                  }
               } else {
                  return AINodeResult.SUCCESS;
               }
            } else if (var3 && this.spiralTarget != null && this.spiralPos != null) {
               boolean var4 = var1.hasArrivedAtTarget() || this.spiralTarget.removed() || !var1.isSamePlace(this.spiralTarget);
               if (!var4) {
                  float var5 = this.spiralTarget.getDistance(this.spiralPos.x, this.spiralPos.y);
                  float var6 = this.spiralRadius + 50.0F;
                  MobMovement var7 = var1.getCurrentMovement();
                  if (var7 instanceof MobMovementSpiral) {
                     float var8 = ((MobMovementSpiral)var7).getCurrentRadius();
                     var6 = Math.min(var6, var8 + 50.0F);
                  }

                  var4 = var5 > var6;
               }

               if (var4) {
                  this.isActive = false;
                  var1.moveAccuracy = this.startMoveAccuracy;
                  var1.setTemporarySpeed.runAndSend(0.0F);
                  var1.setHardened.runAndSend(false);
                  if (this.chaser != null && !this.spiralTarget.removed() && var1.isSamePlace(this.spiralTarget)) {
                     this.chaser.startCircling(var1, var2, this.spiralTarget, var1.hasArrivedAtTarget() ? 8 : 1);
                  }

                  return AINodeResult.FAILURE;
               } else {
                  if (!var1.isHardened) {
                     var1.setHardened.runAndSend(true);
                  }

                  return AINodeResult.SUCCESS;
               }
            } else {
               this.isActive = false;
               var1.moveAccuracy = this.startMoveAccuracy;
               return AINodeResult.FAILURE;
            }
         }
      }

      public void startSpiralling(Mob var1) {
         PestWardenHead var2 = (PestWardenHead)this.mob();
         Point2D.Float var3 = GameMath.normalize(var2.dx, var2.dy);
         float var4 = GameMath.getAngle(var3);
         float var5 = GameMath.getAngle(GameMath.normalize(var1.x - var2.x, var1.y - var2.y));
         float var6 = GameMath.getAngleDifference(var4, var5);
         Point2D.Float var7 = new Point2D.Float(var1.x, var1.y);
         byte var8 = 40;
         int var9 = MobMovementSpiral.getSemiCircles(this.spiralRadius, (float)var8, 200.0F);
         float var10 = var2.getSpeed();
         MobMovementSpiralLevelPos var11 = new MobMovementSpiralLevelPos(var2, var7.x, var7.y, this.spiralRadius, var9, (float)var8, var10, var6 < 0.0F);
         if (var11.clockwise) {
            var11.startAngle += 15.0F;
         } else {
            var11.startAngle -= 15.0F;
         }

         this.getBlackboard().mover.setCustomMovement(this, var11);
         this.isMovingToStartPos = false;
         this.spiralTarget = var1;
         this.spiralPos = var7;
         var2.moveAccuracy = 5;
         var2.setTemporarySpeed.runAndSend(var10);
         var2.setHardened.runAndSend(true);
      }

      public AINodeResult findNewStartPos() {
         Blackboard var1 = this.getBlackboard();
         Mob var2 = (Mob)var1.getObject(Mob.class, "currentTarget");
         if (var2 != null) {
            int var3 = GameRandom.globalRandom.nextInt(360);
            Point2D.Float var4 = GameMath.getAngleDir((float)var3);
            ((PestWardenHead)this.mob()).moveAccuracy = this.startMoveAccuracy;
            var1.mover.setCustomMovement(this, new MobMovementRelative(var2, var4.x * this.spiralRadius, var4.y * this.spiralRadius));
            this.spiralTarget = var2;
            this.isMovingToStartPos = true;
            return AINodeResult.SUCCESS;
         } else {
            this.isActive = false;
            ((PestWardenHead)this.mob()).moveAccuracy = this.startMoveAccuracy;
            return AINodeResult.FAILURE;
         }
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((PestWardenHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((PestWardenHead)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (PestWardenHead)var2, var3);
      }
   }
}
