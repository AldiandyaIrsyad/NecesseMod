package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameTexture;

public class LockedNoSettler extends Settler {
   public LockedNoSettler() {
      super((String)null);
   }

   public DrawOptions getSettlerFaceDrawOptions(int var1, int var2, Color var3, Mob var4) {
      GameTexture var5 = Settings.UI.settler_locked;
      return var5.initDraw().color(var3).posMiddle(var1, var2);
   }

   public String getGenericMobName() {
      return Localization.translate("misc", "settlementlockbed");
   }
}
