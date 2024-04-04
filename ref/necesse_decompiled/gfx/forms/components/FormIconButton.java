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
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.ButtonStateTextures;

public class FormIconButton extends FormButton implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private int height;
   private GameMessage[] tooltips;
   public ButtonStateTextures textures;

   public FormIconButton(int var1, int var2, ButtonStateTextures var3, GameMessage... var4) {
      this(var1, var2, var3, var3.active.getWidth(), var3.active.getHeight(), var4);
   }

   public FormIconButton(int var1, int var2, ButtonStateTextures var3, int var4, int var5, GameMessage... var6) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var4;
      this.height = var5;
      this.textures = var3;
      this.tooltips = var6;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      ButtonState var5 = this.getButtonState();
      GameTexture var6;
      if (this.isDown() && this.isHovering()) {
         var6 = (GameTexture)var5.downTextureGetter.apply(this.textures);
      } else {
         var6 = (GameTexture)var5.textureGetter.apply(this.textures);
      }

      var6.initDraw().color(var4).draw(this.getX(), this.getY());
      if (this.isHovering()) {
         GameTooltips var7 = this.getTooltips();
         if (var7 != null) {
            Screen.addTooltip(var7, TooltipLocation.FORM_FOCUS);
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

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
