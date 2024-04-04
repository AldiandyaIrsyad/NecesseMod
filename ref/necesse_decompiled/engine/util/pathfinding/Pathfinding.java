package necesse.engine.util.pathfinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashProxyLinkedList;

public abstract class Pathfinding<T> {
   public boolean useBestOfConnected = false;
   public boolean biDirectional = false;

   public Pathfinding() {
   }

   public <C extends Pathfinding<T>> Pathfinding<T>.Process<C> getProcess(T var1, T var2, int var3) {
      return new Process(this, var1, var2, var3);
   }

   public <C extends Pathfinding<T>> PathResult<T, C> findPath(T var1, T var2, int var3) {
      Process var4 = this.getProcess(var1, var2, var3);
      return var4.findPath();
   }

   protected abstract boolean isAtTarget(T var1, T var2);

   protected boolean handleNewNode(T var1, Pathfinding<T>.Node var2, HashProxyLinkedList<Pathfinding<T>.Node, T> var3, HashProxyLinkedList<Pathfinding<T>.Node, T> var4) {
      double var5 = var2.pathCost + this.getNodePathCost(var1, var2.item);
      boolean var7 = false;
      Node var8 = (Node)var4.getObject(var1);
      if (var8 != null) {
         if (var8.pathCost > var5) {
            var8.setCameFrom(var2, var5);
         }

         var7 = true;
      }

      Node var9 = (Node)var3.getObject(var1);
      if (var9 != null) {
         if (var9.pathCost > var5) {
            var9.setCameFrom(var2, var5);
         }

         var7 = true;
      }

      return var7;
   }

   protected abstract void handleConnectedNodes(Pathfinding<T>.Node var1, HashProxyLinkedList<Pathfinding<T>.Node, T> var2, HashProxyLinkedList<Pathfinding<T>.Node, T> var3, HashSet<T> var4, Function<T, Boolean> var5, BiConsumer<Pathfinding<T>.Node, Pathfinding<T>.Node> var6, Runnable var7);

   public LinkedList<Pathfinding<T>.Node> constructPath(Pathfinding<T>.Node var1) {
      LinkedList var2 = new LinkedList();
      var2.add(var1);

      while(var1.cameFrom != null) {
         var2.addFirst(var1.cameFrom);
         var1 = var1.cameFrom;
      }

      return var2;
   }

   protected Pathfinding<T>.Node removeAndReturnNextNode(HashProxyLinkedList<Pathfinding<T>.Node, T> var1) {
      GameLinkedList.Element var2 = (GameLinkedList.Element)var1.streamElements().min(Comparator.comparingDouble((var1x) -> {
         return this.getNodeComparable((Node)var1x.object);
      })).orElse((Object)null);
      if (var2 == null) {
         return null;
      } else {
         var2.remove();
         return (Node)var2.object;
      }
   }

   protected double getNodeComparable(Pathfinding<T>.Node var1) {
      return var1.heuristicCost + var1.pathCost;
   }

   protected abstract double getNodeHeuristicCost(T var1, T var2);

   protected abstract double getNodeCost(T var1);

   protected abstract double getNodePathCost(T var1, T var2);

   public class Process<C extends Pathfinding<T>> {
      public final C finder;
      public final T from;
      public final T target;
      public final int maxIterations;
      private final HashProxyLinkedList<Pathfinding<T>.Node, T> openNodes = new HashProxyLinkedList<Pathfinding<T>.Node, T>((var0) -> {
         return var0.item;
      }) {
         public void onAdded(GameLinkedList<Pathfinding<T>.Node>.Element var1) {
            super.onAdded(var1);
            if (((Node)var1.object).reverseDirection) {
               Process.this.openBiDirectionalNodes.add((Node)var1.object);
            }

         }

         public void onRemoved(GameLinkedList<Pathfinding<T>.Node>.Element var1) {
            super.onRemoved(var1);
            if (((Node)var1.object).reverseDirection && !Process.this.openBiDirectionalNodes.remove(((Node)var1.object).item)) {
            }

         }
      };
      private final HashProxyLinkedList<Pathfinding<T>.Node, T> openBiDirectionalNodes = new HashProxyLinkedList((var0) -> {
         return var0.item;
      });
      private final HashProxyLinkedList<Pathfinding<T>.Node, T> closedNodes = new HashProxyLinkedList((var0) -> {
         return var0.item;
      });
      private final HashSet<T> invalidChecked = new HashSet();
      private int iterations;
      private long time;
      private PathResult<T, C> result;
      public final Object nodeLock = new Object();

