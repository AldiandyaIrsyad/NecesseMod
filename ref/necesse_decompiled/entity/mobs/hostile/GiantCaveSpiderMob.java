package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobTexture;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.CollisionShooterPlayerChaserWandererAI;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.CaveSpiderSpitProjectile;
import necesse.entity.projectile.CaveSpiderWebProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.light.GameLight;

public class GiantCaveSpiderMob extends HostileMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{LootItem.between("cavespidergland", 1, 2, MultiTextureMatItem.getGNDData(0))});
   public Variant variant;
   protected GameDamage meleeDamage;
   protected GameDamage spitDamage;

   public GiantCaveSpiderMob(Variant var1, int var2, GameDamage var3, GameDamage var4) {
      super(var2);
      this.variant = GiantCaveSpiderMob.Variant.NORMAL;
      this.variant = var1;
      this.meleeDamage = var3;
      this.spitDamage = var4;
      this.setSpeed(30.0F);
      this.setFriction(3.0F);
      this.setKnockbackModifier(0.2F);
      this.setArmor(5);
      this.attackAnimTime = 500;
      this.collision = new Rectangle(-20, -20, 40, 40);
      this.hitBox = new Rectangle(-30, -25, 60, 50);
      this.selectBox = new Rectangle(-40, -45, 80, 60);
   }

   public GiantCaveSpiderMob() {
      this(GiantCaveSpiderMob.Variant.NORMAL, 200, new GameDamage(30.0F), new GameDamage(20.0F));
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextShortUnsigned(this.variant.ordinal());
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.variant = GiantCaveSpiderMob.Variant.values()[var1.getNextShortUnsigned()];
   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new CollisionShooterPlayerChaserWandererAI<GiantCaveSpiderMob>((Supplier)null, 256, this.meleeDamage, 100, CooldownAttackTargetAINode.CooldownTimer.HAS_TARGET, 3500, 480, 40000) {
         public boolean shootAtTarget(GiantCaveSpiderMob var1, Mob var2) {
            if (var1.canAttack() && !var1.inLiquid()) {
               int var3 = (int)var1.getDistance(var2);
               var1.attack(var2.getX(), var2.getY(), false);
               Point var4 = ProjectileToolItem.controlledRangePosition(GameRandom.globalRandom, var1.getX(), var1.getY(), var2.getX(), var2.getY(), Math.max(320, var3 + 32), 32, 16);
               int var5 = (int)var1.getDistance((float)var4.x, (float)var4.y);
               if (GameRandom.globalRandom.nextBoolean()) {
                  var1.getLevel().entityManager.projectiles.add(new CaveSpiderWebProjectile(var1.x, var1.y, (float)var4.x, (float)var4.y, var1.spitDamage, var1, var5));
               } else {
                  var1.getLevel().entityManager.projectiles.add(new CaveSpiderSpitProjectile(var1.variant, var1.x, var1.y, (float)var4.x, (float)var4.y, var1.spitDamage, var1, var5));
               }

               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean shootAtTarget(Mob var1, Mob var2) {
            return this.shootAtTarget((GiantCaveSpiderMob)var1, var2);
         }
      });
   }

   public Point getPathMoveOffset() {
      return new Point(32, 32);
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("cavespider", 3);
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 10; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), ((MobTexture)this.variant.texture.get()).body, 14 + var3 / 5, var3 % 5, 48, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 48;
      int var12 = var8.getDrawY(var6) - 60;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      if (this.isAttacking) {
         var13.x = 6;
      }

      final TextureDrawOptionsEnd var14 = ((MobTexture)this.variant.texture.get()).body.initDraw().sprite(var13.x, var13.y, 96).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      TextureDrawOptionsEnd var15 = ((MobTexture)this.variant.texture.get()).shadow.initDraw().sprite(var13.x, var13.y, 96).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var15.draw();
      });
   }

   public int getRockSpeed() {
      return 15;
   }

   public float getAttackingMovementModifier() {
      return 0.0F;
   }

   public int getTileWanderPriority(TilePosition var1) {
      return var1.tileID() == TileRegistry.spiderNestID ? 1000 : super.getTileWanderPriority(var1);
   }

   public void attack(int var1, int var2, boolean var3) {
      super.attack(var1, var2, var3);
      this.setFacingDir(this.attackDir.x, this.attackDir.y);
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      this.setFacingDir(this.attackDir.x, this.attackDir.y);
      if (this.isClient()) {
         Screen.playSound(GameResources.spit, SoundEffect.effect(this));
      }

   }

   public static enum Variant {
      NORMAL(() -> {
         return MobRegistry.Textures.giantCaveSpider;
      }, new Color(160, 200, 65)),
      BLACK(() -> {
         return MobRegistry.Textures.giantSnowCaveSpider;
      }, new Color(66, 129, 195)),
      SWAMP(() -> {
         return MobRegistry.Textures.giantSwampCaveSpider;
      }, new Color(82, 126, 60));

      public Supplier<MobTexture> texture;
      public Color particleColor;

      private Variant(Supplier var3, Color var4) {
         this.texture = var3;
         this.particleColor = var4;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{NORMAL, BLACK, SWAMP};
      }
   }
}
