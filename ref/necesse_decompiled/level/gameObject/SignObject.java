package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SignObject extends GameObject {
   public GameTexture texture;

   public SignObject() {
      super(new Rectangle(5, 11, 22, 10));
      this.mapColor = new Color(150, 119, 70);
      this.displayMapTooltip = true;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/sign");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12 % 4, 0, 32).light(var9).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var13.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(var4 % 4, 0, 32).alpha(var5).draw(var8, var9);
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         PacketOpenContainer var5 = PacketOpenContainer.ObjectEntity(ContainerRegistry.SIGN_CONTAINER, var1.entityManager.getObjectEntity(var2, var3));
         ContainerRegistry.openAndSendContainer(var4.getServerClient(), var5);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new SignObjectEntity(var1, var2, var3);
   }
}
