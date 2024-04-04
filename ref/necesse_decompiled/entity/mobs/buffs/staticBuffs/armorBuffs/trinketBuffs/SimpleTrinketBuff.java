package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.util.Arrays;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SimpleTrinketBuff extends TrinketBuff {
   private GameMessage[] tooltips;
   private ModifierValue<?>[] modifiers;

   public SimpleTrinketBuff(GameMessage[] var1, ModifierValue<?>... var2) {
      this.tooltips = var1;
      this.modifiers = var2;
   }

   public SimpleTrinketBuff(GameMessage var1, ModifierValue<?>... var2) {
      this(new GameMessage[]{var1}, var2);
   }

   public SimpleTrinketBuff(String[] var1, ModifierValue<?>... var2) {
      this((GameMessage[])Arrays.stream(var1).map((var0) -> {
         return new LocalMessage("itemtooltip", var0);
      }).toArray((var0) -> {
         return new LocalMessage[var0];
      }), var2);
   }

   public SimpleTrinketBuff(String var1, ModifierValue<?>... var2) {
      this(new String[]{var1}, var2);
   }

   public SimpleTrinketBuff(ModifierValue<?>... var1) {
      this((GameMessage[])null, var1);
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      ModifierValue[] var3 = this.modifiers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModifierValue var6 = var3[var5];
         var6.apply(var1);
      }

   }

   public ListGameTooltips getTrinketTooltip(TrinketItem var1, InventoryItem var2, PlayerMob var3) {
      ListGameTooltips var4 = super.getTrinketTooltip(var1, var2, var3);
      int var6;
      int var7;
      if (this.tooltips == null) {
         ModifierValue[] var5 = this.modifiers;
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            ModifierValue var8 = var5[var7];
            ModifierTooltip var9 = var8.getTooltip();
            if (var9 != null) {
               var4.add((Object)var9.toTooltip(false));
            }
         }
      } else {
         GameMessage[] var10 = this.tooltips;
         var6 = var10.length;

         for(var7 = 0; var7 < var6; ++var7) {
            GameMessage var11 = var10[var7];
            var4.add(var11.translate());
         }
      }

      return var4;
   }
}
