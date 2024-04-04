package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.item.WrappingPaperContainer;

public class WrappingPaperContainerForm<T extends WrappingPaperContainer> extends ContainerForm<T> {
   public FormLocalTextButton wrapButton;

   public WrappingPaperContainerForm(Client var1, T var2) {
      super(var1, 300, 300, var2);
      FormFlow var3 = new FormFlow(10);
      if (!var2.paperSlot.isClear()) {
         this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel(var2.paperSlot.getItem().getItemLocalization(), new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
      }

      this.addComponent((FormContainerSlot)var3.nextY(new FormContainerSlot(var1, var2, var2.CONTENT_SLOT, this.getWidth() / 2 - 20, 10), 10));
      this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "presentmessage", new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 5));
      byte var4 = 80;
      FormContentBox var5 = (FormContentBox)this.addComponent(new FormContentBox(4, var3.next(var4) + 4, this.getWidth() - 8, var4 - 8, GameBackground.textBox));
      FormTextBox var6 = (FormTextBox)var5.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, Settings.UI.textBoxTextColor, 0, 0, var5.getMinContentWidth(), 6, 300));
      var6.allowTyping = true;
      var6.setEmptyTextSpace(new Rectangle(var5.getX(), var5.getY(), var5.getWidth(), var5.getHeight()));
      var6.onChange((var2x) -> {
         Rectangle var3 = var5.getContentBoxToFitComponents();
         var5.setContentBox(var3);
         var5.scrollToFit(var6.getCaretBoundingBox());
      });
      var6.onCaretMove((var2x) -> {
         if (!var2x.causedByMouse) {
            var5.scrollToFit(var6.getCaretBoundingBox());
         }

      });
      var3.next(10);
      int var7 = Math.min(150, this.getWidth() - 20);
      this.wrapButton = (FormLocalTextButton)this.addComponent((FormLocalTextButton)var3.nextY(new FormLocalTextButton("ui", "wrapconfirm", this.getWidth() / 2 - var7 / 2, 60, var7, FormInputSize.SIZE_24, ButtonColor.BASE), 10));
      this.wrapButton.onClicked((var2x) -> {
         var2.wrapButton.runAndSend(var6.getText());
      });
      this.setHeight(var3.next());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.wrapButton.setActive(((WrappingPaperContainer)this.container).canWrap());
      super.draw(var1, var2, var3);
   }
}
