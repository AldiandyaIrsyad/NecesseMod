package necesse.gfx.forms.components.lists;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.state.MainMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameFont.FontOptions;

public class FormSteamFriendsJoinList extends FormSteamFriendsList {
   protected MainMenu mainMenu;

   public FormSteamFriendsJoinList(MainMenu var1, int var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.mainMenu = var1;
   }

   protected void onFriendClicked(FormSteamFriendsList.FriendElement var1) {
      this.playTickSound();
      SelectionFloatMenu var2 = new SelectionFloatMenu(this);
      var2.add(Localization.translate("ui", "join", "name", var1.name), () -> {
         this.mainMenu.connect(var1.steamID, MainMenu.ConnectFrom.Multiplayer);
         var2.remove();
      });
      this.getManager().openFloatMenu(var2, -2, 0);
   }

   public boolean shouldShow(FormSteamFriendsList.FriendElement var1) {
      return var1.inGame && var1.gameInfo.getGameID() == 1169040L;
   }

   public GameMessage getEmptyMessage() {
      return new LocalMessage("ui", "nofriendsingame");
   }

   public FontOptions getEmptyMessageFontOptions() {
      return new FontOptions(16);
   }
}
