package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.QueenBeeAI;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class QueenBeeMob extends FriendlyMob {
   public static int minMigrationTime = 120000;
   public static int maxMigrationTime = 240000;
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItem("queenbee")});
   public Point migrationApiary = null;
   public long migrateTime;
   public HashSet<Integer> honeyBeeUniqueIDs = new HashSet();
   public long deathTime;

   public QueenBeeMob() {
      super(1);
      this.setSpeed(10.0F);
      this.setFriction(2.0F);
      this.setSwimSpeed(1.0F);
      this.collision = new Rectangle(-7, -5, 14, 10);
      this.hitBox = new Rectangle(-12, -14, 24, 24);
      this.selectBox = new Rectangle(-16, -28, 32, 34);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.migrationApiary != null) {
         var1.addPoint("migrationApiary", this.migrationApiary);
         var1.addLong("migrateTime", this.migrateTime);
      }

      var1.addIntArray("honeyBeeUniqueIDs", this.honeyBeeUniqueIDs.stream().mapToInt((var0) -> {
         return var0;
      }).toArray());
      if (this.deathTime != 0L) {
         var1.addLong("deathTime", this.deathTime);
      }

   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.migrationApiary = var1.getPoint("migrationApiary", this.migrationApiary, false);
      if (this.migrationApiary != null) {
         this.migrateTime = var1.getLong("migrateTime", this.migrateTime, false);
      }

      int[] var2 = var1.getIntArray("honeyBeeUniqueIDs", new int[0]);
      this.honeyBeeUniqueIDs.clear();
      int[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         this.honeyBeeUniqueIDs.add(var6);
      }

      this.deathTime = var1.getLong("deathTime", this.deathTime, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      if (this.migrationApiary != null) {
         var1.putNextBoolean(true);
         var1.putNextInt(this.migrationApiary.x);
         var1.putNextInt(this.migrationApiary.y);
         var1.putNextLong(this.migrateTime);
      } else {
         var1.putNextBoolean(false);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      if (var1.getNextBoolean()) {
         int var2 = var1.getNextInt();
         int var3 = var1.getNextInt();
         this.migrationApiary = new Point(var2, var3);
         this.migrateTime = var1.getNextLong();
      } else {
         this.migrationApiary = null;
      }

   }

   public void init() {
      super.init();
      this.ai = new BehaviourTreeAI(this, new QueenBeeAI(8000));
   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public void serverTick() {
      super.serverTick();
      if (this.migrationApiary == null) {
         if (this.deathTime == 0L) {
            this.deathTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(HoneyBeeMob.minLostBeeDeathTime, HoneyBeeMob.maxLostBeeDeathTime);
         }

         if (this.deathTime <= this.getTime()) {
            this.remove();
         }
      }

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
      TextureDrawOptionsEnd var17 = MobRegistry.Textures.queenBee.shadow.initDraw().sprite(0, this.dir, 32).light(var10).pos(var11, var12);
      var2.add((var1x) -> {
         var17.draw();
      });
      float var18 = GameUtils.getBobbing(var14, var13);
      var12 -= 10;
      var12 = (int)((float)var12 + var18 * 5.0F);
      final TextureDrawOptionsEnd var19 = MobRegistry.Textures.queenBee.body.initDraw().sprite(var16.x, var16.y, 32).light(var10).pos(var11, var12);
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            var19.draw();
         }
      });
   }

   public Point getAnimSprite(int var1, int var2, int var3) {
      long var4 = this.getTime();
      var4 += (long)(new GameRandom((long)this.getUniqueID())).nextInt(200);
      return new Point(GameUtils.getAnim(var4, 2, 200), var3);
   }

   public void setMigrationApiary(int var1, int var2) {
      this.migrationApiary = new Point(var1, var2);
      this.migrateTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(minMigrationTime, maxMigrationTime);
      this.deathTime = 0L;
   }

   public boolean shouldMigrate() {
      return this.migrationApiary != null && this.migrateTime <= this.getTime();
   }

   public void clearMigrationApiary() {
      this.migrationApiary = null;
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
}
