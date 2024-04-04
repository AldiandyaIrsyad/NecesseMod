package necesse.inventory.item.miscItem;

import java.io.FileNotFoundException;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameMusic;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class VinylItem extends Item {
   public GameMusic music;

   public VinylItem(GameMusic var1) {
      super(1);
      this.music = var1;
      this.rarity = Item.Rarity.UNCOMMON;
      this.setItemCategory(new String[]{"misc", "vinyls"});
   }

   protected void loadItemTextures() {
      try {
         this.itemTexture = GameTexture.fromFileRaw("items/" + this.getStringID());
      } catch (FileNotFoundException var2) {
         this.itemTexture = this.music.loadVinylTexture();
      }

   }

   public GameMessage getNewLocalization() {
      return (GameMessage)(this.music.fromName == null ? new LocalMessage("item", "vinyl", "name", this.music.trackName) : (new GameMessageBuilder()).append((GameMessage)(new LocalMessage("item", "vinyl", "name", this.music.trackName))).append(" (").append(this.music.fromName).append(")"));
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "vinyltip"));
      return var4;
   }
}
