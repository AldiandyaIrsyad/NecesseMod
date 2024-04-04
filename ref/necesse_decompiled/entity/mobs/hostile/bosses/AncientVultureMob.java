package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
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
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobHealthScaling;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.BossNearbyBuff;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;
import necesse.entity.mobs.mobMovement.MobMovementRelative;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.AncientVultureProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AncientVultureMob extends FlyingBossMob {
   public static RotationLootItem uniqueDrops = RotationLootItem.privateLootRotation(new LootItem("vulturestaff"), new LootItem("vulturesburst"), new LootItem("vulturestalon"));
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItem(0.1F, "vulturemask"), new ChanceLootItem(0.2F, "beatdownvinyl")});
   public static LootTable privateLootTable;
   protected MobHealthScaling scaling = new MobHealthScaling(this);
   public LinkedList<Mob> spawnedMobs = new LinkedList();
   public static GameDamage collisionDamage;
   public static GameDamage projectileDamage;
   public static GameDamage hatchlingCollision;
   public static GameDamage hatchlingProjectile;
   public static MaxHealthGetter MAX_HEALTH;
   public final EmptyMobAbility flickSoundAbility;
   public final EmptyMobAbility popSoundAbility;

   public AncientVultureMob() {
      super(100);
      this.difficultyChanges.setMaxHealth(MAX_HEALTH);
      this.moveAccuracy = 60;
      this.setSpeed(150.0F);
      this.setArmor(20);
      this.setFriction(1.0F);
      this.setKnockbackModifier(0.0F);
      this.collision = new Rectangle(-55, -90, 110, 110);
      this.hitBox = new Rectangle(-55, -90, 110, 110);
      this.selectBox = new Rectangle(-75, -110, 150, 130);
      this.flickSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (AncientVultureMob.this.isClient()) {
               Screen.playSound(GameResources.flick, SoundEffect.effect(AncientVultureMob.this).pitch(0.8F));
            }

         }
      });
      this.popSoundAbility = (EmptyMobAbility)this.registerAbility(new EmptyMobAbility() {
         protected void run() {
            if (AncientVultureMob.this.isClient()) {
               Screen.playSound(GameResources.pop, SoundEffect.effect(AncientVultureMob.this).volume(0.3F).pitch(0.5F).falloffDistance(1400));
            }

         }
      });
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
      this.ai = new BehaviourTreeAI(this, new AncientVultureAI());
      if (this.isClient()) {
         Screen.playSound(GameResources.roar, SoundEffect.globalEffect().pitch(1.3F));
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

   public void setFacingDir(float var1, float var2) {
      if (var1 < 0.0F) {
         this.dir = 0;
      } else if (var1 > 0.0F) {
         this.dir = 1;
      }

   }

   public void clientTick() {
      super.clientTick();
      Screen.setMusic(MusicRegistry.AncientVulturesFeast, Screen.MusicPriority.EVENT, 1.5F);
      Screen.registerMobHealthStatusBar(this);
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(130.0F + var1 * 90.0F);
   }

   public void serverTick() {
      super.serverTick();
      this.scaling.serverTick();
      BossNearbyBuff.applyAround(this);
      float var1 = Math.abs((float)this.getHealth() / (float)this.getMaxHealth() - 1.0F);
      this.setSpeed(130.0F + var1 * 90.0F);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 5; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), MobRegistry.Textures.ancientVulture, 4 + var3, 10, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   protected void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 160;
      int var12 = var8.getDrawY(var6) - 200;
      long var13 = var4.getWorldEntity().getTime() % 350L;
      byte var15;
      if (var13 < 100L) {
         var15 = 0;
      } else if (var13 < 200L) {
         var15 = 1;
      } else if (var13 < 300L) {
         var15 = 2;
      } else {
         var15 = 3;
      }

      float var16 = Math.min(30.0F, this.dx / 5.0F);
      TextureDrawOptionsEnd var17 = MobRegistry.Textures.ancientVulture.initDraw().sprite(var15, 0, 320).light(var10).mirror(this.dir == 0, false).rotate(var16, 160, 100).pos(var11, var12);
      TextureDrawOptions var18 = this.getShadowDrawOptions(var5, var6, var10, var8);
      var3.add((var2x) -> {
         var18.draw();
         var17.draw();
      });
   }

   protected TextureDrawOptions getShadowDrawOptions(int var1, int var2, GameLight var3, GameCamera var4) {
      GameTexture var5 = MobRegistry.Textures.ancientVulture_shadow;
      int var6 = var4.getDrawX(var1) - var5.getWidth() / 2;
      int var7 = var4.getDrawY(var2) - var5.getHeight() / 2 + 10;
      return var5.initDraw().light(var3).pos(var6, var7);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      super.drawOnMap(var1, var2, var3);
      int var4 = var2 - 32;
      int var5 = var3 - 16;
      MobRegistry.Textures.ancientVulture.initDraw().sprite(0, 5, 128, 64).size(64, 32).mirror(this.dir == 0, false).draw(var4, var5);
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-21, -16, 40, 32);
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
      collisionDamage = new GameDamage(43.0F);
      projectileDamage = new GameDamage(47.0F);
      hatchlingCollision = new GameDamage(30.0F);
      hatchlingProjectile = new GameDamage(35.0F);
      MAX_HEALTH = new MaxHealthGetter(4000, 5500, 6500, 7500, 9000);
   }

   public class AncientVultureAI<T extends AncientVultureMob> extends AINode<T> {
      private int removeCounter;
      private Mob currentTarget;
      private long findNewTargetTime;
      private ArrayList<AncientVultureAI<T>.AttackRotation> attackRotations = new ArrayList();
      private int currentRotation;

      public AncientVultureAI() {
         this.attackRotations.add(new FlyAroundAttackRotation());
         this.attackRotations.add(new SimpleProjectileAttackRotation());
         this.attackRotations.add(new PlopEggsAttackRotation());
         this.currentRotation = 0;
      }

      protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
         var3.onEvent("refreshBossDespawn", (var1x) -> {
            this.removeCounter = 0;
         });
         if (var2.isServer()) {
            ((AttackRotation)this.attackRotations.get(this.currentRotation)).start();
         }

         var3.onRemoved((var1x) -> {
            AncientVultureMob.this.spawnedMobs.forEach(Mob::remove);
         });
      }

      public void init(T var1, Blackboard<T> var2) {
      }

      public AINodeResult tick(T var1, Blackboard<T> var2) {
         this.tickTargets();
         if (this.currentTarget != null) {
            this.removeCounter = 0;
            if (((AttackRotation)this.attackRotations.get(this.currentRotation)).isOver()) {
               this.currentRotation = (this.currentRotation + 1) % this.attackRotations.size();
               ((AttackRotation)this.attackRotations.get(this.currentRotation)).start();
            }

            ((AttackRotation)this.attackRotations.get(this.currentRotation)).tick();
         } else {
            AncientVultureMob.this.stopMoving();
            ++this.removeCounter;
            if (this.removeCounter > 100) {
               AncientVultureMob.this.remove();
            }
         }

         return AINodeResult.SUCCESS;
      }

      public void tickTargets() {
         if (this.currentTarget != null) {
            if (this.currentTarget.isSamePlace(this.mob()) && !this.currentTarget.removed()) {
               if (AncientVultureMob.this.getWorldEntity().getTime() <= this.findNewTargetTime) {
                  this.refreshTarget();
               }
            } else {
               this.refreshTarget();
            }
         } else {
            this.refreshTarget();
         }

      }

      public void refreshTarget() {
         this.currentTarget = (Mob)GameUtils.streamServerClients(AncientVultureMob.this.getLevel()).map((var0) -> {
            return var0.playerMob;
         }).min(Comparator.comparingInt((var1) -> {
            return (int)AncientVultureMob.this.getDistance(var1);
         })).orElse((Object)null);
         this.findNewTargetTime = AncientVultureMob.this.getWorldEntity().getTime() + 5000L;
      }

      // $FF: synthetic method
      // $FF: bridge method
      public AINodeResult tick(Mob var1, Blackboard var2) {
         return this.tick((AncientVultureMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public void init(Mob var1, Blackboard var2) {
         this.init((AncientVultureMob)var1, var2);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
         this.onRootSet(var1, (AncientVultureMob)var2, var3);
      }

      private class FlyAroundAttackRotation extends AncientVultureAI<T>.AttackRotation {
         private final Point[] positionRotation;
         private int currentPoint;
         private int timer;

         private FlyAroundAttackRotation() {
            super(null);
            this.positionRotation = new Point[]{new Point(260, 260), new Point(-260, -180), new Point(260, -260), new Point(-260, 180)};
         }

         public void start() {
            this.timer = 0;
         }

         public void tick() {
            ++this.timer;
            if (AncientVultureMob.this.hasArrivedAtTarget()) {
               this.currentPoint = (this.currentPoint + 1) % this.positionRotation.length;
            }

            Point var1 = this.positionRotation[this.currentPoint];
            AncientVultureMob.this.setMovement(new MobMovementRelative(AncientVultureAI.this.currentTarget, (float)var1.x, (float)var1.y));
         }

         public boolean isOver() {
            float var1 = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
            return (float)this.timer > 20.0F * (5.0F + var1 * 10.0F);
         }

         // $FF: synthetic method
         FlyAroundAttackRotation(Object var2) {
            this();
         }
      }

      private class SimpleProjectileAttackRotation extends AncientVultureAI<T>.AttackRotation {
         private int timer;

         private SimpleProjectileAttackRotation() {
            super(null);
         }

         public void start() {
            this.timer = 0;
         }

         public void tick() {
            ++this.timer;
            AncientVultureMob.this.setMovement(new MobMovementRelative(AncientVultureAI.this.currentTarget, 0.0F, -200.0F));
            float var1 = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
            byte var2;
            if (var1 < 0.15F) {
               var2 = 4;
            } else if (var1 < 0.4F) {
               var2 = 3;
            } else if (var1 < 0.7F) {
               var2 = 2;
            } else {
               var2 = 1;
            }

            if (this.timer % (20 / var2) == 0) {
               AncientVultureMob.this.flickSoundAbility.runAndSend();
               AncientVultureMob.this.getLevel().entityManager.projectiles.add(new AncientVultureProjectile(AncientVultureAI.this.mob(), AncientVultureMob.this.getX(), AncientVultureMob.this.getY(), AncientVultureAI.this.currentTarget.getX(), AncientVultureAI.this.currentTarget.getY(), AncientVultureMob.projectileDamage));
            }

         }

         public boolean isOver() {
            float var1 = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
            return (float)this.timer > 20.0F * (3.0F + var1 * 7.0F);
         }

         // $FF: synthetic method
         SimpleProjectileAttackRotation(Object var2) {
            this();
         }
      }

      private class PlopEggsAttackRotation extends AncientVultureAI<T>.AttackRotation {
         private int timer;
         private float buffer;

         private PlopEggsAttackRotation() {
            super(null);
         }

         public void start() {
            this.timer = 0;
            this.buffer = 0.0F;
            this.findNewPosition();
         }

         public void tick() {
            ++this.timer;
            float var1 = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
            byte var2;
            if (var1 < 0.08F) {
               var2 = 1;
            } else if (var1 < 0.3F) {
               var2 = 2;
            } else if (var1 < 0.5F) {
               var2 = 3;
            } else if (var1 < 0.7F) {
               var2 = 4;
            } else {
               var2 = 5;
            }

            long var3 = GameUtils.streamServerClients(AncientVultureMob.this.getLevel()).filter((var1x) -> {
               return !var1x.isDead() && AncientVultureMob.this.getDistance(var1x.playerMob) < 1280.0F;
            }).count();
            float var5 = Math.min(1.0F + (float)(var3 - 1L) / 2.0F, 4.0F);
            this.buffer += 1.0F / (float)var2 / 20.0F * var5;
            if (this.buffer > 1.0F) {
               --this.buffer;
               AncientVultureMob.this.popSoundAbility.runAndSend();
               AncientVultureEggMob var6 = new AncientVultureEggMob(AncientVultureMob.this);
               AncientVultureMob.this.getLevel().entityManager.addMob(var6, (float)AncientVultureMob.this.getX(), (float)AncientVultureMob.this.getY());
               AncientVultureMob.this.spawnedMobs.removeIf(Entity::removed);
               AncientVultureMob.this.spawnedMobs.add(var6);
            }

            if (AncientVultureMob.this.hasArrivedAtTarget()) {
               this.findNewPosition();
            }

         }

         public boolean isOver() {
            float var1 = (float)AncientVultureMob.this.getHealth() / (float)AncientVultureMob.this.getMaxHealth();
            return (float)this.timer > 20.0F * (7.0F + var1 * 13.0F);
         }

         private void findNewPosition() {
            if (AncientVultureAI.this.currentTarget == null) {
               AncientVultureMob.this.setMovement(new MobMovementLevelPos(AncientVultureMob.this.x, AncientVultureMob.this.y));
            } else {
               float var1 = (float)GameRandom.globalRandom.nextInt(360);
               float var2 = (float)Math.cos(Math.toRadians((double)var1));
               float var3 = (float)Math.sin(Math.toRadians((double)var1));
               float var4 = 300.0F;
               AncientVultureMob.this.setMovement(new MobMovementRelative(AncientVultureAI.this.currentTarget, var2 * var4, var3 * var4));
            }

         }

         // $FF: synthetic method
         PlopEggsAttackRotation(Object var2) {
            this();
         }
      }

      private abstract class AttackRotation {
         private AttackRotation() {
         }

         public abstract void start();

         public abstract void tick();

         public abstract boolean isOver();

         // $FF: synthetic method
         AttackRotation(Object var2) {
            this();
         }
      }
   }
}
