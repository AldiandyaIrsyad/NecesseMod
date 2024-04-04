package necesse.inventory.item;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltips;

public abstract class ItemStatTip {
   public ItemStatTip() {
   }

   public abstract GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4);

   public String toString(Color var1, Color var2, Color var3, boolean var4) {
      return this.toMessage(var1, var2, var3, var4).translate();
   }

   public FairType toFairType(FontOptions var1, Color var2, Color var3, Color var4, boolean var5) {
      FairType var6 = new FairType();
      var6.append(var1, this.toString(var2, var3, var4, var5));
      var6.applyParsers(TypeParsers.GAME_COLOR);
      return var6;
   }

   public GameTooltips toTooltip(Color var1, Color var2, Color var3, boolean var4) {
      return new FairTypeTooltip(this.toFairType((new FontOptions(Settings.tooltipTextSize)).outline(), var1, var2, var3, var4));
   }
}
