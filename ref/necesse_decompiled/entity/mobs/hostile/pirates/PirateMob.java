package necesse.entity.mobs.hostile.pirates;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.HumanTexture;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.TargetedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PirateAITree;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public abstract class PirateMob extends HostileMob {
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new ChanceLootItemList(0.05F, new LootItemInterface[]{new OneOfLootItems(new LootItem("cutlass"), new LootItemInterface[]{new LootItemList(new LootItemInterface[]{new LootItem("flintlock"), LootItem.between("simplebullet", 40, 80)})})}), LootItem.between("coin", 10, 50)});
   private boolean rangedAttack;
   private long shootTime;
   private Mob shootTarget;
   private int normalAnimTime;
   public int meleeDamage = 30;
   public int shootDamage = 40;
   public Point baseTile;
   public final TargetedMobAbility startShootingAbility;
   public final CoordinateMobAbility shootAbility;

   public PirateMob(int var1) {
      super(var1);
      this.attackCooldown = 500;
      this.normalAnimTime = 400;
      this.attackAnimTime = this.normalAnimTime;
      this.setSpeed(35.0F);
      this.setSwimSpeed(1.0F);
      this.setFriction(3.0F);
      this.setArmor(20);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.canDespawn = false;
      this.baseTile = null;
      this.startShootingAbility = (TargetedMobAbility)this.registerAbility(new TargetedMobAbility() {
         protected void run(Mob var1) {
            short var2 = 1500;
            PirateMob.this.attackAnimTime = var2 + 500;
            PirateMob.this.attackCooldown = var2;
            PirateMob.this.shootTime = PirateMob.this.getWorldEntity().getTime() + (long)var2;
            PirateMob.this.rangedAttack = true;
            PirateMob.this.shootTarget = var1;
            PirateMob.this.startAttackCooldown();
            if (var1 != null) {
               PirateMob.this.showAttack(var1.getX(), var1.getY(), false);
            } else {
               PirateMob.this.showAttack(PirateMob.this.getX() + 100, PirateMob.this.getY(), false);
            }

         }
      });
      this.shootAbility = (CoordinateMobAbility)this.registerAbility(new CoordinateMobAbility() {
         protected void run(int var1, int var2) {
            PirateMob.this.rangedAttack = true;
            PirateMob.this.attackAnimTime = PirateMob.this.normalAnimTime;
            PirateMob.this.shootAbilityProjectile(var1, var2);
         }
      });
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.baseTile != null) {
         var1.addPoint("baseTile", this.baseTile);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      if (var1.hasLoadDataByName("baseTile")) {
         this.baseTile = var1.getPoint("baseTile", (Point)null);
      }

   }

   public void init() {
      super.init();
      this.setupAI();
   }

   public void setupAI() {
      if (this.baseTile == null || this.baseTile.x == 0 && this.baseTile.y == 0) {
         this.baseTile = new Point(this.getX() / 32, this.getY() / 32);
      }

      this.ai = new BehaviourTreeAI(this, new PirateAITree(544, 5000, 40, 640, 60000));
   }

   public PathDoorOption getPathDoorOption() {
      return this.getLevel() != null ? this.getLevel().regionManager.CAN_OPEN_DOORS_OPTIONS : null;
   }

   public void clientTick() {
      super.clientTick();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   public void serverTick() {
      super.serverTick();
      this.tickShooting();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   private void tickShooting() {
      if (this.shootTime != 0L && this.getWorldEntity().getTime() > this.shootTime) {
         if (this.isServer() && this.shootTarget != null && this.isSamePlace(this.shootTarget) && !this.getLevel().collides((Line2D)(new Line2D.Float(this.x, this.y, this.shootTarget.x, this.shootTarget.y)), (CollisionFilter)(new CollisionFilter()).projectileCollision())) {
            this.shootAbility.runAndSend(this.shootTarget.getX(), this.shootTarget.getY());
         }

         this.shootTime = 0L;
      }

   }

   public LootTable getLootTable() {
      return this.isSummoned ? new LootTable() : lootTable;
   }

   public void spawnDeathParticles(float var1, float var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), this.getPirateTexture().body, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void superAddDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      HumanDrawOptions var14 = (new HumanDrawOptions(var4, this.getPirateTexture())).sprite(var13).dir(this.dir).light(var10);
      if (this.inLiquid(var5, var6)) {
         var12 -= 10;
         var14.armSprite(2);
         var14.mask(MobRegistry.Textures.boat_mask[var13.y % 4], 0, -7);
      }

      float var15 = this.getAttackAnimProgress();
      if (this.isAttacking) {
         this.addAttackDraw(var14, var15);
      }

      final DrawOptions var16 = var14.pos(var11, var12);
      final TextureDrawOptionsEnd var17 = this.inLiquid(var5, var6) ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, this.dir % 4, 64).light(var10).pos(var11, var12 + 7) : null;
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            if (var17 != null) {
               var17.draw();
            }

            var16.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   protected abstract HumanTexture getPirateTexture();

   protected GameTexture getBoatTexture() {
      return MobRegistry.Textures.woodBoat;
   }

   protected void addAttackDraw(HumanDrawOptions var1, float var2) {
      if (this.rangedAttack) {
         this.addRangedAttackDraw(var1, var2);
      } else {
         this.addMeleeAttackDraw(var1, var2);
      }

   }

   protected void addRangedAttackDraw(HumanDrawOptions var1, float var2) {
      var1.itemAttack(new InventoryItem("flintlock"), (PlayerMob)null, var2, this.attackDir.x, this.attackDir.y);
   }

   protected void addMeleeAttackDraw(HumanDrawOptions var1, float var2) {
      var1.itemAttack(new InventoryItem("cutlass"), (PlayerMob)null, var2, this.attackDir.x, this.attackDir.y);
   }

   public float getAttackAnimProgress() {
      float var1 = super.getAttackAnimProgress();
      if (!this.isAttacking) {
         this.attackAnimTime = this.normalAnimTime;
         this.attackCooldown = 500;
         this.rangedAttack = false;
      }

      return var1;
   }

   protected void doWasHitLogic(MobWasHitEvent var1) {
      super.doWasHitLogic(var1);
   }

   public int getRockSpeed() {
      return 20;
   }

   public void shootAbilityProjectile(int var1, int var2) {
      if (this.isServer()) {
         Projectile var3 = ProjectileRegistry.getProjectile("handgunbullet", this.getLevel(), this.x, this.y, (float)var1, (float)var2, 500.0F, 800, new GameDamage((float)this.shootDamage), 50, this);
         var3.resetUniqueID(new GameRandom((long)(var1 + var2)));
         this.getLevel().entityManager.projectiles.add(var3);
      }

      this.showAttack(var1, var2, false);
      if (this.isClient()) {
         Screen.playSound(GameResources.handgun, SoundEffect.effect(this));
      }

   }

   public void setSummoned() {
      this.isSummoned = true;
      this.spawnLightThreshold = (new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0)).min(150, Integer.MAX_VALUE);
   }

   public MobSpawnLocation checkSpawnLocation(MobSpawnLocation var1) {
      return var1.checkNotSolidTile().checkNotLevelCollides();
   }

   public boolean shouldSave() {
      return !this.isSummoned;
   }

   public int getRespawnTime() {
      return !this.isSummoned ? super.getRespawnTime() : BossMob.getBossRespawnTime(this);
   }
}
