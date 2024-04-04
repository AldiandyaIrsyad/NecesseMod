package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AncientTotemObject extends GameObject {
   public GameTexture texture;

   public AncientTotemObject() {
      super(new Rectangle(32, 32));
      this.mapColor = new Color(86, 69, 40);
      this.displayMapTooltip = true;
      this.objectHealth = 1;
      this.drawDamage = false;
      this.toolType = ToolType.ALL;
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/ancienttotem");
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      super.onDestroyed(var1, var2, var3, var4, var5);
      if (var1.isServer()) {
         System.out.println("Ancient Vulture has been spawned at " + var1.getIdentifier() + ".");
         float var6 = (float)GameRandom.globalRandom.nextInt(360);
         float var7 = (float)Math.cos(Math.toRadians((double)var6));
         float var8 = (float)Math.sin(Math.toRadians((double)var6));
         float var9 = 960.0F;
         Mob var10 = MobRegistry.getMob("ancientvulture", var1);
         var1.entityManager.addMob(var10, (float)(var2 * 32 + 16 + (int)(var7 * var9)), (float)(var3 * 32 + 16 + (int)(var8 * var9)));
         var1.getServer().network.sendToClientsWithTile(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", var10.getLocalization())), var1, var2, var3);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) - 16;
      int var11 = var7.getTileDrawY(var5) - 32;
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().size(64, 64).light(var9).pos(var10, var11);
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
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -32, 32, 32));
      return var4;
   }
}
