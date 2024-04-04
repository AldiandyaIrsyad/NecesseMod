package necesse.gfx.forms.presets.debug;

import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormDebugItemList;
import necesse.gfx.gameFont.FontOptions;

public class DebugItemForm extends Form {
   public FormDebugItemList itemList;
   public FormTextInput itemFilter;
   private final DebugForm parent;

   public DebugItemForm(String var1, DebugForm var2) {
      super((String)var1, 300, 400);
      this.parent = var2;
      this.addComponent(new FormLabel("Items", new FontOptions(20), 0, this.getWidth() / 2, 10));
      this.itemList = (FormDebugItemList)this.addComponent(new FormDebugItemList(0, 30, this.getWidth(), this.getHeight() - 130, var2.client));
      ((FormCheckBox)this.addComponent(new FormCheckBox("Show all", this.getWidth() - 100, this.getHeight() - 98))).onClicked((var1x) -> {
         this.itemList.showUnobtainable(((FormCheckBox)var1x.from).checked);
      });
      this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, this.getHeight() - 98));
      this.itemFilter = (FormTextInput)this.addComponent(new FormTextInput(0, this.getHeight() - 80, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
      this.itemFilter.placeHolder = new StaticMessage("Search filter");
      this.itemFilter.rightClickToClear = true;
      this.itemFilter.onChange((var1x) -> {
         this.itemList.setFilter(((FormTextInput)var1x.from).getText());
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth()))).onClicked((var2x) -> {
         this.itemFilter.setTyping(false);
         var2.makeCurrent(var2.mainMenu);
      });
   }
}
