package necesse.gfx.forms.presets.debug;

import necesse.engine.Screen;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormObjectList;
import necesse.gfx.forms.presets.debug.tools.PlaceObjectGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameObject.GameObject;

public class DebugObjectsForm extends Form {
   public FormTextInput objectFilter;
   public FormObjectList objectList;
   public final DebugForm parent;

   public DebugObjectsForm(String var1, final DebugForm var2) {
      super((String)var1, 240, 400);
      this.parent = var2;
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, 360, 240))).onClicked((var2x) -> {
         var2.makeCurrent(var2.world);
         this.objectFilter.setTyping(false);
      });
      this.addComponent(new FormLabel("Objects", new FontOptions(20), 0, this.getWidth() / 2, 10));
      this.objectList = (FormObjectList)this.addComponent(new FormObjectList(0, 40, this.getWidth(), this.getHeight() - 140) {
         public void onClicked(GameObject var1) {
            PlaceObjectGameTool var2x = new PlaceObjectGameTool(var2, var1);
            Screen.clearGameTools(var2);
            Screen.setGameTool(var2x, var2);
         }
      });
      this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, 302));
      this.objectFilter = (FormTextInput)this.addComponent(new FormTextInput(0, 320, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
      this.objectFilter.placeHolder = new StaticMessage("Search filter");
      this.objectFilter.rightClickToClear = true;
      this.objectFilter.onChange((var1x) -> {
         this.objectList.setFilter(((FormTextInput)var1x.from).getText());
      });
   }
}
