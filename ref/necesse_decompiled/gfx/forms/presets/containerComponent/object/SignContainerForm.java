package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.object.SignContainer;

public class SignContainerForm<T extends SignContainer> extends ContainerForm<T> {
   private String lastSavedText;
   private FormContentBox textContent;
   private FormTextBox text;

   public static TypeParser[] getParsers(FontOptions var0) {
      return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(var0.getSize()), TypeParsers.InputIcon(var0)};
   }

   public SignContainerForm(Client var1, final T var2) {
      super(var1, 400, 160, var2);
      this.textContent = (FormContentBox)this.addComponent(new FormContentBox(8, 8, this.getWidth() - 16, this.getHeight() - 16, GameBackground.textBox));
      FontOptions var3 = new FontOptions(16);
      this.text = (FormTextBox)this.textContent.addComponent(new FormTextBox(var3, FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, this.textContent.getMinContentWidth(), 20, 300) {
         public void changedTyping(boolean var1) {
            super.changedTyping(var1);
            if (!var1) {
               String var2x = SignContainerForm.this.text.getText();
               if (!SignContainerForm.this.lastSavedText.equals(var2x)) {
                  SignContainerForm.this.lastSavedText = var2x;
                  var2.updateTextAction.runAndSend(var2x);
               }
            }

         }
      });
      this.text.setParsers(getParsers(var3));
      this.text.allowItemAppend = true;
      this.text.setEmptyTextSpace(new Rectangle(this.textContent.getX(), this.textContent.getY(), this.textContent.getWidth(), this.textContent.getHeight()));
      this.text.setText(var2.objectEntity.getTextString());
      this.lastSavedText = this.text.getText();
      this.text.onChange((var1x) -> {
         Rectangle var2 = this.textContent.getContentBoxToFitComponents();
         this.textContent.setContentBox(var2);
         this.textContent.scrollToFit(this.text.getCaretBoundingBox());
      });
      this.text.onCaretMove((var1x) -> {
         if (!var1x.causedByMouse) {
            this.textContent.scrollToFit(this.text.getCaretBoundingBox());
         }

      });
      this.text.onInputEvent((var0) -> {
         if (var0.event.getID() == 256) {
            ((FormTypingComponent)var0.from).setTyping(false);
            var0.event.use();
            var0.preventDefault();
         }

      });
      Rectangle var4 = this.textContent.getContentBoxToFitComponents();
      this.textContent.setContentBox(var4);
   }
}
