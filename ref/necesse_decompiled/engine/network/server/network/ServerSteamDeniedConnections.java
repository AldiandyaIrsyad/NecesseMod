package necesse.engine.network.server.network;

import com.codedisaster.steamworks.SteamID;
import java.util.HashMap;
import java.util.function.Consumer;
import necesse.engine.util.GameLinkedList;

public class ServerSteamDeniedConnections {
   private static final int connectionTimeout = 10000;
   private HashMap<SteamID, GameLinkedList<InvitedUser>.Element> steamIDs = new HashMap();
   private GameLinkedList<InvitedUser> timeoutQueue = new GameLinkedList();

   public ServerSteamDeniedConnections() {
   }

   public synchronized void addDeniedUser(SteamID var1) {
      GameLinkedList.Element var2 = (GameLinkedList.Element)this.steamIDs.get(var1);
      if (var2 != null && !var2.isRemoved()) {
         var2.remove();
      }

      GameLinkedList.Element var3 = this.timeoutQueue.addLast(new InvitedUser(var1));
      this.steamIDs.put(var1, var3);
   }

   public synchronized void removeDeniedUser(SteamID var1) {
      GameLinkedList.Element var2 = (GameLinkedList.Element)this.steamIDs.remove(var1);
      if (var2 != null && !var2.isRemoved()) {
         var2.remove();
      }

   }

   public synchronized boolean isDenied(SteamID var1) {
      return this.steamIDs.containsKey(var1);
   }

   public synchronized void runCleanup(Consumer<SteamID> var1) {
      while(true) {
         if (!this.timeoutQueue.isEmpty()) {
            InvitedUser var2 = (InvitedUser)this.timeoutQueue.getFirst();
            long var3 = System.currentTimeMillis() - var2.invitedTime;
            if (var3 > 10000L) {
               var1.accept(var2.steamID);
               this.steamIDs.remove(var2.steamID);
               this.timeoutQueue.removeFirst();
               continue;
            }
         }

         return;
      }
   }

   private static class InvitedUser {
      public final SteamID steamID;
      public long invitedTime;

      public InvitedUser(SteamID var1) {
         this.steamID = var1;
         this.invitedTime = System.currentTimeMillis();
      }
   }
}
