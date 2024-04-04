package necesse.inventory.item.placeableItem.tileItem;

import necesse.engine.GameEvents;
import necesse.engine.Screen;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class GrassSeedItem extends PlaceableItem {
   public String grassStringID;

   public GrassSeedItem(String var1) {
      super(100, true);
      this.grassStringID = var1;
      this.dropsAsMatDeathPenalty = true;
      this.addGlobalIngredient(new String[]{"anycompostable"});
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "grassseedtip"));
      return var4;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         int var7 = var2 / 32;
         int var8 = var3 / 32;
         if (var1.isProtected(var7, var8)) {
            return "protected";
         } else if (var1.getTileID(var7, var8) != TileRegistry.dirtID) {
            return "notdirt";
         } else {
            return var4.getPositionPoint().distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16)) > 100.0 ? "outofrange" : null;
         }
      } else {
         return "outsidelevel";
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      int var7 = var2 / 32;
      int var8 = var3 / 32;
      ItemPlaceEvent var9 = new ItemPlaceEvent(var1, var7, var8, var4, var5);
      GameEvents.triggerEvent(var9);
      if (!var9.isPrevented()) {
         GameTile var10 = TileRegistry.getTile(this.grassStringID);
         var10.placeTile(var1, var7, var8);
         if (var1.isClient()) {
            Screen.playSound(GameResources.tap, SoundEffect.effect((float)(var7 * 32 + 16), (float)(var8 * 32 + 16)));
         } else {
            var1.sendTileUpdatePacket(var7, var8);
            var4.getServerClient().newStats.tiles_placed.increment(1);
            var1.getLevelTile(var7, var8).checkAround();
            var1.getLevelObject(var7, var8).checkAround();
         }

         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      if (this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null) == null) {
         float var8 = 0.5F;
         int var9 = var2 / 32;
         int var10 = var3 / 32;
         TileRegistry.getTile(this.grassStringID).drawPreview(var1, var9, var10, var8, var5, var4);
      }

   }
}
