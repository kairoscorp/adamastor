package corp.kairos.adamastor.Collector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class LogDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "LogDatabaseLog";

    public LogDatabaseHelper(Context context) {
        super(context, "ActivityLog.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
    }

    private void createDB (SQLiteDatabase db) {

        // This should be removed at deploy
        db.execSQL("DROP TABLE IF EXISTS 'ServiceLogs'");


        // Create tables
        String LogsTable =
                "CREATE TABLE IF NOT EXISTS 'ServiceLogs' ( "
                + "id  INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "timestamp NUMERIC, "
                + "foreground TEXT, "
                + "activity INTEGER,"
                + "screen_active INTEGER, "
                + "call_active INTEGER, "
                + "music_active INTEGER, "
                + "ring_mode INTEGER, "
                + "latitude REAL, "
                + "longitude REAL, "
                + "provider TEXT, "
                + "account TEXT,"
                + "context INTEGER"
                + ");";

        db.execSQL(LogsTable);
    }


    protected void addLogEntry(String timestamp,
                            String foreground,
                            int activity,
                            int screen_active,
                            int call_active,
                            int music_active,
                            int ring_mode,
                            double latitude,
                            double longitude,
                            String provider,
                            String account,
                            int context){

        SQLiteDatabase activityLogDB = this.getWritableDatabase();

        String ENTRY =
                "INSERT INTO 'ServiceLogs' ("
                + "timestamp, "
                + "foreground, "
                + "activity, "
                + "screen_active, "
                + "call_active, "
                + "music_active, "
                + "ring_mode, "
                + "latitude, "
                + "longitude, "
                + "provider, "
                + "account, "
                + "context) VALUES ("
                + "'" + timestamp + "', "
                + "'" + foreground + "', "
                + "'" + activity + "', "
                + "'" + screen_active + "', "
                + "'" + call_active + "', "
                + "'" + music_active + "', "
                + "'" + ring_mode + "', "
                + "'" + latitude + "', "
                + "'" + longitude + "', "
                + "'" + provider + "', "
                + "'" + account + "', "
                + "'" + context  + "'"
                + ");";

        activityLogDB.execSQL(ENTRY);
    }

    protected Map<String, Long> getContextStatistics() {
        Map<String, Long> result = new TreeMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT logs.context As Context, SUM(logs.id) AS Times " +
                       "FROM 'ServiceLogs' AS logs " +
                       "GROUP BY logs.context;";

        Cursor res = db.rawQuery(query, null );
        res.moveToFirst();

        while(!res.isAfterLast()) {
            String context = String.valueOf(res.getInt(res.getColumnIndex("Context")));

            // Each record means approximately 10 seconds in the context
            int timeSeconds = res.getInt(res.getColumnIndex("Times")) * 10;

            result.put(context, TimeUnit.SECONDS.toMillis(timeSeconds));
            res.moveToNext();
        }

        return result;
    }



    protected List<String> getAllLogs(){
        List<String> result = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM ServiceLogs";

        Cursor res = db.rawQuery(query, null );
        res.moveToFirst();


        while(!res.isAfterLast()){
            String entry = "";
            entry = res.getString(res.getColumnIndex("id"));
            entry = entry + "::" + res.getString(res.getColumnIndex("timestamp"));
            entry = entry + "::" + res.getString(res.getColumnIndex("screen_active"));
            entry = entry + "::" + res.getString(res.getColumnIndex("call_active"));
            entry = entry + "::" + res.getString(res.getColumnIndex("music_active"));
            entry = entry + "::" + res.getString(res.getColumnIndex("ring_mode"));
            entry = entry + "::" + res.getString(res.getColumnIndex("latitude"));
            entry = entry + "::" + res.getString(res.getColumnIndex("longitude"));
            entry = entry + "::" + res.getString(res.getColumnIndex("provider"));
            entry = entry + "::" + res.getString(res.getColumnIndex("account"));
            entry = entry + "::" + res.getString(res.getColumnIndex("context"));
            result.add(entry);
            res.moveToNext();
        }

        return result;
    }

    public File exportDatabaseCSV(){
        File dir = CollectorService.getInstance().getDir("dumps",Context.MODE_PRIVATE);
        File file = new File(dir,"csvfile.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM ServiceLogs",null);
            csvWrite.writeNext(curCSV.getColumnNames());

            while(curCSV.moveToNext())
            {
                String arrStr[] ={
                        curCSV.getString(curCSV.getColumnIndex("id")),
                        curCSV.getString(curCSV.getColumnIndex("timestamp")),
                        String.valueOf(this.getAppKey(curCSV.getString(curCSV.getColumnIndex("foreground")))),
                        curCSV.getString(curCSV.getColumnIndex("activity")),
                        curCSV.getString(curCSV.getColumnIndex("screen_active")),
                        curCSV.getString(curCSV.getColumnIndex("call_active")),
                        curCSV.getString(curCSV.getColumnIndex("music_active")),
                        curCSV.getString(curCSV.getColumnIndex("ring_mode")),
                        curCSV.getString(curCSV.getColumnIndex("latitude")),
                        curCSV.getString(curCSV.getColumnIndex("longitude")),
                        curCSV.getString(curCSV.getColumnIndex("provider")),
                        curCSV.getString(curCSV.getColumnIndex("account")),
                        curCSV.getString(curCSV.getColumnIndex("context"))
                };
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }
        return file;
    }

    public File exportCleanDatabaseCSV(){
        File dir = CollectorService.getInstance().getDir("dumps",Context.MODE_PRIVATE);
        File file = new File(dir,"csvfile.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM ServiceLogs",null);
            csvWrite.writeNext(curCSV.getColumnNames());
            csvWrite.writeNext(new String[] {
                    "timestamp",
                    "foregorund",
                    "activity",
                    "screen_active",
                    "call_active",
                    "music_active",
                    "ring_mode",
                    "location",
                    "context"});

            while(curCSV.moveToNext())
            {
                String arrStr[] ={
                        curCSV.getString(curCSV.getColumnIndex("timestamp")),
                        String.valueOf(this.getAppKey(
                                curCSV.getString(curCSV.getColumnIndex("foreground")))),
                        curCSV.getString(curCSV.getColumnIndex("activity")),
                        curCSV.getString(curCSV.getColumnIndex("screen_active")),
                        curCSV.getString(curCSV.getColumnIndex("call_active")),
                        curCSV.getString(curCSV.getColumnIndex("music_active")),
                        curCSV.getString(curCSV.getColumnIndex("ring_mode")),
                        String.valueOf(DataCleaner.getLocation(
                                curCSV.getDouble(curCSV.getColumnIndex("latitude")),
                                curCSV.getDouble(curCSV.getColumnIndex("longitude")),
                                curCSV.getString(curCSV.getColumnIndex("provider")))),
                        curCSV.getString(curCSV.getColumnIndex("context"))
                };
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }
        return file;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }

    public void addEntryAppLookUp(String app){
        String NEWTABLE = "CREATE TABLE IF NOT EXISTS 'AppLookUp' "
                + "( app TEXT PRIMARY KEY"
                + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(NEWTABLE);

        String query = "SELECT * FROM AppLookUp where app = '" + app + "';";
        SQLiteDatabase db2 = this.getReadableDatabase();
        Cursor cursor = db2.rawQuery(query,null);
        if(cursor.getCount() == 0){
            String ENTRY = "INSERT INTO AppLookUp (" +
                    "app" +
                    ") VALUES ('"+
                    app + "');";

            db.execSQL(ENTRY);
        }
    }

    public int getAppKey(String app){
        String query = "SELECT rowid FROM AppLookUp WHERE app = '" + app + "';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        int result = 0;

        if(cursor.moveToFirst()){
            result = cursor.getInt(cursor.getColumnIndex("rowid"));
        }else{
            addEntryAppLookUp(app);
            result = getAppKey(app);
        }

        return result;
    }

    public boolean setModel(InputStream modelIS){

        boolean result = true;

        String NEWTABLE = "CREATE TABLE IF NOT EXISTS 'Models' "
                + "( model BLOB "
                + ");";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(NEWTABLE);

        String CLEAR_MODELS = "DELETE FROM 'Models'";
        db.execSQL(CLEAR_MODELS);
        String sql = "INSERT INTO Models(model) VALUES(?)";
        SQLiteStatement statement = db.compileStatement(sql);

        try {
            byte[] modelData = inputStreamToByteArray(modelIS);
            statement.bindBlob(1,modelData);
            statement.execute();
        }catch (Exception e){
            Log.i(TAG, "Error Loading Model");
            result = false;
        }

        return result;
    }

    public InputStream getModel(){

        String NEWTABLE = "CREATE TABLE IF NOT EXISTS 'Models' "
                + "( model BLOB "
                + ");";

        SQLiteDatabase dbWrite = this.getWritableDatabase();
        dbWrite.execSQL(NEWTABLE);

        InputStream result = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM 'Models'";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount()> 0 && cursor.moveToFirst()){
            result = new ByteArrayInputStream(cursor.getBlob(0));
        }

        return result;
    }

    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    private static byte[] inputStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1638400];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }
}
