package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Trade;

/**
 * Class responsible for evaluating property trades based on area differences
 * and converting the evaluation score into a color representation.
 */
public class TradeEval {

    /**
     * Evaluates a property trade by calculating the percentage difference between
     * the areas of the two properties involved.
     *
     * The result is stored as a score (0–100) in the given {@link Trade} object.
     *
     * @param trade the trade object containing the properties to be evaluated
     */
    public static void evaluateTrade(Trade trade){
        double totalTradingArea = trade.getOwner1Property().getArea() + trade.getOwner2Property().getArea();
        double tradingAreaDelta = Math.abs(trade.getOwner1Property().getArea() - trade.getOwner2Property().getArea());

        trade.setScore((int)((1 - (tradingAreaDelta / totalTradingArea)) * 100));
    }

    /**
     * Converts an evaluation score (0–100) into a semi-transparent RGBA color
     * for visual representation.
     *
     * The color transitions from red (low score) to yellow (medium score)
     * to green (high score).
     *
     * @param eval the evaluation score as an integer from 0 to 100
     * @return a color string in the format "rgba(r, g, 0, 0.3)"
     */
    public static String IntToColor(int eval){
        if (eval < 0) eval = 0;
        if (eval > 100) eval = 100;

        int red, green;

        if (eval <= 50){
            red = 255;
            green = (int) (255 * (eval / 50.0));
        } else {
            red = (int) (255 * (1 - (eval - 50) / 50.0));
            green = 255;
        }

        return String.format("rgba(%d, %d, 0, 0.3)",red,green);
    }
}
