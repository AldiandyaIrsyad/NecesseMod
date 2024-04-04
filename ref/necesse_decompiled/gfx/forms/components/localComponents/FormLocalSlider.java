package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.gameFont.FontOptions;

public class FormLocalSlider extends FormSlider {
   private GameMessage localMessage;

   public FormLocalSlider(GameMessage var1, int var2, int var3, int var4, int var5, int var6, int var7, FontOptions var8) {
      super("", var2, var3, var4, var5, var6, var7, var8);
      this.setLocalization(var1);
   }

   public FormLocalSlider(String var1, String var2, int var3, int var4, int var5, int var6, int var7, int var8, FontOptions var9) {
      this(new LocalMessage(var1, var2), var3, var4, var5, var6, var7, var8, var9);
   }

   public FormLocalSlider(GameMessage var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super("", var2, var3, var4, var5, var6, var7);
      this.setLocalization(var1);
   }

   public FormLocalSlider(String var1, String var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this(new LocalMessage(var1, var2), var3, var4, var5, var6, var7, var8);
   }

   public void setLocalization(GameMessage var1) {
      this.localMessage = var1;
      this.updateText();
   }

   public void setLocalization(String var1, String var2) {
      this.setLocalization(new LocalMessage(var1, var2));
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            FormLocalSlider.this.updateText();
         }

         public boolean isDisposed() {
            return FormLocalSlider.this.isDisposed();
         }
      });
   }

   private void updateText() {
      if (this.localMessage != null) {
         this.text = this.localMessage.translate();
      } else {
         this.text = "";
      }

   }
}
