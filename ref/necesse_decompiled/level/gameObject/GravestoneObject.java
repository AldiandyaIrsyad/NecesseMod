package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GravestoneObject extends GameObject {
   protected String textureName;
   protected GameTexture texture;
   protected final GameRandom drawRandom;

   public GravestoneObject(String var1, ToolType var2, Color var3) {
      super(new Rectangle());
      this.textureName = var1;
      this.mapColor = var3;
      this.toolType = var2;
      this.displayMapTooltip = true;
      this.objectHealth = 50;
      this.drawDamage = false;
      this.isLightTransparent = true;
      this.drawRandom = new GameRandom();
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return var1.getCrateLootTable();
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", "gravestone");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/" + this.textureName);
   }

   public Rectangle getCollision(Level var1, int var2, int var3, int var4) {
      return super.getCollision(var1, var2, var3, var4);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4);
      int var11 = var7.getTileDrawY(var5);
      byte var12 = var3.getObjectRotation(var4, var5);
      final byte var13;
      if (var12 == 0) {
         var13 = 22;
      } else if (var12 == 2) {
         var13 = 4;
      } else {
         var13 = 16;
      }

      boolean var14 = var8 != null && (Boolean)var8.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER);
      final SharedTextureDrawOptions var15 = new SharedTextureDrawOptions(this.texture);
      var15.addSprite(var12, 0, 32, this.texture.getHeight() - 32).spelunkerLight(var9, var14, (long)this.getID(), var3).pos(var10, var11 - this.texture.getHeight() + 64);
      TextureDrawOptionsEnd var16 = this.texture.initDraw().sprite(var12, this.texture.getHeight() / 32 - 1, 32).light(var9).pos(var10, var11);
      var1.add(new LevelSortedDrawable(this, var4, var5) {
         public int getSortY() {
            return var13;
         }

         public void draw(TickManager var1) {
            var15.draw();
         }
      });
      var2.add((var1x) -> {
         var16.draw();
      });
   }

   public void drawPreview(Level var1, int var2, int var3, int var4, float var5, PlayerMob var6, GameCamera var7) {
      int var8 = var7.getTileDrawX(var2);
      int var9 = var7.getTileDrawY(var3);
      this.texture.initDraw().sprite(var4, this.texture.getHeight() / 32 - 1, 32).alpha(var5).draw(var8, var9);
      this.texture.initDraw().sprite(var4, 0, 32, this.texture.getHeight() - 32).alpha(var5).draw(var8, var9 - this.texture.getHeight() + 64);
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      super.onDestroyed(var1, var2, var3, var4, var5);
      if (var4 != null && var4.achievementsLoaded()) {
         var4.achievements().GRAVE_DIGGER.markCompleted(var4);
      }

   }

   public List<ObjectHoverHitbox> getHoverHitboxes(Level var1, int var2, int var3) {
      List var4 = super.getHoverHitboxes(var1, var2, var3);
      byte var5 = var1.getObjectRotation(var2, var3);
      if (var5 == 2) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -16, 32, 16, 4));
      } else if (var5 == 1 || var5 == 3) {
         var4.add(new ObjectHoverHitbox(var2, var3, 0, -10, 32, 10, 16));
      }

      return var4;
   }

   public boolean shouldSnapSmartMining(Level var1, int var2, int var3) {
      return true;
   }
}
