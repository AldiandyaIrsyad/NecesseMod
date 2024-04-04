package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.pathfinding.PathResult;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class TilePathFindingDrawOptions implements DrawOptions {
   private Point mouseTile;
   private GameCamera camera;
   private Pathfinding<Point>.Node mouseNode;
   private TilePathfinding finder;
   private DrawOptionsList drawOptions = new DrawOptionsList();
   private SharedTextureDrawOptions quadDrawOptions = new SharedTextureDrawOptions(Screen.getQuadTexture());

   public TilePathFindingDrawOptions(PathResult<Point, TilePathfinding> var1, GameCamera var2) {
      this.finder = (TilePathfinding)var1.finder;
      this.camera = var2;
      DrawOptionsList var10000 = this.drawOptions;
      SharedTextureDrawOptions var10001 = this.quadDrawOptions;
      Objects.requireNonNull(var10001);
      var10000.add(var10001::draw);
      double var3 = (Double)Stream.concat(var1.openNodes.stream(), var1.closedNodes.stream()).map((var0) -> {
         return var0.pathCost;
      }).max(Double::compareTo).orElse(1.0);
      int var5 = 0;

      Iterator var6;
      Pathfinding.Node var7;
      float var8;
      for(var6 = var1.closedNodes.iterator(); var6.hasNext(); ++var5) {
         var7 = (Pathfinding.Node)var6.next();
         var8 = (float)(var7.pathCost / var3);
         this.addNodeDrawOptions(var7, var1, var5, var8, 0.0F, 0.0F, 0.5F, var2, this.finder.moveOffsetX, this.finder.moveOffsetY);
      }

      var5 = 0;

      for(var6 = var1.openNodes.iterator(); var6.hasNext(); ++var5) {
         var7 = (Pathfinding.Node)var6.next();
         var8 = (float)(var7.pathCost / var3);
         this.addNodeDrawOptions(var7, var1, var5, var8, 1.0F, 0.0F, 0.5F, var2, this.finder.moveOffsetX, this.finder.moveOffsetY);
      }

      var6 = var1.invalidChecked.iterator();

      while(var6.hasNext()) {
         Point var11 = (Point)var6.next();
         int var13 = var2.getTileDrawX(var11.x);
         int var9 = var2.getTileDrawY(var11.y);
         this.quadDrawOptions.addFull().size(32, 32).color(0.0F, 1.0F, 1.0F, 0.25F).pos(var13, var9);
      }

      this.drawOptions.add(TilePathfinding.getPathLineDrawOptions(var1.path, var2));
      int var10 = var2.getTileDrawX(((Point)var1.target).x);
      int var12 = var2.getTileDrawY(((Point)var1.target).y);
      this.quadDrawOptions.addFull().size(32, 32).color(0.0F, 0.0F, 0.5F).pos(var10, var12);
      Pathfinding.Node var14 = var1.getLastPathNode();
      if (var14 != null) {
         this.addNodeDrawOptions(var14, (PathResult)var1, 0, 0.0F, 0.0F, 1.0F, 0.5F, var2, this.finder.moveOffsetX, this.finder.moveOffsetY);
      }

      this.drawOptions.add(() -> {
         Screen.addTooltip((new StringTooltips("Path iterations: " + var1.iterations)).add("Node priority: " + ((TilePathfinding)var1.finder).options.nodePriority).add("Time taken: " + GameUtils.getTimeStringNano(var1.nsTaken)).add("Found target: " + var1.foundTarget), TooltipLocation.PLAYER);
      });
   }

   public TilePathFindingDrawOptions(Pathfinding<Point>.Process<TilePathfinding> var1, GameCamera var2) {
      this.finder = (TilePathfinding)var1.finder;
      this.camera = var2;
      synchronized(var1.nodeLock) {
         double var4 = (Double)Stream.concat(var1.streamOpenNodes(), var1.streamClosedNodes()).map((var0) -> {
            return var0.pathCost;
         }).max(Double::compareTo).orElse(1.0);
         int var6 = 0;

         Iterator var7;
         Pathfinding.Node var8;
         float var9;
         for(var7 = var1.getClosedNodes().iterator(); var7.hasNext(); ++var6) {
            var8 = (Pathfinding.Node)var7.next();
            var9 = (float)(var8.pathCost / var4);
            this.addNodeDrawOptions(var8, var1, var6, var9, 0.0F, 0.0F, 0.5F, var2, this.finder.moveOffsetX, this.finder.moveOffsetY);
         }

         var6 = 0;

         for(var7 = var1.getOpenNodes().iterator(); var7.hasNext(); ++var6) {
            var8 = (Pathfinding.Node)var7.next();
            var9 = (float)(var8.pathCost / var4);
            this.addNodeDrawOptions(var8, var1, var6, var9, 1.0F, 0.0F, 0.5F, var2, this.finder.moveOffsetX, this.finder.moveOffsetY);
         }

         var7 = var1.getInvalidChecked().iterator();

         while(var7.hasNext()) {
            Point var14 = (Point)var7.next();
            int var16 = var2.getTileDrawX(var14.x);
            int var10 = var2.getTileDrawY(var14.y);
            this.quadDrawOptions.addFull().size(32, 32).color(0.0F, 1.0F, 1.0F, 0.25F).pos(var16, var10);
         }

         int var13 = var2.getTileDrawX(((Point)var1.target).x);
         int var15 = var2.getTileDrawY(((Point)var1.target).y);
         this.quadDrawOptions.addFull().size(32, 32).color(0.0F, 0.0F, 0.5F).pos(var13, var15);
         this.addTileDrawOptions((Point)var1.target, 0, 0.0F, 0.0F, 1.0F, 0.5F, var2);
         this.drawOptions.add(() -> {
            Screen.addTooltip((new StringTooltips("Path iterations: " + var1.getIterations())).add("Node priority: " + this.finder.options.nodePriority).add("Time taken: " + GameUtils.getTimeStringNano(var1.getTime())), TooltipLocation.PLAYER);
         });
      }
   }

   public void draw() {
      this.mouseTile = new Point(this.camera.getMouseLevelTilePosX(), this.camera.getMouseLevelTilePosY());
      this.quadDrawOptions.draw();
      this.drawOptions.draw();
      if (this.mouseNode != null) {
         LinkedList var1 = this.finder.constructPath(this.mouseNode);
         FinalPath var2 = new FinalPath(TilePathfinding.reducePathPoints(this.finder, var1));
         var2.drawPath((Mob)null, this.camera);
         Screen.addTooltip((new StringTooltips("Path length: " + var2.getFullLength())).add("Time for speed 35: " + Entity.getTravelTimeMillis(35.0F, var2.getCurrentLength())), TooltipLocation.PLAYER);
      }

   }

   public boolean isMouseOverTile(int var1, int var2) {
      return this.mouseTile != null && this.mouseTile.x == var1 && this.mouseTile.y == var2;
   }

   private void addNodeDrawOptions(Pathfinding<Point>.Node var1, Pathfinding<Point>.Process<TilePathfinding> var2, int var3, float var4, float var5, float var6, float var7, GameCamera var8, int var9, int var10) {
      this.addNodeDrawOptions(var1, (TilePathfinding)var2.finder, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   private void addNodeDrawOptions(Pathfinding<Point>.Node var1, PathResult<Point, TilePathfinding> var2, int var3, float var4, float var5, float var6, float var7, GameCamera var8, int var9, int var10) {
      this.addNodeDrawOptions(var1, (TilePathfinding)var2.finder, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   private void addNodeDrawOptions(Pathfinding<Point>.Node var1, TilePathfinding var2, int var3, float var4, float var5, float var6, float var7, GameCamera var8, int var9, int var10) {
      this.addNodeDrawOptions(var1, var2.options, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   private void addNodeDrawOptions(Pathfinding<Point>.Node var1, PathOptions var2, int var3, float var4, float var5, float var6, float var7, GameCamera var8, int var9, int var10) {
      this.addNodeDrawOptions(var1, var2.nodePriority, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   private void addNodeDrawOptions(Pathfinding<Point>.Node var1, TilePathfinding.NodePriority var2, int var3, float var4, float var5, float var6, float var7, GameCamera var8, int var9, int var10) {
      int var11 = var8.getTileDrawX(((Point)var1.item).x);
      int var12 = var8.getTileDrawY(((Point)var1.item).y);
      if (var11 > -32 && var12 > -32 && var11 < var8.getWidth() && var12 < var8.getHeight()) {
         this.quadDrawOptions.addFull().size(32, 32).color(var4, var5, var6, var7).pos(var11, var12);
         if (var1.cameFrom != null) {
            int var13 = var8.getTileDrawX(((Point)var1.cameFrom.item).x);
            int var14 = var8.getTileDrawY(((Point)var1.cameFrom.item).y);
            this.drawOptions.add(() -> {
               Screen.drawLineRGBA(var11 + var9, var12 + var10, var13 + var9, var14 + var10, 1.0F, 1.0F, 1.0F, 0.5F);
            });
         }

         FontOptions var15 = (new FontOptions(12)).alphaf(0.8F);
         this.drawOptions.add(() -> {
            FontManager.bit.drawString((float)var11, (float)var12, "" + var3, var15);
            if (this.isMouseOverTile(((Point)var1.item).x, ((Point)var1.item).y)) {
               this.mouseNode = var1;
               Screen.addTooltip((new StringTooltips("Path cost: " + GameMath.toDecimals(var1.pathCost, 1))).add("Node cost: " + GameMath.toDecimals(var1.nodeCost, 1)).add("Heuristic cost: " + GameMath.toDecimals(var1.heuristicCost, 1)).add("Total cost: " + var2.cost.getCost(var1)).add("Path count: " + var1.pathCount), TooltipLocation.PLAYER);
            }

         });
      }

   }

   private void addTileDrawOptions(Point var1, int var2, float var3, float var4, float var5, float var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var1.x);
      int var9 = var7.getTileDrawY(var1.y);
      if (var8 > -32 && var9 > -32 && var8 < var7.getWidth() && var9 < var7.getHeight()) {
         this.quadDrawOptions.addFull().size(32, 32).color(var3, var4, var5, var6).pos(var8, var9);
         FontOptions var10 = (new FontOptions(12)).alphaf(0.8F);
         this.drawOptions.add(() -> {
            FontManager.bit.drawString((float)var8, (float)var9, "" + var2, var10);
         });
      }

   }
}
