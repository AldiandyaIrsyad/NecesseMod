package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.LadderUpObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

class LadderUpObject extends GameObject {
   public GameTexture texture;
   public int ladderDownObjectID = -1;
   public final String textureName;
   public final int targetDestination;

   protected LadderUpObject(String var1, int var2, Color var3) {
      this.textureName = var1;
      this.targetDestination = var2;
      this.mapColor = var3;
      this.drawDamage = false;
      this.toolType = ToolType.UNBREAKABLE;
      this.isLightTransparent = true;
      this.lightLevel = 75;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName + "up");
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) - this.texture.getWidth() / 2 + 16;
      int var11 = var7.getTileDrawY(var5) - (this.texture.getHeight() - 32) + 32;
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().section(0, this.texture.getWidth(), 32, this.texture.getHeight()).light(var9).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var12.draw();
         }
      });
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -20, 32, 20));
      return var4;
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      if (!var1.isIslandPosition()) {
         return "notisland";
      } else {
         return var1.getIslandDimension() != this.targetDestination ? "notcave" : null;
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
            ((PortalObjectEntity)var5).use(var1.getServer(), var4.getServerClient());
         }
      }

      super.interact(var1, var2, var3, var4);
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new LadderUpObjectEntity(this.textureName + "up", var1, var2, var3, 0, this.ladderDownObjectID, this.texture == null ? null : new GameSprite(this.texture, 0, 0, 32));
   }
}
