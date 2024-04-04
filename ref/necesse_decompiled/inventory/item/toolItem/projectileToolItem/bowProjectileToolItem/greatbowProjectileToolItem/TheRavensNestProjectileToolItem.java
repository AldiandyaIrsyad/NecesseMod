package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.TheRavensNestProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.Level;

public class TheRavensNestProjectileToolItem extends GreatbowProjectileToolItem {
   public TheRavensNestProjectileToolItem() {
      super(1800);
      this.attackAnimTime.setBaseValue(600);
      this.rarity = Item.Rarity.EPIC;
      this.attackDamage.setBaseValue(122.0F).setUpgradedValue(1.0F, 135.0F);
      this.velocity.setBaseValue(200);
      this.attackRange.setBaseValue(1400);
      this.attackXOffset = 12;
      this.attackYOffset = 38;
      this.particleColor = new Color(169, 150, 236);
   }

   protected void addExtraBowTooltips(ListGameTooltips var1, InventoryItem var2, PlayerMob var3, GameBlackboard var4) {
      super.addExtraBowTooltips(var1, var2, var3, var4);
      var1.add((String)Localization.translate("itemtooltip", "theravensnesttip"), 400);
   }

   public Projectile getProjectile(Level var1, int var2, int var3, Mob var4, InventoryItem var5, int var6, ArrowItem var7, boolean var8, float var9, int var10, GameDamage var11, int var12, float var13, PacketReader var14) {
      return new TheRavensNestProjectile(var1, var4, var4.x, var4.y, (float)var2, (float)var3, var9, var10, var11, var12);
   }

   protected void fireProjectiles(Level var1, final int var2, final int var3, final PlayerMob var4, final InventoryItem var5, final int var6, final ArrowItem var7, final boolean var8, final PacketReader var9) {
      final GameRandom var10 = new GameRandom((long)var6);

      for(int var11 = 0; var11 < 3; ++var11) {
         var1.entityManager.addLevelEventHidden(new WaitForSecondsEvent((float)var11 * 0.1F) {
            public void onWaitOver() {
               int var1 = var10.getIntBetween(-45, 45);
               Projectile var2x = TheRavensNestProjectileToolItem.this.getProjectile(this.level, var2, var3 + var1, var4, var5, var6, var7, var8, var9);
               var2x.setModifier(new ResilienceOnHitProjectileModifier(TheRavensNestProjectileToolItem.this.getResilienceGain(var5)));
               var2x.dropItem = var8;
               var2x.getUniqueID(var10);
               this.level.entityManager.projectiles.addHidden(var2x);
               if (this.level.isServer()) {
                  this.level.getServer().network.sendToClientsAtExcept(new PacketSpawnProjectile(var2x), (ServerClient)var4.getServerClient(), var4.getServerClient());
               }

            }
         });
      }

   }
}
