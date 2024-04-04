package necesse.engine;

import necesse.engine.world.WorldSettings;

public class ProtectedDifficultyBasedGetter<T> {
   protected Object[] array = new Object[GameDifficulty.values().length];
   protected boolean[] set = new boolean[GameDifficulty.values().length];

   public ProtectedDifficultyBasedGetter() {
   }

   protected ProtectedDifficultyBasedGetter<T> set(GameDifficulty var1, T var2) {
      int var3 = var1.ordinal();
      this.array[var3] = var2;
      this.set[var3] = true;
      int var4 = GameDifficulty.CLASSIC.ordinal();

      for(int var5 = 0; var5 < this.array.length; ++var5) {
         if (!this.set[var5]) {
            if (this.array[var5] == null) {
               this.array[var5] = var2;
            } else if (var3 < var4 && var5 < var3) {
               this.array[var5] = var2;
            } else if (var3 > var4 && var5 > var3) {
               this.array[var5] = var2;
            }
         }
      }

      return this;
   }

   protected T get(GameDifficulty var1) {
      return this.array[var1.ordinal()];
   }

   protected T get(WorldSettingsGetter var1) {
      if (var1 == null) {
         return this.get(GameDifficulty.CLASSIC);
      } else {
         WorldSettings var2 = var1.getWorldSettings();
         return var2 != null ? this.get(var2.difficulty) : this.get(GameDifficulty.CLASSIC);
      }
   }
}
