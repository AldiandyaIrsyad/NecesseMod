package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.NetworkPacketList;
import necesse.engine.network.Packet;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.packet.PacketServerStatus;
import necesse.engine.network.packet.PacketServerStatusRequest;
import necesse.engine.network.server.network.ServerOpenNetwork;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainMenu;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldSettings;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormIndexEvent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormServerList extends FormSelectedList<ServerElement> {
   private FormEventsHandler<FormIndexEvent<FormServerList>> doubleSelect = new FormEventsHandler();
   private DatagramSocket socket;
   private Thread listenThread;
   private Thread lanThread;
   private NetworkPacketList incompletePackets = new NetworkPacketList(5000);
   private ServerElement landSearchElement;

   public static String fileDir() {
      return GlobalData.cfgPath() + "serverlist.cfg";
   }

   public FormServerList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 35);
   }

   public void reset() {
      super.reset();
      this.elements = Collections.synchronizedList(new ArrayList());
   }

   public FormServerList onDoubleSelect(FormEventListener<FormIndexEvent<FormServerList>> var1) {
      this.doubleSelect.addListener(var1);
      return this;
   }

   public void addServer(String var1, String var2, int var3, boolean var4) throws IllegalArgumentException {
      if (var3 >= 0 && var3 <= 65535) {
         ServerElement var5 = new ServerElement(var1, var2, var3);
         this.elements.add(var5);
         this.sort();
         if (var4) {
            var5.refresh(this);
         }

         this.saveData();
      } else {
         throw new IllegalArgumentException("Invalid port");
      }
   }

   public void addServer(String var1, String var2, int var3) throws IllegalArgumentException {
      this.addServer(var1, var2, var3, true);
   }

   public boolean canDeleteSelected() {
      ServerElement var1 = (ServerElement)this.getSelectedElement();
      return var1 != null && var1.canDelete();
   }

   public void deleteSelected() {
      ServerElement var1 = (ServerElement)this.getSelectedElement();
      if (var1 != null && var1.canDelete()) {
         this.elements.remove(var1);
         this.saveData();
         this.clearSelected();
      }
   }

   public boolean hasSelected() {
      return this.getSelectedElement() != null;
   }

   public String getSelectedName() {
      ServerElement var1 = (ServerElement)this.getSelectedElement();
      return var1 == null ? "N/A" : var1.getName();
   }

   public void refresh() {
      this.elements.forEach((var1) -> {
         var1.refresh(this);
      });
   }

   public void saveData() {
      SaveData var1 = new SaveData("SERVERS");
      this.elements.stream().filter(ServerElement::shouldSave).forEach((var1x) -> {
         var1.addSaveData(var1x.getSaveData());
      });
      var1.saveScript(new File(fileDir()));
   }

   public boolean readData() {
      File var1 = new File(fileDir());
      if (!var1.exists()) {
         return false;
      } else {
         this.elements = Collections.synchronizedList(new ArrayList());
         this.clearSelected();

         try {
            LoadData var2 = new LoadData(var1);
            var2.getLoadDataByName("SERVER").forEach((var1x) -> {
               ServerElement var2 = new ServerElement(var1x);
               if (var2.port != 0) {
                  this.elements.add(var2);
               }

            });
         } catch (Exception var3) {
            System.err.println("Error loading server list, some might be missing");
            var3.printStackTrace();
         }

         this.sort();
         return true;
      }
   }

   public void connect(MainMenu var1) {
      ServerElement var2 = (ServerElement)this.getSelectedElement();
      if (var2 != null) {
         var2.lastConnectedTime = (new Date()).getTime();
         this.sort();
         this.saveData();
         var1.connect(var2.getName(), var2.address, var2.port, MainMenu.ConnectFrom.Multiplayer);
      }

   }

   public void setupSocket() {
      if (this.socket == null) {
         try {
            this.socket = new DatagramSocket();
            if (this.listenThread != null) {
               this.listenThread.interrupt();
            }

            this.listenThread = new Thread("Server list") {
               public void run() {
                  while(!FormServerList.this.socket.isClosed()) {
                     try {
                        byte[] var1 = new byte[1024];
                        DatagramPacket var2 = new DatagramPacket(var1, var1.length);

                        try {
                           FormServerList.this.socket.receive(var2);
                        } catch (SocketException var12) {
                           continue;
                        } catch (IOException var13) {
                           var13.printStackTrace();
                           continue;
                        }

                        if (var2.getAddress() != null) {
                           boolean var3 = false;

                           try {
                              NetworkPacket var4 = FormServerList.this.incompletePackets.submitPacket(new NetworkPacket(FormServerList.this.socket, var2), (Consumer)null);
                              if (var4 != null) {
                                 Packet var5 = var4.getTypePacket();
                                 if (var5 instanceof PacketServerStatus) {
                                    PacketServerStatus var6 = (PacketServerStatus)var5;
                                    ServerElement[] var7 = (ServerElement[])FormServerList.this.elements.toArray(new ServerElement[0]);
                                    int var8 = var7.length;

                                    for(int var9 = 0; var9 < var8; ++var9) {
                                       ServerElement var10 = var7[var9];
                                       if (var10.applyPacket(var6)) {
                                          var3 = true;
                                       }
                                    }

                                    if (!var3 && var4.networkInfo instanceof DatagramNetworkInfo && ((DatagramNetworkInfo)var4.networkInfo).address.isSiteLocalAddress()) {
                                       FormServerList.this.elements.add(FormServerList.this.new ServerElement(var4, (DatagramNetworkInfo)var4.networkInfo, var6, FormServerList.this));
                                       FormServerList.this.sort();
                                    }
                                 }
                              }
                           } catch (UnknownPacketException var11) {
                              GameLog.warn.println("Server list received unknown packet");
                           }
                        }
                     } catch (Exception var14) {
                        var14.printStackTrace();
                     }
                  }

               }
            };
            this.listenThread.start();
         } catch (SocketException var2) {
            var2.printStackTrace();
         }

      }
   }

   public void loadServerList() {
      if (!this.readData()) {
         this.saveData();
      }

      this.setupSocket();
      this.refresh();
      this.startLanSearch();
   }

   public void startLanSearch() {
      if (this.landSearchElement != null) {
         this.elements.remove(this.landSearchElement);
      }

      this.landSearchElement = new ServerElement(new LocalMessage("ui", "searchinglan"));
      Runnable var1 = () -> {
         MulticastSocket var1 = null;

         try {
            var1 = new MulticastSocket();
            var1.setBroadcast(true);
            LinkedList var2 = new LinkedList();
            Enumeration var3 = NetworkInterface.getNetworkInterfaces();

            while(true) {
               NetworkInterface var4;
               InetAddress var7;
               do {
                  do {
                     if (!var3.hasMoreElements()) {
                        for(int var10 = 0; var10 < ServerOpenNetwork.lanPorts.length; ++var10) {
                           int var11 = ServerOpenNetwork.lanPorts[var10];
                           if (this.socket == null || this.socket.isClosed()) {
                              break;
                           }

                           Iterator var12 = var2.iterator();

                           while(var12.hasNext()) {
                              var7 = (InetAddress)var12.next();
                              NetworkPacket var8 = new NetworkPacket(new PacketServerStatusRequest(this.socket.getLocalPort(), 0), new DatagramNetworkInfo(this.socket, var7, var11));
                              var8.sendPacket();
                           }
                        }

                        var1.close();
                        Thread.sleep(5000L);
                        this.landSearchElement.name = new LocalMessage("ui", "searchlandone");
                        return;
                     }

                     var4 = (NetworkInterface)var3.nextElement();
                  } while(var4.isLoopback());
               } while(!var4.isUp());

               Iterator var5 = var4.getInterfaceAddresses().iterator();

               while(var5.hasNext()) {
                  InterfaceAddress var6 = (InterfaceAddress)var5.next();
                  var7 = var6.getBroadcast();
                  if (var7 != null) {
                     var2.add(var7);
                  }
               }
            }
         } catch (InterruptedException | IOException var9) {
            if (var1 != null && !var1.isClosed()) {
               var1.close();
            }
         }

      };
      if (this.lanThread != null && this.lanThread.isAlive()) {
         this.lanThread.interrupt();
      }

      this.lanThread = new Thread((ThreadGroup)null, var1, "LAN Search");
      this.lanThread.start();
      this.elements.add(this.landSearchElement);
      this.sort();
   }

   public void sort() {
      Collections.sort(this.elements);
   }

   public void dispose() {
      super.dispose();
      if (this.lanThread != null) {
         this.lanThread.interrupt();
      }

      if (this.listenThread != null) {
         this.listenThread.interrupt();
      }

      if (this.socket != null) {
         this.socket.close();
      }

   }

   public class ServerElement extends FormSelectedElement<FormServerList> implements Comparable<ServerElement> {
      private int state;
      private GameMessage name;
      private String address;
      private InetAddress netAddress;
      private boolean addressError;
      private int port;
      private boolean isLanServer;
      private boolean isText;
      private boolean gotInfo;
      private long uniqueID;
      private int slots;
      private int online;
      private boolean hasPassword;
      private int modsHash;
      private String version;
      private WorldSettings worldSettings;
      private int latency;
      private boolean expectsPacket;
      private long refreshTime;
      private long gotInfoRefresh;
      private long lastConnectedTime;
      private long lastClick;

      public ServerElement(GameMessage var2) {
         this.name = var2;
         this.state = 0;
         this.isText = true;
      }

      public ServerElement(String var2, String var3, int var4) {
         this.name = new StaticMessage(var2);
         this.address = var3;
         this.state = GameRandom.globalRandom.nextInt();

         try {
            this.netAddress = InetAddress.getByName(var3);
         } catch (UnknownHostException var6) {
            this.addressError = true;
         }

         this.port = var4;
         this.lastConnectedTime = (new Date()).getTime();
      }

      public ServerElement(LoadData var2) {
         this.state = GameRandom.globalRandom.nextInt();
         this.applyLoadData(var2);
      }

      public ServerElement(NetworkPacket var2, DatagramNetworkInfo var3, PacketServerStatus var4, FormServerList var5) {
         this("LAN Server", var3.address.getHostAddress(), var3.port);
         this.applyStatus(var4);
         this.isLanServer = true;
         this.refresh(var5, false);
      }

      public String getName() {
         return this.name.translate();
      }

      public SaveData getSaveData() {
         SaveData var1 = new SaveData("SERVER");
         var1.addSafeString("name", this.getName());
         var1.addSafeString("address", this.address);
         var1.addInt("port", this.port);
         var1.addLong("lastConnected", this.lastConnectedTime);
         return var1;
      }

      public void applyLoadData(LoadData var1) {
         this.name = new StaticMessage(var1.getSafeString("name", "necesse/server"));
         this.address = var1.getSafeString("address", "address");
         this.port = var1.getInt("port", 14159);
         this.lastConnectedTime = var1.getLong("lastConnected", this.lastConnectedTime);

         try {
            this.netAddress = InetAddress.getByName(this.address);
         } catch (UnknownHostException var3) {
            this.addressError = true;
         }

      }

      public boolean shouldSave() {
         return !this.isText && !this.isLanServer;
      }

      public boolean canDelete() {
         return !this.isText && !this.isLanServer;
      }

      public int getSlots() {
         return this.slots;
      }

      public int getOnline() {
         return this.online;
      }

      public boolean hasPassword() {
         return this.hasPassword;
      }

      public int getModsHash() {
         return this.modsHash;
      }

      public String getVersion() {
         return this.version;
      }

      public boolean gotInfo() {
         long var1 = System.currentTimeMillis() - this.gotInfoRefresh;
         return this.gotInfo && var1 > 500L;
      }

      public boolean applyPacket(PacketServerStatus var1) {
         if (this.isText) {
            return false;
         } else if (this.state == var1.state) {
            return this.applyStatus(var1);
         } else {
            return this.gotInfo && var1.uniqueID == this.uniqueID ? this.applyStatus(var1) : false;
         }
      }

      private boolean applyStatus(PacketServerStatus var1) {
         if (this.expectsPacket) {
            this.latency = (int)(System.currentTimeMillis() - this.refreshTime);
         }

         this.gotInfo = true;
         this.uniqueID = var1.uniqueID;
         this.online = var1.playersOnline;
         this.slots = var1.slots;
         this.hasPassword = var1.passwordProtected;
         this.worldSettings = var1.worldSettings;
         this.modsHash = var1.modsHash;
         this.version = var1.version;
         this.expectsPacket = false;
         return true;
      }

      public void refresh(FormServerList var1) {
         this.refresh(var1, true);
      }

      public void refresh(FormServerList var1, boolean var2) {
         if (!this.isText) {
            if (var1.socket != null) {
               try {
                  NetworkPacket var3 = new NetworkPacket(new PacketServerStatusRequest(this.isLanServer ? var1.socket.getLocalPort() : 0, this.state), new DatagramNetworkInfo(var1.socket, this.netAddress, this.port));
                  if (this.netAddress != null) {
                     var3.sendPacket();
                  }
               } catch (IOException var4) {
                  var4.printStackTrace();
               }
            }

            this.refreshTime = System.currentTimeMillis();
            this.expectsPacket = true;
            if (var2) {
               this.gotInfo = false;
               this.gotInfoRefresh = this.refreshTime;
            }

         }
      }

      void draw(FormServerList var1, TickManager var2, PlayerMob var3, int var4) {
         if (this.isText) {
            FontOptions var19 = (new FontOptions(16)).color(Settings.UI.activeTextColor);
            String var20 = GameUtils.maxString(this.getName(), var19, var1.width - 10);
            int var21 = var1.width / 2 - FontManager.bit.getWidthCeil(var20, var19) / 2;
            FontManager.bit.drawString((float)var21, 10.0F, var20, var19);
         } else {
            Color var5 = this.isSelected() ? Settings.UI.highlightTextColor : Settings.UI.activeTextColor;
            FontOptions var6 = (new FontOptions(16)).color(var5);
            String var7 = GameUtils.maxString(this.getName(), var6, var1.width - 60);
            FontManager.bit.drawString(10.0F, 2.0F, var7, var6);
            Color var8 = this.isSelected() ? var5 : Settings.UI.activeTextColor;
            FontOptions var9 = (new FontOptions(12)).color(var8);
            String var10 = GameUtils.maxString(this.getAddressString(), var9, var1.width - 60);
            FontManager.bit.drawString(10.0F, 20.0F, var10, var9);
            boolean var11 = this.gotInfo();
            String var12;
            if (var11) {
               var12 = this.latency < 0 ? "N/A" : String.valueOf(this.latency);
               FormScoreboardList.drawLatencyBars(this.latency, 5, 16, var1.width - 32 - 5, 2);
               if (this.isMouseOver(var1) && FormScoreboardList.isMouseOverLatencyBar(var1.width - 32 - 5, 2, this.getMoveEvent())) {
                  Screen.addTooltip(new StringTooltips(Localization.translate("ui", "latencytip", "latency", var12)), TooltipLocation.FORM_FOCUS);
               }

               int var13 = var1.width - 22;
               SERVER_ICONS[] var14 = FormServerList.SERVER_ICONS.values();
               int var15 = var14.length;

               for(int var16 = 0; var16 < var15; ++var16) {
                  SERVER_ICONS var17 = var14[var16];
                  IconOptions var18 = var17.getOptions(this);
                  if (var18 != null) {
                     var18.sprite.initDraw().size(16, 16).draw(var13, 20);
                     if (var18.tooltip != null && this.getMoveEvent() != null && this.mouseOverIcon(var13, 20, this.getMoveEvent())) {
                        Screen.addTooltip(var18.tooltip, TooltipLocation.FORM_FOCUS);
                     }

                     var13 -= 18;
                  }
               }
            }

            var12 = this.getStatusString();
            FontManager.bit.drawString((float)(var1.width - 10 - FontManager.bit.getWidthCeil(var12, var6) - (var11 ? 32 : 0)), 2.0F, var12, var6);
         }
      }

      private boolean mouseOverIcon(int var1, int var2, InputEvent var3) {
         return var3 == null ? false : (new Rectangle(var1, var2, 16, 16)).contains(var3.pos.hudX, var3.pos.hudY);
      }

      public String getStatusString() {
         if (this.gotInfo()) {
            return this.online + "/" + this.slots;
         } else {
            long var1 = System.currentTimeMillis() - this.refreshTime;
            if (var1 > 10000L) {
               return "???";
            } else {
               int var3 = (int)(var1 / 500L % 3L);
               StringBuilder var4 = new StringBuilder();

               for(int var5 = 0; var5 < 3; ++var5) {
                  var4.append(var3 == var5 ? "?" : ".");
               }

               return var4.toString();
            }
         }
      }

      public String getAddressString() {
         return this.addressError ? Localization.translate("ui", "unknownaddress") : this.address + ":" + this.port;
      }

      public int compareTo(ServerElement var1) {
         if (this.isLanServer) {
            return 1;
         } else if (var1.isLanServer) {
            return -1;
         } else if (this.isText) {
            return 1;
         } else {
            return var1.isText ? -1 : Long.compare(var1.lastConnectedTime, this.lastConnectedTime);
         }
      }

      void onClick(FormServerList var1, int var2, InputEvent var3, PlayerMob var4) {
         if (var3.getID() == -100) {
            if (!this.isText) {
               ServerElement var5 = (ServerElement)var1.getSelectedElement();
               super.onClick((FormSelectedList)var1, var2, var3, var4);
               if (var5 == this && System.currentTimeMillis() - this.lastClick < 500L) {
                  var1.doubleSelect.onEvent(new FormIndexEvent(var1, var2));
               }

               if (this.isSelected()) {
                  this.lastClick = System.currentTimeMillis();
                  if (var5 != this) {
                     FormServerList.this.playTickSound();
                  }
               }

            }
         }
      }

      void onControllerEvent(FormServerList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            if (!this.isText) {
               ServerElement var6 = (ServerElement)var1.getSelectedElement();
               super.onControllerEvent((FormSelectedList)var1, var2, var3, var4, var5);
               if (var6 == this && System.currentTimeMillis() - this.lastClick < 500L) {
                  var1.doubleSelect.onEvent(new FormIndexEvent(var1, var2));
               }

               if (this.isSelected()) {
                  this.lastClick = System.currentTimeMillis();
                  if (var6 != this) {
                     FormServerList.this.playTickSound();
                  }
               }

               var3.use();
            }
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormSelectedList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormServerList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormSelectedList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormServerList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormServerList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormServerList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormServerList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public int compareTo(Object var1) {
         return this.compareTo((ServerElement)var1);
      }
   }

   @FunctionalInterface
   private interface IconGetter {
      IconOptions getOptions(ServerElement var1);
   }

   private static class IconOptions {
      public final GameSprite sprite;
      public final GameTooltips tooltip;

      public IconOptions(GameSprite var1, GameTooltips var2) {
         Objects.requireNonNull(var1);
         this.sprite = var1;
         this.tooltip = var2;
      }
   }

   private static enum SERVER_ICONS {
      VERSION_CHECK((var0) -> {
         boolean var1 = var0.getVersion().equals("0.24.2");
         GameSprite var2 = new GameSprite(var1 ? Settings.UI.status_version_good : Settings.UI.status_version_bad);
         StringTooltips var3 = new StringTooltips(Localization.translate("ui", var1 ? "correctversion" : "wrongversion"));
         if (!var1) {
            var3.add(Localization.translate("ui", "serverversion", "version", var0.getVersion()));
         }

         return new IconOptions(var2, var3);
      }),
      MODS_CHECK((var0) -> {
         boolean var1 = var0.getModsHash() != 0;
         boolean var2 = var0.getModsHash() == ModLoader.getModsHash();
         if (!var1 && var2) {
            return null;
         } else {
            GameSprite var3 = new GameSprite(var2 ? Settings.UI.status_mods_good : Settings.UI.status_mods_bad);
            StringTooltips var4 = new StringTooltips(Localization.translate("ui", var2 ? "modmatch" : "modmismatch"));
            return new IconOptions(var3, var4);
         }
      }),
      PASSWORD_CHECK((var0) -> {
         if (!var0.hasPassword()) {
            return null;
         } else {
            GameSprite var1 = new GameSprite(Settings.UI.status_password);
            StringTooltips var2 = new StringTooltips(Localization.translate("ui", "passwordprotected"));
            return new IconOptions(var1, var2);
         }
      }),
      ACHIEVEMENTS_CHECK((var0) -> {
         if (var0.worldSettings == null) {
            return null;
         } else {
            GameSprite var1 = new GameSprite(var0.worldSettings.achievementsEnabled() ? Settings.UI.status_achievements_good : Settings.UI.status_achievements_bad);
            StringTooltips var2 = new StringTooltips(Localization.translate("ui", var0.worldSettings.achievementsEnabled() ? "achenabled" : "achdisabled"));
            return new IconOptions(var1, var2);
         }
      }),
      WORLD_SETTINGS((var0) -> {
         if (var0.worldSettings == null) {
            return null;
         } else {
            GameSprite var1 = new GameSprite(Settings.UI.status_settings);
            GameTooltips var2 = var0.worldSettings.getTooltips(new LocalMessage("ui", "serversettings"));
            return new IconOptions(var1, var2);
         }
      });

      private final IconGetter iconGetter;

      private SERVER_ICONS(IconGetter var3) {
         Objects.requireNonNull(var3);
         this.iconGetter = var3;
      }

      public IconOptions getOptions(ServerElement var1) {
         return this.iconGetter.getOptions(var1);
      }

      // $FF: synthetic method
      private static SERVER_ICONS[] $values() {
         return new SERVER_ICONS[]{VERSION_CHECK, MODS_CHECK, PASSWORD_CHECK, ACHIEVEMENTS_CHECK, WORLD_SETTINGS};
      }
   }
}
