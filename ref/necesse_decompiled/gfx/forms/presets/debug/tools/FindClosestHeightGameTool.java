package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.liquidManager.ClosestHeightResult;

public class FindClosestHeightGameTool extends MouseDebugGameTool {
   public HudDrawElement hudElement;
   private Mode mode;
   private int maxSameHeightTravel;
   private int desiredHeight;
   private Point fromTile;
   private MouseWheelBuffer wheelBuffer;

   public FindClosestHeightGameTool(DebugForm var1, String var2) {
      super(var1, var2);
      this.mode = FindClosestHeightGameTool.Mode.NO_COLLISION;
      this.maxSameHeightTravel = 0;
      this.desiredHeight = 0;
      this.wheelBuffer = new MouseWheelBuffer(false);
   }

   public void init() {
      this.fromTile = null;
      this.onLeftClick((var1) -> {
         this.fromTile = new Point(this.getMouseTileX(), this.getMouseTileY());
         this.update();
         return true;
      }, "Start find");
      this.onRightClick((var1) -> {
         if (this.hudElement != null) {
            this.hudElement.remove();
         }

         return true;
      }, "Clear draw");
      this.onKeyClick(266, (var1) -> {
         this.desiredHeight = Math.min(this.desiredHeight + 1, 10);
         this.update();
         return true;
      }, "Increase desired height");
      this.onKeyClick(267, (var1) -> {
         this.desiredHeight = Math.max(this.desiredHeight - 1, -10);
         this.update();
         return true;
      }, "Decrease desired height");
      this.onKeyClick(77, (var1) -> {
         Mode[] var2 = FindClosestHeightGameTool.Mode.values();
         this.mode = var2[Math.floorMod(this.mode.ordinal() + 1, var2.length)];
         this.update();
         return true;
      }, "");
      this.onScroll((var1) -> {
         this.wheelBuffer.add(var1);
         int var2 = this.wheelBuffer.useAllScrollY();
         if (var2 != 0) {
            this.maxSameHeightTravel = Math.max(this.maxSameHeightTravel + var2, 0);
            this.update();
         }

         return true;
      }, "");
      this.update();
   }

   public void update() {
      this.setKeyUsage(77, "Mode: " + this.mode);
      this.scrollUsage = "Max same height travel: " + this.maxSameHeightTravel;
      this.setLeftUsage("Find " + this.desiredHeight + " height");
      if (this.fromTile != null) {
         Level var1 = this.getLevel();
         final ClosestHeightResult var2 = var1.liquidManager.findClosestHeightTile(this.fromTile.x, this.fromTile.y, this.desiredHeight, this.maxSameHeightTravel, (var2x) -> {
            return this.mode.validChecker.check(var1, this.fromTile, var2x);
         });
         if (this.hudElement != null) {
            this.hudElement.remove();
         }

         this.hudElement = new HudDrawElement() {
            public void addDrawables(List<SortedDrawable> var1, GameCamera var2x, PlayerMob var3) {
               final DrawOptionsList var4 = new DrawOptionsList();
               Iterator var5 = var2.closedTiles.iterator();

               Point var6;
               int var7;
               int var8;
               while(var5.hasNext()) {
                  var6 = (Point)var5.next();
                  var7 = var2x.getTileDrawX(var6.x);
                  var8 = var2x.getTileDrawY(var6.y);
                  var4.add(Screen.initQuadDraw(32, 32).color(1.0F, 0.0F, 0.0F, 0.5F).pos(var7, var8));
               }

               var5 = var2.openTiles.iterator();

               while(var5.hasNext()) {
                  var6 = (Point)var5.next();
                  var7 = var2x.getTileDrawX(var6.x);
                  var8 = var2x.getTileDrawY(var6.y);
                  var4.add(Screen.initQuadDraw(32, 32).color(0.0F, 1.0F, 0.0F, 0.5F).pos(var7, var8));
               }

               int var9 = var2x.getTileDrawX(var2.startX);
               int var10 = var2x.getTileDrawY(var2.startY);
               var4.add(Screen.initQuadDraw(32, 32).color(0.0F, 0.0F, 1.0F).pos(var9, var10));
               var9 = var2x.getTileDrawX(var2.best.x);
               var10 = var2x.getTileDrawY(var2.best.y);
               var4.add(Screen.initQuadDraw(32, 32).color(1.0F, 1.0F, 0.0F).pos(var9, var10));
               if (var2.found != null) {
                  var9 = var2x.getTileDrawX(var2.found.x);
                  var10 = var2x.getTileDrawY(var2.found.y);
                  var4.add(Screen.initQuadDraw(32, 32).color(0.0F, 1.0F, 0.0F).pos(var9, var10));
               }

               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return Integer.MIN_VALUE;
                  }

                  public void draw(TickManager var1) {
                     var4.draw();
                  }
               });
            }
         };
         var1.hudManager.addElement(this.hudElement);
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

   private static enum Mode {
      NO_COLLISION((var0, var1, var2) -> {
         return true;
      }),
      LINE_COLLISION((var0, var1, var2) -> {
         return !var0.collides((Line2D)(new Line2D.Float((float)(var1.x * 32 + 16), (float)(var1.y * 32 + 16), (float)(var2.x * 32 + 16), (float)(var2.y * 32 + 16))), (CollisionFilter)(new CollisionFilter()).mobCollision());
      }),
      LINE_WIDTH_16_COLLISION((var0, var1, var2) -> {
         return !var0.collides(new Line2D.Float((float)(var1.x * 32 + 16), (float)(var1.y * 32 + 16), (float)(var2.x * 32 + 16), (float)(var2.y * 32 + 16)), 16.0F, 2.0F, (new CollisionFilter()).mobCollision());
      }),
      LINE_WIDTH_32_COLLISION((var0, var1, var2) -> {
         return !var0.collides(new Line2D.Float((float)(var1.x * 32 + 16), (float)(var1.y * 32 + 16), (float)(var2.x * 32 + 16), (float)(var2.y * 32 + 16)), 32.0F, 2.0F, (new CollisionFilter()).mobCollision());
      });

      public final ValidChecker validChecker;

      private Mode(ValidChecker var3) {
         this.validChecker = var3;
      }

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{NO_COLLISION, LINE_COLLISION, LINE_WIDTH_16_COLLISION, LINE_WIDTH_32_COLLISION};
      }
   }

   private interface ValidChecker {
      boolean check(Level var1, Point var2, Point var3);
   }
}
