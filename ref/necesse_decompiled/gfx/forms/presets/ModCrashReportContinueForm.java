package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.reports.CrashReportData;

public class ModCrashReportContinueForm extends ConfirmationContinueForm {
   public ModCrashReportContinueForm(CrashReportData var1, List<LoadedMod> var2, int var3, int var4, Consumer<ModCrashReportContinueForm> var5, Consumer<ModCrashReportContinueForm> var6) {
      super("modcrash", 450, 10000);
      this.setWidth(GameMath.limit(Screen.getHudWidth() - 100, Math.min(450, var3), var3));
      this.setupConfirmation((var4x) -> {
         String var5 = var2.isEmpty() ? "N/A" : GameUtils.join((LoadedMod[])var2.toArray(new LoadedMod[0]), LoadedMod::getModNameString, ", ", " & ");
         LocalMessage var6 = new LocalMessage("ui", "modrunerror", "mod", var5);
         FormLocalLabel var7 = (FormLocalLabel)var4x.addComponent(new FormLocalLabel(var6, (new FontOptions(16)).color(Settings.UI.activeTextColor), 0, this.getWidth() / 2, 10, this.getWidth() - 20));
         FormContentBox var8 = (FormContentBox)var4x.addComponent(new FormContentBox(4, var7.getHeight() + 20, this.getWidth() - 8, GameMath.limit(Screen.getHudHeight() - 150, Math.min(100, var4), var4), GameBackground.textBox));
         FormTextBox var9 = (FormTextBox)var8.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, -1, -1, -1));

         try {
            StringWriter var10 = new StringWriter();

            try {
               var10.write("Mods: " + var5 + "\n\n");
               Iterator var11 = var1.errors.iterator();

               while(true) {
                  if (!var11.hasNext()) {
                     var10.flush();
                     var9.setText(var10.toString());
                     break;
                  }

                  Throwable var12 = (Throwable)var11.next();
                  var12.printStackTrace(new PrintWriter(var10));
               }
            } catch (Throwable var14) {
               try {
                  var10.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }

               throw var14;
            }

            var10.close();
         } catch (IOException var15) {
            var9.setText("Error writing error log");
         }

         var9.allowTyping = false;
         var9.setEmptyTextSpace(new Rectangle(var8.getX(), var8.getY(), var8.getWidth(), var8.getHeight()));
         var9.onChange((var2x) -> {
            Rectangle var3 = var8.getContentBoxToFitComponents();
            var8.setContentBox(var3);
            var8.scrollToFit(var9.getCaretBoundingBox());
         });
         var9.onCaretMove((var2x) -> {
            if (!var2x.causedByMouse) {
               var8.scrollToFit(var9.getCaretBoundingBox());
            }

         });
         Rectangle var16 = var8.getContentBoxToFitComponents();
         var8.setContentBox(var16);
      }, new LocalMessage("ui", "settings"), new LocalMessage("ui", "closebutton"), () -> {
         var5.accept(this);
      }, () -> {
         var6.accept(this);
      });
   }
}
