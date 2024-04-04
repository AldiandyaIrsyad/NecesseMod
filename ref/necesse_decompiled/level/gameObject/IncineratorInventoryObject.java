package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.FueledIncineratorObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class IncineratorInventoryObject extends GameObject {
   public GameTexture texture;
   public GameTexture openTexture;
   public GameTexture activeTexture;
   public GameTexture activeOpenTexture;

   public IncineratorInventoryObject() {
      super(new Rectangle(2, 6, 28, 20));
      this.setItemCategory(new String[]{"objects", "craftingstations"});
      this.mapColor = new Color(125, 122, 119);
      this.displayMapTooltip = true;
      this.toolType = ToolType.ALL;
      this.rarity = Item.Rarity.COMMON;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.lightHue = 50.0F;
      this.lightSat = 0.2F;
      this.replaceCategories.add("workstation");
      this.canReplaceCategories.add("workstation");
      this.canReplaceCategories.add("wall");
      this.canReplaceCategories.add("furniture");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/incinerator");
      this.openTexture = GameTexture.fromFile("objects/incinerator_open");
      this.activeTexture = GameTexture.fromFile("objects/incinerator_active");
      this.activeOpenTexture = GameTexture.fromFile("objects/incinerator_active_open");
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add((String)Localization.translate("itemtooltip", "incineratortip"), 400);
      return var3;
   }

   public int getLightLevel(Level var1, int var2, int var3) {
      FueledIncineratorObjectEntity var4 = this.getIncineratorObjectEntity(var1, var2, var3);
      return var4 != null && var4.isFuelRunning() ? 100 : 0;
   }

   public void tickEffect(Level var1, int var2, int var3) {
      super.tickEffect(var1, var2, var3);
      FueledIncineratorObjectEntity var4 = this.getIncineratorObjectEntity(var1, var2, var3);
      if (var4 != null && var4.isFuelRunning()) {
         if (var4.isInUse()) {
            float var5 = 0.5F;

            while(var5 >= 1.0F || GameRandom.globalRandom.getChance(var5)) {
               --var5;
               byte var6 = 30;
               byte var7 = 8;
               ParticleOption var8 = var1.entityManager.addParticle((float)(var2 * 32 + GameRandom.globalRandom.getIntBetween(11, 21)), (float)(var3 * 32 + GameRandom.globalRandom.getIntBetween(10, 16) + var7), GameRandom.globalRandom.getChance(0.75F) ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.getFloatBetween(-1.0F, 1.0F), GameRandom.globalRandom.getFloatBetween(-1.0F, 1.0F)).heightMoves((float)(var6 - var7), (float)(var6 + 10 - var7)).colorRandom(30.0F, 1.0F, 0.9F, 12.0F, 0.1F, 0.1F).sizeFades(10, 14).lifeTime(2000);
               if (GameRandom.globalRandom.nextBoolean()) {
                  var8.onProgress(0.5F, (var2x) -> {
                     for(int var3 = 0; var3 < GameRandom.globalRandom.getIntBetween(1, 2); ++var3) {
                        var1.entityManager.addParticle(var2x.x + (float)((int)(GameRandom.globalRandom.nextGaussian() * 2.0)), var2x.y, Particle.GType.COSMETIC).smokeColor().sizeFades(8, 12).heightMoves((float)(var6 + 6), (float)(var6 + 20));
                     }

                  });
               }
            }
         } else if (GameRandom.globalRandom.nextInt(10) == 0) {
            int var9 = 24 + GameRandom.globalRandom.nextInt(8);
            var1.entityManager.addParticle((float)(var2 * 32 + GameRandom.globalRandom.getIntBetween(8, 24)), (float)(var3 * 32 + 32), Particle.GType.COSMETIC).smokeColor().heightMoves((float)var9, (float)(var9 + 20)).lifeTime(1000);
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      int var12 = var3.getObjectRotation(var4, var5) % (this.texture.getWidth() / 32);
      boolean var13 = var8 != null && (Boolean)var8.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER);
      FueledIncineratorObjectEntity var14 = this.getIncineratorObjectEntity(var3, var4, var5);
      GameTexture var15 = var14 != null && var14.isFuelRunning() ? this.activeTexture : this.texture;
      if (this.openTexture != null) {
         ObjectEntity var16 = var3.entityManager.getObjectEntity(var4, var5);
         if (var16 != null && var16.implementsOEUsers() && ((OEUsers)var16).isInUse()) {
            var15 = var14 != null && var14.isFuelRunning() ? this.activeOpenTexture : this.openTexture;
         }
      }

      final SharedTextureDrawOptions var17 = new SharedTextureDrawOptions(var15);
      var17.addSprite(var12, 0, 32, var15.getHeight()).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - var15.getHeight() + 32);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return 16;
         }

         public void draw(TickManager var1) {
            var17.draw();
         }
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      var4 %= this.texture.getWidth() / 32;
      this.texture.initDraw().sprite(var4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16));
      return var4;
   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new FueledIncineratorObjectEntity(var1, var2, var3, 2, 10);
   }

   public String getInteractTip(Level var1, int var2, int var3, PlayerMob var4, boolean var5) {
      return Localization.translate("controls", "opentip");
   }

   public boolean canInteract(Level var1, int var2, int var3, PlayerMob var4) {
      return true;
   }

   public void interact(Level var1, int var2, int var3, PlayerMob var4) {
      if (var1.isServer()) {
         OEInventoryContainer.openAndSendContainer(ContainerRegistry.INCINERATOR_INVENTORY_CONTAINER, var4.getServerClient(), var1, var2, var3);
      }

   }

   public FueledIncineratorObjectEntity getIncineratorObjectEntity(Level var1, int var2, int var3) {
      ObjectEntity var4 = var1.entityManager.getObjectEntity(var2, var3);
      return var4 instanceof FueledIncineratorObjectEntity ? (FueledIncineratorObjectEntity)var4 : null;
   }
}
