package pigeonpun.megastructureBayonet.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class bayonetCleanUpAfterStationRefit extends BaseCommandPlugin {
    public static Logger log = Global.getLogger(bayonetCleanUpAfterStationRefit.class);
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        CampaignFleetAPI fleet = (CampaignFleetAPI) dialog.getInteractionTarget();
        if(fleet.getFleetData() != null && !fleet.getFleetData().getMembersListCopy().isEmpty()) {
            FleetMemberAPI bayonetStation_Origin = ((CampaignFleetAPI) dialog.getInteractionTarget()).getFleetData().getMembersListCopy().get(0);
            FleetMemberAPI bayonetStation_Modified;
            FleetDataAPI playerFleetData = Global.getSector().getPlayerFleet().getFleetData();
            for(FleetMemberAPI member: playerFleetData.getMembersListCopy()) {
                if(member.getId().equals(bayonetStation_Origin.getId())) {
                    playerFleetData.removeFleetMember(member);
                    log.info("Cleaned up after Bayonet refit");
                    bayonetStation_Modified = member;
                    break;
                }
            }
            Global.getSector().getMemoryWithoutUpdate().set("$bayonet_isFleetEncounter", false);
        }
        return false;
    }
}
