package necesse.engine;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public enum DisplayMode {
   Windowed(new LocalMessage("settingsui", "displaywindowed"), true),
   Borderless(new LocalMessage("settingsui", "displayborderless"), false),
   Fullscreen(new LocalMessage("settingsui", "displayfullscreen"), true);

   public final GameMessage displayName;
   public final boolean canSelectSize;

   private DisplayMode(GameMessage var3, boolean var4) {
      this.displayName = var3;
      this.canSelectSize = var4;
   }

   // $FF: synthetic method
   private static DisplayMode[] $values() {
      return new DisplayMode[]{Windowed, Borderless, Fullscreen};
   }
}
