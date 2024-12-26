package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.MutableMarketStatsAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import pigeonpun.megastructureBayonet.ModPlugin;

public class bayonetSubmarketStorage extends StoragePlugin {
//    public static final int BASE_STORAGE = 10000;
    public static final int BASE_STORAGE_SHIP = 5;

    @Override
    public void init(SubmarketAPI submarket) {
        super.init(submarket);
//        submarket.getMarket().getStats().getDynamic().getMod(ModPlugin.BAYONET_STORAGE_STATS_KEY).modifyFlat(ModPlugin.BAYONET_STORAGE_STATS_KEY, BASE_STORAGE);
        submarket.getMarket().getStats().getDynamic().getMod(ModPlugin.BAYONET_SHIP_STORAGE_STATS_KEY).modifyFlat(ModPlugin.BAYONET_SHIP_STORAGE_STATS_KEY, BASE_STORAGE_SHIP);
    }

//    public int getTotalModifiedStorageSpace() {
//        MutableMarketStatsAPI stats = market.getStats();
//        return (int) Math.floor(stats.getDynamic().getMod(ModPlugin.BAYONET_STORAGE_STATS_KEY).computeEffective(0f));
//    }
    public int getTotalModifiedShipStorageSpace() {
        MutableMarketStatsAPI stats = market.getStats();
        return (int) Math.floor(stats.getDynamic().getMod(ModPlugin.BAYONET_SHIP_STORAGE_STATS_KEY).computeEffective(0f));
    }
//    public int getCurrentStorageSpace() {
//            int count = 0;
//            for(CargoStackAPI stack: submarket.getCargo().getStacksCopy()) {
//                count += stack.getSize();
//            }
//        return count;
//    }
    public int getCurrentShipStorageSpace() {
        int count = 0;
        if(submarket.getCargo().getMothballedShips() != null) {
            count = submarket.getCargo().getMothballedShips().getNumMembers();
        }
        return count;
    }
    @Override
    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        return false;
    }

    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        return false;
    }

    @Override
    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        transaction.getBought();
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        if(action.equals(TransferAction.PLAYER_SELL)) {
            if(member.getVariant().getTags().contains(bayonetManager.BAYONET_ENTITY_TAG) || member.getVariant().getTags().contains(ModPlugin.BAYONET_SHIP_STORAGE_NO_STORE) ){
                return true;
            }
            return getCurrentShipStorageSpace() >= getTotalModifiedShipStorageSpace();
        }
        return false;
    }

    @Override
    public String getIllegalTransferText(FleetMemberAPI member, TransferAction action) {
        if(member.getVariant().getTags().contains(bayonetManager.BAYONET_ENTITY_TAG) || member.getVariant().getTags().contains(ModPlugin.BAYONET_SHIP_STORAGE_NO_STORE)){
            return "Unavailable";
        }
        return "No ship cargo space available";
    }

    @Override
    public String getSellVerb() {
        return "Fleet";
    }

    @Override
    public String getBuyVerb() {
        return "Storage";
    }

    @Override
    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {
        return "Not enough cargo space.";
    }

    @Override
    public boolean showInFleetScreen() {
        return true;
    }
}
