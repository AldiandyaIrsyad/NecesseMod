package necesse.gfx.forms.presets.sidebar;

import java.awt.Rectangle;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;

public class WireEditSidebarForm extends SidebarForm {
   private static boolean[] isEditingWire = new boolean[4];
   private FormCheckBox[] wires;

   public static boolean isEditing(int var0) {
      return isEditingWire[var0];
   }

   public WireEditSidebarForm(InventoryItem var1) {
      super("wireeditsidebar", 160, 120, var1);
      this.addComponent(new FormLocalLabel("ui", "wireedit", new FontOptions(20), 0, this.getWidth() / 2, 10));
      FormFlow var2 = new FormFlow(40);
      this.wires = new FormCheckBox[4];

      for(int var3 = 0; var3 < 4; ++var3) {
         this.wires[var3] = ((FormLocalCheckBox)this.addComponent(new FormLocalCheckBox("ui", "wire" + var3, 10, var2.next(20)))).onClicked((var1x) -> {
            isEditingWire[var3] = ((FormCheckBox)var1x.from).checked;
         });
         this.wires[var3].checked = isEditing(var3);
      }

      this.setHeight(var2.next());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      for(int var4 = 0; var4 < this.wires.length; ++var4) {
         this.wires[var4].checked = isEditing(var4);
      }

      super.draw(var1, var2, var3);
   }

   static {
      isEditingWire[0] = true;
   }
}
