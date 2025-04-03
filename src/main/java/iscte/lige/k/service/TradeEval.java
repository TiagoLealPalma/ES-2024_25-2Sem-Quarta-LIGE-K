package iscte.lige.k.service;

public class TradeEval {
    private static class RGB {
        public int r, g, b;
        public RGB(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    public static int evaluateTrade(){
        //TODO
        return 1;
    }

    // Converts a grade in a range of colors (red -> yellow -> green)
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
