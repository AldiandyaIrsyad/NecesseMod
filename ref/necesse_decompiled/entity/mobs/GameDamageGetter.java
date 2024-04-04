package necesse.entity.mobs;

import necesse.engine.DifficultyBasedGetter;
import necesse.engine.GameDifficulty;
import necesse.engine.ProtectedDifficultyBasedGetter;

public class GameDamageGetter extends DifficultyBasedGetter<GameDamage> {
   public GameDamageGetter() {
   }

   public GameDamageGetter(int var1, int var2, int var3, int var4, int var5) {
      this.set(GameDifficulty.CASUAL, new GameDamage((float)var1));
      this.set(GameDifficulty.ADVENTURE, new GameDamage((float)var2));
      this.set(GameDifficulty.CLASSIC, new GameDamage((float)var3));
      this.set(GameDifficulty.HARD, new GameDamage((float)var4));
      this.set(GameDifficulty.BRUTAL, new GameDamage((float)var5));
   }

   public GameDamageGetter set(GameDifficulty var1, GameDamage var2) {
      super.set(var1, var2);
      return this;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public DifficultyBasedGetter set(GameDifficulty var1, Object var2) {
      return this.set(var1, (GameDamage)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ProtectedDifficultyBasedGetter set(GameDifficulty var1, Object var2) {
      return this.set(var1, (GameDamage)var2);
   }
}
