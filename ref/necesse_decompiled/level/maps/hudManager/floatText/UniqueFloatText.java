package necesse.level.maps.hudManager.floatText;

import java.util.ArrayList;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class UniqueFloatText extends FloatTextFade {
   public final String uniqueType;

   public UniqueFloatText(int var1, int var2, String var3, FontOptions var4, String var5) {
      super(var1, var2, var3, var4);
      this.avoidOtherText = false;
      this.uniqueType = var5;
   }

   public UniqueFloatText(int var1, int var2, String var3, FontOptions var4) {
      this(var1, var2, var3, var4, (String)null);
   }

   public void addThis(Level var1, ArrayList<HudDrawElement> var2) {
      if (this.uniqueType != null) {
         for(int var3 = 0; var3 < var2.size(); ++var3) {
            HudDrawElement var4 = (HudDrawElement)var2.get(var3);
            if (!var4.isRemoved() && var4 != this && var4 instanceof UniqueFloatText) {
               String var5 = ((UniqueFloatText)var4).uniqueType;
               if (var5 != null && var5.equals(this.uniqueType)) {
                  var2.remove(var3);
                  --var3;
               }
            }
         }
      }

      super.addThis(var1, var2);
   }
}
