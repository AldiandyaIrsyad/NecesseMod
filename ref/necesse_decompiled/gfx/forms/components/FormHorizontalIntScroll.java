package necesse.gfx.forms.components;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;

public class FormHorizontalIntScroll extends FormHorizontalScroll<Integer> {
   public FormHorizontalIntScroll(int var1, int var2, int var3, FormHorizontalScroll.DrawOption var4, GameMessage var5, int var6, int var7, int var8) {
      super(var1, var2, var3, var4, 0);
      this.set(var5, var6, var7, var8);
   }

   public FormHorizontalIntScroll(int var1, int var2, int var3, FormHorizontalScroll.DrawOption var4, String var5, int var6, int var7, int var8) {
      this(var1, var2, var3, var4, (GameMessage)(new StaticMessage(var5)), var6, var7, var8);
   }

   public void set(GameMessage var1, int var2, int var3, int var4) {
      FormHorizontalScroll.ScrollElement[] var5 = new FormHorizontalScroll.ScrollElement[var4 - var3 + 1];

      for(int var6 = var3; var6 <= var4; ++var6) {
         int var7 = var6 - var3;
         var5[var7] = new FormHorizontalScroll.ScrollElement(var6, var1);
      }

      this.setData(var5);
      this.setElement(new FormHorizontalScroll.ScrollElement(var2, var1));
   }

   public void set(String var1, int var2, int var3, int var4) {
      this.set((GameMessage)(new StaticMessage(var1)), var2, var3, var4);
   }
}
