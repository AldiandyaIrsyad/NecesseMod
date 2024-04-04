package necesse.gfx.forms.components.localComponents;

import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormLocalCheckBox extends FormCheckBox {
   private GameMessage localMessage;
   private int maxWidth;

   public FormLocalCheckBox(GameMessage var1, int var2, int var3, int var4) {
      super("", var2, var3);
      this.setLocalization(var1, var4);
   }

   public FormLocalCheckBox(GameMessage var1, int var2, int var3) {
      this(var1, var2, var3, -1);
   }

   public FormLocalCheckBox(String var1, String var2, int var3, int var4, int var5) {
      this(new LocalMessage(var1, var2), var3, var4, var5);
   }

   public FormLocalCheckBox(String var1, String var2, int var3, int var4) {
      this(new LocalMessage(var1, var2), var3, var4);
   }

   public FormLocalCheckBox(GameMessage var1, int var2, int var3, boolean var4, int var5) {
      super("", var2, var3, var4);
      this.setLocalization(var1, var5);
   }

   public FormLocalCheckBox(GameMessage var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, -1);
   }

   public FormLocalCheckBox(String var1, String var2, int var3, int var4, boolean var5) {
      this(new LocalMessage(var1, var2), var3, var4, var5);
   }

   public FormLocalCheckBox(String var1, String var2, int var3, int var4, boolean var5, int var6) {
      this(new LocalMessage(var1, var2), var3, var4, var5, var6);
   }

   public FormLocalCheckBox useButtonTexture(ButtonColor var1, ButtonIcon var2, ButtonIcon var3) {
      super.useButtonTexture(var1, var2, var3);
      return this;
   }

   public FormLocalCheckBox useButtonTexture(ButtonColor var1) {
      super.useButtonTexture(var1);
      return this;
   }

   public FormLocalCheckBox useButtonTexture() {
      super.useButtonTexture();
      return this;
   }

   public void setLocalization(GameMessage var1, int var2) {
      this.localMessage = var1;
      this.maxWidth = var2;
      if (this.localMessage != null) {
         this.setText(this.localMessage.translate(), var2);
      } else {
         this.setText("", var2);
      }

   }

   protected void init() {
      super.init();
      Localization.addListener(new LocalizationChangeListener() {
         public void onChange(Language var1) {
            FormLocalCheckBox.this.setText(FormLocalCheckBox.this.localMessage == null ? "" : FormLocalCheckBox.this.localMessage.translate(), FormLocalCheckBox.this.maxWidth);
         }

         public boolean isDisposed() {
            return FormLocalCheckBox.this.isDisposed();
         }
      });
   }

   public void setLocalization(GameMessage var1) {
      this.setLocalization(var1, -1);
   }

   public void setLocalization(String var1, String var2) {
      this.setLocalization(new LocalMessage(var1, var2));
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FormCheckBox useButtonTexture() {
      return this.useButtonTexture();
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FormCheckBox useButtonTexture(ButtonColor var1) {
      return this.useButtonTexture(var1);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public FormCheckBox useButtonTexture(ButtonColor var1, ButtonIcon var2, ButtonIcon var3) {
      return this.useButtonTexture(var1, var2, var3);
   }
}
