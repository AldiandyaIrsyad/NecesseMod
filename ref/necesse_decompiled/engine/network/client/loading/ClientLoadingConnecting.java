package necesse.engine.network.client.loading;

import necesse.engine.GameCache;
import necesse.engine.GameLog;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketConnectRequest;
import necesse.engine.network.packet.PacketRequestPassword;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPasswordInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontOptions;

public class ClientLoadingConnecting extends ClientLoadingPhase {
   private FormSwitcher switcher;
   private NoticeForm connecting;
   private Form passwordForm;
   private FormPasswordInput passwordInput;
   private boolean waitingForPass;
   private boolean rememberPass = true;
   private String password = "";

   public ClientLoadingConnecting(ClientLoading var1) {
      super(var1, false);
   }

   public FormResizeWrapper start() {
      if (this.client.getLocalServer() != null) {
         this.password = this.client.getLocalServer().settings.password;
      }

      this.waitingForPass = false;
      this.switcher = new FormSwitcher();
      this.connecting = (NoticeForm)this.switcher.addComponent(new NoticeForm("connecting", 400, 120));
      this.connecting.setupNotice((GameMessage)(new LocalMessage("loading", "connecting")), new LocalMessage("ui", "connectcancel"));
      this.connecting.onContinue(this::cancelConnection);
      this.passwordForm = (Form)this.switcher.addComponent(new Form("password", 320, 120));
      this.passwordForm.addComponent(new FormLocalLabel("ui", "passwordreq", new FontOptions(16), 0, this.passwordForm.getWidth() / 2, 5));
      this.passwordInput = (FormPasswordInput)this.passwordForm.addComponent(new FormPasswordInput(4, 20, FormInputSize.SIZE_32_TO_40, this.passwordForm.getWidth() - 8, 50));
      ((FormLocalCheckBox)this.passwordForm.addComponent(new FormLocalCheckBox("ui", "rememberpass", 10, 65, this.rememberPass))).onClicked((var1x) -> {
         this.rememberPass = ((FormCheckBox)var1x.from).checked;
      });
      this.passwordInput.onSubmit((var1x) -> {
         if (!this.passwordInput.getText().isEmpty() && (var1x.event.getID() == 257 || var1x.event.getID() == 335)) {
            this.submitPassword();
         }

      });
      FormLocalTextButton var1 = (FormLocalTextButton)this.passwordForm.addComponent(new FormLocalTextButton("ui", "connectserver", 4, this.passwordForm.getHeight() - 40, this.passwordForm.getWidth() / 2 - 6));
      var1.onClicked((var1x) -> {
         this.submitPassword();
      });
      this.passwordInput.onChange((var2) -> {
         var1.setActive(!this.passwordInput.getText().isEmpty());
      });
      var1.setActive(false);
      ((FormLocalTextButton)this.passwordForm.addComponent(new FormLocalTextButton("ui", "connectcancel", this.passwordForm.getWidth() / 2 + 2, this.passwordForm.getHeight() - 40, this.passwordForm.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.cancelConnection();
      });
      this.switcher.makeCurrent(this.connecting);
      return new FormResizeWrapper(this.switcher, () -> {
         this.connecting.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
         this.passwordForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      });
   }

   public GameMessage getLoadingMessage() {
      return (GameMessage)(this.waitingForPass ? new StaticMessage("WAITING_PASSWORD") : new LocalMessage("loading", "connecting"));
   }

   private void submitPassword() {
      this.password = this.passwordInput.getText();
      this.client.network.sendPacket(new PacketConnectRequest(this.password));
      this.waitingForPass = false;
      this.switcher.makeCurrent(this.connecting);
   }

   public void tick() {
      if (!this.isWaiting() && !this.waitingForPass) {
         this.client.network.sendPacket(new PacketConnectRequest(this.password));
         this.setWait(2500);
      }
   }

   public void submitApprovedPacket(PacketConnectApproved var1) {
      this.markDone();
   }

   public void submitRequestPasswordPacket(PacketRequestPassword var1) {
      if (this.switcher != null && !this.switcher.isDisposed()) {
         if (GameCache.cacheFileExists(this.loading.getClientCachePath(var1.worldUniqueID, "Password"))) {
            byte[] var2 = GameCache.getBytes(this.loading.getClientCachePath(var1.worldUniqueID, "Password"));
            if (var2 != null && var2.length > 0) {
               this.password = new String(var2);
               this.client.network.sendPacket(new PacketConnectRequest(this.password));
               return;
            }
         }

         this.waitingForPass = true;
         this.switcher.makeCurrent(this.passwordForm);
         this.passwordInput.setText("");
         this.passwordInput.setTyping(true);
      } else {
         GameLog.warn.println("Received request password packet on wrong loading phase");
      }

   }

   public void submitWrongPassword(long var1) {
      this.password = "";
      GameCache.removeCache(this.loading.getClientCachePath(var1, "Password"));
      GameLog.debug.println("Wiped password cache");
   }

   public void end() {
      if (this.rememberPass && !this.password.isEmpty()) {
         GameCache.cacheBytes(this.password.getBytes(), this.loading.getClientCachePath("Password"));
         GameLog.debug.println("Saved password bytes");
      }

   }
}
