package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.registries.StaticObjectGameRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.RaiderMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ShirtArmorItem;
import necesse.inventory.item.armorItem.ShoesArmorItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelMap;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class HumanRaiderMob extends HostileMob implements RaiderMob {
   public static StaticObjectGameRegistry<HumanRaiderWeapon> weaponRegistry = new StaticObjectGameRegistry<HumanRaiderWeapon>("HumanRaiderWeapon", 32767) {
      public void registerCore() {
         this.registerObject("woodsword", new HumanRaiderMeleeWeapon("woodsword", 500, 800, 40, new GameDamage(17.0F), 25));
         this.registerObject("coppersword", new HumanRaiderMeleeWeapon("coppersword", 500, 800, 40, new GameDamage(24.0F), 25));
         this.registerObject("ironsword", new HumanRaiderMeleeWeapon("ironsword", 500, 800, 40, new GameDamage(30.0F), 25));
         this.registerObject("demonicsword", new HumanRaiderMeleeWeapon("demonicsword", 500, 800, 40, new GameDamage(35.0F), 25));
         this.registerObject("ivysword", new HumanRaiderMeleeWeapon("ivysword", 500, 800, 40, new GameDamage(38.0F), 25));
         this.registerObject("cutlass", new HumanRaiderMeleeWeapon("cutlass", 500, 800, 40, new GameDamage(43.0F), 25));
         this.registerObject("tungstensword", new HumanRaiderMeleeWeapon("tungstensword", 500, 800, 40, new GameDamage(52.0F), 25));
         this.registerObject("tungstenspear", new HumanRaiderMeleeWeapon("tungstenspear", 500, 800, 40, new GameDamage(56.0F), 25));
         this.registerObject("woodbow", new HumanRaiderProjectileWeapon("woodbow", 500, 1200, 256, "zombiearrow", new GameDamage(16.0F), 20, 50, 480, (var0) -> {
            Screen.playSound(GameResources.bow, SoundEffect.effect(var0));
         }));
         this.registerObject("copperbow", new HumanRaiderProjectileWeapon("copperbow", 500, 1200, 256, "zombiearrow", new GameDamage(22.0F), 20, 60, 480, (var0) -> {
            Screen.playSound(GameResources.bow, SoundEffect.effect(var0));
         }));
         this.registerObject("ironbow", new HumanRaiderProjectileWeapon("ironbow", 500, 1200, 256, "zombiearrow", new GameDamage(27.0F), 20, 70, 480, (var0) -> {
            Screen.playSound(GameResources.bow, SoundEffect.effect(var0));
         }));
         this.registerObject("demonicbow", new HumanRaiderProjectileWeapon("demonicbow", 500, 1200, 320, "zombiearrow", new GameDamage(33.0F), 20, 80, 480, (var0) -> {
            Screen.playSound(GameResources.bow, SoundEffect.effect(var0));
         }));
         this.registerObject("ivybow", new HumanRaiderProjectileWeapon("ivybow", 500, 1200, 320, "zombiearrow", new GameDamage(38.0F), 20, 100, 480, (var0) -> {
            Screen.playSound(GameResources.bow, SoundEffect.effect(var0));
         }));
         this.registerObject("handgun", new HumanRaiderProjectileWeapon("handgun", 500, 1200, 320, "handgunbullet", new GameDamage(35.0F), 20, 400, 480, (var0) -> {
            Screen.playSound(GameResources.handgun, SoundEffect.effect(var0));
         }));
         this.registerObject("tungstenbow", new HumanRaiderProjectileWeapon("tungstenbow", 500, 1200, 320, "zombiearrow", new GameDamage(43.0F), 20, 120, 480, (var0) -> {
            Screen.playSound(GameResources.bow, SoundEffect.effect(var0));
         }));
         this.registerObject("glacialbow", new HumanRaiderProjectileWeapon("glacialbow", 500, 1200, 384, "zombiearrow", new GameDamage(48.0F), 20, 120, 480, (var0) -> {
            Screen.playSound(GameResources.bow, SoundEffect.effect(var0));
         }));
         this.registerObject("spiderboomerang", new HumanRaiderProjectileWeapon("spiderboomerang", 500, 1200, 192, "spiderboomerang", new GameDamage(24.0F), 20, 100, 256, (Consumer)null));
         this.registerObject("bloodbolt", (new HumanRaiderProjectileWeapon("bloodbolt", 500, 1200, 320, "bloodbolt", new GameDamage(30.0F), 20, 100, 384, (var0) -> {
            Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var0));
         })).dropTickets(50));
         this.registerObject("voidapprentice", new HumanRaiderProjectileWeapon((String)null, 500, 1200, 320, "voidapprentice", new GameDamage(35.0F), 20, 100, 384, (var0) -> {
            Screen.playSound(GameResources.magicbolt1, SoundEffect.effect(var0));
         }));
         this.registerObject("quartzstaff", new HumanRaiderProjectileWeapon("quartzstaff", 500, 1200, 384, "quartzbolt", new GameDamage(45.0F), 20, 120, 480, (var0) -> {
            Screen.playSound(GameResources.flick, SoundEffect.effect(var0).pitch(0.8F));
         }));
         this.registerObject("quartzstaff2", new HumanRaiderProjectileWeapon("quartzstaff", 500, 1200, 384, "quartzbolt", new GameDamage(50.0F), 20, 120, 480, (var0) -> {
            Screen.playSound(GameResources.flick, SoundEffect.effect(var0).pitch(0.8F));
         }));
         this.registerObject("iciclestaff", new HumanRaiderProjectileWeapon("iciclestaff", 500, 1200, 384, "iciclestaff", new GameDamage(53.0F), 20, 120, 480, (var0) -> {
            Screen.playSound(GameResources.jingle, SoundEffect.effect(var0).volume(0.6F));
         }));
      }

      protected void onRegister(HumanRaiderWeapon var1, int var2, String var3, boolean var4) {
      }

      protected void onRegistryClose() {
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
         this.onRegister((HumanRaiderWeapon)var1, var2, var3, var4);
      }
   };
   public static LootTable lootTable = new LootTable(new LootItemInterface[]{new LootItemInterface() {
      public void addPossibleLoot(LootList var1, Object... var2) {
         var1.add("coin");
      }

      public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
         HumanRaiderMob var5 = (HumanRaiderMob)LootTable.expectExtra(HumanRaiderMob.class, var4, 0);
         if (var5 != null) {
            int var6 = (int)((float)var5.minCoinDrops * var3);
            int var7 = (int)((float)var5.maxCoinDrops * var3);
            if (var6 > 0 && var7 > 0) {
               int var8 = var2.getIntBetween(var6, var7);
               if (var8 > 0) {
                  var1.add(new InventoryItem("coin", var8));
               }
            }
         }

      }
   }, new LootItemInterface() {
      public void addPossibleLoot(LootList var1, Object... var2) {
         HumanRaiderMob var3 = (HumanRaiderMob)LootTable.expectExtra(HumanRaiderMob.class, var2, 0);
         if (var3 != null) {
            if (var3.weaponID != -1) {
               HumanRaiderWeapon var4 = (HumanRaiderWeapon)HumanRaiderMob.weaponRegistry.getObject(var3.weaponID);
               if (var4 != null && var4.weaponStringID != null && ItemRegistry.itemExists(var4.weaponStringID)) {
                  Item var5 = ItemRegistry.getItem(var4.weaponStringID);
                  if (ItemRegistry.isObtainable(var5.getID()) && var4.dropTickets > 0) {
                     var1.add(var5);
                  }
               }
            }

            if (var3.helmet != null && ItemRegistry.isObtainable(var3.helmet.item.getID()) && var3.helmetDropTickets > 0) {
               var1.add(var3.helmet.item);
            }

            if (var3.chest != null && ItemRegistry.isObtainable(var3.chest.item.getID()) && var3.chestDropTickets > 0) {
               var1.add(var3.chest.item);
            }

            if (var3.boots != null && ItemRegistry.isObtainable(var3.boots.item.getID()) && var3.bootsDropTickets > 0) {
               var1.add(var3.boots.item);
            }
         }

      }

      public void addItems(List<InventoryItem> var1, GameRandom var2, float var3, Object... var4) {
         HumanRaiderMob var5 = (HumanRaiderMob)LootTable.expectExtra(HumanRaiderMob.class, var4, 0);
         if (var2.getChance(0.25F)) {
            TicketSystemList var6 = new TicketSystemList();
            if (var5 != null) {
               if (var5.weaponID != -1) {
                  HumanRaiderWeapon var7 = (HumanRaiderWeapon)HumanRaiderMob.weaponRegistry.getObject(var5.weaponID);
                  if (var7 != null && var7.weaponStringID != null && ItemRegistry.itemExists(var7.weaponStringID)) {
                     Item var8 = ItemRegistry.getItem(var7.weaponStringID);
                     if (ItemRegistry.isObtainable(var8.getID()) && var7.dropTickets > 0) {
                        var6.addObject(var7.dropTickets, new InventoryItem(var8));
                     }
                  }
               }

               if (var5.helmet != null && ItemRegistry.isObtainable(var5.helmet.item.getID()) && var5.helmetDropTickets > 0) {
                  var6.addObject(var5.helmetDropTickets, var5.helmet.copy());
               }

               if (var5.chest != null && ItemRegistry.isObtainable(var5.chest.item.getID()) && var5.chestDropTickets > 0) {
                  var6.addObject(var5.chestDropTickets, var5.chest.copy());
               }

               if (var5.boots != null && ItemRegistry.isObtainable(var5.boots.item.getID()) && var5.bootsDropTickets > 0) {
                  var6.addObject(var5.bootsDropTickets, var5.boots.copy());
               }
            }

            InventoryItem var9 = (InventoryItem)var6.getRandomObject(var2);
            if (var9 != null) {
               var1.add(var9);
            }
         }

      }
   }, new ChanceLootItem(0.01F, "stormingthehamletpart1vinyl"), new ChanceLootItem(0.01F, "stormingthehamletpart2vinyl")});
   public SettlementRaidLevelEvent raidEvent;
   public int lookSeed;
   public HumanLook look;
   public Point preparingTile;
   public Point attackTile;
   public long raidingStartTicks;
   public int raidingGroup;
   public int raidEventUniqueID;
   public boolean isPreparing;
   public boolean isRaiding;
   public float difficultyModifier;
   public int weaponID;
   public InventoryItem helmet;
   public InventoryItem chest;
   public InventoryItem boots;
   public int helmetDropTickets;
   public int chestDropTickets;
   public int bootsDropTickets;
   public int minCoinDrops;
   public int maxCoinDrops;
   public boolean combatTriggered;

   public HumanRaiderMob() {
      super(100);
      this.lookSeed = GameRandom.globalRandom.nextInt();
      this.raidEventUniqueID = 0;
      this.difficultyModifier = 1.0F;
      this.weaponID = -1;
      this.helmetDropTickets = 100;
      this.chestDropTickets = 100;
      this.bootsDropTickets = 100;
      this.combatTriggered = false;
      this.setSpeed(25.0F);
      this.setFriction(3.0F);
      this.setSwimSpeed(1.2F);
      this.collision = new Rectangle(-10, -7, 20, 14);
      this.hitBox = new Rectangle(-14, -12, 28, 24);
      this.selectBox = new Rectangle(-14, -41, 28, 48);
      this.shouldSave = false;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("lookSeed", this.lookSeed);
      var1.addInt("weapon", this.weaponID);
      var1.addInt("armor", this.getArmorFlat());
      SaveData var2;
      if (this.helmet != null) {
         var2 = new SaveData("helmet");
         this.helmet.addSaveData(var2);
         var1.addSaveData(var2);
      }

      if (this.chest != null) {
         var2 = new SaveData("chest");
         this.chest.addSaveData(var2);
         var1.addSaveData(var2);
      }

      if (this.boots != null) {
         var2 = new SaveData("boots");
         this.boots.addSaveData(var2);
         var1.addSaveData(var2);
      }

      var1.addInt("minCoinDrops", this.minCoinDrops);
      var1.addInt("maxCoinDrops", this.maxCoinDrops);
      var1.addBoolean("isPreparing", this.isPreparing);
      var1.addBoolean("isRaiding", this.isRaiding);
      if (this.preparingTile != null) {
         var1.addPoint("preparingTile", this.preparingTile);
      }

      if (this.attackTile != null) {
         var1.addPoint("attackTile", this.attackTile);
      }

      var1.addLong("raidingStartTicks", this.raidingStartTicks);
      var1.addInt("raidingGroup", this.raidingGroup);
      var1.addFloat("difficultyModifier", this.difficultyModifier);
      var1.addInt("raidEventUniqueID", this.raidEventUniqueID);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.lookSeed = var1.getInt("lookSeed", this.lookSeed);
      this.weaponID = var1.getInt("weapon", this.weaponID);
      int var2 = var1.getInt("armor", -1);
      if (var2 >= 0) {
         this.setArmor(var2);
      }

      LoadData var3 = var1.getFirstLoadDataByName("helmet");
      if (var3 != null) {
         this.helmet = InventoryItem.fromLoadData(var3);
      }

      LoadData var4 = var1.getFirstLoadDataByName("chest");
      if (var4 != null) {
         this.chest = InventoryItem.fromLoadData(var4);
      }

      LoadData var5 = var1.getFirstLoadDataByName("boots");
      if (var5 != null) {
         this.boots = InventoryItem.fromLoadData(var5);
      }

      this.minCoinDrops = var1.getInt("minCoinDrops", this.minCoinDrops);
      this.maxCoinDrops = var1.getInt("maxCoinDrops", this.maxCoinDrops);
      this.isPreparing = var1.getBoolean("isPreparing", this.isPreparing);
      this.isRaiding = var1.getBoolean("isRaiding", this.isRaiding);
      this.preparingTile = var1.getPoint("preparingTile", this.preparingTile, false);
      this.attackTile = var1.getPoint("attackTile", this.attackTile, false);
      this.raidingStartTicks = var1.getLong("raidingStartTicks", this.raidingStartTicks);
      this.raidingGroup = var1.getInt("raidingGroup", this.raidingGroup);
      this.difficultyModifier = var1.getFloat("difficultyModifier", this.difficultyModifier);
      this.raidEventUniqueID = var1.getInt("raidEventUniqueID", this.raidEventUniqueID);
      this.updateAIAndLook();
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.lookSeed = var1.getNextInt();
      this.weaponID = var1.getNextInt();
      this.helmet = InventoryItem.fromContentPacket(var1);
      this.chest = InventoryItem.fromContentPacket(var1);
      this.boots = InventoryItem.fromContentPacket(var1);
      this.setArmor(var1.getNextInt());
      this.updateAIAndLook();
      this.isPreparing = var1.getNextBoolean();
      this.isRaiding = var1.getNextBoolean();
      int var2;
      int var3;
      if (var1.getNextBoolean()) {
         var2 = var1.getNextInt();
         var3 = var1.getNextInt();
         this.preparingTile = new Point(var2, var3);
      }

      if (var1.getNextBoolean()) {
         var2 = var1.getNextInt();
         var3 = var1.getNextInt();
         this.attackTile = new Point(var2, var3);
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.lookSeed);
      var1.putNextInt(this.weaponID);
      InventoryItem.addPacketContent(this.helmet, var1);
      InventoryItem.addPacketContent(this.chest, var1);
      InventoryItem.addPacketContent(this.boots, var1);
      var1.putNextInt(this.getArmorFlat());
      var1.putNextBoolean(this.isPreparing);
      var1.putNextBoolean(this.isRaiding);
      var1.putNextBoolean(this.preparingTile != null);
      if (this.preparingTile != null) {
         var1.putNextInt(this.preparingTile.x);
         var1.putNextInt(this.preparingTile.y);
      }

      var1.putNextBoolean(this.attackTile != null);
      if (this.attackTile != null) {
         var1.putNextInt(this.attackTile.x);
         var1.putNextInt(this.attackTile.y);
      }

   }

   public void init() {
      super.init();
      this.updateAIAndLook();
   }

   public void updateAIAndLook() {
      Object var1 = null;
      if (this.weaponID >= 0) {
         var1 = (HumanRaiderWeapon)weaponRegistry.getObject(this.weaponID);
      }

      if (var1 == null) {
         var1 = new HumanRaiderMeleeWeapon((String)null, 500, 1000, 40, new GameDamage(1.0F), 25);
      }

      this.attackAnimTime = ((HumanRaiderWeapon)var1).attackSpeed;
      this.attackCooldown = ((HumanRaiderWeapon)var1).attackCooldown;
      this.ai = new BehaviourTreeAI(this, new HumanRaiderAI((HumanRaiderWeapon)var1), new AIMover(HumanMob.humanPathIterations));
      this.look = new HumanLook(new GameRandom((long)this.lookSeed));
   }

   public void serverTick() {
      super.serverTick();
      if (this.raidEventUniqueID != 0 && !this.combatTriggered && this.isInCombat()) {
         LevelEvent var1 = this.getLevel().entityManager.getLevelEvent(this.raidEventUniqueID, false);
         if (var1 instanceof SettlementRaidLevelEvent) {
            this.combatTriggered = true;
            ((SettlementRaidLevelEvent)var1).triggerCombatEvent();
         }
      }

   }

   public LootTable getLootTable() {
      return lootTable;
   }

   public PathDoorOption getPathDoorOption() {
      return this.getLevel() != null ? this.getLevel().regionManager.CAN_BREAK_OBJECTS_OPTIONS : null;
   }

   public boolean shouldSave() {
      return this.isRaiding || this.isPreparing;
   }

   public boolean canDespawn() {
      return !this.isPreparing && !this.isRaiding ? super.canDespawn() : false;
   }

   public void spawnDeathParticles(float var1, float var2) {
      GameSkin var3 = this.look.getGameSkin(true);

      for(int var4 = 0; var4 < 4; ++var4) {
         this.getLevel().entityManager.addParticle((Particle)(new FleshParticle(this.getLevel(), var3.getBodyTexture(), GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 10.0F, var1, var2)), Particle.GType.IMPORTANT_COSMETIC);
      }

   }

   public void addDrawables(List<MobDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, Level var4, int var5, int var6, TickManager var7, GameCamera var8, PlayerMob var9) {
      super.addDrawables(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      GameLight var10 = var4.getLightLevel(var5 / 32, var6 / 32);
      int var11 = var8.getDrawX(var5) - 22 - 10;
      int var12 = var8.getDrawY(var6) - 44 - 7;
      Point var13 = this.getAnimSprite(var5, var6, this.dir);
      boolean var14 = this.inLiquid(var5, var6);
      if (var14) {
         var13.x = 0;
      }

      var12 += this.getBobbing(var5, var6);
      var12 += this.getLevel().getTile(var5 / 32, var6 / 32).getMobSinkingAmount(this);
      HumanDrawOptions var15 = (new HumanDrawOptions(var4, this.look, true)).sprite(var13).dir(this.dir).light(var10);
      if (var14) {
         var12 -= 10;
         var15.armSprite(2);
         var15.mask(MobRegistry.Textures.boat_mask[var13.y % 4], 0, -7);
      }

      if (this.helmet != null) {
         var15.helmet(this.helmet);
      }

      if (this.chest != null) {
         var15.chestplate(this.chest);
      } else {
         var15.chestplate(ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.look.getShirtColor()));
      }

      if (this.boots != null) {
         var15.boots(this.boots);
      } else {
         var15.boots(ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.look.getShoesColor()));
      }

      float var16 = this.getAttackAnimProgress();
      if (this.isAttacking) {
         HumanRaiderWeapon var17 = null;
         if (this.weaponID >= 0) {
            var17 = (HumanRaiderWeapon)weaponRegistry.getObject(this.weaponID);
         }

         if (var17 != null) {
            var17.setupAttackDraw(this, var15, var5, var6, var10, var16);
         } else {
            ItemAttackDrawOptions var18 = ItemAttackDrawOptions.start(this.dir).armSprite(this.look.getGameSkin(true).getBodyTexture(), 0, 8, 32).swingRotation(var16).light(var10);
            if (this.chest != null) {
               var18.armorSpriteAndColor(this.chest, (PlayerMob)null);
            } else {
               var18.armorSpriteAndColor(ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.look.getShirtColor()), (PlayerMob)null);
            }

            var15.attackAnim(var18, var16);
         }
      }

      final DrawOptions var19 = var15.pos(var11, var12);
      final TextureDrawOptionsEnd var20 = var14 ? MobRegistry.Textures.woodBoat.initDraw().sprite(0, var13.y, 64).light(var10).pos(var11, var12 + 7) : null;
      var1.add(new MobDrawable() {
         public void draw(TickManager var1) {
            if (var20 != null) {
               var20.draw();
            }

            var19.draw();
         }
      });
      this.addShadowDrawables(var2, var5, var6, var10, var8);
   }

   public boolean shouldDrawOnMap() {
      return true;
   }

   public boolean isVisibleOnMap(Client var1, LevelMap var2) {
      return true;
   }

   public Rectangle drawOnMapBox() {
      return new Rectangle(-10, -24, 20, 24);
   }

   public GameTooltips getMapTooltips() {
      return new StringTooltips(this.getDisplayName());
   }

   public void drawOnMap(TickManager var1, int var2, int var3) {
      Point var4 = this.getAnimSprite(this.getDrawX(), this.getDrawY(), this.dir);
      HumanDrawOptions var5 = (new HumanDrawOptions(this.getLevel(), this.look, true)).sprite(var4).dir(this.dir).size(32, 32);
      if (this.helmet != null) {
         var5.helmet(this.helmet);
      }

      if (this.chest != null) {
         var5.chestplate(this.chest);
      } else {
         var5.chestplate(ShirtArmorItem.addColorData(new InventoryItem("shirt"), this.look.getShirtColor()));
      }

      if (this.boots != null) {
         var5.boots(this.boots);
      } else {
         var5.boots(ShoesArmorItem.addColorData(new InventoryItem("shoes"), this.look.getShoesColor()));
      }

      var5.pos(var2 - 15, var3 - 26).draw();
   }

   public void makeRaider(SettlementRaidLevelEvent var1, Point var2, Point var3, long var4, int var6, float var7) {
      this.isPreparing = true;
      this.isRaiding = false;
      this.raidingStartTicks = var4;
      this.raidingGroup = var6;
      this.preparingTile = var2;
      this.attackTile = var3;
      this.difficultyModifier = var7;
      this.raidEventUniqueID = var1 == null ? 0 : var1.getUniqueID();
   }

   public long getRaidingStartTicks() {
      return this.raidingStartTicks;
   }

   public void updateRaidingStartTicks(long var1) {
      this.raidingStartTicks = var1;
   }

   public void setRaidEvent(SettlementRaidLevelEvent var1) {
      this.raidEvent = var1;
   }

   public void raidOver() {
      this.isPreparing = false;
      this.isRaiding = false;
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      super.showAttack(var1, var2, var3, var4);
      if (this.weaponID >= 0) {
         HumanRaiderWeapon var5 = (HumanRaiderWeapon)weaponRegistry.getObject(this.weaponID);
         if (var5 != null) {
            var5.showAttack(this, var1, var2);
         }
      }

   }

   public abstract static class HumanRaiderWeapon implements IDDataContainer {
      public final IDData idData = new IDData();
      public final String weaponStringID;
      public int attackSpeed;
      public int attackCooldown;
      public int attackDistance;
      public int dropTickets = 50;

      public IDData getIDData() {
         return this.idData;
      }

      public HumanRaiderWeapon(String var1, int var2, int var3, int var4) {
         this.weaponStringID = var1;
         this.attackSpeed = var2;
         this.attackCooldown = var3;
         this.attackDistance = var4;
      }

      public HumanRaiderWeapon dropTickets(int var1) {
         this.dropTickets = var1;
         return this;
      }

      public boolean canHitTarget(HumanRaiderMob var1, float var2, float var3, Mob var4) {
         return ChaserAINode.hasLineOfSightToTarget(var1, var2, var3, var4);
      }

      public abstract void attackTarget(HumanRaiderMob var1, Mob var2, float var3);

      public abstract void showAttack(HumanRaiderMob var1, int var2, int var3);

      public abstract void setupAttackDraw(HumanRaiderMob var1, HumanDrawOptions var2, int var3, int var4, GameLight var5, float var6);
   }

   public static class HumanRaiderMeleeWeapon extends HumanRaiderWeapon {
      public GameDamage damage;
      public int knockback;
      public Consumer<HumanRaiderMob> playSound;

      public HumanRaiderMeleeWeapon(String var1, int var2, int var3, int var4, GameDamage var5, int var6, Consumer<HumanRaiderMob> var7) {
         super(var1, var2, var3, var4);
         this.damage = var5;
         this.knockback = var6;
         this.playSound = var7;
      }

      public HumanRaiderMeleeWeapon(String var1, int var2, int var3, int var4, GameDamage var5, int var6) {
         this(var1, var2, var3, var4, var5, var6, (Consumer)null);
      }

      public void attackTarget(HumanRaiderMob var1, Mob var2, float var3) {
         if (var2.getWorldSettings() != null) {
            var3 *= var2.getWorldSettings().difficulty.raiderDamageModifier;
         }

         var2.isServerHit(this.damage.modDamage(var3), var2.x - var1.x, var2.y - var1.y, (float)this.knockback, var1);
      }

      public void showAttack(HumanRaiderMob var1, int var2, int var3) {
         if (this.playSound != null) {
            this.playSound.accept(var1);
         }

      }

      public void setupAttackDraw(HumanRaiderMob var1, HumanDrawOptions var2, int var3, int var4, GameLight var5, float var6) {
         if (this.weaponStringID != null) {
            var2.itemAttack(new InventoryItem(this.weaponStringID), (PlayerMob)null, var6, var1.attackDir.x, var1.attackDir.y);
         } else {
            ItemAttackDrawOptions var7 = ItemAttackDrawOptions.start(var1.dir).armSprite(var1.look.getGameSkin(true).getBodyTexture(), 0, 8, 32).swingRotation(var6).light(var5);
            if (var1.chest != null) {
               var7.armorSpriteAndColor(var1.chest, (PlayerMob)null);
            } else {
               var7.armorSpriteAndColor(ShirtArmorItem.addColorData(new InventoryItem("shirt"), var1.look.getShirtColor()), (PlayerMob)null);
            }

            var2.attackAnim(var7, var6);
         }

      }
   }

   public class HumanRaiderAI<T extends HumanRaiderMob> extends SelectorAINode<T> {
      public final EscapeAINode<T> escapeAINode;
      public final PlayerChaserAI<T> playerChaserAI;
      public final WandererAINode<T> wandererAINode;
      public boolean setChaserRaidingDistance;

      public HumanRaiderAI(final HumanRaiderWeapon var2) {
         this.addChild(this.escapeAINode = new EscapeAINode<T>() {
            public boolean shouldEscape(T var1, Blackboard<T> var2) {
               if (var1.isHostile && !var1.isSummoned && (Boolean)var1.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING)) {
                  return true;
               } else {
                  return !var1.isPreparing && !var1.isRaiding;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean shouldEscape(Mob var1, Blackboard var2) {
               return this.shouldEscape((HumanRaiderMob)var1, var2);
            }
         });
         this.addChild(new AINode<T>() {
            protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
            }

            public void init(T var1, Blackboard<T> var2) {
            }

            public AINodeResult tick(T var1, Blackboard<T> var2) {
               if (!HumanRaiderMob.this.isRaiding) {
                  if (HumanRaiderMob.this.raidingStartTicks > 0L) {
                     --HumanRaiderMob.this.raidingStartTicks;
                  }

                  if (HumanRaiderMob.this.raidingStartTicks <= 0L) {
                     HumanRaiderMob.this.isRaiding = true;
                     if (HumanRaiderMob.this.raidEvent != null) {
                        HumanRaiderMob.this.raidEvent.startRaid();
                     }
                  }
               } else if (HumanRaiderMob.this.attackTile != null) {
                  var2.put(HumanRaiderAI.this.playerChaserAI.targetFinderAINode.baseKey, new Point(var1.attackTile.x * 32 + 16, var1.attackTile.y * 32 + 16));
               } else {
                  var2.put(HumanRaiderAI.this.playerChaserAI.targetFinderAINode.baseKey, (Object)null);
               }

               if (HumanRaiderMob.this.isPreparing && !HumanRaiderMob.this.isRaiding) {
                  Iterator var3 = var2.getLastHits().iterator();

                  while(var3.hasNext()) {
                     AIWasHitEvent var4 = (AIWasHitEvent)var3.next();
                     var1.getLevel().entityManager.mobs.getInRegionByTileRange(var1.getX() / 32, var1.getY() / 32, 25).stream().filter((var0) -> {
                        return var0 instanceof RaiderMob;
                     }).forEach((var1x) -> {
                        var1x.ai.blackboard.submitEvent("startRaid" + HumanRaiderMob.this.raidingGroup, new AIEvent());
                     });
                     HumanRaiderMob.this.isRaiding = true;
                     if (HumanRaiderMob.this.raidEvent != null) {
                        HumanRaiderMob.this.raidEvent.startRaid();
                     }
                  }

                  for(var3 = var2.getLastCustomEvents("startRaid" + HumanRaiderMob.this.raidingGroup).iterator(); var3.hasNext(); HumanRaiderMob.this.isRaiding = true) {
                     AIEvent var5 = (AIEvent)var3.next();
                  }
               }

               if (!HumanRaiderAI.this.setChaserRaidingDistance && HumanRaiderMob.this.isRaiding) {
                  HumanRaiderAI.this.playerChaserAI.targetFinderAINode.distance = new TargetFinderDistance((int)((double)Math.max(var1.getLevel().width, var1.getLevel().height) * GameMath.diagonalDistance) * 32);
                  HumanRaiderAI.this.setChaserRaidingDistance = true;
                  HumanRaiderMob.this.isPreparing = false;
               }

               return AINodeResult.FAILURE;
            }

            // $FF: synthetic method
            // $FF: bridge method
            public AINodeResult tick(Mob var1, Blackboard var2) {
               return this.tick((HumanRaiderMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public void init(Mob var1, Blackboard var2) {
               this.init((HumanRaiderMob)var1, var2);
            }

            // $FF: synthetic method
            // $FF: bridge method
            protected void onRootSet(AINode var1, Mob var2, Blackboard var3) {
               this.onRootSet(var1, (HumanRaiderMob)var2, var3);
            }
         });
         this.addChild(this.playerChaserAI = new PlayerChaserAI<T>(480, var2.attackDistance, false, false) {
            public boolean canHitTarget(T var1, float var2x, float var3, Mob var4) {
               return var2.canHitTarget(var1, var2x, var3, var4);
            }

            public boolean attackTarget(T var1, Mob var2x) {
               if (var1.canAttack()) {
                  var1.attack(var2x.getX(), var2x.getY(), false);
                  var2.attackTarget(var1, var2x, HumanRaiderMob.this.difficultyModifier);
                  return true;
               } else {
                  return false;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean attackTarget(Mob var1, Mob var2x) {
               return this.attackTarget((HumanRaiderMob)var1, var2x);
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean canHitTarget(Mob var1, float var2x, float var3, Mob var4) {
               return this.canHitTarget((HumanRaiderMob)var1, var2x, var3, var4);
            }
         });
         this.playerChaserAI.targetFinderAINode.baseKeyIsJustPreference = true;
         this.playerChaserAI.targetFinderAINode.targetNonBaseWithinRange = 480;
         this.playerChaserAI.targetFinderAINode.loseTargetMinCooldown = 1000;
         this.playerChaserAI.targetFinderAINode.loseTargetMaxCooldown = 2000;
         this.playerChaserAI.chaserAINode.moveIfFailedPath = (var1x, var2x) -> {
            return HumanRaiderMob.this.getDistance(var1x) > 320.0F;
         };
         this.addChild(this.wandererAINode = new WandererAINode<T>(40000) {
            public Point getBase(T var1) {
               if (var1.isRaiding) {
                  return null;
               } else {
                  return var1.isPreparing ? var1.preparingTile : null;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public Point getBase(Mob var1) {
               return this.getBase((HumanRaiderMob)var1);
            }
         });
         this.wandererAINode.searchRadius = 5;
      }
   }

   public static class HumanRaiderProjectileWeapon extends HumanRaiderWeapon {
      public String projectileStringID;
      public GameDamage damage;
      public int knockback;
      public int projectileSpeed;
      public int projectileDistance;
      public Consumer<HumanRaiderMob> playSound;

      public HumanRaiderProjectileWeapon(String var1, int var2, int var3, int var4, String var5, GameDamage var6, int var7, int var8, int var9, Consumer<HumanRaiderMob> var10) {
         super(var1, var2, var3, var4);
         this.projectileStringID = var5;
         this.projectileSpeed = var8;
         this.projectileDistance = var9;
         this.damage = var6;
         this.knockback = var7;
         this.playSound = var10;
      }

      public void attackTarget(HumanRaiderMob var1, Mob var2, float var3) {
         if (var2.getWorldSettings() != null) {
            var3 *= var2.getWorldSettings().difficulty.raiderDamageModifier;
         }

         Projectile var4 = ProjectileRegistry.getProjectile(this.projectileStringID, var1.getLevel(), var1.x, var1.y, var2.x, var2.y, (float)this.projectileSpeed, this.projectileDistance, this.damage.modDamage(var3), this.knockback, var1);
         if (var2.isPlayer) {
            var4.setTargetPrediction(var2, -10.0F);
         }

         var4.moveDist(10.0);
         var1.getLevel().entityManager.projectiles.add(var4);
      }

      public void showAttack(HumanRaiderMob var1, int var2, int var3) {
         if (this.playSound != null) {
            this.playSound.accept(var1);
         }

      }

      public void setupAttackDraw(HumanRaiderMob var1, HumanDrawOptions var2, int var3, int var4, GameLight var5, float var6) {
         if (this.weaponStringID != null) {
            var2.itemAttack(new InventoryItem(this.weaponStringID), (PlayerMob)null, var6, var1.attackDir.x, var1.attackDir.y);
         } else {
            ItemAttackDrawOptions var7 = ItemAttackDrawOptions.start(var1.dir).armSprite(var1.look.getGameSkin(true).getBodyTexture(), 0, 8, 32).pointRotation(var1.attackDir.x, var1.attackDir.y).light(var5);
            if (var1.chest != null) {
               var7.armorSpriteAndColor(var1.chest, (PlayerMob)null);
            } else {
               var7.armorSpriteAndColor(ShirtArmorItem.addColorData(new InventoryItem("shirt"), var1.look.getShirtColor()), (PlayerMob)null);
            }

            var2.attackAnim(var7, var6);
         }

      }
   }
}
