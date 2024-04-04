package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
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
import necesse.engine.localization.message.GameMessage;
import necesse.engine.sound.SoundEffect;
import necesse.engine.state.State;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class SelectTileGameTool implements GameTool {
   public final Level level;
   public GameMessage tooltip;
   public boolean displayErrorTip;
   protected TilePosition lastHoverPos;
   public Rectangle lastHoverBounds;
   protected GameMessage lastHoverErr;
   protected HudDrawElement hudElement;

   public SelectTileGameTool(Level var1, GameMessage var2, boolean var3) {
      this.level = var1;
      this.tooltip = var2;
      this.displayErrorTip = var3;
      var1.hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            final DrawOptionsList var4 = new DrawOptionsList();
            TilePosition var5 = SelectTileGameTool.this.lastHoverPos;
            if (var5 != null) {
               Color var6 = SelectTileGameTool.this.lastHoverErr == null ? new Color(50, 200, 50) : new Color(200, 50, 50);
               Rectangle var7 = SelectTileGameTool.this.lastHoverBounds;
               DrawOptions var8;
               if (var7 != null) {
                  var4.add(HUD.tileBoundOptions(var2, var6, false, var7));
                  var8 = SelectTileGameTool.this.getIconTexture(var6, var2.getDrawX((int)(var7.getCenterX() * 32.0)), var2.getDrawY((int)(var7.getCenterY() * 32.0)));
                  if (var8 != null) {
                     var4.add(var8);
                  }
               } else {
                  var4.add(HUD.tileBoundOptions(var2, var6, false, var5.tileX, var5.tileY, var5.tileX, var5.tileY));
                  var8 = SelectTileGameTool.this.getIconTexture(var6, var2.getTileDrawX(var5.tileX) + 16, var2.getTileDrawY(var5.tileY) + 16);
                  if (var8 != null) {
                     var4.add(var8);
                  }
               }
            }

            if (!var4.isEmpty()) {
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return -1000000;
                  }

                  public void draw(TickManager var1) {
                     var4.draw();
                  }
               });
            }

         }
      });
   }

   public SelectTileGameTool(Level var1, GameMessage var2) {
      this(var1, var2, true);
   }

   public abstract DrawOptions getIconTexture(Color var1, int var2, int var3);

   public boolean inputEvent(InputEvent var1) {
      State var2 = GlobalData.getCurrentState();
      if (var2.getFormManager() != null && var2.getFormManager().isMouseOver(var1)) {
         return false;
      } else {
         int var3;
         int var4;
         if (var1.isMouseMoveEvent()) {
            var3 = var2.getCamera().getMouseLevelTilePosX(var1);
            var4 = var2.getCamera().getMouseLevelTilePosY(var1);
            this.lastHoverPos = new TilePosition(this.level, var3, var4);
            this.lastHoverErr = this.isValidTile(this.lastHoverPos);
            return true;
         } else {
            if (var1.getID() == -100 && var1.state) {
               var3 = var2.getCamera().getMouseLevelTilePosX(var1);
               var4 = var2.getCamera().getMouseLevelTilePosY(var1);
               this.lastHoverPos = new TilePosition(this.level, var3, var4);
               this.lastHoverErr = this.isValidTile(this.lastHoverPos);
               if (this.lastHoverErr == null) {
                  boolean var5 = this.onSelected(var1, this.lastHoverPos);
                  if (var5) {
                     this.hudElement.remove();
                     Screen.clearGameTool(this);
                     Screen.playSound(GameResources.tick, SoundEffect.ui());
                  }

                  return var5;
               }
            } else if (var1.getID() == -99 && var1.state) {
               Screen.clearGameTool(this);
               return true;
            }

            return false;
         }
      }
   }

   public boolean controllerEvent(ControllerEvent var1) {
      State var2 = GlobalData.getCurrentState();
      if (var2.getFormManager() != null && var2.getFormManager().isMouseOver(Screen.input().mousePos())) {
         return false;
      } else {
         int var3;
         int var4;
         if (var1.getState() != ControllerInput.CURSOR && var1.getState() != ControllerInput.AIM) {
            if (var1.getState() == ControllerInput.MENU_SELECT && var1.buttonState) {
               var3 = var2.getCamera().getMouseLevelTilePosX();
               var4 = var2.getCamera().getMouseLevelTilePosY();
               this.lastHoverPos = new TilePosition(this.level, var3, var4);
               this.lastHoverErr = this.isValidTile(this.lastHoverPos);
               if (this.lastHoverErr == null) {
                  boolean var5 = this.onSelected(InputEvent.ControllerButtonEvent(var1, Screen.tickManager), this.lastHoverPos);
                  if (var5) {
                     this.hudElement.remove();
                     Screen.clearGameTool(this);
                     Screen.playSound(GameResources.tick, SoundEffect.ui());
                  }

                  return var5;
               }
            } else if (var1.getState() == ControllerInput.MENU_BACK && var1.buttonState) {
               Screen.clearGameTool(this);
               return true;
            }

            return false;
         } else {
            var3 = var2.getCamera().getMouseLevelTilePosX();
            var4 = var2.getCamera().getMouseLevelTilePosY();
            this.lastHoverPos = new TilePosition(this.level, var3, var4);
            this.lastHoverErr = this.isValidTile(this.lastHoverPos);
            return true;
         }
      }
   }

   public void isCancelled() {
      this.hudElement.remove();
      this.onSelected((InputEvent)null, (TilePosition)null);
   }

   public void isCleared() {
      this.hudElement.remove();
      this.onSelected((InputEvent)null, (TilePosition)null);
   }

   public GameTooltips getTooltips() {
      ListGameTooltips var1 = new ListGameTooltips();
      if (this.lastHoverErr != null && this.displayErrorTip) {
         var1.add(this.lastHoverErr.translate());
      }

      if (this.tooltip != null) {
         if (Input.lastInputIsController) {
            var1.add((Object)(new InputTooltip(ControllerInput.MENU_SELECT, this.tooltip.translate())));
            var1.add((Object)(new InputTooltip(ControllerInput.MENU_BACK, Localization.translate("ui", "cancelbutton"))));
         } else {
            var1.add((Object)(new InputTooltip(-100, this.tooltip.translate())));
            var1.add((Object)(new InputTooltip(-99, Localization.translate("ui", "cancelbutton"))));
         }
      }

      return var1.size() == 0 ? null : var1;
   }

   public abstract boolean onSelected(InputEvent var1, TilePosition var2);

   public abstract GameMessage isValidTile(TilePosition var1);
}
