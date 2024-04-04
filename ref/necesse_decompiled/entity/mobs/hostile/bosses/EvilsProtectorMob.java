package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.Screen;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
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
import necesse.entity.levelEvent.mobAbilityLevelEvent.EvilsProtectorBombAttackEvent;
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
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.IsolateRunningAINode;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageManagerNode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageSkipTo;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.IdleTimeAttackStage;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SmokePuffParticle;
import necesse.entity.projectile.EvilsProtectorAttack1Projectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.EvilsProtectorAttack2Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsProtectorMob extends BossMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("demonicbar", 12), new ChanceLootItem(0.2F, "firsttrialvinyl")});
   public static LootTable privateLootTable = new LootTable(new LootItemInterface[]{new ConditionLootItem("demonheart", (var0, var1) -> {
      ServerClient var2 = (ServerClient)LootTable.expectExtra(ServerClient.class, var1, 1);
      return var2 != null && var2.playerMob.getMaxHealthFlat() < 200 && var2.playerMob.getInv().getAmount(ItemRegistry.getItem("demonheart"), false, true, true, "have") == 0;
   }), new LootItem("forceofwind", (new GNDItemMap()).setString("enchantment", "")), RotationLootItem.privateLootRotation(new LootItem("magicfoci", (new GNDItemMap()).setString("enchantment", "")), new LootItem("rangefoci", (new GNDItemMap()).setString("enchantment", "")), new LootItem("meleefoci", (new GNDItemMap()).setString("enchantment", "")), new LootItem("summonfoci", (new GNDItemMap()).setString("enchantment", "")))});
   public static GameDamage landDamage = new GameDamage(50.0F);
   public static GameDamage bombDamage = new GameDamage(21.0F);
   public static GameDamage boltDamage = new GameDamage(18.0F);
   public static GameDamage volleyFireballDamage = new GameDamage(16.0F);
   public static GameDamage waveFireballDamage = new GameDamage(19.0F);
   public static GameDamage minionDamage = new GameDamage(15.0F);
   public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(1500, 2500, 3000, 3500, 4000);
   private static final int[] flyUpAnimFrames = new int[]{800, 700, 50, 50};
   private static final int flyUpAnimFramesTotal;
   private static final int[] flyAnimFrames;
   private static final float[] attackAnimFrameParts;
   private int[] attackAnimFrames = new int[]{200, 100, 200};
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public ArrayList<Mob> spawnedPortals = new ArrayList();
   public ArrayList<Projectile> spawnedProjectiles = new ArrayList();
   protected boolean isAttack2;
   protected boolean isFlying;
   protected boolean isFlyingUp;
   private long flyUpTime;
   protected boolean isLanding;
   protected boolean playedLandingSound;
   private long landingTime;
   public final EmptyMobAbility flyUpAbility;
   public final EmptyMobAbility fireSoundAbility;
   public final CoordinateMobAbility landAbility;
   public final IntMobAbility attackAbility;

   public EvilsProtectorMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.setArmor(10);
      this.setFriction(10.0F);
      this.collision = new Rectangle(-40, -40, 80, 60);
      this.hitBox = new Rectangle(-40, -40, 80, 60);
      this.selectBox = new Rectangle(-60, -50, 120, 70);
      this.dir = 0;
      this.attackAnimTime = Arrays.stream(this.attackAnimFrames).reduce(0, Integer::sum);
      this.setKnockbackModifier(0.0F);
      this.flyUpAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            EvilsProtectorMob.this.isFlyingUp = true;
            EvilsProtectorMob.this.flyUpTime = EvilsProtectorMob.this.getWorldEntity().getTime();
            if (EvilsProtectorMob.this.isClient()) {
               Screen.playSound(GameResources.dragonfly1, SoundEffect.effect(EvilsProtectorMob.this));
            }

         }
      });
      this.landAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            EvilsProtectorMob.this.setPos((float)var1, (float)var2, true);
            EvilsProtectorMob.this.isLanding = true;
            EvilsProtectorMob.this.landingTime = EvilsProtectorMob.this.getWorldEntity().getTime();
            EvilsProtectorMob.this.playedLandingSound = false;
         }
      });
      this.fireSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (EvilsProtectorMob.this.isClient()) {
               Screen.playSound(GameResources.magicbolt2, SoundEffect.effect(EvilsProtectorMob.this).pitch(GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F)));
            }

         }
      });
      this.attackAbility = (IntMobAbility)this.registerAbility(new IntMobAbility() {
         protected void run(int var1) {
            if (var1 <= 0) {
               var1 = 500;
            }

            EvilsProtectorMob.this.attackAnimTime = var1;

            for(int var2 = 0; var2 < EvilsProtectorMob.attackAnimFrameParts.length; ++var2) {
               EvilsProtectorMob.this.attackAnimFrames[var2] = (int)((float)EvilsProtectorMob.this.attackAnimTime * EvilsProtectorMob.attackAnimFrameParts[var2]);
            }

            EvilsProtectorMob.this.attack(0, 0, false);
         }
      });
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.isFlyingUp);
      if (this.isFlyingUp) {
         var1.putNextLong(this.flyUpTime);
      }

      var1.putNextBoolean(this.isLanding);
      if (this.isLanding) {
         var1.putNextLong(this.landingTime);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.isFlyingUp = var1.getNextBoolean();
      if (this.isFlyingUp) {
         this.flyUpTime = var1.getNextLong();
      }

      this.isLanding = var1.getNextBoolean();
      if (this.isLanding) {
         this.landingTime = var1.getNextLong();
      }

   }

   public void setupMovementPacket(PacketWriter var1) {
      super.setupMovementPacket(var1);
      var1.putNextBoolean(this.isFlying);
   }

   public void applyMovementPacket(PacketReader var1, boolean var2) {
      super.applyMovementPacket(var1, var2);
      this.isFlying = var1.getNextBoolean();
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
      this.ai = new BehaviourTreeAI(this, new BalorAINew());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(0.8F));
      } else if (this.isServer()) {
         this.isLanding = true;
         this.landingTime = this.getWorldEntity().getTime();
      }

   }

   public int getFlyingHeight() {
      return this.isFlying ? 1000 : super.getFlyingHeight();
   }

   public boolean canTakeDamage() {
      return !this.isFlying() && !this.isLanding;
   }

   public boolean canLevelInteract() {
      return !this.isFlying();
   }

   public boolean canPushMob(Mob var1) {
      return this.isFlying() ? false : super.canPushMob(var1);
   }

   public boolean isVisible() {
      return !this.isFlying();
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public int getMaxHealth() {
      return super.getMaxHealth() + (int)((float)(this.scaling == null ? 0 : this.scaling.getHealthIncrease()) * this.getMaxHealthModifier());
   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.TheFirstTrial, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      if (!this.isFlying || this.isLanding && this.playedLandingSound) {
         this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 270.0F, 0.7F);
      }

      long var1;
      if (this.isFlyingUp) {
         var1 = this.getWorldEntity().getTime() - this.flyUpTime;
         if (var1 >= (long)(flyUpAnimFramesTotal + 1000)) {
            this.isFlyingUp = false;
         } else if (!this.isFlying && var1 >= (long)flyUpAnimFramesTotal) {
            this.isFlying = true;
         }
      } else if (this.isLanding) {
         var1 = this.getWorldEntity().getTime() - this.landingTime;
         if (var1 >= 2000L) {
            this.isFlying = false;
            this.isLanding = false;
            this.playedLandingSound = false;
            short var3 = 200;
            float var4 = 360.0F / (float)var3;

            for(int var5 = 0; var5 < var3; ++var5) {
               int var6 = (int)((float)var5 * var4 + GameRandom.globalRandom.nextFloat() * var4);
               float var7 = (float)Math.sin(Math.toRadians((double)var6)) * (float)GameRandom.globalRandom.getIntBetween(50, 150);
               float var8 = (float)Math.cos(Math.toRadians((double)var6)) * (float)GameRandom.globalRandom.getIntBetween(50, 150) * 0.8F;
               this.getLevel().entityManager.addParticle(this.x, this.y, var5 % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(var7, var8, 0.8F).color(new Color(50, 50, 50)).heightMoves(0.0F, 30.0F).lifeTime(1000);
            }

            Screen.playSound(GameResources.magicbolt3, SoundEffect.effect(this));
            this.getLevel().getClient().startCameraShake(this, 400, 40, 12.0F, 12.0F, false);
         } else if (!this.playedLandingSound && var1 >= 1000L) {
            Screen.playSound(GameResources.swoosh, SoundEffect.effect(this).volume(0.6F));
            this.playedLandingSound = true;
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      long var1;
      if (this.isFlyingUp) {
         var1 = this.getWorldEntity().getTime() - this.flyUpTime;
         if (var1 >= (long)flyUpAnimFramesTotal) {
            this.isFlying = true;
            this.isFlyingUp = false;
         }
      } else if (this.isLanding) {
         var1 = this.getWorldEntity().getTime() - this.landingTime;
         if (var1 >= 2000L) {
            this.isFlying = false;
            this.isLanding = false;
            short var3 = 300;
            int var4 = var3 / 2;
            Ellipse2D.Float var5 = new Ellipse2D.Float(this.x - (float)var4, this.y - (float)var4 * 0.8F, (float)var3, (float)var3 * 0.8F);
            GameUtils.streamTargets(this, GameUtils.rangeTileBounds(this.getX(), this.getY(), 5)).filter((var2) -> {
               return var2.canBeHit(this) && var5.intersects(var2.getHitBox());
            }).forEach((var1x) -> {
               var1x.isServerHit(landDamage, (float)var1x.getX() - this.x, (float)var1x.getY() - this.y, 100.0F, this);
            });
         }
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public LootTable getPrivateLootTable() {
      return privateLootTable;
   }

   public void playHitSound() {
      float var1 = (Float)GameRandom.globalRandom.getOneOf((Object[])(0.95F, 1.0F, 1.05F));
      Screen.playSound(GameResources.npchurt, SoundEffect.effect(this).pitch(var1));
   }

   protected void playDeathSound() {
      Screen.playSound(GameResources.fadedeath3, SoundEffect.effect(this));
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      this.isAttack2 = !this.isAttack2;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.evilsProtector2, var3, 2, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void spawnRemoveParticles(float var1, float var2) {
      this.getLevel().entityManager.addParticle((Particle)(new SmokePuffParticle(this.getLevel(), (float)this.getX(), (float)this.getY(), 160, new Color(50, 50, 50))), Particle.GType.CRITICAL);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 96;
      int var12 = var8.getDrawY(var6) - 130;
      int var13 = var12;
      float var14 = 1.0F;
      float var15 = 1.0F;
      int var16 = 0;
      int var17 = 0;
      int var18 = 0;
      int var19 = 0;
      float var20 = this.getAttackAnimProgress();
      if (this.isAttacking) {
         int var21 = GameUtils.getAnim((long)(var20 * (float)this.attackAnimTime), this.attackAnimFrames);
         var16 = var21 % 3;
         var17 = (this.isAttack2 ? 2 : 1) + var21 / 3;
      }

      long var26;
      if (this.isFlyingUp) {
         var26 = this.getWorldEntity().getTime() - this.flyUpTime;
         if (var26 <= (long)(flyUpAnimFramesTotal + 1000)) {
            if (var26 < (long)flyUpAnimFramesTotal) {
               var16 = GameUtils.getAnim(var26, flyUpAnimFrames);
               var17 = 0;
               var18 = var16;
               var19 = var17;
            } else {
               long var23 = GameMath.limit(var26 - (long)flyUpAnimFramesTotal, 0L, 1000L);
               float var25 = (float)var23 / 1000.0F;
               var14 = Math.abs(var25 - 1.0F);
               var15 = var14;
               var13 = (int)((float)var12 - var25 * 500.0F);
               var16 = GameUtils.getAnimContinuous(var23, flyAnimFrames);
               var17 = 0;
               var18 = var16;
               var19 = 1;
            }
         } else {
            var15 = 0.0F;
            var14 = 0.0F;
         }
      } else if (this.isLanding) {
         var26 = this.getWorldEntity().getTime() - this.landingTime;
         if (var26 < 1000L) {
            var14 = 0.0F;
            var15 = (float)var26 / 1000.0F;
            var18 = GameUtils.getAnimContinuous(var26, flyAnimFrames);
            var19 = 1;
         } else if (var26 < 2000L) {
            float var28 = (float)(var26 - 1000L) / 1000.0F;
            var14 = var28;
            var13 = (int)((float)var12 - Math.abs(var28 - 1.0F) * 500.0F);
            var16 = GameUtils.getAnimContinuous(var26, flyAnimFrames);
            var17 = 0;
         }
      } else if (this.isFlying) {
         return;
      }

      final TextureDrawOptionsEnd var27 = MobRegistry.Textures.evilsProtector.initDraw().sprite(var16, var17, 192).light(var10).alpha(var14).pos(var11, var13);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var27.draw();
         }
      });
      TextureDrawOptionsEnd var22 = MobRegistry.Textures.evilsProtector_shadow.initDraw().sprite(var18, var19, 192).light(var10).alpha(var15).pos(var11, var12);
      var2.add((var1x) -> {
         var22.draw();
      });
   }

   public boolean shouldDrawOnMap() {
      return !this.isFlying;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 32;
      int var5 = var3 - 24;
      MobRegistry.Textures.evilsProtector2.initDraw().sprite(0, 0, 64).size(32, 32).draw(var4, var5);
      MobRegistry.Textures.evilsProtector2.initDraw().sprite(1, 0, 64).size(32, 32).draw(var4 + 32, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-23, -18, 46, 21);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName() + " " + this.getHealth() + "/" + this.getMaxHealth());
   }

   public void remove(float var1, float var2, Attacker var3, boolean var4) {
      super.remove(var1, var2, var3, var4);
      this.spawnedPortals.forEach(Mob::remove);
      this.spawnedProjectiles.forEach(Projectile::remove);
      this.spawnedPortals.clear();
      this.spawnedProjectiles.clear();
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
      flyUpAnimFramesTotal = Arrays.stream(flyUpAnimFrames).reduce(0, Integer::sum);
      flyAnimFrames = new int[]{50, 200, 100, 100};
      attackAnimFrameParts = new float[]{0.4F, 0.2F, 0.4F};
   }

   public static class BalorAINew<T extends EvilsProtectorMob> extends SelectorAINode<T> {
      private int escapeCounter;

      public BalorAINew() {
         this.addChild(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
               var3.onEvent("refreshBossDespawn", (var1x) -> {
                  BalorAINew.this.escapeCounter = 0;
               });
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               if (!var1.getLevel().getServer().world.worldEntity.isNight()) {
                  var1.remove();
                  var1.getLevel().getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "epescape")), (Level)var1.getLevel());
                  return AINodeResult.SUCCESS;
               } else {
                  ArrayList var3 = (ArrayList)GameUtils.streamServerClients(var1.getLevel()).filter((var0) -> {
                     return !var0.isDead();
                  }).map((var0) -> {
                     return var0.playerMob;
                  }).filter((var1x) -> {
                     return !var1x.removed() && var1x.getDistance(var1) < 1280.0F;
                  }).collect(Collectors.toCollection(ArrayList::new));
                  var2.put("balorTargets", var3);
                  if (var3.isEmpty()) {
                     BalorAINew.this.escapeCounter++;
                     if (BalorAINew.this.escapeCounter >= 100) {
                        var1.remove();
                     }

                     return AINodeResult.FAILURE;
                  } else {
                     BalorAINew.this.escapeCounter = 0;
                     return AINodeResult.FAILURE;
                  }
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((EvilsProtectorMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((EvilsProtectorMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (EvilsProtectorMob)var2, var3);
            }
         });
         AttackStageManagerNode var1 = new AttackStageManagerNode();
         var1.allowSkippingBack = false;
         this.addChild(new IsolateRunningAINode(var1));
         var1.addChild(new IdleTimeAttackStage(3000));
         PortalSpawningManagerNode var2 = new PortalSpawningManagerNode(30000);
         var2.addChild(new IdleTimeAttackStage(2500));
         var2.addChild(new VolleyAttackStage());
         var1.addChild(var2);
         var1.addChild(new FlyingBombAttackStage(0.75F, 0.8F, 10));
         var1.addChild(new LandAttackStage());
         var2 = new PortalSpawningManagerNode(30000);
         var2.addChild(new IdleTimeAttackStage(2000));
         var2.addChild(new VolleyAttackStage());
         var2.addChild(new IdleTimeAttackStage(500));
         var2.addChild(new HomingProjectileAttackStage());
         var1.addChild(var2);
         var1.addChild(new FlyingBombAttackStage(0.5F, 0.55F, 10));
         var1.addChild(new LandAttackStage());
         AttackStageManagerNode var3 = new AttackStageManagerNode(AINodeResult.RUNNING);
         var3.addChild(new IdleTimeAttackStage(2000));
         var3.addChild(new VolleyWaveAttackStage(-65.0F, 200.0F, 2000, 12.5F));
         var3.addChild(new IdleTimeAttackStage(300));
         var3.addChild(new HomingProjectileAttackStage());
         var1.addChild(var3);
         var1.addChild(new FlyingBombAttackStage(0.25F, 0.3F, 10));
         var1.addChild(new LandAttackStage());
         var2 = new PortalSpawningManagerNode(30000);
         var2.addChild(new IdleTimeAttackStage(1000));
         var2.addChild(new VolleyWaveAttackStage(-75.0F, 220.0F, 1500, 12.5F));
         var2.addChild(new IdleTimeAttackStage(500));
         var2.addChild(new HomingProjectileAttackStage());
         var1.addChild(var2);
         var1.addChild(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               return AINodeResult.RUNNING;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((EvilsProtectorMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((EvilsProtectorMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (EvilsProtectorMob)var2, var3);
            }
         });
      }

      public ArrayList<Mob> getAttackers(T var1) {
         return (ArrayList)var1.streamAttackers().map(Attacker::getAttackOwner).filter((var0) -> {
            return var0 != null && !var0.removed();
         }).filter((var0) -> {
            return var0.isPlayer;
         }).filter((var1x) -> {
            return var1x.isSamePlace(var1);
         }).distinct().collect(Collectors.toCollection(ArrayList::new));
      }

      public Mob getRandomAttacker(T var1, List<PlayerMob> var2) {
         ArrayList var3 = this.getAttackers(var1);
         return var3.isEmpty() ? (Mob)GameRandom.globalRandom.getOneOf(var2) : (Mob)GameRandom.globalRandom.getOneOf((List)var3);
      }

      public void findLandingArea(T var1) {
         ArrayList var2 = new ArrayList();
         var1.streamAttackers().map(Attacker::getAttackOwner).filter((var0) -> {
            return var0 != null && !var0.removed();
         }).filter((var0) -> {
            return var0.isPlayer;
         }).filter((var1x) -> {
            return var1x.isSamePlace(var1);
         }).distinct().forEach((var2x) -> {
            for(int var3 = -2; var3 <= 2; ++var3) {
               for(int var4 = -2; var4 <= 2; ++var4) {
                  int var5 = var2x.getX() + var3 * 32;
                  int var6 = var2x.getY() + var4 * 32;
                  if (!var1.collidesWith(var1.getLevel(), var5, var6)) {
                     var2.add(new Point(var5, var6));
                  }
               }
            }

         });
         Point var3;
         if (!var2.isEmpty()) {
            var3 = (Point)GameRandom.globalRandom.getOneOf((List)var2);
            var1.landAbility.runAndSend(var3.x, var3.y);
         } else {
            for(int var7 = -8; var7 < 8; ++var7) {
               for(int var4 = -8; var4 <= 8; ++var4) {
                  int var5 = var1.getX() + var7 * 32;
                  int var6 = var1.getY() + var4 * 32;
                  if (!var1.collidesWith(var1.getLevel(), var5, var6)) {
                     var2.add(new Point(var5, var6));
                  }
               }
            }

            if (!var2.isEmpty()) {
               var3 = (Point)GameRandom.globalRandom.getOneOf((List)var2);
               var1.landAbility.runAndSend(var3.x, var3.y);
            } else {
               var1.landAbility.runAndSend(var1.getX(), var1.getY());
            }
         }

      }

      public void spawnRandomGates(T var1, int var2) {
         var1.spawnedPortals.forEach(Mob::remove);
         ArrayList var3 = new ArrayList();
         EvilsPortalMob var4 = new EvilsPortalMob();

         int var5;
         for(var5 = -15; var5 <= 15; ++var5) {
            for(int var6 = -15; var6 <= 15; ++var6) {
               if (var5 < -2 || var5 > 2 || var6 < -2 || var6 > 2) {
                  int var7 = var5 + var1.getX() / 32;
                  int var8 = var6 + var1.getY() / 32;
                  if (!var1.getLevel().isSolidTile(var7, var8) && !var4.collidesWith(var1.getLevel(), var7 * 32 + 16, var8 * 32 + 16)) {
                     var3.add(new Point(var7, var8));
                  }
               }
            }
         }

         for(var5 = 0; var5 < var2 && !var3.isEmpty(); ++var5) {
            Point var9 = (Point)GameRandom.globalRandom.getOneOf((List)var3);
            var4 = new EvilsPortalMob();
            float var10002 = (float)(var9.x * 32 + 16);
            int var10003 = var9.y * 32;
            var1.getLevel().entityManager.addMob(var4, var10002, (float)(var10003 + 16));
            var1.spawnedPortals.add(var4);
            var3.remove(var9);
         }

      }

      public class PortalSpawningManagerNode extends AttackStageManagerNode<T> {
         public int portalsCooldown;
         public int portalTimer;

         public PortalSpawningManagerNode(int var2) {
            super(AINodeResult.RUNNING);
            this.portalsCooldown = var2;
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            super.onStarted(var1, var2);
            this.portalTimer = this.portalsCooldown;
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            ArrayList var3 = (ArrayList)var2.getObject(ArrayList.class, "balorTargets");
            float var4 = GameMath.limit(1.0F + (float)(var3.size() - 1) / 2.0F, 1.0F, 3.0F);
            this.portalTimer = (int)((float)this.portalTimer + 50.0F * var4);
            if (this.portalTimer >= this.portalsCooldown) {
               int var5 = GameMath.limit(2 + (var3.size() - 1) / 2, 2, 10);
               BalorAINew.this.spawnRandomGates(var1, var5);
               this.portalTimer = 0;
            }

            return super.tick(var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((EvilsProtectorMob)var1, var2);
         }
      }

      public class VolleyAttackStage extends AINode<T> {
         public VolleyAttackStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            ArrayList var3 = (ArrayList)var2.getObject(ArrayList.class, "balorTargets");
            LinkedList var4 = new LinkedList();
            Iterator var5 = var3.iterator();

            while(true) {
               float var7;
               do {
                  if (!var5.hasNext()) {
                     var1.fireSoundAbility.runAndSend();
                     var1.attackAbility.runAndSend(500);
                     return AINodeResult.SUCCESS;
                  }

                  PlayerMob var6 = (PlayerMob)var5.next();
                  var7 = Projectile.getAngleToTarget(var1.x, var1.y, (float)var6.getX(), (float)var6.getY());
               } while(!var4.stream().noneMatch((var1x) -> {
                  return Math.abs(GameMath.getAngleDifference(var7, var1x)) < 20.0F;
               }));

               float var8 = GameRandom.globalRandom.getFloatOffset(var7, 12.5F);

               for(int var9 = -1; var9 <= 1; ++var9) {
                  EvilsProtectorAttack1Projectile var10 = new EvilsProtectorAttack1Projectile(var1.x, var1.y, var8 + (float)(var9 * 10), 70.0F, 800, EvilsProtectorMob.volleyFireballDamage, var1);
                  var1.getLevel().entityManager.projectiles.add(var10);
                  var1.spawnedProjectiles.add(var10);
               }

               var4.add(var7);
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (EvilsProtectorMob)var2, var3);
         }
      }

      public class FlyingBombAttackStage extends BalorAINew<T>.FlyingAttackStage implements AttackStageSkipTo<T>, AttackStageInterface<T> {
         public float startHealthPerc;
         public float bombsPerSecond;
         public int secondsToStayFlying;
         public int landTicker;
         public float bombBuffer;
         public boolean started;

         public FlyingBombAttackStage(float var2, float var3, int var4) {
            super();
            this.startHealthPerc = var2;
            this.bombsPerSecond = var3;
            this.secondsToStayFlying = var4;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public boolean shouldSkipTo(T var1, boolean var2) {
            if (this.started) {
               return false;
            } else {
               float var3 = (float)var1.getHealth() / (float)var1.getMaxHealth();
               return var3 <= this.startHealthPerc;
            }
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.landTicker = 20 * this.secondsToStayFlying;
            this.bombBuffer = 0.0F;
            this.started = true;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tickFlying(T var1, Blackboard<T> var2) {
            this.bombBuffer += 1.0F / this.bombsPerSecond / 20.0F;
            if (this.bombBuffer >= 1.0F) {
               --this.bombBuffer;
               ArrayList var3 = BalorAINew.this.getAttackers(var1);

               for(int var4 = 0; var4 < Math.min(5, var3.size()); ++var4) {
                  int var5 = GameRandom.globalRandom.nextInt(var3.size());
                  Mob var6 = (Mob)var3.remove(var5);
                  if (var6 != null) {
                     Mob var7 = var6.getMount();
                     HashSet var8 = new HashSet();
                     var8.add(var6);

                     while(var7 != null) {
                        var8.add(var6);
                        var6 = var7;
                        var7 = var7.getMount();
                        if (var8.contains(var7)) {
                           break;
                        }
                     }

                     var1.getLevel().entityManager.addLevelEvent(new EvilsProtectorBombAttackEvent(var1, GameRandom.globalRandom.getIntOffset((int)(var6.x + var6.dx * 3.0F), 50), GameRandom.globalRandom.getIntOffset((int)(var6.y + var6.dy * 3.0F), 50), GameRandom.globalRandom, EvilsProtectorMob.bombDamage));
                  }
               }
            }

            --this.landTicker;
            if (this.landTicker <= 0) {
               BalorAINew.this.findLandingArea(var1);
               return AINodeResult.SUCCESS;
            } else {
               return AINodeResult.RUNNING;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (EvilsProtectorMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean shouldSkipTo(Mob var1, boolean var2) {
            return this.shouldSkipTo((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((EvilsProtectorMob)var1, var2);
         }
      }

      public class LandAttackStage extends BalorAINew<T>.LandedAttackStage {
         public LandAttackStage() {
            super();
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tickLanded(T var1, Blackboard<T> var2) {
            return AINodeResult.SUCCESS;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (EvilsProtectorMob)var2, var3);
         }
      }

      public class HomingProjectileAttackStage extends AINode<T> {
         protected float projectilesBuffer;

         public HomingProjectileAttackStage() {
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            ArrayList var3 = (ArrayList)var2.getObject(ArrayList.class, "balorTargets");
            int var4 = var3.size();
            this.projectilesBuffer = (float)((double)this.projectilesBuffer + Math.pow((double)(1.0F / (float)(var4 + 1)), 0.5) * (double)var4);
            if (this.projectilesBuffer >= 1.0F) {
               int var5 = (int)this.projectilesBuffer;
               this.projectilesBuffer -= (float)var5;
               ArrayList var6 = GameRandom.globalRandom.getCountOf(var5, (List)var3);
               Iterator var7 = var6.iterator();

               while(var7.hasNext()) {
                  PlayerMob var8 = (PlayerMob)var7.next();
                  EvilsProtectorAttack2Projectile var9 = new EvilsProtectorAttack2Projectile(var1.getLevel(), var1, var8, EvilsProtectorMob.boltDamage);
                  var1.getLevel().entityManager.projectiles.add(var9);
                  var1.spawnedProjectiles.add(var9);
               }

               var1.fireSoundAbility.runAndSend();
               var1.attackAbility.runAndSend(500);
            }

            return AINodeResult.SUCCESS;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (EvilsProtectorMob)var2, var3);
         }
      }

      public class VolleyWaveAttackStage extends AINode<T> implements AttackStageInterface<T> {
         public float targetStartAngleOffset;
         public float totalAngle;
         public float anglePerProjectile;
         public int totalTime;
         public boolean initial;
         public float currentAngle;
         public float remainingAngle;
         public float angleBuffer;
         public int direction;

         public VolleyWaveAttackStage(float var2, float var3, int var4, float var5) {
            this.targetStartAngleOffset = var2;
            this.totalAngle = var3;
            this.totalTime = var4;
            this.anglePerProjectile = var5;
         }

         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public void onStarted(T var1, Blackboard<T> var2) {
            this.initial = true;
            this.angleBuffer = 0.0F;
         }

         public void onEnded(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (this.initial) {
               ArrayList var3 = (ArrayList)var2.getObject(ArrayList.class, "balorTargets");
               PlayerMob var4 = (PlayerMob)GameRandom.globalRandom.getOneOf((List)var3);
               if (var4 == null) {
                  return AINodeResult.SUCCESS;
               }

               this.direction = (Integer)GameRandom.globalRandom.getOneOf((Object[])(-1, 1));
               this.currentAngle = GameMath.getAngle(new Point2D.Float(var4.x - var1.x, var4.y - var1.y)) + this.targetStartAngleOffset * (float)this.direction + 90.0F;
               this.remainingAngle = this.totalAngle;
               this.initial = false;
            }

            this.angleBuffer += this.totalAngle * 50.0F / (float)this.totalTime;

            while(this.angleBuffer >= this.anglePerProjectile) {
               this.currentAngle += this.anglePerProjectile * (float)this.direction;
               EvilsProtectorAttack1Projectile var5 = new EvilsProtectorAttack1Projectile(var1.x, var1.y, this.currentAngle, 50.0F, 800, EvilsProtectorMob.waveFireballDamage, var1);
               var1.getLevel().entityManager.projectiles.add(var5);
               var1.spawnedProjectiles.add(var5);
               this.angleBuffer -= this.anglePerProjectile;
               this.remainingAngle -= this.anglePerProjectile;
               if (this.remainingAngle <= 0.0F) {
                  break;
               }
            }

            if (!var1.isAttacking) {
               var1.attackAbility.runAndSend(300);
               var1.fireSoundAbility.runAndSend();
            }

            return this.angleBuffer >= this.remainingAngle ? AINodeResult.SUCCESS : AINodeResult.RUNNING;
         }

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void init(Mob var1, Blackboard var2) {
            this.init((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
            this.onRootSet(var1, (EvilsProtectorMob)var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEnded(Mob var1, Blackboard var2) {
            this.onEnded((EvilsProtectorMob)var1, var2);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onStarted(Mob var1, Blackboard var2) {
            this.onStarted((EvilsProtectorMob)var1, var2);
         }
      }

      public abstract class LandedAttackStage extends AINode<T> {
         public LandedAttackStage() {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (var1.isFlying) {
               if (!var1.isLanding) {
                  BalorAINew.this.findLandingArea(var1);
               }
            } else if (!var1.isFlyingUp) {
               return this.tickLanded(var1, var2);
            }

            return AINodeResult.RUNNING;
         }

         public abstract AINodeResult tickLanded(T var1, Blackboard<T> var2);

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((EvilsProtectorMob)var1, var2);
         }
      }

      public abstract class FlyingAttackStage extends AINode<T> {
         public FlyingAttackStage() {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (!var1.isFlyingUp && !var1.isFlying) {
               var1.flyUpAbility.runAndSend();
            } else if (var1.isFlying) {
               return this.tickFlying(var1, var2);
            }

            return AINodeResult.RUNNING;
         }

         public abstract AINodeResult tickFlying(T var1, Blackboard<T> var2);

         // $FF: synthetic method
         // $FF: bridge method
         public AINodeResult tick(Mob var1, Blackboard var2) {
            return this.tick((EvilsProtectorMob)var1, var2);
         }
      }
   }
}
