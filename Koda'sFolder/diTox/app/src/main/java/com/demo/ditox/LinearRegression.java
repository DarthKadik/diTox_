package com.demo.ditox;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class LinearRegression {
    private double[] beta;
    private HashMap<Integer, String> featureMap;
    private final OLSMultipleLinearRegression regression;

    public LinearRegression(){
        this.regression = new OLSMultipleLinearRegression();
    }

    public void train(Map<String, double[]> data){
        double[] y = data.get("wellness_score");
        double[][] x = new double[data.size()-1][data.get("wellness_score").length];
        String[] keys = data.keySet().toArray(new String[0]);

        int k = 0;
        for(int i = 0; i < data.size(); i++){
            if(keys[i].equals("wellness_score")){
                continue;
            }
            x[k] = data.get(keys[k]);
            this.featureMap.put(k, keys[k]);
            this.featureMap.put(k+1, "total_time");
            this.featureMap.put(k+2, "intercept");
            k++;
        }
        double[][] x_aug = this.augment_data(x);
        this.regression.newSampleData(y, x_aug);
        this.beta = this.regression.estimateRegressionParameters();

    }

    private double[][] augment_data(double[][] x){
        int m = x.length;
        int n = x[0].length;
        System.out.println(n + " " + m);
        double[][] x_aug = new double[m+2][n];

        for(int j = 0; j < n; j++){
            double sum = 0;

            for(int i = 0; i < m; i++){
                sum += x[i][j];
            }
            for(int i = 0; i < m; i++){
                x_aug[i][j] = x[i][j] / (sum + 1e-6);
            }
            x_aug[m][j] = sum;
        }
        for(int i = 0; i < n; i++){
            x_aug[m+1][i] = 1.0;
        }
        return x_aug;
    }

    public HashMap<Integer, String> getFeatureMap() {
        return featureMap;
    }
    public double[] getBeta() {
        return beta;
    }
}








