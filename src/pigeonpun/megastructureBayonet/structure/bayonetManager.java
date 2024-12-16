package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import pigeonpun.megastructureBayonet.ModPlugin;

public class bayonetManager {
    public static final Logger log = Global.getLogger(bayonetManager.class);
    public static final String bayonet_cargo_memory_ID = "$megastructure_bayonet_cargo";
    public static final String bayonet_station_memory_ID = "$megastructure_bayonet_station";
    public static final String bayonet_entity_ID = "mega_bayonet";

    //todo: the station variant
//            CampaignFleetAPI fleet = Global.getFactory().createEmptyFleet(Factions.PLAYER, "Temp", false);

    public static void saveCargo(CargoAPI cargo) {
        CargoAPI tempCargo = getCargo();
        //todo: condition check to see if reach max storage space
        tempCargo.addAll(cargo);
        Global.getSector().getMemoryWithoutUpdate().set(bayonet_cargo_memory_ID, tempCargo);
    }

    /**
     * Get cargo from memoryAPI if available, else create a new cargo
     * @return
     */
    public static CargoAPI getCargo() {
        CargoAPI tempCargo;
        if(Global.getSector().getMemoryWithoutUpdate().get(bayonet_cargo_memory_ID) != null && Global.getSector().getMemoryWithoutUpdate().get(bayonet_cargo_memory_ID) instanceof CargoAPI) {
            tempCargo = (CargoAPI) Global.getSector().getMemoryWithoutUpdate().get(bayonet_cargo_memory_ID);
        } else {
            tempCargo = Global.getFactory().createCargo(true);
        }
        Global.getSector().getMemoryWithoutUpdate().set(bayonet_cargo_memory_ID, tempCargo);
        return tempCargo;
    }

    /**
     * Recreate Bayonet ship to "teleport" the ship to locations
     */
    public static void recreateBayonetStation() {
        SectorEntityToken station;
        if(Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID) != null && Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID) instanceof SectorEntityToken) {
            station = (SectorEntityToken) Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID);
            //if station location is different from current player location -> remove the old station -> create a new one at the new location
            //todo: create in hyperspace
            if(!station.getContainingLocation().getId().equals(Global.getSector().getCurrentLocation().getId())) {
                for(CustomCampaignEntityAPI entity: station.getContainingLocation().getCustomEntities()) {
                    if(entity.getId().equals(bayonet_entity_ID)) {
                        station.getContainingLocation().removeEntity(entity);
                        break;
                    }
                }
                station = createStation();
            } else {
                //if the location is the same -> set a different coordinate for the station
                station.setContainingLocation(Global.getSector().getCurrentLocation());
                station.setLocation(Global.getSector().getPlayerFleet().getLocation().x, Global.getSector().getPlayerFleet().getLocation().y);
            }
        } else {
            station = createStation();
        }
        Global.getSector().getMemoryWithoutUpdate().set(bayonet_station_memory_ID, station);
    }
    private static SectorEntityToken createStation() {
        SectorEntityToken temp = Global.getSector().getCurrentLocation().addCustomEntity(
                bayonet_entity_ID,
                "The Bayonet", "megastructure-bayonet",
                Factions.NEUTRAL
        );
        temp.getMemoryWithoutUpdate().set("$abandonedStation", true);
//        MarketAPI market = Global.getFactory().createMarket("megastructure_bayonet_station", temp.getName(), 0);
        MarketAPI market = Global.getFactory().createMarket("megastructure_bayonet_station", temp.getName(), 0);
        market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        market.setPrimaryEntity(temp);
        market.setFactionId(temp.getFaction().getId());
        market.addCondition(Conditions.ABANDONED_STATION);
        market.getCondition(Conditions.ABANDONED_STATION).setSurveyed(false);
        market.addSubmarket(ModPlugin.BAYONET_STORAGE_SUBMARKET);
        market.setPlanetConditionMarketOnly(false);
        ((StoragePlugin)market.getSubmarket(ModPlugin.BAYONET_STORAGE_SUBMARKET).getPlugin()).setPlayerPaidToUnlock(true);
        temp.setMarket(market);
        temp.getMemoryWithoutUpdate().unset("$tradeMode");
//        Misc.setAbandonedStationMarket("megastructure_bayonet_station", temp);
        temp.setLocation(Global.getSector().getPlayerFleet().getLocation().x, Global.getSector().getPlayerFleet().getLocation().y);
        return temp;
    }
}
