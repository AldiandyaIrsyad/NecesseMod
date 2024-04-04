package necesse.engine.network.server.network;

import com.codedisaster.steamworks.SteamID;
import java.util.HashMap;
import necesse.engine.util.GameLinkedList;

public class ServerSteamInvitedUsers {
   private static final int invitedUsersTimeout = 600000;
   private HashMap<SteamID, GameLinkedList<InvitedUser>.Element> steamIDs = new HashMap();
   private GameLinkedList<InvitedUser> timeoutQueue = new GameLinkedList();

   public ServerSteamInvitedUsers() {
   }

   public synchronized void addInvitedUser(SteamID var1) {
      this.clean();
      GameLinkedList.Element var2 = (GameLinkedList.Element)this.steamIDs.get(var1);
      if (var2 != null && !var2.isRemoved()) {
         var2.remove();
      }

      GameLinkedList.Element var3 = this.timeoutQueue.addLast(new InvitedUser(var1));
      this.steamIDs.put(var1, var3);
   }

   public synchronized boolean isInvited(SteamID var1) {
      this.clean();
      return this.steamIDs.containsKey(var1);
   }

   public synchronized void clean() {
      while(true) {
         if (!this.timeoutQueue.isEmpty()) {
            InvitedUser var1 = (InvitedUser)this.timeoutQueue.getFirst();
            long var2 = System.currentTimeMillis() - var1.invitedTime;
            if (var2 > 600000L) {
               this.steamIDs.remove(var1.steamID);
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
