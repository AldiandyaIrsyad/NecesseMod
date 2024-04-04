package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
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
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.interfaces.OpenSound;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class StoneCoffinObject extends GameObject implements OpenSound {
   protected String textureName;
   protected GameTexture texture;
   protected GameTexture openTexture;
   protected int counterID;
   protected final GameRandom drawRandom;
   protected String droppedItemStringID;

   protected StoneCoffinObject(String var1, String var2, ToolType var3, Color var4) {
      super(new Rectangle(32, 32));
      this.textureName = var1;
      this.droppedItemStringID = var2;
      this.mapColor = var4;
      this.toolType = var3;
      this.displayMapTooltip = true;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return this.droppedItemStringID != null ? new LootTable(new LootItemInterface[]{LootItem.between(this.droppedItemStringID, 10, 20).preventLootMultiplier()}) : super.getLootTable(var1, var2, var3);
   }

   public MultiTile getMultiTile(int var1) {
      return new MultiTile(0, 1, 1, 2, var1, true, new int[]{this.counterID, this.getID()});
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

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      if (var4 == 0) {
         return new Rectangle(var2 * 32 + 6, var3 * 32, 20, 26);
      } else if (var4 == 1) {
         return new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 26, 20);
      } else {
         return var4 == 2 ? new Rectangle(var2 * 32 + 6, var3 * 32 + 6, 20, 26) : new Rectangle(var2 * 32, var3 * 32 + 6, 26, 20);
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      boolean var13 = var8 != null && (Boolean)var8.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER);
      GameTexture var14 = this.texture;
      if (this.openTexture != null) {
         ObjectEntity var15 = var3.entityManager.getObjectEntity(var4, var5);
         if (var15 != null && var15.implementsOEUsers() && ((OEUsers)var15).isInUse()) {
            var14 = this.openTexture;
         }
      }

      final SharedTextureDrawOptions var16 = new SharedTextureDrawOptions(var14);
      if (var12 == 0) {
         var16.addSprite(0, var14.getHeight() / 32 - 1, 32).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11);
      } else if (var12 == 1) {
         var16.addSprite(1, 0, 32, var14.getHeight()).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - var14.getHeight() + 32);
      } else if (var12 == 2) {
         var16.addSprite(3, 0, 32, var14.getHeight() - 32).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - var14.getHeight() + 64);
      } else {
         var16.addSprite(5, 0, 32, var14.getHeight()).spelunkerLight(var9, var13, (long)this.getID(), var3).pos(var10, var11 - var14.getHeight() + 32);
      }

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
      if (var4 == 0) {
         this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - 32);
      } else if (var4 == 1) {
         this.texture.initDraw().sprite(1, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
         this.texture.initDraw().sprite(2, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8 + 32, var9 - this.texture.getHeight() + 32);
      } else if (var4 == 2) {
         this.texture.initDraw().sprite(3, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9);
      } else {
         this.texture.initDraw().sprite(4, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8 - 32, var9 - this.texture.getHeight() + 32);
         this.texture.initDraw().sprite(5, 0, 32, this.texture.getHeight()).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 32);
      }

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
      return !this.getMultiTile(0).isMaster ? null : new InventoryObjectEntity(var1, var2, var3, 10) {
         public boolean canSetInventoryName() {
            return false;
         }
      };
   }

   public static int[] registerCoffinObject(String var0, String var1, String var2, ToolType var3, Color var4) {
      StoneCoffinObject var5 = new StoneCoffinObject(var1, var2, var3, var4);
      StoneCoffin2Object var6 = new StoneCoffin2Object(var1, var2, var3, var4);
      int var7 = ObjectRegistry.registerObject(var0, var5, 0.0F, false);
      int var8 = ObjectRegistry.registerObject(var0 + "2", var6, 0.0F, false);
      var5.counterID = var8;
      var6.counterID = var7;
      return new int[]{var7, var8};
   }
}
