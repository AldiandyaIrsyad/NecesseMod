package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SettlementFlagObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.HumanLook;
import necesse.gfx.PlayerSprite;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.SettlementFlagObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SettlementFlagObject extends GameObject {
   public GameTexture texture;
   private PlayerMob player;

   public SettlementFlagObject() {
      super(new Rectangle(6, 6, 20, 20));
      this.mapColor = new Color(84, 84, 84);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/settlementflag");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - this.texture.getHeight() + 32);
      HumanLook var14 = var3.settlementLayer.getLook();
      final Object var13;
      if (var14 != null) {
         var13 = this.getHumanLookDrawOptions(var10, var11, 1.0F, var9, var14);
      } else {
         var13 = this.texture.initDraw().sprite(1, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - this.texture.getHeight() + 32);
      }

      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var12.draw();
            ((DrawOptions)var13).draw();
         }
      });
   }

   private synchronized DrawOptions getHumanLookDrawOptions(int var1, int var2, float var3, GameLight var4, HumanLook var5) {
      if (this.player == null) {
         this.player = new PlayerMob(0L, (NetworkClient)null);
      }

      this.player.look = var5;
      this.player.getInv().giveLookArmor();
      DrawOptions var6 = PlayerSprite.getIconDrawOptions(var1, var2 - 32, 32, 32, this.player, 0, 2, var3, var4);
      return () -> {
         GameResources.rectangleShader.use(var1 + 8, var2 - 30, 16, 28);
         var6.draw();
         GameResources.rectangleShader.stop();
      };
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 4, -32, 24, 32));
      return var4;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new SettlementFlagObjectEntity(var1, var2, var3);
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else if (!var1.isIslandPosition()) {
         return "notisland";
      } else if (var1.getIslandDimension() != 0) {
         return "notsurface";
      } else {
         return var1.biome.hasVillage() ? "villagebiome" : null;
      }
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "settlementflagtip1"));
      var3.add(Localization.translate("itemtooltip", "settlementflagtip2"));
      var3.add(Localization.translate("itemtooltip", "settlementflagtip3"));
      return var3;
   }

   public Item generateNewObjectItem() {
      return new SettlementFlagObjectItem(this);
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "usetip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public int getInteractDistance() {
      return Integer.MAX_VALUE;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         SettlementFlagObjectEntity var5 = (SettlementFlagObjectEntity)var1.entityManager.getObjectEntity(var2, var3);
         PacketOpenContainer var6 = PacketOpenContainer.ObjectEntity(ContainerRegistry.SETTLEMENT_CONTAINER, var5, var5.getContainerContentPacket(var4.getServerClient()));
         ContainerRegistry.openAndSendContainer(var4.getServerClient(), var6);
      }

   }
}
