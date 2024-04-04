package necesse.gfx.forms.components.lists;

import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;

public class FormSteamFriendsInviteList extends FormSteamFriendsList {
   protected Client client;

   public FormSteamFriendsInviteList(Client var1, int var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.client = var1;
   }

   protected void onFriendClicked(FormSteamFriendsList.FriendElement var1) {
      this.playTickSound();
      SelectionFloatMenu var2 = new SelectionFloatMenu(this);
      var2.add(Localization.translate("ui", "invite", "name", var1.name), () -> {
         if (this.client.inviteToSteamLobby(var1.steamID)) {
            this.client.chat.addMessage(Localization.translate("ui", "invited", "name", var1.name));
         } else {
            this.client.chat.addMessage(Localization.translate("ui", "couldnotinvite", "name", var1.name));
         }

         var2.remove();
      });
      this.getManager().openFloatMenu(var2, -2, 0);
   }
}
