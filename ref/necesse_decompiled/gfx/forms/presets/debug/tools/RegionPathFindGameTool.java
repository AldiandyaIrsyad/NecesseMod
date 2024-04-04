package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.path.RegionPathfinding;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.regionSystem.SemiRegion;

public class RegionPathFindGameTool extends MouseDebugGameTool {
   public PathResult<SemiRegion, RegionPathfinding> result;
   public boolean cacheResult = false;
   public double pathTileLength;
   public Point from;
   public Point to;
   public TilePathFindGameTool.DoorMode doorMode;
   public HudDrawElement hudElement;

   public RegionPathFindGameTool(DebugForm var1) {
      super(var1, "Region path finding");
      this.doorMode = TilePathFindGameTool.DoorMode.CAN_OPEN;
   }

   public void init() {
      this.onLeftClick((var1) -> {
         this.from = new Point(this.getMouseTileX(), this.getMouseTileY());
         this.updatePath();
         return true;
      }, "Select start tile");
      this.onRightClick((var1) -> {
         this.to = new Point(this.getMouseTileX(), this.getMouseTileY());
         this.updatePath();
         return true;
      }, "Select target tile");
      this.onScroll((var1) -> {
         TilePathFindGameTool.DoorMode[] var2 = TilePathFindGameTool.DoorMode.values();
         int var3 = Math.floorMod(this.doorMode.ordinal() + (var1.getMouseWheelY() < 0.0 ? -1 : 1), var2.length);
         this.doorMode = var2[var3];
         this.updateScrollUsage();
         this.updatePath();
         return true;
      }, "");
      this.updateScrollUsage();
      this.onKeyClick(67, (var1) -> {
         ((PathDoorOption)this.doorMode.doorOptionGetter.apply(this.getLevel())).invalidateCache();
         this.updatePath();
         return true;
      }, "Reset cache");
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1) {
                  if (RegionPathFindGameTool.this.from != null) {
                     HUD.tileBoundOptions(var2, new Color(50, 50, 200), false, RegionPathFindGameTool.this.from.x, RegionPathFindGameTool.this.from.y, RegionPathFindGameTool.this.from.x, RegionPathFindGameTool.this.from.y).draw();
                  }

                  if (RegionPathFindGameTool.this.to != null) {
                     HUD.tileBoundOptions(var2, new Color(50, 200, 50), false, RegionPathFindGameTool.this.to.x, RegionPathFindGameTool.this.to.y, RegionPathFindGameTool.this.to.x, RegionPathFindGameTool.this.to.y).draw();
                  }

                  if (RegionPathFindGameTool.this.result != null) {
                     Iterator var2x = RegionPathFindGameTool.this.result.closedNodes.iterator();

                     Pathfinding.Node var3;
                     Point var4;
                     while(var2x.hasNext()) {
                        var3 = (Pathfinding.Node)var2x.next();
                        var4 = ((SemiRegion)var3.item).getLevelTile(((SemiRegion)var3.item).getAverageCell());
                        Screen.drawCircle(var2.getTileDrawX(var4.x) + 16, var2.getTileDrawY(var4.y) + 16, 8, 10, 1.0F, 0.0F, 0.0F, 1.0F, true);
                     }

                     var2x = RegionPathFindGameTool.this.result.openNodes.iterator();

                     while(var2x.hasNext()) {
                        var3 = (Pathfinding.Node)var2x.next();
                        var4 = ((SemiRegion)var3.item).getLevelTile(((SemiRegion)var3.item).getAverageCell());
                        Screen.drawCircle(var2.getTileDrawX(var4.x) + 16, var2.getTileDrawY(var4.y) + 16, 8, 10, 0.0F, 1.0F, 0.0F, 1.0F, true);
                     }

                     SemiRegion var14 = null;
                     int var15 = 0;

                     SemiRegion var6;
                     for(Iterator var16 = RegionPathFindGameTool.this.result.path.iterator(); var16.hasNext(); var14 = var6) {
                        Pathfinding.Node var5 = (Pathfinding.Node)var16.next();
                        var6 = (SemiRegion)var5.item;
                        if (var14 != null) {
                           Color var7 = Color.getHSBColor((float)var15 / (float)RegionPathFindGameTool.this.result.path.size(), 1.0F, 1.0F);
                           Point var8 = var14.getLevelTile(var14.getAverageCell());
                           Point var9 = var6.getLevelTile(var6.getAverageCell());
                           int var10 = var2.getTileDrawX(var8.x) + 16;
                           int var11 = var2.getTileDrawY(var8.y) + 16;
                           int var12 = var2.getTileDrawX(var9.x) + 16;
                           int var13 = var2.getTileDrawY(var9.y) + 16;
                           Screen.drawLineRGBA(var10, var11, var12, var13, (float)var7.getRed() / 255.0F, (float)var7.getGreen() / 255.0F, (float)var7.getBlue() / 255.0F, 1.0F);
                        }

                        ++var15;
                     }

                     StringTooltips var17 = (new StringTooltips()).add("Found: " + RegionPathFindGameTool.this.result.foundTarget).add("Iterations: " + RegionPathFindGameTool.this.result.iterations).add("Time: " + GameUtils.getTimeStringNano(RegionPathFindGameTool.this.result.nsTaken)).add("Tile length: " + GameMath.toDecimals(RegionPathFindGameTool.this.pathTileLength, 2)).add("Cache result: " + RegionPathFindGameTool.this.cacheResult);
                     Screen.addTooltip(var17, TooltipLocation.PLAYER);
                  }

               }
            });
         }
      });
   }

   public void updateScrollUsage() {
      this.scrollUsage = this.doorMode.displayName;
   }

   public void updatePath() {
      if (this.from != null && this.to != null) {
         PathDoorOption var1 = (PathDoorOption)this.doorMode.doorOptionGetter.apply(this.getLevel());
         this.result = RegionPathfinding.findMoveToTile(this.getLevel(), this.from.x, this.from.y, this.to.x, this.to.y, var1, (var0, var1x) -> {
            return var0.getRegionID() == var1x.getRegionID();
         }, 1000);
         this.cacheResult = var1.canMoveToTile(this.from.x, this.from.y, this.to.x, this.to.y, false);
         this.pathTileLength = RegionPathfinding.estimatePathTileLength(this.result.path);
      } else {
         this.result = null;
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
