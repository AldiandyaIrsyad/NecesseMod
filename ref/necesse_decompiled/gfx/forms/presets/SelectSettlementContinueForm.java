package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public abstract class SelectSettlementContinueForm extends NoticeForm {
   public SelectSettlementContinueForm(String var1, int var2, int var3, GameMessage var4, Option... var5) {
      super(var1, var2, var3);
      this.setupNotice((var4x) -> {
         FormFlow var5x = new FormFlow();
         if (var4 != null) {
            var5x.next(5);
            var4x.addComponent((FormLocalLabel)var5x.nextY(new FormLocalLabel(var4, new FontOptions(20), 0, var2 / 2, 0, var2 - 10)));
            var5x.next(5);
         }

         if (var5.length == 0) {
            var5x.next(10);
            var4x.addComponent((FormLocalLabel)var5x.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementselecthelp"), new FontOptions(16), 0, var2 / 2, 0, var2 - 10)));
            var5x.next(10);
         } else {
            Option[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Option var9 = var6[var8];
               FormLocalTextButton var10 = (FormLocalTextButton)var4x.addComponent(new FormLocalTextButton(var9.name, 0, var5x.next(40), var2));
               var10.onClicked((var2x) -> {
                  var9.onSelected(this);
               });
               var10.setActive(var9.available);
            }
         }

      }, new LocalMessage("ui", "cancelbutton"));
      this.onContinue(this::onCancel);
   }

   public abstract void onCancel();

   public abstract static class Option {
      public final boolean available;
      public final int islandX;
      public final int islandY;
      public final GameMessage name;

      public Option(boolean var1, int var2, int var3, GameMessage var4) {
         this.available = var1;
         this.islandX = var2;
         this.islandY = var3;
         this.name = var4;
      }

      public abstract void onSelected(SelectSettlementContinueForm var1);
   }
}
