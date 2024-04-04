package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.gameTool.GameTool;
import necesse.engine.localization.Localization;
import necesse.engine.state.State;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class CreateOrExpandGlobalZoneGameTool implements GameTool {
   public final Level level;
   private HudDrawElement hudElement;
   private Point mouseDownTile;
   private boolean isRemoving;

   public CreateOrExpandGlobalZoneGameTool(Level var1) {
      this.level = var1;
   }

   public void init() {
      this.level.hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            Point var4 = CreateOrExpandGlobalZoneGameTool.this.mouseDownTile;
            if (var4 != null) {
               int var5 = var2.getMouseLevelTilePosX();
               int var6 = var2.getMouseLevelTilePosY();
               Rectangle var7 = new Rectangle(Math.min(var4.x, var5) * 32, Math.min(var4.y, var6) * 32, (Math.abs(var4.x - var5) + 1) * 32, (Math.abs(var4.y - var6) + 1) * 32);
               Color var8;
               Color var9;
               if (CreateOrExpandGlobalZoneGameTool.this.isRemoving) {
                  var8 = new Color(255, 0, 0, 170);
                  var9 = new Color(255, 0, 0, 100);
               } else {
                  var8 = new Color(0, 255, 0, 170);
                  var9 = new Color(0, 255, 0, 100);
               }

               final SharedTextureDrawOptions var10 = Zoning.getRectangleDrawOptions(var7, var8, var9, var2);
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return -2000000;
                  }

                  public void draw(TickManager var1) {
                     var10.draw();
                  }
               });
            }

         }
      });
   }

   public boolean inputEvent(InputEvent var1) {
      State var2 = GlobalData.getCurrentState();
      int var3 = var2.getCamera().getMouseLevelTilePosX(var1);
      int var4 = var2.getCamera().getMouseLevelTilePosY(var1);
      if (this.mouseDownTile != null && !var1.state) {
         int var5 = Math.min(this.mouseDownTile.x, var3);
         int var6 = Math.min(this.mouseDownTile.y, var4);
         int var7 = Math.max(this.mouseDownTile.x, var3);
         int var8 = Math.max(this.mouseDownTile.y, var4);
         Rectangle var9;
         if (var1.getID() == -100) {
            if (!this.isRemoving) {
               var9 = new Rectangle(var5, var6, var7 - var5 + 1, var8 - var6 + 1);
               this.onExpandedZone(var9);
               var1.use();
               this.mouseDownTile = null;
            }
         } else if (var1.getID() == -99 && this.isRemoving) {
            this.mouseDownTile = null;
            var9 = new Rectangle(var5, var6, var7 - var5 + 1, var8 - var6 + 1);
            this.onShrankZone(var9);
            var1.use();
         }
      }

      if (!var1.isMoveUsed() && (var2.getFormManager() == null || !var2.getFormManager().isMouseOver(var1)) && !var1.isUsed() && var1.state) {
         if (var1.getID() == -100) {
            if (this.mouseDownTile != null && this.isRemoving) {
               this.mouseDownTile = null;
            } else {
               this.mouseDownTile = new Point(var3, var4);
               this.isRemoving = false;
            }

            var1.use();
            return true;
         }

         if (var1.getID() == -99) {
            if (this.mouseDownTile != null && !this.isRemoving) {
               this.mouseDownTile = null;
            } else {
               this.mouseDownTile = new Point(var3, var4);
               this.isRemoving = true;
            }

            var1.use();
            return true;
         }
      }

      return false;
   }

   public boolean controllerEvent(ControllerEvent var1) {
      State var2 = GlobalData.getCurrentState();
      int var3 = var2.getCamera().getMouseLevelTilePosX(Screen.mousePos());
      int var4 = var2.getCamera().getMouseLevelTilePosY(Screen.mousePos());
      if (this.mouseDownTile != null && var1.isButton && !var1.buttonState) {
         int var5 = Math.min(this.mouseDownTile.x, var3);
         int var6 = Math.min(this.mouseDownTile.y, var4);
         int var7 = Math.max(this.mouseDownTile.x, var3);
         int var8 = Math.max(this.mouseDownTile.y, var4);
         Rectangle var9;
         if (var1.getState() == ControllerInput.MENU_NEXT) {
            if (!this.isRemoving) {
               var9 = new Rectangle(var5, var6, var7 - var5 + 1, var8 - var6 + 1);
               this.onExpandedZone(var9);
               var1.use();
               this.mouseDownTile = null;
            }
         } else if (var1.getState() == ControllerInput.MENU_PREV && this.isRemoving) {
            this.mouseDownTile = null;
            var9 = new Rectangle(var5, var6, var7 - var5 + 1, var8 - var6 + 1);
            this.onShrankZone(var9);
            var1.use();
         }
      }

      if (!var1.isUsed() && var1.isButton && var1.buttonState && (var2.getFormManager() == null || !var2.getFormManager().isMouseOver(Screen.mousePos()))) {
         if (var1.getState() == ControllerInput.MENU_NEXT) {
            if (this.mouseDownTile != null && this.isRemoving) {
               this.mouseDownTile = null;
            } else {
               this.mouseDownTile = new Point(var3, var4);
               this.isRemoving = false;
            }

            var1.use();
            return true;
         }

         if (var1.getState() == ControllerInput.MENU_PREV) {
            if (this.mouseDownTile != null && !this.isRemoving) {
               this.mouseDownTile = null;
            } else {
               this.mouseDownTile = new Point(var3, var4);
               this.isRemoving = true;
            }

            var1.use();
            return true;
         }
      }

      return false;
   }

   public void isCancelled() {
      this.hudElement.remove();
   }

   public void isCleared() {
      this.hudElement.remove();
   }

   public GameTooltips getTooltips() {
      ListGameTooltips var1 = new ListGameTooltips();
      if (this.mouseDownTile == null) {
         if (Input.lastInputIsController) {
            var1.add((Object)(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "settlementexpandzone"))));
            var1.add((Object)(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "settlementshrinkzone"))));
         } else {
            var1.add((Object)(new InputTooltip(-100, Localization.translate("ui", "settlementexpandzone"))));
            var1.add((Object)(new InputTooltip(-99, Localization.translate("ui", "settlementshrinkzone"))));
         }
      }

      return var1;
   }

   public abstract void onExpandedZone(Rectangle var1);

   public abstract void onShrankZone(Rectangle var1);
}
