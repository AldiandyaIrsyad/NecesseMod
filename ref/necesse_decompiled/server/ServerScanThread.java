package necesse.server;

import java.util.Scanner;
import necesse.engine.network.server.Server;

public class ServerScanThread extends Thread {
   private ServerWrapper serverWrapper;
   private Server server;

   public ServerScanThread(ServerWrapper var1) {
      super("Command scanner");
      this.serverWrapper = var1;
   }

   public void setServer(Server var1) {
      this.server = var1;
   }

   public void run() {
      Scanner var1 = new Scanner(System.in);

      while(true) {
         do {
            if (this.isInterrupted()) {
               var1.close();
               return;
            }
         } while(!var1.hasNextLine());

         String var2 = var1.nextLine();
         this.serverWrapper.submitCommand(this.server != null && !this.server.hasClosed() ? this.server : null, var2);
      }
   }
}
