package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import org.apache.log4j.Logger;

import java.awt.*;

public class bayonetFleetEventListener implements FleetEventListener, EveryFrameScript {
    public static final Logger log = Global.getLogger(bayonetFleetEventListener.class);
    CampaignFleetAPI bayonetFleet;
    protected float repairingTimeLeft = 0f;

    public bayonetFleetEventListener(CampaignFleetAPI fleet) {
        this.bayonetFleet = fleet;
    }
    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        if(reason.equals(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE)) {
            fleet.setAbortDespawn(true);
            //todo: custom bayonet member variant
            FleetMemberAPI member = bayonetManager.createBayonetStationMember();
            fleet.getFleetData().addFleetMember(member);
            bayonetManager.changeBayonetStationStatus(bayonetManager.BAYONET_STATION_STATUS.REPAIRING, fleet);
            notifyRepairing(fleet.getStats().getDynamic().getMod(bayonetManager.BAYONET_SHIP_STATION_REPAIR_DAY_STATS_KEY).computeEffective(0f));
            //todo: add intel that ship is being repair
//            fleet.setLocation(Global.getSector().getPlayerFleet().getLocation().x, Global.getSector().getPlayerFleet().getLocation().y);
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    public void notifyRepairing(float duration) {
        repairingTimeLeft = Math.max(repairingTimeLeft, duration);
    }
    public float getRemainRepairDay() {
        return repairingTimeLeft;
    }

    @Override
    public void advance(float amount) {
        if (bayonetManager.isBayonetRepairing(bayonetFleet)) {
            float days = Global.getSector().getClock().convertToDays(amount);

            repairingTimeLeft -= days;
            if (repairingTimeLeft <= 0) {
                repairingTimeLeft = 0;
            }
            //todo: move repairingTimeLeft into statusData
            bayonetFleet.addFloatingText("Repairing", Color.red, 0.0000000001f);
        }
        if(Global.getSector().getCampaignUI().getCurrentCoreTab() != null) {
            FleetMemberAPI bayonetStation = bayonetFleet.getFleetData().getMembersListCopy().get(0);
            if(Global.getSector().getCampaignUI().getCurrentCoreTab().equals(CoreUITabId.REFIT) && !isBayonetAdded()) {
                //adding it to player fleet
                Global.getSector().getPlayerFleet().getFleetData().addFleetMember(bayonetStation);
                //Core UI doesn't sync up completely with the member adding, force opening the UI tab again seems to do the trick
                //if it works, it workS
                Global.getSector().getCampaignUI().showCoreUITab(Global.getSector().getCampaignUI().getCurrentCoreTab());
            } else {
                if(!Global.getSector().getCampaignUI().getCurrentCoreTab().equals(CoreUITabId.REFIT) && isBayonetAdded()) {
                    //removing the station if it's not refit UI
                    Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(bayonetStation);
                    //if it works, it workS
                    Global.getSector().getCampaignUI().showCoreUITab(Global.getSector().getCampaignUI().getCurrentCoreTab());
                }
            }
        } else {
            //When player close refit
            if(isBayonetAdded()) {
                FleetMemberAPI bayonetStation = bayonetFleet.getFleetData().getMembersListCopy().get(0);
                Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(bayonetStation);
            }
        }
    }
    public boolean isBayonetAdded() {
        FleetDataAPI playerFleetData = Global.getSector().getPlayerFleet().getFleetData();
        for(FleetMemberAPI member: playerFleetData.getMembersListCopy()) {
            if(member.getId().equals(bayonetFleet.getFleetData().getMembersListCopy().get(0).getId())) {
                return true;
            }
        }
        return false;
    }
}
