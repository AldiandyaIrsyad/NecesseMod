package necesse.entity.levelEvent;

import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.manager.ObjectDestroyedListenerEntityComponent;
import necesse.entity.manager.OnMobSpawnedListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class ExtractionIncursionEvent extends IncursionLevelEvent implements ObjectDestroyedListenerEntityComponent, MobBuffsEntityComponent, LevelBuffsEntityComponent, OnMobSpawnedListenerEntityComponent {
   public int startObjects = -1;
   public int doneAtRemainingObjects = -1;
   public int remainingObjects = -1;
   public int mobSpawnsGraceTime = 3000;
   public long lastObjectDestroyedTime;
   public int startMobSpawns = 20;
   public int maxMobsSpawns = 180;
   public int currentMobsSpawned = 0;

   public ExtractionIncursionEvent() {
   }

   public ExtractionIncursionEvent(String var1) {
      super(var1);
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("startObjects", this.startObjects);
      var1.addInt("doneAtRemainingObjects", this.doneAtRemainingObjects);
      var1.addInt("currentMobsSpawned", this.currentMobsSpawned);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.startObjects = var1.getInt("startObjects", this.startObjects, false);
      this.doneAtRemainingObjects = var1.getInt("doneAtRemainingObjects", this.doneAtRemainingObjects, false);
      this.currentMobsSpawned = var1.getInt("currentMobsSpawned", this.currentMobsSpawned, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.startObjects);
      var1.putNextInt(this.doneAtRemainingObjects);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startObjects = var1.getNextInt();
      this.doneAtRemainingObjects = var1.getNextInt();
   }

   public void setupUpdatePacket(PacketWriter var1) {
      super.setupUpdatePacket(var1);
      var1.putNextInt(this.remainingObjects);
      var1.putNextInt(this.currentMobsSpawned);
   }

   public void applyUpdatePacket(PacketReader var1) {
      super.applyUpdatePacket(var1);
      this.remainingObjects = var1.getNextInt();
      this.currentMobsSpawned = var1.getNextInt();
   }

   public void init() {
      super.init();
      if (!this.isClient()) {
         this.remainingObjects = 0;

         for(int var1 = 0; var1 < this.level.width; ++var1) {
            for(int var2 = 0; var2 < this.level.height; ++var2) {
               if (this.level.getObject(var1, var2).isIncursionExtractionObject) {
                  ++this.remainingObjects;
               }
            }
         }

         if (this.startObjects == -1 || this.doneAtRemainingObjects == -1) {
            this.startObjects = this.remainingObjects;
            this.doneAtRemainingObjects = this.startObjects / 5;
         }
      }

   }

   public void onObjectDestroyed(GameObject var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      if (var1.isIncursionExtractionObject) {
         this.lastObjectDestroyedTime = this.getTime();
         --this.remainingObjects;
         this.isDirty = true;
      }

   }

   public void onMobSpawned(Mob var1) {
      if (var1.isHostile) {
         ++this.currentMobsSpawned;
         this.isDirty = true;
      }

   }

   public float getPercentProgress() {
      return 1.0F - GameMath.limit((float)(this.remainingObjects - this.doneAtRemainingObjects) / (float)(this.startObjects - this.doneAtRemainingObjects), 0.0F, 1.0F);
   }

   public Stream<ModifierValue<?>> getLevelModifiers() {
      return !this.isDone && !this.isFighting && !this.bossPortalSpawned ? Stream.empty() : Stream.of(new ModifierValue(LevelModifiers.ENEMIES_RETREATING, true));
   }

   public Stream<ModifierValue<?>> getLevelModifiers(Mob var1) {
      if (!this.isDone && !this.isFighting && !this.bossPortalSpawned) {
         if (var1.isPlayer) {
            float var2 = (float)Math.pow((double)this.getPercentProgress(), 2.0);
            int var3 = this.startMobSpawns + Math.round((float)this.maxMobsSpawns * var2);
            ModifierValue var4 = new ModifierValue(BuffModifiers.MOB_SPAWN_RATE, 0.0F);
            if (this.currentMobsSpawned >= var3 || this.lastObjectDestroyedTime + (long)this.mobSpawnsGraceTime >= this.getTime()) {
               var4 = var4.max(0.0F, 1000000);
            }

            return Stream.of(var4, (new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD)).min(150));
         }

         if (var1.isHostile) {
            return Stream.of(new ModifierValue(BuffModifiers.CHASER_RANGE, 0.5F + this.getPercentProgress() / 2.0F));
         }
      } else if (var1.isPlayer) {
         return Stream.of((new ModifierValue(BuffModifiers.MOB_SPAWN_RATE, 0.0F)).max(0.0F, 1000000));
      }

      return Stream.empty();
   }

   public boolean isObjectiveDone() {
      return this.remainingObjects <= this.doneAtRemainingObjects;
   }

   public int getObjectiveCurrent() {
      return this.startObjects - this.remainingObjects;
   }

   public int getObjectiveMax() {
      return this.startObjects - this.doneAtRemainingObjects;
   }
}
