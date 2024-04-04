package necesse.level.maps;

import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.gameTile.GameTile;

public class LevelTile {
   public final GameTile tile;
   public final Level level;
   public final int tileX;
   public final int tileY;

   private LevelTile(Level var1, int var2, int var3, GameTile var4) {
      this.tile = var4;
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
   }

   public LevelTile(Level var1, int var2, int var3) {
      this(var1, var2, var3, var1.getTile(var2, var3));
   }

   public static LevelTile custom(Level var0, int var1, int var2, GameTile var3) {
      return new LevelTile(var0, var1, var2, var3);
   }

   public String canPlace() {
      return this.tile.canPlace(this.level, this.tileX, this.tileY);
   }

   public boolean isValid() {
      return this.tile.isValid(this.level, this.tileX, this.tileY);
   }

   public void checkAround() {
      this.tile.checkAround(this.level, this.tileX, this.tileY);
   }

   public void attemptPlace(PlayerMob var1, String var2) {
      this.tile.attemptPlace(this.level, this.tileX, this.tileY, var1, var2);
   }

   public void onTileDestroyed(ServerClient var1, ArrayList<ItemPickupEntity> var2) {
      this.tile.onDestroyed(this.level, this.tileX, this.tileY, var1, var2);
   }

   public void tick(Mob var1) {
      this.tile.tick(var1, this.level, this.tileX, this.tileY);
   }

   public void tick() {
      this.tile.tick(this.level, this.tileX, this.tileY);
   }

   public void tickValid(boolean var1) {
      this.tile.tickValid(this.level, this.tileX, this.tileY, var1);
   }

   public void onDamaged(int var1, ServerClient var2, boolean var3, int var4, int var5) {
      this.tile.onDamaged(this.level, this.tileX, this.tileY, var1, var2, var3, var4, var5);
   }

   public GameTooltips getMapTooltips() {
      return this.tile.getMapTooltips(this.level, this.tileX, this.tileY);
   }

   public int getLiquidBobbing() {
      return this.tile.getLiquidBobbing(this.level, this.tileX, this.tileY);
   }

   public int getHeight() {
      return this.level.liquidManager.getHeight(this.tileX, this.tileY);
   }

   public String toString() {
      return super.toString() + "{" + this.tileX + "x" + this.tileY + ", " + this.level.getHostString() + ", " + this.tile.getDisplayName() + "}";
   }
}
