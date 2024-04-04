package necesse.gfx.forms.presets.sidebar;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.presets.PresetCopyFilter;

public class PresetFilterSidebarForm extends SidebarForm {
   private boolean isValid = true;

   public PresetFilterSidebarForm(String var1, PresetCopyFilter var2) {
      super("presetFilter", 200, 200);
      FormFlow var3 = new FormFlow(5);
      this.addComponent(new FormLabel(var1, new FontOptions(20), -1, 5, var3.next(25)));
      ((FormCheckBox)this.addComponent(new FormCheckBox("Tiles", 5, var3.next(20), var2.acceptTiles))).onClicked((var1x) -> {
         var2.acceptTiles = ((FormCheckBox)var1x.from).checked;
      });
      ((FormCheckBox)this.addComponent(new FormCheckBox("Objects", 5, var3.next(20), var2.acceptObjects))).onClicked((var1x) -> {
         var2.acceptObjects = ((FormCheckBox)var1x.from).checked;
      });
      ((FormCheckBox)this.addComponent(new FormCheckBox("Wires", 5, var3.next(20), var2.acceptWires))).onClicked((var1x) -> {
         var2.acceptWires = ((FormCheckBox)var1x.from).checked;
      });
      this.setHeight(var3.next() + 5);
   }

   public boolean isValid(Client var1) {
      return this.isValid;
   }

   public void invalidate() {
      this.isValid = false;
   }
}
