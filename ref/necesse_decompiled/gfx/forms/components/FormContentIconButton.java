package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormContentIconButton extends FormContentButton {
   private GameMessage[] tooltips;
   private ButtonIcon icon;
   private boolean mirrorX;
   private boolean mirrorY;

   public FormContentIconButton(int var1, int var2, int var3, FormInputSize var4, ButtonColor var5, ButtonIcon var6, GameMessage... var7) {
      super(var1, var2, var3, var4, var5);
      this.icon = var6;
      this.tooltips = var7;
   }

   public FormContentIconButton(int var1, int var2, FormInputSize var3, ButtonColor var4, ButtonIcon var5, GameMessage... var6) {
      this(var1, var2, var3.height, var3, var4, var5, var6);
   }

   protected int getIconDrawX(ButtonIcon var1, int var2, int var3) {
      return var2 + var3 / 2 - var1.texture.getWidth() / 2;
   }

   protected int getIconDrawY(ButtonIcon var1, int var2, int var3) {
      return var2 + var3 / 2 - var1.texture.getHeight() / 2;
   }

   public Color getContentColor() {
      return (Color)this.icon.colorGetter.apply(this.getButtonState());
   }

   protected void drawContent(int var1, int var2, int var3, int var4) {
      if (this.icon != null) {
         this.icon.texture.initDraw().color(this.getContentColor()).mirror(this.mirrorX, this.mirrorY).draw(this.getIconDrawX(this.icon, var1, var3), this.getIconDrawY(this.icon, var2, var4));
      }
   }

   protected void addTooltips(PlayerMob var1) {
      super.addTooltips(var1);
      GameTooltips var2 = this.getTooltips(var1);
      if (var2 != null) {
         Screen.addTooltip(var2, TooltipLocation.FORM_FOCUS);
      }

   }

   public GameTooltips getTooltips(PlayerMob var1) {
      if (this.tooltips.length == 0) {
         return null;
      } else {
         StringTooltips var2 = new StringTooltips();
         GameMessage[] var3 = this.tooltips;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            GameMessage var6 = var3[var5];
            var2.add(var6.translate(), 350);
         }

         return var2;
      }
   }

   public void setTooltips(GameMessage... var1) {
      this.tooltips = var1;
   }

   public void setIcon(ButtonIcon var1) {
      this.icon = var1;
   }

   public FormContentIconButton mirror(boolean var1, boolean var2) {
      this.mirrorX = var1;
      this.mirrorY = var2;
      return this;
   }

   public FormContentIconButton mirrorX() {
      this.mirrorX = true;
      return this;
   }

   public FormContentIconButton mirrorY() {
      this.mirrorY = true;
      return this;
   }

   public void drawDraggingElement(int var1, int var2) {
      GameTooltips var3 = this.getTooltips((PlayerMob)null);
      if (var3 != null) {
         var3.draw(var1, var2 - var3.getHeight() - 4, GameColor.DEFAULT_COLOR);
      }

   }
}
