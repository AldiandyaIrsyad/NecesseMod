package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public class NoticeForm extends ContinueForm {
   public static final int BUTTON_COOLDOWN_NONE = 0;
   public static final int BUTTON_COOLDOWN_NEVER = -1;
   public static final int BUTTON_COOLDOWN_HIDE = -2;
   private FormLocalTextButton button;
   private FormContentBox content;
   public final int maxHeight;
   public int padding;
   private long noticeTime;
   private int noticeCooldown;

   public NoticeForm(String var1, int var2, int var3) {
      super(var1, var2, var3);
      this.padding = 10;
      this.maxHeight = var3;
      this.button = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "continuebutton", 4, this.getHeight() - 40, this.getWidth() - 8));
      this.button.onClicked((var1x) -> {
         this.applyContinue();
      });
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, 0, var2, var3 - 40));
   }

   public NoticeForm(String var1) {
      this(var1, 400, 400);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void setupNotice(Consumer<FormContentBox> var1, GameMessage var2) {
      this.content.clearComponents();
      var1.accept(this.content);
      this.button.setLocalization(var2);
      this.updateHeight();
   }

   public void setupNotice(Consumer<FormContentBox> var1) {
      this.setupNotice((Consumer)var1, new LocalMessage("ui", "continuebutton"));
   }

   public void setupNotice(GameMessage var1, GameMessage var2) {
      this.setupNotice((var2x) -> {
         var2x.addComponent(new FormLocalLabel(var1, new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
      }, var2);
   }

   public final void setupNotice(GameMessage var1) {
      this.setupNotice((GameMessage)var1, new LocalMessage("ui", "continuebutton"));
   }

   public boolean canContinue() {
      return this.button.isActive();
   }

   public void setButtonCooldown(int var1) {
      switch (var1) {
         case -2:
            if (this.hasComponent(this.button)) {
               this.removeComponent(this.button);
            }
            break;
         case -1:
            if (!this.hasComponent(this.button)) {
               this.addComponent(this.button);
            }

            this.noticeTime = -1L;
            this.button.setActive(false);
            break;
         case 0:
            if (!this.hasComponent(this.button)) {
               this.addComponent(this.button);
            }

            this.noticeTime = -1L;
            this.button.setActive(true);
            break;
         default:
            if (!this.hasComponent(this.button)) {
               this.addComponent(this.button);
            }

            if (var1 > 0) {
               this.noticeTime = System.currentTimeMillis();
               this.noticeCooldown = var1;
               this.button.setActive(false);
            } else {
               this.noticeTime = -1L;
               this.button.setActive(true);
            }
      }

      this.updateHeight();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.tickCooldown();
      super.draw(var1, var2, var3);
   }

   private void tickCooldown() {
      if (this.noticeTime != -1L && System.currentTimeMillis() > this.noticeTime + (long)this.noticeCooldown) {
         this.button.setActive(true);
         this.noticeTime = -1L;
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
      int var2 = this.hasComponent(this.button) ? 40 : 0;
      this.setHeight(Math.min(var1.height, this.maxHeight - var2) + var2);
      this.content.setHeight(this.getHeight() - var2);
      this.button.setY(this.getHeight() - 40);
      this.onWindowResized();
   }
}
