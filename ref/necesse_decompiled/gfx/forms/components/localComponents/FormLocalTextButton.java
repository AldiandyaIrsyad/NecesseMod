package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.ui.ButtonColor;

public class FormLocalTextButton extends FormTextButton {
   private GameMessage localMessage;
   private GameMessage localTooltip;

   public FormLocalTextButton(GameMessage var1, GameMessage var2, int var3, int var4, int var5, FormInputSize var6, ButtonColor var7) {
      super("", "", var3, var4, var5, var6, var7);
      this.setLocalization(var1);
      this.setLocalTooltip(var2);
   }

   public FormLocalTextButton(GameMessage var1, GameMessage var2, int var3, int var4, int var5) {
      super("", "", var3, var4, var5);
      this.setLocalization(var1);
      this.setLocalTooltip(var2);
   }

   public FormLocalTextButton(String var1, String var2, String var3, String var4, int var5, int var6, int var7, FormInputSize var8, ButtonColor var9) {
      this((GameMessage)(new LocalMessage(var1, var2)), (GameMessage)(new LocalMessage(var3, var4)), var5, var6, var7, var8, var9);
   }

   public FormLocalTextButton(String var1, String var2, String var3, String var4, int var5, int var6, int var7) {
      this((GameMessage)(new LocalMessage(var1, var2)), (GameMessage)(new LocalMessage(var3, var4)), var5, var6, var7);
   }

   public FormLocalTextButton(GameMessage var1, int var2, int var3, int var4, FormInputSize var5, ButtonColor var6) {
      super("", var2, var3, var4, var5, var6);
      this.setLocalization(var1);
   }

   public FormLocalTextButton(GameMessage var1, int var2, int var3, int var4) {
      super("", var2, var3, var4);
      this.setLocalization(var1);
   }

   public FormLocalTextButton(String var1, String var2, int var3, int var4, int var5, FormInputSize var6, ButtonColor var7) {
      this(new LocalMessage(var1, var2), var3, var4, var5, var6, var7);
   }

   public FormLocalTextButton(String var1, String var2, int var3, int var4, int var5) {
      this(new LocalMessage(var1, var2), var3, var4, var5);
   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            FormLocalTextButton.this.setText(FormLocalTextButton.this.localMessage == null ? "" : FormLocalTextButton.this.localMessage.translate());
            FormLocalTextButton.this.setTooltip(FormLocalTextButton.this.localTooltip == null ? "" : FormLocalTextButton.this.localTooltip.translate());
         }

         public boolean isDisposed() {
            return FormLocalTextButton.this.isDisposed();
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
