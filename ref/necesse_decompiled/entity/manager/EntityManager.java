package necesse.entity.manager;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketRemovePickupEntity;
import necesse.engine.network.packet.PacketRemoveProjectile;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.packet.PacketSpawnPickupEntity;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.ConcurrentHashMapQueue;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.EntityListsRegionSearch;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.DamagedObjectEntity;
import necesse.entity.Entity;
import necesse.entity.TileDamageResult;
import necesse.entity.TileDamageType;
import necesse.entity.chains.Chain;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.levelEvent.toolItemEvent.ToolItemEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.ParticleOptions;
import necesse.entity.pickup.PickupEntity;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;
import necesse.level.maps.regionSystem.Region;

public class EntityManager {
   public static int MAX_PICKUP_ENTITIES = 1000;
   public final Level level;
   public final Object lock = new Object();
   public final EntityComponentManager componentManager = new EntityComponentManager();
   public final EntityList<Mob> mobs;
   public final EntityRegionList<PlayerMob> players;
   public final EntityList<PickupEntity> pickups;
   public final EntityList<Projectile> projectiles;
   public final EntityList<ObjectEntity> objectEntities;
   public final EntityList<Particle> particles;
   public final ParticleOptions particleOptions;
   public final EntityList<DamagedObjectEntity> damagedObjects;
   public final ArrayList<Chain> chains = new ArrayList();
   public final ArrayList<Trail> trails = new ArrayList();
   protected final ConcurrentHashMap<Integer, LevelEvent> events = new ConcurrentHashMap();
   public int eventCacheTTL = 5000;
   protected final ConcurrentHashMapQueue<Integer, CachedLevelEvent> eventsCache = new ConcurrentHashMapQueue();
   public final ArrayList<GroundPillarHandler<?>> pillarHandlers = new ArrayList();
   private LinkedList<Entity> drawOnMap = new LinkedList();
   private float spawnRateMod;
   private float spawnCapMod;
   private float chaserDistanceMod;
   private Rectangle particlesAllowed = new Rectangle();
   public HashMap<Point, Mob> serverOpenedDoors = new HashMap();
   public final ClientSubmittedHits submittedHits;

