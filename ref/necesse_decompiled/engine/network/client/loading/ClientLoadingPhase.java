package necesse.engine.network.client.loading;

import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.state.MainMenu;
import necesse.gfx.forms.FormResizeWrapper;

public abstract class ClientLoadingPhase extends ClientLoadingUtil {
   public final boolean resetOnLevelChange;
   private boolean isDone;

   public ClientLoadingPhase(ClientLoading var1, boolean var2) {
      super(var1);
      this.resetOnLevelChange = var2;
   }

   public abstract FormResizeWrapper start();

   public abstract GameMessage getLoadingMessage();

   public abstract void tick();

   public abstract void end();

   public void reset() {
      this.isDone = false;
   }

   public final boolean isDone() {
      return this.isDone;
   }

   protected final void markDone() {
      this.isDone = true;
   }

   public final void cancelConnection() {
      if (GlobalData.getCurrentState() instanceof MainMenu) {
         ((MainMenu)GlobalData.getCurrentState()).cancelConnection();
      }

   }
}
