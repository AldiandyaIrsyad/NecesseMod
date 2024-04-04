package necesse.engine.network.client.loading;

import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.presets.NoticeForm;

public abstract class ClientLoadingAutoPhase extends ClientLoadingPhase {
   private NoticeForm form;

   public ClientLoadingAutoPhase(ClientLoading var1, boolean var2) {
      super(var1, var2);
   }

   public FormResizeWrapper start() {
      this.form = new NoticeForm("loading", 400, 120);
      this.form.setupNotice((GameMessage)this.getLoadingMessage(), new LocalMessage("ui", "connectcancel"));
      this.form.onContinue(this::cancelConnection);
      return new FormResizeWrapper(this.form, () -> {
         this.form.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      });
   }

   public final void updateLoadingMessage() {
      if (this.form != null && !this.form.isDisposed()) {
         this.form.setupNotice(this.getLoadingMessage());
         this.form.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      }

   }
}
