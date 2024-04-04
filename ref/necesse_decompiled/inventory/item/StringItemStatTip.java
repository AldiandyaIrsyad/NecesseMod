package necesse.inventory.item;

import java.awt.Color;
import necesse.gfx.GameColor;

public abstract class StringItemStatTip extends ItemStatTip {
   protected String value;
   protected String compareValue;
   protected boolean addParenthesisToChange = true;
   protected Boolean compareValueIsBetter = null;

   public StringItemStatTip(String var1) {
      this.value = var1;
      this.compareValue = null;
   }

   public StringItemStatTip addParenthesisToChange(boolean var1) {
      this.addParenthesisToChange = var1;
      return this;
   }

   public StringItemStatTip setCompareValue(String var1, Boolean var2) {
      this.compareValue = var1;
      this.compareValueIsBetter = var2;
      return this;
   }

   public StringItemStatTip setCompareValue(String var1) {
      return this.setCompareValue(var1, (Boolean)null);
   }

   protected String getReplaceValue(Color var1, Color var2, Color var3, boolean var4) {
      String var5 = this.value;
      if (this.compareValue != null && !this.value.equals(this.compareValue)) {
         if (var4) {
            String var6 = this.compareValue;
            if (this.addParenthesisToChange) {
               var6 = "(" + var6 + ")";
            }

            if (this.compareValueIsBetter == null) {
               if (var3 != null) {
                  var6 = GameColor.getCustomColorCode(var3) + var6 + GameColor.NO_COLOR.getColorCode();
               }
            } else if (this.compareValueIsBetter) {
               if (var1 != null) {
                  var6 = GameColor.getCustomColorCode(var1) + var6 + GameColor.NO_COLOR.getColorCode();
               }
            } else if (var2 != null) {
               var6 = GameColor.getCustomColorCode(var2) + var6 + GameColor.NO_COLOR.getColorCode();
            }

            var5 = var5 + " " + var6;
         } else if (this.compareValueIsBetter == null) {
            if (var3 != null) {
               var5 = GameColor.getCustomColorCode(var3) + var5 + GameColor.NO_COLOR.getColorCode();
            }
         } else if (this.compareValueIsBetter) {
            if (var1 != null) {
               var5 = GameColor.getCustomColorCode(var1) + var5 + GameColor.NO_COLOR.getColorCode();
            }
         } else if (var2 != null) {
            var5 = GameColor.getCustomColorCode(var2) + var5 + GameColor.NO_COLOR.getColorCode();
         }
      }

      return var5;
   }
}
