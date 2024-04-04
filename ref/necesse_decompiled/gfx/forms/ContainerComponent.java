package necesse.gfx.forms;

import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.state.MainGame;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.inventory.container.Container;

public interface ContainerComponent<T extends Container> {
   void setHidden(boolean var1);

   T getContainer();

   boolean shouldOpenInventory();

   default boolean shouldCloseInventory() {
      return false;
   }

   default boolean shouldShowInventory() {
      return this.shouldOpenInventory();
   }

   default boolean shouldShowToolbar() {
      return true;
   }

   static void setPosInventory(Form var0) {
      MainGameFormManager var1 = getFormManager();
      if (var1 != null) {
         var0.setPosition(new FormRelativePosition(var1.toolbar, (var1.toolbar.getWidth() - var0.getWidth()) / 2, -var0.getHeight() - Settings.UI.formSpacing));
      } else {
         var0.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() - 75 - var0.getHeight() / 2);
      }

   }

   static void setPosFocus(Form var0) {
      MainGameFormManager var1 = getFormManager();
      if (var1 != null) {
         var0.setPosition(new FormRelativePosition(var1.inventory, (var1.inventory.getWidth() - var0.getWidth()) / 2, -var0.getHeight() - Settings.UI.formSpacing));
      }

   }

   static MainGameFormManager getFormManager() {
      return GlobalData.getCurrentState() instanceof MainGame ? ((MainGame)GlobalData.getCurrentState()).formManager : null;
   }

   default void onWindowResized() {
   }

   default void onContainerClosed() {
   }
}
