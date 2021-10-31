package com.forgestorm.client.util.color;

import com.forgestorm.shared.util.color.SkinColorList;

import static com.forgestorm.client.util.Log.println;

public class ColorConverter {

    /**
     * Just a test class to get some color things figured out.
     */

    public static void main(String[] args) {
//        colorFromRGB(100f, 88.6f, 84.3f);
//        colorFromRGB(99.6f, 72.2f, 60.4f);
//        colorFromRGB(82f, 54.9f, 44.3f);
//        colorFromRGB(69.4f, 32.2f, 19.2f);
//        colorFromRGB(44.3f, 15.3f, 10.6f);
//        colorFromRGB(29f, 11.8f, 7.8f);


//        colorFromRGB(255, 226, 215);
//        colorFromRGB(254, 184, 154);
//        colorFromRGB(209, 140, 113);
//        colorFromRGB(177, 82, 49);
//        colorFromRGB(113, 39, 27);
//        colorFromRGB(74, 30, 20);

        SkinColorList.printAll();

    }

    static int i = 0;

    public static void colorFromRGB(float red, float green, float blue) {


//        println(getClass(),  red + ", " + green + ", " + blue);

        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

//        println(getClass(),  r + "f, " + g + "f, " + b + "f");

        String sr = Float.toString(r).replace("0.", ".").substring(0, Math.min(10, 3));
        String sg = Float.toString(g).replace("0.", ".").substring(0, Math.min(10, 3));
        String sb = Float.toString(b).replace("0.", ".").substring(0, Math.min(10, 3));


        String num = "SKIN_TONE_" + i + "(new Color(" + sr + "f, " + sg + "f, " + sb + "f" + ", 1)),";

        println(ColorConverter.class, num);
//        println(getClass(),  );


        i++;
    }
}
