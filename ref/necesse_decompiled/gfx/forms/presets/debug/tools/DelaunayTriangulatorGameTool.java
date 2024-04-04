package necesse.gfx.forms.presets.debug.tools;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.util.voronoi.DelaunayTriangulator;
import necesse.engine.util.voronoi.TriangleData;
import necesse.engine.util.voronoi.TriangleLine;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.level.maps.hudManager.HudDrawElement;

public class DelaunayTriangulatorGameTool extends MouseDebugGameTool {
   public ControlForm controlForm;
   public HudDrawElement hudElement;
   public ArrayList<Point2D.Float> points = new ArrayList();
   public ArrayList<TriangleData> triangles = new ArrayList();
   public ArrayList<TriangleLine> voronoiLines = new ArrayList();

   public DelaunayTriangulatorGameTool(DebugForm var1) {
      super(var1, "DelaunayTriangulator");
   }

   public void init() {
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            final DrawOptionsList var4 = new DrawOptionsList();
            Iterator var5;
            if (DelaunayTriangulatorGameTool.this.triangles != null && DelaunayTriangulatorGameTool.this.controlForm.showTriangles.checked) {
               var5 = DelaunayTriangulatorGameTool.this.triangles.iterator();

               while(var5.hasNext()) {
                  TriangleData var6 = (TriangleData)var5.next();
                  var4.add(var6.getDrawOptions(var2));
               }
            }

            if (DelaunayTriangulatorGameTool.this.voronoiLines != null && DelaunayTriangulatorGameTool.this.controlForm.showVoronoi.checked) {
               var5 = DelaunayTriangulatorGameTool.this.voronoiLines.iterator();

               while(var5.hasNext()) {
                  TriangleLine var9 = (TriangleLine)var5.next();
                  var4.add(var9.getDrawOptions(var2));
               }
            }

            if (DelaunayTriangulatorGameTool.this.controlForm.showPoints.checked) {
               var5 = DelaunayTriangulatorGameTool.this.points.iterator();

               while(var5.hasNext()) {
                  Point2D.Float var10 = (Point2D.Float)var5.next();
                  int var7 = var2.getDrawX(var10.x);
                  int var8 = var2.getDrawY(var10.y);
                  var4.add(Screen.initQuadDraw(4, 4).color(1.0F, 0.0F, 0.0F).posMiddle(var7, var8));
               }
            }

            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1) {
                  var4.draw();
               }
            });
         }
      });
      this.updateInput();
      if (this.controlForm != null) {
         this.controlForm.invalidate();
      }

      if (GlobalData.getCurrentState() instanceof MainGame) {
         this.controlForm = new ControlForm(200);
         ((MainGame)GlobalData.getCurrentState()).formManager.addSidebar(this.controlForm);
      }

   }

   public void updateInput() {
      this.onLeftClick((var1) -> {
         this.points.add(new Point2D.Float((float)this.getMouseX(), (float)this.getMouseY()));
         this.triangles = DelaunayTriangulator.compute(this.points, false, this.voronoiLines = new ArrayList());
         return true;
      }, "Add point");
      this.onRightClick((var1) -> {
         int var2 = -1;
         double var3 = 0.0;

         for(int var5 = 0; var5 < this.points.size(); ++var5) {
            Point2D.Float var6 = (Point2D.Float)this.points.get(var5);
            double var7 = var6.distance((double)this.getMouseX(), (double)this.getMouseY());
            if (var2 == -1 || var7 < var3) {
               var2 = var5;
               var3 = var7;
            }
         }

         if (var2 != -1) {
            this.points.remove(var2);
            this.triangles = DelaunayTriangulator.compute(this.points, false, this.voronoiLines = new ArrayList());
         }

         return true;
      }, "Remove point");
      this.onKeyClick(84, (var1) -> {
         long var2 = System.currentTimeMillis();
         this.triangles = DelaunayTriangulator.compute(this.points, false, this.voronoiLines = new ArrayList());
         System.out.println("Triangles: " + this.triangles.size() + ", Voronoi: " + this.voronoiLines.size() + " took " + GameUtils.getTimeStringMillis(System.currentTimeMillis() - var2));
         return true;
      }, "Triangulate");
   }

   public void isCancelled() {
      super.isCancelled();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      if (this.controlForm != null) {
         this.controlForm.invalidate();
      }

   }

   public void isCleared() {
      super.isCleared();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      if (this.controlForm != null) {
         this.controlForm.invalidate();
      }

   }

   protected static class ControlForm extends SidebarForm {
      private boolean isValid = true;
      public FormCheckBox showPoints;
      public FormCheckBox showTriangles;
      public FormCheckBox showVoronoi;

      public ControlForm(int var1) {
         super("DTControl", var1, 100);
         FormFlow var2 = new FormFlow(10);
         this.showPoints = (FormCheckBox)this.addComponent((FormCheckBox)var2.nextY(new FormCheckBox("Show points", 5, 0, this.getWidth() - 10, true), 5));
         this.showTriangles = (FormCheckBox)this.addComponent((FormCheckBox)var2.nextY(new FormCheckBox("Show triangles", 5, 0, this.getWidth() - 10, true), 5));
         this.showVoronoi = (FormCheckBox)this.addComponent((FormCheckBox)var2.nextY(new FormCheckBox("Show voronoi", 5, 0, this.getWidth() - 10, true), 5));
         this.showPoints.handleClicksIfNoEventHandlers = true;
         this.showTriangles.handleClicksIfNoEventHandlers = true;
         this.showVoronoi.handleClicksIfNoEventHandlers = true;
         this.setHeight(var2.next() + 5);
      }

      public boolean isValid(Client var1) {
         return this.isValid;
      }

      public void invalidate() {
         this.isValid = false;
      }
   }
}
