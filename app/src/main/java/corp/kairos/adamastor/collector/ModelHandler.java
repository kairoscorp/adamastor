package corp.kairos.adamastor.collector;

import android.util.Log;

import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.SerializationUtil;

import java.io.InputStream;

public class ModelHandler {

    private Evaluator evaluator;

    public ModelHandler(){
    }

    public int predictContext(){
        int prediction = 1;

        if(evaluator != null) {
            prediction = 2;
        }

        return prediction;
    }

    public void setModel(InputStream is){
        try{
            this. evaluator = this.createEvaluator(is);
        }catch(Exception e){
            Log.i("CollectorServiceLog", "Error Reading Model");
        }
    }

    private Evaluator createEvaluator(InputStream is) throws Exception {
        PMML pmml = SerializationUtil.deserializePMML(is);

        ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();

        ModelEvaluator<?> modelEvaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
        modelEvaluator.verify();

        return modelEvaluator;
    }
}
