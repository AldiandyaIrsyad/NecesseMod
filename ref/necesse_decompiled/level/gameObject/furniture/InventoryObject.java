package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.interfaces.OpenSound;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class InventoryObject extends FurnitureObject implements OpenSound {
   public GameTexture texture;
   public GameTexture openTexture;
   protected String textureName;
   public int slots;
   protected final GameRandom drawRandom;

   public InventoryObject(String var1, int var2, Rectangle var3, ToolType var4, Color var5) {
      super(var3);
      this.textureName = var1;
      this.toolType = var4;
      this.slots = var2;
      this.mapColor = var5;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
   }

   public InventoryObject(String var1, int var2, Rectangle var3, Color var4) {
      this(var1, var2, var3, ToolType.ALL, var4);
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);

      try {
         this.openTexture = GameTexture.fromFileRaw("objects/" + this.textureName + "_open");
      } catch (FileNotFoundException var2) {
         this.openTexture = null;
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      int var12 = var3.getObjectRotation(var4, var5) % (this.texture.getWidth() / 32);
      boolean var13 = var8 != null && (Boolean)var8.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER);
      GameTexture var14 = this.texture;
      if (this.openTexture != null) {
         ObjectEntity var15 = var3.entityManager.getObjectEntity(var4, var5);
         if (var15 != null && var15.implementsOEUsers() && ((OEUsers)var15).isInUse()) {
            var14 = this.openTexture;
         }
      }

      final SharedTextureDrawOptions var16 = new SharedTextureDrawOptions(var14);
      var16.addSprite(var12, 0, 32, this.texture.getHeight()).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - this.texture.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var16.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      var4 %= this.texture.getWidth() / 32;
      this.texture.initDraw().sprite(var4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
   }

   public void playOpenSound(Level var1, int var2, int var3) {
      if (this.openTexture != null) {
         Screen.playSound(GameResources.chestopen, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
      }

   }

   public void playCloseSound(Level var1, int var2, int var3) {
      if (this.openTexture != null) {
         Screen.playSound(GameResources.chestclose, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
      }

   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new InventoryObjectEntity(var1, var2, var3, this.slots);
   }

   public void doExplosionDamage(Level var1, int var2, int var3, int var4, int var5, ServerClient var6) {
      if (!var1.settlementLayer.isActive()) {
         super.doExplosionDamage(var1, var2, var3, var4, var5, var6);
      }

   }
}
