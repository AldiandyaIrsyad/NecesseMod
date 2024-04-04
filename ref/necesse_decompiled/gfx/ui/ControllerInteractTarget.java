package necesse.gfx.ui;

import necesse.gfx.drawOptions.DrawOptions;

public interface ControllerInteractTarget {
   void runInteract();

   DrawOptions getDrawOptions();

   void onCurrentlyFocused();
}
