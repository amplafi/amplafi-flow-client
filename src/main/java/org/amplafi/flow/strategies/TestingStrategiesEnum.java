package org.amplafi.flow.strategies;

/**
 * Just a simple enum for managing the available strategies.
 * Nothing more complicated is needed just now.
 * @author paul
 */
public enum TestingStrategiesEnum  {

    BogusString(new BogusStringDataStrategy()), CorruptParams(new CorruptParamsNameTestingStrategy()), NullParam(new NullParamStrategy()), ParamCombinations(new CombinationsStrategy()), RealisticParams(new RealisticParamsStrategy());
    
    private AbstractTestingStrategy strategy;
    
    /**
     * Constructor for this enum value.
     * @para 
     */
    TestingStrategiesEnum(AbstractTestingStrategy strategy){
        this.strategy = strategy;
    }
    
    /**
     * @return the 
     */
    public AbstractTestingStrategy getStrategy(){
        return strategy;
    }
    
    /**
     * @return comma separated list of strategy names.
     */ 
    public static String listStrategyNames(){
        StringBuffer out = new StringBuffer();
        
        for(TestingStrategiesEnum s : TestingStrategiesEnum.values()){
            out.append(s.name());
            out.append(",");
        }
        return out.toString();
    }

}
