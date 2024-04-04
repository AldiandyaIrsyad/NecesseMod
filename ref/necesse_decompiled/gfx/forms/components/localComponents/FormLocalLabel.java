package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;

public class FormLocalLabel extends FormLabel {
   private GameMessage localMessage;
   private int maxWidth;

   public FormLocalLabel(GameMessage var1, FontOptions var2, int var3, int var4, int var5) {
      super("", var2, var3, var4, var5);
      this.setLocalization(var1);
   }

   public FormLocalLabel(GameMessage var1, FontOptions var2, int var3, int var4, int var5, int var6) {
      super("", var2, var3, var4, var5);
      this.setLocalization(var1, var6);
   }

   public FormLocalLabel(String var1, String var2, FontOptions var3, int var4, int var5, int var6) {
      this(new LocalMessage(var1, var2), var3, var4, var5, var6);
   }

   public FormLocalLabel(String var1, String var2, FontOptions var3, int var4, int var5, int var6, int var7) {
      this(new LocalMessage(var1, var2), var3, var4, var5, var6, var7);
   }

   public void setLocalization(GameMessage var1) {
      this.localMessage = var1;
      this.updateText();
   }

   public void setLocalization(GameMessage var1, int var2) {
      this.maxWidth = var2;
      this.setLocalization(var1);
   }

   public void setLocalization(String var1, String var2) {
      this.setLocalization(new LocalMessage(var1, var2));
   }

   public void setLocalization(String var1, String var2, int var3) {
      this.maxWidth = var3;
      this.setLocalization(var1, var2);
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            FormLocalLabel.this.updateText();
         }

         public boolean isDisposed() {
            return FormLocalLabel.this.isDisposed();
         }
      });
   }

   private void updateText() {
      if (this.localMessage != null) {
         if (this.maxWidth <= 0) {
            this.setText(this.localMessage);
         } else {
            this.setText(this.localMessage, this.maxWidth);
         }
      } else {
         this.setText("");
      }

   }
}
