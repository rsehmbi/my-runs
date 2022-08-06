package com.example.ramanpreet_sehmbi.Services;

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N19e6db9c0(i);
        return p;
    }
    static double N19e6db9c0(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 18.809121) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 18.809121) {
            p = WekaClassifier.N6c29caa31(i);
        }
        return p;
    }
    static double N6c29caa31(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 516.16875) {
            p = WekaClassifier.N1e7f4e172(i);
        } else if (((Double) i[0]).doubleValue() > 516.16875) {
            p = 2;
        }
        return p;
    }
    static double N1e7f4e172(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() <= 3.332819) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() > 3.332819) {
            p = WekaClassifier.N7371bc8c3(i);
        }
        return p;
    }
    static double N7371bc8c3(Object []i) {
        double p = Double.NaN;
        if (i[18] == null) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() <= 3.935474) {
            p = 2;
        } else if (((Double) i[18]).doubleValue() > 3.935474) {
            p = 1;
        }
        return p;
    }
}
