package necesse.engine.util.gameAreaSearch;

public class EmptyGameAreaSearch<T> extends GameAreaSearch<T> {
   public EmptyGameAreaSearch() {
      super(0, 0, 0, 0, 0, 0, 0);
      this.isDone = true;
   }

   protected T get(int var1, int var2) {
      return null;
   }
}
