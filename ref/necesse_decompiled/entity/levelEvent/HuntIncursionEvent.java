package necesse.entity.levelEvent;

import java.util.HashSet;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.manager.MobDeathListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class HuntIncursionEvent extends IncursionLevelEvent implements MobDeathListenerEntityComponent, MobBuffsEntityComponent, LevelBuffsEntityComponent {
   public int progress;
   public int max;

   public HuntIncursionEvent() {
   }

   public HuntIncursionEvent(String var1, int var2, int var3) {
      super(var1);
      this.bossStringID = var1;
      this.progress = var2;
      this.max = var3;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("progress", this.progress);
      var1.addInt("max", this.max);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.progress = var1.getInt("progress", this.progress, false);
      this.max = var1.getInt("max", this.max, false);
   }

   public void setupUpdatePacket(PacketWriter var1) {
      super.setupUpdatePacket(var1);
      var1.putNextInt(this.progress);
      var1.putNextInt(this.max);
   }

   public void applyUpdatePacket(PacketReader var1) {
      super.applyUpdatePacket(var1);
      this.progress = var1.getNextInt();
      this.max = var1.getNextInt();
   }

   public void onLevelMobDied(Mob var1, Attacker var2, HashSet<Attacker> var3) {
      if (var1.isHostile && var1.countKillStat()) {
         ++this.progress;
         this.isDirty = true;
      }

   }

   public Stream<ModifierValue<?>> getLevelModifiers() {
      return !this.isDone && !this.isFighting && !this.bossPortalSpawned ? Stream.empty() : Stream.of(new ModifierValue(LevelModifiers.ENEMIES_RETREATING, true));
   }

   public float getPercentProgress() {
      return GameMath.limit((float)this.progress / (float)this.max, 0.0F, 1.0F);
   }

   public Stream<ModifierValue<?>> getLevelModifiers(Mob var1) {
      if (!this.isDone && !this.isFighting && !this.bossPortalSpawned) {
         if (var1.isPlayer) {
            return Stream.of(new ModifierValue(BuffModifiers.MOB_SPAWN_RATE, 1.0F + this.getPercentProgress()), (new ModifierValue(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD)).min(150));
         }

         if (var1.isHostile) {
            return Stream.of(new ModifierValue(BuffModifiers.CHASER_RANGE, this.getPercentProgress()));
         }
      } else if (var1.isPlayer) {
         return Stream.of((new ModifierValue(BuffModifiers.MOB_SPAWN_RATE, 0.0F)).max(0.0F, 1000000));
      }

      return Stream.empty();
   }

   public boolean isObjectiveDone() {
      return this.progress >= this.max;
   }

   public int getObjectiveCurrent() {
      return this.progress;
   }

   public int getObjectiveMax() {
      return this.max;
   }
}
