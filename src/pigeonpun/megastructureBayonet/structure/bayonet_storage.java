package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.MutableMarketStatsAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import pigeonpun.megastructureBayonet.ModPlugin;

public class bayonet_storage extends StoragePlugin {
    public static final int BASE_STORAGE = 10000;

    @Override
    public void init(SubmarketAPI submarket) {
        super.init(submarket);
        submarket.getMarket().getStats().getDynamic().getMod(ModPlugin.BAYONET_STORAGE_STATS_KEY).modifyFlat(ModPlugin.BAYONET_STORAGE_STATS_KEY, BASE_STORAGE);
    }

    public int getTotalModifiedStorageSpace() {
        MutableMarketStatsAPI stats = market.getStats();
        return (int) Math.floor(stats.getDynamic().getMod(ModPlugin.BAYONET_STORAGE_STATS_KEY).computeEffective(0f));
    }
    public int getCurrentStorageSpace() {
            int count = 0;
            for(CargoStackAPI stack: submarket.getCargo().getStacksCopy()) {
                count += stack.getSize();
            }
        return count;
    }
    @Override
    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        return super.isIllegalOnSubmarket(commodityId, action);
    }

    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        boolean isAllowed = false;
        if(getCurrentStorageSpace() + stack.getSize() <= getTotalModifiedStorageSpace()) {
            isAllowed = true;
        }
        return isAllowed;
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        return true;
    }

    @Override
    public String getIllegalTransferText(FleetMemberAPI member, TransferAction action) {
        return "No ship cargo space available";
    }
    //todo: add text for storage space
    //todo: test it
    //todo: add to the station
    //Limited storage space, no ship storage space.


    @Override
    public String getSellVerb() {
        return "Fleet";
    }

    @Override
    public String getBuyVerb() {
        return "Unavailable";
    }

    @Override
    public String getIllegalTransferText(CargoStackAPI stack, TransferAction action) {
        return "Not enough cargo space.";
    }

    @Override
    public boolean showInFleetScreen() {
        return false;
    }
}
