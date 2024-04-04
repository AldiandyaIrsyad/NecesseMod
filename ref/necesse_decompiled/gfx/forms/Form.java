package necesse.gfx.forms;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.control.InputPosition;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormMoveEvent;
import necesse.gfx.forms.events.FormResizeEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.HUD;

public class Form extends FormComponent implements FormPositionContainer, ComponentListContainer<FormComponent> {
   public final String name;
   private ComponentList<FormComponent> components;
   private boolean hidden;
   private FormPosition position;
   private int width;
   private int height;
   private Set<Point> tiles;
   private List<Rectangle> tileBounds;
   private int tileWidth;
   private int tileHeight;
   private int tileXPadding;
   private int tileYPadding;
   private Rectangle draggingBox;
   private boolean showDraggingCursor;
   private boolean isHoveringDraggingBox;
   private InputEvent draggingMouseDown;
   private Point draggingStartPos;
   private boolean resizeUp;
   private boolean resizeDown;
   private boolean resizeLeft;
   private boolean resizeRight;
   protected Dimension resizeMinimum;
   private ResizeDir resizeDir;
   private InputEvent resizeMouseDown;
   private Point resizeStartPos;
   private Dimension resizeStartDim;
   public boolean shouldLimitDrawArea;
   public boolean drawBase;
   protected FormEventsHandler<FormMoveEvent<Form>> dragEvents;
   protected FormEventsHandler<FormResizeEvent<Form>> resizeEvents;

   protected Form(String var1, Set<Point> var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this.resizeMinimum = new Dimension(0, 0);
      this.shouldLimitDrawArea = true;
      this.dragEvents = new FormEventsHandler();
      this.resizeEvents = new FormEventsHandler();
      this.name = var1;
      this.width = var5;
      this.height = var6;
      if (var2 != null) {
         this.setTiled(var2, var3, var4, var7, var8);
      }

      this.position = new FormFixedPosition(0, 0);
      this.components = new ComponentList<FormComponent>() {
         public InputEvent offsetEvent(InputEvent var1, boolean var2) {
            int var3;
            if (!var2 && (var1.pos.hudX < Form.this.getX() || var1.pos.hudX > Form.this.getX() + Form.this.getWidth())) {
               var3 = Integer.MIN_VALUE;
            } else {
               var3 = var1.pos.hudX - Form.this.getX();
               if (Form.this.tiles != null) {
                  var3 -= Form.this.tileXPadding;
               }
            }

            int var4;
            if (!var2 && (var1.pos.hudY < Form.this.getY() || var1.pos.hudY > Form.this.getY() + Form.this.getHeight())) {
               var4 = Integer.MIN_VALUE;
            } else {
               var4 = var1.pos.hudY - Form.this.getY();
               if (Form.this.tiles != null) {
                  var4 -= Form.this.tileYPadding;
               }
            }

            return InputEvent.ReplacePosEvent(var1, InputPosition.fromHudPos(Screen.input(), var3, var4));
         }

         public FormManager getManager() {
            return Form.this.getManager();
         }
      };
      this.drawBase = true;
      this.zIndex = 0;
   }

   public Form(String var1, int var2, int var3) {
      this(var1, (Set)null, var2, var3, var2, var3);
   }

   public Form(int var1, int var2) {
      this((String)null, var1, var2);
   }

