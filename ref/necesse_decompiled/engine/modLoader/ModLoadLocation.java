package necesse.engine.modLoader;

import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.ui.HoverStateTextures;

public enum ModLoadLocation {
   DEVELOPMENT_FOLDER(new LocalMessage("ui", "modfromdev"), () -> {
      return Settings.UI.config_icon;
   }),
   MODS_FOLDER(new LocalMessage("ui", "modfromfolder"), () -> {
      return Settings.UI.folder_icon;
   }),
   STEAM_WORKSHOP(new LocalMessage("ui", "modfromworkshop"), () -> {
      return Settings.UI.steam_icon;
   });

   public final GameMessage message;
   public final Supplier<HoverStateTextures> iconSupplier;

   private ModLoadLocation(GameMessage var3, Supplier var4) {
      this.message = var3;
      this.iconSupplier = var4;
   }

   // $FF: synthetic method
   private static ModLoadLocation[] $values() {
      return new ModLoadLocation[]{DEVELOPMENT_FOLDER, MODS_FOLDER, STEAM_WORKSHOP};
   }
}
