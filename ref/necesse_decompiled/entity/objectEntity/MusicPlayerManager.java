package necesse.entity.objectEntity;

import java.util.Arrays;
import java.util.Objects;
import necesse.engine.MusicList;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.VinylItem;

public abstract class MusicPlayerManager {
   protected boolean isPaused;
   protected boolean hasAnyVinyls;
   protected VinylItem[] currentVinyls;
   protected MusicList currentMusicList;
   protected long playingOffset;

   public MusicPlayerManager(int var1) {
      this.currentVinyls = new VinylItem[var1];
   }

   public boolean fixSlots(int var1) {
      if (this.currentVinyls.length != var1) {
         this.currentVinyls = (VinylItem[])Arrays.copyOf(this.currentVinyls, var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean fixSlots(Inventory var1, boolean var2) {
      if (this.fixSlots(var1.getSize())) {
         this.updatePlayingAll(var1, var2);
      }

      return false;
   }

   public abstract void markDirty();

   public void addSaveData(SaveData var1) {
      var1.addBoolean("isPaused", this.isPaused);
      var1.addLong("playingOffset", this.getMusicPlayingOffset());
   }

   public void applyLoadData(LoadData var1) {
      this.isPaused = var1.getBoolean("isPaused", false, false);
      long var2 = var1.getLong("playingOffset", 0L, false);
      if (!this.hasAnyVinyls) {
         this.playingOffset = 0L;
      } else if (this.isPaused) {
         this.playingOffset = var2;
      } else {
         this.playingOffset = System.currentTimeMillis() - var2;
      }

      if (this.currentMusicList != null) {
         this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
      }

   }

   public void writeContentPacket(PacketWriter var1) {
      var1.putNextBoolean(this.isPaused);
      var1.putNextLong(this.getMusicPlayingOffset());
   }

   public void readContentPacket(PacketReader var1) {
      this.isPaused = var1.getNextBoolean();
      long var2 = var1.getNextLong();
      if (!this.hasAnyVinyls) {
         this.playingOffset = 0L;
      } else if (this.isPaused) {
         this.playingOffset = var2;
      } else {
         this.playingOffset = System.currentTimeMillis() - var2;
      }

      if (this.currentMusicList != null) {
         this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
      }

   }

   public MusicList getCurrentMusicList() {
      if (this.currentMusicList != null) {
         this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
      }

      return this.currentMusicList;
   }

   public boolean hasAnyVinyls() {
      return this.hasAnyVinyls;
   }

   public int getSlots() {
      return this.currentVinyls.length;
   }

   public MusicOptionsOffset getCurrentMusic() {
      if (this.currentMusicList != null) {
         this.currentMusicList.setTimeOffset(this.getMusicPlayingOffset());
         return this.currentMusicList.getExpectedMusicPlaying();
      } else {
         return null;
      }
   }

   public MusicOptions getPreviousMusic() {
      int var1 = -1;
      MusicOptionsOffset var2 = this.getCurrentMusic();
      if (var2 != null) {
         ItemMusicOptions var3 = toItemMusic(var2.options);
         if (var3 != null) {
            var1 = var3.itemSlot;
         }
      }

      if (var1 != -1) {
         for(int var5 = this.currentVinyls.length; var5 > 0; --var5) {
            int var4 = Math.floorMod(var1 + var5 - 1, this.currentVinyls.length);
            if (this.currentVinyls[var4] != null) {
               return new ItemMusicOptions(this.currentVinyls[var4], var4);
            }
         }
      }

      return null;
   }

   public void setIsPaused(boolean var1) {
      if (this.isPaused != var1) {
         this.isPaused = var1;
         if (!this.hasAnyVinyls) {
            this.playingOffset = 0L;
         } else {
            this.playingOffset = System.currentTimeMillis() - this.playingOffset;
         }

         this.markDirty();
      }

   }

   public boolean isPaused() {
      return this.isPaused;
   }

   public void forwardMilliseconds(long var1) {
      if (this.hasAnyVinyls) {
         if (var1 != 0L) {
            if (this.isPaused) {
               this.playingOffset -= var1;
            } else {
               this.playingOffset += var1;
            }

            this.markDirty();
         }

      }
   }

   public void setOffset(long var1) {
      if (this.hasAnyVinyls) {
         if (this.isPaused) {
            this.playingOffset = var1;
         } else {
            this.playingOffset = System.currentTimeMillis() + var1;
         }

         this.markDirty();
      }
   }

   public long getMusicPlayingOffset() {
      if (!this.hasAnyVinyls) {
         return 0L;
      } else {
         return this.isPaused ? this.playingOffset : System.currentTimeMillis() - this.playingOffset;
      }
   }

   public void updateHasAnyVinyls(boolean var1) {
      boolean var2 = this.hasAnyVinyls;
      this.hasAnyVinyls = Arrays.stream(this.currentVinyls).anyMatch((var0) -> {
         return !Objects.isNull(var0);
      });
      if (this.hasAnyVinyls) {
         if (!var2) {
            if (this.isPaused) {
               this.playingOffset = 0L;
            } else {
               this.playingOffset = System.currentTimeMillis();
            }
         }

         if (var1) {
            int var3 = -1;
            MusicOptionsOffset var4 = this.getCurrentMusic();
            if (var4 != null) {
               ItemMusicOptions var5 = toItemMusic(var4.options);
               if (var5 != null) {
                  var3 = var5.itemSlot;
               }
            }

            this.currentMusicList = new MusicList();
            long var11 = 0L;
            boolean var7 = false;

            for(int var8 = 0; var8 < this.currentVinyls.length; ++var8) {
               VinylItem var9 = this.currentVinyls[var8];
               if (var9 != null) {
                  MusicOptions var10 = (new ItemMusicOptions(var9, var8)).fadeInTime(1000).volume(1.5F);
                  if (var4 != null) {
                     if (var3 == var8) {
                        var11 -= var4.offset;
                        var7 = true;
                     } else if (!var7) {
                        var11 -= var10.music.sound.getLengthInMillis() - (long)var10.getFadeOutTime();
                     }
                  }

                  this.currentMusicList.addMusic(var10);
               }
            }

            if (this.isPaused) {
               this.playingOffset = var11;
            } else {
               this.playingOffset = System.currentTimeMillis() + var11;
            }
         }
      } else {
         this.currentMusicList = null;
         if (this.isPaused) {
            this.playingOffset = 0L;
         } else {
            this.playingOffset = System.currentTimeMillis();
         }
      }

   }

   public void updatePlaying(Inventory var1, int var2, boolean var3) {
      InventoryItem var4 = var1.getItem(var2);
      if (var4 != null && var4.item instanceof VinylItem) {
         VinylItem var5 = (VinylItem)var4.item;
         if (this.currentVinyls[var2] != var5) {
            this.currentVinyls[var2] = var5;
            this.updateHasAnyVinyls(var3);
         }
      } else if (this.currentVinyls[var2] != null) {
         this.currentVinyls[var2] = null;
         this.updateHasAnyVinyls(var3);
      }

   }

   public void updatePlayingAll(Inventory var1, boolean var2) {
      boolean var3 = false;

      for(int var4 = 0; var4 < this.currentVinyls.length; ++var4) {
         InventoryItem var5 = var1.getItem(var4);
         if (var5 != null && var5.item instanceof VinylItem) {
            VinylItem var6 = (VinylItem)var5.item;
            if (this.currentVinyls[var4] != var6) {
               this.currentVinyls[var4] = var6;
               var3 = true;
            }
         } else if (this.currentVinyls[var4] != null) {
            this.currentVinyls[var4] = null;
            var3 = true;
         }
      }

      if (var3) {
         this.updateHasAnyVinyls(var2);
      }

   }

   protected static ItemMusicOptions toItemMusic(MusicOptions var0) {
      return var0 instanceof ItemMusicOptions ? (ItemMusicOptions)var0 : null;
   }

   public boolean isSame(MusicPlayerManager var1) {
      if (this.isPaused != var1.isPaused) {
         return false;
      } else if (this.hasAnyVinyls != var1.hasAnyVinyls) {
         return false;
      } else if (Math.abs(this.playingOffset - var1.playingOffset) >= 500L) {
         return false;
      } else {
         return this.hasAnyVinyls && Arrays.equals(this.currentVinyls, var1.currentVinyls);
      }
   }

   protected static class ItemMusicOptions extends MusicOptions {
      public final VinylItem vinylItem;
      public final int itemSlot;

      public ItemMusicOptions(VinylItem var1, int var2) {
         super(var1.music);
         this.vinylItem = var1;
         this.itemSlot = var2;
      }
   }
}
