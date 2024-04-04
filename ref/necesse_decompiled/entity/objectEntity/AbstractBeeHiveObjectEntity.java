package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import necesse.engine.GameTileRange;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.FruitTreeObject;
import necesse.level.maps.Level;

public abstract class AbstractBeeHiveObjectEntity extends ObjectEntity {
   public static int secondsToProduceHoneyPerBee = 10800;
   public static float frameProductionMultiplier = 2.0F;
   public static int frameLifespanSeconds = 1200;
   public static int workerBeeLifespanSeconds = 1200;
   public static int minCooldownForRoamingBee = 2000;
   public static int maxCooldownForRoamingBee = 15000;
   public static int maxRoamingBeesForSpawnTileRange = 14;
   public static int maxRoamingBeesCloseForSpawn = 30;
   public static GameTileRange pollinateTileRange = new GameTileRange(25, new Point[0]);
   public static GameTileRange migrateTileRange = new GameTileRange(5, 50, new Point[0]);
   public long lastProductionTickWorldTime = this.getWorldEntity().getWorldTime();
   public boolean hasQueen;
   public ProductionTimer honey;
   public ProductionTimer frames;
   public ProductionTimer bees;
   public HashSet<Integer> beesOutsideApiaryUniqueIDs = new HashSet();
   public long nextRoamingBeeSpawnTime;

   public AbstractBeeHiveObjectEntity(Level var1, String var2, int var3, int var4) {
      super(var1, var2, var3, var4);
      this.nextRoamingBeeSpawnTime = this.getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(minCooldownForRoamingBee, maxCooldownForRoamingBee);
      this.honey = new ProductionTimer("honey", 0) {
         public int getSecondsForNextProduction() {
            if (AbstractBeeHiveObjectEntity.this.honey.amount < AbstractBeeHiveObjectEntity.this.getMaxStoredHoney() && AbstractBeeHiveObjectEntity.this.bees.amount > 0) {
               float var1 = AbstractBeeHiveObjectEntity.this.frames.amount > 0 ? AbstractBeeHiveObjectEntity.frameProductionMultiplier : 1.0F;
               return (int)((float)(AbstractBeeHiveObjectEntity.secondsToProduceHoneyPerBee / AbstractBeeHiveObjectEntity.this.bees.amount) / var1);
            } else {
               return -1;
            }
         }
      };
      this.frames = new ProductionTimer("frame", 0) {
         public int getSecondsForNextProduction() {
            return AbstractBeeHiveObjectEntity.this.frames.amount > 0 && AbstractBeeHiveObjectEntity.this.bees.amount > 0 ? AbstractBeeHiveObjectEntity.frameLifespanSeconds : -1;
         }

         public boolean shouldResetBufferOnNoProduction() {
            return AbstractBeeHiveObjectEntity.this.frames.amount <= 0 || AbstractBeeHiveObjectEntity.this.bees.amount <= 0;
         }

         public void onProductionTick() {
            AbstractBeeHiveObjectEntity.this.makeProductionChange(this, () -> {
               --this.amount;
            });
         }
      };
      this.bees = new ProductionTimer("bee", 0) {
         public int getSecondsForNextProduction() {
            if (AbstractBeeHiveObjectEntity.this.hasQueen && (AbstractBeeHiveObjectEntity.this.bees.amount < AbstractBeeHiveObjectEntity.this.getMaxBees() || AbstractBeeHiveObjectEntity.this.canCreateQueens())) {
               return (int)((float)AbstractBeeHiveObjectEntity.workerBeeLifespanSeconds * Math.max(1.0F, (float)AbstractBeeHiveObjectEntity.this.bees.amount) / ((float)AbstractBeeHiveObjectEntity.this.getMaxBees() / 2.0F));
            } else {
               return (AbstractBeeHiveObjectEntity.this.hasQueen || AbstractBeeHiveObjectEntity.this.bees.amount <= 0) && AbstractBeeHiveObjectEntity.this.bees.amount <= AbstractBeeHiveObjectEntity.this.getMaxBees() ? -1 : AbstractBeeHiveObjectEntity.workerBeeLifespanSeconds / AbstractBeeHiveObjectEntity.this.bees.amount;
            }
         }

         public void onProductionTick() {
            if (AbstractBeeHiveObjectEntity.this.hasQueen && AbstractBeeHiveObjectEntity.this.canCreateQueens() && AbstractBeeHiveObjectEntity.this.bees.amount == AbstractBeeHiveObjectEntity.this.getMaxBees()) {
               if (AbstractBeeHiveObjectEntity.this.isServer() && AbstractBeeHiveObjectEntity.this.getLevel().isLoadingComplete() && GameRandom.globalRandom.getChance(AbstractBeeHiveObjectEntity.this.getQueenBeeChance())) {
                  AbstractBeeHiveObjectEntity.this.tryMigratingQueenBee();
               }
            } else {
               AbstractBeeHiveObjectEntity.this.makeProductionChange(this, () -> {
                  if (AbstractBeeHiveObjectEntity.this.hasQueen) {
                     ++this.amount;
                  } else {
                     --this.amount;
                  }

               });
            }

         }
      };
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addLong("lastProductionTickWorldTime", this.lastProductionTickWorldTime);
      var1.addBoolean("hasQueen", this.hasQueen);
      this.honey.addSaveData("honey", var1);
      this.bees.addSaveData("bee", var1);
      this.frames.addSaveData("frame", var1);
      var1.addIntArray("beesOutsideApiaryUniqueIDs", this.beesOutsideApiaryUniqueIDs.stream().mapToInt((var0) -> {
         return var0;
      }).toArray());
      var1.addLong("nextRoamingBeeSpawnTime", this.nextRoamingBeeSpawnTime);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.lastProductionTickWorldTime = var1.getLong("lastProductionTickWorldTime", this.lastProductionTickWorldTime);
      this.hasQueen = var1.getBoolean("hasQueen", this.hasQueen);
      this.honey.applyLoadData("honey", var1);
      this.bees.applyLoadData("bee", var1);
      this.frames.applyLoadData("frame", var1);
      int[] var2 = var1.getIntArray("beesOutsideApiaryUniqueIDs", new int[0]);
      this.beesOutsideApiaryUniqueIDs.clear();
      int[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int var6 = var3[var5];
         this.beesOutsideApiaryUniqueIDs.add(var6);
      }

      this.nextRoamingBeeSpawnTime = var1.getLong("nextRoamingBeeSpawnTime", this.nextRoamingBeeSpawnTime);
   }

