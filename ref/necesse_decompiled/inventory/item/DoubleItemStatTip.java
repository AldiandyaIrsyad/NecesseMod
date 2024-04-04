package necesse.inventory.item;

import java.awt.Color;
import java.util.function.DoubleFunction;
import necesse.engine.util.GameMath;
import necesse.gfx.GameColor;

public abstract class DoubleItemStatTip extends ItemStatTip {
   protected double value;
   protected double compareValue;
   protected int decimals;
   protected DoubleFunction<String> valueToString = GameMath::removeDecimalIfZero;
   protected DoubleFunction<String> deltaToString = (var0) -> {
      return var0 > 0.0 ? "+" + GameMath.removeDecimalIfZero(var0) : GameMath.removeDecimalIfZero(var0);
   };
   protected boolean addParenthesisToChange = true;
   protected boolean higherIsBetter = true;

   public DoubleItemStatTip(double var1, int var3) {
      this.value = var1;
      this.decimals = var3;
      this.compareValue = Double.NaN;
   }

   public DoubleItemStatTip setToString(DoubleFunction<String> var1) {
      this.valueToString = var1;
      this.deltaToString = (var1x) -> {
         return var1x > 0.0 ? "+" + (String)var1.apply(var1x) : (String)var1.apply(var1x);
      };
      return this;
   }

   public DoubleItemStatTip setValueToString(DoubleFunction<String> var1) {
      this.valueToString = var1;
      return this;
   }

   public DoubleItemStatTip setDeltaToString(DoubleFunction<String> var1) {
      this.deltaToString = var1;
      return this;
   }

   public DoubleItemStatTip addParenthesisToChange(boolean var1) {
      this.addParenthesisToChange = var1;
      return this;
   }

   public DoubleItemStatTip setCompareValue(double var1, boolean var3) {
      this.compareValue = var1;
      this.higherIsBetter = var3;
      return this;
   }

   public DoubleItemStatTip setCompareValue(double var1) {
      return this.setCompareValue(var1, true);
   }

   protected String getReplaceValue(Color var1, Color var2, boolean var3) {
      double var4 = this.decimals <= 0 ? (double)Math.round(this.value) : GameMath.toDecimals(this.value, this.decimals);
      String var6 = (String)this.valueToString.apply(var4);
      if (!Double.isNaN(this.compareValue) && this.value != this.compareValue) {
         double var7 = this.decimals <= 0 ? (double)Math.round(this.value - this.compareValue) : GameMath.toDecimals(this.value - this.compareValue, this.decimals);
         boolean var9 = this.higherIsBetter ? var7 > 0.0 : var7 < 0.0;
         if (var3) {
            String var10 = (String)this.deltaToString.apply(var7);
            if (this.addParenthesisToChange) {
               var10 = "(" + var10 + ")";
            }

            if (var9) {
               if (var1 != null) {
                  var10 = GameColor.getCustomColorCode(var1) + var10 + GameColor.NO_COLOR.getColorCode();
               }
            } else if (var2 != null) {
               var10 = GameColor.getCustomColorCode(var2) + var10 + GameColor.NO_COLOR.getColorCode();
            }

            var6 = var6 + " " + var10;
         } else if (var9) {
            if (var1 != null) {
               var6 = GameColor.getCustomColorCode(var1) + var6 + GameColor.NO_COLOR.getColorCode();
            }
         } else if (var2 != null) {
            var6 = GameColor.getCustomColorCode(var2) + var6 + GameColor.NO_COLOR.getColorCode();
         }
      }

      return var6;
   }
}