   public Form(String var1, Set<Point> var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var3, var4, var5, var6);
   }

   public Form(Set<Point> var1, int var2, int var3) {
      this((String)null, var1, var2, var3);
   }

   public Form(String var1, Set<Point> var2, int var3, int var4) {
      this(var1, var2, var3, var3, var4, var4);
   }

   public Form(Set<Point> var1, int var2) {
      this((String)null, var1, var2, 0);
   }

   protected void init() {
      super.init();
      this.components.init();
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (!this.isHidden()) {
         if (var1.isMouseMoveEvent()) {
            this.isHoveringDraggingBox = false;
            int var4;
            int var5;
            if (this.resizeMouseDown != null) {
               var4 = GameMath.limit(Screen.mousePos().hudX, 0, Screen.getHudWidth()) - this.resizeMouseDown.pos.hudX;
               var5 = GameMath.limit(Screen.mousePos().hudY, 0, Screen.getHudHeight()) - this.resizeMouseDown.pos.hudY;
               int var6 = this.getX();
               int var7 = this.getY();
               int var8 = this.width;
               int var9 = this.height;
               if (this.resizeDir != Form.ResizeDir.LEFT && this.resizeDir != Form.ResizeDir.UP_LEFT && this.resizeDir != Form.ResizeDir.DOWN_LEFT) {
                  if (this.resizeDir == Form.ResizeDir.RIGHT || this.resizeDir == Form.ResizeDir.UP_RIGHT || this.resizeDir == Form.ResizeDir.DOWN_RIGHT) {
                     var8 = Math.max(this.resizeStartDim.width + var4, this.resizeMinimum.width);
                  }
               } else {
                  var6 = this.resizeStartPos.x + Math.min(var4, this.resizeStartDim.width - this.resizeMinimum.width);
                  var8 = Math.max(this.resizeStartDim.width - var4, this.resizeMinimum.width);
               }

               if (this.resizeDir != Form.ResizeDir.UP && this.resizeDir != Form.ResizeDir.UP_LEFT && this.resizeDir != Form.ResizeDir.UP_RIGHT) {
                  if (this.resizeDir == Form.ResizeDir.DOWN || this.resizeDir == Form.ResizeDir.DOWN_LEFT || this.resizeDir == Form.ResizeDir.DOWN_RIGHT) {
                     var9 = Math.max(this.resizeStartDim.height + var5, this.resizeMinimum.height);
                  }
               } else {
                  var7 = this.resizeStartPos.y + Math.min(var5, this.resizeStartDim.height - this.resizeMinimum.height);
                  var9 = Math.max(this.resizeStartDim.height - var5, this.resizeMinimum.height);
               }

               FormResizeEvent var10 = new FormResizeEvent(this, var1, var6, var7, var8, var9);
               this.resizeEvents.onEvent(var10);
               if (!var10.hasPreventedDefault()) {
                  this.setPosition(var10.x, var10.y);
                  this.setWidth(var10.width);
                  this.setHeight(var10.height);
                  var1.useMove();
               }
            } else if (this.draggingMouseDown != null) {
               this.isHoveringDraggingBox = false;
               var4 = Screen.mousePos().hudX - this.draggingMouseDown.pos.hudX;
               var5 = Screen.mousePos().hudY - this.draggingMouseDown.pos.hudY;
               FormMoveEvent var13 = new FormMoveEvent(this, var1, this.draggingStartPos.x + var4, this.draggingStartPos.y + var5);
               this.dragEvents.onEvent(var13);
               if (!var13.hasPreventedDefault()) {
                  this.setPosition(var13.x, var13.y);
                  var1.useMove();
               }
            }
         }

         InputEvent var11;
         if (!this.resizeUp && !this.resizeDown && !this.resizeLeft && !this.resizeRight) {
            this.resizeDir = null;
         } else {
            var11 = this.components.offsetEvent(var1, true);
            byte var12 = 2;
            byte var14 = 6;
            if (!var11.isMoveUsed() && this.resizeMouseDown == null) {
               this.resizeDir = null;
               if (var11.pos.hudX >= -var14 && var11.pos.hudX <= this.width + var14 && var11.pos.hudY >= -var14 && var11.pos.hudY <= this.height + var14) {
                  if (this.resizeLeft && var11.pos.hudX < var12) {
                     this.resizeDir = Form.ResizeDir.LEFT;
                  }

                  if (this.resizeRight && var11.pos.hudX > this.width - var12 - 1) {
                     this.resizeDir = Form.ResizeDir.RIGHT;
                  }

                  if (this.resizeUp && var11.pos.hudY < var12) {
                     if (this.resizeLeft && var11.pos.hudX < var12) {
                        this.resizeDir = Form.ResizeDir.UP_LEFT;
                     } else if (this.resizeRight && var11.pos.hudX > this.width - var12 - 1) {
                        this.resizeDir = Form.ResizeDir.UP_RIGHT;
                     } else {
                        this.resizeDir = Form.ResizeDir.UP;
                     }
                  }

                  if (this.resizeDown && var11.pos.hudY > this.height - var12 - 1) {
                     if (this.resizeLeft && var11.pos.hudX < var12) {
                        this.resizeDir = Form.ResizeDir.DOWN_LEFT;
                     } else if (this.resizeRight && var11.pos.hudX > this.width - var12 - 1) {
                        this.resizeDir = Form.ResizeDir.DOWN_RIGHT;
                     } else {
                        this.resizeDir = Form.ResizeDir.DOWN;
                     }
                  }
               }

               if (this.resizeDir != null) {
                  var11.useMove();
               }
            }

            if (var11.getID() == -100) {
               if (!var11.state) {
                  if (this.resizeMouseDown != null) {
                     this.resizeMouseDown = null;
                     var11.use();
                  }
               } else if (this.resizeDir != null) {
                  this.resizeMouseDown = InputEvent.MouseMoveEvent(Screen.mousePos(), var2);
                  this.resizeStartPos = new Point(this.getX(), this.getY());
                  this.resizeStartDim = new Dimension(this.getWidth(), this.getHeight());
                  var11.use();
               }
            }
         }

         if (!var1.isUsed()) {
            this.components.submitInputEvent(var1, var2, var3);
            if (!var1.isUsed()) {
               if (this.draggingBox != null) {
                  if (var1.isMouseMoveEvent()) {
                     var11 = this.components.offsetEvent(var1, false);
                     this.isHoveringDraggingBox = !var1.isMoveUsed() && this.draggingBox.contains(var11.pos.hudX, var11.pos.hudY);
                  } else if (var1.getID() == -100) {
                     var11 = this.components.offsetEvent(var1, false);
                     if (!var11.state) {
                        if (this.draggingMouseDown != null) {
                           this.draggingMouseDown = null;
                           var11.use();
                        }
                     } else if (this.draggingBox.contains(var11.pos.hudX, var11.pos.hudY)) {
                        this.draggingMouseDown = InputEvent.MouseMoveEvent(Screen.mousePos(), var2);
                        this.draggingStartPos = new Point(this.getX(), this.getY());
                        var11.use();
                     }
                  }
               }

            }
         }
      }
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      if (!this.isHidden()) {
         this.components.submitControllerEvent(var1, var2, var3);
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (!this.isHidden()) {
         if (this.shouldLimitDrawArea) {
            var5 = var5.intersection(new Rectangle(var2 + this.getX() + this.tileXPadding, var3 + this.getY() + this.tileYPadding, this.width, this.height));
         }

         if (var6) {
            Screen.drawShape(var5, false, 0.0F, 1.0F, 1.0F, 1.0F);
         }

         this.components.addNextControllerComponents(var1, var2 + this.getX() + this.tileXPadding, var3 + this.getY() + this.tileYPadding, var4, var5, var6);
      }

   }

   public Form onDragged(FormEventListener<FormMoveEvent<Form>> var1) {
      this.dragEvents.addListener(var1);
      return this;
   }

   public Form onResize(FormEventListener<FormResizeEvent<Form>> var1) {
      this.resizeEvents.addListener(var1);
      return this;
   }

   public Form setMinimumResize(int var1, int var2) {
      this.resizeMinimum = new Dimension(var1, var2);
      return this;
   }

   public Form allowResize(FormEventListener<FormResizeEvent<Form>> var1) {
      return this.allowResize(true, true, true, true, var1);
   }

   public Form allowResize(boolean var1, boolean var2, boolean var3, boolean var4, FormEventListener<FormResizeEvent<Form>> var5) {
      this.resizeUp = var1;
      this.resizeDown = var2;
      this.resizeLeft = var3;
      this.resizeRight = var4;
      this.onResize(var5);
      return this;
   }

   public ComponentList<FormComponent> getComponentList() {
      return this.components;
   }

   public void setPosMiddle(int var1, int var2) {
      this.setPosition(var1 - this.width / 2, var2 - this.height / 2);
   }

   public void drawBase(TickManager var1) {
      Performance.record(var1, "base", (Runnable)(() -> {
         if (Settings.UI.form.backgroundTexture != null) {
            if (this.tiles != null) {
               int var1 = Settings.UI.form.edgeResolution * 2 - Settings.UI.form.edgeMargin * 2;
               HUD.getBackgroundEdged(Settings.UI.form.backgroundTexture, Settings.UI.form.edgeResolution, var1 - this.tileXPadding * 2, var1 - this.tileYPadding * 2, this.getX() + this.tileXPadding, this.getY() + this.tileYPadding, this.tiles, this.tileWidth, this.tileHeight).draw();
            } else {
               GameBackground.form.getDrawOptions(this.getX(), this.getY(), this.width, this.height).draw();
            }

         }
      }));
   }

   public void drawEdge(TickManager var1) {
      Performance.record(var1, "base", (Runnable)(() -> {
         if (Settings.UI.form.edgeTexture != null) {
            if (this.tiles != null) {
               int var1 = Settings.UI.form.edgeResolution * 2 - Settings.UI.form.edgeMargin * 2;
               HUD.getBackgroundEdged(Settings.UI.form.edgeTexture, Settings.UI.form.edgeResolution, var1 - this.tileXPadding * 2, var1 - this.tileYPadding * 2, this.getX() + this.tileXPadding, this.getY() + this.tileYPadding, this.tiles, this.tileWidth, this.tileHeight).draw();
            } else {
               GameBackground.form.getEdgeDrawOptions(this.getX(), this.getY(), this.width, this.height).draw();
            }

         }
      }));
   }

   public void drawComponents(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.components.drawComponents(var1, var2, var3);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Performance.record(var1, this.name == null ? "form" : this.name, (Runnable)(() -> {
         if (this.resizeDir != null) {
            switch (this.resizeDir) {
               case UP_LEFT:
               case DOWN_RIGHT:
                  Screen.setCursor(Screen.CURSOR.ARROWS_DIAGONAL1);
                  break;
               case UP_RIGHT:
               case DOWN_LEFT:
                  Screen.setCursor(Screen.CURSOR.ARROWS_DIAGONAL2);
                  break;
               case UP:
               case DOWN:
                  Screen.setCursor(Screen.CURSOR.ARROWS_VERTICAL);
                  break;
               case LEFT:
               case RIGHT:
                  Screen.setCursor(Screen.CURSOR.ARROWS_HORIZONTAL);
            }
         }

         if (this.drawBase) {
            this.drawBase(var1);
         }

         Rectangle var4;
         if (this.shouldLimitDrawArea) {
            var4 = new Rectangle(this.width, this.height);
         } else {
            var4 = new Rectangle(-this.getX(), -this.getY(), Screen.getHudWidth(), Screen.getHudHeight());
         }

         Rectangle var5 = new Rectangle(this.width, this.height);
         Rectangle var6;
         if (var3 == null) {
            var6 = null;
         } else {
            var6 = var5.intersection(new Rectangle(var3.x - this.getX(), var3.y - this.getY(), var3.width, var3.height));
         }

         if (this.showDraggingCursor) {
            if (this.draggingMouseDown != null) {
               Screen.setCursor(Screen.CURSOR.GRAB_ON);
            } else if (this.isHoveringDraggingBox) {
               Screen.setCursor(Screen.CURSOR.GRAB_OFF);
            }
         }

         int var7 = this.getX();
         int var8 = this.getY();
         if (this.tiles != null) {
            var7 += this.tileXPadding;
            var8 += this.tileYPadding;
         }

         FormShader.FormShaderState var9 = GameResources.formShader.startState(new Point(var7, var8), var4);

         try {
            this.drawComponents(var1, var2, var6);
         } finally {
            var9.end();
         }

         if (this.drawBase) {
            this.drawEdge(var1);
         }

      }));
   }

   public List<Rectangle> getHitboxes() {
      if (this.isHidden()) {
         return singleBox(new Rectangle(this.getX(), this.getY(), 0, 0));
      } else if (this.drawBase) {
         if (this.tiles == null) {
            return singleBox(new Rectangle(this.getX() - 2, this.getY() - 2, this.width + 4, this.height + 4));
         } else {
            LinkedList var5 = new LinkedList();
            Iterator var6 = this.tileBounds.iterator();

            while(var6.hasNext()) {
               Rectangle var7 = (Rectangle)var6.next();
               var5.add(new Rectangle(this.getX() + var7.x * this.tileWidth - this.tileXPadding - 2, this.getY() + var7.y * this.tileHeight - this.tileYPadding - 2, var7.width * this.tileWidth + this.tileXPadding * 2 + 4, var7.height * this.tileHeight + this.tileYPadding * 2 + 4));
            }

            return var5;
         }
      } else {
         List var1 = this.components.getHitboxes();
         ArrayList var2 = new ArrayList(var1.size());
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Rectangle var4 = (Rectangle)var3.next();
            var2.add(new Rectangle(this.getX() + var4.x, this.getY() + var4.y, var4.width, var4.height));
         }

         return var2;
      }
   }

   public int getWidth() {
      return this.tiles != null ? this.width + this.tileXPadding * 2 : this.width;
   }

   public int getHeight() {
      return this.tiles != null ? this.height + this.tileYPadding * 2 : this.height;
   }

   public void setWidth(int var1) {
      this.width = var1;
      this.tiles = null;
      this.tileBounds = null;
   }

   public void setHeight(int var1) {
      this.height = var1;
      this.tiles = null;
      this.tileBounds = null;
   }

   public void setTiled(Set<Point> var1, int var2, int var3, int var4, int var5) {
      this.tileWidth = var2;
      this.tileHeight = var3;
      this.tileXPadding = var4;
      this.tileYPadding = var5;
      if (var1.isEmpty()) {
         this.tiles = new HashSet();
         this.tileBounds = new ArrayList();
         this.width = 0;
         this.height = 0;
      } else {
         int var6 = Integer.MAX_VALUE;
         int var7 = Integer.MAX_VALUE;
         int var8 = Integer.MIN_VALUE;
         int var9 = Integer.MIN_VALUE;
         Iterator var10 = var1.iterator();

         while(var10.hasNext()) {
            Point var11 = (Point)var10.next();
            if (var11.x < var6) {
               var6 = var11.x;
            }

            if (var11.y < var7) {
               var7 = var11.y;
            }

            if (var11.x > var8) {
               var8 = var11.x;
            }

            if (var11.y > var9) {
               var9 = var11.y;
            }
         }

         TreeSet var13 = Zoning.getNewZoneSet();
         Iterator var14 = var1.iterator();

         while(var14.hasNext()) {
            Point var12 = (Point)var14.next();
            var13.add(new Point(var12.x - var6, var12.y - var7));
         }

         this.tiles = var13;
         this.tileBounds = Zoning.toRectangles(var13);
         this.width = (var8 - var6 + 1) * var2;
         this.height = (var9 - var7 + 1) * var3;
      }

   }

   public void setTiled(Set<Point> var1, int var2, int var3) {
      this.setTiled(var1, var2, var2, var3, var3);
   }

   public void setDraggingBox(Rectangle var1, boolean var2) {
      this.draggingBox = var1;
      if (this.draggingBox == null) {
         this.draggingMouseDown = null;
         this.draggingStartPos = null;
      }

      this.showDraggingCursor = var2;
   }

   public void setDraggingBox(Rectangle var1) {
      this.setDraggingBox(var1, true);
   }

   public Rectangle getDraggingBox() {
      return this.draggingBox;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }

   public boolean shouldDraw() {
      return !this.isHidden();
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean var1) {
      if (this.hidden != var1) {
         this.hidden = var1;
         Screen.submitNextMoveEvent();
      }

   }

   public void dispose() {
      super.dispose();
      this.components.disposeComponents();
   }

   private static enum ResizeDir {
      UP_LEFT,
      UP,
      UP_RIGHT,
      LEFT,
      RIGHT,
      DOWN_LEFT,
      DOWN,
      DOWN_RIGHT;

      private ResizeDir() {
      }

      // $FF: synthetic method
      private static ResizeDir[] $values() {
         return new ResizeDir[]{UP_LEFT, UP, UP_RIGHT, LEFT, RIGHT, DOWN_LEFT, DOWN, DOWN_RIGHT};
      }
   }
}
