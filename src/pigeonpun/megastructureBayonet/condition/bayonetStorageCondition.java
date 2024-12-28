package pigeonpun.megastructureBayonet.condition;

import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SubmarketPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import pigeonpun.megastructureBayonet.structure.bayonetManager;
import pigeonpun.megastructureBayonet.structure.bayonetSubmarketStorage;

import java.awt.*;

public class bayonetStorageCondition extends BaseMarketConditionPlugin {
    @Override
    public void init(MarketAPI market, MarketConditionAPI condition) {
        super.init(market, condition);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        float opad = 10f;
        float pad = 3f;
        float f = bayonetManager.getStorageFeeFraction();
        int percent = (int) (f * 100f);

        Color h = Misc.getHighlightColor();

        int cargoCost = bayonetManager.getStorageCargoTotalFee(market);
        int shipCost = bayonetManager.getStorageShipTotalFee(market);

        if (cargoCost + shipCost > 0) {
            //tooltip.beginGrid(150, 1);
            tooltip.addPara("Monthly fees and expenses (%s of base value of stored items):", opad, h, "" + percent + "%");
            tooltip.beginGridFlipped(300, 1, 80, 10);
            int j = 0;
            tooltip.addToGrid(0, j++, "Ships in storage", Misc.getDGSCredits(shipCost));
            tooltip.addToGrid(0, j++, "Cargo in storage", Misc.getDGSCredits(cargoCost));
            tooltip.addGrid(pad);
        } else {
            tooltip.addPara("Monthly fees and expenses are equal to %s of base value of the stored items.", opad, h, "" + percent + "%");
        }
    }

    @Override
    public void apply(String id) {

    }

    @Override
    public void unapply(String id) {

    }
}
