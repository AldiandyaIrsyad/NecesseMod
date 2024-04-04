package necesse.inventory.item;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class LocalMessageStringItemStatTip extends StringItemStatTip {
   protected String localeCategory;
   protected String localeKey;
   protected String replaceKey;

   public LocalMessageStringItemStatTip(String var1, String var2, String var3, String var4) {
      super(var4);
      this.localeCategory = var1;
      this.localeKey = var2;
      this.replaceKey = var3;
   }

   public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
      return new LocalMessage(this.localeCategory, this.localeKey, this.replaceKey, this.getReplaceValue(var1, var2, var3, var4));
   }
}
