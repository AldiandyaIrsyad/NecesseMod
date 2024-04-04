package necesse.entity.mobs;

import necesse.engine.DifficultyBasedGetter;
import necesse.engine.GameDifficulty;
import necesse.engine.ProtectedDifficultyBasedGetter;

public class MaxHealthGetter extends DifficultyBasedGetter<Integer> {
   public MaxHealthGetter() {
   }

   public MaxHealthGetter(int var1) {
      this.set(GameDifficulty.CLASSIC, var1);
   }

   public MaxHealthGetter(int var1, int var2, int var3, int var4, int var5) {
      this.set(GameDifficulty.CASUAL, var1);
      this.set(GameDifficulty.ADVENTURE, var2);
      this.set(GameDifficulty.CLASSIC, var3);
      this.set(GameDifficulty.HARD, var4);
      this.set(GameDifficulty.BRUTAL, var5);
   }

   public MaxHealthGetter set(GameDifficulty var1, Integer var2) {
      super.set(var1, var2);
      return this;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public DifficultyBasedGetter set(GameDifficulty var1, Object var2) {
      return this.set(var1, (Integer)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ProtectedDifficultyBasedGetter set(GameDifficulty var1, Object var2) {
      return this.set(var1, (Integer)var2);
   }
}
