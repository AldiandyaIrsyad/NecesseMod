package necesse.entity.mobs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.DifficultyBasedGetter;
import necesse.engine.GameDifficulty;
import necesse.engine.world.WorldSettings;

public class MobDifficultyChanges {
   private final Mob mob;
   private HashMap<GameDifficulty, HashMap<String, MobDifficultyChange>> difficulties = new HashMap();
   private boolean locked;
   private GameDifficulty lastDifficulty;
   protected int mobStartHealth;

   public MobDifficultyChanges(Mob var1) {
      this.lastDifficulty = GameDifficulty.CLASSIC;
      this.mob = var1;
   }

   public void init() {
      WorldSettings var1 = this.mob.getWorldSettings();
      if (var1 != null) {
         this.lastDifficulty = var1.difficulty;
      }

      this.forceRunChanges(this.lastDifficulty);
      this.locked = true;
   }

   public void tick() {
      WorldSettings var1 = this.mob.getWorldSettings();
      if (var1 != null && this.lastDifficulty != var1.difficulty) {
         this.forceRunChanges(var1.difficulty);
         this.lastDifficulty = var1.difficulty;
      }

   }

   protected void forceRunChanges(GameDifficulty var1) {
      HashMap var2 = (HashMap)this.difficulties.get(var1);
      if (var2 != null) {
         Iterator var3 = var2.values().iterator();

         while(var3.hasNext()) {
            MobDifficultyChange var4 = (MobDifficultyChange)var3.next();
            var4.run(!this.locked, this.lastDifficulty);
         }
      }

   }

   public boolean setChange(GameDifficulty var1, String var2, MobDifficultyChange var3) {
      if (this.locked) {
         throw new IllegalStateException("Mob difficulty changes must be registered in construction");
      } else {
         AtomicBoolean var4 = new AtomicBoolean();
         this.difficulties.compute(var1, (var3x, var4x) -> {
            if (var4x == null) {
               var4x = new HashMap();
            }

            var4.set(var4x.put(var2, var3) != null);
            return var4x;
         });
         return var4.get();
      }
   }

   public boolean removeChange(GameDifficulty var1, String var2) {
      if (this.locked) {
         throw new IllegalStateException("Mob difficulty changes must be registered in construction");
      } else {
         HashMap var3 = (HashMap)this.difficulties.get(var1);
         if (var3 != null) {
            MobDifficultyChange var4 = (MobDifficultyChange)var3.remove(var2);
            if (var3.isEmpty()) {
               this.difficulties.remove(var1);
            }

            return var4 != null;
         } else {
            return false;
         }
      }
   }

   public boolean clearChange(GameDifficulty var1) {
      if (this.locked) {
         throw new IllegalStateException("Mob difficulty changes must be registered in construction");
      } else {
         return this.difficulties.remove(var1) != null;
      }
   }

   public boolean hasChange(GameDifficulty var1, String var2) {
      HashMap var3 = (HashMap)this.difficulties.get(var1);
      if (var3 != null) {
         return var3.get(var2) != null;
      } else {
         return false;
      }
   }

   public boolean hasChanges(GameDifficulty var1) {
      HashMap var2 = (HashMap)this.difficulties.get(var1);
      return var2 != null && !var2.isEmpty();
   }

   public boolean setServerChange(GameDifficulty var1, String var2, MobDifficultyChange var3) {
      return this.setChange(var1, var2, (var2x, var3x) -> {
         if (this.mob.isServer() || this.mob.getLevel() != null && !this.mob.getLevel().isLoadingComplete()) {
            var3.run(var2x, var3x);
         }

      });
   }

   public boolean setClientChange(GameDifficulty var1, String var2, MobDifficultyChange var3) {
      return this.setChange(var1, var2, (var2x, var3x) -> {
         if (this.mob.isClient()) {
            var3.run(var2x, var3x);
         }
      });
   }

   public void setMaxHealth(GameDifficulty var1, int var2) {
      if (!this.mob.isClient()) {
         if (var1 == GameDifficulty.CLASSIC) {
            if (!this.locked) {
               this.mob.setMaxHealth(var2);
               this.mob.setHealth(var2);
            }
         } else if (!this.hasChange(GameDifficulty.CLASSIC, "serverMaxHealth")) {
            this.setMaxHealth(GameDifficulty.CLASSIC, this.mob.getMaxHealthFlat());
         }
      }

      this.mobStartHealth = this.mob.getMaxHealthFlat();
      this.setServerChange(var1, "serverMaxHealth", (var2x, var3) -> {
         if (var2x && var3 != GameDifficulty.CLASSIC) {
            this.mobStartHealth = this.mob.getMaxHealthFlat();
         }

         int var4 = this.mob.getMaxHealthFlat();
         if (this.mobStartHealth == var4 && var4 != var2) {
            float var5 = this.mob.getHealthPercent();
            this.mob.setMaxHealth(var2);
            this.mob.setHealth(Math.max(1, (int)((float)this.mob.getMaxHealth() * var5)));
            if (!var2x) {
               this.mob.sendHealthPacket(true);
            }

            this.mobStartHealth = var2;
         }

      });
   }

   public void setMaxHealth(int var1, int var2, int var3, int var4, int var5) {
      this.setMaxHealth(GameDifficulty.CASUAL, var1);
      this.setMaxHealth(GameDifficulty.ADVENTURE, var2);
      this.setMaxHealth(GameDifficulty.CLASSIC, var3);
      this.setMaxHealth(GameDifficulty.HARD, var4);
      this.setMaxHealth(GameDifficulty.BRUTAL, var5);
   }

   public void setMaxHealth(DifficultyBasedGetter<Integer> var1) {
      this.setMaxHealth(GameDifficulty.CASUAL, (Integer)var1.get(GameDifficulty.CASUAL));
      this.setMaxHealth(GameDifficulty.ADVENTURE, (Integer)var1.get(GameDifficulty.ADVENTURE));
      this.setMaxHealth(GameDifficulty.CLASSIC, (Integer)var1.get(GameDifficulty.CLASSIC));
      this.setMaxHealth(GameDifficulty.HARD, (Integer)var1.get(GameDifficulty.HARD));
      this.setMaxHealth(GameDifficulty.BRUTAL, (Integer)var1.get(GameDifficulty.BRUTAL));
   }

   @FunctionalInterface
   public interface MobDifficultyChange {
      void run(boolean var1, GameDifficulty var2);
   }
}
