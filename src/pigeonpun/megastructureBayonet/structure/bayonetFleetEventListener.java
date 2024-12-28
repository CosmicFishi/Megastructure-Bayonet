package pigeonpun.megastructureBayonet.structure;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import org.apache.log4j.Logger;

import java.awt.*;

public class bayonetFleetEventListener implements FleetEventListener, EveryFrameScript {
    public static final Logger log = Global.getLogger(bayonetFleetEventListener.class);
    CampaignFleetAPI fleet;
    protected float repairingTimeLeft = 0f;
    
    public bayonetFleetEventListener(CampaignFleetAPI fleet) {
        this.fleet = fleet;
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
        return false;
    }

    public void notifyRepairing(float duration) {
        repairingTimeLeft = Math.max(repairingTimeLeft, duration);
    }
    public float getRemainRepairDay() {
        return repairingTimeLeft;
    }

    @Override
    public void advance(float amount) {
        if (bayonetManager.isBayonetRepairing(fleet)) {
            float days = Global.getSector().getClock().convertToDays(amount);

            repairingTimeLeft -= days;
            if (repairingTimeLeft <= 0) {
                repairingTimeLeft = 0;
            }
            fleet.addFloatingText("Repairing", Color.red, 0.0000000001f);
        }
    }
}
