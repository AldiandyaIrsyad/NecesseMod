package necesse.entity.pickup;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.packet.PacketSpawnPickupEntity;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.TickItem;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.ItemPickupText;
import necesse.level.maps.light.GameLight;

public class ItemPickupEntity extends PickupEntity {
   public InventoryItem item;
   public float height;
   public float dh;
   public boolean showsLightBeam;
   protected GameLinkedList<ItemPickupReservedAmount> reservedPickups;
   protected long playerDeathAuth;
   protected int deathInventoryID;
   protected int deathInventorySlot;
   protected boolean deathIsLocked;
   public long nextSpoilTickWorldTime;

   public ItemPickupEntity() {
      this.reservedPickups = new GameLinkedList();
      this.bouncy = 0.75F;
   }

   public ItemPickupEntity(Level var1, InventoryItem var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      super(var1, var3, var4, var5, var6);
      this.reservedPickups = new GameLinkedList();
      this.height = var7;
      this.dh = var8;
      this.item = var2;
      this.item.setLocked(false);
      this.bouncy = 0.75F;
   }

   public ItemPickupEntity(Level var1, InventoryItem var2, float var3, float var4, float var5, float var6) {
      this(var1, var2, var3, var4, var5, var6, GameRandom.globalRandom.getFloatBetween(5.0F, 15.0F), GameRandom.globalRandom.getFloatBetween(20.0F, 30.0F));
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      if (this.playerDeathAuth != 0L) {
         var1.addLong("playerDeathAuth", this.playerDeathAuth);
         var1.addInt("deathInventoryID", this.deathInventoryID);
         var1.addInt("deathInventorySlot", this.deathInventorySlot);
         var1.addBoolean("deathIsLocked", this.deathIsLocked);
      }

      SaveData var2 = new SaveData("ITEM");
      this.item.addSaveData(var2);
      var1.addSaveData(var2);
      var1.addBoolean("showsLightBeam", this.showsLightBeam);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      if (var1.hasLoadDataByName("playerDeathAuth")) {
         this.playerDeathAuth = var1.getLong("playerDeathAuth", -1L);
         this.deathInventoryID = var1.getInt("deathInventoryID", -1);
         this.deathInventorySlot = var1.getInt("deathInventorySlot", -1);
         this.deathIsLocked = var1.getBoolean("deathIsLocked", false);
         if (this.playerDeathAuth == -1L || this.deathInventoryID == -1 || this.deathInventorySlot == -1) {
            this.playerDeathAuth = 0L;
            this.deathInventoryID = 0;
            this.deathInventorySlot = 0;
         }
      }

      LoadData var2 = var1.getFirstLoadDataByName("ITEM");
      if (var2 != null) {
         this.item = InventoryItem.fromLoadData(var2);
      }

      if (this.item == null) {
         System.err.println("Loaded pickup entity on was invalid and removed.");
         this.remove();
      }

      this.showsLightBeam = var1.getBoolean("showsLightBeam", this.showsLightBeam, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.height);
      var1.putNextFloat(this.dh);
      if (this.playerDeathAuth != 0L) {
         var1.putNextBoolean(true);
         var1.putNextLong(this.playerDeathAuth);
         var1.putNextShortUnsigned(this.deathInventoryID);
         var1.putNextShortUnsigned(this.deathInventorySlot);
         var1.putNextBoolean(this.deathIsLocked);
      } else {
         var1.putNextBoolean(false);
      }

      Packet var2 = InventoryItem.getContentPacket(this.item);
      var1.putNextContentPacket(var2);
      var1.putNextBoolean(this.showsLightBeam);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.height = var1.getNextFloat();
      this.dh = var1.getNextFloat();
      if (var1.getNextBoolean()) {
         this.playerDeathAuth = var1.getNextLong();
         this.deathInventoryID = var1.getNextShortUnsigned();
         this.deathInventorySlot = var1.getNextShortUnsigned();
         this.deathIsLocked = var1.getNextBoolean();
      } else {
         this.playerDeathAuth = 0L;
         this.deathInventoryID = 0;
         this.deathInventorySlot = 0;
         this.deathIsLocked = false;
      }

      Packet var2 = var1.getNextContentPacket();
      this.item = InventoryItem.fromContentPacket(var2);
      this.showsLightBeam = var1.getNextBoolean();
   }

