package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
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

public class FormVarToggleIconButton extends FormButton implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private int height;
   private GameMessage[] tooltips;
   private GameSprite onSprite;
   private GameSprite offSprite;
   private Supplier<Boolean> varGetter;

   public FormVarToggleIconButton(int var1, int var2, GameSprite var3, GameSprite var4, Consumer<Boolean> var5, Supplier<Boolean> var6, GameMessage... var7) {
      this(var1, var2, var3, var4, var5, var6, var3.width, var3.height, var7);
   }

   public FormVarToggleIconButton(int var1, int var2, GameSprite var3, GameSprite var4, Consumer<Boolean> var5, Supplier<Boolean> var6, int var7, int var8, GameMessage... var9) {
      this.position = new FormFixedPosition(var1, var2);
      this.width = var7;
      this.height = var8;
      this.onSprite = var3;
      this.offSprite = var4;
      this.varGetter = var6;
      this.tooltips = var9;
      this.onClicked((var2x) -> {
         var5.accept(!(Boolean)var6.get());
      });
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Color var4 = this.getDrawColor();
      if ((Boolean)this.varGetter.get()) {
         this.onSprite.initDraw().color(var4).draw(this.getX(), this.getY());
      } else {
         this.offSprite.initDraw().color(var4).draw(this.getX(), this.getY());
      }

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

   public void setIconSprite(GameSprite var1, GameSprite var2) {
      this.onSprite = var1;
      this.offSprite = var2;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
