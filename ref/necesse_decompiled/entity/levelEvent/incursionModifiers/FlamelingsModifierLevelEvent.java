package necesse.entity.levelEvent.incursionModifiers;

import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;

public class FlamelingsModifierLevelEvent extends LevelEvent {
   public long flamelingSpawnInterval;
   public long flamelingSpawnOffset;
   public long flamelingUptime;
   public final LevelMob<Mob> flameling;

   public FlamelingsModifierLevelEvent() {
      super(true);
      this.flameling = new LevelMob(-1);
      this.shouldSave = true;
   }

   public FlamelingsModifierLevelEvent(long var1, long var3, long var5) {
      this();
      this.flamelingSpawnInterval = var1;
      this.flamelingSpawnOffset = var3;
      this.flamelingUptime = var5;
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextLong(this.flamelingSpawnInterval);
      var1.putNextLong(this.flamelingSpawnOffset);
      var1.putNextLong(this.flamelingUptime);
      var1.putNextInt(this.flameling.uniqueID);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.flamelingSpawnInterval = var1.getNextLong();
      this.flamelingSpawnOffset = var1.getNextLong();
      this.flamelingUptime = var1.getNextLong();
      this.flameling.uniqueID = var1.getNextInt();
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("flamelingSpawnInterval", this.flamelingSpawnInterval);
      var1.addLong("flamelingSpawnOffset", this.flamelingSpawnOffset);
      var1.addLong("flamelingUptime", this.flamelingUptime);
      var1.addInt("flamelingUniqueID", this.flameling.uniqueID);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.flamelingSpawnInterval = var1.getLong("flamelingSpawnInterval", this.flamelingSpawnInterval, false);
      this.flamelingSpawnOffset = var1.getLong("flamelingSpawnOffset", this.flamelingSpawnOffset, false);
      this.flamelingUptime = var1.getLong("flamelingUptime", this.flamelingUptime, false);
      this.flameling.uniqueID = var1.getInt("flamelingUniqueID", -1, false);
   }

   public void serverTick() {
      super.serverTick();
      long var1 = this.getCurrentTimeInProgress();
      if (var1 <= this.flamelingSpawnInterval) {
         this.flameling.uniqueID = -1;
      } else if (this.flameling.uniqueID == -1) {
         Mob var3 = this.flameling.get(this.level);
         if (var3 == null || var3.removed()) {
            this.spawnFlameling();
         }
      }

   }

   public long getCurrentTimeInProgress() {
      long var1 = this.getFullFlamelingRunTime();
      return Math.floorMod(this.getTime() - this.flamelingSpawnOffset, var1);
   }

   public long getFullFlamelingRunTime() {
      return this.flamelingSpawnInterval + this.flamelingUptime;
   }

   public void spawnFlameling() {
      Mob var1 = MobRegistry.getMob("flameling", this.level);
      var1.resetUniqueID();
      GameRandom var2 = new GameRandom();
      List var3 = (List)GameUtils.streamServerClients(this.level).map((var0) -> {
         return var0.playerMob;
      }).collect(Collectors.toList());
      if (!var3.isEmpty()) {
         Mob var4 = (Mob)var2.getOneOf(var3);
         int var5 = var4.getTileX();
         int var6 = var4.getTileY();

         for(int var7 = 0; var7 <= 20; ++var7) {
            int var8 = var2.getIntBetween(4, 8) * 32;
            int var9 = var2.getIntBetween(4, 8) * 32;
            if (var2.getChance(50)) {
               var8 = -var8;
            }

            if (var2.getChance(50)) {
               var9 = -var9;
            }

            if (!var4.collidesWith(this.getLevel(), var4.getTileX() * 32 + var8 + 16, var4.getTileY() * 32 + var9 + 16)) {
               var5 = var4.getTileX() * 32 + var8 + 16;
               var6 = var4.getTileY() * 32 + var9 + 16;
               break;
            }
         }

         var1.onSpawned(var5, var6);
         this.level.entityManager.mobs.add(var1);
         this.flameling.uniqueID = var1.getUniqueID();
      }
   }
}
