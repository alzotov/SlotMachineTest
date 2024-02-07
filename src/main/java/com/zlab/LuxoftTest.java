package com.zlab;

public class LuxoftTest {
    
    public int calculateReturn(int[] pricesPerDay){

        int maxProfit = 0;
        for(int i=0; i<pricesPerDay.length; i++){
            for(int j=i+1; i<pricesPerDay.length; j++){
                int maxProfitCandidate = pricesPerDay[j]-pricesPerDay[i];
                Math.max(maxProfit, maxProfitCandidate);
            }
        }
        return maxProfit;
    }
}
