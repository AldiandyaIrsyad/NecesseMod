package necesse.engine.util.generalTree;

public class GeneralTree {
   public int xTopAdjustment = 0;
   public int yTopAdjustment = 0;
   public int maxDepth = Integer.MAX_VALUE;
   public int levelSeparation;
   public int siblingSeparation;
   public int subtreeSeparation;

   public GeneralTree(int var1, int var2, int var3) {
      this.levelSeparation = var1;
      this.siblingSeparation = var2;
      this.subtreeSeparation = var3;
   }

   public boolean calculateNodePositions(GeneralTreeNode var1) {
      return this.calculateNodePositions(var1, 0, 0);
   }

   public boolean calculateNodePositions(GeneralTreeNode var1, int var2, int var3) {
      this.xTopAdjustment = var2;
      this.yTopAdjustment = var3;
      if (var1 != null) {
         var1.resetPos();
         this.firstWalk(var1);
         this.xTopAdjustment = var1.x - var1.prelim;
         this.yTopAdjustment = var1.y;
         return this.secondWalk(var1, 0);
      } else {
         return true;
      }
   }

   protected void firstWalk(GeneralTreeNode var1) {
      var1.modifier = 0;
      if (!var1.isLeaf() && var1.level != this.maxDepth) {
         GeneralTreeNode var3;
         GeneralTreeNode var2 = var3 = var1.getFirstChild();
         this.firstWalk(var2);

         while(var3.hasRightSibling()) {
            var3 = var3.getRightSibling();
            this.firstWalk(var3);
         }

         int var4 = (var2.prelim + var3.prelim) / 2;
         if (var1.hasLeftSibling()) {
            var1.prelim = var1.getLeftSibling().prelim + this.siblingSeparation + this.meanNodeSize(var1.getLeftSibling(), var1);
            var1.modifier = var1.prelim - var4;
            this.apportion(var1);
         } else {
            var1.prelim = var4;
         }
      } else if (var1.hasLeftSibling()) {
         var1.prelim = var1.getLeftSibling().prelim + this.siblingSeparation + this.meanNodeSize(var1.getLeftSibling(), var1);
      } else {
         var1.prelim = 0;
      }

   }

   protected boolean secondWalk(GeneralTreeNode var1, int var2) {
      boolean var3 = true;
      if (var1.level <= this.maxDepth) {
         int var4 = this.xTopAdjustment + var1.prelim + var2;
         int var5 = this.yTopAdjustment + var1.level * this.levelSeparation;
         if (this.canFitInTree(var4, var5)) {
            var1.x = var4;
            var1.y = var5;
            if (!var1.getChildren().isEmpty()) {
               var3 = this.secondWalk(var1.getFirstChild(), var2 + var1.modifier);
            }

            if (var3 && var1.hasRightSibling()) {
               var3 = this.secondWalk(var1.getRightSibling(), var2);
            }
         } else {
            var3 = false;
         }
      }

      return var3;
   }

   protected void apportion(GeneralTreeNode var1) {
      GeneralTreeNode var2 = var1.getFirstChild();
      GeneralTreeNode var3 = var2.leftNeighbour;
      int var4 = 1;
      int var5 = this.maxDepth - var1.level;

      while(var2 != null && var3 != null && var4 <= var5) {
         int var6 = 0;
         int var7 = 0;
         GeneralTreeNode var8 = var2;
         GeneralTreeNode var9 = var3;

         int var10;
         for(var10 = 0; var10 < var4; ++var10) {
            var8 = var8.parent;
            var9 = var9.parent;
            if (var9 == null) {
               return;
            }

            var7 += var8.modifier;
            var6 += var9.modifier;
         }

         var10 = var3.prelim + var6 + this.subtreeSeparation + this.meanNodeSize(var2, var3) - (var2.prelim + var7);
         if (var10 > 0) {
            int var11 = 0;

            GeneralTreeNode var12;
            for(var12 = var1; var12 != null && var12 != var9; var12 = var12.getLeftSibling()) {
               ++var11;
            }

            if (var12 == null) {
               return;
            }

            int var13 = var10 / var11;

            for(var12 = var1; var12 != var9; var12 = var12.getLeftSibling()) {
               var12.prelim += var10;
               var12.modifier += var10;
               var10 -= var13;
            }
         }

         ++var4;
         if (var2.isLeaf()) {
            var2 = this.getLeftMost(var1, var4);
         } else {
            var2 = var2.getFirstChild();
         }
      }

   }

   private GeneralTreeNode getLeftMost(GeneralTreeNode var1, int var2) {
      if (var1.level >= var2) {
         return var1;
      } else if (var1.isLeaf()) {
         return null;
      } else {
         GeneralTreeNode var3 = var1.getFirstChild();

         GeneralTreeNode var4;
         for(var4 = this.getLeftMost(var3, var2); var4 != null && var3.hasRightSibling(); var4 = this.getLeftMost(var3, var2)) {
            var3 = var3.getRightSibling();
         }

         return var4;
      }
   }

   protected int meanNodeSize(GeneralTreeNode var1, GeneralTreeNode var2) {
      int var3 = 0;
      if (var1 != null) {
         var3 += var1.nodeSize / 2;
      }

      if (var2 != null) {
         var3 += var2.nodeSize / 2;
      }

      return var3;
   }

   public boolean canFitInTree(int var1, int var2) {
      return true;
   }
}
