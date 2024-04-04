package necesse.entity.mobs.friendly;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.MobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.HoneyBeeAI;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.PollinateObjectHandler;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class HoneyBeeMob extends FriendlyMob {
   public static int minStayOutOfApiaryTime = 10000;
   public static int maxStayOutOfApiaryTime = 240000;
   public static int minLostBeeDeathTime = 120000;
   public static int maxLostBeeDeathTime = 600000;
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("honeybee")});
   public Point apiaryHome = null;
   public long returnToApiaryTime;
   public long pollinateTime;
   public long deathTime;
   public final LevelMob<QueenBeeMob> followingQueen = new LevelMob<QueenBeeMob>() {
      public void onMobChanged(QueenBeeMob var1, QueenBeeMob var2) {
         super.onMobChanged(var1, var2);
         if (var2 != null) {
            var2.honeyBeeUniqueIDs.add(HoneyBeeMob.this.getUniqueID());
         }

      }

      // $FF: synthetic method
      // $FF: bridge method
      public void onMobChanged(Mob var1, Mob var2) {
         this.onMobChanged((QueenBeeMob)var1, (QueenBeeMob)var2);
      }
   };
   protected long pollinateStartTime;
   protected int pollinateTileX;
   protected int pollinateTileY;
   protected int pollinateAnimationTime;
   protected PollinateObjectHandler pollinateTarget;
   public PollinateMobAbility pollinateAbility;

   public HoneyBeeMob() {
      super(1);
      this.setSpeed(40.0F);
      this.setFriction(2.0F);
      this.setSwimSpeed(1.0F);
      this.collision = new Rectangle(-7, -5, 14, 10);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -28, 32, 34);
      this.pollinateAbility = (PollinateMobAbility)this.registerAbility(new PollinateMobAbility());
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.apiaryHome != null) {
         var1.addPoint("apiaryHome", this.apiaryHome);
         var1.addLong("returnToApiaryTime", this.returnToApiaryTime);
      }

      if (this.followingQueen != null) {
         var1.addInt("followingQueen", this.followingQueen.uniqueID);
      }

      var1.addLong("pollinateTime", this.pollinateTime);
      if (this.deathTime != 0L) {
         var1.addLong("deathTime", this.deathTime);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.apiaryHome = var1.getPoint("apiaryHome", this.apiaryHome, false);
      if (this.apiaryHome != null) {
         this.returnToApiaryTime = var1.getLong("returnToApiaryTime", this.returnToApiaryTime, false);
      }

      this.followingQueen.uniqueID = var1.getInt("followingQueen", -1, false);
      this.pollinateTime = var1.getLong("pollinateTime", this.pollinateTime, false);
      this.deathTime = var1.getLong("deathTime", this.deathTime, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      if (this.followingQueen.uniqueID != -1) {
         var1.putNextBoolean(true);
         var1.putNextInt(this.followingQueen.uniqueID);
      } else {
         var1.putNextBoolean(false);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      if (var1.getNextBoolean()) {
         this.followingQueen.uniqueID = var1.getNextInt();
      } else {
         this.followingQueen.uniqueID = -1;
      }

   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new HoneyBeeAI(8000));
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void clientTick() {
      super.clientTick();
      this.tickPollinate();
   }

   public void serverTick() {
      super.serverTick();
      QueenBeeMob var1 = (QueenBeeMob)this.followingQueen.get(this.getLevel());
      if (this.apiaryHome == null && var1 == null) {
         this.followingQueen.uniqueID = -1;
         if (this.deathTime == 0L) {
            this.deathTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(minLostBeeDeathTime, maxLostBeeDeathTime);
         }

         if (this.deathTime <= this.getTime()) {
            this.remove();
         }
      }

      this.tickPollinate();
   }

   public void tickPollinate() {
      if (this.pollinateAnimationTime > 0) {
         long var1 = this.getTime() - this.pollinateStartTime;
         if (var1 <= (long)this.pollinateAnimationTime) {
            if (GameRandom.globalRandom.nextInt(20) == 0) {
               MultiTile var3 = this.getLevel().getObject(this.pollinateTileX, this.pollinateTileY).getMultiTile(this.getLevel(), this.pollinateTileX, this.pollinateTileY);
               Point var4 = new Point(var3.getCenterXOffset() * 16, var3.getCenterYOffset() * 16);
               int var5 = 8 + GameRandom.globalRandom.nextInt(24);
               this.getLevel().entityManager.addParticle((float)(this.pollinateTileX * 32 + var4.x + GameRandom.globalRandom.nextInt(32)), (float)(this.pollinateTileY * 32 + var4.y + 32), Particle.GType.COSMETIC).color(new Color(255, 211, 58, 200)).heightMoves((float)var5, (float)(var5 + 40)).lifeTime(2000);
            }
         } else {
            this.pollinateAnimationTime = 0;
            if (!this.isClient() && this.pollinateTarget != null && this.pollinateTarget.canPollinate()) {
               this.pollinateTarget.pollinate();
            }

            if (!this.isServer()) {
               for(int var7 = 0; var7 < 5; ++var7) {
                  MultiTile var8 = this.getLevel().getObject(this.pollinateTileX, this.pollinateTileY).getMultiTile(this.getLevel(), this.pollinateTileX, this.pollinateTileY);
                  Point var9 = new Point(var8.getCenterXOffset() * 16, var8.getCenterYOffset() * 16);
                  int var6 = 8 + GameRandom.globalRandom.nextInt(24);
                  this.getLevel().entityManager.addParticle((float)(this.pollinateTileX * 32 + var9.x + GameRandom.globalRandom.nextInt(32)), (float)(this.pollinateTileY * 32 + var9.y + 32), Particle.GType.COSMETIC).color(new Color(255, 211, 58, 200)).heightMoves((float)var6, (float)(var6 + 40)).lifeTime(2000);
               }
            }
         }
      }

   }

   public boolean isPollinating() {
      return this.pollinateAnimationTime != 0;
   }

   public PathDoorOption getPathDoorOption() {
      return this.getLevel() != null ? this.getLevel().regionManager.CANNOT_PASS_DOORS_OPTIONS : null;
   }

   public int getFlyingHeight() {
      return 20;
   }

   public boolean canTakeDamage() {
      return false;
   }

   public boolean canBeTargeted(Mob var1, NetworkClient var2) {
      return true;
   }

   protected void checkCollision() {
   }

   public boolean canPushMob(Mob var1) {
      return false;
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 16;
      int var12 = var8.getDrawY(var6) - 22;
      short var13 = 1000;
      long var14 = var4.getTime();
      var14 += (long)(new GameRandom((long)this.getUniqueID())).nextInt(var13);
      Point var16 = this.getAnimSprite(var5, var6, this.dir);
      TextureDrawOptionsEnd var17 = MobRegistry.Textures.honeyBee.shadow.initDraw().sprite(0, this.dir, 32).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var17.draw();
      });
      float var18 = GameUtils.getBobbing(var14, var13);
      if (this.isPollinating()) {
         long var19 = this.getTime() - this.pollinateStartTime;
         float var21 = GameUtils.getAnimFloat(var19, this.pollinateAnimationTime);
         float var22;
         float var23;
         if (var21 < 0.2F) {
            var22 = var21 / 0.2F;
            var18 *= 1.0F - var22;
            var23 = (float)Math.cos((double)var22 * Math.PI) / 2.0F + 0.5F;
            var12 = (int)((float)var12 + (1.0F - var23) * 12.0F);
         } else if (var21 < 0.8F) {
            var18 = 0.0F;
            var12 += 12;
            var16.x = 0;
         } else {
            var22 = (var21 - 0.8F) / 0.2F;
            var18 *= var22;
            var23 = (float)Math.cos((double)var22 * Math.PI) / 2.0F + 0.5F;
            var12 = (int)((float)var12 + var23 * 12.0F);
         }
      }

      var12 -= 6;
      var12 = (int)((float)var12 + var18 * 5.0F);
      final TextureDrawOptionsEnd var24 = MobRegistry.Textures.honeyBee.body.initDraw().sprite(var16.x, var16.y, 32).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var24.draw();
         }
      });
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      long var4 = this.getTime();
      var4 += (long)(new GameRandom((long)this.getUniqueID())).nextInt(200);
      return new Point(GameUtils.getAnim(var4, 2, 200), var3);
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      if (this.apiaryHome != null) {
         ObjectEntity var3 = this.getLevel().entityManager.getObjectEntity(this.apiaryHome.x, this.apiaryHome.y);
         if (var3 instanceof AbstractBeeHiveObjectEntity) {
            AbstractBeeHiveObjectEntity var4 = (AbstractBeeHiveObjectEntity)var3;
            var4.onRoamingBeeDied(this);
         }
      }

   }

   public void setFollowingQueen(QueenBeeMob var1) {
      if (var1 != null) {
         this.followingQueen.uniqueID = var1.getUniqueID();
      } else {
         this.followingQueen.uniqueID = -1;
      }

      this.clearApiaryHome();
      this.returnToApiaryTime = 0L;
      this.deathTime = 0L;
   }

   public void setApiaryHome(int var1, int var2) {
      this.apiaryHome = new Point(var1, var2);
      this.returnToApiaryTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(minStayOutOfApiaryTime, maxStayOutOfApiaryTime);
      this.deathTime = 0L;
   }

   public void clearApiaryHome() {
      if (this.apiaryHome != null) {
         AbstractBeeHiveObjectEntity var1 = (AbstractBeeHiveObjectEntity)this.getLevel().entityManager.getObjectEntity(this.apiaryHome.x, this.apiaryHome.y, AbstractBeeHiveObjectEntity.class);
         if (var1 != null) {
            var1.onRoamingBeeLost(this);
         }
      }

      this.apiaryHome = null;
   }

   public boolean shouldReturnToApiary() {
      return this.apiaryHome != null && (this.returnToApiaryTime <= this.getTime() || this.getWorldEntity().isNight());
   }

   public boolean isHealthBarVisible() {
      return false;
   }

   protected void playDeathSound() {
   }

   protected void playHitSound() {
   }

   public void spawnDamageText(int var1, int var2, boolean var3) {
   }

   public class PollinateMobAbility extends MobAbility {
      public PollinateMobAbility() {
      }

      public void runAndSend(PollinateObjectHandler var1, int var2) {
         Packet var3 = new Packet();
         PacketWriter var4 = new PacketWriter(var3);
         var4.putNextShortUnsigned(var1.tileX);
         var4.putNextShortUnsigned(var1.tileY);
         var4.putNextShortUnsigned(var2);
         HoneyBeeMob.this.pollinateTarget = var1;
         this.runAndSendAbility(var3);
      }

      public void executePacket(PacketReader var1) {
         HoneyBeeMob.this.pollinateStartTime = HoneyBeeMob.this.getTime();
         HoneyBeeMob.this.pollinateTileX = var1.getNextShortUnsigned();
         HoneyBeeMob.this.pollinateTileY = var1.getNextShortUnsigned();
         HoneyBeeMob.this.pollinateAnimationTime = var1.getNextShortUnsigned();
      }
   }
}
