package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RoyalEggObject extends GameObject {
   public GameTexture texture;

   public RoyalEggObject() {
      super(new Rectangle(4, 4, 24, 24));
      this.mapColor = new Color(89, 44, 44);
      this.displayMapTooltip = true;
      this.objectHealth = 1;
      this.drawDamage = false;
      this.attackThrough = true;
      this.isLightTransparent = true;
      this.toolType = ToolType.ALL;
      this.lightSat = 0.2F;
      this.lightLevel = 50;
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("item", "royalegg");
   }

   public void loadTextures() {
      super.loadTextures();
      this.texture = GameTexture.fromFile("objects/royalegg");
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      return new LootTable();
   }

   public void playDamageSound(Level var1, int var2, int var3, boolean var4) {
      Screen.playSound(GameResources.npcdeath, SoundEffect.effect((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)));
   }

   public void attackThrough(Level var1, int var2, int var3, GameDamage var4) {
      super.attackThrough(var1, var2, var3, var4);
      this.playDamageSound(var1, var2, var3, true);
   }

   public void onDestroyed(Level var1, int var2, int var3, ServerClient var4, ArrayList<ItemPickupEntity> var5) {
      super.onDestroyed(var1, var2, var3, var4, var5);
      this.spawnBoss(var1, var2, var3);
   }

   public void spawnBoss(Level var1, int var2, int var3) {
      if (var1.isServer()) {
         System.out.println("Queen Spider has been spawned at " + var1.getIdentifier() + ".");
         float var4 = (float)GameRandom.globalRandom.nextInt(360);
         float var5 = (float)Math.cos(Math.toRadians((double)var4));
         float var6 = (float)Math.sin(Math.toRadians((double)var4));
         float var7 = 960.0F;
         Mob var8 = MobRegistry.getMob("queenspider", var1);
         var1.entityManager.addMob(var8, (float)(var2 * 32 + 16 + (int)(var5 * var7)), (float)(var3 * 32 + 16 + (int)(var6 * var7)));
         var1.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bossawoke", "name", var8.getLocalization())), (Level)var1);
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, Level var3, int var4, int var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      GameLight var9 = var3.getLightLevel(var4, var5);
      int var10 = var7.getTileDrawX(var4) - (this.texture.getWidth() - 32) / 2;
      int var11 = var7.getTileDrawY(var5) - (this.texture.getHeight() - 32);
      final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).pos(var10, var11);
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
      var4.add(new ObjectHoverHitbox(var2, var3, 0, -24, 32, 24));
      return var4;
   }

   public void onMouseHover(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, boolean var6) {
      super.onMouseHover(var1, var2, var3, var4, var5, var6);
      Screen.addTooltip(new StringTooltips(this.getDisplayName()), TooltipLocation.INTERACT_FOCUS);
   }
}