   public Rectangle getSelectBox() {
      Rectangle var1 = super.getSelectBox();
      var1.y -= 10 + (int)Math.max(0.0F, this.height);
      float var2 = (float)Math.pow((double)this.sinking, 0.4000000059604645);
      var1.y += (int)(var2 * 24.0F);
      return var1;
   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (this.height != -1.0F) {
         float var2 = 50.0F * var1 / 250.0F;
         this.dh -= var2;
         this.height += this.dh * var1 / 250.0F;
         if (this.height < 0.0F) {
            this.dh = -this.dh * this.bouncy * 0.7F;
            this.height = -this.height;
            if (Math.abs(this.dh) < var2 * 2.0F) {
               this.height = -1.0F;
               this.dh = 0.0F;
            }
         }
      }

   }

   public float getSinkingRate() {
      return this.height > 0.0F ? 0.0F : this.item.item.getSinkingRate(this, this.sinking);
   }

   public float getMaxSinking() {
      return this.item.item.getMaxSinking(this);
   }

   public long getLifespanMillis() {
      WorldSettings var1 = this.getWorldSettings();
      long var2 = (long)((var1 == null ? Settings.droppedItemsLifeMinutes : var1.droppedItemsLifeMinutes) * 60) * 1000L;
      long var4 = this.item.item.getDropDecayTime(this.item);
      if (var4 > 0L && var2 > 0L) {
         return Math.min(var2, var4);
      } else if (var4 > 0L) {
         return var4;
      } else {
         return var2 > 0L ? var2 : 0L;
      }
   }

   public void clientTick() {
      super.clientTick();
      this.item.item.tickPickupEntity(this);
      WorldSettings var1 = this.getWorldSettings();
      if (this.item.item.isTickItem()) {
         ((TickItem)this.item.item).tick((Inventory)null, -1, this.item, this, this, this, var1, (var1x) -> {
            if (var1x == null) {
               this.remove();
            } else {
               this.item = var1x;
               this.markDirty();
            }

         });
      }

      if ((var1 == null || var1.survivalMode) && this.nextSpoilTickWorldTime <= this.getWorldTime() && this.item.item.shouldSpoilTick(this.item)) {
         this.nextSpoilTickWorldTime = this.item.item.tickSpoilTime(this.item, this, 1.0F, (var1x) -> {
            if (var1x == null) {
               this.remove();
            } else {
               this.item = var1x;
               this.markDirty();
            }

         });
      }

   }

   public void serverTick() {
      super.serverTick();
      if (!this.removed()) {
         if (this.item.getAmount() <= 0) {
            this.remove();
         } else {
            this.item.item.tickPickupEntity(this);
            WorldSettings var1 = this.getWorldSettings();
            if (this.item.item.isTickItem()) {
               ((TickItem)this.item.item).tick((Inventory)null, -1, this.item, this, this, this, var1, (var1x) -> {
                  if (var1x == null) {
                     this.remove();
                  } else {
                     this.item = var1x;
                     this.markDirty();
                  }

               });
            }

            if ((var1 == null || var1.survivalMode) && this.nextSpoilTickWorldTime <= this.getWorldTime() && this.item.item.shouldSpoilTick(this.item)) {
               this.nextSpoilTickWorldTime = this.item.item.tickSpoilTime(this.item, this, 1.0F, (var1x) -> {
                  if (var1x == null) {
                     this.remove();
                  } else {
                     this.item = var1x;
                     this.markDirty();
                  }

               });
            }

         }
      }
   }

   public boolean canBePickedUpBySettlers() {
      return this.getTarget() == null && this.getReservedAuth() == -1L;
   }

   public float getTargetRange(ServerClient var1) {
      return super.getTargetRange(var1) + (Float)var1.playerMob.buffManager.getModifier(BuffModifiers.ITEM_PICKUP_RANGE) * 32.0F;
   }

   public float getTargetStreamRange() {
      return super.getTargetStreamRange() + BuffModifiers.MAX_PICKUP_RANGE_MODIFIER * 32.0F;
   }

