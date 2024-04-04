package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardHomingEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.voidWizard.VoidWizardMissileEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ability.TimedMobAbility;
import necesse.entity.mobs.ability.VolumePitchSoundMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.ConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.VoidWizardWaveProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;
import necesse.level.maps.light.GameLight;

public class VoidWizard extends BossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("voidshard", 25), new OneOfLootItems(LootItem.between("recallscroll", 12, 20), new LootItemInterface[]{LootItem.between("travelscroll", 5, 10)}), new ChanceLootItem(0.25F, "wizardsawakeningvinyl")});
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("voidstaff"), new LootItem("voidmissile"), new LootItem("magicstilts"));
   public static LootTable privateLootTable;
   public static GameDamage cloneProjectile;
   public static GameDamage missile;
   public static GameDamage waveProjectile;
   public static GameDamage homingExplosion;
   public static int homingExplosionRange;
   public static MaxHealthGetter MAX_HEALTH;
   protected boolean itemSpawned;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public boolean swingAttack;
   protected ArrayList<Projectile> projectiles;
   protected boolean inSecondStageTransition;
   protected long secondStageTransitionStartTime;
   protected boolean playedSecondStageSound;
   protected boolean inDeathTransition;
   protected boolean allowDeath;
   protected long deathTransitionStartTime;
   protected boolean isSecondStage;
   public final VoidTeleportAbility teleportAbility;
   public final BooleanMobAbility changeHostile;
   public final TimedMobAbility startSecondStageAbility;
   public final TimedMobAbility startDeathStageAbility;
   public final VolumePitchSoundMobAbility playBoltSoundAbility;

   public VoidWizard() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.attackAnimTime = 200;
      this.setSpeed(50.0F);
      this.setFriction(3.0F);
      this.setArmor(10);
      this.setKnockbackModifier(0.0F);
      this.dir = 2;
      this.updateBoxes();
      this.shouldSave = true;
      this.isHostile = false;
      this.teleportAbility = (VoidTeleportAbility)this.registerAbility(new VoidTeleportAbility());
      this.changeHostile = (BooleanMobAbility)this.registerAbility(new BooleanMobAbility() {
         protected void run(boolean var1) {
            if (!VoidWizard.this.isHostile && var1 && VoidWizard.this.isClient()) {
               Screen.playSound(GameResources.magicroar, SoundEffect.globalEffect());
            }

            VoidWizard.this.isHostile = var1;
            if (!VoidWizard.this.isHostile) {
               VoidWizard.this.isSecondStage = false;
               VoidWizard.this.updateBoxes();
               VoidWizard.this.inSecondStageTransition = false;
               VoidWizard.this.resetAI();
            }

         }
      });
      this.startSecondStageAbility = (TimedMobAbility)this.registerAbility(new TimedMobAbility() {
         protected void run(long var1) {
            VoidWizard.this.inSecondStageTransition = true;
            VoidWizard.this.playedSecondStageSound = false;
            VoidWizard.this.secondStageTransitionStartTime = var1;
            VoidWizard.this.isSecondStage = true;
            VoidWizard.this.updateBoxes();
            VoidWizard.this.moveX = 0.0F;
            VoidWizard.this.moveY = 0.0F;
         }
      });
      this.playBoltSoundAbility = (VolumePitchSoundMobAbility)this.registerAbility(new VolumePitchSoundMobAbility() {
         protected void run(float var1, float var2) {
            if (VoidWizard.this.isClient()) {
               Screen.playSound(GameResources.magicbolt1, SoundEffect.globalEffect().volume(var1).pitch(var2));
            }

         }
      });
      this.startDeathStageAbility = (TimedMobAbility)this.registerAbility(new TimedMobAbility() {
         protected void run(long var1) {
            VoidWizard.this.inDeathTransition = true;
            VoidWizard.this.deathTransitionStartTime = var1;
            VoidWizard.this.moveX = 0.0F;
            VoidWizard.this.moveY = 0.0F;
         }
      });
   }

   public void init() {
      super.init();
      VoidWizardAI var1 = this.resetAI();
      this.projectiles = new ArrayList();
      if (this.isServer() && this.getLevel().getIslandDimension() == -101) {
         Point2D.Float var2 = DungeonArenaLevel.getBossPosition();
         this.setPos(var2.x, var2.y, true);
         this.setHealth(this.getMaxHealth());
         this.dir = 2;
      }

      if (this.itemSpawned) {
         if (this.isClient()) {
            this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), (float)this.getX(), (float)this.getY(), new Color(100, 22, 22))), Particle.GType.CRITICAL);
            if (this.isHostile) {
               Screen.playSound(GameResources.magicroar, SoundEffect.globalEffect());
            }
         } else if (this.isServer()) {
            var1.makeHostile(this);
         }
      }

   }

   protected VoidWizardAI<VoidWizard> resetAI() {
      VoidWizardAI var1 = new VoidWizardAI();
      this.ai = new BehaviourTreeAI(this, var1);
      return var1;
   }

   public void updateBoxes() {
      if (this.isSecondStage) {
         this.collision = new Rectangle(-20, -50, 40, 45);
         this.hitBox = new Rectangle(-20, -50, 40, 50);
         this.selectBox = new Rectangle(-20, -61, 40, 60);
      } else {
         this.collision = new Rectangle(-10, -7, 20, 14);
         this.hitBox = new Rectangle(-18, -15, 36, 30);
         this.selectBox = new Rectangle(-14, -41, 28, 48);
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.itemSpawned);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.itemSpawned = var1.getNextBoolean();
   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isHostile);
      var1.putNextBoolean(this.isSecondStage);
      var1.putNextBoolean(this.inSecondStageTransition);
      if (this.inSecondStageTransition) {
         var1.putNextLong(this.secondStageTransitionStartTime);
      }

      var1.putNextBoolean(this.inDeathTransition);
      if (this.inDeathTransition) {
         var1.putNextLong(this.deathTransitionStartTime);
      }

   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isHostile = var1.getNextBoolean();
      this.isSecondStage = var1.getNextBoolean();
      this.updateBoxes();
      this.inSecondStageTransition = var1.getNextBoolean();
      if (this.inSecondStageTransition) {
         this.secondStageTransitionStartTime = var1.getNextLong();
      }

      this.inDeathTransition = var1.getNextBoolean();
      if (this.inDeathTransition) {
         this.deathTransitionStartTime = var1.getNextLong();
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

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public void makeItemSpawned() {
      this.itemSpawned = true;
   }

   public boolean canAddProjectile() {
      return this.projectiles.size() < 50;
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
      if (this.isHostile) {
         Screen.setMusic(MusicRegistry.WizardsAwakening, Screen.MusicPriority.EVENT, 1.5F);
         Color var1 = getWizardShade(this);
         float var2 = (float)var1.getRed() / 255.0F;
         float var3 = (float)var1.getGreen() / 255.0F;
         float var4 = (float)var1.getBlue() / 255.0F;
         float var5 = 1.0F / GameMath.min(var2, var3, var4);
         Screen.setSceneShade(var2 * var5, var3 * var5, var4 * var5);
         Screen.registerMobHealthStatusBar(this);
         BossNearbyBuff.applyAround(this);
      }

      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 270.0F, 0.5F);
      if (this.inDeathTransition) {
         this.setHealthHidden(1);
      } else if (this.inSecondStageTransition) {
         long var6 = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime;
         if (!this.playedSecondStageSound && var6 >= 4500L) {
            Screen.playSound(GameResources.magicroar, SoundEffect.globalEffect());
            this.playedSecondStageSound = true;
         }

         if (var6 >= 5000L) {
            this.inSecondStageTransition = false;
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      if (this.isHostile) {
         BossNearbyBuff.applyAround(this);
      }

      long var1;
      if (this.inDeathTransition) {
         this.setHealthHidden(1);
         var1 = this.getWorldEntity().getTime() - this.deathTransitionStartTime;
         if (var1 >= 3000L && !this.removed()) {
            this.allowDeath = true;
            this.setHealthHidden(0);
         }
      } else if (this.inSecondStageTransition) {
         var1 = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime;
         if (var1 >= 5000L) {
            this.inSecondStageTransition = false;
         }
      }

   }

   public int getFlyingHeight() {
      return this.isSecondStage ? 50 : super.getFlyingHeight();
   }

   public boolean canHitThroughCollision() {
      return this.isSecondStage ? true : super.canHitThroughCollision();
   }

   public CollisionFilter getLevelCollisionFilter() {
      return this.isSecondStage ? null : super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public void spawnDeathParticles(float var1, float var2) {
   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath2, SoundEffect.effect(this));
   }

   public void setHealthHidden(int var1, float var2, float var3, Attacker var4, boolean var5) {
      if (var1 <= 0 && !this.allowDeath) {
         var1 = 1;
         if (this.isServer() && !this.inDeathTransition) {
            this.startDeathStageAbility.runAndSend(this.getWorldEntity().getTime());
         }
      }

      super.setHealthHidden(var1, var2, var3, var4, var5);
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      var2.stream().map(Attacker::getAttackOwner).filter((var0) -> {
         return var0 != null && var0.isPlayer;
      }).distinct().forEach((var1x) -> {
         this.getLevel().getServer().network.sendPacket(new PacketChatMessage(new LocalMessage("misc", "bossdefeat", "name", this.getLocalization())), (ServerClient)((PlayerMob)var1x).getServerClient());
      });
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11;
      int var12;
      long var13;
      byte var15;
      short var16;
      int var17;
      int var18;
      final TextureDrawOptionsEnd var20;
      if (this.inDeathTransition) {
         this.dir = 2;
         var11 = var8.getDrawX(var5) - 48;
         var12 = var8.getDrawY(var6) - 84;
         var13 = this.getWorldEntity().getTime() - this.deathTransitionStartTime;
         var15 = 16;
         var16 = 3000;
         var17 = var16 / var15;
         var18 = (int)Math.min(GameMath.limit(var13, 0L, (long)var16) / (long)var17, (long)(var15 - 1));
         final TextureDrawOptionsEnd var19 = MobRegistry.Textures.voidWizard2.initDraw().sprite(var18 % 8, 2 + var18 / 8, 96).light(var10).pos(var11, var12);
         var20 = MobRegistry.Textures.voidWizard3.initDraw().sprite(var18 % 8, 2 + var18 / 8, 96).light(var10.minLevelCopy(Math.min(var10.getLevel() + 100.0F, 150.0F))).pos(var11, var12);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               var19.draw();
               var20.draw();
            }
         });
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      } else if (this.inSecondStageTransition) {
         this.dir = 2;
         var11 = var8.getDrawX(var5) - 48;
         var12 = var8.getDrawY(var6) - 84;
         var13 = this.getWorldEntity().getTime() - this.secondStageTransitionStartTime;
         var15 = 6;
         var16 = 3000;
         short var29 = 2000;
         var18 = var29 / var15;
         int var32 = (int)Math.min(GameMath.limit(var13 - (long)var16, 0L, (long)var29) / (long)var18, (long)(var15 - 1));
         var20 = MobRegistry.Textures.voidWizard2.initDraw().sprite(var32, 1, 96).light(var10).pos(var11, var12);
         final TextureDrawOptionsEnd var21 = MobRegistry.Textures.voidWizard3.initDraw().sprite(var32, 1, 96).light(var10.minLevelCopy(Math.min(var10.getLevel() + 100.0F, 150.0F))).pos(var11, var12);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               var20.draw();
               var21.draw();
            }
         });
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      } else if (this.isSecondStage) {
         var11 = var8.getDrawX(var5) - 48;
         var12 = var8.getDrawY(var6) - 84;
         int var22 = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
         float var14 = Math.min(30.0F, this.dx / 5.0F);
         final TextureDrawOptionsEnd var25 = MobRegistry.Textures.voidWizard2.initDraw().sprite(var22, 0, 96).light(var10).mirror(this.dir == 0, false).rotate(var14, 48, 64).pos(var11, var12);
         final TextureDrawOptionsEnd var27 = MobRegistry.Textures.voidWizard3.initDraw().sprite(var22, 0, 96).light(var10.minLevelCopy(Math.min(var10.getLevel() + 100.0F, 150.0F))).mirror(this.dir == 0, false).rotate(var14, 48, 64).pos(var11, var12);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               var25.draw();
               var27.draw();
            }
         });
         var17 = MobRegistry.Textures.human_big_shadow.getHeight();
         TextureDrawOptionsEnd var31 = MobRegistry.Textures.human_big_shadow.initDraw().sprite(0, 0, var17).light(var10).posMiddle(var8.getDrawX(var5), var8.getDrawY(var6) + 10);
         var2.add((var1x) -> {
            var31.draw();
         });
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      } else {
         var11 = var8.getDrawX(var5) - 22 - 10;
         var12 = var8.getDrawY(var6) - 44 - 7;
         Point var23 = this.getAnimSprite(var5, var6, this.dir);
         var12 += this.getBobbing(var5, var6);
         var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
         HumanDrawOptions var24 = (new HumanDrawOptions(var4, MobRegistry.Textures.voidWizard)).sprite(var23).dir(this.dir).light(var10);
         float var26 = this.getAttackAnimProgress();
         if (this.isAttacking) {
            ItemAttackDrawOptions var28 = ItemAttackDrawOptions.start(this.dir).itemSprite(MobRegistry.Textures.voidWizard.body, 0, 9, 32).itemRotatePoint(3, 3).itemEnd().armSprite(MobRegistry.Textures.voidWizard.body, 0, 8, 32).light(var10);
            if (this.swingAttack) {
               var28.swingRotation(var26);
            } else {
               var28.pointRotation(this.attackDir.x, this.attackDir.y);
            }

            var24.attackAnim(var28, var26);
         }

         final DrawOptions var30 = var24.pos(var11, var12);
         var1.add(new MobDrawable() {
            public void draw(TickManager var1) {
               var30.draw();
            }
         });
         this.addShadowDrawables(var2, var5, var6, var10, var8);
      }

   }

   public static Color getWizardShade(Mob var0) {
      Color var1 = new Color(255, 255, 255);
      if (var0 == null) {
         return var1;
      } else {
         float var2 = (float)var0.getWorldEntity().getTime() / 5000.0F;
         float var3 = var2 - (float)Math.floor((double)var2);
         return Color.getHSBColor(var3, 0.2F, 1.0F);
      }
   }

   public static Color getWizardColor(Mob var0) {
      return new Color(100, 22, 22);
   }

   public static Color getWizardProjectileColor(Mob var0) {
      return new Color(50, 0, 102);
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
      int var4;
      int var5;
      if (this.isSecondStage) {
         var4 = var2 - 24;
         var5 = var3 - 32;
         int var6 = GameUtils.getAnim(this.getWorldEntity().getTime(), 4, 400);
         MobRegistry.Textures.voidWizard2.initDraw().sprite(var6, 0, 96).size(48, 48).draw(var4, var5);
      } else {
         var4 = var2 - 16;
         var5 = var3 - 26;
         Point var7 = this.getAnimSprite(this.getDrawX(), this.getDrawY(), this.dir);
         (new HumanDrawOptions(this.getLevel(), MobRegistry.Textures.voidWizard)).sprite(var7).dir(this.dir).size(32, 32).draw(var4, var5);
      }

   }

   public Stream<ModifierValue<?>> getDefaultModifiers() {
      return Stream.of((new ModifierValue(BuffModifiers.SLOW, 0.0F)).max(0.2F), new ModifierValue(BuffModifiers.ATTACK_MOVEMENT_MOD, 0.0F));
   }

   public float getSpeed() {
      return super.getSpeed() * (this.isSecondStage ? 1.4F : 1.0F);
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("voidwiz", 4);
   }

   static {
      privateLootTable = new LootTable(new LootItemInterface[]{new ConditionLootItem("emptypendant", (var0, var1) -> {
         ServerClient var2 = (ServerClient)LootTable.expectExtra(ServerClient.class, var1, 1);
         return var2 != null && var2.playerMob.getInv().trinkets.getSize() < 5 && var2.playerMob.getInv().getAmount(ItemRegistry.getItem("emptypendant"), false, true, true, "have") == 0;
      }), uniqueDrops});
      cloneProjectile = new GameDamage(42.0F);
      missile = new GameDamage(35.0F);
      waveProjectile = new GameDamage(32.0F);
      homingExplosion = new GameDamage(50.0F);
      homingExplosionRange = 55;
      MAX_HEALTH = new MaxHealthGetter(3500, 4000, 4500, 5000, 6000);
   }

   public class VoidTeleportAbility extends MobAbility {
      public VoidTeleportAbility() {
      }

      public void runAndSend(int var1, int var2, int var3, boolean var4) {
         Packet var5 = new Packet();
         PacketWriter var6 = new PacketWriter(var5);
         var6.putNextInt(var1);
         var6.putNextInt(var2);
         var6.putNextInt(var3);
         var6.putNextBoolean(var4);
         this.runAndSendAbility(var5);
      }

      public void executePacket(PacketReader var1) {
         int var2 = var1.getNextInt();
         int var3 = var1.getNextInt();
         int var4 = var1.getNextInt();
         boolean var5 = var1.getNextBoolean();
         if (var5 && VoidWizard.this.isClient()) {
            VoidWizard.this.getLevel().entityManager.addParticle((Particle)(new TeleportParticle(VoidWizard.this.getLevel(), VoidWizard.this.getX(), VoidWizard.this.getY(), VoidWizard.this)), Particle.GType.CRITICAL);
            VoidWizard.this.getLevel().lightManager.refreshParticleLightFloat((float)var2, (float)var3, 270.0F, 0.5F);
         }

         VoidWizard.this.dir = var4;
         VoidWizard.this.setPos((float)var2, (float)var3, true);
      }
   }

   public static class VoidWizardAI<T extends VoidWizard> extends SelectorAINode<T> {
      private int inActiveTimer;
      private Point middlePoint;

      public VoidWizardAI() {
         this.addChild(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               if (var1.isHostile) {
                  if (!var1.inDeathTransition && !var1.inSecondStageTransition) {
                     return AINodeResult.FAILURE;
                  } else {
                     var2.mover.stopMoving(var1);
                     return AINodeResult.SUCCESS;
                  }
               } else {
                  var2.mover.stopMoving(var1);
                  return AINodeResult.SUCCESS;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((VoidWizard)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((VoidWizard)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (VoidWizard)var2, var3);
            }
         });
         this.addChild(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
               var3.onEvent("refreshBossDespawn", (var1x) -> {
                  VoidWizardAI.this.inActiveTimer = 0;
               });
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               boolean var3 = GameUtils.streamServerClients(var1.getLevel()).anyMatch((var1x) -> {
                  return !var1x.isDead() && var1x.playerMob.canBeTargeted(var1, (NetworkClient)null);
               });
               if (!var3) {
                  VoidWizardAI.this.inActiveTimer++;
                  if (VoidWizardAI.this.inActiveTimer > 100) {
                     Point2D.Float var4 = DungeonArenaLevel.getBossPosition();
                     var1.teleportAbility.runAndSend((int)var4.x, (int)var4.y, 2, true);
                     var1.changeHostile.runAndSend(false);
                     VoidWizardAI.this.inActiveTimer = 0;
                     var1.clearProjectiles();
                     var1.setHealth(var1.getMaxHealth());
                  }

                  return AINodeResult.SUCCESS;
               } else {
                  VoidWizardAI.this.inActiveTimer = 0;
                  return AINodeResult.FAILURE;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((VoidWizard)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((VoidWizard)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (VoidWizard)var2, var3);
            }
         });
         AttackStageManagerNode var1 = new AttackStageManagerNode();
         var1.allowSkippingBack = true;
         this.addChild(new ConditionAINode(new IsolateRunningAINode(var1), (var0) -> {
            return !var0.isSecondStage;
         }, AINodeResult.FAILURE));
         var1.addChild(new FindNewPosStage(true, false));
         var1.addChild(new CheckSwitchSecondStage());
         var1.addChild(new HomingAttacksStage());
         var1.addChild(new CheckSwitchSecondStage());
         var1.addChild(new FindNewPosStage(true, false));
         var1.addChild(new CheckSwitchSecondStage());
         var1.addChild(new MissileAttacksStage());
         var1.addChild(new CheckSwitchSecondStage());
         var1.addChild(new IdleTimeAttackStage(500));
         var1.addChild(new CheckSwitchSecondStage());
         var1.addChild(new SpawnClonesStage());
         var1.addChild(new CheckSwitchSecondStage());
         var1 = new AttackStageManagerNode();
         var1.allowSkippingBack = true;
         this.addChild(new ConditionAINode(new IsolateRunningAINode(var1), (var0) -> {
            return var0.isSecondStage;
         }, AINodeResult.FAILURE));
         var1.addChild(new FindNewPosStage(false, true));
         var1.addChild(new WaveAttacksStage());
         var1.addChild(new IdleTimeAttackStage(1000));
         var1.addChild(new FindNewPosStage(false, true));
         var1.addChild(new HomingAttacksStage());
         var1.addChild(new IdleTimeAttackStage(1000));
         var1.addChild(new FindNewPosStage(false, true));
         var1.addChild(new MissileAttacksStage());
         var1.addChild(new IdleTimeAttackStage(1000));
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

            if (var1.getLevel().getIslandDimension() == -101) {
               this.middlePoint = new Point(var1.getLevel().width / 2, var1.getLevel().height / 2);
            } else {
               this.middlePoint = new Point(var1.getX() / 32, var1.getY() / 32);
            }

            var1.changeHostile.runAndSend(true);
         }

      }

      public Point findNewPosition(Mob var1) {
         ArrayList var2 = new ArrayList();
         byte var3 = 10;
         byte var4 = 5;
         int var5 = var1.getX() / 32;
         int var6 = var1.getY() / 32;
         int var7 = (int)Math.signum((float)(this.middlePoint.x - var5)) * var3 / 2;
         int var8 = (int)Math.signum((float)(this.middlePoint.y - var6)) * var3 / 2;

         for(int var9 = -var3; var9 <= var3; ++var9) {
            if (var9 <= -var4 || var9 >= var4) {
               for(int var10 = -var3; var10 <= var3; ++var10) {
                  if (var10 <= -var4 || var10 >= var4) {
                     Point var11 = new Point(var5 + var7 + var9, var6 + var8 + var10);
                     if (!var1.getLevel().isSolidTile(var11.x, var11.y)) {
                        var2.add(var11);
                     }
                  }
               }
            }
         }

         return (Point)GameRandom.globalRandom.getOneOf((List)var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (VoidWizard)var2, var3);
      }

      protected class FindNewPosStage extends MoveTaskAINode<T> implements AttackStageInterface<T> {
         public boolean waitForArrive;
         public boolean directMovement;
         private boolean hasStartedMoving;

         public FindNewPosStage(boolean var2, boolean var3) {
            this.waitForArrive = var2;
            this.directMovement = var3;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tickNode(T var1, Blackboard<T> var2) {
            if (!this.hasStartedMoving) {
               this.hasStartedMoving = true;
               Point var3 = VoidWizardAI.this.findNewPosition(var1);
               if (var3 != null) {
                  if (!this.directMovement) {
                     return this.moveToTileTask(var3.x, var3.y, (BiPredicate)null, (var0) -> {
                        var0.move((Runnable)null);
                        return null;
                     });
                  }

                  var2.mover.directMoveTo(this, var3.x * 32 + 16, var3.y * 32 + 16);
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
            return this.tickNode((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (VoidWizard)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((VoidWizard)var1, var2);
         }
      }

      protected class CheckSwitchSecondStage extends AINode<T> {
         protected CheckSwitchSecondStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (!var1.isSecondStage && var1.getHealth() <= var1.getMaxHealth() / 2) {
               var1.startSecondStageAbility.runAndSend(var1.getWorldEntity().getTime());
               return AINodeResult.RUNNING;
            } else {
               return AINodeResult.SUCCESS;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (VoidWizard)var2, var3);
         }
      }

      protected class HomingAttacksStage extends AINode<T> implements AttackStageInterface<T> {
         public LevelEvent event;

         protected HomingAttacksStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (this.event == null) {
               List var3 = (List)GameUtils.streamServerClients(var1.getLevel()).filter((var1x) -> {
                  return !var1x.isDead() && var1x.playerMob.canBeTargeted(var1, (NetworkClient)null);
               }).collect(Collectors.toList());
               ServerClient var4 = (ServerClient)GameRandom.globalRandom.getOneOf(var3);
               if (var4 == null) {
                  return AINodeResult.SUCCESS;
               }

               this.event = new VoidWizardHomingEvent(var1, var4.playerMob, var1.isSecondStage, !var1.isSecondStage);
               var1.getLevel().entityManager.addLevelEvent(this.event);
            }

            return !this.event.isOver() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.event = null;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (VoidWizard)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((VoidWizard)var1, var2);
         }
      }

      protected class MissileAttacksStage extends AINode<T> implements AttackStageInterface<T> {
         public LevelEvent event;

         protected MissileAttacksStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (this.event == null) {
               List var3 = (List)GameUtils.streamServerClients(var1.getLevel()).filter((var1x) -> {
                  return !var1x.isDead() && var1x.playerMob.canBeTargeted(var1, (NetworkClient)null);
               }).collect(Collectors.toList());
               ServerClient var4 = (ServerClient)GameRandom.globalRandom.getOneOf(var3);
               if (var4 == null) {
                  return AINodeResult.SUCCESS;
               }

               this.event = new VoidWizardMissileEvent(var1, var4.playerMob);
               var1.getLevel().entityManager.addLevelEvent(this.event);
            }

            return !this.event.isOver() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.event = null;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (VoidWizard)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((VoidWizard)var1, var2);
         }
      }

      protected class SpawnClonesStage extends AINode<T> implements AttackStageInterface<T> {
         public LinkedList<VoidWizardClone> clones = new LinkedList();
         public int ticker;

         protected SpawnClonesStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (this.ticker == 0) {
               this.clones.clear();
               List var3 = (List)GameUtils.streamServerClients(var1.getLevel()).filter((var1x) -> {
                  return !var1x.isDead() && var1x.playerMob.canBeTargeted(var1, (NetworkClient)null);
               }).map((var0) -> {
                  return var0.playerMob;
               }).collect(Collectors.toList());
               Object var4 = (Mob)GameRandom.globalRandom.getOneOf(var3);
               if (var4 == null) {
                  var4 = var1;
               }

               GameUtils.streamServerClients(var1.getLevel()).filter((var1x) -> {
                  return var1x.summonFocus == var1;
               }).forEach(ServerClient::clearSummonFocus);
               ArrayList var5 = new ArrayList();
               int var6 = Math.min(3 + var3.size(), 8);
               float var7 = 360.0F / (float)var6;

               for(int var8 = 0; var8 < var6; ++var8) {
                  float var9 = var7 * (float)var8;
                  Point2D.Float var10 = GameMath.getAngleDir(var9 - 90.0F);
                  var5.add(new Point(((Mob)var4).getX() - (int)(var10.x * 80.0F), ((Mob)var4).getY() - (int)(var10.y * 80.0F)));
               }

               Point var15 = null;

               while(!var5.isEmpty()) {
                  int var16 = GameRandom.globalRandom.nextInt(var5.size());
                  Point var17 = (Point)var5.remove(var16);
                  if (!var1.collidesWith(var1.getLevel(), var17.x, var17.y)) {
                     if (var15 == null) {
                        var15 = var17;
                        var1.setFacingDir(((Mob)var4).x - (float)var17.x, ((Mob)var4).y - (float)var17.y);
                     } else {
                        VoidWizardClone var11 = (VoidWizardClone)MobRegistry.getMob("voidwizardclone", var1.getLevel());
                        var11.setOriginal(var1);
                        var11.setFacingDir(((Mob)var4).x - (float)var17.x, ((Mob)var4).y - (float)var17.y);
                        float var10002 = (float)var17.x;
                        var1.getLevel().entityManager.addMob(var11, var10002, (float)var17.y);
                        this.clones.add(var11);
                     }
                  }
               }

               if (var15 == null) {
                  var15 = new Point(var1.getX(), var1.getY());
               }

               var1.teleportAbility.runAndSend(var15.x, var15.y, var1.dir, true);
            }

            ++this.ticker;
            if (this.ticker <= 20) {
               return AINodeResult.RUNNING;
            } else {
               Iterator var12 = this.clones.iterator();

               while(var12.hasNext()) {
                  VoidWizardClone var14 = (VoidWizardClone)var12.next();
                  Point var13 = VoidWizardAI.this.findNewPosition(var14);
                  if (var13 != null) {
                     var14.moveToPos(var13.x, var13.y);
                  } else {
                     var14.remove();
                  }
               }

               return AINodeResult.SUCCESS;
            }
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.ticker = 0;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (VoidWizard)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((VoidWizard)var1, var2);
         }
      }

      protected class WaveAttacksStage extends AINode<T> implements AttackStageInterface<T> {
         public Mob target;
         public int tick;

         protected WaveAttacksStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (this.target == null) {
               List var3 = (List)GameUtils.streamServerClients(var1.getLevel()).filter((var1x) -> {
                  return !var1x.isDead() && var1x.playerMob.canBeTargeted(var1, (NetworkClient)null);
               }).map((var0) -> {
                  return var0.playerMob;
               }).collect(Collectors.toList());
               this.target = (Mob)GameRandom.globalRandom.getOneOf(var3);
               if (this.target == null) {
                  return AINodeResult.SUCCESS;
               }
            }

            int var10 = this.target.getX();
            int var4 = this.target.getY();
            byte var5 = 6;
            if (this.tick == 0) {
               VoidWizardWaveProjectile var6 = new VoidWizardWaveProjectile(var1, var1.getX(), var1.getY(), var10, var4, VoidWizard.waveProjectile);
               var6.setLevel(var1.getLevel());
               var6.moveDist(20.0);
               var1.addProjectile(var6);
               var1.getLevel().entityManager.projectiles.add(var6);
               var1.playBoltSoundAbility.runAndSend(1.0F, 1.0F);
            } else {
               Point2D.Float var7;
               Point2D.Float var8;
               VoidWizardWaveProjectile var9;
               int var11;
               if (this.tick == var5) {
                  for(var11 = -1; var11 <= 1; ++var11) {
                     if (var11 != 0) {
                        var7 = GameMath.normalize((float)(var1.getX() - var10), (float)(var1.getY() - var4));
                        var8 = GameMath.getPerpendicularPoint((float)var10, (float)var4, (float)(var11 * 100), var7);
                        var9 = new VoidWizardWaveProjectile(var1, var1.getX(), var1.getY(), (int)var8.x, (int)var8.y, VoidWizard.waveProjectile);
                        var9.setLevel(var1.getLevel());
                        var9.moveDist(20.0);
                        var1.addProjectile(var9);
                        var1.getLevel().entityManager.projectiles.add(var9);
                     }
                  }

                  var1.playBoltSoundAbility.runAndSend(1.0F, 1.0F);
               } else if (this.tick == var5 * 2) {
                  for(var11 = -1; var11 <= 1; ++var11) {
                     if (var11 != 0) {
                        var7 = GameMath.normalize((float)(var1.getX() - var10), (float)(var1.getY() - var4));
                        var8 = GameMath.getPerpendicularPoint((float)var10, (float)var4, (float)(var11 * 200), var7);
                        var9 = new VoidWizardWaveProjectile(var1, var1.getX(), var1.getY(), (int)var8.x, (int)var8.y, VoidWizard.waveProjectile);
                        var9.setLevel(var1.getLevel());
                        var9.moveDist(20.0);
                        var1.addProjectile(var9);
                        var1.getLevel().entityManager.projectiles.add(var9);
                     }
                  }

                  var1.playBoltSoundAbility.runAndSend(1.0F, 1.0F);
               }
            }

            ++this.tick;
            return this.tick > var5 * 3 ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.target = null;
            this.tick = 0;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (VoidWizard)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((VoidWizard)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((VoidWizard)var1, var2);
         }
      }
   }

   public static class TeleportParticle extends Particle {
      private VoidWizard owner;
      private Point animSprite;
      private int dir;

      public TeleportParticle(Level var1, int var2, int var3, VoidWizard var4) {
         super(var1, (float)var2, (float)var3, 0.0F, 0.0F, 1000L);
         this.owner = var4;
         this.animSprite = var4.getAnimSprite(var2, var3, this.dir);
         this.dir = var4.dir;
      }

      public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
         float var9 = this.getLifeCyclePercent();
         if (!this.removed()) {
            GameLight var10 = var5.getLightLevel(this);
            int var11 = this.getX() - var7.getX() - 22 - 10;
            int var12 = this.getY() - var7.getY() - 44 - 7;
            float var13 = Math.abs(var9 - 1.0F);
            final DrawOptions var14 = (new HumanDrawOptions(var5, MobRegistry.Textures.voidWizard)).sprite(this.animSprite).dir(this.dir).light(var10).alpha(var13).pos(var11, var12);
            var1.add(new EntityDrawable(this) {
               public void draw(TickManager var1) {
                  var14.draw();
               }
            });
         }
      }
   }
}
