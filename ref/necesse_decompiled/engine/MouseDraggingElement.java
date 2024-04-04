package necesse.engine;

import necesse.engine.control.Input;

public interface MouseDraggingElement {
   boolean draw(int var1, int var2);

   default boolean isKeyDown(Input var1) {
      return var1.isKeyDown(-100);
   }
}
