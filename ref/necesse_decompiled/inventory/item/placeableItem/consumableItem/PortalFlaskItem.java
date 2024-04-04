package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWorldPosition;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.HomePortalMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class PortalFlaskItem extends ConsumableItem implements ItemInteractAction {
   public PortalFlaskItem() {
      super(1, false);
      this.attackAnimTime.setBaseValue(500);
      this.rarity = Item.Rarity.RARE;
      this.itemCooldownTime.setBaseValue(2000);
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (this.singleUse) {
         var5.setAmount(var5.getAmount() - 1);
      }

      if (var1.isServer()) {
         ServerClient var7 = var4.getServerClient();
         var7.validateSpawnPoint(true);
         Point var8 = new Point(16, 16);
         Level var9 = var1.getServer().world.getLevel(var7.spawnLevelIdentifier);
         if (!var7.isDefaultSpawnPoint()) {
            var8 = RespawnObject.calculateSpawnOffset(var9, var7.spawnTile.x, var7.spawnTile.y, var7);
         }

         Point2D.Float var10 = GameMath.normalize((float)var2 - var4.x, (float)var3 - var4.y);
         HomePortalMob var11 = (HomePortalMob)MobRegistry.getMob("homeportal", var1);
         HomePortalMob var12 = (HomePortalMob)MobRegistry.getMob("homeportal", var9);
         var11.setupPortal(var7, var12);
         var12.setupPortal(var7, var11);
         Iterator var13 = var7.homePortals.iterator();

         while(var13.hasNext()) {
            MobWorldPosition var14 = (MobWorldPosition)var13.next();
            Mob var15 = var14.getMob(var7.getServer(), false);
            if (var15 != null) {
               var15.remove(0.0F, 0.0F, (Attacker)null, true);
            }
         }

         var7.homePortals.clear();
         var11.dx = var10.x * 50.0F;
         var11.dy = var10.y * 50.0F;
         int var16 = (int)var4.y - 4;
         if (var1.collides((Shape)var11.getCollision((int)var4.x, var16), (CollisionFilter)var11.getLevelCollisionFilter())) {
            var16 += 4;
         }

         var1.entityManager.addMob(var11, (float)((int)var4.x), (float)var16);
         var9.entityManager.addMob(var12, (float)(var7.spawnTile.x * 32 + var8.x), (float)(var7.spawnTile.y * 32 + var8.y - 4));
         var7.homePortals.add(new MobWorldPosition(var11));
         var7.homePortals.add(new MobWorldPosition(var12));
      }

      return var5;
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt4, SoundEffect.effect(var4).volume(0.5F).pitch(GameRandom.globalRandom.getFloatBetween(1.0F, 1.1F)));
      }

   }

   public void setDrawAttackRotation(InventoryItem var1, ItemAttackDrawOptions var2, float var3, float var4, float var5) {
      var2.swingRotation(var5);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      return null;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "homeportaltip"));
      var4.add(Localization.translate("itemtooltip", "homeportalcleartip"));
      if (!this.singleUse) {
         var4.add(Localization.translate("itemtooltip", "infiniteuse"));
      }

      return var4;
   }

   public ItemControllerInteract getControllerInteract(Level var1, PlayerMob var2, InventoryItem var3, boolean var4, int var5, LinkedList<Rectangle> var6, LinkedList<Rectangle> var7) {
      return var4 && !this.overridesObjectInteract(var1, var2, var3) ? null : new ItemControllerInteract(var2.getX(), var2.getY()) {
         public DrawOptions getDrawOptions(GameCamera var1) {
            return null;
         }

         public void onCurrentlyFocused(GameCamera var1) {
         }
      };
   }

   public boolean canLevelInteract(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return true;
   }

   public InventoryItem onLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, PacketReader var9) {
      if (var1.isServer()) {
         ServerClient var10 = var4.getServerClient();
         Iterator var11 = var10.homePortals.iterator();

         while(var11.hasNext()) {
            MobWorldPosition var12 = (MobWorldPosition)var11.next();
            Mob var13 = var12.getMob(var10.getServer(), false);
            if (var13 != null) {
               var13.remove(0.0F, 0.0F, (Attacker)null, true);
            }
         }

         var10.homePortals.clear();
      }

      return var6;
   }
}
