package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;

public interface SettlementToolHandler {
   default boolean onLeftClick(Point var1) {
      return false;
   }

   default DrawOptions getLeftClickDraw(Point var1, Point var2) {
      return null;
   }

   default boolean onLeftClickSelection(Point var1, Point var2, Rectangle var3) {
      return false;
   }

   default DrawOptions getLeftClickSelectionDraw(Point var1, Point var2, Rectangle var3) {
      return null;
   }

   default boolean onRightClick(Point var1) {
      return false;
   }

   default DrawOptions getRightClickDraw(Point var1, Point var2) {
      return null;
   }

   default boolean onRightClickSelection(Point var1, Point var2, Rectangle var3) {
      return false;
   }

   default DrawOptions getRightClickSelectionDraw(Point var1, Point var2, Rectangle var3) {
      return null;
   }

   default boolean onHover(Point var1, Consumer<ListGameTooltips> var2, Consumer<Screen.CURSOR> var3) {
      return false;
   }
}
