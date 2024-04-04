package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.gameTool.GameTool;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.state.State;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.Zoning;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public abstract class SelectSettlersContainerGameTool implements GameTool {
   public static int maxPixelDistanceToConsiderClick = 4;
   public final Client client;
   public final Level level;
   public final SelectedSettlersHandler selectedSettlers;
   protected Point mouseDownPos;
   protected boolean mouseDownIsLeftClick;
   protected HudDrawElement hudElement;
   protected Screen.CURSOR cursor;
   protected ListGameTooltips tooltips;
   protected InputEvent ignoreNextInputEvent;

   public SelectSettlersContainerGameTool(Client var1, SelectedSettlersHandler var2) {
      this.client = var1;
      this.level = var1.getLevel();
      this.selectedSettlers = var2;
   }

   public abstract SettlementToolHandler getCurrentToolHandler();

   public abstract Stream<Mob> streamAllSettlers(Rectangle var1);

   public abstract void commandAttack(Mob var1);

   public abstract void commandGuard(int var1, int var2);

   public abstract void commandGuard(ArrayList<Point> var1);

   public void init() {
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      this.level.hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            Point var4 = SelectSettlersContainerGameTool.this.mouseDownPos;
            if (var4 != null) {
               Point var5 = var2.getMouseLevelPos();
               double var6 = var4.distance(var5);
               final Object var8 = null;
               SettlementToolHandler var9 = SelectSettlersContainerGameTool.this.getCurrentToolHandler();
               if (var6 <= (double)SelectSettlersContainerGameTool.maxPixelDistanceToConsiderClick) {
                  if (var9 != null) {
                     if (SelectSettlersContainerGameTool.this.mouseDownIsLeftClick) {
                        var8 = var9.getLeftClickDraw(var4, var5);
                     } else {
                        var8 = var9.getRightClickDraw(var4, var5);
                     }
                  }
               } else {
                  Rectangle var10 = new Rectangle(Math.min(var4.x, var5.x), Math.min(var4.y, var5.y), Math.max(Math.abs(var4.x - var5.x), 1), Math.max(Math.abs(var4.y - var5.y), 1));
                  if (var9 != null) {
                     if (SelectSettlersContainerGameTool.this.mouseDownIsLeftClick) {
                        var8 = var9.getLeftClickSelectionDraw(var4, var5, var10);
                     } else {
                        var8 = var9.getRightClickSelectionDraw(var4, var5, var10);
                     }
                  }

                  if (var8 == null) {
                     if (SelectSettlersContainerGameTool.this.mouseDownIsLeftClick) {
                        Color var16 = new Color(255, 255, 255, 200);
                        Color var17 = new Color(255, 255, 255, 20);
                        SharedTextureDrawOptions var18 = Zoning.getRectangleDrawOptions(var10, var16, var17, 8, var2);
                        Objects.requireNonNull(var18);
                        var8 = var18::draw;
                     } else if (!SelectSettlersContainerGameTool.this.selectedSettlers.isEmpty()) {
                        ArrayList var11 = SelectSettlersContainerGameTool.this.getSettlerMovePositions(var4, var5);
                        DrawOptionsList var12 = new DrawOptionsList();
                        Iterator var13 = var11.iterator();

                        while(var13.hasNext()) {
                           Point var14 = (Point)var13.next();
                           Color var15 = new Color(93, 3, 255);
                           var12.add(SelectSettlersContainerGameTool.this.getActionParticleDrawOptions(var2, (float)var14.x, (float)var14.y, 0.0F, var15));
                        }

                        var8 = var12;
                     }
                  }
               }

               if (var8 != null) {
                  var1.add(new SortedDrawable() {
                     public int getPriority() {
                        return Integer.MAX_VALUE;
                     }

                     public void draw(TickManager var1) {
                        ((DrawOptions)var8).draw();
                     }
                  });
               }
            }

         }
      });
   }

   public boolean inputEvent(InputEvent var1) {
      State var2 = GlobalData.getCurrentState();
      if (var1.isMouseMoveEvent() || var1.isMouseClickEvent()) {
         this.tooltips = null;
         this.cursor = null;
         if (this.mouseDownPos == null && (var2.getFormManager() == null || !var2.getFormManager().isMouseOver(var1))) {
            int var3 = var2.getCamera().getMouseLevelPosX(var1);
            int var4 = var2.getCamera().getMouseLevelPosY(var1);
            boolean var5 = false;
            SettlementToolHandler var6 = this.getCurrentToolHandler();
            if (var6 != null) {
               var5 = var6.onHover(new Point(var3, var4), (var1x) -> {
                  this.tooltips = var1x;
               }, (var1x) -> {
                  this.cursor = var1x;
               });
            }

            if (!var5 && (this.tooltips == null || this.tooltips.isEmpty()) && this.cursor == null && Settings.showControlTips) {
               ListGameTooltips var7 = new ListGameTooltips();
               if (Input.lastInputIsController) {
                  if (!this.selectedSettlers.isEmpty()) {
                     var7.add((Object)(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "settlementcommandguard"))));
                  } else {
                     var7.add((Object)(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "settlementcommandselect"))));
                  }
               } else if (!this.selectedSettlers.isEmpty()) {
                  var7.add((Object)(new InputTooltip(-99, Localization.translate("ui", "settlementcommandguard"))));
               } else {
                  var7.add((Object)(new InputTooltip(-100, Localization.translate("ui", "settlementcommandselect"))));
               }

               this.tooltips = var7;
            }
         }
      }

      Point var8;
      boolean var9;
      if (var1.getID() == -100) {
         if (this.mouseDownPos != null && !var1.state) {
            var8 = var2.getCamera().getMouseLevelPos(var1);
            var9 = false;
            if (this.mouseDownIsLeftClick) {
               var9 = this.leftClick(this.mouseDownPos, var8);
            }

            if (var9) {
               var1.use();
            } else if (this.mouseDownPos.distance(var8) <= (double)maxPixelDistanceToConsiderClick) {
               this.ignoreNextInputEvent = InputEvent.MouseButtonEvent(0, true, var1.pos, Screen.tickManager);
               Screen.input().submitInputEvent(this.ignoreNextInputEvent);
            }

            this.mouseDownPos = null;
            return var9;
         }
      } else if (var1.getID() == -99 && this.mouseDownPos != null && !var1.state) {
         var8 = var2.getCamera().getMouseLevelPos(var1);
         var9 = false;
         if (!this.mouseDownIsLeftClick) {
            var9 = this.rightClick(this.mouseDownPos, var8);
         }

         if (var9) {
            var1.use();
         } else if (this.mouseDownPos.distance(var8) <= (double)maxPixelDistanceToConsiderClick) {
            this.ignoreNextInputEvent = InputEvent.MouseButtonEvent(1, true, var1.pos, Screen.tickManager);
            Screen.input().submitInputEvent(this.ignoreNextInputEvent);
         }

         this.mouseDownPos = null;
         return var9;
      }

      if (!var1.isMoveUsed() && !var1.isUsed() && (var2.getFormManager() == null || !var2.getFormManager().isMouseOver(var1)) && !var1.isSameEvent(this.ignoreNextInputEvent) && var1.state) {
         if (var1.getID() == -100) {
            this.mouseDownPos = var2.getCamera().getMouseLevelPos(var1);
            this.mouseDownIsLeftClick = true;
            var1.use();
            return true;
         }

         if (var1.getID() == -99) {
            this.mouseDownPos = var2.getCamera().getMouseLevelPos(var1);
            this.mouseDownIsLeftClick = false;
            var1.use();
            return true;
         }
      }

      return false;
   }

   public boolean controllerEvent(ControllerEvent var1) {
      State var2 = GlobalData.getCurrentState();
      if (var1.getState() == ControllerInput.AIM || var1.isButton) {
         this.tooltips = null;
         this.cursor = null;
         if (this.mouseDownPos == null && (var2.getFormManager() == null || !var2.getFormManager().isMouseOver(Screen.mousePos()))) {
            int var3 = var2.getCamera().getMouseLevelPosX();
            int var4 = var2.getCamera().getMouseLevelPosY();
            boolean var5 = false;
            SettlementToolHandler var6 = this.getCurrentToolHandler();
            if (var6 != null) {
               var5 = var6.onHover(new Point(var3, var4), (var1x) -> {
                  this.tooltips = var1x;
               }, (var1x) -> {
                  this.cursor = var1x;
               });
            }

            if (!var5 && (this.tooltips == null || this.tooltips.isEmpty()) && this.cursor == null && Settings.showControlTips) {
               ListGameTooltips var7 = new ListGameTooltips();
               if (Input.lastInputIsController) {
                  if (!this.selectedSettlers.isEmpty()) {
                     var7.add((Object)(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "settlementcommandguard"))));
                  } else {
                     var7.add((Object)(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "settlementcommandselect"))));
                  }
               } else if (!this.selectedSettlers.isEmpty()) {
                  var7.add((Object)(new InputTooltip(-99, Localization.translate("ui", "settlementcommandguard"))));
               } else {
                  var7.add((Object)(new InputTooltip(-100, Localization.translate("ui", "settlementcommandselect"))));
               }

               this.tooltips = var7;
            }
         }
      }

      Point var8;
      boolean var9;
      if (var1.getState() == ControllerInput.MENU_NEXT) {
         if (this.mouseDownPos != null && !var1.buttonState) {
            var8 = var2.getCamera().getMouseLevelPos();
            var9 = false;
            if (this.mouseDownIsLeftClick) {
               var9 = this.leftClick(this.mouseDownPos, var8);
            }

            this.mouseDownPos = null;
            if (var9) {
               var1.use();
            }

            return var9;
         }
      } else if (var1.getState() == ControllerInput.MENU_PREV && this.mouseDownPos != null && !var1.buttonState) {
         var8 = var2.getCamera().getMouseLevelPos();
         var9 = false;
         if (!this.mouseDownIsLeftClick) {
            var9 = this.rightClick(this.mouseDownPos, var8);
         }

         this.mouseDownPos = null;
         if (var9) {
            var1.use();
         }

         return var9;
      }

      if (!var1.isUsed() && (var2.getFormManager() == null || !var2.getFormManager().isMouseOver()) && var1.buttonState) {
         if (var1.getState() == ControllerInput.MENU_NEXT) {
            this.mouseDownPos = var2.getCamera().getMouseLevelPos();
            this.mouseDownIsLeftClick = true;
            var1.use();
            return true;
         }

         if (var1.getState() == ControllerInput.MENU_PREV) {
            this.mouseDownPos = var2.getCamera().getMouseLevelPos();
            this.mouseDownIsLeftClick = false;
            var1.use();
            return true;
         }
      }

      return false;
   }

   public boolean leftClick(Point var1, Point var2) {
      double var3 = var1.distance(var2);
      SettlementToolHandler var5 = this.getCurrentToolHandler();
      Rectangle var6;
      if (var3 <= (double)maxPixelDistanceToConsiderClick) {
         if (var5 != null && var5.onLeftClick(var2)) {
            return true;
         } else {
            var6 = new Rectangle(var2.x - 50, var2.y - 50, 100, 100);
            Mob var8 = (Mob)this.streamAllSettlers(var6).filter((var0) -> {
               return var0 instanceof CommandMob;
            }).filter((var1x) -> {
               return var1x.getSelectBox().contains(var2);
            }).max(Comparator.comparingInt(Mob::getDrawY)).orElse((Object)null);
            if (var8 != null) {
               this.selectedSettlers.selectOrDeselectSettler(var8.getUniqueID());
            } else {
               this.selectedSettlers.clear();
            }

            return true;
         }
      } else {
         var6 = new Rectangle(Math.min(var1.x, var2.x), Math.min(var1.y, var2.y), Math.max(Math.abs(var1.x - var2.x), 1), Math.max(Math.abs(var1.y - var2.y), 1));
         if (var5 != null && var5.onLeftClickSelection(var1, var2, var6)) {
            return true;
         } else {
            List var7 = (List)this.streamAllSettlers(var6).filter((var0) -> {
               return var0 instanceof CommandMob;
            }).filter((var1x) -> {
               return ((CommandMob)var1x).canBeCommanded(this.client);
            }).filter((var1x) -> {
               return var6.intersects(var1x.getSelectBox());
            }).map(Entity::getUniqueID).collect(Collectors.toList());
            this.selectedSettlers.selectSettlers((Iterable)var7);
            return true;
         }
      }
   }

   public boolean rightClick(Point var1, Point var2) {
      double var3 = var1.distance(var2);
      PlayerMob var7;
      Mob var8;
      Rectangle var10;
      Color var18;
      if (var3 <= (double)maxPixelDistanceToConsiderClick) {
         SettlementToolHandler var15 = this.getCurrentToolHandler();
         if (var15 != null && var15.onRightClick(var2)) {
            return true;
         } else if (this.selectedSettlers.isEmpty()) {
            return false;
         } else {
            synchronized(this.selectedSettlers) {
               var7 = this.client.getPlayer();
               var8 = (Mob)this.level.entityManager.streamAreaMobsAndPlayersTileRange(var2.x, var2.y, 10).filter((var1x) -> {
                  return var1x.getSelectBox().contains(var2);
               }).filter((var1x) -> {
                  return !this.selectedSettlers.contains(var1x.getUniqueID());
               }).filter((var1x) -> {
                  return var1x.canBeTargeted(var7, var7.getNetworkClient());
               }).findBestDistance(1, Comparator.comparingInt((var0) -> {
                  return -var0.getDrawY();
               })).orElse((Object)null);
               if (var8 != null) {
                  this.commandAttack(var8);
                  var18 = new Color(255, 41, 3);
                  var10 = var8.getSelectBox();
                  this.spawnActionParticles((float)var10.x + (float)var10.width / 2.0F, (float)var10.y + (float)var10.height / 2.0F, var18);
                  return true;
               } else {
                  this.commandGuard(var2.x, var2.y);
                  this.spawnActionParticles((float)var2.x, (float)var2.y);
                  return true;
               }
            }
         }
      } else {
         Rectangle var5 = new Rectangle(Math.min(var1.x, var2.x), Math.min(var1.y, var2.y), Math.abs(var1.x - var2.x), Math.abs(var1.y - var2.y));
         SettlementToolHandler var6 = this.getCurrentToolHandler();
         if (var6 != null && var6.onRightClickSelection(var1, var2, var5)) {
            return true;
         } else if (this.selectedSettlers.isEmpty()) {
            return false;
         } else {
            if (this.selectedSettlers.getSize() == 1) {
               var7 = this.client.getPlayer();
               var8 = (Mob)this.level.entityManager.streamAreaMobsAndPlayersTileRange(var2.x, var2.y, 10).filter((var1x) -> {
                  return var1x.getSelectBox().contains(var2);
               }).filter((var1x) -> {
                  return !this.selectedSettlers.contains(var1x.getUniqueID());
               }).filter((var1x) -> {
                  return var1x.canBeTargeted(var7, var7.getNetworkClient());
               }).findBestDistance(1, Comparator.comparingInt((var0) -> {
                  return -var0.getDrawY();
               })).orElse((Object)null);
               if (var8 != null) {
                  this.commandAttack(var8);
                  var18 = new Color(255, 41, 3);
                  var10 = var8.getSelectBox();
                  this.spawnActionParticles((float)var10.x + (float)var10.width / 2.0F, (float)var10.y + (float)var10.height / 2.0F, var18);
                  return true;
               }
            }

            ArrayList var16 = this.getSettlerMovePositions(var1, var2);
            Point var9;
            synchronized(this.selectedSettlers) {
               if (var16.size() == 1) {
                  var9 = (Point)var16.get(0);
                  this.commandGuard(var9.x, var9.y);
               } else {
                  this.commandGuard(var16);
               }
            }

            Iterator var17 = var16.iterator();

            while(var17.hasNext()) {
               var9 = (Point)var17.next();
               this.spawnActionParticles((float)var9.x, (float)var9.y);
            }

            return true;
         }
      }
   }

   public ArrayList<Point> getSettlerMovePositions(Point var1, Point var2) {
      double var3 = var1.distance(var2);
      synchronized(this.selectedSettlers) {
         if (this.selectedSettlers.getSize() <= 1) {
            return new ArrayList(Collections.singletonList(var2));
         } else {
            Point2D.Float var6 = GameMath.normalize((float)(var2.x - var1.x), (float)(var2.y - var1.y));
            int var7 = this.selectedSettlers.getSize();
            ArrayList var8 = new ArrayList(var7);
            double var9 = var3 / (double)Math.max(var7 - 1, 1);

            for(int var11 = 0; var11 < var7; ++var11) {
               double var12 = var9 * (double)var11;
               int var14 = (int)((double)var1.x + (double)var6.x * var12);
               int var15 = (int)((double)var1.y + (double)var6.y * var12);
               var8.add(new Point(var14, var15));
            }

            return var8;
         }
      }
   }

   public void spawnActionParticles(float var1, float var2) {
      this.spawnActionParticles(var1, var2, new Color(93, 3, 255));
   }

   public DrawOptions getActionParticleDrawOptions(GameCamera var1, float var2, float var3, float var4, Color var5) {
      int var6 = var1.getDrawX(var2);
      int var7 = var1.getDrawY(var3);
      float var8 = (float)((double)this.level.getWorldEntity().getLocalTime() / 2.0 % 360.0);
      float var9 = 1.0F - var4;
      int var10 = (int)((float)Math.max(GameResources.aim.getWidth(), GameResources.aim.getHeight()) * var9);
      return GameResources.aim.initDraw().color(var5).size(var10).rotate(var8).posMiddle(var6, var7);
   }

   public void spawnActionParticles(float var1, float var2, final Color var3) {
      this.level.entityManager.addParticle(new Particle(this.level, var1, var2, 500L) {
         public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3x, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
            DrawOptions var9 = SelectSettlersContainerGameTool.this.getActionParticleDrawOptions(var7, this.x, this.y, this.getLifeCyclePercent(), var3);
            var3x.add((var1x) -> {
               var9.draw();
            });
         }
      }, Particle.GType.CRITICAL);
   }

   public void isCancelled() {
      this.hudElement.remove();
   }

   public void isCleared() {
      this.hudElement.remove();
   }

   public boolean canCancel() {
      return false;
   }

   public boolean forceControllerCursor() {
      return true;
   }

   public Screen.CURSOR getCursor() {
      return this.cursor;
   }

   public GameTooltips getTooltips() {
      return this.tooltips;
   }
}