   public void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      var1.putNextLong(this.lastProductionTickWorldTime);
      var1.putNextBoolean(this.hasQueen);
      this.honey.writePacket(var1);
      this.bees.writePacket(var1);
      this.frames.writePacket(var1);
   }

   public void applyContentPacket(PacketReader var1) {
      super.applyContentPacket(var1);
      this.lastProductionTickWorldTime = var1.getNextLong();
      this.hasQueen = var1.getNextBoolean();
      this.honey.readPacket(var1);
      this.bees.readPacket(var1);
      this.frames.readPacket(var1);
   }

   public void clientTick() {
      super.clientTick();
      this.tickProduction(false);
   }

   public void serverTick() {
      super.serverTick();
      this.tickProduction(false);
      this.tickRoamingBeeSpawn();
   }

   public void tickProduction(boolean var1) {
      long var2 = this.getWorldEntity().getWorldTime() - this.lastProductionTickWorldTime;
      long var4 = var2 / 1000L;
      if (var4 > 0L || var1) {
         ProductionTimer[] var6 = new ProductionTimer[]{this.honey, this.frames, this.bees};
         boolean var7 = false;
         int[] var8 = new int[var6.length];

         int var10;
         for(int var9 = 0; var9 < var6.length; ++var9) {
            var10 = var6[var9].getSecondsForNextProduction();
            var8[var9] = var10;
            if (var10 >= 0) {
               var7 = true;
            } else if (var6[var9].shouldResetBufferOnNoProduction()) {
               var6[var9].buffer = 0;
            }
         }

         if (var7) {
            int[] var15 = new int[var6.length];
            var10 = 0;

            int var11;
            int var12;
            int var13;
            for(var11 = 0; var11 < var6.length; ++var11) {
               var12 = var8[var11];
               if (var12 < 0) {
                  var15[var11] = Integer.MAX_VALUE;
               } else {
                  var13 = var8[var11] - var6[var11].buffer;
                  var15[var11] = var13;
                  if (var13 < var15[var10]) {
                     var10 = var11;
                  }
               }
            }

            if ((long)var15[var10] <= var4) {
               var11 = Math.max(0, var15[var10]);
               var6[var10].buffer = 0;
               var6[var10].onProductionTick();
               this.lastProductionTickWorldTime += (long)var11 * 1000L;

               for(var12 = 0; var12 < var6.length; ++var12) {
                  if (var12 != var10) {
                     var6[var12].buffer += var11;
                  }
               }

               this.tickProduction(true);
               return;
            }

            ProductionTimer[] var16 = var6;
            var12 = var6.length;

            for(var13 = 0; var13 < var12; ++var13) {
               ProductionTimer var14 = var16[var13];
               var14.buffer = (int)((long)var14.buffer + var4);
            }

            this.lastProductionTickWorldTime += var4 * 1000L;
         } else {
            this.lastProductionTickWorldTime = this.getWorldEntity().getWorldTime();
         }
      }

   }

   public void tickRoamingBeeSpawn() {
      if (this.nextRoamingBeeSpawnTime <= this.getWorldEntity().getTime()) {
         this.nextRoamingBeeSpawnTime = this.getWorldEntity().getTime() + (long)GameRandom.globalRandom.getIntBetween(minCooldownForRoamingBee / 4, maxCooldownForRoamingBee / 4);
         if (this.getWorldEntity().isNight()) {
            return;
         }

         if (this.getLevel().rainingLayer.isRaining() && this.getLevel().regionManager.isOutside(this.getTileX(), this.getTileY())) {
            return;
         }

         this.removeInvalidRoamingBees();
         int var1 = this.bees.amount - this.beesOutsideApiaryUniqueIDs.size();
         if (var1 > 0) {
            long var2 = this.getLevel().entityManager.mobs.getInRegionByTileRange(this.getTileX(), this.getTileY(), maxRoamingBeesForSpawnTileRange).stream().filter((var0) -> {
               return var0 instanceof HoneyBeeMob;
            }).filter((var1x) -> {
               return GameMath.diagonalMoveDistance(this.getTileX(), this.getTileY(), var1x.getTileX(), var1x.getTileY()) <= (double)maxRoamingBeesForSpawnTileRange;
            }).count();
            if (var2 < (long)maxRoamingBeesCloseForSpawn) {
               HoneyBeeMob var4 = (HoneyBeeMob)MobRegistry.getMob("honeybee", this.getLevel());
               var4.setApiaryHome(this.getTileX(), this.getTileY());
               if (GameRandom.globalRandom.getChance(0.3F)) {
                  int var5 = (int)(var4.returnToApiaryTime - this.getTime());
                  var4.pollinateTime = this.getTime() + (long)GameRandom.globalRandom.nextInt(var5);
               }

               Point var6 = this.getAdjacentSpawnPos(var4);
               if (var6 != null) {
                  float var10002 = (float)var6.x;
                  this.getLevel().entityManager.addMob(var4, var10002, (float)var6.y);
                  this.beesOutsideApiaryUniqueIDs.add(var4.getUniqueID());
               }
            }
         }
      }

   }

   protected void removeInvalidRoamingBees() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.beesOutsideApiaryUniqueIDs.iterator();

      while(true) {
         int var3;
         HoneyBeeMob var5;
         do {
            Mob var4;
            do {
               if (!var2.hasNext()) {
                  if (!var1.isEmpty()) {
                     HashSet var10001 = this.beesOutsideApiaryUniqueIDs;
                     Objects.requireNonNull(var10001);
                     var1.forEach(var10001::remove);
                     this.makeProductionChange(() -> {
                        this.bees.amount = Math.max(0, this.bees.amount - var1.size());
                     });
                  }

                  return;
               }

               var3 = (Integer)var2.next();
               var4 = (Mob)this.getLevel().entityManager.mobs.get(var3, false);
            } while(!(var4 instanceof HoneyBeeMob));

            var5 = (HoneyBeeMob)var4;
         } while(var5.apiaryHome != null && var5.apiaryHome.x == this.getTileX() && var5.apiaryHome.y == this.getTileY());

         var1.add(var3);
      }
   }

   protected void tryMigratingQueenBee() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = migrateTileRange.getValidTiles(this.getTileX(), this.getTileY()).iterator();

      while(var2.hasNext()) {
         Point var3 = (Point)var2.next();
         AbstractBeeHiveObjectEntity var4 = (AbstractBeeHiveObjectEntity)this.getLevel().entityManager.getObjectEntity(var3.x, var3.y, AbstractBeeHiveObjectEntity.class);
         if (var4 != null && var4.canTakeMigratingQueen()) {
            var1.add(var4);
         }
      }

      if (!var1.isEmpty()) {
         AbstractBeeHiveObjectEntity var10 = (AbstractBeeHiveObjectEntity)GameRandom.globalRandom.getOneOf((List)var1);
         QueenBeeMob var11 = (QueenBeeMob)MobRegistry.getMob("queenbee", this.getLevel());
         var11.setMigrationApiary(var10.getTileX(), var10.getTileY());
         Point var12 = this.getAdjacentSpawnPos(var11);
         if (var12 != null) {
            float var10002 = (float)var12.x;
            this.getLevel().entityManager.addMob(var11, var10002, (float)var12.y);
            this.removeInvalidRoamingBees();
            int var5 = GameMath.limit(GameRandom.globalRandom.getIntOffset(this.bees.amount / 2, this.bees.amount / 6), 0, this.bees.amount);
            if (var5 > 0) {
               int var6 = this.bees.amount - this.beesOutsideApiaryUniqueIDs.size();

               for(int var7 = 0; var7 < Math.min(var5, var6); ++var7) {
                  HoneyBeeMob var8 = (HoneyBeeMob)MobRegistry.getMob("honeybee", this.getLevel());
                  var8.setFollowingQueen(var11);
                  Point var9 = this.getAdjacentSpawnPos(var8);
                  if (var9 != null) {
                     --var6;
                     this.makeProductionChange(() -> {
                        --this.bees.amount;
                     });
                     var10002 = (float)var9.x;
                     this.getLevel().entityManager.addMob(var8, var10002, (float)var9.y);
                  }
               }

               if (var6 < var5) {
                  List var13 = (List)this.beesOutsideApiaryUniqueIDs.stream().map((var1x) -> {
                     return (Mob)this.getLevel().entityManager.mobs.get(var1x, false);
                  }).filter((var0) -> {
                     return var0 instanceof HoneyBeeMob;
                  }).map((var0) -> {
                     return (HoneyBeeMob)var0;
                  }).collect(Collectors.toList());

                  for(int var14 = var6; var14 < var5 && !var13.isEmpty(); ++var14) {
                     HoneyBeeMob var15 = (HoneyBeeMob)var13.remove(GameRandom.globalRandom.nextInt(var13.size()));
                     var15.setFollowingQueen(var11);
                  }
               }

               this.markDirty();
            }
         }
      }

   }

   protected Point getAdjacentSpawnPos(Mob var1) {
      LinkedList var2 = this.getObject().getMultiTile(this.getLevel(), this.getTileX(), this.getTileY()).getAdjacentTiles(this.getTileX(), this.getTileY(), true);
      ArrayList var3 = new ArrayList();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Point var5 = (Point)var4.next();
         Point var6 = new Point(var5.x * 32 + 16, var5.y * 32 + 16);
         if (!var1.collidesWith(this.getLevel(), var6.x, var6.y)) {
            var3.add(var6);
         }
      }

      if (!var3.isEmpty()) {
         return (Point)GameRandom.globalRandom.getOneOf((List)var3);
      } else {
         return null;
      }
   }

   protected void addDebugTooltips(StringTooltips var1) {
      var1.add("Has queen: " + this.hasQueen);
      var1.add("Honey: " + this.honey.amount);
      var1.add("Bees: " + this.bees.amount);
      var1.add("Frames: " + this.frames.amount);
      var1.add("Time since last production tick: " + GameUtils.getTimeStringMillis(this.getWorldEntity().getWorldTime() - this.lastProductionTickWorldTime));
      var1.add("Time for next bee spawn: " + GameUtils.getTimeStringMillis(this.nextRoamingBeeSpawnTime - this.getWorldEntity().getTime()));
      var1.add("Roaming bees: " + this.beesOutsideApiaryUniqueIDs);
      this.honey.addDebugTooltip(var1);
      this.bees.addDebugTooltip(var1);
      this.frames.addDebugTooltip(var1);
   }

   protected void makeProductionChange(Runnable var1) {
      this.makeProductionChange((ProductionTimer)null, var1);
   }

   protected void makeProductionChange(ProductionTimer var1, Runnable var2) {
      int var3 = var1 == this.honey ? -1 : this.honey.getSecondsForNextProduction();
      int var4 = var1 == this.frames ? -1 : this.frames.getSecondsForNextProduction();
      int var5 = var1 == this.bees ? -1 : this.bees.getSecondsForNextProduction();
      var2.run();
      this.honey.adjustProductionBuffer(var3);
      this.frames.adjustProductionBuffer(var4);
      this.bees.adjustProductionBuffer(var5);
   }

   public void onRoamingBeeDied(Mob var1) {
      if (this.beesOutsideApiaryUniqueIDs.remove(var1.getUniqueID())) {
         if (this.bees.amount > 0) {
            this.makeProductionChange(() -> {
               --this.bees.amount;
            });
         }

         this.markDirty();
      }

   }

   public void onRoamingBeeLost(Mob var1) {
      if (this.beesOutsideApiaryUniqueIDs.remove(var1.getUniqueID())) {
         if (this.bees.amount > 0) {
            this.makeProductionChange(() -> {
               --this.bees.amount;
            });
         }

         this.markDirty();
      }

   }

   public void onRoamingBeeReturned(Mob var1) {
      this.beesOutsideApiaryUniqueIDs.remove(var1.getUniqueID());
   }

   public boolean hasQueen() {
      return this.hasQueen;
   }

   public void addQueen() {
      if (!this.hasQueen) {
         this.makeProductionChange(() -> {
            this.hasQueen = true;
         });
         this.markDirty();
      }

   }

   public void migrateQueen(QueenBeeMob var1) {
      if (!this.hasQueen) {
         this.beesOutsideApiaryUniqueIDs.addAll(var1.honeyBeeUniqueIDs);
         this.makeProductionChange(() -> {
            this.hasQueen = true;
            ProductionTimer var10000 = this.bees;
            var10000.amount += var1.honeyBeeUniqueIDs.size();
         });
         this.markDirty();
      }

   }

   public void removeQueen(Mob var1) {
      if (this.hasQueen) {
         this.makeProductionChange(() -> {
            this.hasQueen = false;
         });
         Point var2 = FruitTreeObject.getItemDropPos(this.getTileX(), this.getTileY(), var1);
         this.getLevel().entityManager.pickups.add((new InventoryItem("queenbee")).getPickupEntity(this.getLevel(), (float)var2.x, (float)var2.y));
         this.markDirty();
      }

   }

   public boolean canAddWorkerBee() {
      return this.bees.amount < this.getMaxBees();
   }

   public void addWorkerBee() {
      this.makeProductionChange(() -> {
         ++this.bees.amount;
      });
      this.markDirty();
   }

   public boolean canAddFrame() {
      return this.frames.amount < this.getMaxFrames();
   }

   public void addFrame() {
      this.makeProductionChange(() -> {
         ++this.frames.amount;
      });
      this.markDirty();
   }

   public int getFrameAmount() {
      return this.frames.amount;
   }

   public int getHoneyAmount() {
      return this.honey.amount;
   }

   public int getBeeAmount() {
      return this.bees.amount;
   }

   public void resetHarvestItems() {
      if (this.honey.amount != 0) {
         this.makeProductionChange(() -> {
            this.honey.amount = 0;
         });
         this.markDirty();
      }

   }

   public ArrayList<InventoryItem> getHarvestItems() {
      ArrayList var1 = new ArrayList();
      int var2 = this.getHoneyAmount();
      if (var2 > 0) {
         var1.add(new InventoryItem("honey", var2));
      }

      return var1;
   }

   public ArrayList<InventoryItem> getHarvestSplitItems() {
      ArrayList var1 = new ArrayList();
      int var2 = this.getHoneyAmount();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.add(new InventoryItem("honey"));
      }

      return var1;
   }

   public void harvest(Mob var1) {
      if (!this.isClient()) {
         ArrayList var2 = this.getHarvestSplitItems();
         if (!var2.isEmpty()) {
            Point var3 = FruitTreeObject.getItemDropPos(this.getTileX(), this.getTileY(), var1);
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               InventoryItem var5 = (InventoryItem)var4.next();
               this.getLevel().entityManager.pickups.add(var5.getPickupEntity(this.getLevel(), (float)var3.x, (float)var3.y));
            }

            this.resetHarvestItems();
         }
      }

   }

   public String getInteractTip(PlayerMob var1) {
      return this.getHoneyAmount() > 0 ? Localization.translate("controls", "harvesttip") : Localization.translate("controls", "inspecttip");
   }

   public void interact(PlayerMob var1) {
      if (!this.isClient()) {
         if (this.getHoneyAmount() > 0) {
            this.harvest(var1);
         } else if (var1 != null && var1.isServerClient()) {
            ServerClient var2 = var1.getServerClient();
            if (var2 != null) {
               GameMessageBuilder var3 = new GameMessageBuilder();
               float var4 = (float)this.getBeeAmount() / (float)this.getMaxBees();
               int var5 = this.getTileX() * 32 + 16;
               int var6 = this.getTileY() * 32 + 32;
               if (!this.hasQueen) {
                  var3.append("ui", "missingqueen").append("\n");
               }

               if (var4 <= 0.0F) {
                  if (this.hasQueen) {
                     var3.append("ui", "beesempty");
                  }
               } else if (var4 < 0.25F) {
                  var3.append("ui", "beesfewbees");
               } else if (var4 < 0.5F) {
                  var3.append("ui", "beesunderhalf");
               } else if (var4 < 0.75F) {
                  var3.append("ui", "beesoverhalf");
               } else if (var4 < 1.0F) {
                  var3.append("ui", "beesalmostfull");
               } else {
                  var3.append("ui", "beesfull");
               }

               var2.sendUniqueFloatText(var5, var6, var3, "inspect", 6000);
            }
         }
      }

   }

   public ArrayList<InventoryItem> getDroppedItems() {
      ArrayList var1 = this.getHarvestSplitItems();
      if (this.hasQueen) {
         var1.add(new InventoryItem("queenbee"));
      }

      return var1;
   }

   public abstract int getMaxBees();

   public abstract int getMaxFrames();

   public abstract int getMaxStoredHoney();

   public abstract boolean canCreateQueens();

   public float getQueenBeeChance() {
      return 0.5F;
   }

   public boolean canTakeMigratingQueen() {
      return !this.hasQueen();
   }

   protected abstract class ProductionTimer {
      public String debugName;
      public int amount;
      public int buffer;

      public ProductionTimer(String var2, int var3) {
         this.debugName = var2;
         this.amount = var3;
      }

      public void addSaveData(String var1, SaveData var2) {
         var2.addInt(var1 + "Amount", this.amount);
         var2.addInt(var1 + "Buffer", this.buffer);
      }

      public void applyLoadData(String var1, LoadData var2) {
         this.amount = var2.getInt(var1 + "Amount", this.amount);
         this.buffer = var2.getInt(var1 + "Buffer", this.buffer);
      }

      public void writePacket(PacketWriter var1) {
         var1.putNextInt(this.amount);
         var1.putNextInt(this.buffer);
      }

      public void readPacket(PacketReader var1) {
         this.amount = var1.getNextInt();
         this.buffer = var1.getNextInt();
      }

      public abstract int getSecondsForNextProduction();

      public void adjustProductionBuffer(int var1) {
         if (var1 >= 0) {
            int var2 = this.getSecondsForNextProduction();
            if (var2 >= 0) {
               double var3 = (double)this.buffer / (double)var1;
               this.buffer = (int)((double)var2 * var3);
            }
         }
      }

      public boolean shouldResetBufferOnNoProduction() {
         return true;
      }

      public void onProductionTick() {
         AbstractBeeHiveObjectEntity.this.makeProductionChange(this, () -> {
            ++this.amount;
         });
      }

      public void addDebugTooltip(StringTooltips var1) {
         int var2 = this.getSecondsForNextProduction();
         var1.add(this.debugName + "Tick: " + var2 + ", " + this.buffer + " (" + (var2 - this.buffer) + ")");
      }
   }
}
