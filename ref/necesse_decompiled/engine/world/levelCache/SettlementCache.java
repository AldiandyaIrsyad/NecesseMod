package necesse.engine.world.levelCache;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class SettlementCache {
   public final boolean loaded;
   public final int islandX;
   public final int islandY;
   public boolean active;
   public GameMessage name;
   public long ownerAuth;
   public int teamID;

   private SettlementCache(boolean var1, int var2, int var3) {
      this.active = false;
      this.name = null;
      this.ownerAuth = -1L;
      this.teamID = -1;
      this.loaded = var1;
      this.islandX = var2;
      this.islandY = var3;
   }

   public SettlementCache(int var1, int var2) {
      this(true, var1, var2);
   }

   public SettlementCache(int var1, int var2, boolean var3, GameMessage var4, long var5, int var7) {
      this(true, var1, var2);
      this.active = var3;
      this.name = var4;
      this.ownerAuth = var5;
      this.teamID = var7;
   }

   public SettlementCache(Level var1) {
      this(var1.isIslandPosition() && var1.getIslandDimension() == 0, var1.getIslandX(), var1.getIslandY());
      if (this.loaded && var1.settlementLayer.isActive()) {
         this.active = true;
         this.name = var1.settlementLayer.getSettlementName();
         this.ownerAuth = var1.settlementLayer.getOwnerAuth();
         this.teamID = var1.settlementLayer.getTeamID();
      }

   }

   public boolean hasAccess(ServerClient var1) {
      return this.ownerAuth == var1.authentication || var1.isSameTeam(this.teamID);
   }
}
