package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class ZoneSelectorDebugGameTool extends MouseDebugGameTool {
   private LevelSelectTilesGameTool addAreaTool = new LevelSelectTilesGameTool(-100) {
      public Level getLevel() {
         return ZoneSelectorDebugGameTool.this.getLevel();
      }

      public int getMouseX() {
         return ZoneSelectorDebugGameTool.this.getMouseX();
      }

      public int getMouseY() {
         return ZoneSelectorDebugGameTool.this.getMouseY();
      }

      public void onTileSelection(int var1, int var2, int var3, int var4) {
         for(int var5 = var1; var5 <= var3; ++var5) {
            for(int var6 = var2; var6 <= var4; ++var6) {
               ZoneSelectorDebugGameTool.this.zoning.addTile(var5, var6);
            }
         }

      }

      public void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6) {
         Rectangle var7 = new Rectangle(var3 * 32, var4 * 32, (var5 - var3 + 1) * 32, (var6 - var4 + 1) * 32);
         Zoning.getRectangleDrawOptions(var7, new Color(0, 255, 0, 170), new Color(0, 255, 0, 100), var1).draw();
      }

      public GameTooltips getTooltips() {
         return ZoneSelectorDebugGameTool.this.getTooltips();
      }
   };
   private LevelSelectTilesGameTool removeAreaTool = new LevelSelectTilesGameTool(-99) {
      public Level getLevel() {
         return ZoneSelectorDebugGameTool.this.getLevel();
      }

      public int getMouseX() {
         return ZoneSelectorDebugGameTool.this.getMouseX();
      }

      public int getMouseY() {
         return ZoneSelectorDebugGameTool.this.getMouseY();
      }

      public void onTileSelection(int var1, int var2, int var3, int var4) {
         for(int var5 = var1; var5 <= var3; ++var5) {
            for(int var6 = var2; var6 <= var4; ++var6) {
               ZoneSelectorDebugGameTool.this.zoning.removeTile(var5, var6);
            }
         }

      }

      public void drawTileSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6) {
         Rectangle var7 = new Rectangle(var3 * 32, var4 * 32, (var5 - var3 + 1) * 32, (var6 - var4 + 1) * 32);
         Zoning.getRectangleDrawOptions(var7, new Color(255, 0, 0, 170), new Color(255, 0, 0, 100), var1).draw();
      }

      public GameTooltips getTooltips() {
         return ZoneSelectorDebugGameTool.this.getTooltips();
      }
   };
   private HudDrawElement hudElement;
   private Zoning zoning = new Zoning();
   private boolean showRectangles;

   public ZoneSelectorDebugGameTool(DebugForm var1, String var2) {
      super(var1, var2);
   }

   public void init() {
      this.addAreaTool.init();
      this.removeAreaTool.init();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            final SharedTextureDrawOptions var4 = new SharedTextureDrawOptions(Screen.getQuadTexture());
            if (ZoneSelectorDebugGameTool.this.showRectangles) {
               float var5 = 0.0F;

               for(Iterator var6 = ZoneSelectorDebugGameTool.this.zoning.getTileRectangles().iterator(); var6.hasNext(); var5 = (var5 + 0.1F) % 1.0F) {
                  Rectangle var7 = (Rectangle)var6.next();
                  Color var8 = Color.getHSBColor(var5, 1.0F, 1.0F);
                  Color var9 = new Color(var8.getRed(), var8.getGreen(), var8.getBlue(), 170);
                  Color var10 = new Color(var8.getRed(), var8.getGreen(), var8.getBlue(), 100);
                  Zoning.addRectangleDrawOptions(var4, new Rectangle(var7.x * 32, var7.y * 32, var7.width * 32, var7.height * 32), var9, var10, var2);
               }
            } else {
               Iterator var11 = ZoneSelectorDebugGameTool.this.zoning.getTiles().iterator();

               while(var11.hasNext()) {
                  Point var12 = (Point)var11.next();
                  boolean[] var13 = new boolean[Level.adjacentGetters.length];

                  for(int var14 = 0; var14 < var13.length; ++var14) {
                     Point var15 = Level.adjacentGetters[var14];
                     var13[var14] = ZoneSelectorDebugGameTool.this.zoning.containsTile(var12.x + var15.x, var12.y + var15.y);
                  }

                  Zoning.addDrawOptions(var4, var12, var13, new Color(0, 0, 255, 170), new Color(0, 0, 255, 100), var2);
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
      this.onKeyClick(71, (var1) -> {
         this.showRectangles = !this.showRectangles;
         return true;
      }, "Toggle rectangles");
      this.setLeftUsage("Add area");
      this.setRightUsage("Remove area");
   }

   public boolean inputEvent(InputEvent var1) {
      if (this.addAreaTool.inputEvent(var1)) {
         return true;
      } else {
         return this.removeAreaTool.inputEvent(var1) ? true : super.inputEvent(var1);
      }
   }

   public void isCancelled() {
      this.addAreaTool.isCancelled();
      this.removeAreaTool.isCancelled();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }

   public void isCleared() {
      this.addAreaTool.isCleared();
      this.removeAreaTool.isCleared();
      if (this.hudElement != null) {
         this.hudElement.remove();
      }

   }
}
