package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameResources;

public class TremorsModifierLevelEvent extends LevelEvent {
   private long tremorTimeOffset;
   private long tremorInterval;
   private long tremorDuration;
   SoundPlayer tremorSound;

   public TremorsModifierLevelEvent() {
      super(true);
      this.shouldSave = true;
   }

   public TremorsModifierLevelEvent(long var1, long var3, long var5) {
      this();
      this.tremorTimeOffset = var1;
      this.tremorInterval = var3;
      this.tremorDuration = var5;
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextLong(this.tremorInterval);
      var1.putNextLong(this.tremorDuration);
      var1.putNextLong(this.tremorTimeOffset);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.tremorInterval = var1.getNextLong();
      this.tremorDuration = var1.getNextLong();
      this.tremorTimeOffset = var1.getNextLong();
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("tremorInterval", this.tremorInterval);
      var1.addLong("tremorDuration", this.tremorDuration);
      var1.addLong("tremorTimeOffset", this.tremorTimeOffset);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.tremorInterval = var1.getLong("tremorInterval", this.tremorInterval, false);
      this.tremorDuration = var1.getLong("tremorDuration", this.tremorDuration, false);
      this.tremorTimeOffset = var1.getLong("tremorTimeOffset", this.tremorTimeOffset, false);
   }

   public void serverTick() {
      super.serverTick();
      if (this.level.isServer()) {
         this.level.getServer().streamClients().filter((var1) -> {
            return !var1.isSamePlace(this.level) && var1.hasSpawned();
         }).forEach((var1) -> {
            this.removeTremorBuffs(var1.playerMob);
         });
      }

   }

   public void clientTick() {
      super.clientTick();
      PlayerMob var1 = this.getClient().getPlayer();
      long var2 = this.getCurrentTimeInProgress();
      long var4;
      if (var2 <= this.tremorInterval) {
         var4 = this.tremorInterval - var2;
         this.setTremorBuff(var1, true, (int)var4, this.getClient());
      } else {
         if (this.tremorSound == null || this.tremorSound.isDone()) {
            this.tremorSound = Screen.playSound(GameResources.rumble, SoundEffect.globalEffect().volume(1.0F));
         }

         if (this.tremorSound != null && this.tremorSound.isPlaying()) {
            this.tremorSound.refreshLooping(0.5F);
         }

         var4 = var2 - this.tremorInterval;
         long var6 = this.tremorDuration - var4;
         this.setTremorBuff(var1, false, (int)var6, this.getClient());
      }

   }

   public long getCurrentTimeInProgress() {
      long var1 = this.getFullTremorDurationRunTime();
      return Math.floorMod(this.getTime() - this.tremorTimeOffset, var1);
   }

   public long getFullTremorDurationRunTime() {
      return this.tremorInterval + this.tremorDuration;
   }

   public void removeTremorBuffs(PlayerMob var1) {
      var1.buffManager.removeBuff(BuffRegistry.TREMOR_HAPPENING, true);
      var1.buffManager.removeBuff(BuffRegistry.TREMORS_INCOMING, true);
   }

   public void setTremorBuff(PlayerMob var1, boolean var2, int var3, Client var4) {
      if (var2 && !var1.buffManager.hasBuff(BuffRegistry.TREMORS_INCOMING)) {
         if (var1.buffManager.hasBuff(BuffRegistry.TREMOR_HAPPENING)) {
            var1.buffManager.removeBuff(BuffRegistry.TREMOR_HAPPENING, true);
         }

         var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.TREMORS_INCOMING, var1, var3, (Attacker)null), true);
      } else if (!var2 && !var1.buffManager.hasBuff(BuffRegistry.TREMOR_HAPPENING)) {
         if (var1.buffManager.hasBuff(BuffRegistry.TREMORS_INCOMING)) {
            var1.buffManager.removeBuff(BuffRegistry.TREMORS_INCOMING, true);
         }

         var1.buffManager.addBuff(new ActiveBuff(BuffRegistry.TREMOR_HAPPENING, var1, var3, (Attacker)null), true);
         if (var4 != null) {
            var4.startCameraShake(var1.x, var1.y, var3, 60, 3.0F, 3.0F, false);
         }
      }

   }

   public void over() {
      super.over();
      if (this.level.isServer()) {
         this.level.getServer().streamClients().filter((var1) -> {
            return var1.isSamePlace(this.level) && var1.hasSpawned();
         }).forEach((var0) -> {
            if (var0.playerMob.buffManager.hasBuff(BuffRegistry.TREMORS_INCOMING)) {
               var0.playerMob.buffManager.removeBuff(BuffRegistry.TREMORS_INCOMING, true);
            }

            if (var0.playerMob.buffManager.hasBuff(BuffRegistry.TREMOR_HAPPENING)) {
               var0.playerMob.buffManager.removeBuff(BuffRegistry.TREMOR_HAPPENING, true);
            }

         });
      }

   }
}