   public EntityManager(Level var1) {
      this.level = var1;
      this.submittedHits = new ClientSubmittedHits(this);
      this.mobs = new EntityList(this, true, "mob", (var1x) -> {
         if (var1.isServer() && var1x.shouldSendSpawnPacket()) {
            var1.getServer().network.sendToClientsWithEntity(new PacketSpawnMob(var1x), var1x);
         }

      }, (var1x) -> {
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsWithEntity(new PacketRemoveMob(var1x.getUniqueID()), var1x);
         }

      }, Entity::getPositionPoint, (var0) -> {
         return new Mob[var0];
      });
      this.mobs.onHiddenAdded = (var1x) -> {
         var1.entityManager.componentManager.streamAll(OnMobSpawnedListenerEntityComponent.class).forEach((var1xx) -> {
            var1xx.onMobSpawned(var1x);
         });
      };
      this.players = new EntityRegionList(var1, Entity::getPositionPoint);
      this.pickups = new EntityList(this, true, "pickup", (var1x) -> {
         if (var1.isServer() && var1x.shouldSendSpawnPacket()) {
            var1.getServer().network.sendToClientsWithEntity(new PacketSpawnPickupEntity(var1x), var1x);
         }

      }, (var1x) -> {
         if (var1.isServer()) {
            var1.getServer().network.sendToClientsWithEntity(new PacketRemovePickupEntity(var1x.getUniqueID()), var1x);
         }

      }, Entity::getPositionPoint, (var0) -> {
         return new PickupEntity[var0];
      });
      this.projectiles = new EntityList(this, true, "projectile", (var1x) -> {
         if (var1.isServer() && var1x.shouldSendSpawnPacket()) {
            var1.getServer().network.sendToClientsWithEntity(new PacketSpawnProjectile(var1x), var1x);
         }

      }, (var1x) -> {
         if (var1.isServer() && var1x.sendRemovePacket) {
            var1.getServer().network.sendToClientsWithEntity(new PacketRemoveProjectile(var1x.getUniqueID()), var1x);
         }

      }, Entity::getPositionPoint, (var0) -> {
         return new Projectile[var0];
      });
      this.projectiles.onHiddenAdded = (var2) -> {
         if (var1.isServer()) {
            this.submittedHits.submitNewProjectile(var2);
         }

      };
      this.particles = new EntityList(this, false, "particle", (Consumer)null, (Consumer)null, Entity::getPositionPoint, (var0) -> {
         return new Particle[var0];
      });
      this.particles.cacheTTL = 0;
      this.particleOptions = new ParticleOptions(var1);
      this.objectEntities = new EntityList(this, true, "objectEntity", this::getTileUniqueID, Entity::removed, Entity::dispose, (Consumer)null, (Consumer)null, (Function)null, (var0) -> {
         return new ObjectEntity[var0];
      });
      this.objectEntities.cacheTTL = 0;
      this.damagedObjects = new EntityList(this, false, "damagedObject", this::getTileUniqueID, Entity::removed, Entity::dispose, (Consumer)null, (Consumer)null, (Function)null, (var0) -> {
         return new DamagedObjectEntity[var0];
      });
      this.damagedObjects.cacheTTL = 0;
      this.updateMods();
   }

   public void addChain(Chain var1) {
      synchronized(this.chains) {
         this.chains.add(var1);
      }
   }

   public void addTrail(Trail var1) {
      synchronized(this.trails) {
         this.trails.add(var1);
      }
   }

   public void changeMobLevel(Mob var1, Level var2, int var3, int var4, boolean var5) {
      this.changeMobLevel(var1.getUniqueID(), var2, var3, var4, var5);
   }

   public void changeMobLevel(int var1, Level var2, int var3, int var4, boolean var5) {
      Mob var6 = (Mob)this.mobs.get(var1, false);
      if (var6 != null) {
         Mob var7 = var6.getMount();
         if (var5 && var7 != null) {
            this.changeMobLevel(var7, var2, var3, var4, true);
         } else {
            var6.dismount();
         }

         this.mobs.onRemoved.accept(var6);
         this.mobs.map.remove(var1);
         var2.entityManager.addMob(var6, (float)var3, (float)var4);
      } else {
         System.err.println("Tried to change level of invalid mob unique id " + var1 + " from " + this.level.getIdentifier() + " tp " + var2.getIdentifier());
      }

   }

   public void addMob(Mob var1, float var2, float var3) {
      var1.setPos(var2, var3, true);
      this.mobs.add(var1);
   }

   public void addParticle(Particle var1, Particle.GType var2) {
      this.addParticle(var1, false, var2);
   }

   public void addParticle(Particle var1, boolean var2, Particle.GType var3) {
      if (!this.level.isServer()) {
         if (var3.canAdd(this.level)) {
            if (var2 || this.particlesAllowed.contains(var1.getX(), var1.getY())) {
               this.particles.add(var1);
            }
         }
      }
   }

   public ParticleOption addParticle(ParticleOption var1, Particle.GType var2) {
      if (var2 != null) {
         if (!var2.canAdd(this.level)) {
            return var1;
         }

         Point2D.Float var3 = var1.getLevelPos();
         if (!this.particlesAllowed.contains((double)var3.x, (double)var3.y)) {
            return var1;
         }
      }

      this.particleOptions.add(var1);
      return var1;
   }

   public ParticleOption addParticle(Supplier<Point2D.Float> var1, float var2, float var3, Particle.GType var4) {
      return this.addParticle(ParticleOption.standard(var2, var3).snapPosition(var1), var4);
   }

   public ParticleOption addParticle(Entity var1, float var2, float var3, Particle.GType var4) {
      return this.addParticle(ParticleOption.standard(var2, var3).snapPosition(var1), var4);
   }

   public ParticleOption addParticle(Supplier<Point2D.Float> var1, Particle.GType var2) {
      return this.addParticle(var1, 0.0F, 0.0F, var2);
   }

   public ParticleOption addParticle(Entity var1, Particle.GType var2) {
      return this.addParticle(var1, 0.0F, 0.0F, var2);
   }

   public ParticleOption addParticle(float var1, float var2, Particle.GType var3) {
      return this.addParticle(ParticleOption.standard(var1, var2), var3);
   }

   public ParticleOption addTopParticle(ParticleOption var1, Particle.GType var2) {
      if (var2 != null) {
         if (!var2.canAdd(this.level)) {
            return var1;
         }

         Point2D.Float var3 = var1.getLevelPos();
         if (!this.particlesAllowed.contains((double)var3.x, (double)var3.y)) {
            return var1;
         }
      }

      this.particleOptions.addTop(var1);
      return var1;
   }

   public ParticleOption addTopParticle(Supplier<Point2D.Float> var1, float var2, float var3, Particle.GType var4) {
      return this.addTopParticle(ParticleOption.standard(var2, var3).snapPosition(var1), var4);
   }

   public ParticleOption addTopParticle(Entity var1, float var2, float var3, Particle.GType var4) {
      return this.addTopParticle(ParticleOption.standard(var2, var3).snapPosition(var1), var4);
   }

   public ParticleOption addTopParticle(Supplier<Point2D.Float> var1, Particle.GType var2) {
      return this.addTopParticle(var1, 0.0F, 0.0F, var2);
   }

   public ParticleOption addTopParticle(Entity var1, Particle.GType var2) {
      return this.addTopParticle(var1, 0.0F, 0.0F, var2);
   }

   public ParticleOption addTopParticle(float var1, float var2, Particle.GType var3) {
      return this.addTopParticle(ParticleOption.standard(var1, var2), var3);
   }

   public void updateParticlesAllowed(GameCamera var1) {
      this.particlesAllowed = new Rectangle(var1.getX() - 50, var1.getY() - 50, var1.getWidth() + 100, var1.getHeight() + 100);
   }

   private int getTileUniqueID(int var1, int var2) {
      return var1 + var2 * this.level.width + 1;
   }

   private int getTileUniqueID(Entity var1) {
      return this.getTileUniqueID(var1.getX(), var1.getY());
   }

   private int getMaxTileUniqueID() {
      return this.level.width * this.level.height + 1;
   }

   public ObjectEntity getObjectEntity(int var1, int var2) {
      return (ObjectEntity)this.objectEntities.get(this.getTileUniqueID(var1, var2), false);
   }

   public <T extends ObjectEntity> T getObjectEntity(int var1, int var2, Class<T> var3) {
      ObjectEntity var4 = this.getObjectEntity(var1, var2);
      return var4 != null && var3.isAssignableFrom(var4.getClass()) ? (ObjectEntity)var3.cast(var4) : null;
   }

   public void removeObjectEntity(int var1, int var2) {
      ObjectEntity var3 = this.getObjectEntity(var1, var2);
      if (var3 != null) {
         var3.remove();
      }

   }

   public void addLevelEvent(LevelEvent var1) {
      this.addLevelEventHidden(var1);
      if (this.level.isServer()) {
         this.level.getServer().network.sendToClientsWithEntity(new PacketLevelEvent(var1), var1);
      }

   }

   public void addLevelEventHidden(LevelEvent var1) {
      var1.level = this.level;
      int var2 = var1.getUniqueID();
      var1.setUniqueID(var2);
      synchronized(this.lock) {
         this.events.compute(var2, (var2x, var3) -> {
            if (var3 != null && var3 != var1) {
               this.runEventRemoveLogic(var3, false);
            }

            return var1;
         });
         this.componentManager.add(var2, var1);
         if (var1 instanceof LevelBuffsEntityComponent) {
            this.level.buffManager.updateBuffs();
         }
      }

      var1.init();
      if (this.level.isServer()) {
         if (var1 instanceof ToolItemEvent) {
            this.submittedHits.submitNewToolItemEvent((ToolItemEvent)var1);
         } else if (var1 instanceof MobAbilityLevelEvent) {
            this.submittedHits.submitNewMobAbilityLevelEvent((MobAbilityLevelEvent)var1);
         }
      }

   }

   public LevelEvent getLevelEvent(int var1, boolean var2) {
      synchronized(this.lock) {
         LevelEvent var4 = (LevelEvent)this.events.get(var1);
         if (var4 != null) {
            return var4;
         }
      }

      if (var2) {
         synchronized(this.lock) {
            CachedLevelEvent var9 = (CachedLevelEvent)this.eventsCache.get(var1);
            if (var9 != null) {
               return var9.event;
            }
         }
      }

      return null;
   }

   protected void runEventRemoveLogic(LevelEvent var1, boolean var2) {
      var1.onDispose();
      if (this.level.isServer() && var2 && var1.shouldSendOverPacket()) {
         this.level.getServer().network.sendToClientsAt(new PacketLevelEventOver(var1.getUniqueID()), (Level)this.level);
      }

   }

   public Collection<LevelEvent> getLevelEvents() {
      return this.events.values();
   }

   public DamagedObjectEntity getDamagedObjectEntity(int var1, int var2) {
      return (DamagedObjectEntity)this.damagedObjects.get(this.getTileUniqueID(var1, var2), false);
   }

   public int clearLevelEvents(String var1) {
      synchronized(this.lock) {
         LinkedList var3 = new LinkedList();
         this.events.forEach((var3x, var4x) -> {
            if (var4x == null) {
               var3.add(var3x);
            } else if (var4x.getStringID().contains(var1)) {
               var3.add(var3x);
               var4x.over();
               this.runEventRemoveLogic(var4x, true);
               if (this.eventCacheTTL > 0) {
                  this.eventsCache.addLast(var3x, new CachedLevelEvent(var3x, var4x));
               }
            }

         });
         boolean var4 = false;
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            int var6 = (Integer)var5.next();
            LevelEvent var7 = (LevelEvent)this.events.remove(var6);
            if (var7 != null) {
               this.componentManager.remove(var6, var7);
               if (var7 instanceof LevelBuffsEntityComponent) {
                  var4 = true;
               }
            }
         }

         if (var4) {
            this.level.buffManager.updateBuffs();
         }

         return var3.size();
      }
   }

   public int clearLevelEvents() {
      synchronized(this.lock) {
         int var2 = this.events.size();
         LinkedList var3 = new LinkedList();
         this.events.forEach((var2x, var3x) -> {
            var3.add(var2x);
            var3x.over();
            this.runEventRemoveLogic(var3x, true);
            if (this.eventCacheTTL > 0) {
               this.eventsCache.addLast(var2x, new CachedLevelEvent(var2x, var3x));
            }

         });
         boolean var4 = false;
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            int var6 = (Integer)var5.next();
            LevelEvent var7 = (LevelEvent)this.events.remove(var6);
            if (var7 != null) {
               this.componentManager.remove(var6, var7);
               if (var7 instanceof LevelBuffsEntityComponent) {
                  var4 = true;
               }
            }
         }

         if (var4) {
            this.level.buffManager.updateBuffs();
         }

         return var2;
      }
   }

   public Stream<ModifierValue<?>> getLevelModifiers() {
      return this.componentManager.streamAll(LevelBuffsEntityComponent.class).flatMap(LevelBuffsEntityComponent::getLevelModifiers);
   }

   public Stream<ModifierValue<?>> getMobModifiers(Mob var1) {
      return this.componentManager.streamAll(MobBuffsEntityComponent.class).flatMap((var1x) -> {
         return var1x.getLevelModifiers(var1);
      });
   }

   public void addPillarHandler(GroundPillarHandler<?> var1) {
      synchronized(this.pillarHandlers) {
         var1.level = this.level;
         this.pillarHandlers.add(var1);
      }
   }

   public <T extends Entity> GameAreaStream<T> streamArea(float var1, float var2, int var3, EntityRegionList<? extends T>... var4) {
      return this.streamAreaTileRange((int)var1, (int)var2, var3 / 32 + 1, var4);
   }

   public <T extends Entity> GameAreaStream<T> streamAreaTileRange(int var1, int var2, int var3, EntityRegionList<? extends T>... var4) {
      return (new EntityListsRegionSearch(this.level, (float)var1, (float)var2, var3, var4)).streamEach();
   }

   public GameAreaStream<Mob> streamAreaMobsAndPlayers(float var1, float var2, int var3) {
      return this.streamAreaMobsAndPlayersTileRange((int)var1, (int)var2, var3 / 32 + 1);
   }

   public GameAreaStream<Mob> streamAreaMobsAndPlayersTileRange(int var1, int var2, int var3) {
      return (new EntityListsRegionSearch(this.level, (float)var1, (float)var2, var3, new EntityRegionList[]{this.mobs.regionList, this.players})).streamEach();
   }

   public void forEachRegionDrawStreams(int var1, int var2, Consumer<Stream<? extends Entity>> var3) {
      synchronized(this.lock) {
         var3.accept(this.mobs.getInRegion(var1, var2).stream());
         var3.accept(this.players.getInRegion(var1, var2).stream());
         var3.accept(this.pickups.getInRegion(var1, var2).stream());
         var3.accept(this.projectiles.getInRegion(var1, var2).stream());
         var3.accept(this.particles.getInRegion(var1, var2).stream());
      }
   }

   public void refreshSetLevel() {
      synchronized(this.lock) {
         this.mobs.stream().forEach((var1) -> {
            var1.setLevel(this.level);
         });
         this.pickups.stream().forEach((var1) -> {
            var1.setLevel(this.level);
         });
         this.projectiles.stream().forEach((var1) -> {
            var1.setLevel(this.level);
         });
         this.objectEntities.stream().forEach((var1) -> {
            var1.setLevel(this.level);
         });
         this.particles.stream().forEach((var1) -> {
            var1.setLevel(this.level);
         });
         this.damagedObjects.stream().forEach((var1) -> {
            var1.setLevel(this.level);
         });
      }
   }

   public LinkedList<Entity> getDrawOnMap() {
      return this.drawOnMap;
   }

   public void frameTick(TickManager var1) {
      this.mobs.frameTick(var1, Mob::tickMovement);
      this.pickups.frameTick(var1, PickupEntity::tickMovement);
      this.projectiles.frameTick(var1, Projectile::tickMovement);
      this.particles.frameTick(var1, Particle::tickMovement);
      float var2 = var1.getDelta();
      this.particleOptions.tickMovement(var2);
      Performance.record(this.level.tickManager(), "movement", (Runnable)(() -> {
         Performance.record(this.level.tickManager(), "events", (Runnable)(() -> {
            synchronized(this.lock) {
               Iterator var3 = this.events.values().iterator();

               while(var3.hasNext()) {
                  LevelEvent var4 = (LevelEvent)var3.next();
                  if (var4 != null && !var4.isOver()) {
                     var4.tickMovement(var2);
                  }
               }

            }
         }));
      }));
      this.objectEntities.frameTick(var1, ObjectEntity::frameTick);
   }

   public void clientTick() {
      LinkedList var1 = new LinkedList();
      Performance.record(this.level.tickManager(), "mobs", (Runnable)(() -> {
         this.mobs.clientTick(Mob::clientTick, var1);
      }));
      Performance.record(this.level.tickManager(), "pickups", (Runnable)(() -> {
         this.pickups.clientTick(PickupEntity::clientTick, var1);
      }));
      Performance.record(this.level.tickManager(), "projectiles", (Runnable)(() -> {
         this.projectiles.clientTick(Projectile::clientTick, var1);
      }));
      Performance.record(this.level.tickManager(), "particles", (Runnable)(() -> {
         this.particles.clientTick(Particle::clientTick, var1);
      }));
      TickManager var10000 = this.level.tickManager();
      ParticleOptions var10002 = this.particleOptions;
      Objects.requireNonNull(var10002);
      Performance.record(var10000, "particleOpts", (Runnable)(var10002::tick));
      Performance.record(this.level.tickManager(), "chains", (Runnable)(() -> {
         synchronized(this.chains) {
            for(int var2 = 0; var2 < this.chains.size(); ++var2) {
               if (((Chain)this.chains.get(var2)).isRemoved()) {
                  this.chains.remove(var2);
                  --var2;
               }
            }

         }
      }));
      Performance.record(this.level.tickManager(), "trails", (Runnable)(() -> {
         synchronized(this.trails) {
            for(int var2 = 0; var2 < this.trails.size(); ++var2) {
               if (((Trail)this.trails.get(var2)).isRemoved()) {
                  this.trails.remove(var2);
                  --var2;
               }
            }

         }
      }));
      Performance.record(this.level.tickManager(), "pillars", (Runnable)(() -> {
         synchronized(this.pillarHandlers) {
            for(int var2 = 0; var2 < this.pillarHandlers.size(); ++var2) {
               if (((GroundPillarHandler)this.pillarHandlers.get(var2)).tickAndShouldRemove()) {
                  this.pillarHandlers.remove(var2);
                  --var2;
               }
            }

         }
      }));
      Performance.record(this.level.tickManager(), "levelEvents", (Runnable)(() -> {
         synchronized(this.lock) {
            while(!this.eventsCache.isEmpty()) {
               CachedLevelEvent var2 = (CachedLevelEvent)this.eventsCache.getFirst();
               if (!var2.shouldDie()) {
                  break;
               }

               this.eventsCache.removeFirst();
            }

            LinkedList var9 = new LinkedList();
            this.events.forEach((var2x, var3x) -> {
               if (var3x == null) {
                  var9.add(var2x);
               } else if (var3x.isOver()) {
                  var9.add(var2x);
                  this.runEventRemoveLogic(var3x, true);
                  if (this.eventCacheTTL > 0) {
                     this.eventsCache.addLast(var2x, new CachedLevelEvent(var2x, var3x));
                  }
               } else if (var3x.getUniqueID() != var2x) {
                  GameLog.warn.println("LevelEvent has changed uniqueID from " + var2x + " to " + var3x.getUniqueID() + ", removing it from level");
                  var9.add(var2x);
                  this.runEventRemoveLogic(var3x, true);
               } else {
                  var3x.clientTick();
               }

            });
            boolean var3 = false;
            Iterator var4 = var9.iterator();

            while(var4.hasNext()) {
               int var5 = (Integer)var4.next();
               LevelEvent var6 = (LevelEvent)this.events.remove(var5);
               if (var6 != null) {
                  this.componentManager.remove(var5, var6);
                  if (var6 instanceof LevelBuffsEntityComponent) {
                     var3 = true;
                  }
               }
            }

            if (var3) {
               this.level.buffManager.updateBuffs();
            }

         }
      }));
      Performance.record(this.level.tickManager(), "objectEntities", (Runnable)(() -> {
         this.objectEntities.clientTick(ObjectEntity::clientTick, var1);
      }));
      this.drawOnMap = var1;
   }

   public void serverTick() {
      LinkedList var1 = new LinkedList();
      this.submittedHits.tick();
      Performance.record(this.level.tickManager(), "mobs", (Runnable)(() -> {
         this.mobs.serverTick((var1x) -> {
            if (this.level.tickManager().getTick() == 1 && GameRandom.globalRandom.nextFloat() < this.getDespawnOdds() && var1x.canDespawn()) {
               var1x.remove();
            } else {
               var1x.serverTick();
               if (var1x.isDirty()) {
                  if (var1x.shouldSendSpawnPacket()) {
                     this.level.getServer().network.sendToClientsWithEntity(new PacketSpawnMob(var1x), var1x);
                  }

                  var1x.markClean();
               }

            }
         }, var1);
      }));
      Performance.record(this.level.tickManager(), "pickups", (Runnable)(() -> {
         if (this.pickups.count() > MAX_PICKUP_ENTITIES) {
            int var2 = this.pickups.count() - MAX_PICKUP_ENTITIES;
            PickupEntity[] var3 = (PickupEntity[])this.pickups.array();
            Arrays.sort(var3, Comparator.comparingLong((var0) -> {
               return var0.spawnTime;
            }));

            for(int var4 = 0; var4 < var2; ++var4) {
               var3[var4].remove();
            }
         }

         this.pickups.serverTick((var1x) -> {
            var1x.serverTick();
            if (var1x.isDirty()) {
               if (this.level.isServer()) {
                  this.level.getServer().network.sendToClientsWithEntity(new PacketSpawnPickupEntity(var1x), var1x);
               }

               var1x.markClean();
            }

         }, var1);
      }));
      Performance.record(this.level.tickManager(), "projectiles", (Runnable)(() -> {
         this.projectiles.serverTick(Projectile::serverTick, var1);
      }));
      Performance.record(this.level.tickManager(), "particles", (Runnable)(() -> {
         this.particles.serverTick(Particle::serverTick, var1);
      }));
      Performance.record(this.level.tickManager(), "chains", (Runnable)(() -> {
         synchronized(this.chains) {
            for(int var2 = 0; var2 < this.chains.size(); ++var2) {
               if (((Chain)this.chains.get(var2)).isRemoved()) {
                  this.chains.remove(var2);
                  --var2;
               }
            }

         }
      }));
      Performance.record(this.level.tickManager(), "trails", (Runnable)(() -> {
         synchronized(this.trails) {
            for(int var2 = 0; var2 < this.trails.size(); ++var2) {
               if (((Trail)this.trails.get(var2)).isRemoved()) {
                  this.trails.remove(var2);
                  --var2;
               }
            }

         }
      }));
      Performance.record(this.level.tickManager(), "pillars", (Runnable)(() -> {
         synchronized(this.pillarHandlers) {
            for(int var2 = 0; var2 < this.pillarHandlers.size(); ++var2) {
               if (((GroundPillarHandler)this.pillarHandlers.get(var2)).tickAndShouldRemove()) {
                  this.pillarHandlers.remove(var2);
                  --var2;
               }
            }

         }
      }));
      Performance.record(this.level.tickManager(), "levelEvents", (Runnable)(() -> {
         synchronized(this.lock) {
            while(!this.eventsCache.isEmpty()) {
               CachedLevelEvent var2 = (CachedLevelEvent)this.eventsCache.getFirst();
               if (!var2.shouldDie()) {
                  break;
               }

               this.eventsCache.removeFirst();
            }

            LinkedList var9 = new LinkedList();
            this.events.forEach((var2x, var3x) -> {
               if (var3x == null) {
                  var9.add(var2x);
               } else if (var3x.isOver()) {
                  var9.add(var2x);
                  this.runEventRemoveLogic(var3x, true);
                  if (this.eventCacheTTL > 0) {
                     this.eventsCache.addLast(var2x, new CachedLevelEvent(var2x, var3x));
                  }
               } else if (var3x.getUniqueID() != var2x) {
                  GameLog.warn.println("LevelEvent has changed uniqueID from " + var2x + " to " + var3x.getUniqueID() + ", removing it from level");
                  var9.add(var2x);
                  this.runEventRemoveLogic(var3x, true);
               } else {
                  var3x.serverTick();
               }

            });
            boolean var3 = false;
            Iterator var4 = var9.iterator();

            while(var4.hasNext()) {
               int var5 = (Integer)var4.next();
               LevelEvent var6 = (LevelEvent)this.events.remove(var5);
               if (var6 != null) {
                  this.componentManager.remove(var5, var6);
                  if (var6 instanceof LevelBuffsEntityComponent) {
                     var3 = true;
                  }
               }
            }

            if (var3) {
               this.level.buffManager.updateBuffs();
            }

         }
      }));
      Performance.record(this.level.tickManager(), "damageEntities", (Runnable)(() -> {
         this.damagedObjects.serverTick(DamagedObjectEntity::serverTick, var1);
      }));
      Performance.record(this.level.tickManager(), "objectEntities", (Runnable)(() -> {
         this.objectEntities.serverTick((var1x) -> {
            var1x.serverTick();
            if (var1x.isDirty()) {
               if (this.level.isServer()) {
                  this.level.getServer().network.sendToClientsWithTile(new PacketObjectEntity(var1x), this.level, var1x.getTileX(), var1x.getTileY());
               }

               var1x.markClean();
            }

         }, var1);
      }));
      this.drawOnMap = var1;
   }

   public boolean tickMobSpawning(Server var1, ServerClient var2) {
      return var1.world.settings.disableMobSpawns ? true : (Boolean)Performance.record(this.level.tickManager(), "mobSpawning", (Supplier)(() -> {
         int var3 = (int)this.mobs.streamInRegionsShape(GameUtils.rangeBounds(var2.playerMob.getX(), var2.playerMob.getY(), Mob.MOB_SPAWN_AREA.maxSpawnDistance + 320), 0).filter((var0) -> {
            return var0.isHostile && var0.canDespawn;
         }).count();
         if ((float)var3 < var2.getMobSpawnCap(this.level)) {
            MobSpawnTable var4 = var2.getMobSpawnTable(this.level);
            return this.spawnRandomMob(var1, var2, (var1x) -> {
               return var1x.tile().tile.getMobSpawnTable(var1x, var4);
            }, getMobSpawnTile(this.level, var2.playerMob.getX(), var2.playerMob.getY(), Mob.MOB_SPAWN_AREA, (var1x) -> {
               return var1x.x >= 0 && var1x.y >= 0 && var1x.x < this.level.width && var1x.y < this.level.height && !this.level.isSolidTile(var1x.x, var1x.y) ? this.level.getTile(var1x.x, var1x.y).getMobSpawnPositionTickets(this.level, var1x.x, var1x.y) : 0;
            }));
         } else {
            return true;
         }
      }));
   }

   public boolean tickCritterSpawning(Server var1, ServerClient var2) {
      return (Boolean)Performance.record(this.level.tickManager(), "critterSpawning", (Supplier)(() -> {
         int var3 = (int)this.mobs.streamInRegionsShape(GameUtils.rangeBounds(var2.playerMob.getX(), var2.playerMob.getY(), Mob.CRITTER_SPAWN_AREA.maxSpawnDistance + 320), 0).filter((var0) -> {
            return var0.isCritter && var0.canDespawn;
         }).count();
         if ((float)var3 < var2.getCritterSpawnCap(this.level)) {
            MobSpawnTable var4 = var2.getCritterSpawnTable(this.level);
            return this.spawnRandomMob(var1, var2, var4, getMobSpawnTile(this.level, var2.playerMob.getX(), var2.playerMob.getY(), Mob.CRITTER_SPAWN_AREA, (var1x) -> {
               return var1x.x >= 0 && var1x.y >= 0 && var1x.x < this.level.width && var1x.y < this.level.height && !this.level.isSolidTile(var1x.x, var1x.y) ? 100 : 0;
            }));
         } else {
            return true;
         }
      }));
   }

   private boolean spawnRandomMob(Server var1, ServerClient var2, Function<TilePosition, MobSpawnTable> var3, Point var4) {
      if (var4 != null) {
         MobSpawnTable var5 = (MobSpawnTable)var3.apply(new TilePosition(this.level, var4.x, var4.y));
         return this.spawnRandomMob(var1, var2, var5, var4);
      } else {
         return false;
      }
   }

   private boolean spawnRandomMob(Server var1, ServerClient var2, MobSpawnTable var3, Point var4) {
      int var5 = 0;
      if (var4 != null && var3 != null) {
         while(true) {
            MobChance var6 = var3.getRandomMob(this.level, var2, var4, GameRandom.globalRandom);
            if (var6 == null) {
               return false;
            }

            Mob var7 = var6.getMob(this.level, var2, var4);
            if (var7 != null) {
               Point var8 = var7.getPathMoveOffset();
               if (var7.isValidSpawnLocation(var1, var2, var4.x * 32 + var8.x, var4.y * 32 + var8.y)) {
                  var7.onSpawned(var4.x * 32 + var8.x, var4.y * 32 + var8.y);
                  this.mobs.add(var7);
                  return true;
               }
            }

            var3 = var3.withoutRandomMob(var6);
            ++var5;
         }
      } else {
         return false;
      }
   }

   public static Point getMobSpawnTile(Level var0, int var1, int var2, MobSpawnArea var3, Function<Point, Integer> var4) {
      Point var5 = (Point)Performance.record(var0.tickManager(), "getSpawnPos", (Supplier)(() -> {
         return var4 != null ? var3.getRandomTicketTile(GameRandom.globalRandom, var1 / 32, var2 / 32, var4) : var3.getRandomTile(GameRandom.globalRandom, var1 / 32, var2 / 32);
      }));
      if (var5 != null) {
         int var6 = var5.x * 32 + 16;
         int var7 = var5.y * 32 + 16;
         if (var0.entityManager.players.streamArea((float)var6, (float)var7, var3.minSpawnDistance).anyMatch((var3x) -> {
            return var3x.getDistance((float)var6, (float)var7) < (float)var3.minSpawnDistance;
         })) {
            return null;
         }
      }

      return var5;
   }

   public static Point getMobSpawnTile(Level var0, int var1, int var2, int var3, int var4) {
      double var5 = GameRandom.globalRandom.nextDouble() * Math.PI * 2.0;
      double var7 = 2.0 / (Math.pow((double)var4, 2.0) - Math.pow((double)var3, 2.0));
      double var9 = Math.sqrt(2.0 * GameRandom.globalRandom.nextDouble() / var7 + Math.pow((double)var3, 2.0));
      double var11 = var9 * Math.cos(var5) + (double)var1;
      double var13 = var9 * Math.sin(var5) + (double)var2;
      int var15 = (int)(var11 / 32.0);
      int var16 = (int)(var13 / 32.0);
      return GameUtils.streamServerClients(var0).anyMatch((var3x) -> {
         return var3x.playerMob.getDistance((float)(var15 * 32 + 16), (float)(var16 * 32 + 16)) <= (float)var3;
      }) ? null : new Point(var15, var16);
   }

   public int getSize() {
      return this.mobs.count() + this.pickups.count() + this.damagedObjects.count() + this.projectiles.count() + this.particles.count() + this.objectEntities.count();
   }

   public TileDamageResult doDamageOverride(int var1, int var2, int var3, TileDamageType var4) {
      return this.getOrCreateDamagedObjectEntity(var1, var2).doDamageOverride(var3, var4);
   }

   public TileDamageResult doDamage(int var1, int var2, int var3, TileDamageType var4, int var5, ServerClient var6) {
      return this.getOrCreateDamagedObjectEntity(var1, var2).doDamage(var3, var4, var5, var6);
   }

   public TileDamageResult doDamage(int var1, int var2, int var3, TileDamageType var4, int var5, ServerClient var6, boolean var7, int var8, int var9) {
      return this.getOrCreateDamagedObjectEntity(var1, var2).doDamage(var3, var4, var5, var6, var7, var8, var9);
   }

   public TileDamageResult doDamage(int var1, int var2, int var3, ToolType var4, int var5, ServerClient var6, boolean var7, int var8, int var9) {
      return this.getOrCreateDamagedObjectEntity(var1, var2).doDamage(var3, var4, var5, var6, var7, var8, var9);
   }

   public DamagedObjectEntity getOrCreateDamagedObjectEntity(int var1, int var2) {
      DamagedObjectEntity var3 = this.getDamagedObjectEntity(var1, var2);
      if (var3 == null) {
         var3 = new DamagedObjectEntity(this.level, var1, var2);
      }

      this.damagedObjects.add(var3);
      return var3;
   }

   public float getDespawnOdds() {
      return 0.05F;
   }

   public boolean uniqueIDOccupied(int var1) {
      if (var1 <= this.getMaxTileUniqueID()) {
         return true;
      } else if (this.mobs.get(var1, false) != null) {
         return true;
      } else if (this.pickups.get(var1, false) != null) {
         return true;
      } else if (this.projectiles.get(var1, false) != null) {
         return true;
      } else {
         return this.events.containsKey(var1);
      }
   }

   public void updateMods() {
      this.spawnRateMod = 1.0F;
      this.spawnCapMod = 1.0F;
      this.chaserDistanceMod = 1.0F;
   }

   public float getChaserDistanceMod() {
      return this.chaserDistanceMod;
   }

   public float getSpawnRate() {
      return this.level.biome.getSpawnRateMod(this.level) * this.spawnRateMod;
   }

   public float getSpawnCapMod() {
      return this.level.biome.getSpawnCapMod(this.level) * this.spawnCapMod;
   }

   public static float getSpawnCap(int var0, float var1, float var2) {
      return (float)(Math.pow((double)var0, 0.3333333432674408) * (double)var1 + (double)var2);
   }

   public void onServerClientLoadedRegion(Region var1, ServerClient var2) {
      Iterator var3 = this.level.entityManager.mobs.getInRegion(var1.regionX, var1.regionY).iterator();

      while(var3.hasNext()) {
         Mob var4 = (Mob)var3.next();
         if (var4.shouldSendSpawnPacket()) {
            var2.sendPacket(new PacketSpawnMob(var4));
         }
      }

      var3 = this.level.entityManager.pickups.getInRegion(var1.regionX, var1.regionY).iterator();

      while(var3.hasNext()) {
         PickupEntity var5 = (PickupEntity)var3.next();
         if (var5.shouldSendSpawnPacket()) {
            var2.sendPacket(new PacketSpawnPickupEntity(var5));
         }
      }

      var3 = this.level.entityManager.projectiles.getInRegion(var1.regionX, var1.regionY).iterator();

      while(var3.hasNext()) {
         Projectile var6 = (Projectile)var3.next();
         if (var6.shouldSendSpawnPacket()) {
            var2.sendPacket(new PacketSpawnProjectile(var6));
         }
      }

   }

   public void onRegionUnloaded(Region var1) {
      Iterator var2 = this.level.entityManager.mobs.getInRegion(var1.regionX, var1.regionY).iterator();

      while(var2.hasNext()) {
         Mob var3 = (Mob)var2.next();
         var3.remove();
      }

      var2 = this.level.entityManager.pickups.getInRegion(var1.regionX, var1.regionY).iterator();

      while(var2.hasNext()) {
         PickupEntity var4 = (PickupEntity)var2.next();
         var4.remove();
      }

      var2 = this.level.entityManager.projectiles.getInRegion(var1.regionX, var1.regionY).iterator();

      while(var2.hasNext()) {
         Projectile var5 = (Projectile)var2.next();
         var5.remove();
      }

   }

   public void onLoadingComplete() {
      this.mobs.onLoadingComplete();
      this.pickups.onLoadingComplete();
      this.projectiles.onLoadingComplete();
      this.objectEntities.onLoadingComplete();
      this.particles.onLoadingComplete();
      this.damagedObjects.onLoadingComplete();
      this.events.values().forEach(LevelEvent::onLoadingComplete);
   }

   public void onUnloading() {
      this.mobs.onUnloading();
      this.pickups.onUnloading();
      this.projectiles.onUnloading();
      this.objectEntities.onUnloading();
      this.particles.onUnloading();
      this.damagedObjects.onUnloading();
      this.events.values().forEach(LevelEvent::onUnloading);
   }

   public void dispose() {
      this.mobs.dispose();
      this.pickups.dispose();
      this.projectiles.dispose();
      this.objectEntities.dispose();
      this.particles.dispose();
      this.damagedObjects.dispose();
      this.events.values().forEach(LevelEvent::onDispose);
   }

   private class CachedLevelEvent {
      public final int uniqueID;
      public final LevelEvent event;
      public final long endTime;

      public CachedLevelEvent(int var2, LevelEvent var3) {
         this.uniqueID = var2;
         this.event = var3;
         this.endTime = EntityManager.this.level.getWorldEntity().getTime() + (long)EntityManager.this.eventCacheTTL;
      }

      public boolean shouldDie() {
         return this.endTime <= EntityManager.this.level.getWorldEntity().getTime();
      }
   }
}
