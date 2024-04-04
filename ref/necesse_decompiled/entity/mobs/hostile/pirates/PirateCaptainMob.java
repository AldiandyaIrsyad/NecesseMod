package necesse.entity.mobs.hostile.pirates;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.AreaFinder;
import necesse.engine.CameraShake;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.manager.EntityManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.TimedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PirateAITree;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.friendly.human.humanShop.PirateHumanMob;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.shader.ShaderState;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.light.GameLight;

public class PirateCaptainMob extends PirateMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("coin", 300, 500), new ChanceLootItem(0.2F, "siegevinyl")});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItemList(new LootItemInterface[]{new LootItem("handcannon"), LootItem.between("cannonball", 10, 50)}), new LootItem("piratetelescope"), new LootItem("spareboatparts"));
   public static LootTable privateLootTable;
   public static MobSpawnTable pirateMobs;
   public static MaxHealthGetter MAX_HEALTH;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   protected boolean inCombat;
   protected boolean inSecondStage;
   protected boolean inSecondStageTransition;
   protected long secondStageTransitionStartTime;
   protected CameraShake secondStageCameraShake;
   protected SoundPlayer secondStageRumble;
   protected SoundPlayer secondStageWind;
   private PositionSoundEffect secondStageWindEffect;
   protected float leftCannonRotation = 135.0F;
   protected float midCannonRotation = 90.0F;
   protected float rightCannonRotation = 45.0F;
   public boolean dropLadder;
   protected int shootDistance = 960;
   public BooleanMobAbility setInCombatAbility;
   public TimedMobAbility startSecondStageAbility;
   public EmptyMobAbility endSecondStageAbility;
   public CoordinateMobAbility fireCannonAbility;
   public static int secondStageTransitionTime;
   public static int captainMeleeDamage;
   public static int captainCannonDamage;
   public static GameDamage collisionDamage;

   public PirateCaptainMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.setSpeed(15.0F);
      this.setKnockbackModifier(0.0F);
      this.setRegen(100.0F);
      this.meleeDamage = captainMeleeDamage;
      this.shootDamage = captainCannonDamage;
      this.setInCombatAbility = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            PirateCaptainMob.this.inCombat = var1;
         }
      });
      this.startSecondStageAbility = (TimedMobAbility)this.registerAbility(new TimedMobAbility() {
         protected void run(long var1) {
            PirateCaptainMob.this.inSecondStageTransition = true;
            PirateCaptainMob.this.secondStageTransitionStartTime = var1;
            PirateCaptainMob.this.inSecondStage = true;
            PirateCaptainMob.this.moveX = 0.0F;
            PirateCaptainMob.this.moveY = 0.0F;
            PirateCaptainMob.this.setMovement((MobMovement)null);
            PirateCaptainMob.this.updateStageVariables();
         }
      });
      this.endSecondStageAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            PirateCaptainMob.this.inSecondStageTransition = false;
            PirateCaptainMob.this.inSecondStage = false;
            PirateCaptainMob.this.updateStageVariables();
            if (!PirateCaptainMob.this.isClient() && PirateCaptainMob.this.collidesWith(PirateCaptainMob.this.getLevel())) {
               Point var1 = PirateCaptainMob.this.findEmptySpot(4);
               if (var1 != null) {
                  PirateCaptainMob.this.setPos((float)var1.x, (float)var1.y, true);
                  PirateCaptainMob.this.sendMovementPacket(true);
               }
            }

         }
      });
      this.fireCannonAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            Point2D.Float var3 = GameMath.normalize((float)(var1 - PirateCaptainMob.this.getX()), (float)(var2 - PirateCaptainMob.this.getY() - 20));
            float var4 = GameMath.fixAngle(GameMath.getAngle(var3) + 90.0F);
            if (var4 >= 45.0F && var4 < 135.0F) {
               PirateCaptainMob.this.rightCannonRotation = var4 - 90.0F;
            } else if (var4 >= 135.0F && var4 < 225.0F) {
               PirateCaptainMob.this.midCannonRotation = var4 - 90.0F;
            } else if (var4 >= 225.0F && var4 < 315.0F) {
               PirateCaptainMob.this.leftCannonRotation = var4 - 90.0F;
            }

            int var5 = (int)PirateCaptainMob.this.getDistance((float)var1, (float)(var2 + 20));
            if (PirateCaptainMob.this.isServer()) {
               float var6 = Math.abs((float)PirateCaptainMob.this.getHealth() / (float)PirateCaptainMob.this.getMaxHealth() - 1.0F);
               int var7 = 125 + (int)(var6 * 100.0F);
               Projectile var8 = ProjectileRegistry.getProjectile("captaincannonball", PirateCaptainMob.this.getLevel(), (float)PirateCaptainMob.this.getX(), (float)(PirateCaptainMob.this.getY() - 20), (float)var1, (float)var2, (float)var7, var5, new GameDamage((float)PirateCaptainMob.this.shootDamage), 50, PirateCaptainMob.this);
               var8.isSolid = false;
               PirateCaptainMob.this.getLevel().entityManager.projectiles.add(var8);
            } else {
               Screen.playSound(GameResources.explosionLight, SoundEffect.effect(PirateCaptainMob.this).volume(2.0F).pitch(0.9F));

               for(int var9 = 0; var9 < 40; ++var9) {
                  PirateCaptainMob.this.getLevel().entityManager.addTopParticle((float)PirateCaptainMob.this.getX(), (float)(PirateCaptainMob.this.getY() - 20), Particle.GType.IMPORTANT_COSMETIC).movesConstant(PirateCaptainMob.this.dx / 4.0F + var3.x * 20.0F + GameRandom.globalRandom.floatGaussian() * 5.0F, PirateCaptainMob.this.dy / 4.0F + var3.y * 20.0F + GameRandom.globalRandom.floatGaussian() * 5.0F).smokeColor().lifeTime(1000);
               }
            }

         }
      });
   }

   public Point findEmptySpot(int var1) {
      AreaFinder var2 = new AreaFinder(this.getX() / 32, this.getY() / 32, var1, true) {
         public boolean checkPoint(int var1, int var2) {
            return PirateCaptainMob.this.getLevel().collides((Shape)(new Rectangle(PirateCaptainMob.this.getX() - 10, PirateCaptainMob.this.getY() - 7, 20, 14)), (CollisionFilter)(new CollisionFilter()).mobCollision());
         }
      };
      var2.tickFinder(var2.getRemainingTicks());
      if (var2.hasFound()) {
         Point var3 = var2.getFirstFind();
         return new Point(var3.x * 32 + 16, var3.y * 32 + 16);
      } else {
         return null;
      }
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addBoolean("dropLadder", this.dropLadder);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.dropLadder = var1.getBoolean("dropLadder", true);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.inCombat);
      var1.putNextBoolean(this.inSecondStage);
      var1.putNextBoolean(this.inSecondStageTransition);
      var1.putNextLong(this.secondStageTransitionStartTime);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.inCombat = var1.getNextBoolean();
      this.inSecondStage = var1.getNextBoolean();
      this.inSecondStageTransition = var1.getNextBoolean();
      if (this.inSecondStageTransition) {
         this.secondStageTransitionStartTime = var1.getNextLong();
      }

      this.updateStageVariables();
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

   public void init() {
      super.init();
   }

   public GameDamage getCollisionDamage(Mob var1) {
      return collisionDamage;
   }

   public boolean canCollisionHit(Mob var1) {
      return this.inSecondStage && super.canCollisionHit(var1);
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void setupAI() {
      if (this.baseTile == null || this.baseTile.x == 0 && this.baseTile.y == 0) {
         this.baseTile = new Point(this.getX() / 32, this.getY() / 32);
      }

      this.ai = new BehaviourTreeAI(this, new PirateCaptainAITree(this.shootDistance, 5000, 40, 640, 60000));
   }

   public boolean isInCombat() {
      return this.inCombat || super.isInCombat();
   }

   public void clientTick() {
      super.clientTick();
      if (this.inCombat) {
         Screen.setMusic(MusicRegistry.PirateCaptainsLair, Screen.MusicPriority.EVENT, 1.5F);
         Screen.registerMobHealthStatusBar(this);
         BossNearbyBuff.applyAround(this);
      }

      if (this.inSecondStage && !this.inSecondStageTransition) {
         if (this.secondStageWind == null || this.secondStageWind.isDone()) {
            this.secondStageWindEffect = SoundEffect.effect(this).volume(0.7F).falloffDistance(1400);
            this.secondStageWind = Screen.playSound(GameResources.wind, this.secondStageWindEffect);
         }

         if (this.secondStageWind != null) {
            this.secondStageWind.refreshLooping(1.0F);
            this.secondStageWindEffect.volume(Math.min(1.0F, this.getCurrentSpeed() / 100.0F) * 0.7F);
         }
      }

      if (this.inSecondStageTransition) {
         Client var1 = this.getLevel().getClient();
         if (this.secondStageCameraShake == null) {
            var1.startCameraShake(this, this.secondStageTransitionStartTime, secondStageTransitionTime + 500, 40, 3.0F, 3.0F, true);
            this.secondStageCameraShake = new CameraShake((long)secondStageTransitionTime, secondStageTransitionTime, 40, 1.5F, 1.5F, true);
         }

         if (this.secondStageRumble == null || this.secondStageRumble.isDone()) {
            this.secondStageRumble = Screen.playSound(GameResources.rumble, SoundEffect.effect(this).volume(1.0F).falloffDistance(1400));
         }

         if (this.secondStageRumble != null) {
            this.secondStageRumble.refreshLooping(1.0F);
         }

         this.getSecondStageTransitionProgress();

         for(int var2 = 0; var2 < 2; ++var2) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0F, this.y + GameRandom.globalRandom.floatGaussian() * 5.0F + 20.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0F, GameRandom.globalRandom.floatGaussian() * 3.0F).smokeColor().heightMoves(10.0F, GameRandom.globalRandom.getFloatBetween(30.0F, 40.0F)).lifeTime(1000);
         }
      } else {
         this.secondStageCameraShake = null;
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      if (this.inCombat) {
         BossNearbyBuff.applyAround(this);
      }

      this.getSecondStageTransitionProgress();
      if (this.inCombat && !this.inSecondStage && !this.isAttacking) {
         float var1 = (float)this.getHealth() / (float)this.getMaxHealth();
         if (var1 <= 0.9F) {
            this.startSecondStageAbility.runAndSend(this.getWorldEntity().getTime());
         }
      }

      if (!this.inCombat && this.inSecondStage && this.getHealth() == this.getMaxHealth()) {
         this.endSecondStageAbility.runAndSend();
      }

   }

   public void updateStageVariables() {
      if (this.inSecondStage) {
         this.setSpeed(140.0F);
         this.setFriction(0.5F);
         this.collision = new Rectangle(-20, -37, 40, 44);
         this.hitBox = new Rectangle(-25, -42, 50, 54);
         this.selectBox = new Rectangle(-30, -121, 60, 128);
         this.moveAccuracy = 50;
      } else {
         this.setSpeed(15.0F);
         this.setFriction(3.0F);
         this.collision = new Rectangle(-10, -7, 20, 14);
         this.hitBox = new Rectangle(-14, -12, 28, 24);
         this.selectBox = new Rectangle(-14, -41, 28, 48);
         this.moveAccuracy = 5;
      }

   }

   public CollisionFilter getLevelCollisionFilter() {
      return this.isFlying() ? null : super.getLevelCollisionFilter();
   }

   public int getFlyingHeight() {
      if (this.isRiding()) {
         return super.getFlyingHeight();
      } else if (this.inSecondStageTransition) {
         float var1 = this.getSecondStageTransitionProgress();
         return (int)(var1 * 50.0F);
      } else {
         return this.inSecondStage ? 40 : 0;
      }
   }

   public boolean canHitThroughCollision() {
      return this.isFlying();
   }

   public float getSecondStageTransitionProgress() {
      if (this.inSecondStageTransition) {
         long var1 = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime;
         if (var1 >= (long)secondStageTransitionTime) {
            this.inSecondStageTransition = false;
            return 1.0F;
         } else {
            return (float)var1 / (float)secondStageTransitionTime;
         }
      } else {
         return -1.0F;
      }
   }

   public boolean inLiquid(int var1, int var2) {
      return this.inSecondStage ? false : super.inLiquid(var1, var2);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      Point2D.Float var11 = this.secondStageCameraShake == null ? new Point2D.Float() : this.secondStageCameraShake.getCurrentShake(this.getWorldEntity().getTime());
      int var12 = var8.getDrawX((float)var5 + var11.x) - 32;
      int var13 = var8.getDrawY((float)var6 + var11.y) - 51;
      Point var14;
      if (this.inSecondStage) {
         this.dir = 2;
         var14 = new Point(0, 2);
      } else {
         var14 = this.getAnimSprite(var5, var6, this.dir);
      }

      var13 += this.getBobbing(var5, var6);
      HumanDrawOptions var15 = (new HumanDrawOptions(var4, this.getPirateTexture())).sprite(var14).dir(this.dir).light(var10);
      float var16;
      int var17;
      TextureDrawOptionsEnd var21;
      final TextureDrawOptionsEnd var23;
      TextureDrawOptionsEnd var24;
      if (this.inSecondStageTransition) {
         var16 = this.getSecondStageTransitionProgress();
         var17 = (int)(var16 * 220.0F) - 180;
         final DrawOptions var18 = var15.pos(var12, var13 - Math.max(var17, 0));
         GameTexture var19 = MobRegistry.Textures.pirateCaptainShip_mask;
         GameTexture var20 = MobRegistry.Textures.pirateCaptainShip;
         var13 -= var17;
         var21 = var20.initDraw().sprite(0, 0, 192).light(var10);
         final ShaderState var22 = GameResources.edgeMaskShader.setup(var21, (GameTexture)var19, 0, var17 + 70);
         var23 = var21.pos(var12 - 64, var13 - 64);
         var24 = var20.initDraw().sprite(0, 1, 192).light(var10);
         final ShaderState var25 = GameResources.edgeMaskShader.setup(var24, (GameTexture)var19, 0, var17 + 70);
         final TextureDrawOptionsEnd var26 = var24.pos(var12 - 64, var13 - 64);
         TextureDrawOptionsEnd var27 = var20.initDraw().sprite(0, 12, 32).mirrorX().light(var10);
         TextureDrawOptionsEnd var28 = var20.initDraw().sprite(1, 12, 32).light(var10);
         TextureDrawOptionsEnd var29 = var20.initDraw().sprite(0, 12, 32).light(var10);
         GameResources.edgeMaskShader.setup(var27, (GameTexture)var19, -62, var17 + 70 - 64 - 56);
         GameResources.edgeMaskShader.setup(var28, (GameTexture)var19, -80, var17 + 70 - 64 - 62);
         GameResources.edgeMaskShader.setup(var29, (GameTexture)var19, -98, var17 + 70 - 64 - 56);
         final TextureDrawOptionsEnd var30 = var27.pos(var12 - 2, var13 + 56);
         final TextureDrawOptionsEnd var31 = var28.pos(var12 + 16, var13 + 62);
         final TextureDrawOptionsEnd var32 = var29.pos(var12 + 34, var13 + 56);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               var22.use();

               try {
                  var23.draw();
               } finally {
                  var22.stop();
               }

               var18.draw();
               var25.use();

               try {
                  var26.draw();
                  var30.draw();
                  var31.draw();
                  var32.draw();
               } finally {
                  var25.stop();
               }

            }
         });
      } else if (this.inSecondStage) {
         var16 = GameMath.limit(this.dx / 10.0F, -15.0F, 15.0F);
         var17 = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 600);
         var13 -= 40;
         GameTexture var34 = MobRegistry.Textures.pirateCaptainShip;
         DrawOptions var36 = var15.pos(var12, var13);
         TextureDrawOptionsEnd var37 = var34.initDraw().sprite(var17, 0, 192).mirror(this.dx < 0.0F, false).rotate(var16, 96, 120).light(var10).pos(var12 - 64, var13 - 64);
         var21 = var34.initDraw().sprite(var17, 1, 192).mirror(this.dx < 0.0F, false).rotate(var16, 96, 120).light(var10).pos(var12 - 64, var13 - 64);
         TextureDrawOptionsEnd var38 = var34.initDraw().sprite(0, 12, 32).rotate(var16, 34, 0).addRotation(this.leftCannonRotation - var16 + 45.0F + 180.0F, 16, 16).mirrorX().light(var10).pos(var12 - 2, var13 + 56);
         var23 = var34.initDraw().sprite(1, 12, 32).rotate(var16, 16, -6).addRotation(this.midCannonRotation - var16 - 90.0F, 16, 14).light(var10).pos(var12 + 16, var13 + 62);
         var24 = var34.initDraw().sprite(0, 12, 32).rotate(var16, -2, 0).addRotation(this.rightCannonRotation - var16 - 45.0F, 14, 14).light(var10).pos(var12 + 34, var13 + 56);
         var3.add((var6x) -> {
            var37.draw();
            var36.draw();
            var21.draw();
            var38.draw();
            var23.draw();
            var24.draw();
         });
      } else {
         var13 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
         var16 = this.getAttackAnimProgress();
         if (this.isAttacking) {
            this.addAttackDraw(var15, var16);
         }

         if (this.inLiquid(var5, var6)) {
            var13 -= 10;
            var15.armSprite(2);
            var15.mask(MobRegistry.Textures.boat_mask[var14.y % 4], 0, -7);
         }

         final DrawOptions var33 = var15.pos(var12, var13);
         final TextureDrawOptionsEnd var35 = this.inLiquid(var5, var6) ? MobRegistry.Textures.steelBoat.initDraw().sprite(0, this.dir % 4, 64).light(var10).pos(var12, var13 + 7) : null;
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               if (var35 != null) {
                  var35.draw();
               }

               var33.draw();
            }
         });
      }

      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 16;
      int var5 = var3 - 26;
      Point var6;
      if (this.inSecondStage) {
         this.dir = 2;
         var6 = new Point(0, 2);
      } else {
         var6 = this.getAnimSprite(this.getDrawX(), this.getDrawY(), this.dir);
      }

      HumanDrawOptions var7 = (new HumanDrawOptions(this.getLevel(), this.getPirateTexture())).size(32, 32).sprite(var6).dir(this.dir);
      if (this.inSecondStage) {
         var5 -= 20;
         int var8 = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 600);
         GameTexture var9 = MobRegistry.Textures.pirateCaptainShip;
         var9.initDraw().sprite(var8, 0, 192).size(96, 96).draw(var4 - 32, var5 - 32);
         var7.draw(var4, var5);
         var9.initDraw().sprite(var8, 1, 192).size(96, 96).draw(var4 - 32, var5 - 32);
         var9.initDraw().sprite(0, 12, 32).size(16, 16).mirrorX().draw(var4 - 1, var5 + 28);
         var9.initDraw().sprite(1, 12, 32).size(16, 16).draw(var4 + 8, var5 + 31);
         var9.initDraw().sprite(0, 12, 32).size(16, 16).draw(var4 + 17, var5 + 28);
      } else {
         var7.draw(var4, var5);
      }

   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      if (this.inSecondStage) {
         GameTexture var5 = MobRegistry.Textures.pirateCaptainShip_shadow;
         int var6 = var4.getDrawX(var1) - var5.getWidth() / 2;
         int var7 = var4.getDrawY(var2) - var5.getHeight() / 2;
         var7 += this.getBobbing(var1, var2);
         return var5.initDraw().light(var3).pos(var6, var7);
      } else {
         return super.getShadowDrawOptions(var1, var2, var3, var4);
      }
   }

   protected HumanTexture getPirateTexture() {
      return MobRegistry.Textures.pirateCaptain;
   }

   public Rectangle drawOnMapBox() {
      return this.inSecondStage ? new Rectangle(-16, -60, 32, 60) : new Rectangle(-8, -22, 16, 25);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public LootTable getLootTable() {
      return this.dropLadder ? new LootTable(new LootItemInterface[]{lootTable, new LootItem("deepladderdown")}) : lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   protected void addRangedAttackDraw(HumanDrawOptions var1, float var2) {
      var1.itemAttack(new InventoryItem("handcannon"), (PlayerMob)null, var2, this.attackDir.x, this.attackDir.y);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F));
   }

   public void shootAbilityProjectile(int var1, int var2) {
      if (this.isServer()) {
         GameRandom var3 = new GameRandom((long)(var1 + var2));
         float var4 = this.getDistance((float)var1, (float)var2) / (float)this.shootDistance;
         int var5 = (int)((float)var1 + (var3.nextFloat() * 2.0F - 1.0F) * 30.0F * var4);
         int var6 = (int)((float)var2 + (var3.nextFloat() * 2.0F - 1.0F) * 30.0F * var4);
         int var7 = (int)this.getDistance((float)var5, (float)var6);
         Projectile var8 = ProjectileRegistry.getProjectile("captaincannonball", this.getLevel(), this.x, this.y, (float)var5, (float)var6, 150.0F, var7, new GameDamage((float)this.shootDamage), 50, this);
         var8.isSolid = true;
         var8.resetUniqueID(var3);
         this.getLevel().entityManager.projectiles.add(var8);
      }

      this.showAttack(var1, var2, false);
      if (this.isClient()) {
         Screen.playSound(GameResources.explosionLight, SoundEffect.effect(this).volume(2.0F).pitch(0.9F));
      }

   }

   public int getRespawnTime() {
      return BossMob.getBossRespawnTime(this);
   }

   public void spawnDeathParticles(float var1, float var2) {
      if (this.inSecondStage) {
         for(int var3 = 0; var3 < 7; ++var3) {
            this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.pirateCaptainShip, var3, 12, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
         }
      }

   }

   protected void playDeathSound() {
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var1x) -> {
         ServerClient var2 = ((PlayerMob)var1x).getServerClient();
         var2.sendChatMessage((GameMessage)(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
         if (var2.achievementsLoaded()) {
            var2.achievements().DEFEAT_PIRATE.markCompleted(var2);
         }

      });
      PirateHumanMob var3 = (PirateHumanMob)MobRegistry.getMob("piratehuman", this.getLevel());
      var3.setTrapped(true);
      var3.dir = this.dir;
      Point var4 = this.findEmptySpot(4);
      if (var4 != null) {
         float var10002 = (float)var4.x;
         this.getLevel().entityManager.addMob(var3, var10002, (float)var4.y);
      } else {
         this.getLevel().entityManager.addMob(var3, this.x, this.y);
      }

   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("piratecap", 4);
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new ConditionLootItem("piratesheath", (var0, var1) -> {
         ServerClient var2 = (ServerClient)LootTable.expectExtra(ServerClient.class, var1, 1);
         return var2 != null && var2.playerMob.getInv().trinkets.getSize() < 6 && var2.playerMob.getInv().getAmount(ItemRegistry.getItem("piratesheath"), false, true, true, "have") == 0;
      }), uniqueDrops});
      pirateMobs = (new MobSpawnTable()).add(100, (String)"piraterecruit").add(50, (String)"pirateparrot");
      MAX_HEALTH = new MaxHealthGetter(4500, 6000, 7000, 8000, 10000);
      secondStageTransitionTime = 5000;
      captainMeleeDamage = 45;
      captainCannonDamage = 100;
      collisionDamage = new GameDamage(50.0F);
   }

   public static class PirateCaptainAITree<T extends PirateCaptainMob> extends PirateAITree<T> {
      public String chaserTargetKey = "chaserTarget";
      public String currentTargetKey = "currentTarget";
      private ArrayList<Mob> spawnedMobs = new ArrayList();
      private long musicCooldown;
      private float minionSpawnBuffer;
      private int dropCombatCounter;

      public PirateCaptainAITree(int var1, int var2, int var3, int var4, int var5) {
         super(var1, var2, var3, var4, var5);
         this.addChildFirst(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
               var3.onEvent("refreshBossDespawn", (var1x) -> {
                  PirateCaptainAITree.this.dropCombatCounter = 0;
               });
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               Mob var3 = (Mob)var2.getObject(Mob.class, PirateCaptainAITree.this.chaserTargetKey);
               int var4;
               Mob var5;
               if (var3 != null) {
                  PirateCaptainAITree.this.dropCombatCounter = 0;

                  for(var4 = 0; var4 < PirateCaptainAITree.this.spawnedMobs.size(); ++var4) {
                     var5 = (Mob)PirateCaptainAITree.this.spawnedMobs.get(var4);
                     if (var5.removed()) {
                        PirateCaptainAITree.this.spawnedMobs.remove(var4);
                        --var4;
                     }
                  }

                  long var10 = GameUtils.streamServerClients(var1.getLevel()).filter((var1x) -> {
                     return !var1x.isDead() && var1.getDistance(var1x.playerMob) < 1280.0F;
                  }).count();
                  float var6 = Math.min(1.0F + (float)(var10 - 1L) / 2.0F, 5.0F);
                  PirateCaptainAITree.this.minionSpawnBuffer = 0.0125F * var6;
                  if (PirateCaptainAITree.this.minionSpawnBuffer >= 1.0F) {
                     PirateCaptainAITree.this.minionSpawnBuffer--;
                     if ((float)PirateCaptainAITree.this.spawnedMobs.size() < 5.0F * var6) {
                        Point var7 = EntityManager.getMobSpawnTile(var1.getLevel(), var3.getX(), var3.getY(), Mob.MOB_SPAWN_AREA, (Function)null);
                        if (var7 != null) {
                           Mob var8 = PirateCaptainAITree.this.getRandomSpawnMob(var1, var7);
                           if (var8 instanceof PirateMob) {
                              ((PirateMob)var8).setSummoned();
                              Point var9 = var8.getPathMoveOffset();
                              if (var8.isValidSpawnLocation(var1.getLevel().getServer(), (ServerClient)null, var7.x * 32 + var9.x, var7.y * 32 + var9.y)) {
                                 var8.setPos((float)(var7.x * 32 + var9.x), (float)(var7.y * 32 + var9.y), true);
                                 ((PirateMob)var8).baseTile = new Point(var1.baseTile);
                                 var1.getLevel().entityManager.mobs.add(var8);
                                 PirateCaptainAITree.this.spawnedMobs.add(var8);
                                 var8.ai.blackboard.put(PirateCaptainAITree.this.currentTargetKey, var3);
                              }
                           }
                        }
                     }
                  }

                  if (PirateCaptainAITree.this.musicCooldown < var1.getWorldEntity().getTime()) {
                     PirateCaptainAITree.this.musicCooldown = var1.getWorldEntity().getTime() + 5000L;
                     var1.setInCombatAbility.runAndSend(true);
                  }
               } else if (var1.inCombat) {
                  PirateCaptainAITree.this.dropCombatCounter++;
                  if (PirateCaptainAITree.this.dropCombatCounter > 60) {
                     PirateCaptainAITree.this.dropCombatCounter = 0;

                     for(var4 = 0; var4 < PirateCaptainAITree.this.spawnedMobs.size(); ++var4) {
                        var5 = (Mob)PirateCaptainAITree.this.spawnedMobs.get(var4);
                        var5.remove();
                        PirateCaptainAITree.this.spawnedMobs.remove(var4);
                        --var4;
                     }

                     if (PirateCaptainAITree.this.musicCooldown != 0L) {
                        PirateCaptainAITree.this.musicCooldown = 0L;
                        var1.setInCombatAbility.runAndSend(false);
                     }
                  }
               }

               return AINodeResult.FAILURE;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((PirateCaptainMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((PirateCaptainMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (PirateCaptainMob)var2, var3);
            }
         });
         this.addChildFirst(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               if (var1.inSecondStageTransition) {
                  var2.mover.stopMoving(var1);
                  return AINodeResult.SUCCESS;
               } else {
                  return AINodeResult.FAILURE;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((PirateCaptainMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((PirateCaptainMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (PirateCaptainMob)var2, var3);
            }
         });
      }

      protected AINode<T> getChaserNode(int var1, int var2, int var3, int var4) {
         SequenceAINode var5 = new SequenceAINode();
         TargetFinderDistance var6 = new TargetFinderDistance(var4);
         var6.targetLostAddedDistance = var4 * 5;
         var5.addChild(new TargetFinderAINode<T>(var6) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((PirateCaptainMob)var1, var2, var3);
            }
         });
         var5.addChild(new PirateCaptainSecondStageChaserAINode());
         SelectorAINode var7 = new SelectorAINode();
         var7.addChild(new ConditionAINode(var5, (var0) -> {
            return var0.inSecondStage;
         }, AINodeResult.FAILURE));
         var7.addChild(super.getChaserNode(var1, var2, var3, var4));
         return var7;
      }

      private Mob getRandomSpawnMob(T var1, Point var2) {
         return PirateCaptainMob.pirateMobs.getRandomMob(var1.getLevel(), (ServerClient)null, var2, GameRandom.globalRandom).getMob(var1.getLevel(), (ServerClient)null, var2);
      }
   }

   public static class PirateCaptainSecondStageChaserAINode<T extends PirateCaptainMob> extends AINode<T> {
      public String chaserTargetKey = "currentTarget";
      public long nextCannonShot = 0L;

      public PirateCaptainSecondStageChaserAINode() {
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, this.chaserTargetKey);
         if (var3 == null) {
            if (var2.mover.isCurrentlyMovingFor(this)) {
               var2.mover.stopMoving(var1);
            }

            return AINodeResult.FAILURE;
         } else {
            if (!var2.mover.isCurrentlyMovingFor(this) || var1.hasArrivedAtTarget()) {
               int var4 = GameRandom.globalRandom.nextInt(360);
               Point2D.Float var5 = GameMath.getAngleDir((float)var4);
               var2.mover.setCustomMovement(this, new MobMovementLevelPos(var3.x + var5.x * 200.0F, var3.y + var5.y * 200.0F));
            }

            if (this.nextCannonShot <= var1.getWorldEntity().getTime()) {
               float var12 = (float)var1.getHealth() / (float)var1.getMaxHealth();
               int var13 = 1500 + (int)(var12 * 4000.0F);
               this.nextCannonShot = var1.getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(var13, var13 + 1000);
               LinkedList var6 = new LinkedList();
               List var7 = (List)GameUtils.streamServerClients(var1.getLevel()).map((var0) -> {
                  return var0.playerMob;
               }).filter((var1x) -> {
                  float var2 = var1x.getDistance(var1);
                  return var2 > 80.0F && var2 <= (float)var1.shootDistance;
               }).collect(Collectors.toList());
               Collections.shuffle(var7);
               Iterator var8 = var7.iterator();

               while(var8.hasNext()) {
                  PlayerMob var9 = (PlayerMob)var8.next();
                  float var10 = Projectile.getAngleToTarget(var1.x, var1.y, var9.x, var9.y);
                  if (var6.stream().noneMatch((var1x) -> {
                     return Math.abs(GameMath.getAngleDifference(var10, var1x)) < 60.0F;
                  })) {
                     byte var11 = 35;
                     var1.fireCannonAbility.runAndSend(var9.getX() + GameRandom.globalRandom.getIntBetween(-var11, var11), var9.getY() + GameRandom.globalRandom.getIntBetween(-var11, var11));
                     var6.add(var10);
                  }
               }
            }

            return AINodeResult.SUCCESS;
         }
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((PirateCaptainMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((PirateCaptainMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (PirateCaptainMob)var2, var3);
      }
   }
}
