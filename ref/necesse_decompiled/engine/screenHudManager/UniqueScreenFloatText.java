package necesse.engine.screenHudManager;

import java.util.ArrayList;
import necesse.gfx.gameFont.FontOptions;

public class UniqueScreenFloatText extends ScreenFloatTextFade {
   public final String uniqueType;

   public UniqueScreenFloatText(int var1, int var2, String var3, FontOptions var4, String var5) {
      super(var1, var2, var3, var4);
      this.avoidOtherText = false;
      this.uniqueType = var5;
   }

   public UniqueScreenFloatText(int var1, int var2, String var3, FontOptions var4) {
      this(var1, var2, var3, var4, (String)null);
   }

   public void addThis(ArrayList<ScreenHudElement> var1) {
      if (this.uniqueType != null) {
         for(int var2 = 0; var2 < var1.size(); ++var2) {
            ScreenHudElement var3 = (ScreenHudElement)var1.get(var2);
            if (!var3.isRemoved() && var3 != this && var3 instanceof UniqueScreenFloatText) {
               String var4 = ((UniqueScreenFloatText)var3).uniqueType;
               if (var4 != null && var4.equals(this.uniqueType)) {
                  var1.remove(var2);
                  --var2;
               }
            }
         }
      }

      super.addThis(var1);
   }
}