   public boolean isValidTarget(ServerClient var1) {
      if (this.playerDeathAuth != 0L && this.playerDeathAuth == var1.authentication) {
         PlayerInventorySlot var2 = new PlayerInventorySlot(this.deathInventoryID, this.deathInventorySlot);
         if (var2.isSlotClear(var1.playerMob.getInv())) {
            return true;
         }
      }

      return var1.playerMob.getInv().canAddItem(this.item, false, "itempickup") > 0;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         if (this.shouldDraw() && this.sinking < 1.0F) {
            float var9 = (float)Math.pow((double)this.sinking, 0.4000000059604645);
            GameLight var10 = var5.getLightLevel(this);
            int var11 = var7.getDrawX(this.x);
            int var12 = var7.getDrawY(this.y) - (int)Math.max(0.0F, this.height);
            var12 += this.getBobbing(this.getX(), this.getY());
            final SharedTextureDrawOptions var13;
            int var16;
            if (this.showsLightBeam) {
               float var14 = (float)Math.sqrt((double)(this.dx * this.dx + this.dy * this.dy));
               float var15 = 1.0F - GameMath.limit(var14 / 70.0F, 0.0F, 1.0F);
               if (var15 > 0.0F) {
                  var16 = GameMath.lerp(var15, 0, 75);
                  int var17 = GameMath.lerp(var15, 0, 200);
                  GameRandom var18 = new GameRandom((long)this.getUniqueID());
                  long var19 = var5.getWorldEntity().getLocalTime();
                  var13 = new SharedTextureDrawOptions(GameResources.gradient);
                  GameLight var21 = var10.minLevelCopy(100.0F);
                  this.addLightBeamDraws(var13, var19 + (long)var18.nextInt(1000000), 10000, var16, var17, 290, 340, 8000, var21, var7.getDrawX(this.x), var7.getDrawY(this.y));
                  this.addLightBeamDraws(var13, var19 + (long)var18.nextInt(1000000), 10000, var16, var17, 290, 340, 8000, var21, var7.getDrawX(this.x), var7.getDrawY(this.y));
                  this.addLightBeamDraws(var13, var19 + (long)var18.nextInt(1000000), 10000, var16, var17, 290, 340, 8000, var21, var7.getDrawX(this.x), var7.getDrawY(this.y));
               } else {
                  var13 = null;
               }
            } else {
               var13 = null;
            }

            final DrawOptions var22 = this.item.getWorldDrawOptions(var8, var11, var12, var10, var9);
            var1.add(new EntityDrawable(this) {
               public void draw(TickManager var1) {
                  if (var13 != null) {
                     var13.draw();
                  }

                  var22.draw();
               }
            });
            int var23 = var7.getDrawX(this.x);
            var16 = var7.getDrawY(this.y);
            var16 += this.getBobbing(this.getX(), this.getY());
            DrawOptions var24 = this.item.item.getWorldShadowDrawOptions(this.item, var8, var23, var16, var10, var9);
            if (var24 != null) {
               var2.add((var1x) -> {
                  var24.draw();
               });
            }
         }

      }
   }

   protected void addLightBeamDraws(SharedTextureDrawOptions var1, long var2, int var4, int var5, int var6, int var7, int var8, int var9, GameLight var10, int var11, int var12) {
      float var13 = GameUtils.getAnimFloatContinuous(var2, var4);
      float var14 = (GameMath.sin(var13 * 360.0F) + 1.0F) / 2.0F;
      int var15 = (int)(var14 * (float)(var6 - var5)) + var5;
      int var16 = (int)((float)var15 / 1.5F);
      int var17 = var15 - var16;
      float var18 = GameUtils.getAnimFloatContinuous(var2, var4 - 1000);
      float var19 = (GameMath.sin(var18 * 360.0F) + 1.0F) / 2.0F;
      byte var20 = 5;
      byte var21 = 10;
      int var22 = (int)((float)var21 * var19);
      int var23 = var21 - var22;
      float var24 = (float)var7 / 360.0F;
      float var25 = (float)var8 / 360.0F;
      if (var25 < var24) {
         ++var25;
      }

      float var26 = GameUtils.getAnimFloatContinuous(var2, var9);
      float var27 = (GameMath.sin(var26 * 360.0F) + 1.0F) / 2.0F;
      Color var28 = new Color(Color.HSBtoRGB(GameMath.lerp(var27, var24, var25) % 1.0F, 1.0F, 1.0F));
      this.addLightBeamDraws(var1, var22 + var20, var23 + var20, var16, var17, var28, var10, var11 + var22 / 2 - var23 / 2, var12);
   }

   protected void addLightBeamDraws(SharedTextureDrawOptions var1, int var2, int var3, int var4, int var5, Color var6, GameLight var7, int var8, int var9) {
      float var10 = (float)var6.getRed() / 255.0F;
      float var11 = (float)var6.getGreen() / 255.0F;
      float var12 = (float)var6.getBlue() / 255.0F;
      float var13 = (float)var6.getAlpha() / 255.0F / 4.0F;
      float var14 = var7.getFloatLevel();
      float var15 = var7.getFloatRed();
      float var16 = var7.getFloatGreen();
      float var17 = var7.getFloatBlue();
      float var18 = var10 * var15 * var14;
      float var19 = var11 * var16 * var14;
      float var20 = var12 * var17 * var14;
      float[] var21 = new float[]{var18, var19, var20, 0.0F, var18, var19, var20, 0.0F, var18, var19, var20, var13, var18, var19, var20, var13};
      float[] var22 = new float[]{var18, var19, var20, var13, var18, var19, var20, var13, var18, var19, var20, 0.0F, var18, var19, var20, 0.0F};
      var1.addFull().size(var2, var4).color(var18, var19, var20, var13).mirrorX().pos(var8 - var2, var9 - var4);
      var1.addFull().size(var3, var4).color(var18, var19, var20, var13).pos(var8, var9 - var4);
      var1.addFull().size(var2, var5).advColor(var21).mirrorX().pos(var8 - var2, var9 - var4 - var5);
      var1.addFull().size(var3, var5).advColor(var21).pos(var8, var9 - var4 - var5);
      var1.addFull().size(var2, 8).advColor(var22).mirrorX().pos(var8 - var2, var9);
      var1.addFull().size(var3, 8).advColor(var22).pos(var8, var9);
   }

   public boolean collidesWith(PickupEntity var1) {
      if (var1.getID() != this.getID()) {
         return false;
      } else {
         return this.playerDeathAuth == 0L && ((ItemPickupEntity)var1).playerDeathAuth == 0L ? super.collidesWith(var1) : false;
      }
   }

   public void collidedWith(PickupEntity var1) {
      if (var1 != this && var1.getID() == this.getID() && !var1.removed()) {
         ItemPickupEntity var2 = (ItemPickupEntity)var1;
         if (!this.isOnPickupCooldown() && !var2.isOnPickupCooldown() && var2.getReservedAuth() == this.getReservedAuth() && this.item.canCombine(this.getLevel(), (PlayerMob)null, var2.item, "pickupcombine")) {
            int var3 = var2.item.getAmount();
            this.item.item.onCombine(this.getLevel(), (PlayerMob)null, (Inventory)null, 0, this.item, var2.item, Integer.MAX_VALUE, var2.item.getAmount(), false, "pickupcombine", (InventoryAddConsumer)null);
            int var4 = var3 - var2.item.getAmount();
            this.spawnTime = Math.max(this.spawnTime, var2.spawnTime);
            this.sinking = Math.min(this.sinking, var2.sinking);
            this.onItemUpdated();
            this.playerDeathAuth = 0L;
            this.deathInventoryID = 0;
            this.deathInventorySlot = 0;
            this.deathIsLocked = false;
            if (this.isServer()) {
               this.getLevel().getServer().network.sendToClientsWithEntity(new PacketSpawnPickupEntity(this), this);
            }

            var2.remove();
            if (var4 > 0) {
               Iterator var5 = var2.reservedPickups.iterator();

               while(var5.hasNext()) {
                  ItemPickupReservedAmount var6 = (ItemPickupReservedAmount)var5.next();
                  var6.submitCombinedEvent(new ItemPickupReservedCombinedEvent(var6, this, var4));
               }
            }
         }
      }

   }

   public void onItemUpdated() {
      this.nextSpoilTickWorldTime = 0L;
   }

   public void onPickup(ServerClient var1) {
      int var2 = this.item.getAmount();
      AtomicBoolean var4 = new AtomicBoolean();
      boolean var3;
      if (this.playerDeathAuth != 0L && this.playerDeathAuth == var1.authentication) {
         var3 = var1.playerMob.getInv().addItem(this.item, new PlayerInventorySlot(this.deathInventoryID, this.deathInventorySlot), this.deathIsLocked, "itempickup", (var1x, var2x, var3x) -> {
            if (var3x > 0 && !(var1x instanceof PlayerInventory)) {
               var4.set(true);
            }

         });
      } else {
         this.item.setNew(true);
         var3 = var1.playerMob.getInv().addItem(this.item, false, "itempickup", (var1x, var2x, var3x) -> {
            if (var3x > 0 && !(var1x instanceof PlayerInventory)) {
               var4.set(true);
            }

         });
      }

      if (var3) {
         var1.markObtainItem(this.item.item.getStringID());
         Packet var5 = new Packet();
         PacketWriter var6 = new PacketWriter(var5);
         InventoryItem.addPacketContent(this.item.copy(var2 - this.item.getAmount()), var6);
         var6.putNextBoolean(var4.get());
         if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsAt(new PacketPickupEntityPickup(this, var5), (ServerClient)var1);
         }

         this.resetTarget();
         if (this.item.getAmount() == 0) {
            this.remove();
         } else {
            this.sendTargetUpdatePacket();
         }
      }

   }

   public void onPickup(ClientClient var1, Packet var2) {
      PacketReader var3 = new PacketReader(var2);
      InventoryItem var4 = InventoryItem.fromContentPacket(var3);
      boolean var5 = var3.getNextBoolean();
      if (var4 != null) {
         int var6 = var4.getAmount();
         if (this.playerDeathAuth != 0L && this.playerDeathAuth == var1.authentication) {
            var1.playerMob.getInv().addItem(var4, new PlayerInventorySlot(this.deathInventoryID, this.deathInventorySlot), this.deathIsLocked, "itempickup", (InventoryAddConsumer)null);
         } else {
            var4.setNew(true);
            var1.playerMob.getInv().addItem(var4, false, "itempickup", (InventoryAddConsumer)null);
         }

         this.item.setAmount(this.item.getAmount() - var6);
         if (var1.slot == this.getLevel().getClient().getSlot()) {
            if (Settings.showPickupText) {
               this.getLevel().hudManager.addElement((new ItemPickupText(var1.playerMob, new InventoryItem(var4.item, var6))).specialOutline(var5));
            }

            Screen.playSound(GameResources.pop, SoundEffect.effect(var1.playerMob));
         }
      }

   }

   public ItemPickupEntity setPlayerDeathAuth(NetworkClient var1, PlayerInventorySlot var2, boolean var3) {
      if (var1 == null) {
         this.playerDeathAuth = 0L;
         this.deathInventoryID = 0;
         this.deathInventorySlot = 0;
         this.deathIsLocked = false;
      } else {
         this.playerDeathAuth = var1.authentication;
         this.deathInventoryID = var2.inventoryID;
         this.deathInventorySlot = var2.slot;
         this.deathIsLocked = var3;
      }

      return this;
   }

   public void removeInvalidReservedPickups() {
      this.reservedPickups.removeIf((var1) -> {
         return this.item == null || !var1.isReserved(this.getLevel().getWorldEntity());
      });
   }

   public int getReservedAmount() {
      return this.reservedPickups.stream().filter((var1) -> {
         return var1.isReserved(this.getLevel().getWorldEntity());
      }).mapToInt((var0) -> {
         return var0.pickupAmount;
      }).sum();
   }

   public int getAvailableAmount() {
      return this.item.getAmount() - this.getReservedAmount();
   }

   public ItemPickupReservedAmount reservePickupAmount(int var1) {
      this.removeInvalidReservedPickups();
      int var2 = this.getReservedAmount();
      if (var2 >= this.item.getAmount()) {
         return null;
      } else {
         int var3 = Math.min(var1, this.item.getAmount());
         ItemPickupReservedAmount var4 = new ItemPickupReservedAmount(this, var3, var2);
         var4.init(this.reservedPickups.addFirst(var4), this.getLevel().getWorldEntity());
         return var4;
      }
   }

   public boolean onMouseHover(GameCamera var1, PlayerMob var2, boolean var3) {
      super.onMouseHover(var1, var2, var3);
      StringTooltips var4 = new StringTooltips(this.item.getItemDisplayName() + (this.item.getAmount() != 1 ? " (" + this.item.getAmount() + ")" : ""), this.item.item.getRarityColor(this.item));
      if (var3) {
         var4.add("Name: " + this.item.getItemDisplayName());
         var4.add("Amount: " + this.item.getAmount());
         var4.add("StringID: " + this.item.item.getStringID());
         var4.add("Height: " + this.height + ", " + this.dh);
         if (this.playerDeathAuth != 0L) {
            var4.add("Player death: " + this.playerDeathAuth + ", " + this.deathInventoryID + ", " + this.deathInventorySlot);
         }
      }

      Screen.addTooltip(var4, TooltipLocation.INTERACT_FOCUS);
      return true;
   }

   public String toString() {
      return super.toString() + "{" + this.item + "}";
   }
}
