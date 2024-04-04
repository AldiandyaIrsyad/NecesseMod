package necesse.engine.steam;

import com.codedisaster.steamworks.SteamPublishedFileID;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUGCCallback;
import com.codedisaster.steamworks.SteamUGCDetails;
import com.codedisaster.steamworks.SteamUGCQuery;

public class DefaultSteamUGCCallback implements SteamUGCCallback {
   public DefaultSteamUGCCallback() {
   }

   public void onUGCQueryCompleted(SteamUGCQuery var1, int var2, int var3, boolean var4, SteamResult var5) {
   }

   public void onSubscribeItem(SteamPublishedFileID var1, SteamResult var2) {
   }

   public void onUnsubscribeItem(SteamPublishedFileID var1, SteamResult var2) {
   }

   public void onRequestUGCDetails(SteamUGCDetails var1, SteamResult var2) {
   }

   public void onCreateItem(SteamPublishedFileID var1, boolean var2, SteamResult var3) {
   }

   public void onSubmitItemUpdate(SteamPublishedFileID var1, boolean var2, SteamResult var3) {
   }

   public void onDownloadItemResult(int var1, SteamPublishedFileID var2, SteamResult var3) {
   }

   public void onUserFavoriteItemsListChanged(SteamPublishedFileID var1, boolean var2, SteamResult var3) {
   }

   public void onSetUserItemVote(SteamPublishedFileID var1, boolean var2, SteamResult var3) {
   }

   public void onGetUserItemVote(SteamPublishedFileID var1, boolean var2, boolean var3, boolean var4, SteamResult var5) {
   }

   public void onStartPlaytimeTracking(SteamResult var1) {
   }

   public void onStopPlaytimeTracking(SteamResult var1) {
   }

   public void onStopPlaytimeTrackingForAllItems(SteamResult var1) {
   }

   public void onDeleteItem(SteamPublishedFileID var1, SteamResult var2) {
   }
}
