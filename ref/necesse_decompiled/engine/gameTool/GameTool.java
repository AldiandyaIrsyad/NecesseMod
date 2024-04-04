package necesse.engine.gameTool;

import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.gfx.gameTooltips.GameTooltips;

public interface GameTool {
   default void init() {
   }

   default void tick() {
   }

   boolean inputEvent(InputEvent var1);

   default boolean controllerEvent(ControllerEvent var1) {
      return false;
   }

   default void onPaused() {
   }

   default void onRenewed() {
   }

   default boolean canCancel() {
      return true;
   }

   default boolean forceControllerCursor() {
      return this.canCancel();
   }

   default boolean startControllerCursor() {
      return this.forceControllerCursor();
   }

   void isCancelled();

   void isCleared();

   GameTooltips getTooltips();

   default Screen.CURSOR getCursor() {
      return null;
   }
}
