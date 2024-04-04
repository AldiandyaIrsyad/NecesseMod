package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.MusicPlayerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MusicPlayerObject extends GameObject implements RoomFurniture {
   public GameTexture texture;
   public GameTextureSection musicNotesTexture;

   public MusicPlayerObject() {
      super(new Rectangle(32, 32));
      this.displayMapTooltip = true;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.rarity = Item.Rarity.RARE;
      this.toolType = ToolType.ALL;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/musicplayer");
      this.musicNotesTexture = GameResources.particlesTextureGenerator.addTexture(GameTexture.fromFile("particles/musicnotes"));
   }

   protected Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 2, var3 * 32 + 10, 28, 20);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 4, var3 * 32 + 6, 26, 22);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 2, var3 * 32 + 4, 28, 20) : new Rectangle(var2 * 32 + 2, var3 * 32 + 6, 26, 22);
      }
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      if (var4 instanceof MusicPlayerObjectEntity) {
         MusicPlayerObjectEntity var5 = (MusicPlayerObjectEntity)var4;
         if (var5.getCurrentMusic() != null && !var5.isPaused() && GameRandom.globalRandom.getChance(10)) {
            int var6 = GameRandom.globalRandom.nextInt(4);
            int var7 = GameRandom.globalRandom.getIntBetween(5, 15) * (Integer)GameRandom.globalRandom.getOneOf((Object[])(1, -1));
            int var8 = GameRandom.globalRandom.getIntBetween(-10, 10);
            byte var9 = var1.getObjectRotation(var2, var3);
            int var10;
            int var11;
            byte var12;
            switch (var9) {
               case 0:
                  var10 = var2 * 32 + 16;
                  var11 = var3 * 32 + 12;
                  var12 = 40;
                  break;
               case 1:
                  var10 = var2 * 32 + 20;
                  var11 = var3 * 32 + 16;
                  var12 = 40;
                  break;
               case 2:
                  var10 = var2 * 32 + 16;
                  var11 = var3 * 32 + 20;
                  var12 = 45;
                  break;
               default:
                  var10 = var2 * 32 + 12;
                  var11 = var3 * 32 + 16;
                  var12 = 40;
            }

            int var13 = GameRandom.globalRandom.getIntBetween(20, 50);
            int var14 = GameRandom.globalRandom.getIntBetween(1000, 3000);
            var1.entityManager.addParticle(ParticleOption.base((float)var10, (float)var11), Particle.GType.COSMETIC).moves((var4x, var5x, var6x, var7x, var8x) -> {
               var4x.x = (float)var10 + (float)Math.sin((double)var7x / 500.0) * (float)var7 + (float)var8 * var8x;
               var4x.y = (float)var11;
            }).sizeFadesInAndOut(12, 18, 100, 500).lifeTime(var14).heightMoves((float)var12, (float)(var12 + var13)).sprite(this.musicNotesTexture.sprite(var6 % 2, var6 / 2, 10)).color(Color.BLACK);
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12, 0, 32, this.texture.getHeight()).light(var9).pos(var10, var11 - (this.texture.getHeight() - 32));
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
      this.texture.initDraw().sprite(var4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - (this.texture.getHeight() - 32));
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.MUSIC_PLAYER_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new MusicPlayerObjectEntity(var1, var2, var3);
   }

   public void doExplosionDamage(Level var1, int var2, int var3, int var4, int var5, ServerClient var6) {
      if (!var1.settlementLayer.isActive()) {
         super.doExplosionDamage(var1, var2, var3, var4, var5, var6);
      }

   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      super.onWireUpdate(var1, var2, var3, var4, var5);
      ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
      if (var6 instanceof MusicPlayerObjectEntity) {
         ((MusicPlayerObjectEntity)var6).onWireUpdated();
      }

   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "musicplayertip"));
      return var3;
   }

   public String getFurnitureType() {
      return "musicplayer";
   }
}
