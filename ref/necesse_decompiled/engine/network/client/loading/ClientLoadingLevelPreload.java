package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.FormResizeWrapper;

public class ClientLoadingLevelPreload extends ClientLoadingAutoPhase {
   public ClientLoadingLevelPreload(ClientLoading var1) {
      super(var1, true);
   }

   public GameMessage getLoadingMessage() {
      return new LocalMessage("loading", "connectmap", new Object[]{"percent", (int)(this.client.levelManager.loading().getPercentPreloaded() * 100.0F)});
   }

   public FormResizeWrapper start() {
      this.client.levelManager.loading().start(this.client.getPlayer());
      return super.start();
   }

   public void tick() {
      if (this.client.levelManager.loading().isPreloadingDone()) {
         this.markDone();
      } else {
         this.setWait(50);
      }

   }

   public void end() {
      this.client.levelManager.finishUp();
   }
}
