package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class FormContentIconToggleButton extends FormContentToggleButton {
   protected GameMessage[] tooltips;
   protected ButtonIcon onIcon;
   protected ButtonIcon offIcon;
   protected boolean onMirrorX;
   protected boolean onMirrorY;
   protected boolean offMirrorX;
   protected boolean offMirrorY;

   public FormContentIconToggleButton(int var1, int var2, int var3, FormInputSize var4, ButtonColor var5, ButtonIcon var6, ButtonIcon var7, GameMessage... var8) {
      super(var1, var2, var3, var4, var5);
      this.onIcon = var6;
      this.offIcon = var7;
      this.tooltips = var8;
   }

   public FormContentIconToggleButton(int var1, int var2, FormInputSize var3, ButtonColor var4, ButtonIcon var5, ButtonIcon var6, GameMessage... var7) {
      this(var1, var2, var3.height, var3, var4, var5, var6, var7);
   }

   public FormContentIconToggleButton(int var1, int var2, int var3, FormInputSize var4, ButtonColor var5, ButtonIcon var6, GameMessage... var7) {
      this(var1, var2, var3, var4, var5, var6, var6, var7);
   }

   public FormContentIconToggleButton(int var1, int var2, FormInputSize var3, ButtonColor var4, ButtonIcon var5, GameMessage... var6) {
      this(var1, var2, var3.height, var3, var4, var5, var6);
   }

   protected int getIconDrawX(ButtonIcon var1, int var2, int var3) {
      return var2 + var3 / 2 - var1.texture.getWidth() / 2;
   }

   protected int getIconDrawY(ButtonIcon var1, int var2, int var3) {
      return var2 + var3 / 2 - var1.texture.getHeight() / 2;
   }

   public Color getContentColor(ButtonIcon var1) {
      return (Color)var1.colorGetter.apply(this.getButtonState());
   }

   protected void drawContent(int var1, int var2, int var3, int var4) {
      boolean var5 = this.isToggled();
      ButtonIcon var6 = var5 ? this.onIcon : this.offIcon;
      if (var6 != null) {
         var6.texture.initDraw().color(this.getContentColor(var6)).mirror(var5 ? this.onMirrorX : this.offMirrorX, var5 ? this.onMirrorY : this.offMirrorY).draw(this.getIconDrawX(var6, var1, var3), this.getIconDrawY(var6, var2, var4));
      }
   }

   protected void addTooltips(PlayerMob var1) {
      super.addTooltips(var1);
      GameTooltips var2 = this.getTooltips();
      if (var2 != null) {
         Screen.addTooltip(var2, TooltipLocation.FORM_FOCUS);
      }

   }

   public GameTooltips getTooltips() {
      if (this.tooltips.length == 0) {
         return null;
      } else {
         StringTooltips var1 = new StringTooltips();
         GameMessage[] var2 = this.tooltips;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            GameMessage var5 = var2[var4];
            var1.add(var5.translate(), 350);
         }

         return var1;
      }
   }

   public void setTooltips(GameMessage... var1) {
      this.tooltips = var1;
   }

   public void setIcon(ButtonIcon var1, ButtonIcon var2) {
      this.onIcon = var1;
      this.offIcon = var2;
   }

   public void setIcon(ButtonIcon var1) {
      this.setIcon(var1, var1);
   }

   public FormContentIconToggleButton onMirror(boolean var1, boolean var2) {
      this.onMirrorX = var1;
      this.onMirrorY = var2;
      return this;
   }

   public FormContentIconToggleButton onMirrorX() {
      this.onMirrorX = true;
      return this;
   }

   public FormContentIconToggleButton onMirrorY() {
      this.onMirrorY = true;
      return this;
   }

   public FormContentIconToggleButton offMirror(boolean var1, boolean var2) {
      this.offMirrorX = var1;
      this.offMirrorY = var2;
      return this;
   }

   public FormContentIconToggleButton offMirrorX() {
      this.offMirrorX = true;
      return this;
   }

   public FormContentIconToggleButton offMirrorY() {
      this.offMirrorY = true;
      return this;
   }
}
