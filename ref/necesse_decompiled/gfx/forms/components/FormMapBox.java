package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.tickManager.TickManager;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.MapDrawElement;
import necesse.level.maps.MapTexture;

public class FormMapBox extends FormComponent implements FormPositionContainer {
   private static final int zoomFadeTime = 1000;
   public final Client client;
   public boolean drawBackground = true;
   private FormPosition position;
   private int width;
   private int height;
   private boolean hidden;
   private int[] zoomLevels;
   private int zoomLevel;
   private long zoomPressTime;
   private boolean centered;
   private boolean mouseDown;
   private int centerX;
   private int centerY;
   private int startX;
   private int startY;
   private int mouseStartX;
   private int mouseStartY;
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
   protected Consumer<Integer> saveZoomLevel;
   protected boolean allowControllerFocus;
   private boolean isControllerSelected;
   private double controllerScrollXBuffer;
   private double controllerScrollYBuffer;
   protected boolean isHovering;
   protected InputEvent lastMoveEvent;

   public FormMapBox(Client var1, int var2, int var3, int var4, int var5, int[] var6, int var7, Consumer<Integer> var8, boolean var9) {
      this.client = var1;
      this.position = new FormFixedPosition(var2, var3);
      this.width = var4;
      this.height = var5;
      this.zoomLevels = var6;
      this.zoomLevel = Math.abs(var7) % this.zoomLevels.length;
      this.saveZoomLevel = var8;
      this.allowControllerFocus = var9;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         this.lastMoveEvent = null;
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
            this.lastMoveEvent = var1;
         }

