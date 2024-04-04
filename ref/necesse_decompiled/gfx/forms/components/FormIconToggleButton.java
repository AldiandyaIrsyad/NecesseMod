package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormIconToggleButton extends FormButtonToggle implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private int height;
   private GameMessage[] tooltips;
   private GameSprite sprite;

   public FormIconToggleButton(int var1, int var2, GameSprite var3, GameMessage... var4) {
      this(var1, var2, var3, var3.width, var3.height, var4);
   }

   public FormIconToggleButton(int var1, int var2, GameSprite var3, int var4, int var5, GameMessage... var6) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var4;
      this.height = var5;
      this.sprite = var3;
      this.tooltips = var6;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      this.sprite.initDraw().color(var4).draw(this.getX(), this.getY());
      if (this.isHovering()) {
         GameTooltips var5 = this.getTooltips();
         if (var5 != null) {
            Screen.addTooltip(var5, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
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
            var1.add(var5.translate());
         }

         return var1;
      }
   }

   public void setTooltips(GameMessage... var1) {
      this.tooltips = var1;
   }

   public void setIconSprite(GameSprite var1) {
      this.sprite = var1;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
