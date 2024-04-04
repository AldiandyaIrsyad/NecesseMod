package necesse.engine.modifiers;

import java.awt.Color;
import java.util.function.BiFunction;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.inventory.item.DoubleItemStatTip;

public class ModifierLimiter<T> {
   private BiFunction<T, T, T> min;
   private BiFunction<T, T, T> max;
   private LimitTooltipGetter<T> minTooltip;
   private LimitTooltipGetter<T> maxTooltip;

   private static int normalFloatMinSign(Float var0, Float var1, Float var2) {
      if (var1 == null) {
         return var0 > var2 ? 1 : 0;
      } else {
         return var0 > var1 ? 1 : 0;
      }
   }

   private static int normalFloatMaxSign(Float var0, Float var1, Float var2) {
      if (var1 == null) {
         return var0 < var2 ? -1 : 0;
      } else {
         return var0 < var1 ? -1 : 0;
      }
   }

   private static int normalIntMinSign(Integer var0, Integer var1, Integer var2) {
      if (var1 == null) {
         return var0 > var2 ? 1 : 0;
      } else {
         return var0 > var1 ? 1 : 0;
      }
   }

   private static int normalIntMaxSign(Integer var0, Integer var1, Integer var2) {
      if (var1 == null) {
         return var0 < var2 ? -1 : 0;
      } else {
         return var0 < var1 ? -1 : 0;
      }
   }

   public static ModifierLimiter<Float> NORMAL_PERC_LIMITER(String var0) {
      return new ModifierLimiter(Math::min, Math::max, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)(var1 * 100.0F), 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)(var2 * 100.0F));
         }

         return new ModifierTooltip(normalFloatMinSign(var1, var3, var4), var5);
      }, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)(var1 * 100.0F), 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)(var2 * 100.0F));
         }

         return new ModifierTooltip(normalFloatMaxSign(var1, var3, var4), var5);
      });
   }

   /** @deprecated */
   @Deprecated
   public static ModifierLimiter<Float> PERC_LIMITER(String var0) {
      return NORMAL_PERC_LIMITER(var0);
   }

   public static ModifierLimiter<Float> INVERSE_PERC_LIMITER(String var0) {
      return new ModifierLimiter(Math::min, Math::max, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)(var1 * 100.0F), 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)(var2 * 100.0F), false);
         }

         return new ModifierTooltip(-normalFloatMinSign(var1, var3, var4), var5);
      }, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)(var1 * 100.0F), 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)(var2 * 100.0F), false);
         }

         return new ModifierTooltip(-normalFloatMaxSign(var1, var3, var4), var5);
      });
   }

   public static ModifierLimiter<Integer> NORMAL_FLAT_INT_LIMITER(String var0) {
      return new ModifierLimiter(Math::min, Math::max, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2);
         }

         return new ModifierTooltip(normalIntMinSign(var1, var3, var4), var5);
      }, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2);
         }

         return new ModifierTooltip(normalIntMaxSign(var1, var3, var4), var5);
      });
   }

   /** @deprecated */
   @Deprecated
   public static ModifierLimiter<Integer> FLAT_INT_LIMITER(String var0) {
      return NORMAL_FLAT_INT_LIMITER(var0);
   }

   public static ModifierLimiter<Integer> INVERSE_FLAT_INT_LIMITER(String var0) {
      return new ModifierLimiter(Math::min, Math::max, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2, false);
         }

         return new ModifierTooltip(-normalIntMinSign(var1, var3, var4), var5);
      }, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 0) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2, false);
         }

         return new ModifierTooltip(-normalIntMaxSign(var1, var3, var4), var5);
      });
   }

   public static ModifierLimiter<Float> NORMAL_FLAT_FLOAT_LIMITER(String var0) {
      return new ModifierLimiter(Math::min, Math::max, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 2) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2);
         }

         return new ModifierTooltip(normalFloatMinSign(var1, var3, var4), var5);
      }, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 2) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2);
         }

         return new ModifierTooltip(normalFloatMaxSign(var1, var3, var4), var5);
      });
   }

   /** @deprecated */
   @Deprecated
   public static ModifierLimiter<Float> FLAT_FLOAT_LIMITER(String var0) {
      return NORMAL_FLAT_FLOAT_LIMITER(var0);
   }

   public static ModifierLimiter<Float> INVERSE_FLAT_FLOAT_LIMITER(String var0) {
      return new ModifierLimiter(Math::min, Math::max, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 2) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2, false);
         }

         return new ModifierTooltip(-normalFloatMinSign(var1, var3, var4), var5);
      }, (var1, var2, var3, var4) -> {
         DoubleItemStatTip var5 = new DoubleItemStatTip((double)var1, 2) {
            public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
               return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", var0, "mod", this.getReplaceValue(var1, var2, var4)));
            }
         };
         if (var2 != null) {
            var5.setCompareValue((double)var2, false);
         }

         return new ModifierTooltip(-normalFloatMaxSign(var1, var3, var4), var5);
      });
   }

   public ModifierLimiter(BiFunction<T, T, T> var1, BiFunction<T, T, T> var2, LimitTooltipGetter<T> var3, LimitTooltipGetter<T> var4) {
      this.max = var2;
      this.min = var1;
      this.maxTooltip = var4;
      this.minTooltip = var3;
   }

   public T min(T var1, T var2) {
      return this.min != null ? this.min.apply(var1, var2) : var1;
   }

   public T max(T var1, T var2) {
      return this.max != null ? this.max.apply(var1, var2) : var1;
   }

   public ModifierTooltip getMinTooltip(T var1, T var2, T var3, T var4) {
      return this.minTooltip == null ? null : this.minTooltip.get(var1, var2, var3, var4);
   }

   public ModifierTooltip getMaxTooltip(T var1, T var2, T var3, T var4) {
      return this.maxTooltip == null ? null : this.maxTooltip.get(var1, var2, var3, var4);
   }

   @FunctionalInterface
   public interface LimitTooltipGetter<T> {
      ModifierTooltip get(T var1, T var2, T var3, T var4);
   }
}
