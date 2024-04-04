package necesse.entity.mobs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketQuestGiverUpdate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.quest.QuestManager;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.light.GameLight;

public interface QuestGiver {
   QuestGiverObject getQuestGiverObject();

   List<Quest> getGivenQuests(ServerClient var1);

   default void applyQuestGiverUpdatePacket(PacketQuestGiverUpdate var1, Client var2) {
      QuestGiverObject var3 = this.getQuestGiverObject();
      synchronized(var3.questLock) {
         var3.clientQuests.clear();
         int[] var5 = var1.questUniqueIDs;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var5[var7];
            var3.clientQuests.add(new GivenQuest(var8, var2.quests));
         }

         var3.clientUpdateTicker = 0;
      }
   }

   static DrawOptions getMarkerDrawOptions(char var0, Color var1, int var2, int var3, GameLight var4, GameCamera var5, int var6, int var7) {
      return getMarkerDrawOptions(Character.toString(var0), var1, var2, var3, var4, var5, var6, var7);
   }

   static DrawOptions getMarkerDrawOptions(String var0, Color var1, int var2, int var3, GameLight var4, GameCamera var5, int var6, int var7) {
      if (!Settings.showQuestMarkers) {
         return () -> {
         };
      } else {
         float var8 = var4.getFloatLevel();
         float var9 = 1.0F;
         if (var8 < 0.5F) {
            var9 = var8 * 2.0F;
         }

         FontOptions var10 = (new FontOptions(32)).outline().color(var1).alphaf(var9);
         int var12;
         if (var0.length() == 1) {
            char var14 = var0.charAt(0);
            var12 = var5.getDrawX(var2) + var6 - FontManager.bit.getWidthCeil(var14, var10) / 2;
            int var13 = var5.getDrawY(var3) + var7 - FontManager.bit.getHeightCeil(var14, var10);
            return () -> {
               FontManager.bit.drawChar((float)var12, (float)var13, var14, var10);
            };
         } else {
            int var11 = var5.getDrawX(var2) + var6 - FontManager.bit.getWidthCeil(var0, var10) / 2;
            var12 = var5.getDrawY(var3) + var7 - FontManager.bit.getHeightCeil(var0, var10);
            return () -> {
               FontManager.bit.drawString((float)var11, (float)var12, var0, var10);
            };
         }
      }
   }

   public static class QuestGiverObject {
      public int serverUpdateInterval = 5000;
      public int clientUpdateInterval = 1000;
      public final Mob mob;
      public final boolean shouldSaveQuests;
      private final Object questLock = new Object();
      private int serverUpdateTicker;
      private HashSet<Long> activeAuths;
      private HashMap<Long, ArrayList<GivenQuest>> serverQuests;
      private int clientUpdateTicker;
      private ArrayList<GivenQuest> clientQuests;

      public QuestGiverObject(Mob var1, boolean var2) {
         this.serverUpdateTicker = this.serverUpdateInterval;
         this.activeAuths = new HashSet();
         this.serverQuests = new HashMap();
         this.clientUpdateTicker = this.clientUpdateInterval;
         this.clientQuests = new ArrayList();
         if (!(var1 instanceof QuestGiver)) {
            throw new IllegalArgumentException("Mob must implement QuestGiver interface");
         } else {
            this.mob = var1;
            this.shouldSaveQuests = var2;
         }
      }

      public void addSaveData(SaveData var1) {
         if (this.shouldSaveQuests) {
            SaveData var2 = new SaveData("questGiver");
            Iterator var3 = this.serverQuests.entrySet().iterator();

            while(var3.hasNext()) {
               Map.Entry var4 = (Map.Entry)var3.next();
               if (!((ArrayList)var4.getValue()).isEmpty()) {
                  SaveData var5 = new SaveData("quests");
                  var5.addLong("auth", (Long)var4.getKey());
                  var5.addIntArray("uniqueIDs", ((ArrayList)var4.getValue()).stream().mapToInt((var0) -> {
                     return var0.questUniqueID;
                  }).toArray());
                  var2.addSaveData(var5);
               }
            }

            var1.addSaveData(var2);
         }
      }

      public void applyLoadData(LoadData var1) {
         if (this.shouldSaveQuests) {
            this.serverQuests.clear();
            LoadData var2 = var1.getFirstLoadDataByName("questGiver");
            if (var2 != null) {
               Iterator var3 = var2.getLoadDataByName("quests").iterator();

               label59:
               while(true) {
                  long var5;
                  int[] var7;
                  do {
                     do {
                        if (!var3.hasNext()) {
                           int[] var17 = var2.getIntArray("questUniqueIDs", new int[0], false);
                           int[] var18 = var17;
                           int var19 = var17.length;

                           for(int var6 = 0; var6 < var19; ++var6) {
                              int var20 = var18[var6];
                              this.mob.getLevel().getServer().world.getQuests().removeQuest(var20);
                           }
                           break label59;
                        }

                        LoadData var4 = (LoadData)var3.next();
                        var5 = var4.getLong("auth", -1L);
                     } while(var5 == -1L);

                     var7 = var2.getIntArray("uniqueIDs", new int[0], false);
                  } while(var7.length <= 0);

                  synchronized(this.questLock) {
                     ArrayList var9 = (ArrayList)this.serverQuests.compute(var5, (var0, var1x) -> {
                        return var1x == null ? new ArrayList() : var1x;
                     });
                     int[] var10 = var7;
                     int var11 = var7.length;

                     for(int var12 = 0; var12 < var11; ++var12) {
                        int var13 = var10[var12];
                        GivenQuest var14 = new GivenQuest(var13, this.mob.getLevel().getServer().world.getQuests());
                        if (var14.quest != null) {
                           var9.add(var14);
                        } else {
                           GameLog.warn.println("Could not find quest for quest giver with unique ID " + var13);
                        }
                     }
                  }
               }
            } else {
               GameLog.warn.println("Could not load quest giver data for mob");
            }

         }
      }

      public void clientTick() {
         this.clientUpdateTicker -= 50;
         if (this.clientUpdateTicker <= 0) {
            this.clientUpdateTicker = this.clientUpdateInterval;
            synchronized(this.questLock) {
               Iterator var2 = this.clientQuests.iterator();

               while(var2.hasNext()) {
                  GivenQuest var3 = (GivenQuest)var2.next();
                  var3.updateQuest(this.mob.getLevel().getClient().quests);
               }
            }
         }

      }

      public ArrayList<GivenQuest> getRequestedQuests(ServerClient var1) {
         this.activeAuths.add(var1.authentication);
         return (ArrayList)this.serverQuests.compute(var1.authentication, (var2, var3) -> {
            List var4 = ((QuestGiver)this.mob).getGivenQuests(var1);
            return (ArrayList)var4.stream().map((var0) -> {
               return new GivenQuest(var0.getUniqueID(), var0);
            }).collect(Collectors.toCollection(ArrayList::new));
         });
      }

      public void serverTick() {
         this.serverUpdateTicker -= 50;
         if (this.serverUpdateTicker <= 0) {
            this.serverUpdateTicker = this.serverUpdateInterval;
            synchronized(this.questLock) {
               HashSet var2 = new HashSet();
               Iterator var3 = this.activeAuths.iterator();

               label73:
               while(true) {
                  ServerClient var6;
                  ArrayList var7;
                  boolean var8;
                  ArrayList var10;
                  label71:
                  do {
                     long var4;
                     while(var3.hasNext()) {
                        var4 = (Long)var3.next();
                        var6 = this.mob.getLevel().getServer().getClientByAuth(var4);
                        var7 = (ArrayList)this.serverQuests.get(var4);
                        if (var6 != null && var6.isSamePlace(this.mob.getLevel()) && var7 != null) {
                           var8 = false;
                           List var9 = ((QuestGiver)this.mob).getGivenQuests(var6);
                           var10 = new ArrayList(var9.size());
                           Iterator var11 = var9.iterator();

                           while(var11.hasNext()) {
                              Quest var12 = (Quest)var11.next();
                              int var13 = var12.getUniqueID();
                              boolean var14 = false;
                              ListIterator var15 = var7.listIterator();

                              while(var15.hasNext()) {
                                 GivenQuest var16 = (GivenQuest)var15.next();
                                 if (var16.questUniqueID == var13) {
                                    var10.add(new GivenQuest(var13, var12));
                                    var15.remove();
                                    var14 = true;
                                    break;
                                 }
                              }

                              if (!var14) {
                                 var10.add(new GivenQuest(var13, var12));
                                 var8 = true;
                              }
                           }

                           this.serverQuests.put(var4, var10);
                           continue label71;
                        }

                        var2.add(var4);
                     }

                     var3 = var2.iterator();

                     while(var3.hasNext()) {
                        var4 = (Long)var3.next();
                        this.activeAuths.remove(var4);
                     }
                     break label73;
                  } while(!var8 && var7.isEmpty());

                  var6.sendPacket(new PacketQuestGiverUpdate(this.mob.getUniqueID(), var10.stream().mapToInt((var0) -> {
                     return var0.questUniqueID;
                  }).toArray()));
               }
            }
         }

      }

      public QuestMarkerOptions getMarkerOptions(PlayerMob var1) {
         Object var2 = null;
         if (var1 != null) {
            if (var1.isClientClient()) {
               var2 = this.clientQuests;
            } else if (var1.isServerClient()) {
               var2 = (List)this.serverQuests.get(var1.getServerClient().authentication);
            }
         }

         if (var2 != null && !((List)var2).isEmpty()) {
            boolean var3 = false;
            boolean var4 = false;
            boolean var5 = false;
            synchronized(this.questLock) {
               Iterator var7 = ((List)var2).iterator();

               label66:
               while(true) {
                  while(true) {
                     if (!var7.hasNext()) {
                        break label66;
                     }

                     GivenQuest var8 = (GivenQuest)var7.next();
                     if (var8.quest == null) {
                        var3 = true;
                     } else {
                        var4 = true;
                        if (var1.getNetworkClient() != null) {
                           var5 = var5 || var8.quest.canComplete(var1.getNetworkClient());
                        }
                     }
                  }
               }
            }

            StringBuilder var6 = new StringBuilder();
            if (var3) {
               var6.append('!');
            }

            if (var4) {
               var6.append('?');
            }

            Color var11 = var4 && !var5 ? new Color(100, 100, 100) : new Color(200, 200, 50);
            return new QuestMarkerOptions(var6.toString(), var11);
         } else {
            return null;
         }
      }
   }

   public static class GivenQuest {
      public int questUniqueID;
      public Quest quest;

      public GivenQuest(int var1, QuestManager var2) {
         this.questUniqueID = var1;
         if (var2 != null) {
            this.updateQuest(var2);
         }

      }

      public GivenQuest(int var1, Quest var2) {
         this.questUniqueID = var1;
         this.quest = var2;
      }

      private void updateQuest(QuestManager var1) {
         this.quest = var1.getQuest(this.questUniqueID);
      }
   }
}
