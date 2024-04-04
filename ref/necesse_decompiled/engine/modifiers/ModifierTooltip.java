package necesse.engine.modifiers;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.inventory.item.ItemStatTip;

public class ModifierTooltip {
   public final int sign;
   public final ItemStatTip tip;

   public ModifierTooltip(int var1, ItemStatTip var2) {
      this.sign = var1;
      this.tip = var2;
   }

   public GameColor getGameColor() {
      if (this.sign < 0) {
         return GameColor.RED;
      } else {
         return this.sign == 0 ? GameColor.YELLOW : GameColor.GREEN;
      }
   }

   public Color getTooltipColor() {
      return (Color)this.getGameColor().color.get();
   }

   public Color getTextColor() {
      if (this.sign < 0) {
         return Settings.UI.errorTextColor;
      } else {
         return this.sign == 0 ? Settings.UI.warningTextColor : Settings.UI.successTextColor;
      }
   }

   public FairType toFairType(FontOptions var1, boolean var2) {
      return this.tip.toFairType(var1, (Color)GameColor.RED.color.get(), (Color)GameColor.GREEN.color.get(), (Color)GameColor.YELLOW.color.get(), var2);
   }

   public FairTypeTooltip toTooltip(boolean var1) {
      FontOptions var2 = (new FontOptions(Settings.tooltipTextSize)).outline();
      if (var1) {
         var2 = var2.color(this.getTooltipColor());
      }

      return new FairTypeTooltip(this.toFairType(var2, true));
   }
}
