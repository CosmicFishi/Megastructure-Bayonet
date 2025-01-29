package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import org.apache.log4j.Logger;
import pigeonpun.megastructureBayonet.intel.bayonetStationStatusIntel;

import static pigeonpun.megastructureBayonet.structure.bayonetStorageFee.BAYONET_STORAGE_FEE_KEY;

public class bayonetManager {
    public static final Logger log = Global.getLogger(bayonetManager.class);
    public static final String BAYONET_STATION_MEMORY_ID = "$megastructure_bayonet_station";
    public static final String BAYONET_ENTITY_ID = "$mega_bayonet";
    public static final String BAYONET_ENTITY_TAG = "mega_bayonet";
    public static final String BAYONET_ENTITY_STATUS_DATA = "mega_bayonet_broke_down";
    public static final String BAYONET_SHIP_STORAGE_STATS_KEY = "bayonet_ship_storage";
    public static final String BAYONET_SHIP_STORAGE_NO_STORE_TAG = "mega_bayonet_ship_storage_no_store";
    public static final String BAYONET_SHIP_STATION_REPAIR_DAY_STATS_KEY = "bayonet_ship_station_repair_day";
    public static final String BAYONET_SHIP_STATION_BUILD_DAY_STATS_KEY = "bayonet_ship_station_build_day";
    public static final String BAYONET_SHIP_STATION_MAINTENANCE_STATS_KEY = "bayonet_ship_station_maintenance";
    public static final String BAYONET_STATION_DAMAGED_TIME_PER_MONTH_MEM_KEY = "$megastructure_bayonet_station_damaged_time_per_month";
    public static final String BAYONET_SHIP_STATION_DAMAGED_PER_TIME_FEE_STATS_KEY = "megastructure_bayonet_station_damaged_time_per_month";
    public static final int BASE_DAMAGED_REPAIR_DAY = 5;
    public static final int BASE_MAINTENANCE_FEE = 30000;
    public static final int BASE_DAMAGED_REPAIR_PER_TIME_FEE = 80000;
    public static final int BASE_BUILD_DAY = 180; //todo: change this to the station industry build days

    public enum BAYONET_STATION_STATUS {
        DAMAGED, FUNCTIONAL, BUILDING
    }
    public static class bayonetStatusData {
        public float dayLeftBeforeFunctional = 0f;
        public BAYONET_STATION_STATUS status = BAYONET_STATION_STATUS.FUNCTIONAL;
        public bayonetStatusData() {}
    }

