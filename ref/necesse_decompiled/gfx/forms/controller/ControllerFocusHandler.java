package necesse.gfx.forms.controller;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.ui.HUD;

public interface ControllerFocusHandler {
   void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3);

   boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4);

   default void onControllerFocused(ControllerFocus var1) {
   }

   default void onControllerUnfocused(ControllerFocus var1) {
   }

   default void frameTickControllerFocus(TickManager var1, ControllerFocus var2) {
   }

   default void drawControllerFocus(ControllerFocus var1) {
      Rectangle var2 = var1.boundingBox;
      byte var3 = 5;
      var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
      HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsColor, true, var2).draw();
   }

   default Point getControllerTooltipAndFloatMenuPoint(ControllerFocus var1) {
      return null;
   }

   default int getControllerFocusHashcode() {
      return this.hashCode();
   }
}
