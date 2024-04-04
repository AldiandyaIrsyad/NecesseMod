package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.ClientClient;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.HoverStateTextures;

public class FormTeamPlayerInvite extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   public final ClientClient client;
   private int width;
   private boolean isHovering;
   public boolean selected;
   public FontOptions fontOptions;
   public Color backgroundColor;

   public FormTeamPlayerInvite(int var1, int var2, ClientClient var3, int var4, Color var5) {
      this.fontOptions = (new FontOptions(16)).color(Settings.UI.activeTextColor);
      this.position = new FormFixedPosition(var1, var2);
      this.client = var3;
      this.width = var4;
      this.selected = false;
      this.backgroundColor = var5;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      } else if (!var1.state && var1.getID() == -100 && this.isMouseOver(var1)) {
         this.playTickSound();
         this.selected = !this.selected;
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.getState() == ControllerInput.MENU_SELECT && this.isControllerFocus() && var1.buttonState) {
         this.selected = !this.selected;
         this.playTickSound();
         var1.use();
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      super.drawControllerFocus(var1);
      Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.backgroundColor != null) {
         int var4 = Settings.UI.form.edgeMargin;
         int var5 = Settings.UI.form.edgeResolution;
         Settings.UI.form.getCenterDrawOptions(this.getX() - var5 + var4, this.getY() - var5 + var4, this.width + var5 * 2 - var4 * 2, 20 + var5 * 2 - var4 * 2).forEachDraw((var1x) -> {
            var1x.color(this.backgroundColor);
         }).draw();
      }

      Color var9 = Settings.UI.activeElementColor;
      if (this.isHovering) {
         var9 = Settings.UI.highlightElementColor;
      }

      HoverStateTextures var10 = this.selected ? Settings.UI.checkbox_checked : Settings.UI.checkbox;
      GameTexture var6 = this.isHovering ? var10.highlighted : var10.active;
      var6.initDraw().color(var9).draw(this.getX() + 4, this.getY() + 2);
      FontManager.bit.drawString((float)(this.getX() + 24), (float)(this.getY() + 2), this.client.getName(), this.fontOptions);
      if (this.backgroundColor != null) {
         int var7 = Settings.UI.form.edgeMargin;
         int var8 = Settings.UI.form.edgeResolution;
         Settings.UI.form.getCenterEdgeDrawOptions(this.getX() - var8 + var7, this.getY() - var8 + var7, this.width + var8 * 2 - var7 * 2, 20 + var8 * 2 - var7 * 2).forEachDraw((var1x) -> {
            var1x.color(this.backgroundColor);
         }).draw();
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, 16));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
