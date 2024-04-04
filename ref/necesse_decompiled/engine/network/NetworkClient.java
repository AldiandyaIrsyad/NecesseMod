package necesse.engine.network;

import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.PlayerMob;
import necesse.level.maps.Level;

public abstract class NetworkClient {
   private boolean isRemote;
   public boolean pvpEnabled;
   private int teamID = -1;
   public final int slot;
   public final long authentication;
   public PlayerMob playerMob;
   protected boolean isDead;
   protected boolean hasSpawned;
   protected boolean isDisposed;
   public boolean craftingUsesNearbyInventories;

   public NetworkClient(int var1, long var2) {
      this.slot = var1;
      this.authentication = var2;
   }

   public abstract boolean pvpEnabled();

   public void setTeamID(int var1) {
      if (var1 >= -1 && var1 <= 32767) {
         this.teamID = var1;
         if (this.playerMob != null) {
            this.playerMob.setTeam(var1);
         }

      } else {
         System.out.println("Attempted invalid team for network client: " + var1);
      }
   }

   public int getTeamID() {
      return this.teamID;
   }

   public boolean isSameTeam(NetworkClient var1) {
      return this.isSameTeam(var1.getTeamID());
   }

   public boolean isSameTeam(int var1) {
      if (this.getTeamID() != -1 && var1 != -1) {
         return this.getTeamID() == var1;
      } else {
         return false;
      }
   }

   protected void makeClientClient() {
      this.isRemote = true;
   }

   protected void makeServerClient() {
      this.isRemote = false;
   }

   public boolean isClient() {
      return this.isRemote;
   }

   public boolean isServer() {
      return !this.isRemote;
   }

   /** @deprecated */
   @Deprecated
   public boolean isClientClient() {
      return this.isClient();
   }

   /** @deprecated */
   @Deprecated
   public boolean isServerClient() {
      return this.isServer();
   }

   public ClientClient getClientClient() {
      return (ClientClient)this;
   }

   public ServerClient getServerClient() {
      return (ServerClient)this;
   }

   public boolean isDead() {
      return this.isDead;
   }

   public boolean hasSpawned() {
      return this.hasSpawned;
   }

   public void dispose() {
      this.isDisposed = true;
      if (this.playerMob != null) {
         this.playerMob.remove();
      }

   }

   public boolean isDisposed() {
      return this.isDisposed;
   }

   public abstract String getName();

   public abstract LevelIdentifier getLevelIdentifier();

   public final boolean isSamePlace(LevelIdentifier var1) {
      return this.getLevelIdentifier().equals(var1);
   }

   public final boolean isSamePlace(int var1, int var2, int var3) {
      return this.getLevelIdentifier().equals(var1, var2, var3);
   }

   public final boolean isSamePlace(NetworkClient var1) {
      return this.isSamePlace(var1.getLevelIdentifier());
   }

   public final boolean isSamePlace(Level var1) {
      return var1 == null ? false : this.isSamePlace(var1.getIdentifier());
   }
}
