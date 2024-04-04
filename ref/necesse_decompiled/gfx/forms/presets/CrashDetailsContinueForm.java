package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;

public class CrashDetailsContinueForm extends ConfirmationContinueForm {
   private FormTextBox textBox;

   public CrashDetailsContinueForm(String var1, String var2, int var3, int var4, int var5, BiConsumer<CrashDetailsContinueForm, String> var6, Consumer<CrashDetailsContinueForm> var7) {
      super("crash", GameMath.limit(Screen.getHudWidth() - 100, Math.min(300, var4), var4), 10000);
      this.setupConfirmation((var5x) -> {
         FormLocalLabel var6 = (FormLocalLabel)var5x.addComponent(new FormLocalLabel("ui", var2, (new FontOptions(16)).color(Settings.UI.activeTextColor), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
         FormContentBox var7 = (FormContentBox)var5x.addComponent(new FormContentBox(4, var6.getHeight() + 20, this.getWidth() - 8, GameMath.limit(Screen.getHudHeight() - 150, Math.min(100, var5), var5), GameBackground.textBox));
         this.textBox = (FormTextBox)var7.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, -1, -1, var3));
         this.textBox.setText(var1);
         this.textBox.allowTyping = true;
         this.textBox.setTyping(true);
         this.textBox.setEmptyTextSpace(new Rectangle(var7.getX(), var7.getY(), var7.getWidth(), var7.getHeight()));
         this.textBox.onChange((var2x) -> {
            Rectangle var3 = var7.getContentBoxToFitComponents();
            var7.setContentBox(var3);
            var7.scrollToFit(this.textBox.getCaretBoundingBox());
         });
         this.textBox.onCaretMove((var2x) -> {
            if (!var2x.causedByMouse) {
               var7.scrollToFit(this.textBox.getCaretBoundingBox());
            }

         });
         Rectangle var8 = var7.getContentBoxToFitComponents();
         var7.setContentBox(var8);
      }, new LocalMessage("ui", "sendreport"), new LocalMessage("ui", "dontsendreport"), () -> {
         var6.accept(this, this.textBox.getText());
      }, () -> {
         var7.accept(this);
      });
   }
}
