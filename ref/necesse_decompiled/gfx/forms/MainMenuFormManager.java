package necesse.gfx.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import necesse.engine.GameLaunch;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.MainMenuForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.forms.presets.TestCrashReportForm;

public class MainMenuFormManager extends FormManager {
   public final MainMenu mainMenu;
   public FormSwitcher switcher;
   private List<ContinueComponent> continueComponents;
   private FormResizeWrapper connecting;
   public MainMenuForm mainForm;

   public MainMenuFormManager(MainMenu var1) {
      this.mainMenu = var1;
      this.continueComponents = new ArrayList();
   }

   public void frameTick(TickManager var1) {
      super.frameTick(var1);
      this.updateActiveForms();
   }

   public void setup() {
      this.switcher = (FormSwitcher)this.addComponent(new FormSwitcher());
      this.mainForm = new MainMenuForm(this.mainMenu);
      this.switcher.addComponent(this.mainForm);
      if (GameLaunch.launchOptions.containsKey("testcrash")) {
         this.addComponent(new TestCrashReportForm());
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      if (this.connecting != null) {
         this.connecting.resizeLogic.run();
      }

      Screen.submitNextMoveEvent();
   }

   public void submitEscapeEvent(InputEvent var1) {
      if (this.connecting != null && this.switcher.isCurrent(this.connecting.component)) {
         this.cancelConnection();
      } else if (!this.continueComponents.isEmpty() && this.switcher.isCurrent((FormComponent)this.continueComponents.get(0))) {
         if (((ContinueComponent)this.continueComponents.get(0)).canContinue()) {
            ((ContinueComponent)this.continueComponents.get(0)).applyContinue();
         }
      } else if (this.switcher.isCurrent(this.mainForm)) {
         this.mainForm.submitEscapeEvent(var1);
      }

   }

   public void cancelConnection() {
      if (this.mainMenu.getClient() != null) {
         if (this.mainMenu.getClient().getLocalServer() != null) {
            this.mainMenu.getClient().getLocalServer().stop();
         }

         this.mainMenu.getClient().disconnect("Connection cancelled");
      }

      this.updateActiveForms();
   }

   private void updateActiveForms() {
      if (!this.continueComponents.isEmpty()) {
         if (!this.switcher.isCurrent((FormComponent)this.continueComponents.get(0))) {
            this.switcher.makeCurrent((FormComponent)this.continueComponents.get(0));
         }
      } else {
         Client var1 = this.mainMenu.getClient();
         if (var1 != null && !var1.isDisconnected()) {
            if (var1.loading.isDone()) {
               GlobalData.setCurrentState(new MainGame(var1));
            } else if (this.connecting != null && !this.switcher.isCurrent(this.connecting.component)) {
               this.switcher.makeCurrent(this.connecting.component);
            }
         } else if (!this.switcher.isCurrent(this.mainForm)) {
            this.switcher.makeCurrent(this.mainForm);
         }
      }

   }

   public void startConnection(Client var1) {
      this.continueComponents.forEach((var1x) -> {
         this.switcher.removeComponent((FormComponent)var1x);
      });
      this.continueComponents.clear();
      if (this.connecting == null) {
         FormResizeWrapper var2 = null;
         if (var1 != null) {
            var2 = var1.loading.getUnusedFormWrapper();
         }

         if (var2 == null) {
            NoticeForm var3 = (NoticeForm)this.switcher.addComponent(new NoticeForm("loading", 400, 120));
            var3.setupNotice((GameMessage)(new LocalMessage("loading", "loading")), new LocalMessage("ui", "connectcancel"));
            var3.onContinue(this::cancelConnection);
            var2 = new FormResizeWrapper(var3, () -> {
               var3.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
            });
         }

         this.setConnectingComponent(var2);
      }

      this.updateActiveForms();
   }

   public void setConnectingComponent(FormResizeWrapper var1) {
      if (this.connecting != null) {
         this.switcher.removeComponent(this.connecting.component);
      }

      this.connecting = var1;
      this.switcher.addComponent(var1.component);
      var1.resizeLogic.run();
      this.updateActiveForms();
   }

   public void addContinueForm(ContinueComponent var1) {
      Objects.requireNonNull(var1);
      this.switcher.addComponent((FormComponent)var1);
      this.continueComponents.add(var1);
      ((FormComponent)var1).onWindowResized();
      var1.onContinue(() -> {
         this.onContinueForm(var1);
      });
      this.updateActiveForms();
   }

   public void notice(GameMessage var1, int var2) {
      NoticeForm var3 = new NoticeForm("notice", 400, 120);
      var3.setupNotice(var1);
      var3.setButtonCooldown(var2);
      this.addContinueForm(var3);
   }

   public void notice(GameMessage var1) {
      this.notice((GameMessage)var1, 2000);
   }

   public void notice(String var1, int var2) {
      this.notice((GameMessage)(new StaticMessage(var1)), var2);
   }

   public void notice(String var1) {
      this.notice((GameMessage)(new StaticMessage(var1)));
   }

   public boolean hasContinueForm() {
      return !this.continueComponents.isEmpty();
   }

   private void onContinueForm(ContinueComponent var1) {
      this.switcher.removeComponent((FormComponent)var1);
      this.continueComponents.remove(var1);
      this.updateActiveForms();
   }
}
