package necesse.entity.objectEntity;

import java.awt.Rectangle;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.LevelIdentifier;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;

public class LadderUpObjectEntity extends PortalObjectEntity {
   private int ladderDownID;
   private GameSprite mapSprite;

   public LadderUpObjectEntity(String var1, Level var2, int var3, int var4, int var5, int var6, GameSprite var7) {
      super(var2, var1, var3, var4, var2.getIdentifier(), var3, var4);
      LevelIdentifier var8 = var2.getIdentifier();
      if (var8.isIslandPosition()) {
         this.destinationIdentifier = new LevelIdentifier(var8.getIslandX(), var8.getIslandY(), 0);
      }

      this.ladderDownID = var6;
      this.mapSprite = var7;
   }

   public void use(Server var1, ServerClient var2) {
      Level var3 = this.getLevel();
      this.teleportClientToAroundDestination(var2, (var4) -> {
         var2.newStats.ladders_used.increment(1);
         if (var4.getObjectID(this.destinationTileX, this.destinationTileY) != this.ladderDownID) {
            var3.setObject(this.getTileX(), this.getTileY(), 0);
            var1.network.sendToClientsWithTile(new PacketChangeObject(var4, this.getTileX(), this.getTileY(), 0), var3, this.getTileX(), this.getTileY());
         }

         return true;
      }, true);
      this.runClearMobs(var2);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-12, -12, 24, 24);
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      this.mapSprite.initDraw().size(24, 24).draw(var2 - 12, var3 - 12);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getObject().getDisplayName());
   }
}
