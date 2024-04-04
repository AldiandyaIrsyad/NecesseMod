package necesse.engine;

public class DifficultyBasedGetter<T> extends ProtectedDifficultyBasedGetter<T> {
   public DifficultyBasedGetter() {
   }

   public DifficultyBasedGetter<T> set(GameDifficulty var1, T var2) {
      super.set(var1, var2);
      return this;
   }

   public T get(GameDifficulty var1) {
      return super.get(var1);
   }

   public T get(WorldSettingsGetter var1) {
      return super.get(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public ProtectedDifficultyBasedGetter set(GameDifficulty var1, Object var2) {
      return this.set(var1, var2);
   }
}
