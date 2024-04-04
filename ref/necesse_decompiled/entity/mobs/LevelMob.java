package necesse.entity.mobs;

import java.util.function.Consumer;
import necesse.engine.util.GameUtils;
import necesse.level.maps.Level;

public class LevelMob<T extends Mob> {
   public int uniqueID;
   private T mob;

   public LevelMob(int var1) {
      this.uniqueID = var1;
   }

   public LevelMob(T var1) {
      this.uniqueID = var1.getUniqueID();
      this.mob = var1;
   }

   public LevelMob() {
      this(-1);
   }

   public T get(Level var1) {
      Mob var2;
      if (this.uniqueID == -1) {
         if (this.mob != null) {
            var2 = this.mob;
            this.mob = null;
            this.onMobChanged(var2, this.mob);
         }
      } else {
         if (this.mob != null && this.mob.getLevel() != var1) {
            var1 = this.onMobChangedLevel(this.mob, var1);
         }

         if (this.mob == null || this.mob.getUniqueID() != this.uniqueID || this.mob.getLevel() != var1 || this.mob.removed()) {
            var2 = this.mob;
            this.mob = null;

            try {
               this.mob = GameUtils.getLevelMob(this.uniqueID, var1, false);
               if (this.mob != null) {
                  this.onMobChanged(var2, this.mob);
               }
            } catch (ClassCastException var4) {
               this.mob = null;
            }
         }
      }

      return this.mob;
   }

   public void computeIfPresent(Level var1, Consumer<T> var2) {
      Mob var3 = this.get(var1);
      if (var3 != null) {
         var2.accept(var3);
      }

   }

   public void onMobChanged(T var1, T var2) {
   }

   public Level onMobChangedLevel(T var1, Level var2) {
      return var2;
   }
}
