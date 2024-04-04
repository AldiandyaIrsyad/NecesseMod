package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.hudManager.HudDrawElement;

public class LinePathGenerationTestGameTool extends MouseDebugGameTool {
   public Point p1;
   public Point p2;
   public Line2D.Float line;
   public GameLinkedList<Point> path;
   public HudDrawElement hudElement;

   public LinePathGenerationTestGameTool(DebugForm var1, String var2) {
      super(var1, var2);
      this.onLeftEvent((var1x) -> {
         if (var1x.state) {
            this.p1 = new Point(this.getMouseTileX(), this.getMouseTileY());
            this.generatePath();
         }

         return true;
      }, "Select point 1");
      this.onRightEvent((var1x) -> {
         if (var1x.state) {
            this.p2 = new Point(this.getMouseTileX(), this.getMouseTileY());
            this.generatePath();
         }

         return true;
      }, "Select point 2");
   }

   public void init() {
      this.p1 = null;
      this.p2 = null;
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1) {
                  if (LinePathGenerationTestGameTool.this.line != null) {
                     Screen.drawLineRGBA(var2.getDrawX(LinePathGenerationTestGameTool.this.line.x1), var2.getDrawY(LinePathGenerationTestGameTool.this.line.y1), var2.getDrawX(LinePathGenerationTestGameTool.this.line.x2), var2.getDrawY(LinePathGenerationTestGameTool.this.line.y2), 1.0F, 0.0F, 0.0F, 1.0F);
                  }

                  Point var9;
                  Point var10;
                  if (LinePathGenerationTestGameTool.this.path != null) {
                     for(Iterator var2x = LinePathGenerationTestGameTool.this.path.elements().iterator(); var2x.hasNext(); Screen.drawLineRGBA(var2.getDrawX(var9.x), var2.getDrawY(var9.y), var2.getDrawX(var10.x), var2.getDrawY(var10.y), 1.0F, 1.0F, 0.0F, 1.0F)) {
                        GameLinkedList.Element var3 = (GameLinkedList.Element)var2x.next();
                        Point var4 = (Point)var3.object;
                        Screen.drawRectangleLines(new Rectangle(var4.x * 32, var4.y * 32, 32, 32), var2, 0.0F, 1.0F, 1.0F, 1.0F);
                        GameLinkedList.Element var5 = var3.prev();
                        Point var6 = var5 == null ? null : (Point)var5.object;
                        GameLinkedList.Element var7 = var3.next();
                        Point var8 = var7 == null ? null : (Point)var7.object;
                        var9 = new Point(var4.x * 32 + 16, var4.y * 32 + 16);
                        var10 = new Point(var4.x * 32 + 16, var4.y * 32 + 16);
                        if (var6 != null) {
                           if (var6.x < var4.x) {
                              var9.x -= 16;
                           } else if (var6.x > var4.x) {
                              var9.x += 16;
                           }

                           if (var6.y < var4.y) {
                              var9.y -= 16;
                           } else if (var6.y > var4.y) {
                              var9.y += 16;
                           }
                        }

                        if (var8 != null) {
                           if (var8.x < var4.x) {
                              var10.x -= 16;
                           } else if (var8.x > var4.x) {
                              var10.x += 16;
                           }

                           if (var8.y < var4.y) {
                              var10.y -= 16;
                           } else if (var8.y > var4.y) {
                              var10.y += 16;
                           }
                        }
                     }
                  }

                  if (LinePathGenerationTestGameTool.this.p1 != null) {
                     Screen.drawRectangleLines(new Rectangle(LinePathGenerationTestGameTool.this.p1.x * 32, LinePathGenerationTestGameTool.this.p1.y * 32, 32, 32), var2, 0.0F, 1.0F, 0.0F, 1.0F);
                  }

                  if (LinePathGenerationTestGameTool.this.p2 != null) {
                     Screen.drawRectangleLines(new Rectangle(LinePathGenerationTestGameTool.this.p2.x * 32, LinePathGenerationTestGameTool.this.p2.y * 32, 32, 32), var2, 0.0F, 0.0F, 1.0F, 1.0F);
                  }

               }
            });
         }
      });
   }

   private void generatePath() {
      if (this.p1 != null && this.p2 != null) {
         this.line = new Line2D.Float((float)(this.p1.x * 32 + 16), (float)(this.p1.y * 32 + 16), (float)(this.p2.x * 32 + 16), (float)(this.p2.y * 32 + 16));
         this.path = new GameLinkedList();
         LinesGeneration.pathTiles(new Line2D.Float(this.p1, this.p2), true, (var1, var2) -> {
            this.path.add(var2);
         });
      }

   }

   public void isCancelled() {
      super.isCancelled();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }

   public void isCleared() {
      super.isCleared();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }
}