      public Process(C var2, T var3, T var4, int var5) {
         this.finder = var2;
         this.from = var3;
         this.target = var4;
         this.maxIterations = var5;
         long var6 = System.nanoTime();
         if (var3 != null && var4 != null) {
            Node var8 = Pathfinding.this.new Node(var3, var4, (Node)null, false);
            this.openNodes.add(var8);
            if (Pathfinding.this.biDirectional) {
               this.openNodes.add(Pathfinding.this.new Node(var4, var3, (Node)null, true));
            }

            if (Pathfinding.this.isAtTarget(var3, var4)) {
               this.result = new PathResult(var2, var3, var4, Pathfinding.this.constructPath(var8), true, new ArrayList(this.openNodes), new ArrayList(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - var6);
            }
         } else {
            this.result = new PathResult(var2, var3, var4, new LinkedList(), false, new ArrayList(this.openNodes), new ArrayList(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - var6);
         }

      }

      public boolean iterate(int var1) {
         if (this.result != null) {
            return true;
         } else {
            synchronized(this.nodeLock) {
               AtomicBoolean var3 = new AtomicBoolean();
               long var4 = System.nanoTime();
               boolean var6 = false;

               for(int var7 = 0; var7 < var1; ++var7) {
                  if (this.openNodes.isEmpty() || this.iterations >= this.maxIterations || this.result != null || Pathfinding.this.biDirectional && this.openBiDirectionalNodes.isEmpty()) {
                     var6 = true;
                     break;
                  }

                  ++this.iterations;
                  Node var8 = Pathfinding.this.removeAndReturnNextNode(this.openNodes);
                  this.closedNodes.add(var8);
                  LinkedList var9 = new LinkedList();
                  Pathfinding.this.handleConnectedNodes(var8, this.openNodes, this.closedNodes, this.invalidChecked, (var3x) -> {
                     this.invalidChecked.remove(var3x);
                     if (Pathfinding.this.handleNewNode(var3x, var8, this.openNodes, this.closedNodes)) {
                        return false;
                     } else {
                        Node var4;
                        if (var8.reverseDirection) {
                           var4 = Pathfinding.this.new Node(var3x, this.from, var8, true);
                        } else {
                           var4 = Pathfinding.this.new Node(var3x, this.target, var8, false);
                        }

                        var8.goesTo.add(var4);
                        if (Pathfinding.this.isAtTarget(var4.item, this.target)) {
                           var9.add(var4);
                           return !Pathfinding.this.useBestOfConnected;
                        } else {
                           this.openNodes.add(var4);
                           return false;
                        }
                     }
                  }, (var3x, var4x) -> {
                     if (var3x.reverseDirection != var4x.reverseDirection) {
                        Node var5 = var3x.reverseDirection ? var4x : var3x;

                        Node var7;
                        for(Node var6 = var3x.reverseDirection ? var3x : var4x; var6 != null; var6 = var7) {
                           var7 = var6.cameFrom;
                           var6.cameFrom = var5;
                           var5 = var6;
                        }

                        this.result = new PathResult(this.finder, this.from, this.target, Pathfinding.this.constructPath(var5), true, new ArrayList(this.openNodes), new ArrayList(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - var4);
                     }

                  }, () -> {
                     var3.set(true);
                  });
                  if (!var9.isEmpty()) {
                     Node var10 = (Node)var9.stream().min(Comparator.comparingDouble(Pathfinding.this::getNodeComparable)).orElse((Object)null);
                     this.result = new PathResult(this.finder, this.from, this.target, Pathfinding.this.constructPath(var10), true, new ArrayList(this.openNodes), new ArrayList(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - var4);
                     break;
                  }

                  if (var3.get()) {
                     break;
                  }
               }

               if (this.result == null && (var6 || var1 >= this.maxIterations || var3.get())) {
                  Node var13 = (Node)Stream.concat(this.openNodes.stream(), this.closedNodes.stream()).min(Comparator.comparingDouble((var0) -> {
                     return var0.heuristicCost;
                  })).orElse((Object)null);
                  if (var13 == null) {
                     this.result = new PathResult(this.finder, this.from, this.target, new LinkedList(), false, new ArrayList(this.openNodes), new ArrayList(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - var4);
                  } else {
                     this.result = new PathResult(this.finder, this.from, this.target, Pathfinding.this.constructPath(var13), false, new ArrayList(this.openNodes), new ArrayList(this.closedNodes), this.invalidChecked, this.iterations, this.maxIterations, this.time + System.nanoTime() - var4);
                  }
               }

               this.time += System.nanoTime() - var4;
               return this.result != null;
            }
         }
      }

      public PathResult<T, C> findPath() {
         this.iterate(this.maxIterations);
         return this.result;
      }

      public boolean isDone() {
         return this.result != null;
      }

      public PathResult<T, C> getResult() {
         return this.result;
      }

      public Iterable<Pathfinding<T>.Node> getOpenNodes() {
         return this.openNodes;
      }

      public Iterable<Pathfinding<T>.Node> getClosedNodes() {
         return this.closedNodes;
      }

      public Iterable<T> getInvalidChecked() {
         return this.invalidChecked;
      }

      public Stream<Pathfinding<T>.Node> streamOpenNodes() {
         return this.openNodes.stream();
      }

      public Stream<Pathfinding<T>.Node> streamClosedNodes() {
         return this.closedNodes.stream();
      }

      public Stream<T> streamInvalidChecked() {
         return this.invalidChecked.stream();
      }

      public int getIterations() {
         return this.iterations;
      }

      public long getTime() {
         return this.time;
      }
   }

   public class Node {
      public final T item;
      public final boolean reverseDirection;
      public Pathfinding<T>.Node cameFrom;
      public LinkedList<Pathfinding<T>.Node> goesTo;
      public double heuristicCost;
      public double nodeCost;
      public double pathCost;
      public int pathCount;

      public Node(T var2, T var3, Pathfinding<T>.Node var4, boolean var5) {
         this.item = var2;
         this.reverseDirection = var5;
         this.setCameFrom(var4);
         this.goesTo = new LinkedList();
         this.heuristicCost = Pathfinding.this.getNodeHeuristicCost(var2, var3);
      }

      public void setCameFrom(Pathfinding<T>.Node var1, Double var2) {
         this.cameFrom = var1;
         if (var1 != null) {
            this.pathCost = var2 != null ? var2 : Pathfinding.this.getNodePathCost(this.item, var1.item) + var1.pathCost;
            this.pathCount = var1.pathCount + 1;
            this.nodeCost = Pathfinding.this.getNodeCost(this.item) + var1.nodeCost;
         } else {
            this.pathCost = 0.0;
            this.pathCount = 0;
            this.nodeCost = Pathfinding.this.getNodeCost(this.item);
         }

      }

      public void setCameFrom(Pathfinding<T>.Node var1) {
         this.setCameFrom(var1, (Double)null);
      }
   }
}