    /**
     * Get total repair days of Bayonet Station once station's status turn into {@code BAYONET_STATION_STATUS.DAMAGED}
     * @param bayonetStation
     * @return
     */
    public static float getTotalRepairDay(CampaignFleetAPI bayonetStation) {
        return bayonetStation.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_REPAIR_DAY_STATS_KEY).computeEffective(0f);
    }
    /**
     * Get total repair days of Bayonet Station once station's status turn into {@code BAYONET_STATION_STATUS.BUILDING}
     * @param bayonetStation
     * @return
     */
    public static float getTotalBuildDay(CampaignFleetAPI bayonetStation) {
        return bayonetStation.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_BUILD_DAY_STATS_KEY).computeEffective(0f);
    }
    public static float getTotalMaintenanceFee(CampaignFleetAPI bayonetStation) {
        return bayonetStation.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_MAINTENANCE_STATS_KEY).computeEffective(0f);
    }

    /**
     * Return the damaged per repair time fee
     * @param bayonetStation
     * @return
     */
    public static float getDamagedPerRepairFee(CampaignFleetAPI bayonetStation) {
        return bayonetStation.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_DAMAGED_PER_TIME_FEE_STATS_KEY).computeEffective(0f);
    }

    /**
     * Get total fee this month with each damaged repairing
     * @param bayonetStation
     * @return
     */
    public static float getTotalDamagedPerMonthFee(CampaignFleetAPI bayonetStation) {
        return getDamagedPerRepairFee(bayonetStation) * getDamagedCountThisMonth();
    }
    public static int getDamagedCountThisMonth() {
        int count = 0;
        if(Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_DAMAGED_TIME_PER_MONTH_MEM_KEY) != null && Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_DAMAGED_TIME_PER_MONTH_MEM_KEY) instanceof Integer) {
            count = (int) Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_DAMAGED_TIME_PER_MONTH_MEM_KEY);
        }
        return count;
    }
    public static void updateDamagedTimeThisMonth() {
        int count = getDamagedCountThisMonth();
        count += 1;
        Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_DAMAGED_TIME_PER_MONTH_MEM_KEY, count);
    }
    public static void resetDamagedTimeThisMonth() {
        Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_DAMAGED_TIME_PER_MONTH_MEM_KEY, 0);
    }
    public static float getStorageFeeFraction() {
        float storageFreeFraction = Global.getSettings().getFloat("bayonet_storageFreeFraction");
        return storageFreeFraction;
    }
    public static int getStorageCargoTotalFee(MarketAPI market) {
        int value = (int) (getStorageCargoValue(market) * getStorageFeeFraction());
        return value;
    }
    public static int getStorageShipTotalFee(MarketAPI market) {
        int value = (int) (getStorageShipValue(market) * getStorageFeeFraction());
        return value;
    }
    public static float getStorageCargoValue(MarketAPI market) {
        bayonetSubmarketStorage plugin = getBayonetStorage(market);
        if (plugin == null) return 0f;
        float value = 0f;
        for (CargoStackAPI stack : plugin.getCargo().getStacksCopy()) {
            value += stack.getSize() * stack.getBaseValuePerUnit();
        }
        return value;
    }

    public static float getStorageShipValue(MarketAPI market) {
        bayonetSubmarketStorage plugin = getBayonetStorage(market);
        if (plugin == null) return 0f;
        float value = 0f;

        for (FleetMemberAPI member : plugin.getCargo().getMothballedShips().getMembersListCopy()) {
            value += member.getBaseValue();
        }
        return value;
    }

    public static void createBayonetStorageFee() {
        if(Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STORAGE_FEE_KEY) == null) {
            new bayonetStorageFee();
        }
    }
    public static bayonetSubmarketStorage getBayonetStorage(CampaignFleetAPI bayonetStation) {
        MarketAPI market = bayonetStation.getMarket();
        return getBayonetStorage(market);
    }
    public static bayonetSubmarketStorage getBayonetStorage(MarketAPI market) {
        if (market == null) return null;
        SubmarketAPI submarket = market.getSubmarket("megastructure_bayonet_storage");
        if (submarket == null) return null;
        return (bayonetSubmarketStorage) submarket.getPlugin();
    }
    public static boolean isBayonetRepairing(CampaignFleetAPI bayonetStation) {
        return getCurrentBayonetStatus(bayonetStation).status.equals(BAYONET_STATION_STATUS.DAMAGED);
    }
    public static boolean isBayonetFunctional(CampaignFleetAPI bayonetStation) {
        return getCurrentBayonetStatus(bayonetStation).status.equals(BAYONET_STATION_STATUS.FUNCTIONAL);
    }
    public static boolean isBayonetBuilding(CampaignFleetAPI bayonetStation) {
        return getCurrentBayonetStatus(bayonetStation).status.equals(BAYONET_STATION_STATUS.BUILDING);
    }

    /**
     * @param bayonetStation
     * @return create new status if {@code bayonetStation} is null
     */
    public static bayonetStatusData getCurrentBayonetStatus(CampaignFleetAPI bayonetStation) {
        bayonetStatusData data;
        if(bayonetStation != null && bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) != null && bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) instanceof bayonetStatusData) {
            data = (bayonetStatusData) bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA);
        } else {
            log.info("Can't find Bayonet status data, obtaining a new status");
            data = new bayonetStatusData();
        }
        return data;
    }
    /**
     * As the name imply, get Bayonet Station fleet, if it doesn't exist, create a new Bayonet fleet + market
     * Also save the newly created to memory
     * @return
     */
    public static CampaignFleetAPI getBayonetStationFleet() {
        CampaignFleetAPI stationFleet;
        if(Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_MEMORY_ID) != null && Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_MEMORY_ID) instanceof CampaignFleetAPI) {
            stationFleet = (CampaignFleetAPI) Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_MEMORY_ID);
        } else {
            //create new station
            log.info("Can't find Bayonet Station in memory. Creating new station");
            stationFleet = createStation(getCurrentBayonetStatus(null));
            Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_MEMORY_ID, stationFleet);
        }
        return stationFleet;
    }
    public static boolean isBayonetStationFleetInit() {
        return Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_MEMORY_ID) != null && Global.getSector().getMemoryWithoutUpdate().get(BAYONET_STATION_MEMORY_ID) instanceof CampaignFleetAPI;
    }

    /**
     * Use for changing bayonet status while setting up some additional stuffs
     * @param status
     * @return
     */
    public static void changeBayonetStationStatus(BAYONET_STATION_STATUS status, CampaignFleetAPI bayonetStation) {
        if(bayonetStation == null) return;
        bayonetStatusData data = getCurrentBayonetStatus(bayonetStation);
        data.status = status;
        switch (status) {
            case DAMAGED:
                for(FleetMemberAPI fleetMember: bayonetStation.getFleetData().getMembersListCopy()) {
                    fleetMember.getRepairTracker().setMothballed(true);
                    fleetMember.getRepairTracker().setCR(0);
                }
                bayonetStation.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_NON_AGGRESSIVE, true);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS, true);
                bayonetStation.setNullAIActionText("Repairing...");
                data.dayLeftBeforeFunctional = getTotalRepairDay(bayonetStation);
                updateDamagedTimeThisMonth();
                break;
            case FUNCTIONAL:
                for(FleetMemberAPI fleetMember: bayonetStation.getFleetData().getMembersListCopy()) {
                    fleetMember.getRepairTracker().setMothballed(false);
                    fleetMember.getRepairTracker().setCR(fleetMember.getRepairTracker().getMaxCR());
                }
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE, true);
                bayonetStation.getMemoryWithoutUpdate().unset(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS);
                bayonetStation.setNullAIActionText(null);
                break;
            case BUILDING:
                //todo: add days
                data.dayLeftBeforeFunctional = getTotalBuildDay(bayonetStation);
                //todo: building intel
                break;
        }
        log.info("Changed Bayonet Station status to " + status.toString());
        bayonetStation.getCustomData().put(BAYONET_ENTITY_STATUS_DATA, data);
        Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_MEMORY_ID, bayonetStation);
        //intel
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        bayonetStationStatusIntel statusIntel = new bayonetStationStatusIntel();
        intelManager.addIntel(statusIntel);
    }
    /**
     * Actually summoning the station.
     * - cant summon if its repairing/building (DONE)
     * - Dev mode enable summoning but if not in hyperspace
     */
    public static void summonBayonetStation(boolean isDevmode) {
        CampaignFleetAPI bayonetStation = getBayonetStationFleet();
        if(!isDevmode && (isBayonetRepairing(bayonetStation) || isBayonetBuilding(bayonetStation))) return;
        //if station location is different from current player location -> remove the old station -> create a new one at the new location
        //todo: create in hyperspace
        if(bayonetStation.getContainingLocation() != null && !bayonetStation.getContainingLocation().getId().equals(Global.getSector().getCurrentLocation().getId())) {
            for(CampaignFleetAPI entity: bayonetStation.getContainingLocation().getFleets()) {
                if(entity.getMemoryWithoutUpdate().contains(BAYONET_ENTITY_ID)) {
                    bayonetStation.getContainingLocation().removeEntity(entity);
                    break;
                }
            }
            //If custom data has broke down status -> create the station broke down
            bayonetStation = createStation(getCurrentBayonetStatus(bayonetStation));
        }
        //if the location is the same -> set a different coordinate for the station
        //Recreating new one from memory still need to set a location
        bayonetStation.setContainingLocation(Global.getSector().getCurrentLocation());
        bayonetStation.setLocation(Global.getSector().getPlayerFleet().getLocation().x, Global.getSector().getPlayerFleet().getLocation().y);
        log.info("Summoning Bayonet Station to location [" + Global.getSector().getPlayerFleet().getLocation().toString() + "] in " + Global.getSector().getCurrentLocation());

        //save it to memory after summoning
        Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_MEMORY_ID, bayonetStation);
    }
    //todo: custom ship for member
    public static FleetMemberAPI createBayonetStationMember() {
        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "station1_hightech_Standard");
        member.getVariant().addTag(BAYONET_ENTITY_TAG);
        member.getVariant().addPermaMod("mega_bayonet_info");
        member.getVariant().setVariantDisplayName("Bayonet Station");
        return member;
    }
    /**
     * Only create the Bayonet fleet with certain status, save it to memory, doesn't set location
     * @param status
     * @return
     */
    public static CampaignFleetAPI createStation(bayonetStatusData status) {
        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet(Factions.PLAYER, FleetTypes.BATTLESTATION, null);
        //todo: select which station to have "installed" on Bayonet
        FleetMemberAPI member = createBayonetStationMember();
        fleet.getFleetData().addFleetMember(member);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE, true);
        //set up custom stats
        fleet.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_REPAIR_DAY_STATS_KEY).modifyFlat(BAYONET_SHIP_STATION_REPAIR_DAY_STATS_KEY, BASE_DAMAGED_REPAIR_DAY);
        fleet.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_BUILD_DAY_STATS_KEY).modifyFlat(BAYONET_SHIP_STATION_BUILD_DAY_STATS_KEY, BASE_BUILD_DAY);
        fleet.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_MAINTENANCE_STATS_KEY).modifyFlat(BAYONET_SHIP_STATION_MAINTENANCE_STATS_KEY, BASE_MAINTENANCE_FEE);
        fleet.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_DAMAGED_PER_TIME_FEE_STATS_KEY).modifyFlat(BAYONET_SHIP_STATION_DAMAGED_PER_TIME_FEE_STATS_KEY, BASE_DAMAGED_REPAIR_PER_TIME_FEE);
        

        fleet.setStationMode(true);

        fleet.clearAbilities();
        fleet.addAbility(Abilities.TRANSPONDER);
        fleet.getAbility(Abilities.TRANSPONDER).activate();
        fleet.getDetectedRangeMod().modifyFlat("gen", 99999999f);
        fleet.setAI(null);
        boolean hasListener = false;
        for (FleetEventListener eventListener : fleet.getEventListeners()) {
            if (eventListener instanceof bayonetFleetEventListener) {
                hasListener = true;
                break;
            }
        }

        if (!hasListener) {
            fleet.addEventListener(new bayonetFleetEventListener(fleet));
            fleet.addScript(new bayonetStationRefitListener(fleet));
            fleet.addScript(new bayonetFleetRepairTracker(fleet));
        }

        //todo: AI core/officer selection ?
