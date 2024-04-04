package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.io.File;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.reports.CrashReportData;

public class CrashReportContinueForm extends ConfirmationContinueForm {
   public CrashReportContinueForm(CrashReportData var1, String var2, int var3, int var4, Consumer<CrashReportContinueForm> var5, Consumer<CrashReportContinueForm> var6) {
      super("crash", 450, 10000);
      this.setupConfirmation((var7) -> {
         FormLocalLabel var8 = (FormLocalLabel)var7.addComponent(new FormLocalLabel("ui", var2, (new FontOptions(16)).color(Settings.UI.activeTextColor), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
         int var9 = Math.min(250, this.getWidth() - 8);
         ((FormLocalTextButton)var7.addComponent(new FormLocalTextButton("ui", "crashshowlog", this.getWidth() / 2 - var9 / 2, var8.getHeight() + 20, var9, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked((var7x) -> {
            this.setupLog(var1, var2, var3, var4, var5, var6);
         });
      }, new LocalMessage("ui", "sendreport"), new LocalMessage("ui", "dontsendreport"), () -> {
         var5.accept(this);
      }, () -> {
         var6.accept(this);
      });
   }

   private void setupLog(CrashReportData var1, String var2, int var3, int var4, Consumer<CrashReportContinueForm> var5, Consumer<CrashReportContinueForm> var6) {
      this.setWidth(GameMath.limit(Screen.getHudWidth() - 100, Math.min(450, var3), var3));
      this.setupConfirmation((var4x) -> {
         FormLocalLabel var5 = (FormLocalLabel)var4x.addComponent(new FormLocalLabel("ui", var2, (new FontOptions(16)).color(Settings.UI.activeTextColor), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
         FormContentBox var6 = (FormContentBox)var4x.addComponent(new FormContentBox(4, var5.getHeight() + 20, this.getWidth() - 8, GameMath.limit(Screen.getHudHeight() - 150, Math.min(100, var4), var4), GameBackground.textBox));
         FormTextBox var7 = (FormTextBox)var6.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, -1, -1, -1));
         var7.setText(var1.getFullReport((File)null));
         var7.allowTyping = false;
         var7.setEmptyTextSpace(new Rectangle(var6.getX(), var6.getY(), var6.getWidth(), var6.getHeight()));
         var7.onChange((var2x) -> {
            Rectangle var3 = var6.getContentBoxToFitComponents();
            var6.setContentBox(var3);
            var6.scrollToFit(var7.getCaretBoundingBox());
         });
         var7.onCaretMove((var2x) -> {
            if (!var2x.causedByMouse) {
               var6.scrollToFit(var7.getCaretBoundingBox());
            }

         });
         Rectangle var8 = var6.getContentBoxToFitComponents();
         var6.setContentBox(var8);
      }, new LocalMessage("ui", "sendreport"), new LocalMessage("ui", "dontsendreport"), () -> {
         var5.accept(this);
      }, () -> {
         var6.accept(this);
      });
   }
}
