package necesse.gfx.forms.presets.debug;

import necesse.engine.Screen;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormTileList;
import necesse.gfx.forms.presets.debug.tools.PlaceTileGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameTile.GameTile;

public class DebugTilesForm extends Form {
   public FormTextInput tileFilter;
   public FormTileList tileList;
   public final DebugForm parent;

   public DebugTilesForm(String var1, final DebugForm var2) {
      super((String)var1, 240, 400);
      this.parent = var2;
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, 360, 240))).onClicked((var2x) -> {
         this.tileFilter.setTyping(false);
         var2.makeCurrent(var2.world);
      });
      this.addComponent(new FormLabel("Tiles", new FontOptions(20), 0, this.getWidth() / 2, 10));
      this.tileList = (FormTileList)this.addComponent(new FormTileList(0, 40, this.getWidth(), this.getHeight() - 140) {
         public void onClicked(GameTile var1) {
            PlaceTileGameTool var2x = new PlaceTileGameTool(var2, var1);
            Screen.clearGameTools(var2);
            Screen.setGameTool(var2x, var2);
         }
      });
      this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, 302));
      this.tileFilter = (FormTextInput)this.addComponent(new FormTextInput(0, 320, FormInputSize.SIZE_32_TO_40, 240, -1));
      this.tileFilter.placeHolder = new StaticMessage("Search filter");
      this.tileFilter.rightClickToClear = true;
      this.tileFilter.onChange((var1x) -> {
         this.tileList.setFilter(((FormTextInput)var1x.from).getText());
      });
   }
}
