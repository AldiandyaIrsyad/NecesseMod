package necesse.inventory.item.toolItem.summonToolItem;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketSummonFocus;
import necesse.engine.network.server.FollowPosition;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.SettlerWeaponItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;

public class SummonToolItem extends ToolItem implements ItemInteractAction {
   public boolean singleUse = false;
   public String mobStringID;
   public String summonType;
   public FollowPosition followPosition;
   public float summonSpaceTaken;
   public boolean drawMaxSummons = true;

   public SummonToolItem(String var1, FollowPosition var2, float var3, int var4) {
      super(var4);
      this.mobStringID = var1;
      this.summonType = "summonedmob";
      this.followPosition = var2;
      this.summonSpaceTaken = var3;
      this.damageType = DamageTypeRegistry.SUMMON;
      this.attackAnimTime.setBaseValue(400);
      this.setItemCategory(new String[]{"equipment", "weapons", "summonweapons"});
      if (this instanceof SettlerWeaponItem) {
         this.setItemCategory(ItemCategory.equipmentManager, new String[]{"weapons", "summonweapons"});
      }

      this.keyWords.add("summon");
      this.attackXOffset = 4;
      this.attackYOffset = 4;
      this.enchantCost.setUpgradedValue(1.0F, 2100);
   }

   public ListGameTooltips getPreEnchantmentTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getPreEnchantmentTooltips(var1, var2, var3);
      var4.add(Localization.translate("itemtooltip", "summonfocustip"));
      GameTooltips var5 = this.getSpaceTakenTooltip(var1, var2);
      if (var5 != null) {
         var4.add((Object)var5);
      }

      if (this.drawMaxSummons) {
         int var6 = this.getMaxSummons(var1, var2);
         if (var6 != 1) {
            var4.add(Localization.translate("itemtooltip", "summonslots", "count", (Object)var6));
         }
      }

      if (this.singleUse) {
         var4.add(Localization.translate("itemtooltip", "singleuse"));
      }

