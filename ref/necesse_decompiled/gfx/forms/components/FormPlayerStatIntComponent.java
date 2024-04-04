package necesse.gfx.forms.components;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameUtils;

public class FormPlayerStatIntComponent extends FormPlayerStatComponent<Integer> {
   private final String suffix;

   public FormPlayerStatIntComponent(int var1, int var2, int var3, GameMessage var4, Supplier<Integer> var5, String var6) {
      super(var1, var2, var3, var4, var5, (var1x) -> {
         return GameUtils.metricNumber((long)var1x) + var6;
      });
      this.suffix = var6;
   }

   public FormPlayerStatIntComponent(int var1, int var2, int var3, GameMessage var4, Supplier<Integer> var5) {
      this(var1, var2, var3, var4, var5, "");
   }

   public String getTooltip(boolean var1, Integer var2, String var3) {
      return var1 && var3.equals(var2 + this.suffix) ? null : this.displayName.translate() + ": " + GameUtils.formatNumber((long)var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public String getTooltip(boolean var1, Object var2, String var3) {
      return this.getTooltip(var1, (Integer)var2, var3);
   }
}
