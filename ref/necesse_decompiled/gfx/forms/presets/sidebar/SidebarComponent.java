package necesse.gfx.forms.presets.sidebar;

import necesse.engine.network.client.Client;

public interface SidebarComponent {
   void onSidebarUpdate(int var1, int var2);

   boolean isValid(Client var1);

   void onAdded(Client var1);

   void onRemoved(Client var1);
}
