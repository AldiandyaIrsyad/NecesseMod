package necesse.engine.util.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class PathResult<T, C extends Pathfinding<T>> {
   public final C finder;
   public final T start;
   public final T target;
   public final LinkedList<Pathfinding<T>.Node> path;
   public final boolean foundTarget;
   public final ArrayList<Pathfinding<T>.Node> openNodes;
   public final ArrayList<Pathfinding<T>.Node> closedNodes;
   public final HashSet<T> invalidChecked;
   public final int iterations;
   public final int maxIterations;
   public final long nsTaken;

   public PathResult(C var1, T var2, T var3, LinkedList<Pathfinding<T>.Node> var4, boolean var5, ArrayList<Pathfinding<T>.Node> var6, ArrayList<Pathfinding<T>.Node> var7, HashSet<T> var8, int var9, int var10, long var11) {
      this.finder = var1;
      this.start = var2;
      this.target = var3;
      this.path = var4;
      this.foundTarget = var5;
      this.openNodes = var6;
      this.closedNodes = var7;
      this.invalidChecked = var8;
      this.iterations = var9;
      this.maxIterations = var10;
      this.nsTaken = var11;
   }

   public Pathfinding<T>.Node getLastPathNode() {
      return this.path.isEmpty() ? null : (Pathfinding.Node)this.path.get(this.path.size() - 1);
   }

   public T getLastPathResult() {
      return this.path.isEmpty() ? null : ((Pathfinding.Node)this.path.get(this.path.size() - 1)).item;
   }
}
