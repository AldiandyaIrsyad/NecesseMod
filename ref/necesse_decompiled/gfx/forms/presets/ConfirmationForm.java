package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.gameFont.FontOptions;

public class ConfirmationForm extends Form {
   protected FormLocalTextButton confirm;
   protected FormLocalTextButton back;
   protected FormContentBox content;
   public int padding;
   public final int maxHeight;
   protected Runnable confirmEvent;
   protected Runnable backEvent;
   private GameMessage confirmText;
   private long confirmCooldownTime;
   private int confirmSecondsLeft;

   public ConfirmationForm(String var1, int var2, int var3) {
      super(var1, var2, var3);
      this.padding = 10;
      this.confirmCooldownTime = -1L;
      this.confirmSecondsLeft = -1;
      this.maxHeight = var3;
      this.confirm = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, var3 - 40, var2 / 2 - 6));
      this.confirm.onClicked((var1x) -> {
         this.submitConfirmEvent();
      });
      this.back = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "backbutton", var2 / 2 + 2, var3 - 40, var2 / 2 - 6));
      this.back.onClicked((var1x) -> {
         this.submitBackEvent();
      });
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, 0, var2, var3 - 40));
   }

   public ConfirmationForm(String var1) {
      this(var1, 400, 400);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void setupConfirmation(Consumer<FormContentBox> var1, GameMessage var2, GameMessage var3, Runnable var4, Runnable var5) {
      this.content.setWidth(this.getWidth());
      this.content.setContentBox(new Rectangle(0, 0, this.getWidth(), this.maxHeight - 40));
      this.confirm.setWidth(this.getWidth() / 2 - 6);
      this.back.setWidth(this.getWidth() / 2 - 6);
      this.back.setX(this.getWidth() / 2);
      this.content.clearComponents();
      var1.accept(this.content);
      this.confirmText = var2;
      this.confirm.setActive(true);
      this.confirm.setLocalization(var2);
      this.back.setLocalization(var3);
      this.confirmEvent = var4;
      this.backEvent = var5;
      this.updateHeight();
      this.prioritizeConfirm();
   }

   public final void setupConfirmation(Consumer<FormContentBox> var1, Runnable var2, Runnable var3) {
      this.setupConfirmation((Consumer)var1, new LocalMessage("ui", "confirmbutton"), new LocalMessage("ui", "backbutton"), var2, var3);
   }

   public void setupConfirmation(GameMessage var1, GameMessage var2, GameMessage var3, Runnable var4, Runnable var5) {
      this.setupConfirmation((var2x) -> {
         var2x.addComponent(new FormLocalLabel(var1, new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
      }, var2, var3, var4, var5);
   }

   public final void setupConfirmation(GameMessage var1, Runnable var2, Runnable var3) {
      this.setupConfirmation((GameMessage)var1, new LocalMessage("ui", "confirmbutton"), new LocalMessage("ui", "backbutton"), var2, var3);
   }

   public void submitConfirmEvent() {
      if (this.confirmEvent != null) {
         this.confirmEvent.run();
      }

   }

   public void submitBackEvent() {
      if (this.backEvent != null) {
         this.backEvent.run();
      }

   }

   public void updateHeight() {
      Rectangle var1 = this.content.getContentBoxToFitComponents();
      var1.x = 0;
      var1.height += var1.y;
      var1.y = 0;
      var1.height += this.padding;
      var1.width = this.getWidth();
      this.content.setContentBox(var1);
      byte var2 = 40;
      this.setHeight(Math.min(var1.height, this.maxHeight - var2) + var2);
      this.content.setHeight(this.getHeight() - var2);
      this.confirm.setY(this.getHeight() - 40);
      this.back.setY(this.getHeight() - 40);
      this.onWindowResized();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.tickCooldown();
      super.draw(var1, var2, var3);
   }

   private void tickCooldown() {
      if (this.confirmCooldownTime != -1L) {
         long var1 = this.confirmCooldownTime - System.currentTimeMillis();
         if (var1 <= 0L) {
            if (this.confirmSecondsLeft > 0) {
               this.confirm.setLocalization(this.confirmText);
            }

            this.confirm.setActive(true);
            this.confirmCooldownTime = -1L;
            this.confirmSecondsLeft = -1;
         } else {
            int var3 = (int)Math.ceil((double)((float)var1 / 1000.0F));
            if (var3 != this.confirmSecondsLeft) {
               this.confirmSecondsLeft = var3;
               GameMessageBuilder var4 = (new GameMessageBuilder()).append(this.confirmText).append(" (" + this.confirmSecondsLeft + ")");
               this.confirm.setLocalization(var4);
            }
         }
      }

   }

   public void startConfirmCooldown(int var1, boolean var2) {
      this.confirmCooldownTime = System.currentTimeMillis() + (long)var1;
      if (var2) {
         this.confirmSecondsLeft = (int)Math.ceil((double)((float)var1 / 1000.0F));
         GameMessageBuilder var3 = (new GameMessageBuilder()).append(this.confirmText).append(" (" + this.confirmSecondsLeft + ")");
         this.confirm.setLocalization(var3);
      } else {
         this.confirmSecondsLeft = -1;
         this.confirm.setLocalization(this.confirmText);
      }

      this.confirm.setActive(false);
   }

   public void prioritizeConfirm() {
      this.prioritizeControllerFocus(new ControllerFocusHandler[]{this.confirm});
   }

   public void prioritizeBack() {
      this.prioritizeControllerFocus(new ControllerFocusHandler[]{this.back});
   }
}
