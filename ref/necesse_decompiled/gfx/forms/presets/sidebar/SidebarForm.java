package necesse.gfx.forms.presets.sidebar;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.inventory.InventoryItem;

public abstract class SidebarForm extends Form implements SidebarComponent {
   protected InventoryItem playerSelectedItem;

   public SidebarForm(String var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public SidebarForm(String var1, int var2, int var3, InventoryItem var4) {
      this(var1, var2, var3);
      this.playerSelectedItem = var4;
   }

   public void onSidebarUpdate(int var1, int var2) {
      this.setPosition(var1, var2);
   }

   public boolean isValid(Client var1) {
      PlayerMob var2 = var1.getPlayer();
      if (var2 == null) {
         return true;
      } else {
         InventoryItem var3 = var2.getSelectedItem();
         return this.playerSelectedItem != null && var3 == this.playerSelectedItem;
      }
   }

   public void onAdded(Client var1) {
   }

   public void onRemoved(Client var1) {
   }
}
