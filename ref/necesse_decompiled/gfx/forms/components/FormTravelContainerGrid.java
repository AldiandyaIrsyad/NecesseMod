package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRemoveDeathLocations;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldGenerator;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.PlayerSprite;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.FormClickHandler;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.containerComponent.TravelContainerComponent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.container.travel.IslandData;
import necesse.inventory.container.travel.IslandsResponseEvent;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;
import necesse.level.maps.biomes.Biome;

public class FormTravelContainerGrid extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private int width;
   private int height;
   private MouseWheelBuffer wheelBuffer;
   public final Client client;
   public final TravelContainer travelContainer;
   public final TravelContainerComponent travelContainerForm;
   private int gridStartX;
   private int gridStartY;
   public final int gridWidth;
   public final int gridHeight;
   private final FormDestination[][] destinations;
   private final FormClickHandler topArrowHandler;
   private final FormClickHandler botArrowHandler;
   private final FormClickHandler leftArrowHandler;
   private final FormClickHandler rightArrowHandler;
   private final FormClickHandler topRightArrowHandler;
   private final FormClickHandler topLeftArrowHandler;
   private final FormClickHandler botRightArrowHandler;
   private final FormClickHandler botLeftArrowHandler;
   private boolean isHoveringTop;
   private boolean isHoveringBot;
   private boolean isHoveringLeft;
   private boolean isHoveringRight;
   private final List<Rectangle> expectedRequests;
   public int minIslandX;
   public int minIslandY;
   public int maxIslandX;
   public int maxIslandY;
   private Point controllerSelected;

   public FormTravelContainerGrid(FormTravelContainerGrid var1, int var2, int var3, int var4, int var5, Client var6, TravelContainer var7, TravelContainerComponent var8) {
      this.wheelBuffer = new MouseWheelBuffer(true);
      this.controllerSelected = null;
      if (var4 >= 80 && var5 >= 80) {
         this.position = new FormFixedPosition(var2, var3);
         this.width = var4;
         this.height = var5;
         this.client = var6;
         this.travelContainer = var7;
         this.travelContainerForm = var8;
         this.minIslandX = Integer.MIN_VALUE;
         this.minIslandY = Integer.MIN_VALUE;
         this.maxIslandX = Integer.MAX_VALUE;
         this.maxIslandY = Integer.MAX_VALUE;
         this.expectedRequests = new ArrayList();
         this.gridWidth = var4 / 40 - 1;
         this.gridHeight = var5 / 40 - 1;
         this.destinations = new FormDestination[this.gridWidth][this.gridHeight];
         this.topArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverTopArrow(var1x) && this.canScrollUp();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollUp();
         });
         this.botArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverBotArrow(var1x) && this.canScrollDown();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollDown();
         });
         this.leftArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverLeftArrow(var1x) && this.canScrollLeft();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollLeft();
         });
         this.rightArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverRightArrow(var1x) && this.canScrollRight();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollRight();
         });
         this.topRightArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverTopArrow(var1x) && this.canScrollUp() && this.isMouseOverRightArrow(var1x) && this.canScrollRight();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollUp();
            this.scrollRight();
         });
         this.topLeftArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverTopArrow(var1x) && this.canScrollUp() && this.isMouseOverLeftArrow(var1x) && this.canScrollLeft();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollUp();
            this.scrollLeft();
         });
         this.botRightArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverBotArrow(var1x) && this.canScrollDown() && this.isMouseOverRightArrow(var1x) && this.canScrollRight();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollDown();
            this.scrollRight();
         });
         this.botLeftArrowHandler = new FormClickHandler((var1x) -> {
            return this.isMouseOverBotArrow(var1x) && this.canScrollDown() && this.isMouseOverLeftArrow(var1x) && this.canScrollLeft();
         }, -100, (var1x) -> {
            this.playTickSound();
            this.scrollDown();
            this.scrollLeft();
         });
         TravelDir var9 = var7.travelDir;
         int var10;
         int var11;
         if (var1 != null) {
            var10 = this.gridWidth - var1.gridWidth;
            var11 = this.gridHeight - var1.gridHeight;
            if (var9 != TravelDir.NorthWest && var9 != TravelDir.West && var9 != TravelDir.SouthWest) {
               if (var9 != TravelDir.NorthEast && var9 != TravelDir.East && var9 != TravelDir.SouthEast) {
                  this.gridStartX = var1.gridStartX - var10 / 2;
               } else {
                  this.gridStartX = var1.gridStartX;
               }
            } else {
               this.gridStartX = var1.gridStartX - var10;
            }

            if (var9 != TravelDir.NorthWest && var9 != TravelDir.North && var9 != TravelDir.NorthEast) {
               if (var9 != TravelDir.SouthWest && var9 != TravelDir.South && var9 != TravelDir.SouthEast) {
                  this.gridStartY = var1.gridStartY - var11 / 2;
               } else {
                  this.gridStartY = var1.gridStartY;
               }
            } else {
               this.gridStartY = var1.gridStartY - var11;
            }
         } else {
            if (var9 != TravelDir.NorthWest && var9 != TravelDir.West && var9 != TravelDir.SouthWest) {
               if (var9 != TravelDir.NorthEast && var9 != TravelDir.East && var9 != TravelDir.SouthEast) {
                  this.gridStartX = var7.playerLevel.getIslandX() - this.gridWidth / 2;
               } else {
                  this.gridStartX = var7.playerLevel.getIslandX();
               }
            } else {
               this.gridStartX = var7.playerLevel.getIslandX() - (this.gridWidth - 1);
            }

            if (var9 != TravelDir.NorthWest && var9 != TravelDir.North && var9 != TravelDir.NorthEast) {
               if (var9 != TravelDir.SouthWest && var9 != TravelDir.South && var9 != TravelDir.SouthEast) {
                  this.gridStartY = var7.playerLevel.getIslandY() - this.gridHeight / 2;
               } else {
                  this.gridStartY = var7.playerLevel.getIslandY();
               }
            } else {
               this.gridStartY = var7.playerLevel.getIslandY() - (this.gridHeight - 1);
            }

            for(var10 = 0; var10 < this.gridWidth; ++var10) {
               for(var11 = 0; var11 < this.gridHeight; ++var11) {
                  int var12 = this.gridStartX + var10;
                  int var13 = this.gridStartY + var11;
                  this.destinations[var10][var11] = new LoadingDestination(var12, var13);
               }
            }
         }

         var7.onEvent(IslandsResponseEvent.class, (var1x) -> {
            this.applyRequestResponse(var1x.startX, var1x.startY, var1x.width, var1x.height, var1x.islands);
         }, () -> {
            return !this.isDisposed();
         });
         this.request(this.gridStartX, this.gridStartY, this.gridWidth, this.gridHeight);
      } else {
         throw new IllegalArgumentException("Width and height must be at least 80");
      }
   }

   public FormTravelContainerGrid(int var1, int var2, int var3, int var4, Client var5, TravelContainer var6, TravelContainerComponent var7) {
      this((FormTravelContainerGrid)null, var1, var2, var3, var4, var5, var6, var7);
   }

   private void request(int var1, int var2, int var3, int var4) {
      this.expectedRequests.add(new Rectangle(var1, var2, var3, var4));
      this.travelContainer.requestIslandsAction.runAndSend(var1, var2, var3, var4);
   }

   public void applyRequestResponse(int var1, int var2, int var3, int var4, IslandData[][] var5) {
      boolean var6 = false;

      int var7;
      for(var7 = 0; var7 < this.expectedRequests.size(); ++var7) {
         Rectangle var8 = (Rectangle)this.expectedRequests.get(var7);
         if (var8.x == var1 && var8.y == var2 && var8.width == var3 && var8.height == var4) {
            var6 = true;
            this.expectedRequests.remove(var7);
            --var7;
         }
      }

      if (!var6) {
         GameLog.warn.println("Received unknown travel grid response");
      }

      for(var7 = Math.max(0, var2 - this.gridStartY); var7 < this.gridHeight; ++var7) {
         int var12 = var7 + this.gridStartY - var2;
         if (var12 >= var4) {
            break;
         }

         for(int var9 = Math.max(0, var1 - this.gridStartX); var9 < this.gridWidth; ++var9) {
            int var10 = var9 + this.gridStartX - var1;
            if (var10 >= var3) {
               break;
            }

            IslandData var11 = var5[var10][var12];
            this.destinations[var9][var7] = new LoadedDestination(var11, var9, var7);
         }
      }

   }

   public void reloadNotes(int var1, int var2) {
      int var3 = var1 - this.gridStartX;
      int var4 = var2 - this.gridStartY;
      if (var3 >= 0 && var4 >= 0 && var3 < this.gridWidth && var4 < this.gridHeight) {
         if (this.destinations[var3][var4] != null) {
            this.destinations[var3][var4].loadNotes();
         }

      }
   }

   public void reloadClients(int var1, int var2) {
      int var3 = var1 - this.gridStartX;
      int var4 = var2 - this.gridStartY;
      if (var3 >= 0 && var4 >= 0 && var3 < this.gridWidth && var4 < this.gridHeight) {
         if (this.destinations[var3][var4] != null) {
            this.destinations[var3][var4].loadClients();
         }

      }
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      int var4;
      int var5;
      if (var1.state && this.isMouseOverGridSpace(var1) && var1.isMouseWheelEvent()) {
         this.wheelBuffer.add(var1);
         var4 = this.wheelBuffer.useAllScrollY();
         if (var4 < 0) {
            if (this.canScrollDown()) {
               this.scrollDown();
               this.playTickSound();
            }
         } else if (var4 > 0 && this.canScrollUp()) {
            this.scrollUp();
            this.playTickSound();
         }

         var5 = this.wheelBuffer.useAllScrollX();
         if (var5 < 0) {
            if (this.canScrollRight()) {
               this.scrollRight();
               this.playTickSound();
            }
         } else if (var5 > 0 && this.canScrollLeft()) {
            this.scrollLeft();
            this.playTickSound();
         }
      }

      this.topRightArrowHandler.handleEvent(var1);
      this.topLeftArrowHandler.handleEvent(var1);
      this.botRightArrowHandler.handleEvent(var1);
      this.botLeftArrowHandler.handleEvent(var1);
      this.topArrowHandler.handleEvent(var1);
      this.botArrowHandler.handleEvent(var1);
      this.leftArrowHandler.handleEvent(var1);
      this.rightArrowHandler.handleEvent(var1);
      if (var1.isMouseMoveEvent()) {
         this.isHoveringTop = this.isMouseOverTopArrow(var1);
         this.isHoveringBot = this.isMouseOverBotArrow(var1);
         this.isHoveringLeft = this.isMouseOverLeftArrow(var1);
         this.isHoveringRight = this.isMouseOverRightArrow(var1);
         if (this.isHoveringTop || this.isHoveringBot || this.isHoveringLeft || this.isHoveringRight) {
            var1.useMove();
         }
      }

      for(var4 = 0; var4 < this.gridWidth; ++var4) {
         for(var5 = 0; var5 < this.gridHeight; ++var5) {
            if (this.destinations[var4][var5] != null) {
               if (var1.isMouseMoveEvent()) {
                  this.destinations[var4][var5].isHovering = this.isMouseOverGrid(var1, var4, var5);
                  if (this.destinations[var4][var5].isHovering) {
                     var1.useMove();
                  }
               }

               this.destinations[var4][var5].clickHandler.handleEvent(var1);
            }
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.getState() == ControllerInput.MENU_SELECT) {
         InputEvent var4;
         if (this.isControllerFocus() && var1.buttonState) {
            if (this.controllerSelected == null) {
               this.controllerSelected = new Point(this.gridWidth / 2, this.gridHeight / 2);
            } else {
               var4 = InputEvent.ControllerButtonEvent(var1, var2);
               this.destinations[this.controllerSelected.x][this.controllerSelected.y].clickHandler.forceHandleEvent(var4);
            }

            var1.use();
         } else if (!var1.buttonState && this.controllerSelected != null) {
            var4 = InputEvent.ControllerButtonEvent(var1, var2);
            this.destinations[this.controllerSelected.x][this.controllerSelected.y].clickHandler.forceHandleEvent(var4);
         }
      } else if ((var1.getState() == ControllerInput.MENU_BACK || var1.getState() == ControllerInput.MAIN_MENU) && this.controllerSelected != null && var1.buttonState) {
         this.controllerSelected = null;
         var1.use();
      }

   }

   public void onControllerUnfocused(ControllerFocus var1) {
      super.onControllerUnfocused(var1);
      this.controllerSelected = null;
   }

   public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
      if (this.controllerSelected == null) {
         return super.handleControllerNavigate(var1, var2, var3, var4);
      } else {
         if (this.expectedRequests.size() < 4) {
            switch (var1) {
               case 0:
                  if (this.controllerSelected.y > 0) {
                     if (this.controllerSelected.y <= 1 && this.canScrollUp()) {
                        this.scrollUp();
                     } else {
                        --this.controllerSelected.y;
                     }
                  }

                  var2.use();
                  break;
               case 1:
                  if (this.controllerSelected.x < this.gridWidth) {
                     if (this.controllerSelected.x >= this.gridWidth - 2 && this.canScrollRight()) {
                        this.scrollRight();
                     } else {
                        ++this.controllerSelected.x;
                     }
                  }

                  var2.use();
                  break;
               case 2:
                  if (this.controllerSelected.y < this.gridHeight) {
                     if (this.controllerSelected.y >= this.gridHeight - 2 && this.canScrollDown()) {
                        this.scrollDown();
                     } else {
                        ++this.controllerSelected.y;
                     }
                  }

                  var2.use();
                  break;
               case 3:
                  if (this.controllerSelected.x > 0) {
                     if (this.controllerSelected.x <= 1 && this.canScrollLeft()) {
                        this.scrollLeft();
                     } else {
                        --this.controllerSelected.x;
                     }
                  }

                  var2.use();
            }
         }

         return true;
      }
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      boolean var4 = this.canScrollUp();
      boolean var5 = this.canScrollDown();
      boolean var6 = this.canScrollLeft();
      boolean var7 = this.canScrollRight();
      boolean var8 = var4 && this.isHoveringTop;
      boolean var9 = var5 && this.isHoveringBot;
      boolean var10 = var6 && this.isHoveringLeft;
      boolean var11 = var7 && this.isHoveringRight;
      int var12 = this.getX();
      int var13 = this.getY();
      if (var4) {
         this.initArrowDraw(Settings.UI.button_navigate_vertical, var8 && !var10 && !var11).draw(var12 + this.width / 2 - 8, var13 + 10);
      }

      if (var5) {
         this.initArrowDraw(Settings.UI.button_navigate_vertical, var9 && !var10 && !var11).mirrorY().draw(var12 + this.width / 2 - 8, var13 + this.height - 20);
      }

      if (var6) {
         this.initArrowDraw(Settings.UI.button_navigate_horizontal, var10 && !var8 && !var9).draw(var12 + 10, var13 + this.height / 2 - 8);
      }

      if (var7) {
         this.initArrowDraw(Settings.UI.button_navigate_horizontal, var11 && !var8 && !var9).mirrorX().draw(var12 + this.width - 20, var13 + this.height / 2 - 8);
      }

      if (var4 && var6) {
         this.initArrowDraw(Settings.UI.button_navigate_diagonal, var8 && var10).draw(var12 + 10, var13 + 10);
      }

      if (var4 && var7) {
         this.initArrowDraw(Settings.UI.button_navigate_diagonal, var8 && var11).mirrorX().draw(var12 + this.width - 24, var13 + 10);
      }

      if (var5 && var6) {
         this.initArrowDraw(Settings.UI.button_navigate_diagonal, var9 && var10).mirrorY().draw(var12 + 10, var13 + this.height - 24);
      }

      if (var5 && var7) {
         this.initArrowDraw(Settings.UI.button_navigate_diagonal, var9 && var11).mirrorX().mirrorY().draw(var12 + this.width - 24, var13 + this.height - 24);
      }

      for(int var14 = 0; var14 < this.gridWidth; ++var14) {
         for(int var15 = 0; var15 < this.gridHeight; ++var15) {
            if (this.destinations[var14][var15] != null) {
               this.destinations[var14][var15].draw(var12 + 20 + var14 * 40, var13 + 20 + var15 * 40, this.controllerSelected != null && this.controllerSelected.x == var14 && this.controllerSelected.y == var15);
            }
         }
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      if (this.controllerSelected != null) {
         Rectangle var2 = var1.boundingBox;
         var2 = new Rectangle(var2.x + 20 + this.controllerSelected.x * 40, var2.y + 20 + this.controllerSelected.y * 40, 40, 40);
         byte var3 = 5;
         var2 = new Rectangle(var2.x - var3, var2.y - var3, var2.width + var3 * 2, var2.height + var3 * 2);
         HUD.selectBoundOptions(Settings.UI.controllerFocusBoundsHighlightColor, true, var2).draw();
      } else {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

   }

   public Point getControllerTooltipAndFloatMenuPoint(ControllerFocus var1) {
      return this.controllerSelected != null ? new Point(var1.boundingBox.x + 20 + this.controllerSelected.x * 40, var1.boundingBox.y + 20 + this.controllerSelected.y * 40) : super.getControllerTooltipAndFloatMenuPoint(var1);
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
   }

   private TextureDrawOptionsEnd initArrowDraw(HoverStateTextures var1, boolean var2) {
      GameTexture var3 = var2 ? var1.highlighted : var1.active;
      return var3.initDraw().color(var2 ? Settings.UI.highlightElementColor : Settings.UI.activeElementColor);
   }

   public boolean canScrollUp() {
      return this.gridStartY > this.minIslandY;
   }

   public void scrollUp() {
      int var1;
      for(var1 = this.gridHeight - 2; var1 >= 0; --var1) {
         int var2 = var1 + 1;

         for(int var3 = 0; var3 < this.gridWidth; ++var3) {
            this.destinations[var3][var2] = this.destinations[var3][var1];
            if (this.destinations[var3][var2] != null) {
               this.destinations[var3][var2].changeGridPos(var3, var2);
            }

            this.destinations[var3][var1] = null;
         }
      }

      --this.gridStartY;

      for(var1 = 0; var1 < this.gridWidth; ++var1) {
         this.destinations[var1][0] = new LoadingDestination(this.gridStartX + var1, this.gridStartY);
      }

      this.request(this.gridStartX, this.gridStartY, this.gridWidth, 1);
      Screen.submitNextMoveEvent();
   }

   public boolean canScrollDown() {
      return this.gridStartY + this.gridHeight - 1 < this.maxIslandY;
   }

   public void scrollDown() {
      int var1;
      for(var1 = 1; var1 < this.gridHeight; ++var1) {
         int var2 = var1 - 1;

         for(int var3 = 0; var3 < this.gridWidth; ++var3) {
            this.destinations[var3][var2] = this.destinations[var3][var1];
            if (this.destinations[var3][var2] != null) {
               this.destinations[var3][var2].changeGridPos(var3, var2);
            }

            this.destinations[var3][var1] = null;
         }
      }

      ++this.gridStartY;

      for(var1 = 0; var1 < this.gridWidth; ++var1) {
         this.destinations[var1][this.gridHeight - 1] = new LoadingDestination(this.gridStartX + var1, this.gridStartY + this.gridHeight - 1);
      }

      this.request(this.gridStartX, this.gridStartY + this.gridHeight - 1, this.gridWidth, 1);
      Screen.submitNextMoveEvent();
   }

   public boolean canScrollLeft() {
      return this.gridStartX > this.minIslandX;
   }

   public void scrollLeft() {
      int var1;
      for(var1 = this.gridWidth - 2; var1 >= 0; --var1) {
         int var2 = var1 + 1;

         for(int var3 = 0; var3 < this.gridHeight; ++var3) {
            this.destinations[var2][var3] = this.destinations[var1][var3];
            if (this.destinations[var2][var3] != null) {
               this.destinations[var2][var3].changeGridPos(var2, var3);
            }

            this.destinations[var1][var3] = null;
         }
      }

      --this.gridStartX;

      for(var1 = 0; var1 < this.gridHeight; ++var1) {
         this.destinations[0][var1] = new LoadingDestination(this.gridStartX, this.gridStartY + var1);
      }

      this.request(this.gridStartX, this.gridStartY, 1, this.gridHeight);
      Screen.submitNextMoveEvent();
   }

   public boolean canScrollRight() {
      return this.gridStartX + this.gridWidth - 1 < this.maxIslandX;
   }

   public void scrollRight() {
      int var1;
      for(var1 = 1; var1 < this.gridWidth; ++var1) {
         int var2 = var1 - 1;

         for(int var3 = 0; var3 < this.gridHeight; ++var3) {
            this.destinations[var2][var3] = this.destinations[var1][var3];
            if (this.destinations[var2][var3] != null) {
               this.destinations[var2][var3].changeGridPos(var2, var3);
            }

            this.destinations[var1][var3] = null;
         }
      }

      ++this.gridStartX;

      for(var1 = 0; var1 < this.gridHeight; ++var1) {
         this.destinations[this.gridWidth - 1][var1] = new LoadingDestination(this.gridStartX + this.gridWidth - 1, this.gridStartY + var1);
      }

      this.request(this.gridStartX + this.gridWidth - 1, this.gridStartY, 1, this.gridHeight);
      Screen.submitNextMoveEvent();
   }

   private boolean isMouseOverTopArrow(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getY(), this.width, 20)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   private boolean isMouseOverBotArrow(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getY() + this.height - 20, this.width, 20)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   private boolean isMouseOverLeftArrow(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX(), this.getY(), 20, this.height)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   private boolean isMouseOverRightArrow(InputEvent var1) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX() + this.width - 20, this.getY(), 20, this.height)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   private boolean isMouseOverGrid(InputEvent var1, int var2, int var3) {
      return var1.isMoveUsed() ? false : (new Rectangle(this.getX() + 20 + var2 * 40 + 2, this.getY() + 20 + var3 * 40 + 2, 36, 36)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   private boolean isMouseOverGridSpace(InputEvent var1) {
      return (new Rectangle(this.getX() + 20, this.getY() + 20, this.width - 40, this.height - 40)).contains(var1.pos.hudX, var1.pos.hudY);
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   private abstract class FormDestination implements ControllerFocusHandler {
      protected FormClickHandler clickHandler;
      protected boolean isHovering;

      private FormDestination() {
      }

      public abstract void draw(int var1, int var2, boolean var3);

      public void loadNotes() {
      }

      public void loadClients() {
      }

      public void changeGridPos(int var1, int var2) {
      }

      public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      }

      public boolean handleControllerNavigate(int var1, ControllerEvent var2, TickManager var3, PlayerMob var4) {
         return false;
      }

      // $FF: synthetic method
      FormDestination(Object var2) {
         this();
      }
   }

   private class LoadingDestination extends FormDestination {
      public final int islandX;
      public final int islandY;
      public final Biome biome;

      public LoadingDestination(int var2, int var3) {
         super(null);
         this.islandX = var2;
         this.islandY = var3;
         this.biome = BiomeRegistry.UNKNOWN;
         this.clickHandler = new FormClickHandler((var0) -> {
            return false;
         }, (var0) -> {
            return false;
         }, (var0) -> {
         });
      }

      public void draw(int var1, int var2, boolean var3) {
         boolean var4 = this.isHovering || var3;
         Color var5 = var4 ? Settings.UI.highlightElementColor : Settings.UI.activeElementColor;
         GameTexture var6 = var4 ? Settings.UI.biome_border.highlighted : Settings.UI.biome_border.active;
         var5 = new Color(var5.getRed(), var5.getGreen(), var5.getBlue(), 100);
         this.biome.getIconTexture(var4).initDraw().sprite(0, 0, 32).color(var5).draw(var1 + 4, var2 + 4);
         var6.initDraw().color(var5).draw(var1, var2);
         if (var4) {
            String var7 = (String)Settings.mapCoordinates.displayCoordinates.apply(new Point(this.islandX, this.islandY), FormTravelContainerGrid.this);
            String var8 = var7 == null ? "" : " (" + var7 + ")";
            String var9 = GlobalData.debugCheatActive() ? " (" + WorldGenerator.getIslandSize(this.islandX, this.islandY) + ")" : "";
            StringTooltips var10 = new StringTooltips(this.biome.getDisplayName() + var8 + var9);
            var10.add(Localization.translate("ui", "travelloadingisland"));
            Screen.addTooltip(var10, TooltipLocation.FORM_FOCUS);
         }

      }
   }

   private class LoadedDestination extends FormDestination {
      public final IslandData destination;
      public final Biome biome;
      public boolean hasDeath;
      public FairTypeDrawOptions noteDrawOptions;
      public final boolean isWorldSpawn;
      public final boolean isPlayerSpawn;
      public final List<ClientClient> clientsThere;
      public final GameRandom random;
      public final long seed;

      public LoadedDestination(IslandData var2, int var3, int var4) {
         super(null);
         this.destination = var2;
         this.biome = BiomeRegistry.getBiome(var2.biome);
         this.hasDeath = var2.hasDeath;
         this.isWorldSpawn = FormTravelContainerGrid.this.travelContainer.worldSpawnLevel.isIslandPosition() && this.destination.islandX == FormTravelContainerGrid.this.travelContainer.worldSpawnLevel.getIslandX() && this.destination.islandY == FormTravelContainerGrid.this.travelContainer.worldSpawnLevel.getIslandY();
         this.isPlayerSpawn = FormTravelContainerGrid.this.travelContainer.playerSpawnLevel.isIslandPosition() && this.destination.islandX == FormTravelContainerGrid.this.travelContainer.playerSpawnLevel.getIslandX() && this.destination.islandY == FormTravelContainerGrid.this.travelContainer.playerSpawnLevel.getIslandY();
         this.random = new GameRandom();
         this.seed = this.random.nextLong();
         this.clientsThere = new ArrayList();
         this.loadNotes();
         this.loadClients();
         this.changeGridPos(var3, var4);
      }

      public void loadNotes() {
         this.noteDrawOptions = null;
         String var1 = FormTravelContainerGrid.this.client.islandNotes.get(this.destination.islandX, this.destination.islandY);
         if (var1 != null && !var1.isEmpty()) {
            FairType var2 = new FairType();
            FontOptions var3 = (new FontOptions(Settings.tooltipTextSize)).outline();
            var2.append(var3, var1);
            var2.applyParsers(TravelContainerComponent.getNoteParsers(var3));
            this.noteDrawOptions = var2.getDrawOptions(FairType.TextAlign.LEFT, 280, true, true);
         }

      }

      public void loadClients() {
         this.clientsThere.clear();
         ClientClient var1 = FormTravelContainerGrid.this.client.getClient();

         for(int var2 = 0; var2 < FormTravelContainerGrid.this.client.getSlots(); ++var2) {
            ClientClient var3 = FormTravelContainerGrid.this.client.getClient(var2);
            if (var3 != null && var3.loadedPlayer && var3.isSameTeam(var1)) {
               LevelIdentifier var4 = var3.getLevelIdentifier();
               if (var4.isIslandPosition() && var4.getIslandX() == this.destination.islandX && var4.getIslandY() == this.destination.islandY) {
                  this.clientsThere.add(var3);
               }
            }
         }

      }

      public void changeGridPos(int var1, int var2) {
         this.clickHandler = new FormClickHandler((var3) -> {
            return FormTravelContainerGrid.this.isMouseOverGrid(var3, var1, var2);
         }, (var0) -> {
            return var0.getID() == -100 || var0.getID() == -99;
         }, (var1x) -> {
            if (!this.destination.isOutsideWorldBorder) {
               FormTravelContainerGrid.this.playTickSound();
               if (var1x.getID() != -99 && (FormTravelContainerGrid.this.travelContainer.travelDir != TravelDir.None || var1x.getID() != -100) && !var1x.isControllerEvent()) {
                  if (var1x.getID() == -100) {
                     if (this.destination.canTravel && FormTravelContainerGrid.this.travelContainer.travelDir != TravelDir.None) {
                        FormTravelContainerGrid.this.travelContainerForm.travelTo(this.destination);
                     } else {
                        FormTravelContainerGrid.this.travelContainerForm.focusTravel(this.destination, this.biome);
                     }
                  }
               } else {
                  SelectionFloatMenu var2 = new SelectionFloatMenu(FormTravelContainerGrid.this.travelContainerForm);
                  if (this.destination.canTravel && FormTravelContainerGrid.this.travelContainer.travelDir != TravelDir.None) {
                     var2.add(Localization.translate("ui", "travelconfirm"), () -> {
                        FormTravelContainerGrid.this.travelContainerForm.travelTo(this.destination);
                        var2.remove();
                     });
                  }

                  var2.add(Localization.translate("ui", "travelsetnotes"), () -> {
                     FormTravelContainerGrid.this.travelContainerForm.focusTravel(this.destination, this.biome);
                     var2.remove();
                  });
                  var2.add(Localization.translate("ui", "clearrecentdeaths"), () -> {
                     return this.hasDeath;
                  }, (Color)null, (Supplier)null, () -> {
                     FormTravelContainerGrid.this.client.network.sendPacket(new PacketRemoveDeathLocations(this.destination.islandX, this.destination.islandY));
                     this.hasDeath = false;
                     if (FormTravelContainerGrid.this.client.getLevel().getIdentifier().equals(this.destination.islandX, this.destination.islandY, 0)) {
                        FormTravelContainerGrid.this.client.levelManager.clearDeathLocations();
                     }

                     var2.remove();
                  });
                  FormTravelContainerGrid.this.getManager().openFloatMenu(var2);
               }

            }
         });
      }

      public void draw(int var1, int var2, boolean var3) {
         boolean var5 = this.isHovering || var3;
         GameTexture var6 = var5 ? Settings.UI.biome_border.highlighted : Settings.UI.biome_border.active;
         Color var4;
         if (this.destination.isOutsideWorldBorder || !this.destination.canTravel && FormTravelContainerGrid.this.travelContainer.travelDir != TravelDir.None) {
            var4 = Settings.UI.inactiveElementColor;
         } else {
            var4 = var5 ? Settings.UI.highlightElementColor : Settings.UI.activeElementColor;
         }

         this.biome.getIconTexture(var5).initDraw().sprite(0, 0, 32).color(var4).draw(var1 + 4, var2 + 4);
         var6.initDraw().color(var4).draw(var1, var2);
         if (var5) {
            String var7 = (String)Settings.mapCoordinates.displayCoordinates.apply(new Point(this.destination.islandX, this.destination.islandY), FormTravelContainerGrid.this);
            String var8 = var7 == null ? "" : " (" + var7 + ")";
            String var9 = GlobalData.debugCheatActive() ? " (" + WorldGenerator.getIslandSize(this.destination.islandX, this.destination.islandY) + ")" : "";
            ListGameTooltips var10 = new ListGameTooltips(this.biome.getDisplayName() + var8 + var9);
            if (this.destination.isOutsideWorldBorder) {
               var10.add(Localization.translate("ui", "travelserverborder"));
            } else if (!this.destination.canTravel && FormTravelContainerGrid.this.travelContainer.travelDir != TravelDir.None) {
               var10.add(Localization.translate("ui", "traveloutrange"));
            }

            if (this.isWorldSpawn) {
               var10.add(Localization.translate("ui", "travelworldspawn"));
            }

            if (!this.isWorldSpawn && this.isPlayerSpawn) {
               var10.add(Localization.translate("ui", "travelselfspawn"));
            }

            if (this.destination.settlementName != null) {
               var10.add("\u00a7#6c53ff" + this.destination.settlementName.translate());
            }

            if (this.hasDeath) {
               var10.add(GameColor.RED.getColorCode() + Localization.translate("misc", "recentdeath"));
            }

            if (!this.destination.isOutsideWorldBorder) {
               var10.add(this.destination.discovered ? Localization.translate("ui", "traveldiscovered") : Localization.translate("ui", "travelnotdiscovered"));
               var10.add(this.destination.visited ? Localization.translate("ui", "travelvisited") : Localization.translate("ui", "travelnotvisited"));
            }

            if (!this.clientsThere.isEmpty()) {
               var10.add(Localization.translate("ui", "travelteam"));
               Iterator var11 = this.clientsThere.iterator();

               while(var11.hasNext()) {
                  ClientClient var12 = (ClientClient)var11.next();
                  var10.add(var12.getName());
               }
            }

            if (this.noteDrawOptions != null) {
               var10.add((Object)(new StringTooltips(Localization.translate("ui", "travelnotes"), GameColor.PURPLE)));
               var10.add((Object)(new FairTypeTooltip(this.noteDrawOptions)));
            }

            Screen.addTooltip(var10, TooltipLocation.FORM_FOCUS);
         }

         int var13 = 0;
         if (this.noteDrawOptions != null) {
            Settings.UI.note_island.initDraw().color(var4).draw(var1 + 5 + this.getIconDrawX(var13), var2 + 5 + this.getIconDrawY(var13));
            ++var13;
         }

         if (this.isWorldSpawn || this.isPlayerSpawn) {
            Settings.UI.spawn_island.initDraw().color(var4).draw(var1 + 5 + this.getIconDrawX(var13), var2 + 5 + this.getIconDrawY(var13));
            ++var13;
         }

         if (this.destination.settlementName != null) {
            Settings.UI.settlement_island.initDraw().color(var4).draw(var1 + 5 + this.getIconDrawX(var13), var2 + 5 + this.getIconDrawY(var13));
            ++var13;
         }

         if (this.hasDeath) {
            Settings.UI.deathmarker_note.initDraw().color(var4).draw(var1 + 5 + this.getIconDrawX(var13), var2 + 5 + this.getIconDrawY(var13));
            ++var13;
         }

         if (this.destination.visited) {
            Settings.UI.visited_note.initDraw().color(var4).draw(var1 + 5 + this.getIconDrawX(var13), var2 + 5 + this.getIconDrawY(var13));
            ++var13;
         }

         if (FormTravelContainerGrid.this.client.getLevel().getIdentifier().isSameIsland(this.destination.islandX, this.destination.islandY)) {
            PlayerSprite.getIconDrawOptions(var1 + 4, var2 + 4, FormTravelContainerGrid.this.client.getPlayer()).draw();
         } else if (this.clientsThere.size() != 0) {
            PlayerMob var14 = ((ClientClient)this.clientsThere.get(this.random.seeded(this.seed).nextInt(this.clientsThere.size()))).playerMob;
            PlayerSprite.getIconDrawOptions(var1 + 4, var2 + 4, var14).draw();
         }

      }

      private int getIconDrawX(int var1) {
         if (var1 > 3) {
            ++var1;
         }

         return var1 / 3 * 10;
      }

      private int getIconDrawY(int var1) {
         if (var1 > 3) {
            ++var1;
         }

         return var1 % 3 * 10;
      }
   }

   public static enum CoordinateSetting {
      RELATIVE_SELF(new LocalMessage("ui", "travelcoordself"), (var0, var1) -> {
         return var1.travelContainer.playerLevel.isIslandPosition() ? var0.x - var1.travelContainer.playerLevel.getIslandX() + ", " + (var0.y - var1.travelContainer.playerLevel.getIslandY()) : null;
      }),
      RELATIVE_SELF_SPAWN(new LocalMessage("ui", "travelcoordselfspawn"), (var0, var1) -> {
         return var1.travelContainer.playerSpawnLevel.isIslandPosition() ? var0.x - var1.travelContainer.playerSpawnLevel.getIslandX() + ", " + (var0.y - var1.travelContainer.playerSpawnLevel.getIslandY()) : null;
      }),
      RELATIVE_WORLD_SPAWN(new LocalMessage("ui", "travelcoordworldspawn"), (var0, var1) -> {
         return var1.travelContainer.worldSpawnLevel.isIslandPosition() ? var0.x - var1.travelContainer.worldSpawnLevel.getIslandX() + ", " + (var0.y - var1.travelContainer.worldSpawnLevel.getIslandY()) : null;
      }),
      GLOBAL(new LocalMessage("ui", "travelcoordglobal"), (var0, var1) -> {
         return var0.x + "," + var0.y;
      });

      public GameMessage displayName;
      public BiFunction<Point, FormTravelContainerGrid, String> displayCoordinates;

      private CoordinateSetting(GameMessage var3, BiFunction var4) {
         this.displayName = var3;
         this.displayCoordinates = var4;
      }

      // $FF: synthetic method
      private static CoordinateSetting[] $values() {
         return new CoordinateSetting[]{RELATIVE_SELF, RELATIVE_SELF_SPAWN, RELATIVE_WORLD_SPAWN, GLOBAL};
      }
   }
}
