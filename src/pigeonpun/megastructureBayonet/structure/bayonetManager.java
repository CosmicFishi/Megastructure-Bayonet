package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
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

public class bayonetManager {
    public static final Logger log = Global.getLogger(bayonetManager.class);
    public static final String bayonet_cargo_memory_ID = "$megastructure_bayonet_cargo";
    public static final String bayonet_station_memory_ID = "$megastructure_bayonet_station";
    public static final String bayonet_entity_ID = "$mega_bayonet";
    public static final String bayonet_entity_TAG = "mega_bayonet";

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

        String coreId = Commodities.ALPHA_CORE;
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(coreId);
        PersonAPI commander = plugin.createPerson(coreId, fleet.getFaction().getId(), new Random());

        fleet.setCommander(commander);
        fleet.getFlagship().setCaptain(commander);
        member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
        fleet.setName("Bayonet Station");
        fleet.getMemoryWithoutUpdate().set(bayonet_entity_ID, true);
        fleet.setOrbit(null);
        Global.getSector().getCurrentLocation().addEntity(fleet);
        fleet.getLocation().set(Global.getSector().getPlayerFleet().getLocation());

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
