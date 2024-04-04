package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceTimer;
import necesse.engine.tickManager.PerformanceTimerManager;
import necesse.engine.tickManager.PerformanceTimerUtils;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.path.PathOptions;
import necesse.entity.mobs.ai.path.TilePathFindingDrawOptions;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.components.chat.ChatMessageList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class TilePathFindGameTool extends MouseDebugGameTool {
   public Pathfinding<Point>.Process<TilePathfinding> process;
   public TilePathfinding.NodePriority nodePriority;
   public DoorMode doorMode;
   public boolean paused;
   public boolean biDirectional;
   public int speed;
   public Point from;
   public Point to;
   public HudDrawElement hudElement;
   public double iterationsBuffer;
   public PerformanceTimerManager performanceTimer;
   private MouseWheelBuffer wheelBuffer;

   public TilePathFindGameTool(DebugForm var1) {
      super(var1, "Tile path finding");
      this.nodePriority = TilePathfinding.NodePriority.TOTAL_COST;
      this.doorMode = TilePathFindGameTool.DoorMode.CAN_OPEN;
      this.speed = 2;
      this.wheelBuffer = new MouseWheelBuffer(false);
   }

   public void init() {
      this.from = null;
      this.to = null;
      this.process = null;
      this.performanceTimer = null;
      this.paused = false;
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
      this.onKeyClick(80, (var1) -> {
         this.paused = !this.paused;
         this.setKeyUsage(80, this.paused ? "Unpause" : "Pause");
         return true;
      }, this.paused ? "Unpause" : "Pause");
      this.onKeyClick(79, (var1) -> {
         DoorMode[] var2 = TilePathFindGameTool.DoorMode.values();
         this.doorMode = var2[(this.doorMode.ordinal() + 1) % var2.length];
         this.setKeyUsage(79, this.doorMode.displayName);
         this.updatePath();
         return true;
      }, this.doorMode.displayName);
      this.onKeyClick(73, (var1) -> {
         TilePathfinding.NodePriority[] var2 = TilePathfinding.NodePriority.values();
         this.nodePriority = var2[(this.nodePriority.ordinal() + 1) % var2.length];
         this.setKeyUsage(73, "Priority: " + this.nodePriority);
         this.updatePath();
         return true;
      }, "Priority: " + this.nodePriority);
      this.onKeyClick(85, (var1) -> {
         this.biDirectional = !this.biDirectional;
         this.setKeyUsage(85, "Bi-directional: " + this.biDirectional);
         this.updatePath();
         return true;
      }, "Bi-directional: " + this.biDirectional);
      this.onKeyClick(89, (var1) -> {
         if (this.performanceTimer != null) {
            PerformanceTimer var2 = (PerformanceTimer)this.performanceTimer.getCurrentRootPerformanceTimer().getPerformanceTimer("tilePathFindingTool");
            if (this.getLevel().isClient()) {
               Client var3 = this.getLevel().getClient();
               ChatMessageList var10001 = var3.chat;
               Objects.requireNonNull(var10001);
               PerformanceTimerUtils.printPerformanceTimer(var2, var10001::addMessage);
            } else {
               PerformanceTimerUtils.printPerformanceTimer(var2);
            }
         }

         return true;
      }, "Print performance");
      this.onScroll((var1) -> {
         this.wheelBuffer.add(var1);
         this.speed = GameMath.limit(this.speed + this.wheelBuffer.useAllScrollY(), 0, 5);
         this.scrollUsage = "Speed: " + (int)Math.pow(10.0, (double)this.speed);
         return true;
      }, "Speed: " + (int)Math.pow(10.0, (double)this.speed));
      this.onKeyClick(76, (var1) -> {
         if (this.process != null && !this.process.isDone()) {
            Performance.runCustomTimer(this.getLevel().tickManager(), this.performanceTimer, () -> {
               Performance.record(this.getLevel().tickManager(), "tilePathFindingTool", (Runnable)(() -> {
                  this.process.iterate(1);
               }));
            });
         }

         return true;
      }, "Iterate once");
      this.getLevel().hudManager.addElement(this.hudElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            final Object var4;
            if (TilePathFindGameTool.this.process == null) {
               var4 = () -> {
               };
            } else if (TilePathFindGameTool.this.process.getResult() == null) {
               var4 = new TilePathFindingDrawOptions(TilePathFindGameTool.this.process, var2);
            } else {
               var4 = new TilePathFindingDrawOptions(TilePathFindGameTool.this.process.getResult(), var2);
            }

            var1.add(new SortedDrawable() {
               public int getPriority() {
                  return -10000;
               }

               public void draw(TickManager var1) {
                  ((DrawOptions)var4).draw();
               }
            });
         }
      });
   }

   public boolean inputEvent(InputEvent var1) {
      return super.inputEvent(var1);
   }

   public void tick() {
      if (!this.paused && this.process != null && !this.process.isDone()) {
         int var1 = (int)Math.pow(10.0, (double)this.speed);
         this.iterationsBuffer += 0.05 * (double)var1;
         if (this.iterationsBuffer >= 1.0) {
            Performance.runCustomTimer(this.getLevel().tickManager(), this.performanceTimer, () -> {
               Performance.record(this.getLevel().tickManager(), "tilePathFindingTool", (Runnable)(() -> {
                  this.process.iterate((int)this.iterationsBuffer);
               }));
            });
            this.iterationsBuffer -= (double)((int)this.iterationsBuffer);
         }
      }

   }

   public void updatePath() {
      if (this.from != null && this.to != null) {
         PlayerMob var1 = new PlayerMob(0L, (NetworkClient)null);
         var1.setLevel(this.getLevel());
         PathOptions var2 = new PathOptions(this.nodePriority);
         PathDoorOption var3 = (PathDoorOption)this.doorMode.doorOptionGetter.apply(this.getLevel());
         TilePathfinding var4 = new TilePathfinding(this.getLevel().tickManager(), this.getLevel(), var1, (BiPredicate)null, var2, var3);
         var4.biDirectional = this.biDirectional;
         this.process = var4.getProcess(this.from, this.to, HumanMob.humanPathIterations);
         this.performanceTimer = new PerformanceTimerManager();
      } else {
         this.process = null;
         this.performanceTimer = null;
      }

      this.iterationsBuffer = 0.0;
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

   public static enum DoorMode {
      CAN_OPEN("Can open doors", (var0) -> {
         return var0.regionManager.CAN_OPEN_DOORS_OPTIONS;
      }),
      CAN_BREAK_DOWN("Can break down objects", (var0) -> {
         return var0.regionManager.CAN_BREAK_OBJECTS_OPTIONS;
      }),
      CANNOT_PASS("Cannot pass doors", (var0) -> {
         return var0.regionManager.CANNOT_PASS_DOORS_OPTIONS;
      }),
      CANNOT_OPEN("Cannot open doors", (var0) -> {
         return var0.regionManager.BASIC_DOOR_OPTIONS;
      });

      public final String displayName;
      public final Function<Level, PathDoorOption> doorOptionGetter;

      private DoorMode(String var3, Function var4) {
         this.displayName = var3;
         this.doorOptionGetter = var4;
      }

      // $FF: synthetic method
      private static DoorMode[] $values() {
         return new DoorMode[]{CAN_OPEN, CAN_BREAK_DOWN, CANNOT_PASS, CANNOT_OPEN};
      }
   }
}
