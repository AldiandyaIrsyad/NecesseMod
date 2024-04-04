package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public class FormTextButton extends FormButton implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private String text;
   private String drawText;
   private String tooltip;
   public int tooltipMaxWidth;
   public FormInputSize size;
   public ButtonColor color;

   public FormTextButton(String var1, String var2, int var3, int var4, int var5, FormInputSize var6, ButtonColor var7) {
      this.tooltipMaxWidth = 400;
      this.position = new FormFixedPosition(var3, var4);
      this.width = var5;
      this.size = var6;
      this.color = var7;
      this.setText(var1);
      this.setTooltip(var2);
   }

   public FormTextButton(String var1, String var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE);
   }

   public FormTextButton(String var1, int var2, int var3, int var4, FormInputSize var5, ButtonColor var6) {
      this(var1, (String)null, var2, var3, var4, var5, var6);
   }

   public FormTextButton(String var1, int var2, int var3, int var4) {
      this(var1, (String)null, var2, var3, var4);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      ButtonState var5 = this.getButtonState();
      int var6 = 0;
      boolean var7 = this.isDown() && this.isHovering();
      if (var7) {
         this.size.getButtonDownDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
         var6 = this.size.buttonDownContentDrawOffset;
      } else {
         this.size.getButtonDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      }

      Rectangle var8 = this.size.getContentRectangle(this.width);
      FormShader.FormShaderState var9 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(var8.x, var8.y, var8.width, var8.height));

      try {
         FontOptions var10 = this.size.getFontOptions().color(this.getTextColor());
         String var11 = this.getDrawText();
         int var12 = this.width / 2 - FontManager.bit.getWidthCeil(var11, var10) / 2;
         FontManager.bit.drawString((float)var12, (float)(var6 + this.size.fontDrawOffset), var11, var10);
      } finally {
         var9.end();
      }

      if (var7) {
         this.size.getButtonDownEdgeDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      } else {
         this.size.getButtonEdgeDrawOptions(this.color, var5, this.getX(), this.getY(), this.width, var4).draw();
      }

      if (this.isHovering()) {
         this.addTooltips(var2);
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
   }

   public String getText() {
      return this.text;
   }

   public String getDrawText() {
      return this.drawText;
   }

   protected void addTooltips(PlayerMob var1) {
      StringTooltips var2 = new StringTooltips();
      if (!this.getDrawText().equals(this.getText())) {
         var2.add(this.getText());
      }

      if (this.tooltip != null && !this.tooltip.isEmpty()) {
         var2.add(this.tooltip, this.tooltipMaxWidth);
      }

      if (var2.getSize() != 0) {
         Screen.addTooltip(var2, TooltipLocation.FORM_FOCUS);
      }

   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
      this.updateDrawText();
   }

   public void setText(String var1) {
      this.text = var1;
      this.updateDrawText();
   }

   private void updateDrawText() {
      this.drawText = this.text;

      for(int var1 = this.getMaxTextWidth(); !this.drawText.isEmpty() && FontManager.bit.getWidthCeil(this.drawText, this.size.getFontOptions().color(Settings.UI.activeTextColor)) > var1; this.drawText = this.drawText.substring(0, this.drawText.length() - 1)) {
      }

   }

   protected int getMaxTextWidth() {
      return this.width;
   }

   public void setTooltip(String var1) {
      this.tooltip = var1;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
