package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.GameAuth;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.teams.PvPTeamsContainer;

public abstract class FormTeamMember extends Form {
   public Color backgroundColor;

   public FormTeamMember(int var1, int var2, int var3, int var4, PvPTeamsContainer.MemberData var5, boolean var6, Color var7) {
      super(var3, var4);
      this.setPosition(var1, var2);
      this.shouldLimitDrawArea = false;
      this.backgroundColor = var7;
      int var8 = 4;
      if (var6) {
         if (var5.auth != GameAuth.getAuthentication()) {
            ((FormContentIconButton)this.addComponent(new FormContentIconButton(0, var4 / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_more, new GameMessage[0]))).onClicked((var2x) -> {
               SelectionFloatMenu var3 = new SelectionFloatMenu(this);
               var3.add(Localization.translate("ui", "teamkick", "name", var5.name), () -> {
                  this.onKickMember(var5);
                  var3.remove();
               });
               var3.add(Localization.translate("ui", "teampassowner"), () -> {
                  this.onPassOwnership(var5);
                  var3.remove();
               });
               this.getManager().openFloatMenu(var3);
            });
         }

         var8 += 20;
      }

      this.addComponent(new FormLabel(var5.name, new FontOptions(16), -1, var8, var4 / 2 - 8, var3 - var8 - 4));
   }

   public abstract void onKickMember(PvPTeamsContainer.MemberData var1);

   public abstract void onPassOwnership(PvPTeamsContainer.MemberData var1);

   public void drawBase(TickManager var1) {
      if (this.backgroundColor != null) {
         int var2 = Settings.UI.form.edgeMargin;
         int var3 = Settings.UI.form.edgeResolution;
         Settings.UI.form.getCenterDrawOptions(this.getX() - var3 + var2, this.getY() - var3 + var2, this.getWidth() + var3 * 2 - var2 * 2, 20 + var3 * 2 - var2 * 2).forEachDraw((var1x) -> {
            var1x.color(this.backgroundColor);
         }).draw();
      }

   }

   public void drawEdge(TickManager var1) {
      if (this.backgroundColor != null) {
         int var2 = Settings.UI.form.edgeMargin;
         int var3 = Settings.UI.form.edgeResolution;
         Settings.UI.form.getCenterEdgeDrawOptions(this.getX() - var3 + var2, this.getY() - var3 + var2, this.getWidth() + var3 * 2 - var2 * 2, 20 + var3 * 2 - var2 * 2).forEachDraw((var1x) -> {
            var1x.color(this.backgroundColor);
         }).draw();
      }

   }
}
