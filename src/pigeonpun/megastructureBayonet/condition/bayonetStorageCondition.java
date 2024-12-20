package pigeonpun.megastructureBayonet.condition;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

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
        float f = Misc.getStorageFeeFraction(); //todo: change this to a amount which can change
        int percent = (int) (f * 100f);

        Color h = Misc.getHighlightColor();

        int cargoCost = (int) (Misc.getStorageCargoValue(market) * f);
        int shipCost = (int) (Misc.getStorageShipValue(market) * f);

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
