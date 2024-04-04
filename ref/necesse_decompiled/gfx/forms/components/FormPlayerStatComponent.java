package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormPlayerStatComponent<T> extends FormComponent implements FormPositionContainer {
   protected FormPosition position;
   protected int width;
   protected GameMessage displayName;
   protected Supplier<T> statSupplier;
   protected Function<T, String> formatter;
   protected boolean isHovering;
   public FontOptions fontOptions;

   public FormPlayerStatComponent(int var1, int var2, int var3, GameMessage var4, Supplier<T> var5, Function<T, String> var6) {
      this.fontOptions = (new FontOptions(16)).color(Settings.UI.activeTextColor);
      this.position = new FormFixedPosition(var1, var2);
      this.width = var3;
      this.displayName = var4;
      this.statSupplier = var5;
      if (var6 == null) {
         var6 = Objects::toString;
      }

      this.formatter = var6;
   }

   public FormPlayerStatComponent(int var1, int var2, int var3, GameMessage var4, Supplier<T> var5) {
      this(var1, var2, var3, var4, var5, (Function)null);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public String getTooltip(boolean var1, T var2, String var3) {
      return !var1 ? this.displayName.translate() + ": " + var3 : null;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      boolean var4 = true;
      Object var5 = this.statSupplier.get();
      String var6 = (String)this.formatter.apply(var5);
      String var7 = var6;
      int var8 = FontManager.bit.getWidthCeil(var6, this.fontOptions);
      if (var8 > this.width / 2) {
         var7 = GameUtils.maxString(var6, this.fontOptions, this.width / 2);
         var8 = FontManager.bit.getWidthCeil(var6, this.fontOptions);
         var4 = false;
      }

      FontManager.bit.drawString((float)(this.getX() + this.width - var8), (float)(this.getY() + 2), var7, this.fontOptions);
      String var9 = this.displayName.translate();
      String var10 = var9;
      int var11 = FontManager.bit.getWidthCeil(var9, this.fontOptions);
      if (var11 > this.width - var8 - 10) {
         var10 = GameUtils.maxString(var9, this.fontOptions, this.width - var8 - 10);
         var4 = false;
      }

      FontManager.bit.drawString((float)this.getX(), (float)(this.getY() + 2), var10, this.fontOptions);
      if (this.isHovering) {
         String var12 = this.getTooltip(var4, var5, var6);
         if (var12 != null) {
            Screen.addTooltip(new StringTooltips(var12), TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.fontOptions.getSize() + 4));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public GameMessage getDisplayName() {
      return this.displayName;
   }
}
