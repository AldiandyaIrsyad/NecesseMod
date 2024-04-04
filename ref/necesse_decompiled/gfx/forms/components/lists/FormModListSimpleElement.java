package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;

public class FormModListSimpleElement extends Form {
   public FormModListSimpleElement(String var1, int var2, final ModLoadLocation var3, final boolean var4, Color var5, final GameTooltips var6) {
      super(var2, 24);
      this.drawBase = false;
      this.shouldLimitDrawArea = false;
      if (var3 != null) {
         this.addComponent(new FormCustomDraw(4, 0, 20, 20) {
            public void draw(TickManager var1, PlayerMob var2, Rectangle var3x) {
               HoverStateTextures var4x = (HoverStateTextures)var3.iconSupplier.get();
               GameTexture var5 = var4x.active;
               Color var6 = var4 ? Settings.UI.activeTextColor : Settings.UI.inactiveTextColor;
               if (this.isHovering()) {
                  var6 = Settings.UI.highlightTextColor;
                  var5 = var4x.highlighted;
               }

               var5.initDraw().size(24).color(var6).draw(this.getX(), this.getY());
               if (this.isHovering()) {
                  Screen.addTooltip(new StringTooltips(var3.message.translate()), TooltipLocation.FORM_FOCUS);
               }

            }
         });
      }

      int var7 = var3 == null ? 4 : 28;
      this.addComponent(new FormLabel(var1, (new FontOptions(16)).color(var5), -1, var7, 4, var2 - var7));
      if (var6 != null) {
         this.addComponent(new FormMouseHover(var7, 0, var2 - var7, this.getHeight()) {
            public GameTooltips getTooltips(PlayerMob var1) {
               return var6;
            }
         }, 1000);
      }

   }

   public FormModListSimpleElement(String var1, int var2, ModLoadLocation var3, boolean var4, Color var5, String var6) {
      this(var1, var2, var3, var4, var5, (GameTooltips)(var6 == null ? null : new StringTooltips(var6)));
   }

   public FormModListSimpleElement(String var1, int var2, ModLoadLocation var3, boolean var4, Color var5) {
      this(var1, var2, var3, var4, var5, (GameTooltips)null);
   }
}
