package necesse.gfx.forms.components;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.ui.ButtonColor;

public class FormLocalTextButtonToggle extends FormTextButtonToggle {
   private GameMessage localMessage;
   private GameMessage localTooltip;

   public FormLocalTextButtonToggle(GameMessage var1, int var2, int var3, int var4, FormInputSize var5, ButtonColor var6) {
      super("", var2, var3, var4, var5, var6);
      this.setLocalization(var1);
   }

   public FormLocalTextButtonToggle(GameMessage var1, int var2, int var3, int var4) {
      super("", var2, var3, var4);
      this.setLocalization(var1);
   }

   public FormLocalTextButtonToggle(String var1, String var2, int var3, int var4, int var5, FormInputSize var6, ButtonColor var7) {
      this(new LocalMessage(var1, var2), var3, var4, var5, var6, var7);
   }

   public FormLocalTextButtonToggle(String var1, String var2, int var3, int var4, int var5) {
      this(new LocalMessage(var1, var2), var3, var4, var5);
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            FormLocalTextButtonToggle.this.setText(FormLocalTextButtonToggle.this.localMessage == null ? "" : FormLocalTextButtonToggle.this.localMessage.translate());
            FormLocalTextButtonToggle.this.setTooltip(FormLocalTextButtonToggle.this.localTooltip == null ? "" : FormLocalTextButtonToggle.this.localTooltip.translate());
         }

         public boolean isDisposed() {
            return FormLocalTextButtonToggle.this.isDisposed();
         }
      });
   }

   public void setLocalization(GameMessage var1) {
      this.localMessage = var1;
      if (this.localMessage != null) {
         this.setText(this.localMessage.translate());
      } else {
         this.setText("");
      }

   }

   public void setLocalization(String var1, String var2) {
      this.setLocalization(new LocalMessage(var1, var2));
   }

   public void setLocalTooltip(GameMessage var1) {
      this.localTooltip = var1;
      if (this.localTooltip != null) {
         this.setTooltip(this.localTooltip.translate());
      } else {
         this.setTooltip("");
      }

   }

   public void setLocalTooltip(String var1, String var2) {
      this.setLocalTooltip(new LocalMessage(var1, var2));
   }
}
