package necesse.engine.modifiers;

import java.awt.Color;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.gfx.GameColor;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class Modifier<T> {
   public static final ModifierAppendFunction<Float> FLOAT_ADD_APPEND = (var0, var1, var2) -> {
      return var0 + var1 * (float)var2;
   };
   public static final ModifierAppendFunction<Integer> INT_ADD_APPEND = (var0, var1, var2) -> {
      return var0 + var1 * var2;
   };
   public static final ModifierAppendFunction<Float> FLOAT_MULT_APPEND = (var0, var1, var2) -> {
      return var0 * (var2 == 1 ? var1 : (float)Math.pow((double)var1, (double)var2));
   };
   public static final ModifierAppendFunction<Boolean> OR_APPEND = (var0, var1, var2) -> {
      return var0 || var1;
   };
   public static final BiFunction<Float, Float, Float> FLOAT_ADD_APPEND_DEPRICATED = (var0, var1) -> {
      return var0 + var1;
   };
   public static final BiFunction<Integer, Integer, Integer> INT_ADD_APPEND_DEPRICATED = (var0, var1) -> {
      return var0 + var1;
   };
   public static final BiFunction<Float, Float, Float> FLOAT_MULT_APPEND_DEPRICATED = (var0, var1) -> {
      return var0 * var1;
   };
   public static final BiFunction<Boolean, Boolean, Boolean> OR_APPEND_DEPRICATED = (var0, var1) -> {
      return var0 || var1;
   };
   public final int index;
   public final ModifierList list;
   public final String stringID;
   public final T defaultBuffManagerValue;
   public final T defaultBuffValue;
   private ModifierAppendFunction<T> appendFunc;
   private Function<T, T> finalLimiter;
   private ModifierTooltipGetter<T> tooltipFunc;
   public final ModifierLimiter<T> limiter;

   public static ModifierTooltipGetter<Float> NORMAL_PERC_PARSER(String var0) {
      return (var1, var2, var3) -> {
         float var4 = var1 - var3;
         LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)Math.round(var4 * 100.0F), 0);
         var5.setValueToString((var0x) -> {
            return var0x > 0.0 ? "+" + GameMath.removeDecimalIfZero(var0x) : GameMath.removeDecimalIfZero(var0x);
         });
         if (var2 != null) {
            var5.setCompareValue((double)Math.round((var2 - var3) * 100.0F));
         }

         return new ModifierTooltip((int)Math.signum(var4), var5);
      };
   }

   public static ModifierTooltipGetter<Float> INVERSE_PERC_PARSER(String var0) {
      return (var1, var2, var3) -> {
         float var4 = var1 - var3;
         LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)Math.round(var4 * 100.0F), 0);
         var5.setValueToString((var0x) -> {
            return var0x > 0.0 ? "+" + GameMath.removeDecimalIfZero(var0x) : GameMath.removeDecimalIfZero(var0x);
         });
         if (var2 != null) {
            var5.setCompareValue((double)Math.round((var2 - var3) * 100.0F), false);
         }

         return new ModifierTooltip((int)Math.signum(-var4), var5);
      };
   }

   public static ModifierTooltipGetter<Float> NORMAL_FLAT_FLOAT_PARSER(String var0) {
      return (var1, var2, var3) -> {
         float var4 = var1 - var3;
         LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)var4, 2);
         var5.setValueToString((var0x) -> {
            return var0x > 0.0 ? "+" + GameMath.removeDecimalIfZero(var0x) : GameMath.removeDecimalIfZero(var0x);
         });
         if (var2 != null) {
            var5.setCompareValue((double)(var2 - var3));
         }

         return new ModifierTooltip((int)Math.signum(var4), var5);
      };
   }

   public static ModifierTooltipGetter<Float> INVERSE_FLAT_FLOAT_PARSER(String var0) {
      return (var1, var2, var3) -> {
         float var4 = var1 - var3;
         LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)var4, 2);
         var5.setValueToString((var0x) -> {
            return var0x > 0.0 ? "+" + GameMath.removeDecimalIfZero(var0x) : GameMath.removeDecimalIfZero(var0x);
         });
         if (var2 != null) {
            var5.setCompareValue((double)(var2 - var3), false);
         }

         return new ModifierTooltip((int)Math.signum(-var4), var5);
      };
   }

   public static ModifierTooltipGetter<Integer> NORMAL_FLAT_INT_PARSER(String var0) {
      return (var1, var2, var3) -> {
         int var4 = var1 - var3;
         LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)var4, 0);
         var5.setValueToString((var0x) -> {
            return var0x > 0.0 ? "+" + GameMath.removeDecimalIfZero(var0x) : GameMath.removeDecimalIfZero(var0x);
         });
         if (var2 != null) {
            var5.setCompareValue((double)(var2 - var3));
         }

         return new ModifierTooltip((int)Math.signum((float)var4), var5);
      };
   }

   public static ModifierTooltipGetter<Integer> INVERSE_FLAT_INT_PARSER(String var0) {
      return (var1, var2, var3) -> {
         int var4 = var1 - var3;
         LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)var4, 0);
         var5.setValueToString((var0x) -> {
            return var0x > 0.0 ? "+" + GameMath.removeDecimalIfZero(var0x) : GameMath.removeDecimalIfZero(var0x);
         });
         if (var2 != null) {
            var5.setCompareValue((double)(var2 - var3), false);
         }

         return new ModifierTooltip((int)Math.signum((float)(-var4)), var5);
      };
   }

   public static ModifierTooltipGetter<Boolean> BOOL_PARSER(String var0) {
      return (var1, var2, var3) -> {
         return var1 ? new ModifierTooltip(1, new ItemStatTip() {
            public GameMessage toMessage(Color var1, Color var2x, Color var3, boolean var4) {
               LocalMessage var5 = new LocalMessage("buffmodifiers", var0);
               return (GameMessage)(var2 != null && !var2 ? (new GameMessageBuilder()).append(GameColor.getCustomColorCode(var1)).append((GameMessage)var5) : var5);
            }
         }) : null;
      };
   }

   public static ModifierTooltipGetter<Boolean> INVERSE_BOOL_PARSER(String var0) {
      return (var1, var2, var3) -> {
         return var1 ? new ModifierTooltip(-1, new ItemStatTip() {
            public GameMessage toMessage(Color var1, Color var2x, Color var3, boolean var4) {
               LocalMessage var5 = new LocalMessage("buffmodifiers", var0);
               return (GameMessage)(var2 != null && var2 ? (new GameMessageBuilder()).append(GameColor.getCustomColorCode(var2x)).append((GameMessage)var5) : var5);
            }
         }) : null;
      };
   }

   public static ModifierTooltipGetter<Boolean> NEUTRAL_BOOL_PARSER(String var0) {
      return (var1, var2, var3) -> {
         return var1 ? new ModifierTooltip(0, new ItemStatTip() {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", var0);
            }
         }) : null;
      };
   }

   public static ModifierTooltipGetter<Float> BAD_PERCENT_MODIFIER(String var0) {
      return (var1, var2, var3) -> {
         float var4 = var1 - var3;
         LocalMessageDoubleItemStatTip var5 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)Math.round(var4 * 100.0F), 0);
         if (var2 != null) {
            var5.setCompareValue((double)Math.round((var2 - var3) * 100.0F), false);
         }

         return new ModifierTooltip(-1, var5);
      };
   }

   public static ModifierTooltipGetter<Float> LESS_GOOD_PERCENT_MODIFIER(String var0) {
      return (var1, var2, var3) -> {
         LocalMessageDoubleItemStatTip var4 = new LocalMessageDoubleItemStatTip("buffmodifiers", var0, "mod", (double)Math.round(var1 * 100.0F), 0);
         if (var2 != null) {
            var4.setCompareValue((double)Math.round(var2 * 100.0F), false);
         }

         return new ModifierTooltip(var1 < 1.0F ? 1 : -1, var4);
      };
   }

   public Modifier(ModifierList var1, String var2, T var3, T var4, ModifierAppendFunction<T> var5, Function<T, T> var6, ModifierTooltipGetter<T> var7, ModifierLimiter<T> var8) {
      Objects.requireNonNull(var5);
      this.stringID = var2;
      this.list = var1;
      this.defaultBuffManagerValue = var3;
      this.defaultBuffValue = var4;
      this.appendFunc = var5;
      this.finalLimiter = var6;
      this.tooltipFunc = var7;
      this.limiter = var8;
      this.index = var1.addModifier(this);
   }

   /** @deprecated */
   @Deprecated
   public Modifier(ModifierList var1, T var2, T var3, ModifierAppendFunction<T> var4, Function<T, T> var5, ModifierTooltipGetter<T> var6, ModifierLimiter<T> var7) {
      this(var1, (String)null, var2, var3, var4, var5, var6, var7);
   }

   public Modifier(ModifierList var1, String var2, T var3, T var4, ModifierAppendFunction<T> var5, ModifierTooltipGetter<T> var6, ModifierLimiter<T> var7) {
      this(var1, var2, var3, var4, var5, (Function)null, var6, var7);
   }

   /** @deprecated */
   @Deprecated
   public Modifier(ModifierList var1, T var2, T var3, ModifierAppendFunction<T> var4, ModifierTooltipGetter<T> var5, ModifierLimiter<T> var6) {
      this(var1, (String)null, var2, var3, var4, (Function)null, var5, var6);
   }

   /** @deprecated */
   @Deprecated
   public Modifier(ModifierList var1, T var2, T var3, BiFunction<T, T, T> var4, Function<T, T> var5, ModifierTooltipGetter<T> var6, ModifierLimiter<T> var7) {
      this(var1, (String)null, var2, var3, (var1x, var2x, var3x) -> {
         return var4.apply(var1x, var2x);
      }, var5, var6, var7);
   }

   /** @deprecated */
   @Deprecated
   public Modifier(ModifierList var1, T var2, T var3, BiFunction<T, T, T> var4, ModifierTooltipGetter<T> var5, ModifierLimiter<T> var6) {
      this(var1, (String)null, var2, var3, (var1x, var2x, var3x) -> {
         return var4.apply(var1x, var2x);
      }, (Function)null, var5, var6);
   }

   public ModifierTooltip getTooltip(T var1, T var2, T var3) {
      if (Objects.equals(var1, var3)) {
         return null;
      } else {
         return this.tooltipFunc == null ? null : this.tooltipFunc.get(var1, var2, var3);
      }
   }

   public ModifierTooltip getTooltip(T var1, T var2) {
      return this.getTooltip(var1, (Object)null, var2);
   }

   public T appendManager(T var1, T var2) {
      return this.appendManager(var1, var2, 1);
   }

   public T appendManager(T var1, T var2, int var3) {
      return var3 <= 0 ? var1 : this.appendFunc.apply(var1, var2, var3);
   }

   public T finalLimit(T var1) {
      return this.finalLimiter != null ? this.finalLimiter.apply(var1) : var1;
   }

   public T max(T var1, T var2) {
      return this.limiter != null ? this.limiter.max(var1, var2) : var1;
   }

   public T min(T var1, T var2) {
      return this.limiter != null ? this.limiter.min(var1, var2) : var1;
   }

   public ModifierTooltip getMinTooltip(T var1, T var2, T var3) {
      return this.limiter != null ? this.limiter.getMinTooltip(var1, var2, var3, this.defaultBuffManagerValue) : null;
   }

   public ModifierTooltip getMinTooltip(T var1, T var2) {
      return this.getMinTooltip(var1, (Object)null, var2);
   }

   public ModifierTooltip getMinTooltip(T var1) {
      return this.getMinTooltip(var1, (Object)null);
   }

   public ModifierTooltip getMaxTooltip(T var1, T var2, T var3) {
      return this.limiter != null ? this.limiter.getMaxTooltip(var1, var2, var3, this.defaultBuffManagerValue) : null;
   }

   public ModifierTooltip getMaxTooltip(T var1, T var2) {
      return this.getMaxTooltip(var1, (Object)null, var2);
   }

   public ModifierTooltip getMaxTooltip(T var1) {
      return this.getMaxTooltip(var1, (Object)null);
   }

   @FunctionalInterface
   public interface ModifierTooltipGetter<V> {
      ModifierTooltip get(V var1, V var2, V var3);
   }
}
