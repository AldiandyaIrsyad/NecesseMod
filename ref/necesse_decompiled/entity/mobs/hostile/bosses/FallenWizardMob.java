package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketMobAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.levelEvent.FallenWizardRespawnEvent;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FallenWizardBeamLevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.RotationAttackStageNode;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.FallenWizardBallProjectile;
import necesse.entity.projectile.FallenWizardWaveProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.FallenWizardScepterProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.temple.TempleArenaLevel;
import necesse.level.maps.light.GameLight;

public class FallenWizardMob extends BossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemInterface() {
      public void addPossibleLoot(LootList var1, Object... var2) {
         var1.add("gatewaytablet");
      }

      public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
         InventoryItem var5 = new InventoryItem("gatewaytablet");
         GatewayTabletItem.initializeGatewayTablet(var5, var2, 1);
         var1.add(var5);
      }
   }, new ChanceLootItem(0.2F, "wizardsrematchvinyl")});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation();
   public static LootTable privateLootTable;
   public static MaxHealthGetter MAX_HEALTH;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   protected LinkedList<Projectile> projectiles = new LinkedList();
   public boolean isVisible;
   public long visibilityFadeTime;
   public boolean spawnParticles;
   public final FallenTeleportAbility teleportAbility;
   public final FallenSetVisibilityAbility visibilityAbility;
   public final BooleanMobAbility changeHostile;
   public final EmptyMobAbility laserBlastSound;
   public final EmptyMobAbility magicBoltSound;
   public final CoordinateMobAbility spawnDragonEffects;
   public static GameDamage laserDamage;
   public static GameDamage scepterDamage;
   public static GameDamage waveDamage;
   public static GameDamage ballDamage;
   public static GameDamage dragonHeadDamage;
   public static GameDamage dragonBodyDamage;

   public FallenWizardMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.setSpeed(60.0F);
      this.setFriction(3.0F);
      this.setArmor(40);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-26, -24, 52, 48);
      this.selectBox = new Rectangle(-19, -52, 38, 64);
      this.dir = 2;
      this.shouldSave = true;
      this.isHostile = false;
      this.isVisible = true;
      this.teleportAbility = (FallenTeleportAbility)this.registerAbility(new FallenTeleportAbility());
      this.visibilityAbility = (FallenSetVisibilityAbility)this.registerAbility(new FallenSetVisibilityAbility());
      this.changeHostile = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            if (!FallenWizardMob.this.isHostile && var1) {
               if (FallenWizardMob.this.isClient()) {
                  Screen.playSound(GameResources.magicroar, SoundEffect.globalEffect());
               }
            } else if (FallenWizardMob.this.isHostile && !var1) {
               FallenWizardMob.this.dir = 2;
               FallenWizardMob.this.isVisible = true;
               FallenWizardMob.this.stopMoving();
               FallenWizardMob.this.resetAI();
            }

            FallenWizardMob.this.isHostile = var1;
         }
      });
      this.laserBlastSound = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (FallenWizardMob.this.isClient()) {
               Screen.playSound(GameResources.laserBlast2, SoundEffect.effect(FallenWizardMob.this).falloffDistance(1250).volume(1.2F).pitch((Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F))));
            }

         }
      });
      this.magicBoltSound = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (FallenWizardMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(FallenWizardMob.this).falloffDistance(1250).volume(1.2F).pitch((Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F))));
            }

         }
      });
      this.spawnDragonEffects = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            if (FallenWizardMob.this.isClient()) {
               FallenWizardMob.this.getLevel().entityManager.addParticle(new PortalParticle(FallenWizardMob.this.getLevel(), var1, var2, 1000L), true, Particle.GType.CRITICAL);
               Screen.playSound(GameResources.magicbolt4, SoundEffect.effect(FallenWizardMob.this).falloffDistance(1250).volume(1.5F));
            }

         }
      });
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.spawnParticles);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.spawnParticles = var1.getNextBoolean();
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isHostile);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isHostile = var1.getNextBoolean();
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
      this.resetAI();
      this.dir = 2;
      if (this.spawnParticles && !this.isServer()) {
         this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), (float)this.getX(), (float)(this.getY() + 2), 96, new Color(67, 6, 127))), Particle.GType.CRITICAL);
      }

      if (this.isServer() && this.isInArena()) {
         Point2D.Float var1 = TempleArenaLevel.getBossPosition();
         this.setPos(var1.x, var1.y, true);
         this.setHealth(this.getMaxHealth());
      }

   }

   protected void resetAI() {
      this.ai = new BehaviourTreeAI(this, new FallenWizardAI());
   }

   public boolean isInArena() {
      return this.getLevel().biome == BiomeRegistry.TEMPLE && this.getLevel().getIslandDimension() == -201;
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public void addProjectile(Projectile var1) {
      this.projectiles.add(var1);
   }

   public void clearProjectiles() {
      this.projectiles.forEach(Projectile::remove);
      this.projectiles.clear();
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void clientTick() {
      super.clientTick();
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(60.0F + 30.0F * var1);
      if (this.isHostile) {
         Screen.setMusic(MusicRegistry.WizardsRematch, Screen.MusicPriority.EVENT, 1.5F);
         Color var2 = VoidWizard.getWizardShade(this);
         Screen.setSceneShade(var2);
         Screen.registerMobHealthStatusBar(this);
         BossNearbyBuff.applyAround(this);
      }

      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 260.0F, 0.5F);
      this.spawnParticles = false;
   }

   public void serverTick() {
      super.serverTick();
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(60.0F + 30.0F * var1);
      this.scaling.serverTick();
      if (this.isHostile) {
         BossNearbyBuff.applyAround(this);
      }

      this.spawnParticles = false;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   public boolean canTakeDamage() {
      return super.canTakeDamage() && this.isVisible();
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 50; ++var3) {
         int var4 = GameRandom.globalRandom.getIntBetween(500, 5000);
         float var5 = (float)var4 / 5000.0F;
         float var6 = 10.0F;
         float var7 = var6 + (float)GameRandom.globalRandom.getIntBetween(70, 150) * var5;
         this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-20.0F, 20.0F), this.y + GameRandom.globalRandom.getFloatBetween(-10.0F, 10.0F), Particle.GType.IMPORTANT_COSMETIC).sizeFades(20, 30).movesFriction(GameRandom.globalRandom.getFloatBetween(-40.0F, 40.0F), GameRandom.globalRandom.getFloatBetween(-20.0F, 20.0F), 0.5F).heightMoves(var6, var7).colorRandom(270.0F, 0.8F, 0.5F, 10.0F, 0.1F, 0.1F).lifeTime(var4);
      }

   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this).falloffDistance(2000).pitch(0.6F));
   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      long var10 = var4.getWorldEntity().getLocalTime() - this.visibilityFadeTime;
      if (this.isVisible() || var10 <= 1000L) {
         GameLight var12 = var4.getLightLevel(var5 / 32, var6 / 32);
         int var13 = var8.getDrawX(var5) - 48;
         int var14 = var8.getDrawY(var6) - 75;
         Point var15 = this.getAnimSprite(var5, var6, this.dir);
         var14 += this.getBobbing(var5, var6);
         var14 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
         HumanDrawOptions var16 = (new HumanDrawOptions(var4, MobRegistry.Textures.fallenWizard)).sprite(var15, 96).size(96, 96).dir(this.dir).light(var12);
         float var17;
         if (var10 < 1000L) {
            var17 = Math.max(0.0F, (float)var10 / 1000.0F);
            if (!this.isVisible()) {
               var17 = Math.abs(var17 - 1.0F);
            }

            var16.allAlpha(var17);
         }

         var17 = this.getAttackAnimProgress();
         if (this.isAttacking) {
            ItemAttackDrawOptions var18 = ItemAttackDrawOptions.start(this.dir).itemSprite(MobRegistry.Textures.fallenWizard.body, 0, 9, 48).itemRotatePoint(14, 4).itemEnd().armRotatePoint(16, 25).offsets(48, 34, 18, 5, 20).armSprite(MobRegistry.Textures.fallenWizard.body, 0, 8, 48).light(var12);
            var18.pointRotation(this.attackDir.x, this.attackDir.y).forEachItemSprite((var0) -> {
               var0.itemRotateOffset(45.0F);
            });
            var16.attackAnim(var18, var17);
         }

         final DrawOptions var19 = var16.pos(var13, var14);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               var19.draw();
            }
         });
         this.addShadowDrawables(var2, var5, var6, var12, var8);
      }
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
      return this.isVisible();
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
      (new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.fallenWizard)).sprite(var6, 96).dir(this.dir).size(48, 48).draw(var4, var5);
   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), new ModifierValue(BuffModifiers.ATTACK_MOVEMENT_MOD, 0.0F));
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var1x) -> {
         ServerClient var2 = ((PlayerMob)var1x).getServerClient();
         var2.sendChatMessage((GameMessage)(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())));
         if (var2.achievementsLoaded()) {
            var2.achievements().REMATCH.markCompleted(var2);
         }

      });
      if (this.isInArena() && this.getLevel().entityManager.getLevelEvents().stream().noneMatch((var0) -> {
         return var0 instanceof FallenWizardRespawnEvent;
      })) {
         Point2D.Float var3 = TempleArenaLevel.getBossPosition();
         long var4 = 600000L;
         this.getLevel().entityManager.addLevelEvent(new FallenWizardRespawnEvent(var3.x, var3.y, this.getLevel().getWorldEntity().getWorldTime() + var4));
      }

   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new ConditionLootItem("wizardsocket", (var0, var1) -> {
         ServerClient var2 = (ServerClient)LootTable.expectExtra(ServerClient.class, var1, 1);
         return var2 != null && var2.playerMob.getInv().trinkets.getSize() < 7 && var2.playerMob.getInv().getAmount(ItemRegistry.getItem("wizardsocket"), false, true, true, "have") == 0;
      }), uniqueDrops});
      MAX_HEALTH = new MaxHealthGetter(15000, 24000, 30000, 34000, 40000);
      laserDamage = new GameDamage(110.0F);
      scepterDamage = new GameDamage(70.0F);
      waveDamage = new GameDamage(85.0F);
      ballDamage = new GameDamage(75.0F);
      dragonHeadDamage = new GameDamage(110.0F);
      dragonBodyDamage = new GameDamage(80.0F);
   }

   public class FallenTeleportAbility extends MobAbility {
      public FallenTeleportAbility() {
      }

      public void runAndSend(int var1, int var2, int var3, boolean var4, boolean var5, boolean var6) {
         Packet var7 = new Packet();
         PacketWriter var8 = new PacketWriter(var7);
         var8.putNextInt(var1);
         var8.putNextInt(var2);
         var8.putNextInt(var3);
         var8.putNextBoolean(var4);
         var8.putNextBoolean(var5);
         var8.putNextBoolean(var6);
         this.runAndSendAbility(var7);
      }

      public void executePacket(PacketReader var1) {
         int var2 = var1.getNextInt();
         int var3 = var1.getNextInt();
         int var4 = var1.getNextInt();
         boolean var5 = var1.getNextBoolean();
         boolean var6 = var1.getNextBoolean();
         boolean var7 = var1.getNextBoolean();
         if (var6 && FallenWizardMob.this.isClient()) {
            if (var5) {
               FallenWizardMob.this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(FallenWizardMob.this.getLevel(), (float)FallenWizardMob.this.getX(), (float)FallenWizardMob.this.getY(), 96, new Color(67, 6, 127))), Particle.GType.CRITICAL);
            } else {
               FallenWizardMob.this.getLevel().entityManager.addParticle((Particle)(new TeleportParticle(FallenWizardMob.this.getLevel(), FallenWizardMob.this.getX(), FallenWizardMob.this.getY(), FallenWizardMob.this)), Particle.GType.CRITICAL);
               FallenWizardMob.this.getLevel().lightManager.refreshParticleLightFloat((float)var2, (float)var3, 270.0F, 0.5F);
            }
         }

         FallenWizardMob.this.dir = var4;
         FallenWizardMob.this.setPos((float)var2, (float)var3, true);
         if (var7 && FallenWizardMob.this.isClient()) {
            if (var5) {
               FallenWizardMob.this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(FallenWizardMob.this.getLevel(), (float)FallenWizardMob.this.getX(), (float)(FallenWizardMob.this.getY() + 2), 96, new Color(67, 6, 127))), Particle.GType.CRITICAL);
            } else {
               FallenWizardMob.this.getLevel().entityManager.addParticle((Particle)(new TeleportParticle(FallenWizardMob.this.getLevel(), FallenWizardMob.this.getX(), FallenWizardMob.this.getY() + 2, FallenWizardMob.this)), Particle.GType.CRITICAL);
            }
         }

      }
   }

   public class FallenSetVisibilityAbility extends MobAbility {
      public FallenSetVisibilityAbility() {
      }

      public void runAndSend(boolean var1, boolean var2) {
         Packet var3 = new Packet();
         PacketWriter var4 = new PacketWriter(var3);
         var4.putNextBoolean(var1);
         var4.putNextBoolean(var2);
         this.runAndSendAbility(var3);
      }

      public void executePacket(PacketReader var1) {
         FallenWizardMob.this.isVisible = var1.getNextBoolean();
         if (var1.getNextBoolean()) {
            FallenWizardMob.this.visibilityFadeTime = FallenWizardMob.this.getWorldEntity().getLocalTime();
         }

      }
   }

   public static class FallenWizardAI<T extends FallenWizardMob> extends SequenceAINode<T> {
      private int inActiveTimer;

      public FallenWizardAI() {
         this.addChild(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               if (!var1.isHostile) {
                  var2.mover.stopMoving(var1);
                  return AINodeResult.FAILURE;
               } else {
                  return AINodeResult.SUCCESS;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((FallenWizardMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((FallenWizardMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (FallenWizardMob)var2, var3);
            }
         });
         this.addChild(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
               if (var3 == null) {
                  FallenWizardAI.this.inActiveTimer++;
                  if (FallenWizardAI.this.inActiveTimer > 100) {
                     if (var1.isInArena()) {
                        Point2D.Float var4 = TempleArenaLevel.getBossPosition();
                        var1.teleportAbility.runAndSend((int)var4.x, (int)var4.y, 2, false, true, false);
                     }

                     var1.changeHostile.runAndSend(false);
                     FallenWizardAI.this.inActiveTimer = 0;
                     var1.clearProjectiles();
                     var1.setHealth(var1.getMaxHealth());
                     return AINodeResult.FAILURE;
                  }
               } else {
                  FallenWizardAI.this.inActiveTimer = 0;
               }

               return AINodeResult.SUCCESS;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((FallenWizardMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((FallenWizardMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (FallenWizardMob)var2, var3);
            }
         });
         this.addChild(new TargetFinderAINode<T>(3200) {
            public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
               return TargetFinderAINode.streamPlayers(var1, var2, var3);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
               return this.streamPossibleTargets((FallenWizardMob)var1, var2, var3);
            }
         });
         AttackStageManagerNode var1 = new AttackStageManagerNode();
         var1.allowSkippingBack = true;
         this.addChild(new IsolateRunningAINode(var1));
         var1.addChild(new FindNewPosStage(true));
         var1.addChild(new IdleTimeAttackStage(0, 200));
         var1.addChild(new RotationAttackStageNode(new AINode[]{new DirectionWaveProjectilesStage(0, 250, 200, 400, 60.0F, 5), new RandomWaveProjectilesStage(0, 250, 400, 600, 4)}));
         var1.addChild(new GhostFindNewPosStage());
         var1.addChild(new IdleTimeAttackStage(100, 500));
         var1.addChild(new BeamSweepStage(1500, 3000, 110.0F, 270.0F));
         var1.addChild(new FindNewPosStage(true));
         var1.addChild(new IdleTimeAttackStage(0, 100));
         var1.addChild(new BulletHellStage(250, -35.0F, 100.0F, 4, 1000, 2000, 7.5F));
         var1.addChild(new FindNewPosStage(true));
         var1.addChild(new IdleTimeAttackStage(0, 100));
         var1.addChild(new DragonSpawnStage(0, 250, 500, 1000, 2));
         var1.addChild(new FindNewPosStage(true));
         var1.addChild(new IdleTimeAttackStage(0, 100));
         var1.addChild(new ScepterProjectilesStage(25, 2250, 2750));
      }

      public PriorityMap<Point> findNewPositions(Mob var1, Blackboard<T> var2) {
         Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
         Point2D.Float var4 = new Point2D.Float(var1.x, var1.y);
         if (var3 != null) {
            var4 = new Point2D.Float(var3.x, var3.y);
         }

         byte var5 = 8;
         byte var6 = 3;
         int var7 = var1.getX() / 32;
         int var8 = var1.getY() / 32;
         int var9 = (int)Math.signum(var4.x / 32.0F - (float)var7) * var5 / 2;
         int var10 = (int)Math.signum(var4.y / 32.0F - (float)var8) * var5 / 2;
         PriorityMap var11 = new PriorityMap();

         for(int var12 = -var5; var12 <= var5; ++var12) {
            if (var12 <= -var6 || var12 >= var6) {
               for(int var13 = -var5; var13 <= var5; ++var13) {
                  if (var13 <= -var6 || var13 >= var6) {
                     Point var14 = new Point(var7 + var9 + var12, var8 + var10 + var13);
                     if (!var1.getLevel().isSolidTile(var14.x, var14.y)) {
                        int var15 = var1.getLevel().liquidManager.getHeight(var14.x, var14.y);
                        if (var15 >= 8) {
                           var11.add(10, var14);
                        } else {
                           var11.add(var15 / 2, var14);
                        }
                     }
                  }
               }
            }
         }

         return var11;
      }

      public Point findNewPosition(Mob var1, Blackboard<T> var2) {
         return (Point)this.findNewPositions(var1, var2).getRandomBestObject(GameRandom.globalRandom, 20);
      }

      public void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         super.onRootSet(var1, var2, var3);
         var3.onWasHit((var2x) -> {
            this.makeHostile(var2);
         });
      }

      public void makeHostile(T var1) {
         if (!var1.isHostile) {
            if (var1.isServer()) {
               var1.getLevel().getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", var1.getLocalization())), (Level)var1.getLevel());
            }

            var1.changeHostile.runAndSend(true);
         }

      }

      public int getHealthPercValue(T var1, int var2, int var3) {
         int var4 = var3 - var2;
         float var5 = (float)var1.getHealth() / (float)var1.getMaxHealth();
         return var2 + (int)((float)var4 * var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (FallenWizardMob)var2, var3);
      }

      protected class FindNewPosStage extends MoveTaskAINode<T> implements AttackStageInterface<T> {
         public boolean waitForArrive;
         private boolean hasStartedMoving;

         public FindNewPosStage(boolean var2) {
            this.waitForArrive = var2;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tickNode(T var1, Blackboard<T> var2) {
            if (!this.hasStartedMoving) {
               this.hasStartedMoving = true;
               Point var3 = FallenWizardAI.this.findNewPosition(var1, var2);
               if (var3 != null) {
                  return this.moveToTileTask(var3.x, var3.y, (BiPredicate)null, (var0) -> {
                     var0.move((Runnable)null);
                     return null;
                  });
               }
            }

            return this.waitForArrive && var2.mover.isCurrentlyMovingFor(this) && var2.mover.isMoving() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.hasStartedMoving = false;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tickNode(Mob var1, Blackboard var2) {
            return this.tickNode((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }

      protected class DirectionWaveProjectilesStage extends AINode<T> implements AttackStageInterface<T> {
         private Point2D.Float direction;
         private int projectilesFired;
         private long lastProjectileTime;
         private long nextShowAttackTime;
         private boolean reversed;
         private int currentTimeGap;
         public float gapSize;
         public int noHealthStartDelay;
         public int fullHealthStartDelay;
         public int noHealthTimeGap;
         public int fullHealthTimeGap;
         public int waves;

         public DirectionWaveProjectilesStage(int var2, int var3, int var4, int var5, float var6, int var7) {
            this.noHealthStartDelay = var2;
            this.fullHealthStartDelay = var3;
            this.noHealthTimeGap = var4;
            this.fullHealthTimeGap = var5;
            this.gapSize = var6;
            this.waves = var7;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            long var3 = var1.getWorldEntity().getTime();
            if (this.nextShowAttackTime <= var3) {
               var1.showAttack((int)(var1.x + this.direction.x * 100.0F), (int)(var1.y + this.direction.y * 100.0F), false);
               this.nextShowAttackTime = var3 + 100L;
               if (var1.isServer()) {
                  var1.getLevel().getServer().network.sendToClientsAt(new PacketMobAttack(var1, (int)(var1.x + this.direction.x * 100.0F), (int)(var1.y + this.direction.y * 100.0F)), (Level)var1.getLevel());
               }
            }

            long var5 = var3 - this.lastProjectileTime;

            while(this.projectilesFired < this.waves && var5 >= (long)this.currentTimeGap) {
               this.lastProjectileTime += (long)this.currentTimeGap;
               var5 = var3 - this.lastProjectileTime;
               Point2D.Float var7 = GameMath.getPerpendicularDir(this.direction.x, this.direction.y);
               if (this.reversed) {
                  var7 = new Point2D.Float(-var7.x, -var7.y);
               }

               float var8 = 200.0F;
               float var9 = (var8 + this.gapSize) * (float)this.waves - this.gapSize;
               float var10 = (var8 + this.gapSize) * (float)this.projectilesFired + var8 / 2.0F;
               float var11 = 500.0F;
               float var12 = var1.x + var7.x * var9 / 2.0F - var7.x * var10 - this.direction.x * var11;
               float var13 = var1.y + var7.y * var9 / 2.0F - var7.y * var10 - this.direction.y * var11;
               short var14 = 2000;
               var1.getLevel().entityManager.projectiles.add(new FallenWizardWaveProjectile(var12, var13, var12 + this.direction.x * 100.0F, var13 + this.direction.y * 100.0F, 120.0F, var14, FallenWizardMob.waveDamage, 50, var1));
               ++this.projectilesFired;
               var1.magicBoltSound.runAndSend();
            }

            return this.projectilesFired < this.waves ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
            Point2D.Float var4;
            if (var3 != null) {
               var4 = new Point2D.Float(var3.x, var3.y);
            } else if (var1.isInArena()) {
               var4 = TempleArenaLevel.getBossPosition();
            } else {
               var4 = new Point2D.Float(var1.x, var1.y + 100.0F);
            }

            this.direction = GameMath.normalize(var4.x - var1.x, var4.y - var1.y);
            if (this.direction.x == 0.0F && this.direction.y == 0.0F) {
               this.direction = new Point2D.Float(0.0F, 1.0F);
            }

            this.reversed = !this.reversed;
            this.nextShowAttackTime = 0L;
            this.projectilesFired = 0;
            this.currentTimeGap = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthTimeGap, this.fullHealthTimeGap);
            int var5 = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthStartDelay, this.fullHealthStartDelay);
            this.lastProjectileTime = var1.getWorldEntity().getTime() - (long)(this.currentTimeGap - var5);
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }

      protected class RandomWaveProjectilesStage extends AINode<T> implements AttackStageInterface<T> {
         private Point2D.Float lastDirection;
         private int projectilesFired;
         private long lastProjectileTime;
         private long nextShowAttackTime;
         private int currentTimeGap;
         public int noHealthStartDelay;
         public int fullHealthStartDelay;
         public int noHealthTimeGap;
         public int fullHealthTimeGap;
         public int waves;

         public RandomWaveProjectilesStage(int var2, int var3, int var4, int var5, int var6) {
            this.noHealthStartDelay = var2;
            this.fullHealthStartDelay = var3;
            this.noHealthTimeGap = var4;
            this.fullHealthTimeGap = var5;
            this.waves = var6;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            long var3 = var1.getWorldEntity().getTime();
            if (this.lastDirection != null && this.nextShowAttackTime <= var3) {
               var1.showAttack((int)(var1.x + this.lastDirection.x * 100.0F), (int)(var1.y + this.lastDirection.y * 100.0F), false);
               this.nextShowAttackTime = var3 + 100L;
               if (var1.isServer()) {
                  var1.getLevel().getServer().network.sendToClientsAt(new PacketMobAttack(var1, (int)(var1.x + this.lastDirection.x * 100.0F), (int)(var1.y + this.lastDirection.y * 100.0F)), (Level)var1.getLevel());
               }
            }

            long var5 = var3 - this.lastProjectileTime;

            while(this.projectilesFired < this.waves && var5 >= (long)this.currentTimeGap) {
               Mob var7 = (Mob)var2.getObject(Mob.class, "currentTarget");
               this.lastProjectileTime += (long)this.currentTimeGap;
               var5 = var3 - this.lastProjectileTime;
               if (var7 != null) {
                  this.lastDirection = GameMath.getAngleDir((float)GameRandom.globalRandom.nextInt(360));
                  Point2D.Float var8 = GameMath.getPerpendicularDir(this.lastDirection.x, this.lastDirection.y);
                  float var9 = GameRandom.globalRandom.getFloatBetween(-200.0F, 200.0F);
                  float var10 = 800.0F;
                  float var11 = var7.x + var8.x * var9 - this.lastDirection.x * var10;
                  float var12 = var7.y + var8.y * var9 - this.lastDirection.y * var10;
                  short var13 = 2300;
                  var1.getLevel().entityManager.projectiles.add(new FallenWizardWaveProjectile(var11, var12, var11 + this.lastDirection.x * 100.0F, var12 + this.lastDirection.y * 100.0F, 120.0F, var13, FallenWizardMob.waveDamage, 50, var1));
               }

               ++this.projectilesFired;
               var1.magicBoltSound.runAndSend();
            }

            return this.projectilesFired < this.waves ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.nextShowAttackTime = 0L;
            this.projectilesFired = 0;
            this.currentTimeGap = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthTimeGap, this.fullHealthTimeGap);
            int var3 = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthStartDelay, this.fullHealthStartDelay);
            this.lastProjectileTime = var1.getWorldEntity().getTime() - (long)(this.currentTimeGap - var3);
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }

      protected class GhostFindNewPosStage extends AINode<T> implements AttackStageInterface<T> {
         private LinkedList<Mob> ghostMobs = new LinkedList();

         public GhostFindNewPosStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            ListIterator var3 = this.ghostMobs.listIterator();
            Mob var4 = null;

            while(var3.hasNext()) {
               Mob var5 = (Mob)var3.next();
               if (var5.removed()) {
                  var4 = var5;
                  var3.remove();
               }
            }

            if (!this.ghostMobs.isEmpty()) {
               return AINodeResult.RUNNING;
            } else {
               if (var4 != null) {
                  var1.teleportAbility.runAndSend(var4.getX(), var4.getY(), var4.dir, true, false, true);
                  var1.visibilityAbility.runAndSend(true, false);
               }

               return AINodeResult.SUCCESS;
            }
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.ghostMobs.clear();
            PriorityMap var3 = FallenWizardAI.this.findNewPositions(var1, var2);
            ArrayList var4 = var3.getBestObjects(20);
            LinkedList var5 = new LinkedList();

            for(int var6 = 0; var6 < 4 && !var4.isEmpty(); ++var6) {
               Point var7;
               if (var5.isEmpty()) {
                  var7 = (Point)var4.stream().min(Comparator.comparingDouble((var1x) -> {
                     return var1x.distance((double)var1.x, (double)var1.y);
                  })).get();
               } else {
                  int var8 = (Integer)IntStream.range(0, var4.size()).boxed().max(Comparator.comparingDouble((var2x) -> {
                     return var5.stream().mapToDouble((var2) -> {
                        return ((Point)var4.get(var2x)).distance(var2);
                     }).sum();
                  })).orElse(-1);
                  var7 = (Point)var4.remove(var8);
               }

               FallenWizardGhostMob var9 = (FallenWizardGhostMob)MobRegistry.getMob("fallenwizardghost", var1.getLevel());
               var1.getLevel().entityManager.addMob(var9, var1.x, var1.y);
               var9.moveToPos(var7.x, var7.y);
               this.ghostMobs.add(var9);
               var5.add(var7);
            }

            if (!this.ghostMobs.isEmpty()) {
               var1.visibilityAbility.runAndSend(false, false);
            }

         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }

      protected class BeamSweepStage extends AINode<T> implements AttackStageInterface<T> {
         private boolean clockwise;
         private LevelEvent event;
         public int noHealthSweepTime;
         public int fullHealthSweepTime;
         public float startAngleOffset;
         public float totalAngleCover;

         public BeamSweepStage(int var2, int var3, float var4, float var5) {
            this.clockwise = GameRandom.globalRandom.nextBoolean();
            this.noHealthSweepTime = var2;
            this.fullHealthSweepTime = var3;
            this.startAngleOffset = var4;
            this.totalAngleCover = var5;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            return this.event.isOver() ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
            long var4 = var1.getWorldEntity().getTime();
            float var6;
            if (var3 != null) {
               var6 = GameMath.getAngle(new Point2D.Float(var3.x - var1.x, var3.y - var1.y));
            } else {
               var6 = (float)GameRandom.globalRandom.nextInt(360);
            }

            float var7;
            if (this.clockwise) {
               var6 += this.startAngleOffset;
               var7 = var6 - this.totalAngleCover;
            } else {
               var6 -= this.startAngleOffset;
               var7 = var6 + this.totalAngleCover;
            }

            int var8 = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthSweepTime, this.fullHealthSweepTime);
            var1.getLevel().entityManager.addLevelEvent(this.event = new FallenWizardBeamLevelEvent(var1, var6, var7, var4, var8, GameRandom.globalRandom.nextInt(), 1400.0F, FallenWizardMob.laserDamage, 50, 500, 0));
            this.clockwise = !this.clockwise;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }

      protected class BulletHellStage extends AINode<T> implements AttackStageInterface<T> {
         private long nextShowAttackTime;
         private float currentAngle;
         private float remainingAngle;
         private float angleBuffer;
         private int attackDirection;
         private int direction;
         private long startTime;
         private int currentShootTime;
         public float targetStartAngleOffset;
         public float totalAngle;
         public float anglePerProjectile;
         public int arms;
         public int chargeUpTime;
         public int noHealthShootTime;
         public int fullHealthShootTime;

         public BulletHellStage(int var2, float var3, float var4, int var5, int var6, int var7, float var8) {
            this.targetStartAngleOffset = var3;
            this.totalAngle = var4;
            this.arms = var5;
            this.chargeUpTime = var2;
            this.noHealthShootTime = var6;
            this.fullHealthShootTime = var7;
            this.anglePerProjectile = var8;
            this.direction = (Integer)GameRandom.globalRandom.getOneOf((Object[])(-1, 1));
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            long var3 = var1.getWorldEntity().getTime();
            long var5 = var3 - this.startTime;
            if (this.nextShowAttackTime <= var3) {
               var1.showAttack((int)(var1.x + (float)(this.attackDirection * 30)), (int)(var1.y - 100.0F), false);
               this.nextShowAttackTime = var3 + 200L;
               if (var5 >= (long)this.chargeUpTime) {
                  var1.magicBoltSound.runAndSend();
               }

               if (var1.isServer()) {
                  var1.getLevel().getServer().network.sendToClientsAt(new PacketMobAttack(var1, (int)(var1.x + (float)(this.attackDirection * 30)), (int)(var1.y - 100.0F)), (Level)var1.getLevel());
               }
            }

            if (var5 < (long)this.chargeUpTime) {
               return AINodeResult.RUNNING;
            } else {
               this.angleBuffer += this.totalAngle * 50.0F / (float)this.currentShootTime;

               while(this.angleBuffer >= this.anglePerProjectile) {
                  this.currentAngle += this.anglePerProjectile * (float)this.direction;

                  for(int var7 = 0; var7 < this.arms; ++var7) {
                     float var8 = this.currentAngle + 360.0F / (float)this.arms * (float)var7;
                     FallenWizardBallProjectile var9 = new FallenWizardBallProjectile(var1.getLevel(), var1, var1.x + (float)(this.attackDirection * 15), var1.y - 35.0F, var8, 90.0F, 1200, FallenWizardMob.ballDamage, 50);
                     var1.getLevel().entityManager.projectiles.add(var9);
                  }

                  this.angleBuffer -= this.anglePerProjectile;
                  this.remainingAngle -= this.anglePerProjectile;
                  if (this.remainingAngle <= 0.0F) {
                     break;
                  }
               }

               return this.angleBuffer >= this.remainingAngle ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
            }
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.direction *= -1;
            Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
            if (var3 != null) {
               this.currentAngle = GameMath.getAngle(new Point2D.Float(var3.x - var1.x, var3.y - var1.y)) + this.targetStartAngleOffset * (float)this.direction + 90.0F;
            } else {
               this.currentAngle = (float)GameRandom.globalRandom.nextInt(360);
            }

            this.attackDirection = (int)Math.signum(GameMath.getAngleDir(this.currentAngle).x);
            if (this.attackDirection == 0) {
               this.attackDirection = (Integer)GameRandom.globalRandom.getOneOf((Object[])(-1, 1));
            }

            this.remainingAngle = this.totalAngle;
            this.startTime = var1.getLevel().getWorldEntity().getTime();
            this.currentShootTime = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthShootTime, this.fullHealthShootTime);
            this.nextShowAttackTime = 0L;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }

      protected class DragonSpawnStage extends AINode<T> implements AttackStageInterface<T> {
         private Point2D.Float direction;
         private int dragonsSpawned;
         private long lastSpawnTime;
         private long nextShowAttackTime;
         private int currentTimeGap;
         public int noHealthStartDelay;
         public int fullHealthStartDelay;
         public int noHealthTimeGap;
         public int fullHealthTimeGap;
         public int dragons;
         private float circlingOffset;

         public DragonSpawnStage(int var2, int var3, int var4, int var5, int var6) {
            this.noHealthStartDelay = var2;
            this.fullHealthStartDelay = var3;
            this.noHealthTimeGap = var4;
            this.fullHealthTimeGap = var5;
            this.dragons = var6;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            long var3 = var1.getWorldEntity().getTime();
            if (this.nextShowAttackTime <= var3) {
               var1.showAttack((int)(var1.x + this.direction.x * 100.0F), (int)(var1.y + this.direction.y * 100.0F), false);
               this.nextShowAttackTime = var3 + 100L;
               if (var1.isServer()) {
                  var1.getLevel().getServer().network.sendToClientsAt(new PacketMobAttack(var1, (int)(var1.x + this.direction.x * 100.0F), (int)(var1.y + this.direction.y * 100.0F)), (Level)var1.getLevel());
               }
            }

            for(long var5 = var3 - this.lastSpawnTime; this.dragonsSpawned < this.dragons && var5 >= (long)this.currentTimeGap; this.circlingOffset += 360.0F / (float)this.dragons) {
               this.lastSpawnTime += (long)this.currentTimeGap;
               var5 = var3 - this.lastSpawnTime;
               Point2D.Float var7 = GameMath.getPerpendicularDir(this.direction.x, this.direction.y);
               int var8 = GameRandom.globalRandom.getIntBetween(-150, 150);
               int var9 = GameRandom.globalRandom.getIntBetween(150, 300);
               float var10 = var1.x + var7.x * (float)var8 - this.direction.x * (float)var9;
               float var11 = var1.y + var7.y * (float)var8 - this.direction.y * (float)var9;
               ++this.dragonsSpawned;
               var1.spawnDragonEffects.runAndSend((int)var10, (int)var11);
               FallenDragonHead var12 = (FallenDragonHead)MobRegistry.getMob("fallendragon", var1.getLevel());
               var12.master = var1;
               var12.circlingAngleOffset = this.circlingOffset;
               if (var1.isInArena()) {
                  var12.centerPosition = TempleArenaLevel.getBossPosition();
               }

               var1.getLevel().entityManager.addMob(var12, var10, var11);
            }

            if (this.dragonsSpawned < this.dragons) {
               return AINodeResult.RUNNING;
            } else {
               this.circlingOffset += 360.0F / (float)this.dragons / 2.0F + 18.0F;
               return AINodeResult.SUCCESS;
            }
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            Mob var3 = (Mob)var2.getObject(Mob.class, "currentTarget");
            Point2D.Float var4;
            if (var3 != null) {
               var4 = new Point2D.Float(var3.x, var3.y);
            } else if (var1.isInArena()) {
               var4 = TempleArenaLevel.getBossPosition();
            } else {
               var4 = new Point2D.Float(var1.x, var1.y + 100.0F);
            }

            this.direction = GameMath.normalize(var4.x - var1.x, var4.y - var1.y);
            if (this.direction.x == 0.0F && this.direction.y == 0.0F) {
               this.direction = new Point2D.Float(0.0F, 1.0F);
            }

            this.nextShowAttackTime = 0L;
            this.dragonsSpawned = 0;
            this.currentTimeGap = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthTimeGap, this.fullHealthTimeGap);
            int var5 = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthStartDelay, this.fullHealthStartDelay);
            this.lastSpawnTime = var1.getWorldEntity().getTime() - (long)(this.currentTimeGap - var5);
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }

      protected class ScepterProjectilesStage extends AINode<T> implements AttackStageInterface<T> {
         private Mob target;
         private long startTime;
         private int currentTimeSpan;
         private float projectileBuffer;
         private long nextShowAttackTime;
         public int projectiles;
         public int noHealthTimeSpan;
         public int fullHealthTimeSpan;

         public ScepterProjectilesStage(int var2, int var3, int var4) {
            this.projectiles = var2;
            this.noHealthTimeSpan = var3;
            this.fullHealthTimeSpan = var4;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            long var3 = var1.getWorldEntity().getTime();
            if (var3 >= this.startTime + (long)this.currentTimeSpan) {
               return AINodeResult.SUCCESS;
            } else {
               if (this.target == null || this.target.removed()) {
                  this.target = (Mob)var2.getObject(Mob.class, "currentTarget");
               }

               if (this.target != null) {
                  if (this.nextShowAttackTime <= var3) {
                     var1.showAttack(this.target.getX(), this.target.getY(), false);
                     this.nextShowAttackTime = var3 + 100L;
                     if (var1.isServer()) {
                        var1.getLevel().getServer().network.sendToClientsAt(new PacketMobAttack(var1, this.target.getX(), this.target.getY()), (Level)var1.getLevel());
                     }
                  }

                  this.projectileBuffer += TickManager.getTickDelta((float)this.currentTimeSpan / 1000.0F / (float)this.projectiles);
                  if (this.projectileBuffer >= 1.0F) {
                     var1.laserBlastSound.runAndSend();

                     while(this.projectileBuffer >= 1.0F) {
                        --this.projectileBuffer;
                        int var5 = this.target.getX() + GameRandom.globalRandom.getIntBetween(-40, 40);
                        int var6 = this.target.getY() + GameRandom.globalRandom.getIntBetween(-40, 40);
                        Point2D.Float var7 = GameMath.normalize((float)var5 - var1.x, (float)var6 - var1.y);
                        Point2D.Float var8 = GameMath.getPerpendicularDir(var7.x, var7.y);
                        int var9 = GameRandom.globalRandom.getIntBetween(-20, 20);
                        short var10 = 1500;
                        FallenWizardScepterProjectile var11 = new FallenWizardScepterProjectile(var1.x + var8.x * (float)var9, var1.y + var8.y * (float)var9, var1.x - var7.x * 100.0F, var1.y - var7.y * 100.0F, 250, var10, FallenWizardMob.scepterDamage, 50, var1);
                        var11.setAngle(var11.getAngle() + (float)GameRandom.globalRandom.getIntBetween(-140, 140));
                        var11.targetPos = new Point(var5, var6);
                        var11.turnSpeed = GameRandom.globalRandom.getFloatBetween(1.0F, 2.0F);
                        var11.angleLeftToTurn = 240.0F;
                        var11.resetUniqueID(GameRandom.globalRandom);
                        var1.getLevel().entityManager.projectiles.add(var11);
                     }
                  }
               }

               return AINodeResult.RUNNING;
            }
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.target = null;
            this.startTime = var1.getWorldEntity().getTime();
            this.projectileBuffer = 0.0F;
            this.nextShowAttackTime = 0L;
            this.currentTimeSpan = FallenWizardAI.this.getHealthPercValue(var1, this.noHealthTimeSpan, this.fullHealthTimeSpan);
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (FallenWizardMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((FallenWizardMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((FallenWizardMob)var1, var2);
         }
      }
   }

   public static class PortalParticle extends Particle {
      public PortalParticle(Level var1, int var2, int var3, long var4) {
         super(var1, (float)var2, (float)var3, 0.0F, 0.0F, var4);
      }

      public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
         if (!this.removed()) {
            GameLight var9 = var5.getLightLevel(this);
            int var10 = var7.getDrawX(this.x);
            int var11 = var7.getDrawY(this.y);
            long var12 = this.getLifeCycleTime();
            float var14 = 1.0F;
            float var15;
            if (var12 >= this.lifeTime - 500L) {
               var15 = GameMath.limit((float)(var12 - (this.lifeTime - 500L)) / 500.0F, 0.0F, 1.0F);
               var14 = Math.abs(var15 - 1.0F);
            }

            var15 = (float)var12 / 2.0F;
            final TextureDrawOptionsEnd var16 = GameResources.fallenWizardPortalParticle.initDraw().light(var9).alpha(var14).rotate(-var15).posMiddle(var10, var11 - 15);
            var1.add(new EntityDrawable(this) {
               public void draw(TickManager var1) {
                  var16.draw();
               }
            });
         }
      }
   }

   public static class TeleportParticle extends Particle {
      private Point animSprite;
      private int dir;

      public TeleportParticle(Level var1, int var2, int var3, FallenWizardMob var4) {
         super(var1, (float)var2, (float)var3, 0.0F, 0.0F, 1000L);
         this.animSprite = var4.getAnimSprite(var2, var3, this.dir);
         this.dir = var4.dir;
      }

      public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
         float var9 = this.getLifeCyclePercent();
         if (!this.removed()) {
            GameLight var10 = var5.getLightLevel(this);
            int var11 = var7.getDrawX(this.x) - 48;
            int var12 = var7.getDrawY(this.y) - 70;
            float var13 = Math.abs(var9 - 1.0F);
            final DrawOptions var14 = (new HumanDrawOptions(var5, MobRegistry.Textures.fallenWizard)).sprite(this.animSprite, 96).size(96, 96).dir(this.dir).light(var10).alpha(var13).pos(var11, var12);
            var1.add(new EntityDrawable(this) {
               public void draw(TickManager var1) {
                  var14.draw();
               }
            });
         }
      }
   }
}
