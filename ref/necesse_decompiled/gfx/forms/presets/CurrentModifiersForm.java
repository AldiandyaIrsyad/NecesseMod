package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public abstract class CurrentModifiersForm extends Form {
   private ListGameTooltips tooltips;
   private boolean isClickRemoving;

   public static ListGameTooltips getTooltips(PlayerMob var0) {
      ListGameTooltips var1 = new ListGameTooltips();
      if (var0 != null) {
         var1.add(Localization.translate("buffmodifiers", "currentmodifiers"));
         List var2 = (List)var0.buffManager.getModifierTooltips().stream().map((var0x) -> {
            return var0x.toTooltip(true);
         }).collect(Collectors.toList());
         if (var2.isEmpty()) {
            var1.add((Object)(new StringTooltips(Localization.translate("bufftooltip", "nomodifiers"), GameColor.YELLOW)));
         } else {
            var1.addAll(var2);
         }
      }

      return var1;
   }

   public CurrentModifiersForm() {
      super((String)"currentmodifiers", 200, 300);
      this.onDragged((var1) -> {
         var1.x = GameMath.limit(var1.x, -this.getWidth() + 20, Screen.getHudWidth() - 20);
         var1.y = GameMath.limit(var1.y, -this.getHeight() + 20, Screen.getHudHeight() - 20);
      });
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (!var1.isUsed()) {
         if (var1.getID() == -99) {
            boolean var4 = this.isMouseOver(var1);
            if (var1.state && var4) {
               this.isClickRemoving = true;
            } else {
               if (this.isClickRemoving && var4) {
                  this.onRemove();
               }

               this.isClickRemoving = false;
            }
         }

      }
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public abstract void onRemove();

   public void update(PlayerMob var1) {
      if (var1 != null) {
         this.tooltips = getTooltips(var1);
         this.setWidth(this.tooltips.getWidth());
         this.setHeight(this.tooltips.getHeight());
      } else {
         this.setWidth(100);
         this.setHeight(200);
      }

      this.setDraggingBox(new Rectangle(this.getWidth(), this.getHeight()));
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.setX(GameMath.limit(this.getX(), -this.getWidth() + 20, Screen.getHudWidth() - 20));
      this.setY(GameMath.limit(this.getY(), -this.getHeight() + 20, Screen.getHudHeight() - 20));
   }

   public void drawBase(TickManager var1) {
      int var2 = GameBackground.itemTooltip.getContentPadding();
      GameBackground.itemTooltip.getDrawOptions(this.getX(), this.getY(), this.getWidth() + var2 * 2, this.getHeight() + var2 * 2).draw();
   }

   public void drawEdge(TickManager var1) {
      int var2 = GameBackground.itemTooltip.getContentPadding();
      GameBackground.itemTooltip.getEdgeDrawOptions(this.getX(), this.getY(), this.getWidth() + var2 * 2, this.getHeight() + var2 * 2).draw();
   }

   public void drawComponents(TickManager var1, PlayerMob var2, Rectangle var3) {
      super.drawComponents(var1, var2, var3);
      if (this.tooltips != null) {
         int var4 = GameBackground.itemTooltip.getContentPadding();
         this.tooltips.draw(var4, var4, GameColor.DEFAULT_COLOR);
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.update(var2);
      super.draw(var1, var2, var3);
   }
}
