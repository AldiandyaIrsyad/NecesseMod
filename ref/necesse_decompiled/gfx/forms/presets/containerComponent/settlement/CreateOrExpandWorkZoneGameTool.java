package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.gameTool.GameTool;
import necesse.engine.localization.Localization;
import necesse.engine.state.State;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ObjectValue;
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
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public abstract class CreateOrExpandWorkZoneGameTool implements GameTool {
   public final Level level;
   private HudDrawElement hudElement;
   private SettlementWorkZone lastHoverZone;
   private Point mouseDownTile;
   private boolean isRemoving;

   public CreateOrExpandWorkZoneGameTool(Level var1) {
      this.level = var1;
   }

   public void init() {
      this.level.hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            Point var4 = CreateOrExpandWorkZoneGameTool.this.mouseDownTile;
            if (var4 != null) {
               int var5 = var2.getMouseLevelTilePosX();
               int var6 = var2.getMouseLevelTilePosY();
               Rectangle var7 = new Rectangle(Math.min(var4.x, var5) * 32, Math.min(var4.y, var6) * 32, (Math.abs(var4.x - var5) + 1) * 32, (Math.abs(var4.y - var6) + 1) * 32);
               Color var8;
               Color var9;
               if (CreateOrExpandWorkZoneGameTool.this.isRemoving) {
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

   protected SettlementWorkZone getHoverZone(State var1, InputPosition var2) {
      int var3 = var1.getCamera().getMouseLevelTilePosX(var2);
      int var4 = var1.getCamera().getMouseLevelTilePosY(var2);
      return (SettlementWorkZone)this.streamEditZones().filter((var2x) -> {
         return var2x.containsTile(var3, var4);
      }).findFirst().orElse((Object)null);
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
               Point var10 = this.mouseDownTile;
               SettlementWorkZone var11 = (SettlementWorkZone)this.streamEditZones().filter((var1x) -> {
                  return var1x.containsTile(this.mouseDownTile.x, this.mouseDownTile.y);
               }).findFirst().orElse((Object)null);
               if (var11 == null) {
                  var10 = null;
                  var11 = (SettlementWorkZone)this.streamEditZones().filter((var1x) -> {
                     Rectangle var2 = var1x.getTileBounds();
                     if (var2 != null) {
                        Rectangle var3 = var2.intersection(var9);
                        if (!var3.isEmpty()) {
                           for(int var4 = 0; var4 < var3.width; ++var4) {
                              for(int var5 = 0; var5 < var3.height; ++var5) {
                                 if (var1x.containsTile(var3.x + var4, var3.y + var5)) {
                                    return true;
                                 }
                              }
                           }
                        }
                     }

                     return false;
                  }).findFirst().orElse((Object)null);
               }

               if (var11 != null) {
                  this.onExpandedZone(var11, var9, var10);
               } else {
                  this.onCreatedNewZone(var9, this.mouseDownTile);
               }

               var1.use();
               this.mouseDownTile = null;
            }
         } else if (var1.getID() == -99 && this.isRemoving) {
            this.mouseDownTile = null;
            var9 = new Rectangle(var5, var6, var7 - var5 + 1, var8 - var6 + 1);
            List var13 = (List)this.streamEditZones().map((var1x) -> {
               Rectangle var2 = var1x.getTileBounds();
               if (var2 != null) {
                  Rectangle var3 = var2.intersection(var9);
                  if (var3.width > 0 && var3.height > 0) {
                     return new ObjectValue(var1x, var3);
                  }
               }

               return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            Iterator var14 = var13.iterator();

            while(var14.hasNext()) {
               ObjectValue var12 = (ObjectValue)var14.next();
               this.onRemovedZone((SettlementWorkZone)var12.object, (Rectangle)var12.value);
            }

            var1.use();
         }
      }

      if (!var1.isMoveUsed()) {
         if (var2.getFormManager() == null || !var2.getFormManager().isMouseOver(var1)) {
            this.lastHoverZone = this.getHoverZone(var2, var1.pos);
            if (this.lastHoverZone != null) {
               var1.useMove();
            }

            if (!var1.isUsed() && var1.state) {
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
         }
      } else {
         this.lastHoverZone = null;
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
               Point var10 = this.mouseDownTile;
               SettlementWorkZone var11 = (SettlementWorkZone)this.streamEditZones().filter((var1x) -> {
                  return var1x.containsTile(this.mouseDownTile.x, this.mouseDownTile.y);
               }).findFirst().orElse((Object)null);
               if (var11 == null) {
                  var10 = null;
                  var11 = (SettlementWorkZone)this.streamEditZones().filter((var1x) -> {
                     Rectangle var2 = var1x.getTileBounds();
                     if (var2 != null) {
                        Rectangle var3 = var2.intersection(var9);
                        if (!var3.isEmpty()) {
                           for(int var4 = 0; var4 < var3.width; ++var4) {
                              for(int var5 = 0; var5 < var3.height; ++var5) {
                                 if (var1x.containsTile(var3.x + var4, var3.y + var5)) {
                                    return true;
                                 }
                              }
                           }
                        }
                     }

                     return false;
                  }).findFirst().orElse((Object)null);
               }

               if (var11 != null) {
                  this.onExpandedZone(var11, var9, var10);
               } else {
                  this.onCreatedNewZone(var9, this.mouseDownTile);
               }

               var1.use();
               this.mouseDownTile = null;
            }
         } else if (var1.getState() == ControllerInput.MENU_PREV && this.isRemoving) {
            this.mouseDownTile = null;
            var9 = new Rectangle(var5, var6, var7 - var5 + 1, var8 - var6 + 1);
            List var13 = (List)this.streamEditZones().map((var1x) -> {
               Rectangle var2 = var1x.getTileBounds();
               if (var2 != null) {
                  Rectangle var3 = var2.intersection(var9);
                  if (var3.width > 0 && var3.height > 0) {
                     return new ObjectValue(var1x, var3);
                  }
               }

               return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            Iterator var14 = var13.iterator();

            while(var14.hasNext()) {
               ObjectValue var12 = (ObjectValue)var14.next();
               this.onRemovedZone((SettlementWorkZone)var12.object, (Rectangle)var12.value);
            }

            var1.use();
         }
      }

      if (!var1.isUsed() && var1.isButton && var1.buttonState) {
         if (var2.getFormManager() == null || !var2.getFormManager().isMouseOver(Screen.mousePos())) {
            this.lastHoverZone = this.getHoverZone(var2, Screen.mousePos());
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
      } else {
         this.lastHoverZone = null;
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
            var1.add((Object)(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "settlementcreateorexpandzone"))));
            var1.add((Object)(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "settlementshrinkzone"))));
         } else {
            var1.add((Object)(new InputTooltip(-100, Localization.translate("ui", "settlementcreateorexpandzone"))));
            var1.add((Object)(new InputTooltip(-99, Localization.translate("ui", "settlementshrinkzone"))));
         }
      }

      return var1;
   }

   public abstract Stream<SettlementWorkZone> streamEditZones();

   public abstract void onCreatedNewZone(Rectangle var1, Point var2);

   public abstract void onRemovedZone(SettlementWorkZone var1, Rectangle var2);

   public abstract void onExpandedZone(SettlementWorkZone var1, Rectangle var2, Point var3);
}
