package necesse.inventory.item.miscItem;

import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BannerItem extends Item {
   public Function<Mob, Buff> buff;
   public int range;
   public boolean buffsPlayers = true;
   public boolean buffsMobs = false;

   public BannerItem(Item.Rarity var1, int var2, Function<Mob, Buff> var3) {
      super(1);
      this.setItemCategory(new String[]{"equipment", "banners"});
      this.rarity = var1;
      this.range = var2;
      this.buff = var3;
      this.worldDrawSize = 32;
      this.incinerationTimeMillis = 30000;
   }

   public void tickHolding(InventoryItem var1, PlayerMob var2) {
      super.tickHolding(var1, var2);
      if (this.buffsPlayers) {
         GameUtils.streamNetworkClients(var2.getLevel()).filter((var2x) -> {
            return var2x.playerMob.getDistance(var2.x, var2.y) <= (float)this.range;
         }).filter((var3x) -> {
            return this.shouldBuffPlayer(var1, var2, var3x.playerMob);
         }).forEach((var1x) -> {
            this.applyBuffs(var1x.playerMob);
         });
      }

      if (this.buffsMobs) {
         Stream var3 = var2.getLevel().entityManager.mobs.streamInRegionsShape(GameUtils.rangeBounds(var2.x, var2.y, this.range), 0);
         var3.filter((var0) -> {
            return !var0.removed();
         }).filter((var2x) -> {
            return var2x.getDistance(var2.x, var2.y) <= (float)this.range;
         }).filter((var3x) -> {
            return this.shouldBuffMob(var1, var2, var3x);
         }).forEach(this::applyBuffs);
      }

   }

   public DrawOptions getStandDrawOptions(Level var1, int var2, int var3, int var4, int var5, GameLight var6) {
      int var7 = GameUtils.getAnim(var1.getWorldEntity().getTime() + (long)(var2 * 97) + (long)(var3 * 151), 4, 800);
      byte var8 = 0;
      byte var9 = 0;
      short var10 = 64;
      if (this.holdTexture.getWidth() / 128 == 6) {
         var8 = -32;
         var9 = -32;
         var10 = 128;
      }

      return this.holdTexture.initDraw().sprite(1 + var7, 3, var10).light(var6).pos(var4 - 9 + var8, var5 - 40 + var9 + (var7 != 1 && var7 != 3 ? 0 : 2));
   }

   public String canAttack(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5) {
      return "";
   }

   public float getSinkingRate(ItemPickupEntity var1, float var2) {
      return super.getSinkingRate(var1, var2) / 5.0F;
   }

   public void applyBuffs(Mob var1) {
      Buff var2 = (Buff)this.buff.apply(var1);
      if (var2 != null) {
         ActiveBuff var3 = new ActiveBuff(var2, var1, 100, (Attacker)null);
         var1.buffManager.addBuff(var3, false);
      }
   }

   public boolean shouldBuffPlayer(InventoryItem var1, PlayerMob var2, PlayerMob var3) {
      return var2 == var3 || var2.isSameTeam(var3);
   }

   public boolean shouldBuffMob(InventoryItem var1, PlayerMob var2, Mob var3) {
      return true;
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "bannertip"));
      return var4;
   }
}
