package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
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
import pigeonpun.megastructureBayonet.ModPlugin;

import java.util.Random;

import static pigeonpun.megastructureBayonet.structure.bayonetStorageFee.BAYONET_STORAGE_FEE_KEY;

public class bayonetManager {
    public static final Logger log = Global.getLogger(bayonetManager.class);
    public static final String bayonet_station_memory_ID = "$megastructure_bayonet_station";
    public static final String bayonet_entity_ID = "$mega_bayonet";
    public static final String bayonet_entity_TAG = "mega_bayonet";

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
    /**
     * Get Bayonet market from memory and if it doesn't exist, create a new Bayonet fleet + market
     * @return Bayonet Market
     */
    public static MarketAPI getBayonetMarket() {
        CampaignFleetAPI stationFleet;
        if(Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID) != null && Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID) instanceof CampaignFleetAPI) {
            stationFleet = (CampaignFleetAPI) Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID);
        } else {
            stationFleet = createStation();
        }
        return stationFleet.getMarket();
    }
    public static bayonetSubmarketStorage getBayonetStorage() {
        MarketAPI market = getBayonetMarket();
        if (market == null) return null;
        SubmarketAPI submarket = market.getSubmarket("megastructure_bayonet_storage");
        if (submarket == null) return null;
        return (bayonetSubmarketStorage) submarket.getPlugin();
    }
    public static bayonetSubmarketStorage getBayonetStorage(MarketAPI market) {
        if (market == null) return null;
        SubmarketAPI submarket = market.getSubmarket("megastructure_bayonet_storage");
        if (submarket == null) return null;
        return (bayonetSubmarketStorage) submarket.getPlugin();
    }
    /**
     * Recreate Bayonet ship to "teleport" the ship to locations
     */
    public static void recreateBayonetStation() {
        CampaignFleetAPI stationFleet;
        if(Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID) != null && Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID) instanceof CampaignFleetAPI) {
            stationFleet = (CampaignFleetAPI) Global.getSector().getMemoryWithoutUpdate().get(bayonet_station_memory_ID);
            //if station location is different from current player location -> remove the old station -> create a new one at the new location
            //todo: create in hyperspace
            if(!stationFleet.getContainingLocation().getId().equals(Global.getSector().getCurrentLocation().getId())) {
                for(CampaignFleetAPI entity: stationFleet.getContainingLocation().getFleets()) {
                    if(entity.getMemoryWithoutUpdate().contains(bayonet_entity_ID)) {
                        stationFleet.getContainingLocation().removeEntity(entity);
                        break;
                    }
                }
                stationFleet = createStation();
            } else {
                //if the location is the same -> set a different coordinate for the station
                stationFleet.setContainingLocation(Global.getSector().getCurrentLocation());
                stationFleet.setLocation(Global.getSector().getPlayerFleet().getLocation().x, Global.getSector().getPlayerFleet().getLocation().y);
            }
        } else {
            stationFleet = createStation();
        }
        Global.getSector().getMemoryWithoutUpdate().set(bayonet_station_memory_ID, stationFleet);
    }
    private static CampaignFleetAPI createStation() {
        CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet(Factions.PLAYER, FleetTypes.BATTLESTATION, null);
        //todo: select which station to have "installed" on Bayonet
        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "station1_hightech_Standard");
        member.getVariant().addTag(bayonet_entity_TAG);
        fleet.getFleetData().addFleetMember(member);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_NO_JUMP, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE, true);
        fleet.setStationMode(true);

        fleet.clearAbilities();
        fleet.addAbility(Abilities.TRANSPONDER);
        fleet.getAbility(Abilities.TRANSPONDER).activate();
        fleet.getDetectedRangeMod().modifyFlat("gen", 99999999f);
        fleet.setAI(null);

        //todo: AI core selection ?
        String coreId = Commodities.ALPHA_CORE;
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(coreId);
        PersonAPI commander = plugin.createPerson(coreId, fleet.getFaction().getId(), new Random());

        fleet.setCommander(commander);
        fleet.getFlagship().setCaptain(commander);
        member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
        fleet.setName("Bayonet Station");
        fleet.getMemoryWithoutUpdate().set(bayonet_entity_ID, true);
        fleet.setOrbit(null);
        //todo: remove this, only set position on the selected planet
        Global.getSector().getCurrentLocation().addEntity(fleet);
        fleet.getLocation().set(Global.getSector().getPlayerFleet().getLocation());

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

        return fleet;
    }
}
