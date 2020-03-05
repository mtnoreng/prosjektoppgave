import org.apache.commons.math3.distribution.NormalDistribution;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class OperationGenerator {
    private OperationType[] operationTypes;
    private int days;
    private Map<Integer, List<OperationType>> logOperations=new HashMap<Integer, List<OperationType>>();
    private Map<int[],Integer> operationTime=new HashMap<int[],Integer>();
    private int[] locations;
    private int[] excludedOperations;

    public OperationGenerator(int days, int[] locations){
        this.locations=locations;
        this.days=days;
        this.excludedOperations= new int[]{1,2,8,10};
    }

    public void createOperationTypes(){
        //number,vessel1,vessel2,vesselBigTask, presedensOver, presedensAv, mHyppighet, sdHyppighet,
        //varighet, penalty
        int [] op1v1=new int[]{2,3,4,6};
        OperationType op1= new OperationType(1,op1v1,null,null,2
                ,0,17520,730,8,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Transport net before operation");
        int [] op2v1=new int[]{2,3,4,6};
        int [] op2v2=new int[]{2,3,4,6};
        OperationType op2=new OperationType(2, op2v1,op2v2,null,
                3, 1, 17520, 730, 4,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Install net");
        int [] op3v1=new int[]{2,3,4,6};
        OperationType op3= new OperationType(3, op3v1, null,
                null, 0,2, 17520, 730, 8,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Transport net after operation");
        int [] op4v1=new int[]{5};
        int [] op4v2=new int[]{2,3,4,6};
        OperationType op4=new OperationType(4, op4v1, op4v2, null,
                0, 0, 1152, 192, 5,DataGenerator.costPenalty*DataGenerator.maxSailingTime*8,"Delousing");
        int [] op5v1=new int[]{2,3,4};
        int [] op5v2=new int[]{2,3,4};
        int [] op5BT=new int[]{6};
        OperationType op5= new OperationType(5, op5v1, op5v2, op5BT,
                0,0, 8760, 360, 40,DataGenerator.costPenalty*DataGenerator.maxSailingTime,"Large inspection of the facility");
        int [] op6v1=new int[]{2};
        OperationType op6=new OperationType(6, op6v1, null, null,
                0,0, 5110, 730, 5,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Wash the net");
        int [] op7v1=new int[]{4,6};
        OperationType op7=new OperationType(7, op7v1, null, null,
                0,0, 8760, 360, 48,DataGenerator.costPenalty*DataGenerator.maxSailingTime,"tightening anchor lines");
        int [] op8v1=new int[]{2,3,4,6};
        int [] op8v2=new int[]{2,3,4,6};
        OperationType op8=new OperationType(8, op8v1, op8v2, null,
                0,9, 8760, 360, 3,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Small installation facility");
        int [] op9v1=new int[]{2,3,4,6};
        OperationType op9= new OperationType(9, op9v1, null, null,
                8, 0, 8760, 360, 6,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Easy transport of equipment to facility");
        int [] op10v1=new int[]{2,3,4,6};
        OperationType op10=new OperationType(10, op10v1, null, null,
                0,0, 720, 100, 2,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Remove dead fish");
        int [] op11v1=new int[]{2,3,4,6};
        OperationType op11=new OperationType(11, op11v1, null, null,
                0,0, 720, 100, 4,DataGenerator.costPenalty*DataGenerator.maxSailingTime*8,"Support wellboat");
        int [] op12v1=new int[]{1,2,3,4,6};
        OperationType op12=new OperationType(12, op12v1, null, null,
                0,0, 720, 100, 5,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Inspect net ROV");
        int [] op13v1=new int[]{1,3};
        OperationType op13=new OperationType(13, op13v1, null, null,
                0,0, 8760, 360, 4,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Inspect net diver");
        int [] op14v1=new int[]{2};
        OperationType op14=new OperationType(14, op14v1, null, null,
                0,0, 8760, 360, 4,DataGenerator.costPenalty*DataGenerator.maxSailingTime,"Wash bottom ring and floating collar");
        int [] op15v1=new int[]{2,3,4,6};
        OperationType op15=new OperationType(15, op15v1, null, null,
                0,0, 168, 24, 3,DataGenerator.costPenalty*DataGenerator.maxSailingTime*4,"Support working boat");
        this.operationTypes=new OperationType[]{op1,op2,op3,op4,op5,op6,op7,op8,op9,op10,op11,op12,op13,op14,op15};
    }


    public void generateInstanceFromDistribution(){
        int period=this.days*24;
        for (int loc : this.locations) {
            for (OperationType opType : this.operationTypes) {
                int mean = opType.getmFrequency();
                int sd=opType.getSdFrequency();
                double t = (double) ThreadLocalRandom.current().nextInt(1, mean+1);
                NormalDistribution nd = new NormalDistribution(mean,sd);
                double samplePoint=nd.sample();
                if(samplePoint>=t && samplePoint<=(t+period) && !ExtendedModel.containsElement(opType.getNumber(),excludedOperations)){
                    int time=((int) Math.ceil((samplePoint-t)/2));
                    if(time<10){
                        time=10;
                    }
                    else if(time>55){
                        time=10;
                    }
                    int[] locOp=new int[]{loc,opType.getNumber()};
                    operationTime.put(locOp,time);
                    if (logOperations.get(loc)!=null){
                        logOperations.get(loc).add(opType);
                    }
                    else{
                        logOperations.put(loc,new ArrayList<OperationType>(){{add(opType);}});
                    }
                    int newOp=opType.getPrecedenceOver();
                    int newOp2=opType.getPrecedenceOf();
                    this.findPrecedenceOverDist(loc,newOp);
                    this.findPrecedenceOfDist(loc,newOp2);
                }
            }
        }
    }

    public void findPrecedenceOfDist(int loc,int no2){
        if(no2!=0) {
            OperationType newOp2=findOperationType(no2);
            this.logOperations.get(loc).add(newOp2);
            if (newOp2.getPrecedenceOf() != 0) {
                this.logOperations.get(loc).add(this.findOperationType(newOp2.getPrecedenceOf()));
            }
        }
    }

    public void findPrecedenceOverDist(int loc,int no){
        if(no!=0) {
            OperationType newOp=findOperationType(no);
            this.logOperations.get(loc).add(newOp);
            if (newOp.getPrecedenceOver() != 0) {
                this.logOperations.get(loc).add(this.findOperationType(newOp.getPrecedenceOver()));
            }
        }
    }

    public OperationType findOperationType(int number){
        OperationType matchOp=operationTypes[0];
        for (OperationType op : this.operationTypes){
            if (op.getNumber()==number){
                matchOp=op;
            }
        }
        return matchOp;
    }

    public void writeToFile(String testInstanceName)
            throws IOException {
        FileWriter fileWriter = new FileWriter(testInstanceName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        String tasksForOperation="";
        for (Map.Entry<Integer, List<OperationType>> item : logOperations.entrySet()) {
            int key = item.getKey();
            //System.out.println("Location");
            //System.out.println(key);
            List<OperationType> value = item.getValue();
            //System.out.println("Operations");
            for (OperationType ot:value){
                //System.out.println(ot.getNumber());
            }
            tasksForOperation+=String.valueOf(key);
            for (OperationType opType : value){
                tasksForOperation+=" ";
                tasksForOperation+=String.valueOf(opType.getNumber());
            }
            tasksForOperation+="\n";
        }
        for(Map.Entry<int[], Integer> item : operationTime.entrySet()){
            int[] key = item.getKey();
            int value=item.getValue();
            tasksForOperation+=String.valueOf(0)+" "+String.valueOf(key[0])+" "+String.valueOf(key[1])+" "+String.valueOf(value);
            tasksForOperation+="\n";
        }
        printWriter.print(tasksForOperation);
        printWriter.close();
    }

    public static void main(String[] args) throws IOException {
        int[] loc = new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        OperationGenerator og = new OperationGenerator(5,loc);
        og.createOperationTypes();
        og.generateInstanceFromDistribution();
        og.writeToFile("test_instance_20_locations.txt");
    }
}
