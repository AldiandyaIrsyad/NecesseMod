package necesse.level.gameObject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.LadderDownObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.ChatBubbleText;
import necesse.level.maps.light.GameLight;

public class LadderDownObject extends GameObject {
   public GameTexture texture;
   public int ladderUpObjectID = -1;
   public final String textureName;
   public final int destinationDimension;

   protected LadderDownObject(String var1, int var2, Color var3, Item.Rarity var4) {
      this.textureName = var1;
      this.destinationDimension = var2;
      this.mapColor = var3;
      this.rarity = var4;
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName + "down");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) - this.texture.getWidth() / 2 + 16;
      int var11 = var7.getTileDrawY(var5);
      int var12 = var11 - (this.texture.getHeight() - 32) + 32;
      TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(0, 0, 32).light(var9).pos(var10, var11);
      final TextureDrawOptionsEnd var14 = this.texture.initDraw().section(0, this.texture.getWidth(), 32, this.texture.getHeight()).light(var9).pos(var10, var12);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 20;
         }

         public void draw(TickManager var1) {
            var14.draw();
         }
      });
      var2.add((var1x) -> {
         var13.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2) - this.texture.getWidth() / 2 + 16;
      int var9 = var7.getTileDrawY(var3);
      int var10 = var9 - (this.texture.getHeight() - 32) + 32;
      this.texture.initDraw().sprite(0, 0, 32).alpha(var5).draw(var8, var9);
      this.texture.initDraw().section(0, this.texture.getWidth(), 32, this.texture.getHeight()).alpha(var5).draw(var8, var10);
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else if (!var1.isIslandPosition()) {
         return "notisland";
      } else {
         return var1.getIslandDimension() != 0 ? "notsurface" : null;
      }
   }

   public void attemptPlace(Level var1, int var2, int var3, PlayerMob var4, String var5) {
      if (var1.isClient() && var5.equals("notsurface")) {
         var4.getLevel().hudManager.addElement(new ChatBubbleText(var4, Localization.translate("misc", "laddernotsurface")));
      }

   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer() && var4.isServerClient()) {
         ObjectEntity var5 = var1.entityManager.getObjectEntity(var2, var3);
         if (var5 instanceof PortalObjectEntity) {
            ServerClient var6 = var4.getServerClient();
            ((PortalObjectEntity)var5).use(var1.getServer(), var6);
            if (var6.achievementsLoaded()) {
               var6.achievements().SPELUNKER.markCompleted(var6);
            }
         }
      }

      super.interact(var1, var2, var3, var4);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new LadderDownObjectEntity(this.textureName + "down", var1, var2, var3, this.destinationDimension, this.getID(), this.ladderUpObjectID);
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      if (var1.isServer()) {
         ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
         if (var6 instanceof PortalObjectEntity) {
            PortalObjectEntity var7 = (PortalObjectEntity)var6;
            if (var1.getServer().world.levelExists(var7.getDestinationIdentifier())) {
               Level var8 = var1.getServer().world.getLevel(var7.getDestinationIdentifier());
               if (var8.getObjectID(var7.destinationTileX, var7.destinationTileY) == this.ladderUpObjectID) {
                  var8.setObject(var7.destinationTileX, var7.destinationTileY, 0);
                  var1.getServer().network.sendToClientsWithTile(new PacketChangeObject(var1, var7.destinationTileX, var7.destinationTileY, 0), var8, var7.destinationTileX, var7.destinationTileY);
               }
            }
         }
      }

      super.onDestroyed(var1, var2, var3, var4, var5);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", this.getStringID() + "tip"));
      return var3;
   }

   public static int[] registerLadderPair(String var0, int var1, Color var2, Item.Rarity var3, int var4) {
      LadderDownObject var5 = new LadderDownObject(var0, var1, var2, var3);
      LadderUpObject var6 = new LadderUpObject(var0, var1, var2);
      int var7 = ObjectRegistry.registerObject(var0 + "down", var5, (float)var4, true);
      int var8 = ObjectRegistry.registerObject(var0 + "up", var6, 0.0F, false);
      var5.ladderUpObjectID = var8;
      var6.ladderDownObjectID = var7;
      return new int[]{var7, var8};
   }
}