//        String coreId = Commodities.ALPHA_CORE;
//        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(coreId);
//        PersonAPI commander = plugin.createPerson(coreId, fleet.getFaction().getId(), new Random());
//        fleet.setCommander(commander);
//        fleet.getFlagship().setCaptain(commander);
        member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
        //todo: custom name
        fleet.setName("Bayonet Station");
        fleet.getMemoryWithoutUpdate().set(BAYONET_ENTITY_ID, true);
        fleet.setOrbit(null);
        fleet.setSensorProfile(1f);
        //todo: set building location
        Global.getSector().getCurrentLocation().addEntity(fleet);
//        fleet.getLocation().set(Global.getSector().getPlayerFleet().getLocation());

        //Market on top of fleet
        //to get dialogAPI to work with fleet -> bayonetBaseCampaignPlugin.java (select which dialog plugin go override)
        MarketAPI market = Global.getFactory().createMarket("megastructure_bayonet_station", fleet.getName(), 5);
        market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        market.setPrimaryEntity(fleet);
        market.addCondition(Conditions.ABANDONED_STATION);
        market.getCondition(Conditions.ABANDONED_STATION).setSurveyed(false);
        market.addSubmarket("megastructure_bayonet_storage");
        market.addCondition("megastructure_bayonet_storage_condition");
        ((StoragePlugin)market.getSubmarket("megastructure_bayonet_storage").getPlugin()).setPlayerPaidToUnlock(true);
        market.setFactionId(Factions.PLAYER);
        market.setPlayerOwned(true);
        fleet.getMemoryWithoutUpdate().set("$tradeMode", "OPEN");
        fleet.getMemoryWithoutUpdate().set("$hasMarket", true);
        fleet.setMarket(market);
        fleet.addTag(Tags.STATION);

        //Status related
//        if(status.equals(BAYONET_STATION_STATUS.REPAIRING)) {
//            for(FleetMemberAPI fleetMember: fleet.getFleetData().getMembersListCopy()) {
//                fleetMember.getRepairTracker().setCrashMothballed(true);
//            }
//        }
//        fleet.getCustomData().put(BAYONET_ENTITY_STATUS_DATA, status);
        changeBayonetStationStatus(status.status, fleet);
        //todo: building status, include a day counter

        return fleet;
    }
}