         if (this.isHovering && this.mouseDown) {
            if (var1.pos.hudX != Integer.MIN_VALUE) {
               this.centerX = this.startX + (this.mouseStartX - var1.pos.hudX) * this.zoomLevels[this.zoomLevel];
               this.centerX = Math.max(0, Math.min(this.client.getLevel().width * 32, this.centerX));
            }

            if (var1.pos.hudY != Integer.MIN_VALUE) {
               this.centerY = this.startY + (this.mouseStartY - var1.pos.hudY) * this.zoomLevels[this.zoomLevel];
               this.centerY = Math.max(0, Math.min(this.client.getLevel().height * 32, this.centerY));
            }
         }
      }

      if (!var1.isKeyboardEvent()) {
         if (this.mouseDown && !var1.state && var1.getID() == -100) {
            this.mouseDown = false;
            var1.use();
         }

         if (this.isMouseOver(var1)) {
            if (var1.state && var1.isMouseWheelEvent()) {
               this.wheelBuffer.add(var1);
               this.wheelBuffer.useScrollY((var2x) -> {
                  if (var2x) {
                     this.zoomIn(var1.pos.hudX, var1.pos.hudY);
                  } else {
                     this.zoomOut(var1.pos.hudX, var1.pos.hudY);
                  }

               });
               var1.use();
            }

            int var4;
            if (var1.state && var1.getID() == -100) {
               if (this.client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel() && Screen.isKeyDown(340)) {
                  if (this.client.worldSettings.allowCheats) {
                     var4 = this.getMouseMapPosX(var1.pos.hudX);
                     int var19 = this.getMouseMapPosY(var1.pos.hudY);
                     PlayerMob var6 = this.client.getPlayer();
                     Rectangle var20 = new Rectangle(this.client.getLevel().width * 32, this.client.getLevel().height * 32);
                     if (var20.contains(var6.getCollision(var4, var19))) {
                        var6.setPos((float)var4, (float)var19, true);
                        this.client.sendMovementPacket(true);
                     }
                  } else {
                     this.client.chat.addMessage(Localization.translate("misc", "allowcheats"));
                  }

                  var1.use();
               } else {
                  this.mouseDown = true;
                  this.setCentered(false);
                  this.mouseStartX = var1.pos.hudX;
                  this.mouseStartY = var1.pos.hudY;
                  this.startX = this.centerX;
                  this.startY = this.centerY;
                  var1.use();
               }
            } else if (var1.state && var1.getID() == -99) {
               var4 = this.zoomLevels[this.zoomLevel];
               double var5 = (double)this.getWidth() / 2.0;
               double var7 = (double)this.getHeight() / 2.0;
               InputEvent var9 = InputEvent.OffsetHudEvent(Screen.input(), var1, -this.getX(), -this.getY());
               int var10 = var9.pos.hudX;
               int var11 = var9.pos.hudY;
               Iterator var12 = this.client.getLevel().entityManager.getDrawOnMap().iterator();

               int var15;
               int var16;
               Rectangle var17;
               while(var12.hasNext()) {
                  Entity var13 = (Entity)var12.next();
                  if (var9.isUsed()) {
                     return;
                  }

                  if (var13.isVisibleOnMap(this.client, this.client.levelManager.map())) {
                     Point var14 = var13.getMapPos();
                     var15 = this.getMapPosX(var5, var4, var14.x);
                     var16 = this.getMapPosY(var7, var4, var14.y);
                     var17 = new Rectangle(var13.drawOnMapBox());
                     var17.x += var15;
                     var17.y += var16;
                     if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var17) && var17.contains(var10, var11)) {
                        var13.onMapInteract(var9, var3);
                     }
                  }
               }

               if (var9.isUsed()) {
                  return;
               }

               ClientClient var21 = this.client.getClient();

               for(int var22 = 0; var22 < this.client.getSlots(); ++var22) {
                  if (var9.isUsed()) {
                     return;
                  }

                  ClientClient var24 = this.client.getClient(var22);
                  if (var24 != null && var24.loadedPlayer && var24.hasSpawned() && !var24.isDead() && (var24 == var21 || var24.isSameTeam(var21)) && var24.isSamePlace(this.client.getLevel())) {
                     PlayerMob var26 = var24.playerMob;
                     var16 = this.getMapPosX(var5, var4, var26.getX());
                     int var27 = this.getMapPosY(var7, var4, var26.getY());
                     Rectangle var18 = new Rectangle(var26.drawOnMapBox());
                     var18.x += var16;
                     var18.y += var27;
                     if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var18) && var18.contains(var10, var11)) {
                        var26.onMapInteract(var9, var3);
                     }
                  }
               }

               if (var9.isUsed()) {
                  return;
               }

               Iterator var23 = this.client.levelManager.map().getDrawElements().iterator();

               while(var23.hasNext()) {
                  MapDrawElement var25 = (MapDrawElement)var23.next();
                  if (var9.isUsed()) {
                     return;
                  }

                  var15 = this.getMapPosX(var5, var4, var25.getX());
                  var16 = this.getMapPosY(var7, var4, var25.getY());
                  var17 = new Rectangle(var25.getBoundingBox());
                  var17.x += var15;
                  var17.y += var16;
                  if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var17) && var17.contains(var10, var11)) {
                     var25.onMapInteract(var9, var3);
                  }
               }
            }
         }

      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.getState() == ControllerInput.MENU_SELECT) {
         if (this.isControllerFocus() && var1.buttonState) {
            if (this.isControllerSelected) {
               int var4 = this.zoomLevels[this.zoomLevel];
               double var5 = (double)this.getWidth() / 2.0;
               double var7 = (double)this.getHeight() / 2.0;
               InputEvent var9 = InputEvent.OffsetHudEvent(Screen.input(), InputEvent.ControllerButtonEvent(var1, var2), -this.getX(), -this.getY());
               int var10 = this.getWidth() / 2;
               int var11 = this.getHeight() / 2;
               Iterator var12 = this.client.getLevel().entityManager.getDrawOnMap().iterator();

               int var15;
               int var16;
               Rectangle var17;
               while(var12.hasNext()) {
                  Entity var13 = (Entity)var12.next();
                  if (var9.isUsed()) {
                     return;
                  }

                  if (var13.isVisibleOnMap(this.client, this.client.levelManager.map())) {
                     Point var14 = var13.getMapPos();
                     var15 = this.getMapPosX(var5, var4, var14.x);
                     var16 = this.getMapPosY(var7, var4, var14.y);
                     var17 = new Rectangle(var13.drawOnMapBox());
                     var17.x += var15;
                     var17.y += var16;
                     if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var17) && var17.contains(var10, var11)) {
                        var13.onMapInteract(var9, var3);
                     }
                  }
               }

               if (var9.isUsed()) {
                  return;
               }

               ClientClient var19 = this.client.getClient();

               for(int var20 = 0; var20 < this.client.getSlots(); ++var20) {
                  if (var9.isUsed()) {
                     return;
                  }

                  ClientClient var22 = this.client.getClient(var20);
                  if (var22 != null && var22.loadedPlayer && var22.hasSpawned() && !var22.isDead() && (var22 == var19 || var22.isSameTeam(var19)) && var22.isSamePlace(this.client.getLevel())) {
                     PlayerMob var24 = var22.playerMob;
                     var16 = this.getMapPosX(var5, var4, var24.getX());
                     int var25 = this.getMapPosY(var7, var4, var24.getY());
                     Rectangle var18 = new Rectangle(var24.drawOnMapBox());
                     var18.x += var16;
                     var18.y += var25;
                     if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var18) && var18.contains(var10, var11)) {
                        var24.onMapInteract(var9, var3);
                     }
                  }
               }

               if (var9.isUsed()) {
                  return;
               }

               Iterator var21 = this.client.levelManager.map().getDrawElements().iterator();

               while(var21.hasNext()) {
                  MapDrawElement var23 = (MapDrawElement)var21.next();
                  if (var9.isUsed()) {
                     return;
                  }

                  var15 = this.getMapPosX(var5, var4, var23.getX());
                  var16 = this.getMapPosY(var7, var4, var23.getY());
                  var17 = new Rectangle(var23.getBoundingBox());
                  var17.x += var15;
                  var17.y += var16;
                  if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var17) && var17.contains(var10, var11)) {
                     var23.onMapInteract(var9, var3);
                  }
               }
            } else {
               this.isControllerSelected = true;
               var1.use();
               this.playTickSound();
            }
         }
      } else if (var1.getState() != ControllerInput.MENU_BACK && var1.getState() != ControllerInput.MAIN_MENU) {
         if (var1.getState() == ControllerInput.MENU_NEXT) {
            if (this.isControllerSelected && var1.buttonState) {
               this.zoomIn();
               var1.use();
               this.playTickSound();
            }
         } else if (var1.getState() == ControllerInput.MENU_PREV && this.isControllerSelected && var1.buttonState) {
            this.zoomOut();
            var1.use();
            this.playTickSound();
         }
      } else if (this.isControllerSelected && var1.buttonState) {
         this.isControllerSelected = false;
         var1.use();
         this.playTickSound();
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.allowControllerFocus) {
         ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
      }

   }

   public void onControllerUnfocused(ControllerFocus var1) {
      super.onControllerUnfocused(var1);
      this.isControllerSelected = false;
   }

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      if (this.isControllerSelected) {
         switch (var1) {
            case 0:
               this.centerY -= 10 * this.zoomLevels[this.zoomLevel];
               this.centerY = Math.max(0, Math.min(this.client.getLevel().height * 32, this.centerY));
               break;
            case 1:
               this.centerX += 10 * this.zoomLevels[this.zoomLevel];
               this.centerX = Math.max(0, Math.min(this.client.getLevel().width * 32, this.centerX));
               break;
            case 2:
               this.centerY += 10 * this.zoomLevels[this.zoomLevel];
               this.centerY = Math.max(0, Math.min(this.client.getLevel().height * 32, this.centerY));
               break;
            case 3:
               this.centerX -= 10 * this.zoomLevels[this.zoomLevel];
               this.centerX = Math.max(0, Math.min(this.client.getLevel().width * 32, this.centerX));
         }

         this.setCentered(false);
         return true;
      } else {
         return super.handleControllerNavigate(var1, var2, var3, var4);
      }
   }

   public void frameTickControllerFocus(TickManager var1, ControllerFocus var2) {
      if (this.isControllerSelected && !ControllerInput.isCursorVisible()) {
         this.controllerScrollXBuffer += (double)(ControllerInput.getAimX() * (float)this.zoomLevels[this.zoomLevel] * var1.getDelta()) / 4.0;
         this.controllerScrollYBuffer += (double)(ControllerInput.getAimY() * (float)this.zoomLevels[this.zoomLevel] * var1.getDelta()) / 4.0;
         int var3;
         if (Math.abs(this.controllerScrollXBuffer) >= 1.0) {
            var3 = (int)this.controllerScrollXBuffer;
            this.controllerScrollXBuffer -= (double)var3;
            this.centerX += var3;
            this.centerX = Math.max(0, Math.min(this.client.getLevel().width * 32, this.centerX));
         }

         if (Math.abs(this.controllerScrollYBuffer) >= 1.0) {
            var3 = (int)this.controllerScrollYBuffer;
            this.controllerScrollYBuffer -= (double)var3;
            this.centerY += var3;
            this.centerY = Math.max(0, Math.min(this.client.getLevel().height * 32, this.centerY));
         }

         this.setCentered(false);
      }

   }

   public int getMouseMapPosX(int var1) {
      int var2 = var1 - this.getX();
      return (var2 - this.getWidth() / 2) * this.zoomLevels[this.zoomLevel] + this.centerX;
   }

   public int getMouseMapPosY(int var1) {
      int var2 = var1 - this.getY();
      return (var2 - this.getHeight() / 2) * this.zoomLevels[this.zoomLevel] + this.centerY;
   }

   public void zoomOut() {
      if (this.zoomLevel < this.zoomLevels.length - 1) {
         ++this.zoomLevel;
         this.refreshZoomTime();
         this.saveZoomLevel.accept(this.zoomLevel);
      }

   }

   public void zoomIn() {
      if (this.zoomLevel > 0) {
         --this.zoomLevel;
         this.refreshZoomTime();
         this.saveZoomLevel.accept(this.zoomLevel);
      }

   }

   public void zoomOut(int var1, int var2) {
      if (this.zoomLevel < this.zoomLevels.length - 1) {
         float var3 = (float)this.getMouseMapPosX(var1) / 32.0F;
         float var4 = (float)this.getMouseMapPosY(var2) / 32.0F;
         ++this.zoomLevel;
         int var5 = this.getMouseMapPosX(var1) / 32;
         int var6 = this.getMouseMapPosY(var2) / 32;
         this.centerX = (int)((float)this.centerX + (var3 - (float)var5) * 32.0F);
         this.centerY = (int)((float)this.centerY + (var4 - (float)var6) * 32.0F);
         this.centerX = Math.max(0, Math.min(this.client.getLevel().width * 32, this.centerX));
         this.centerY = Math.max(0, Math.min(this.client.getLevel().height * 32, this.centerY));
         this.refreshZoomTime();
         this.saveZoomLevel.accept(this.zoomLevel);
      }

   }

   public void zoomIn(int var1, int var2) {
      if (this.zoomLevel > 0) {
         float var3 = (float)this.getMouseMapPosX(var1) / 32.0F;
         float var4 = (float)this.getMouseMapPosY(var2) / 32.0F;
         --this.zoomLevel;
         int var5 = this.getMouseMapPosX(var1) / 32;
         int var6 = this.getMouseMapPosY(var2) / 32;
         this.centerX = (int)((float)this.centerX + (var3 - (float)var5) * 32.0F);
         this.centerY = (int)((float)this.centerY + (var4 - (float)var6) * 32.0F);
         this.centerX = Math.max(0, Math.min(this.client.getLevel().width * 32, this.centerX));
         this.centerY = Math.max(0, Math.min(this.client.getLevel().height * 32, this.centerY));
         this.refreshZoomTime();
         this.saveZoomLevel.accept(this.zoomLevel);
      }

   }

   public void setCentered(boolean var1) {
      if (this.centered && !var1) {
         PlayerMob var2 = this.client.getPlayer();
         this.centerX = var2.getX();
         this.centerY = var2.getY();
      }

      this.centered = var1;
   }

   public boolean isCentered() {
      return this.centered;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      boolean var4 = this.isHovering || this.isControllerFocus() && this.isControllerSelected;
      if (!this.centered) {
         System.out.println(this.getWidth() + "x" + this.getHeight());
      }

      if (var4) {
         if (this.mouseDown) {
            Screen.setCursor(Screen.CURSOR.GRAB_ON);
         } else {
            Screen.setCursor(Screen.CURSOR.GRAB_OFF);
         }
      }

      if (this.centered) {
         PlayerMob var5 = this.client.getPlayer();
         this.centerX = var5.getX();
         this.centerY = var5.getY();
      }

      int var53 = this.zoomLevels[this.zoomLevel];
      LevelMap var6 = this.client.levelManager.map();
      if (var6 != null) {
         MapTexture[][] var7 = var6.getMapTextures();
         if (var7 != null) {
            double var8 = 32.0 / (double)var53;
            double var10 = (double)this.getWidth() / 2.0;
            double var12 = (double)this.getWidth() / var8;
            double var14 = var12 / 2.0;
            double var16 = (double)this.getHeight() / 2.0;
            double var18 = (double)this.getHeight() / var8;
            double var20 = var18 / 2.0;
            double var22 = (double)this.centerX / 32.0 - var14;
            double var24 = var22 + var12;
            double var26 = (double)this.centerY / 32.0 - var20;
            double var28 = var26 + var18;
            int var30 = Math.max(0, (int)(var22 / 30.0));
            int var31 = Math.max(0, (int)(var26 / 30.0));
            FormShader.FormShaderState var32 = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(this.getWidth(), this.getHeight()));

            try {
               ControllerFocus var35 = this.getManager().getCurrentFocus();
               int var33;
               int var34;
               if (Input.lastInputIsController && this.isControllerSelected && var35 != null) {
                  var33 = (int)var35.boundingBox.getCenterX() - var32.offset.x;
                  var34 = (int)var35.boundingBox.getCenterY() - var32.offset.y;
               } else {
                  var33 = var32.mouseEvent.pos.hudX;
                  var34 = var32.mouseEvent.pos.hudY;
               }

               if (this.drawBackground) {
                  Screen.initQuadDraw(this.getWidth(), this.getHeight()).color(new Color(50, 50, 50)).draw(0, 0);
               }

               int var36 = (int)(var8 * 30.0);
               int var37 = var30;
               int var38 = var30 * 30;
               int var39 = this.getMapPosX(var10, var53, var38 * 32);

               while(true) {
                  var38 = var37 * 30;
                  int var42;
                  int var44;
                  int var45;
                  if ((double)var38 > var24) {
                     Level var54 = this.client.getLevel();
                     if (Settings.serverPerspective && this.client.getLocalServer() != null) {
                        var54 = this.client.getLocalServer().world.getLevel(var54.getIdentifier());
                     }

                     var54.entityManager.getDrawOnMap().stream().filter((var2x) -> {
                        return var2x.isVisibleOnMap(this.client, var6);
                     }).forEach((var10x) -> {
                        Point var11 = var10x.getMapPos();
                        int var12 = this.getMapPosX(var10, var53, var11.x);
                        int var13 = this.getMapPosY(var16, var53, var11.y);
                        Rectangle var14 = new Rectangle(var10x.drawOnMapBox());
                        var14.x += var12;
                        var14.y += var13;
                        if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var14)) {
                           var10x.drawOnMap(var1, var12, var13);
                           if (var4 && var14.contains(var33, var34)) {
                              GameTooltips var15 = var10x.getMapTooltips();
                              if (var15 != null) {
                                 Screen.addTooltip(var15, TooltipLocation.FORM_FOCUS);
                              }

                              String var16x = var10x.getMapInteractTooltip();
                              if (var16x != null) {
                                 Screen.addTooltip(new InputTooltip(-99, var16x), TooltipLocation.FORM_FOCUS);
                                 Screen.setCursor(Screen.CURSOR.INTERACT);
                              }
                           }
                        }

                     });
                     ClientClient var55 = this.client.getClient();

                     for(var42 = 0; var42 < this.client.getSlots(); ++var42) {
                        ClientClient var57 = this.client.getClient(var42);
                        if (var57 != null && var57.loadedPlayer && var57.hasSpawned() && !var57.isDead() && (var57 == var55 || var57.isSameTeam(var55)) && var57.isSamePlace(var54)) {
                           PlayerMob var60 = var57.playerMob;
                           var45 = this.getMapPosX(var10, var53, var60.getX());
                           int var46 = this.getMapPosY(var16, var53, var60.getY());
                           Rectangle var47 = new Rectangle(var60.drawOnMapBox());
                           var47.x += var45;
                           var47.y += var46;
                           if ((new Rectangle(this.getWidth(), this.getHeight())).intersects(var47)) {
                              var60.drawOnMap(var1, var45, var46);
                              if (var4 && var47.contains(var33, var34)) {
                                 GameTooltips var48 = var60.getMapTooltips();
                                 if (var48 != null) {
                                    Screen.addTooltip(var48, TooltipLocation.FORM_FOCUS);
                                 }

                                 String var49 = var60.getMapInteractTooltip();
                                 if (var49 != null) {
                                    Screen.addTooltip(new InputTooltip(-99, var49), TooltipLocation.FORM_FOCUS);
                                    Screen.setCursor(Screen.CURSOR.INTERACT);
                                 }
                              }
                           }
                        }
                     }

                     Iterator var56 = var6.getDrawElements().iterator();

                     while(true) {
                        MapDrawElement var58;
                        Rectangle var63;
                        do {
                           if (!var56.hasNext()) {
                              if (var4) {
                                 var42 = this.getMouseMapPosX(var33) / 32;
                                 int var59 = this.getMouseMapPosY(var34 + 9) / 32;
                                 if (var6.isTileKnown(var42, var59) && var42 > 0 && var42 < var54.width && var59 > 0 && var59 <= var54.height) {
                                    GameTooltips var61 = var54.getLevelTile(var42, var59).getMapTooltips();
                                    if (var61 != null) {
                                       Screen.addTooltip(var61, TooltipLocation.FORM_FOCUS);
                                    }

                                    GameTooltips var62 = var54.getLevelObject(var42, var59).getMapTooltips();
                                    if (var62 != null) {
                                       Screen.addTooltip(var62, TooltipLocation.FORM_FOCUS);
                                    }
                                 }
                              }

                              this.drawZoomText(this.getWidth() - 4, 0);
                              return;
                           }

                           var58 = (MapDrawElement)var56.next();
                           var44 = this.getMapPosX(var10, var53, var58.getX());
                           var45 = this.getMapPosY(var16, var53, var58.getY());
                           var63 = new Rectangle(var58.getBoundingBox());
                           var63.x += var44;
                           var63.y += var45;
                        } while(!(new Rectangle(this.getWidth(), this.getHeight())).intersects(var63));

                        if (var4 && var63.contains(var33, var34)) {
                           GameTooltips var64 = var58.getTooltips(var44, var45, var2);
                           if (var64 != null) {
                              Screen.addTooltip(var64, TooltipLocation.FORM_FOCUS);
                           }

                           String var65 = var58.getMapInteractTooltip();
                           if (var65 != null) {
                              if (Input.lastInputIsController && this.isControllerSelected && var35 != null) {
                                 Screen.addTooltip(new InputTooltip(ControllerInput.MENU_SELECT, var65), TooltipLocation.FORM_FOCUS);
                              } else {
                                 Screen.addTooltip(new InputTooltip(-99, var65), TooltipLocation.FORM_FOCUS);
                                 Screen.setCursor(Screen.CURSOR.INTERACT);
                              }
                           }
                        }

                        var58.draw(var44, var45, var2);
                     }
                  }

                  if (var37 < var7.length) {
                     int var40 = var31;
                     int var41 = var31 * 30;
                     var42 = this.getMapPosY(var16, var53, var41 * 32);

                     while(true) {
                        var41 = var40 * 30;
                        if ((double)var41 > var28) {
                           var39 += var36;
                           break;
                        }

                        if (var40 < var7[var37].length) {
                           MapTexture var43 = var7[var37][var40];
                           var44 = (int)(var8 * (double)var43.tileWidth);
                           var45 = (int)(var8 * (double)var43.tileHeight);
                           var43.initDraw().size(var44, var45).draw(var39, var42);
                           Screen.drawShape(new Rectangle(var44, var45), (float)var39, (float)var42, false, 1.0F, 0.0F, 0.0F, 0.0F);
                           var42 += var36;
                        }

                        ++var40;
                     }
                  }

                  ++var37;
               }
            } finally {
               var32.end();
            }
         }
      }
   }

   public void drawZoomText(int var1, int var2) {
      if (this.zoomPressTime > System.currentTimeMillis()) {
         long var3 = this.zoomPressTime - System.currentTimeMillis();
         float var5 = (float)var3 / 1000.0F;
         String var6 = "1:" + this.zoomLevels[this.zoomLevel];
         FontOptions var7 = (new FontOptions(16)).colorf(1.0F, 1.0F, 1.0F, var5);
         int var8 = FontManager.bit.getWidthCeil(var6, var7);
         FontManager.bit.drawString((float)(var1 - var8), (float)var2, var6, var7);
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      int var2 = (int)var1.boundingBox.getCenterX();
      int var3 = (int)var1.boundingBox.getCenterY();
      byte var4;
      Rectangle var5;
      if (this.isControllerSelected) {
         var4 = 14;
         var5 = new Rectangle(var2 - var4, var3 - var4, var4 * 2, var4 * 2);
         HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsHighlightColor, true, var5).draw();
         Screen.addControllerGlyph(Localization.translate("controls", "zoomtip"), ControllerInput.MENU_PREV, ControllerInput.MENU_NEXT);
      } else {
         var4 = 20;
         var5 = new Rectangle(var2 - var4, var3 - var4, var4 * 2, var4 * 2);
         HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsColor, true, var5).draw();
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

   }

   public Point getControllerTooltipAndFloatMenuPoint(ControllerFocus var1) {
      int var2 = (int)var1.boundingBox.getCenterX();
      int var3 = (int)var1.boundingBox.getCenterY();
      byte var4;
      if (this.isControllerSelected) {
         var4 = 14;
         return new Point(var2 - var4, var3 - var4);
      } else {
         var4 = 20;
         return new Point(var2 - var4, var3 - var4);
      }
   }

   public int getMapPos(int var1, double var2, int var4, int var5) {
      return (int)(var2 + (double)(var5 - var1) / (double)var4);
   }

   public int getMapPosX(double var1, int var3, int var4) {
      return this.getMapPos(this.centerX, var1, var3, var4);
   }

   public int getMapPosY(double var1, int var3, int var4) {
      return this.getMapPos(this.centerY, var1, var3, var4);
   }

   public void refreshZoomTime() {
      this.zoomPressTime = System.currentTimeMillis() + 1000L;
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int var1) {
      this.height = var1;
   }

   public void setSize(int var1) {
      this.setWidth(var1);
      this.setHeight(var1);
   }

   public boolean isMouseOver(InputEvent var1) {
      return this.isHidden() ? false : super.isMouseOver(var1);
   }

   public boolean shouldDraw() {
      return !this.isHidden();
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean var1) {
      this.hidden = var1;
   }

   public boolean isMouseDown() {
      return this.mouseDown;
   }
}
