package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import org.magiclib.bounty.MagicBountyBattleListener;
import org.magiclib.bounty.MagicBountyIntel;
import pigeonpun.megastructureBayonet.ModPlugin;

import java.util.List;
import java.util.Random;

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
    public static final int BASE_REPAIR_DAY = 90;


    enum BAYONET_STATION_STATUS {
        REPAIRING, FUNCTIONAL, BUILDING
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
        if(bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) != null && bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) instanceof BAYONET_STATION_STATUS) {
            if(bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA).equals(BAYONET_STATION_STATUS.REPAIRING)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isBayonetFunctional(CampaignFleetAPI bayonetStation) {
        if(bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) != null && bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) instanceof BAYONET_STATION_STATUS) {
            if(bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA).equals(BAYONET_STATION_STATUS.FUNCTIONAL)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isBayonetBuilding(CampaignFleetAPI bayonetStation) {
        if(bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) != null && bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) instanceof BAYONET_STATION_STATUS) {
            if(bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA).equals(BAYONET_STATION_STATUS.BUILDING)) {
                return true;
            }
        }
        return false;
    }
    public static BAYONET_STATION_STATUS getCurrentBayonetStatus(CampaignFleetAPI bayonetStation) {
        if(bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) != null && bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA) instanceof BAYONET_STATION_STATUS) {
            return (BAYONET_STATION_STATUS) bayonetStation.getCustomData().get(BAYONET_ENTITY_STATUS_DATA);
        }
        return BAYONET_STATION_STATUS.FUNCTIONAL;
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
            stationFleet = createStation(BAYONET_STATION_STATUS.FUNCTIONAL);
            Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_MEMORY_ID, stationFleet);
        }
        return stationFleet;
    }

    /**
     * Use for changing bayonet status while setting up some additional stuffs
     * @param status
     * @return
     */
    public static void changeBayonetStationStatus(BAYONET_STATION_STATUS status, CampaignFleetAPI bayonetStation) {
        bayonetStation.getCustomData().put(BAYONET_ENTITY_STATUS_DATA, status);
        switch (status) {
            case REPAIRING:
                for(FleetMemberAPI fleetMember: bayonetStation.getFleetData().getMembersListCopy()) {
                    fleetMember.getRepairTracker().setMothballed(true);
                    fleetMember.getRepairTracker().setCR(0);
                    //todo: custom hullmod to set max CR to 0 and to remove the mothballed
                }
                bayonetStation.getMemoryWithoutUpdate().unset(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_NON_AGGRESSIVE, true);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS, true);

                //todo: status intel
//                IntelManagerAPI intelManager = Global.getSector().getIntelManager();
//                List<IntelInfoPlugin> existingMagicIntel = intelManager.getIntel(MagicBountyIntel.class);
//                MagicBountyIntel intelForBounty = null;
                break;
            case FUNCTIONAL:
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);
                bayonetStation.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE, true);
                bayonetStation.getMemoryWithoutUpdate().unset(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS);
                break;
            case BUILDING:
                //todo: add days
                break;
        }
        Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_MEMORY_ID, bayonetStation);
    }
    /**
     * Actually summoning the station.
     * todo: cant summon if its repairing/building
     * todo: Add function for devmode to be able to summon the station to location even if repairing/building
     */
    public static void summonBayonetStation() {
        CampaignFleetAPI bayonetStation = getBayonetStationFleet();
        if(isBayonetRepairing(bayonetStation) || isBayonetBuilding(bayonetStation)) return;
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

        //save it to memory after summoning
        Global.getSector().getMemoryWithoutUpdate().set(BAYONET_STATION_MEMORY_ID, bayonetStation);
    }
    //todo: custom ship for member
    public static FleetMemberAPI createBayonetStationMember() {
        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "station1_hightech_Standard");
        member.getVariant().addTag(BAYONET_ENTITY_TAG);
        return member;
    }
    /**
     * Only create the Bayonet fleet with certain status, doesn't save it to memory, doesn't set location
     * @param status
     * @return
     */
    public static CampaignFleetAPI createStation(BAYONET_STATION_STATUS status) {
        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet(Factions.PLAYER, FleetTypes.BATTLESTATION, null);
        //todo: select which station to have "installed" on Bayonet
        FleetMemberAPI member = createBayonetStationMember();
        fleet.getFleetData().addFleetMember(member);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE, true);
        fleet.getStats().getDynamic().getMod(BAYONET_SHIP_STATION_REPAIR_DAY_STATS_KEY).modifyFlat(BAYONET_SHIP_STATION_REPAIR_DAY_STATS_KEY, BASE_REPAIR_DAY);

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
            fleet.addScript(new bayonetFleetEventListener(fleet));
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
        if(status.equals(BAYONET_STATION_STATUS.REPAIRING)) {
            for(FleetMemberAPI fleetMember: fleet.getFleetData().getMembersListCopy()) {
                fleetMember.getRepairTracker().setCrashMothballed(true);
            }
        }
        fleet.getCustomData().put(BAYONET_ENTITY_STATUS_DATA, status);
        //todo: building status, include a day counter

        return fleet;
    }
}
