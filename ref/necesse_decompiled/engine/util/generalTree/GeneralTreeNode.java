package necesse.engine.util.generalTree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class GeneralTreeNode {
   public final GeneralTreeNode parent;
   public final GeneralTreeNode leftNeighbour;
   public final int level;
   public final int childIndex;
   public final int nodeSize;
   public int x;
   public int y;
   protected int prelim;
   protected int modifier;

   public GeneralTreeNode(HashMap<Integer, GeneralTreeNode> var1, GeneralTreeNode var2, int var3, int var4) {
      this.parent = var2;
      this.level = var2 == null ? 0 : var2.level + 1;
      this.childIndex = var3;
      this.nodeSize = var4;
      this.leftNeighbour = (GeneralTreeNode)var1.getOrDefault(this.level, (Object)null);
      var1.put(this.level, this);
   }

   protected void resetPos() {
      this.x = 0;
      this.y = 0;
      this.prelim = 0;
      this.modifier = 0;
      Iterator var1 = this.getChildren().iterator();

      while(var1.hasNext()) {
         GeneralTreeNode var2 = (GeneralTreeNode)var1.next();
         var2.resetPos();
      }

   }

   public abstract List<? extends GeneralTreeNode> getChildren();

   public GeneralTreeNode getFirstChild() {
      List var1 = this.getChildren();
      return var1.isEmpty() ? null : (GeneralTreeNode)var1.get(0);
   }

   public GeneralTreeNode getLeftSibling() {
      return !this.hasLeftSibling() ? null : (GeneralTreeNode)this.parent.getChildren().get(this.childIndex - 1);
   }

   public boolean hasLeftSibling() {
      return this.childIndex > 0;
   }

   public GeneralTreeNode getRightSibling() {
      return !this.hasRightSibling() ? null : (GeneralTreeNode)this.parent.getChildren().get(this.childIndex + 1);
   }

   public boolean hasRightSibling() {
      if (this.parent == null) {
         return false;
      } else {
         return this.childIndex < this.parent.getChildren().size() - 1;
      }
   }

   public GeneralTreeNode getLeftNeighbour() {
      return this.leftNeighbour;
   }

   public boolean isLeaf() {
      return this.getChildren().isEmpty();
   }
}
