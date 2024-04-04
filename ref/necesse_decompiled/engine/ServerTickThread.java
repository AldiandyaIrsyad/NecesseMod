package necesse.engine;

import java.util.Objects;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.Server;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;

public class ServerTickThread extends Thread {
   public TickManager tickManager;
   private boolean isRunning;
   private Server server;
   private long tickTimeTrack;
   private long tickTimeAverage;
   private static GameExceptionHandler exceptionHandler = new GameExceptionHandler("server ticking");

   public ServerTickThread(Server var1, String var2, int var3) {
      super(var2);
      this.server = var1;
      this.tickManager = new TickManager(var2, var3) {
         public void update() {
            long var1 = System.nanoTime();

            try {
               if (ServerTickThread.this.server.hasClosed()) {
                  ServerTickThread.this.interrupt();
                  return;
               }

               Performance.recordConstant(this, "tickTime", (Runnable)(() -> {
                  if (ServerTickThread.this.server != null) {
                     if (this.isGameTick()) {
                        Server var10002 = ServerTickThread.this.server;
                        Objects.requireNonNull(var10002);
                        Performance.record(this, "gameTick", (Runnable)(var10002::tick));
                     }

                     Performance.record(this, "frameTick", (Runnable)(() -> {
                        ServerTickThread.this.server.frameTick(this);
                     }));
                  }

               }));
               ServerTickThread.exceptionHandler.clear(this.isGameTick());
            } catch (Exception var4) {
               ServerTickThread.exceptionHandler.submitException(this.isGameTick(), var4, () -> {
                  System.err.println("Stuck in crash loop, stopping server.");
                  GameCrashLog.printCrashLog(ServerTickThread.exceptionHandler.getSavedExceptions(), ServerTickThread.this.server.getLocalClient(), ServerTickThread.this.server, "Server", false);
                  ServerTickThread.this.server.stop(PacketDisconnect.Code.SERVER_ERROR, (var0) -> {
                     Thread.currentThread().interrupt();
                  });
               });
            } catch (Error var5) {
               ServerTickThread.exceptionHandler.submitException(this.isGameTick(), new CriticalGameException(var5), () -> {
                  System.err.println("Stuck in crash loop, stopping server.");
                  GameCrashLog.printCrashLog(ServerTickThread.exceptionHandler.getSavedExceptions(), ServerTickThread.this.server.getLocalClient(), ServerTickThread.this.server, "Server", false);
                  ServerTickThread.this.server.stop(PacketDisconnect.Code.SERVER_ERROR, (var0) -> {
                     Thread.currentThread().interrupt();
                  });
               });
            }

            ServerTickThread.this.tickTimeTrack = System.nanoTime() - var1;
         }

         public void updateSecond() {
            ServerTickThread.this.tickTimeAverage = ServerTickThread.this.tickTimeTrack / (long)Math.max(1, this.getTPS());
            ServerTickThread.this.tickTimeTrack = 0L;
         }
      };
      this.server.setTickManager(this.tickManager);
   }

   public synchronized void start() {
      this.tickManager.setMaxFPS(60);
      this.tickManager.init();
      super.start();
   }

   public void run() {
      this.isRunning = true;

      while(!this.isInterrupted()) {
         this.tickManager.tickLogic();
      }

      this.isRunning = false;
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public long getTickTimeAverage() {
      return this.tickTimeAverage;
   }
}