      return var4;
   }

   public void addStatTooltips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, boolean var5) {
      this.addAttackDamageTip(var1, var2, var3, var4, var5);
      this.addCritChanceTip(var1, var2, var3, var4, var5);
   }

   public GameTooltips getSpaceTakenTooltip(InventoryItem var1, PlayerMob var2) {
      float var3 = this.getSummonSpaceTaken(var1, var2);
      return var3 != 1.0F ? new StringTooltips(Localization.translate("itemtooltip", "summonuseslots", "count", (Object)var3)) : null;
   }

   public int getMaxSummons(InventoryItem var1, PlayerMob var2) {
      return var2 == null ? (Integer)BuffModifiers.MAX_SUMMONS.defaultBuffManagerValue : (Integer)var2.buffManager.getModifier(BuffModifiers.MAX_SUMMONS);
   }

   public float getSummonSpaceTaken(InventoryItem var1, PlayerMob var2) {
      return this.summonSpaceTaken;
   }

   public Set<Integer> getValidEnchantmentIDs(InventoryItem var1) {
      return EnchantmentRegistry.summonItemEnchantments;
   }

   public ToolItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return (ToolItemEnchantment)Enchantable.getRandomEnchantment(var1, EnchantmentRegistry.summonItemEnchantments, this.getEnchantmentID(var2), ToolItemEnchantment.class);
   }

   public boolean isValidEnchantment(InventoryItem var1, ItemEnchantment var2) {
      return EnchantmentRegistry.summonItemEnchantments.contains(var2.getID());
   }

   public Point getControllerAttackLevelPos(Level var1, float var2, float var3, PlayerMob var4, InventoryItem var5) {
      Line2D.Float var6 = new Line2D.Float(var4.x, var4.y, var4.x + var2 * ((float)Screen.getSceneWidth() / 2.0F - 20.0F), var4.y + var3 * ((float)Screen.getSceneHeight() / 2.0F - 20.0F));
      double var7 = var6.getP1().distance(var6.getP2());
      LineHitbox var9 = new LineHitbox(var6, 200.0F);
      Stream var10000 = Stream.concat(var1.entityManager.mobs.streamInRegionsInRange((float)var4.getX(), (float)var4.getY(), (int)var7), var1.entityManager.players.streamInRegionsInRange((float)var4.getX(), (float)var4.getY(), (int)var7)).filter((var2x) -> {
         return var2x.canBeTargeted(var4, var4.getNetworkClient()) && var9.intersects(var2x.getSelectBox());
      });
      Objects.requireNonNull(var4);
      Mob var10 = (Mob)var10000.min(Comparator.comparingDouble(var4::getDistance)).orElse((Object)null);
      if (var10 != null) {
         Rectangle var11 = var10.getSelectBox();
         return new Point(var11.x + var11.width / 2, var11.y + var11.height / 2);
      } else {
         return super.getControllerAttackLevelPos(var1, var2, var3, var4, var5);
      }
   }

   public ItemControllerInteract getControllerInteract(Level var1, PlayerMob var2, InventoryItem var3, boolean var4, int var5, LinkedList<Rectangle> var6, LinkedList<Rectangle> var7) {
      Point2D.Float var8 = var2.getControllerAimDir();
      Point var9 = this.getControllerAttackLevelPos(var1, var8.x, var8.y, var2, var3);
      return new ItemControllerInteract(var9.x, var9.y) {
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
      runSummonFocus(var1, var2, var3, var4);
      return var6;
   }

   public void showLevelInteract(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.shake, SoundEffect.effect(var4).volume(0.9F).pitch(GameRandom.globalRandom.getFloatBetween(1.5F, 1.7F)));
      }

   }

   protected void beforeSpawn(AttackingFollowingMob var1, InventoryItem var2, PlayerMob var3) {
   }

   public Point2D.Float findSpawnLocation(AttackingFollowingMob var1, Level var2, int var3, int var4, int var5, PlayerMob var6, InventoryItem var7) {
      return findSpawnLocation(var1, var2, var6.x, var6.y);
   }

   public static Point2D.Float findSpawnLocation(Mob var0, Level var1, float var2, float var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            if (var5 != 0 || var6 != 0) {
               float var7 = var2 + (float)(var5 * 32);
               float var8 = var3 + (float)(var6 * 32);
               if (!var0.collidesWith(var1, (int)var7, (int)var8)) {
                  var4.add(new Point2D.Float(var7, var8));
               }
            }
         }
      }

      if (var4.size() > 0) {
         return (Point2D.Float)var4.get(GameRandom.globalRandom.nextInt(var4.size()));
      } else {
         return new Point2D.Float(var2, var3);
      }
   }

   public void showAttack(Level var1, int var2, int var3, AttackAnimMob var4, int var5, InventoryItem var6, int var7, PacketReader var8) {
      if (var1.isClient()) {
         Screen.playSound(GameResources.magicbolt4, SoundEffect.effect(var4).volume(0.5F).pitch(GameRandom.globalRandom.getFloatBetween(1.4F, 1.5F)));
      }

   }

   public InventoryItem onAttack(Level var1, int var2, int var3, PlayerMob var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      if (var1.isServer()) {
         this.runSummon(var1, var2, var3, var4.getServerClient(), var5, var6, var7, var8, var9, var10);
      }

      if (this.singleUse) {
         var6.setAmount(var6.getAmount() - 1);
      }

      return var6;
   }

   public void runSummon(Level var1, int var2, int var3, ServerClient var4, int var5, InventoryItem var6, PlayerInventorySlot var7, int var8, int var9, PacketReader var10) {
      AttackingFollowingMob var11 = (AttackingFollowingMob)MobRegistry.getMob(this.mobStringID, var1);
      this.summonMob(var4, var11, var2, var3, var5, var6);
   }

   public void summonMob(ServerClient var1, AttackingFollowingMob var2, int var3, int var4, int var5, InventoryItem var6) {
      var1.addFollower(this.summonType, var2, this.followPosition, "summonedmob", this.getSummonSpaceTaken(var6, var1.playerMob), (var2x) -> {
         return this.getMaxSummons(var6, var2x);
      }, (BiConsumer)null, false);
      Point2D.Float var7 = this.findSpawnLocation(var2, var2.getLevel(), var3, var4, var5, var1.playerMob, var6);
      var2.updateDamage(this.getAttackDamage(var6));
      var2.setEnchantment(this.getEnchantment(var6));
      this.beforeSpawn(var2, var6, var1.playerMob);
      var2.getLevel().entityManager.addMob(var2, var7.x, var7.y);
   }

   public void draw(InventoryItem var1, PlayerMob var2, int var3, int var4, boolean var5) {
      super.draw(var1, var2, var3, var4, var5);
      if (this.drawMaxSummons && var5) {
         int var6 = this.getMaxSummons(var1, var2);
         if (var6 > 999) {
            var6 = 999;
         }

         if (var6 != 1) {
            String var7 = String.valueOf(var6);
            int var8 = FontManager.bit.getWidthCeil(var7, tipFontOptions);
            FontManager.bit.drawString((float)(var3 + 28 - var8), (float)(var4 + 16), var7, tipFontOptions);
         }
      }

   }

   public static Mob getNextSummonFocus(Level var0, int var1, int var2, PlayerMob var3) {
      if (var0.isClient()) {
         Mob var4 = (Mob)var0.entityManager.streamAreaMobsAndPlayersTileRange(var1, var2, 10).filter((var3x) -> {
            return var3x.canBeTargeted(var3, var3.getNetworkClient()) && var3x.getSelectBox().contains(var1, var2);
         }).findFirst().orElse((Object)null);
         if (var4 == null) {
            var4 = (Mob)var0.entityManager.streamAreaMobsAndPlayersTileRange(var1, var2, 10).filter((var1x) -> {
               return var1x.canBeTargeted(var3, var3.getNetworkClient());
            }).map((var2x) -> {
               Rectangle var3 = var2x.getSelectBox();
               return new ObjectValue(var2x, (new Point2D.Double(var3.getCenterX(), var3.getCenterY())).distance((double)var1, (double)var2));
            }).filter((var0x) -> {
               return (Double)var0x.value < 75.0;
            }).findBestDistance(1, Comparator.comparingDouble((var0x) -> {
               return (Double)var0x.value;
            })).map((var0x) -> {
               return (Mob)var0x.object;
            }).orElse((Object)null);
         }

         return var4;
      } else {
         return null;
      }
   }

   public static void runSummonFocus(Level var0, int var1, int var2, PlayerMob var3) {
      if (var0.isClient()) {
         Mob var4 = getNextSummonFocus(var0, var1, var2, var3);
         ClientClient var5 = var0.getClient().getClient();
         if (var5 != null) {
            var5.summonFocusMobUniqueID = var4 == null ? -1 : var4.getUniqueID();
            var0.getClient().network.sendPacket(new PacketSummonFocus(var0.getClient().getSlot(), var4));
         }
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public ItemEnchantment getRandomEnchantment(GameRandom var1, InventoryItem var2) {
      return this.getRandomEnchantment(var1, var2);
   }
}
