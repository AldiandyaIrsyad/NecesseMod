package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.reports.FeedbackData;
import necesse.reports.ReportUtils;

public abstract class FeedbackForm extends FormSwitcher {
   private static long nextFeedbackTime;
   public String state;
   public int maxTextBoxWidth;
   public int maxTextBoxHeight;
   public Form inputForm;
   protected FormTextBox textBox;
   protected FormLocalTextButton sendButton;

   public FeedbackForm(String var1, int var2, int var3) {
      this.state = var1;
      this.maxTextBoxWidth = var2;
      this.maxTextBoxHeight = var3;
      int var4 = GameMath.limit(Screen.getHudWidth() - 100, Math.min(300, var2), var2);
      this.inputForm = (Form)this.addComponent(new Form("feedbackinput", var4, 100));
      this.updateInputForm();
      this.onWindowResized();
      this.makeCurrent(this.inputForm);
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            FeedbackForm.this.updateInputForm();
            FeedbackForm.this.onWindowResized();
         }

         public boolean isDisposed() {
            return FeedbackForm.this.isDisposed();
         }
      });
   }

   private void updateInputForm() {
      this.inputForm.clearComponents();
      int var1 = GameMath.limit(Screen.getHudHeight() - 150, Math.min(100, this.maxTextBoxHeight), this.maxTextBoxHeight);
      FormFlow var2 = new FormFlow(5);
      this.inputForm.addComponent(new FormLocalLabel("ui", "givefeedback", new FontOptions(20), 0, this.inputForm.getWidth() / 2, var2.next(30)));
      FormContentBox var3 = (FormContentBox)this.inputForm.addComponent(new FormContentBox(4, var2.next(var1) + 4, this.inputForm.getWidth() - 8, var1 - 8, GameBackground.textBox));
      this.textBox = (FormTextBox)var3.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, var3.getMinContentWidth(), 40, 1000));
      this.textBox.allowTyping = true;
      this.textBox.setEmptyTextSpace(new Rectangle(var3.getX(), var3.getY(), var3.getWidth(), var3.getHeight()));
      this.textBox.onChange((var2x) -> {
         Rectangle var3x = var3.getContentBoxToFitComponents();
         var3.setContentBox(var3x);
         var3.scrollToFit(this.textBox.getCaretBoundingBox());
      });
      this.textBox.onCaretMove((var2x) -> {
         if (!var2x.causedByMouse) {
            var3.scrollToFit(this.textBox.getCaretBoundingBox());
         }

      });
      var2.next(5);
      this.inputForm.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "givefeedbacktip", new FontOptions(16), 0, this.inputForm.getWidth() / 2, 0, this.inputForm.getWidth()), 5));
      int var4 = var2.next(40);
      this.sendButton = (FormLocalTextButton)this.inputForm.addComponent(new FormLocalTextButton("ui", "sendfeedback", 4, var4, this.inputForm.getWidth() / 2 - 6));
      this.sendButton.onClicked((var1x) -> {
         String var2 = this.textBox.getText();
         if (!var2.isEmpty()) {
            this.sendFeedback(new FeedbackData(this.state), var2);
         }

      });
      ((FormLocalTextButton)this.inputForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.inputForm.getWidth() / 2 + 2, var4, this.inputForm.getWidth() / 2 - 6))).onClicked((var1x) -> {
         this.textBox.setTyping(false);
         this.backPressed();
      });
      this.inputForm.setHeight(var2.next());
   }

   private void sendFeedback(FeedbackData var1, String var2) {
      AtomicBoolean var3 = new AtomicBoolean(false);
      Thread var4 = new Thread(() -> {
         String var4 = ReportUtils.sendFeedback(var1, var2);
         if (!var3.get()) {
            if (var4 == null) {
               nextFeedbackTime = System.currentTimeMillis() + 30000L;
               NoticeForm var5 = (NoticeForm)this.addComponent(new NoticeForm("feedbackthanks", 300, 400), (var1x, var2x) -> {
                  if (!var2x) {
                     this.removeComponent(var1x);
                  }

               });
               var5.setupNotice((GameMessage)(new LocalMessage("ui", "sendreportthanks")), new LocalMessage("ui", "continuebutton"));
               var5.onContinue(() -> {
                  this.makeCurrent(this.inputForm);
                  this.textBox.setTyping(false);
                  this.backPressed();
               });
               this.textBox.setText("");
               this.makeCurrent(var5);
            } else {
               ConfirmationForm var6 = (ConfirmationForm)this.addComponent(new ConfirmationForm("feedbackerror", 300, 400), (var1x, var2x) -> {
                  if (!var2x) {
                     this.removeComponent(var1x);
                  }

               });
               var6.setupConfirmation((GameMessage)(new StaticMessage(var4)), new LocalMessage("ui", "sendreportretry"), new LocalMessage("ui", "backbutton"), () -> {
                  this.sendFeedback(var1, var2);
               }, () -> {
                  this.makeCurrent(this.inputForm);
               });
               this.makeCurrent(var6);
            }
         }

      });
      NoticeForm var5 = (NoticeForm)this.addComponent(new NoticeForm("feedbackwait", 300, 400), (var1x, var2x) -> {
         if (!var2x) {
            this.removeComponent(var1x);
         }

      });
      var5.setupNotice((GameMessage)(new LocalMessage("ui", "sendingfeedback")), new LocalMessage("ui", "cancelbutton"));
      var5.setButtonCooldown(200);
      var5.onContinue(() -> {
         var3.set(true);
         var4.interrupt();
         this.makeCurrent(this.inputForm);
      });
      this.makeCurrent(var5);
      var4.start();
   }

   public void startTyping() {
      this.textBox.setTyping(true);
   }

   public abstract void backPressed();

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isCurrent(this.inputForm)) {
         long var4 = nextFeedbackTime - System.currentTimeMillis();
         if (var4 < 0L) {
            this.sendButton.setActive(!this.textBox.getText().isEmpty());
            this.sendButton.setLocalTooltip((GameMessage)null);
         } else {
            this.sendButton.setActive(false);
            this.sendButton.setLocalTooltip(new LocalMessage("ui", "sendfeedbackwaittime", "time", GameUtils.formatSeconds((long)((int)Math.ceil((double)var4 / 1000.0)))));
         }
      }

      super.draw(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.inputForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void submitEscapeEvent(InputEvent var1) {
      if (this.isCurrent(this.inputForm)) {
         this.textBox.setTyping(false);
         this.backPressed();
         var1.use();
      }

   }
}
