package corp.kairos.adamastor.Collector;

import android.location.Location;
import android.util.Log;


import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.FieldValueUtil;
import org.jpmml.evaluator.InputField;
import org.jpmml.evaluator.ModelEvaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.evaluator.OutputField;
import org.jpmml.evaluator.TargetField;
import org.jpmml.model.FieldUtil;
import org.jpmml.model.SerializationUtil;
import java.util.Calendar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import corp.kairos.adamastor.Collector.DataCleaner;
import corp.kairos.adamastor.Settings.Settings;

public class ModelHandler {

    private Evaluator evaluator;

    public ModelHandler(){
    }

    public int predictContext(Calendar time, String foreground, int activity, int screen_active,
                              int call_active, int music_active, int ring_mode, double longitute,
                              double latitude, String provider){
        int prediction = 1;


        if(evaluator != null) {

            List<InputField> activeFields = evaluator.getActiveFields();

            List<TargetField> predictedFields = evaluator.getTargetFields();

            Map<FieldName, FieldValue> entry = new HashMap<>();

            FieldValue hourValue,minuteValue,weekdayValue,foregroundValue,activityValue,
                    screenActiveValue,callActiveValue,musicActiveValue,ringModeValue,
                    locationValue;

            FieldName hourField = FieldName.create("hour");
            FieldName minuteField = FieldName.create("minute");
            FieldName weekdayField = FieldName.create("weekday");
            FieldName foregroundField = FieldName.create("foreground");
            FieldName activityField = FieldName.create("activity");
            FieldName screenActiveField = FieldName.create("screen_active");
            FieldName callActiveField = FieldName.create("call_active");
            FieldName musicActiveField = FieldName.create("music_active");
            FieldName ringModeField = FieldName.create("ring_mode");
            FieldName locationField = FieldName.create("location");

            for(InputField field : activeFields){
                switch(field.getName().getValue()){
                    case "hour": hourValue = FieldValueUtil.prepareInputValue(field.getField(),
                            field.getMiningField(), DataCleaner.getHour(time));
                            entry.put(hourField,hourValue);
                            break;

                    case "minute": minuteValue = FieldValueUtil.prepareInputValue(field.getField(),
                            field.getMiningField(), DataCleaner.getMinute(time));
                            entry.put(minuteField,minuteValue);
                            break;

                    case "weekday": weekdayValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(), DataCleaner.getWeekday(time));
                            entry.put(weekdayField,weekdayValue);
                            break;

                    case "foreground": foregroundValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(),
                            DataCleaner.getActivity(foreground));
                            entry.put(foregroundField,foregroundValue);
                            break;

                    case "activity": activityValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(), activity);
                            entry.put(activityField,activityValue);
                            break;

                    case "screen_active": screenActiveValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(), screen_active);
                            entry.put(screenActiveField,screenActiveValue);
                            break;

                    case "call_active": callActiveValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(), call_active);
                            entry.put(callActiveField,callActiveValue);
                            break;

                    case "music_active": musicActiveValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(), music_active);
                            entry.put(musicActiveField,musicActiveValue);
                            break;

                    case "ring_mode": ringModeValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(), ring_mode);
                            entry.put(ringModeField,ringModeValue);
                            break;

                    case "location": locationValue = FieldValueUtil.prepareInputValue(
                            field.getField(), field.getMiningField(),
                            DataCleaner.getLocation(longitute, latitude, provider));
                            entry.put(locationField,locationValue);
                            break;

                    default: break;
                }

            }

            Map<FieldName, ?> result = evaluator.evaluate(entry);

            prediction = ((Double)EvaluatorUtil.decode(result.get(predictedFields.get(0).getName()))).intValue();
            Log.i("CollectorServiceLog", "Context =" + String.valueOf(prediction));

        }

        return prediction;
    }

    public void setModel(InputStream is){
        try{
            this.evaluator = this.createEvaluator(is);

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

    private void test(){
        Log.i("test","just testing");
    }

}
