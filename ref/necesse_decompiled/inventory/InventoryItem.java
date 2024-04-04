package necesse.inventory;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GameState;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.ItemSave;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.Item;
import necesse.inventory.item.TickItem;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class InventoryItem implements Comparable<InventoryItem> {
   private static final int AMOUNT_OFFSET;
   public final Item item;
   private int amount;
   private boolean isLocked;
   private boolean isNew;
   private GNDItemMap gndData;

   public InventoryItem(Item var1, int var2, boolean var3) {
      Objects.requireNonNull(var1);
      this.item = var1;
      this.setAmount(var2);
      this.setLocked(var3);
      this.gndData = new GNDItemMap();
   }

   public InventoryItem(Item var1, int var2) {
      this(var1, var2, false);
   }

   public InventoryItem(Item var1) {
      this((Item)var1, 1);
   }

   public InventoryItem(String var1, int var2) {
      this(ItemRegistry.getItem(var1), var2);
   }

   public InventoryItem(String var1) {
      this(ItemRegistry.getItem(var1));
   }

   public InventoryItem copy(int var1, boolean var2) {
      InventoryItem var3 = new InventoryItem(this.item, var1, var2);
      var3.isNew = this.isNew;
      var3.gndData = this.gndData.copy();
      return var3;
   }

   public InventoryItem copy(int var1) {
      return this.copy(var1, this.isLocked());
   }

   public InventoryItem copy() {
      return this.copy(this.getAmount());
   }

   public boolean canCombine(Level var1, PlayerMob var2, InventoryItem var3, String var4) {
      return this.item.canCombineItem(var1, var2, this, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public ItemCombineResult combine(Level var1, PlayerMob var2, InventoryItem var3, String var4) {
      return this.combine(var1, var2, (Inventory)null, -1, var3, var4, (InventoryAddConsumer)null);
   }

   public ItemCombineResult combine(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, String var6, InventoryAddConsumer var7) {
      return this.combine(var1, var2, var3, var4, var5, var5.getAmount(), false, var6, var7);
   }

   /** @deprecated */
   @Deprecated
   public ItemCombineResult combine(Level var1, PlayerMob var2, InventoryItem var3, int var4, boolean var5, String var6) {
      return this.combine(var1, var2, (Inventory)null, -1, var3, var4, var5, var6, (InventoryAddConsumer)null);
   }

   public ItemCombineResult combine(Level var1, PlayerMob var2, Inventory var3, int var4, InventoryItem var5, int var6, boolean var7, String var8, InventoryAddConsumer var9) {
      if (this.canCombine(var1, var2, var5, var8)) {
         var6 = Math.min(var5.getAmount(), var6);
         if (var6 <= 0) {
            return ItemCombineResult.failure();
         } else {
            boolean var10 = this.item.onCombine(var1, var2, var3, var4, this, var5, this.item.getStackSize(), var6, var7, var8, var9);
            return var10 ? ItemCombineResult.success() : ItemCombineResult.failure();
         }
      } else {
         return ItemCombineResult.failure();
      }
   }

   public float getBrokerValue() {
      return this.item.getBrokerValue(this) * (float)this.getAmount();
   }

   public int itemStackSize() {
      return this.item.getStackSize();
   }

   public int getAmount() {
      return this.amount - AMOUNT_OFFSET;
   }

   public void setAmount(int var1) {
      this.amount = var1 + AMOUNT_OFFSET;
   }

   public boolean isLocked() {
      return this.isLocked;
   }

   public void setLocked(boolean var1) {
      this.isLocked = var1;
   }

   public boolean isNew() {
      return this.isNew;
   }

   public void setNew(boolean var1) {
      this.isNew = var1;
   }

   public void drawIcon(PlayerMob var1, int var2, int var3, int var4) {
      this.item.drawIcon(this, var1, var2, var3, var4);
   }

   public static Color getSpoilTimeColor(int var0, float var1) {
      byte var2 = 120;
      short var3 = 3600;
      short var4 = 900;
      float var5;
      if (var0 <= var3) {
         var5 = 1.0F - GameMath.limit((float)(var0 - var2) / (float)(var3 - var2), 0.0F, 1.0F);
         return Color.getHSBColor(GameMath.lerp(var5, 60.0F, 0.0F) / 360.0F, 0.85F, var1);
      } else if (var0 <= var3 + var4) {
         var5 = 1.0F - GameMath.limit((float)(var0 - var3) / (float)var4, 0.0F, 1.0F);
         return Color.getHSBColor(0.16666667F, GameMath.lerp(var5, 0.0F, 0.85F), var1);
      } else {
         return null;
      }
   }

   public void draw(PlayerMob var1, int var2, int var3, boolean var4, boolean var5) {
      this.item.draw(this, var1, var2, var3, var5);
      Color var6 = null;
      WorldSettings var7 = var1 == null ? null : var1.getWorldSettings();
      if ((var7 == null || var7.survivalMode) && this.item.shouldSpoilTick(this)) {
         long var8 = this.item.getCurrentSpoilTime(this);
         if (var1 != null && var8 > 0L) {
            long var10 = Math.max(var8 - var1.getWorldTime(), 0L);
            int var12 = (int)(var10 / 1000L);
            var6 = getSpoilTimeColor(var12, 0.85F);
         }
      }

      int var14 = this.getAmount();
      if (var14 > 1 || var6 != null) {
         String var9;
         if (var4 && var14 > 9999) {
            var9 = GameUtils.metricNumber((long)var14, 2, true, RoundingMode.FLOOR, (String)null);
         } else {
            var9 = "" + var14;
         }

         FontOptions var13 = Item.tipFontOptions;
         if (var6 != null) {
            var13 = (new FontOptions(var13)).color(var6);
         }

         int var11 = FontManager.bit.getWidthCeil(var9, var13);
         FontManager.bit.drawString((float)(var2 + 32 - var11), (float)var3, var9, var13);
      }

   }

   public void draw(PlayerMob var1, int var2, int var3, boolean var4) {
      this.draw(var1, var2, var3, true, var4);
   }

   public void draw(PlayerMob var1, int var2, int var3) {
      this.draw(var1, var2, var3, true);
   }

   public DrawOptions getWorldDrawOptions(PlayerMob var1, int var2, int var3, GameLight var4, float var5) {
      return this.item.getWorldDrawOptions(this, var1, var2, var3, var4, var5);
   }

   public DrawOptions getWorldDrawOptions(PlayerMob var1, int var2, int var3, GameLight var4, float var5, int var6) {
      return this.item.getWorldDrawOptions(this, var1, var2, var3, var4, var5, var6);
   }

   public DrawOptions getAttackDrawOptions(Level var1, PlayerMob var2, GameTexture var3, InventoryItem var4, float var5, int var6, int var7, GameLight var8) {
      return this.item.getAttackDrawOptions(this, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public DrawOptions getAttackDrawOptions(Level var1, PlayerMob var2, int var3, float var4, float var5, GameTexture var6, InventoryItem var7, float var8, int var9, int var10, GameLight var11) {
      return this.item.getAttackDrawOptions(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public DrawOptions getAttackDrawOptions(Level var1, PlayerMob var2, int var3, float var4, float var5, GameSprite var6, InventoryItem var7, float var8, int var9, int var10, GameLight var11) {
      return this.item.getAttackDrawOptions(this, var1, var2, var3, var4, var5, var8, var6, var7, var9, var10, var11);
   }

   public Color getDrawColor(PlayerMob var1) {
      return this.item.getDrawColor(this, var1);
   }

   public ListGameTooltips getTooltip(boolean var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = this.item.getTooltips(this, var2, var3);
      if (var1 && this.getAmount() > 9999) {
         var4.add(Localization.translate("itemtooltip", "itemcount", "amount", (Object)this.getAmount()));
      }

      WorldSettings var5 = var2 == null ? null : var2.getWorldSettings();
      if ((var5 == null || var5.survivalMode) && this.item.shouldSpoilTick(this)) {
         long var6 = this.item.getCurrentSpoilTime(this);
         long var8;
         int var10;
         if (var6 > 0L) {
            if (var2 != null) {
               var8 = Math.max(var6 - var2.getWorldTime(), 0L);
               var10 = (int)(var8 / 1000L);
               Color var11 = getSpoilTimeColor(var10, 0.8F);
               if (!Screen.input().isKeyDown(340) && !Screen.input().isKeyDown(344)) {
                  if (var11 != null) {
                     var4.add((Object)(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(var10).translate(), var11)));
                  } else {
                     var4.add((Object)(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(var10).translate())));
                  }
               } else if (var11 != null) {
                  var4.add((Object)(new StringTooltips(ConsumableItem.getSpoilsTimeWithRateMessage(var10, this.item.getCurrentSpoilRateModifier(this)).translate(), var11)));
               } else {
                  var4.add((Object)(new StringTooltips(ConsumableItem.getSpoilsTimeWithRateMessage(var10, this.item.getCurrentSpoilRateModifier(this)).translate())));
               }
            }
         } else if (var6 == 0L) {
            int var12 = (int)((float)this.item.getStartSpoilSeconds(this) * Item.GLOBAL_SPOIL_TIME_MODIFIER);
            Color var9 = getSpoilTimeColor(var12, 0.8F);
            if (var9 != null) {
               var4.add((Object)(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(var12).translate(), var9)));
            } else {
               var4.add((Object)(new StringTooltips(ConsumableItem.getSpoilsTimeMessage(var12).translate())));
            }
         } else if (var2 != null) {
            var8 = Math.max(-var6, 0L);
            var10 = (int)(var8 / 1000L);
            var4.add((Object)(new StringTooltips(ConsumableItem.getSpoilStoppedTimeMessage(var10).translate())));
         }
      }

      return var4;
   }

   public ListGameTooltips getTooltip(PlayerMob var1, GameBlackboard var2) {
      return this.getTooltip(true, var1, var2);
   }

   public GameMessage getItemLocalization() {
      return this.item.getLocalization(this);
   }

   public String getItemDisplayName() {
      return this.item.getDisplayName(this);
   }

   public GNDItemMap getGndData() {
      return this.gndData;
   }

   public void setGndData(GNDItemMap var1) {
      if (var1 != null) {
         this.gndData = var1.copy();
      } else {
         this.gndData = new GNDItemMap();
      }

   }

   public ItemPickupEntity getPickupEntity(Level var1, float var2, float var3, float var4, float var5) {
      return this.item.getPickupEntity(var1, this, var2, var3, var4, var5);
   }

   public ItemPickupEntity getPickupEntity(Level var1, float var2, float var3) {
      Point2D.Float var4 = GameMath.getAngleDir((float)GameRandom.globalRandom.nextInt(360));
      float var5 = GameRandom.globalRandom.getFloatBetween(50.0F, 65.0F);
      return this.getPickupEntity(var1, var2, var3, var4.x * var5, var4.y * var5);
   }

   public boolean combineOrAddToList(Level var1, PlayerMob var2, Collection<InventoryItem> var3, String var4) {
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         InventoryItem var6 = (InventoryItem)var5.next();
         if (var6.canCombine(var1, var2, this, var4)) {
            boolean var7 = var6.item.onCombine(var1, var2, (Inventory)null, -1, var6, this, Integer.MAX_VALUE, this.getAmount(), false, var4, (InventoryAddConsumer)null);
            if (var7 && this.getAmount() <= 0) {
               return true;
            }
         }

         if (this.getAmount() <= 0) {
            break;
         }
      }

      if (this.getAmount() >= 0) {
         var3.add(this);
      }

      return false;
   }

   public static void tickList(GameClock var0, GameState var1, Entity var2, WorldSettings var3, float var4, List<InventoryItem> var5) {
      ListIterator var6 = var5.listIterator();

      while(true) {
         AtomicReference var7;
         do {
            if (!var6.hasNext()) {
               return;
            }

            var7 = new AtomicReference((InventoryItem)var6.next());
            if (((InventoryItem)var7.get()).item.isTickItem()) {
               ((TickItem)((InventoryItem)var7.get()).item).tick((Inventory)null, -1, (InventoryItem)var7.get(), var0, var1, var2, var3, (var2x) -> {
                  if (var2x == null) {
                     var6.remove();
                  } else {
                     var6.set(var2x);
                  }

                  var7.set(var2x);
               });
            }
         } while(var3 != null && !var3.survivalMode);

         if (var7.get() != null && ((InventoryItem)var7.get()).item.shouldSpoilTick((InventoryItem)var7.get())) {
            ((InventoryItem)var7.get()).item.tickSpoilTime((InventoryItem)var7.get(), var0, var4, (var2x) -> {
               if (var2x == null) {
                  var6.remove();
               } else {
                  var6.set(var2x);
               }

               var7.set(var2x);
            });
         }
      }
   }

   public boolean equals(Level var1, InventoryItem var2, boolean var3, boolean var4, String var5) {
      if (this == var2) {
         return true;
      } else if (!this.item.isSameItem(var1, this, var2, var5)) {
         return false;
      } else {
         if (!var3) {
            if (this.getAmount() != var2.getAmount()) {
               return false;
            }

            if (this.isLocked != var2.isLocked) {
               return false;
            }

            if (this.isNew != var2.isNew) {
               return false;
            }
         }

         return var4 || this.item.isSameGNDData(var1, this, var2, var5);
      }
   }

   public boolean equals(Level var1, InventoryItem var2, String var3) {
      return this.equals(var1, var2, false, false, var3);
   }

   public boolean equals(Object var1) {
      return var1 instanceof InventoryItem ? this.equals((Level)null, (InventoryItem)var1, "equals") : super.equals(var1);
   }

   public void addPacketContent(PacketWriter var1) {
      addPacketContent(this, var1);
   }

   public static InventoryItem fromContentPacket(Packet var0) {
      return fromContentPacket(new PacketReader(var0));
   }

   public static InventoryItem fromContentPacket(PacketReader var0) {
      short var1 = var0.getNextShort();
      if (var1 == -1) {
         return null;
      } else {
         int var2 = var0.getNextInt();
         boolean var3 = var0.getNextBoolean();
         Packet var4 = var0.getNextContentPacket();
         Item var5 = ItemRegistry.getItem(var1 & '\uffff');
         if (var5 == null) {
            (new Throwable("Could not find item with ID " + var1)).printStackTrace(System.err);
            return null;
         } else {
            InventoryItem var6 = new InventoryItem(var5, var2);
            var6.isLocked = var3;
            var6.gndData = new GNDItemMap(var4);
            return var6;
         }
      }
   }

   public static Packet getContentPacket(InventoryItem var0) {
      Packet var1 = new Packet();
      PacketWriter var2 = new PacketWriter(var1);
      addPacketContent(var0, var2);
      return var1;
   }

   public static void addPacketContent(InventoryItem var0, PacketWriter var1) {
      if (var0 != null && var0.item != null) {
         var1.putNextShort((short)var0.item.getID());
         var1.putNextInt(var0.getAmount());
         var1.putNextBoolean(var0.isLocked());
         var1.putNextContentPacket(var0.gndData.getContentPacket());
      } else {
         var1.putNextShort((short)-1);
      }

   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("stringID", this.item.getStringID());
      var1.addInt("amount", this.getAmount());
      if (this.gndData.getMapSize() > 0) {
         SaveData var2 = new SaveData("GNDData");
         this.gndData.addSaveData(var2);
         var1.addSaveData(var2);
      }

   }

   public static InventoryItem fromLoadData(LoadData var0) {
      Item var1 = ItemSave.loadItem(var0.getUnsafeString("stringID", (String)null));
      if (var1 == null) {
         return null;
      } else {
         int var2 = var0.getInt("amount", 0);
         if (var2 == 0) {
            return null;
         } else {
            InventoryItem var3 = new InventoryItem(var1, var2);
            LoadData var4 = var0.getFirstLoadDataByName("GNDData");
            if (var4 != null) {
               var3.gndData = new GNDItemMap(var4);
            }

            return var3;
         }
      }
   }

   public int compareTo(InventoryItem var1) {
      int var2 = this.item.compareTo(this, var1);
      return var2 == 0 ? Integer.compare(var1.amount, this.amount) : var2;
   }

   public String toString() {
      return super.toString() + "{" + this.getItemDisplayName() + ", " + this.getAmount() + (this.gndData.getMapSize() > 0 ? ", " + this.gndData.toString() : "") + "}";
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((InventoryItem)var1);
   }

   static {
      AMOUNT_OFFSET = (int)(GameRandom.globalRandom.nextGaussian() * 10000.0);
   }
}
