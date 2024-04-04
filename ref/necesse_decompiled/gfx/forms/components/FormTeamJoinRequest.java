package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.gfx.forms.Form;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.teams.PvPTeamsContainer;

public abstract class FormTeamJoinRequest extends Form {
   public Color backgroundColor;

   public FormTeamJoinRequest(int var1, int var2, int var3, int var4, PvPTeamsContainer.MemberData var5, Color var6) {
      super(var3, var4);
      this.setPosition(var1, var2);
      this.shouldLimitDrawArea = false;
      this.backgroundColor = var6;
      ((FormContentIconButton)this.addComponent(new FormContentIconButton(0, var4 / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_checked_20, new GameMessage[]{new LocalMessage("ui", "acceptbutton")}))).onClicked((var2x) -> {
         this.onAccept(var5);
      });
      ((FormContentIconButton)this.addComponent(new FormContentIconButton(20, var4 / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_escaped_20, new GameMessage[]{new LocalMessage("ui", "declinebutton")}))).onClicked((var2x) -> {
         this.onDecline(var5);
      });
      this.addComponent(new FormLabel(var5.name, new FontOptions(16), -1, 44, var4 / 2 - 8, var3 - 44 - 4));
   }

   public abstract void onAccept(PvPTeamsContainer.MemberData var1);

   public abstract void onDecline(PvPTeamsContainer.MemberData var1);

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
