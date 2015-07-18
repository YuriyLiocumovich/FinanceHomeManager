package com.example.yuriy_mac.financehomemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by yuriy_mac on 1/18/15.
 */
public class DBAdapter {

    private static boolean isFirstTime = true;

    private static final String SELECT_LAST_ROW_ID = "select last_insert_rowid() as fLastRow";
    private static final String LAST_ROW = "fLastRow";
    private static final String DATABASE_NAME = "Account.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;
    private DataBaseHelper dbHelper;

    public DBAdapter(Context context){
        this.context = context;
        dbHelper = new DataBaseHelper(context);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper{

        Context context;

        public DataBaseHelper(Context context){
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            CategoriesLogic.buildTable(db);
            CashLogic.buildTable(db);
            CreditCardLogic.buildTable(db);
            CheckLogic.buildTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public static class CashLogic{

        private static final String TABLE_NAME = "Sums";
        private static final String KEY_ID = "fID";
        private static final String KEY_CATEGORY_ID = "fCategoryID";
        private static final String KEY_SUM = "fSum";
        private static final String KEY_DATE = "fDate";
        private static final String KEY_Type_ID = "fTypeID";
        private static final String KEY_CREDITCARD_ID = "fCredCardID";
        private static final String KEY_IS_CREDIT = "fIsCredit";
        private static final String TABLE_NAME_PAYMENT = "TypeOfPayment";
        private static final String KEY_TYPE = "fType";

        public static void buildTable(SQLiteDatabase db){
            StringBuilder sb = new StringBuilder ();
            sb.append("CREATE TABLE "+TABLE_NAME+"(");
            sb.append(KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append(KEY_CATEGORY_ID + " INTEGER NOT NULL,");
            sb.append(KEY_SUM +" REAL NOT NULL,");
            sb.append(KEY_DATE+" TEXT NOT NULL,");
            sb.append(KEY_Type_ID +" INTEGER NOT NULL,");
            sb.append(KEY_CREDITCARD_ID + " INTEGER,");
            sb.append(KEY_IS_CREDIT + " INTEGER");
            sb.append(")");
            db.execSQL(sb.toString());

            sb = new StringBuilder();
            sb.append("CREATE TABLE "+TABLE_NAME_PAYMENT+"(");
            sb.append(KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append(KEY_TYPE+" TEXT NOT NULL");
            sb.append(")");
            db.execSQL(sb.toString());

            int count = 0;
            long lastRow = 0;
            Cursor cursor = db.rawQuery("SELECT COUNT(*) AS fCount FROM (SELECT * FROM "+TABLE_NAME_PAYMENT+" WHERE "+KEY_TYPE+"='Cash')", null);
            while(cursor.moveToNext()){
                count = cursor.getInt(cursor.getColumnIndex("fCount"));
            }
            cursor.close();

            if(count == 0) {
                sb = new StringBuilder();
                sb.append("INSERT INTO "+TABLE_NAME_PAYMENT+"("+KEY_TYPE+")");
                sb.append("VALUES('Cash')");
                db.execSQL(sb.toString());
            }
            cursor = db.rawQuery("SELECT COUNT(*) AS fCount FROM (SELECT * FROM "+TABLE_NAME_PAYMENT+" WHERE "+KEY_TYPE+"='C.C.')", null);
            while(cursor.moveToNext()){
                count = cursor.getInt(cursor.getColumnIndex("fCount"));
            }
            cursor.close();

            if(count == 0) {
                sb = new StringBuilder();
                sb.append("INSERT INTO "+TABLE_NAME_PAYMENT+"("+KEY_TYPE+")");
                sb.append("VALUES('C.C.')");
                db.execSQL(sb.toString());
            }
        }

        public static Double GetSumCash(DBAdapter adapter, Category.CategoryType categoryType){
            SQLiteDatabase db = adapter.dbHelper.getReadableDatabase();

            Double sum = 0d;
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT sum("+KEY_SUM+") as "+KEY_SUM+" FROM "+TABLE_NAME+" as S");
            sb.append(" INNER JOIN "+CategoriesLogic.TABLE_NAME+" as C ON (S."+KEY_CATEGORY_ID+" = C."+CategoriesLogic.KEY_ID + ")");
            sb.append(" AND (C."+CategoriesLogic.KEY_CATEGORY_TYPE+" = "+(categoryType == Category.CategoryType.CREDIT?Integer.valueOf(0):Integer.valueOf(1))+")");
            sb.append(" INNER JOIN "+TABLE_NAME_PAYMENT+" ON TypeOfPayment.fID = S."+KEY_Type_ID);
            sb.append(" WHERE date(S."+KEY_DATE+") BETWEEN ? AND ?");
            sb.append(" AND S."+KEY_Type_ID+" = 1");
            Cursor cursor = db.rawQuery(sb.toString(),new String[]{Tools.getDateForDB(Global.getStartDate()),Tools.getDateForDB(Global.getEndDate())});
            while(cursor.moveToNext()){
                sum = cursor.getDouble(cursor.getColumnIndex(KEY_SUM));
            }
            cursor.close();
            db.close();
            return sum;
        }

        public static void insert(DBAdapter adapter, Sum sum){
            SQLiteDatabase db = adapter.dbHelper.getWritableDatabase();
            insert(db,sum);
        }

        public static long insert(SQLiteDatabase db, Sum sum){
            long lastRow = 0;

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO " + TABLE_NAME+"("+KEY_CATEGORY_ID+","+ KEY_SUM+","+KEY_DATE+","+KEY_Type_ID+","+KEY_CREDITCARD_ID+","+KEY_IS_CREDIT+")");
            sb.append("VALUES (");
            sb.append("?,?,?,?,?,?)");
            db.execSQL(sb.toString(), new Object[]{sum.getCategoryID(), sum.getSum(), sum.getDate(), sum.getType(), sum.getCreditCardId(), sum.getIsCredit()});

            Cursor cursor = db.rawQuery(SELECT_LAST_ROW_ID,null);
            while(cursor.moveToNext()){
                lastRow = cursor.getLong(cursor.getColumnIndex(LAST_ROW));
            }
            cursor.close();
            return lastRow;
        }
    }

    public static class CategoriesLogic{

        private static final String TABLE_NAME = "Categories";
        private static final String KEY_CATEGORY = "fCategory";
        private static final String KEY_ID = "fID";
        private static final String KEY_CATEGORY_TYPE = "fCategoryType";

        public static void buildTable(SQLiteDatabase db){
            StringBuilder sb = new StringBuilder ();
            sb.append("CREATE TABLE "+TABLE_NAME +"(");
            sb.append(KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append(KEY_CATEGORY + " TEXT NOT NULL,");
            sb.append(KEY_CATEGORY_TYPE + " INTEGER");
            sb.append(")");
            db.execSQL(sb.toString());
        }

        public static long insert(DBAdapter adapter, Category category){
            SQLiteDatabase db = adapter.dbHelper.getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            int count = 0;
            long lastRow = 0;
            Cursor cursor = db.rawQuery("SELECT COUNT(*) AS fCount FROM (SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_CATEGORY+"=?)",new String[]{category.getCategoryText()});
            while(cursor.moveToNext()){
                count = cursor.getInt(cursor.getColumnIndex("fCount"));
            }
            cursor.close();
            if(count == 0) {
                sb.append("INSERT INTO " + TABLE_NAME + "(" + KEY_CATEGORY + ", " + KEY_CATEGORY_TYPE + ")");
                sb.append("VALUES (?,?)");
                db.execSQL(sb.toString(), new Object[]{category.getCategoryText(),
                        category.getCategoryType() == Category.CategoryType.DEBIT ? "1" : "0"});
                cursor = db.rawQuery(SELECT_LAST_ROW_ID,null);
                while(cursor.moveToNext()){
                     lastRow = cursor.getLong(cursor.getColumnIndex(LAST_ROW));
                }
                cursor.close();
            }
            return lastRow;
        }

        public static ArrayList<Category> getCategoryList(Context context, DBAdapter adapter, boolean includeDebit) {
            //TODO Get List from DB

            SQLiteDatabase db = adapter.dbHelper.getReadableDatabase();
            ArrayList<Category> categoriesList = new ArrayList<Category>();

            Cursor cursor = db.rawQuery("SELECT "+KEY_ID+", "+KEY_CATEGORY+", "+KEY_CATEGORY_TYPE+" FROM  "+TABLE_NAME+" ORDER BY "+KEY_CATEGORY, null);
            while(cursor.moveToNext())
            {
                String category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY));
                int categoryType = cursor.getInt(cursor.getColumnIndex(KEY_CATEGORY_TYPE));
                if(!includeDebit &&
                        categoryType == 1)
                    continue;
                categoriesList.add(new Category(context,category,categoryType==1? Category.CategoryType.DEBIT: Category.CategoryType.CREDIT,cursor.getInt(cursor.getColumnIndex(KEY_ID))));
            }
            cursor.close();
            return categoriesList;
        }
    }

    public static class CreditCardLogic{

        private static final String TABLE_NAME = "CreditCards";
        private static final String KEY_CC_NAME = "fName";
        private static final String KEY_ID = "fID";
        private static final String KEY_CC_TOKEN = "fToken";
        private static final String KEY_DEL = "fDEL";

        private static final String TABLE_NAME_CREDIT = "Credit";
        private static final String KEY_TOTAL_CREDIT = "fTotalCred";
        private static final String KEY_NUM_OF_MONTHS = "fNumOfMonths";
        private static final String KEY_PAYMENT_PER_MONTH = "fPaymentPerMonth";
        private static final String KEY_SUMS_ID = "fSumsID";
        private static final String KEY_FROM_DATE = "fFromDate";
        private static final String KEY_TO_DATE = "fToDate";

        public static void buildTable(SQLiteDatabase db){
            StringBuilder sb = new StringBuilder ();
            sb.append("CREATE TABLE "+TABLE_NAME+"(");
            sb.append(KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append(KEY_CC_NAME+" TEXT NOT NULL,");
            sb.append(KEY_CC_TOKEN+" TEXT NULL");
            sb.append(")");
            db.execSQL(sb.toString());

            sb = new StringBuilder();
            sb.append("CREATE TABLE "+TABLE_NAME_CREDIT+"(");
            sb.append(KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append(KEY_TOTAL_CREDIT+" REAL NOT NULL,");
            sb.append(KEY_NUM_OF_MONTHS+ " INTEGER NOT NULL,");
            sb.append(KEY_PAYMENT_PER_MONTH+" REAL NOT NULL,");
            sb.append(KEY_SUMS_ID+" INTEGER NOT NULL,");
            sb.append(KEY_FROM_DATE+" TEXT NOT NULL,");
            sb.append(KEY_TO_DATE+" TEXT NOT NULL");
            sb.append(")");
            db.execSQL(sb.toString());
        }

        public static void insert(DBAdapter dbAdapter, CreditCard creditCard){
            SQLiteDatabase db = dbAdapter.dbHelper.getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT COUNT(*) AS fCount FROM "+TABLE_NAME+" WHERE "+KEY_CC_NAME+"= ? AND "+KEY_CC_TOKEN+"= ?");
            Cursor cursor = db.rawQuery(sb.toString(),new String[]{creditCard.getName(), creditCard.getToken()});
            int count = 0;
            while(cursor.moveToNext()){
                count = cursor.getInt(cursor.getColumnIndex("fCount"));
            }
            if(count == 0) {
                sb = new StringBuilder();
                sb.append("INSERT INTO " + TABLE_NAME + "(" + KEY_CC_NAME + ", " + KEY_CC_TOKEN + ")");
                sb.append("VALUES(?,?)");
                db.execSQL(sb.toString(), new Object[]{creditCard.getName(), creditCard.getToken()});
            }
        }

        public static ArrayList<CreditCard> getCreditCardList(Context context, DBAdapter adapter) {
            SQLiteDatabase db = adapter.dbHelper.getReadableDatabase();
            ArrayList<CreditCard> creditCardList = new ArrayList<CreditCard>();
            Cursor cursor = db.rawQuery("SELECT * FROM  "+TABLE_NAME, null);
            while(cursor.moveToNext())
            {
                CreditCard creditCard = new CreditCard(cursor.getString(cursor.getColumnIndex(KEY_CC_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_CC_TOKEN)), cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                creditCardList.add(creditCard);
            }
            cursor.close();
            return creditCardList;
        }

        public static void insertSum(DBAdapter dbAdapter, Sum sum){
            SQLiteDatabase db = dbAdapter.dbHelper.getWritableDatabase();
            long sumKeyID = CashLogic.insert(db, sum);
            if(sum.getIsCredit() == 1){
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO "+ TABLE_NAME_CREDIT+"(");
                sb.append(KEY_TOTAL_CREDIT+","+ KEY_NUM_OF_MONTHS+","+KEY_PAYMENT_PER_MONTH+","+KEY_SUMS_ID+","+KEY_FROM_DATE+","+KEY_TO_DATE+")");
                sb.append("VALUES(?,?,?,?,?,?)");
                db.execSQL(sb.toString(),new Object[]{sum.getSumCredit().getTotalPaid(),sum.getSumCredit().getNumberOfMonths(),sum.getSumCredit().getAdditionalPayments(),
                    sumKeyID,sum.getDate(),sum.getSumCredit().getToDate()});
            }
        }

        public static ArrayList<CreditCardInfo> getCreditCardInfoList(Context context, DBAdapter dbAdapter){
            SQLiteDatabase db = dbAdapter.dbHelper.getReadableDatabase();
            ArrayList<CreditCardInfo> creditCardInfoList = new ArrayList<CreditCardInfo>();
            CreditCardInfo creditCardInfo;
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT SUMS_1."+CashLogic.KEY_SUM+" AS thisMonth, SUMS_1."+CashLogic.KEY_CREDITCARD_ID+", SUMS_1."+KEY_CC_NAME+", SUMS_1."+
                KEY_CC_TOKEN+", SUMS_2."+CashLogic.KEY_SUM+" AS allMonths FROM");
            sb.append("(SELECT SUM("+CashLogic.KEY_SUM+") AS "+CashLogic.KEY_SUM+","+CashLogic.KEY_CREDITCARD_ID+","+KEY_CC_NAME+","+KEY_CC_TOKEN);
            sb.append(" FROM");
            sb.append("(SELECT IFNULL("+CashLogic.TABLE_NAME+"."+CashLogic.KEY_SUM+",0) AS "+CashLogic.KEY_SUM);
            sb.append(","+TABLE_NAME+"."+KEY_ID+" AS "+CashLogic.KEY_CREDITCARD_ID);
            sb.append(","+TABLE_NAME+"."+KEY_CC_NAME+","+TABLE_NAME+"."+KEY_CC_TOKEN);
            sb.append(" FROM "+CashLogic.TABLE_NAME);
            sb.append(" INNER JOIN "+TABLE_NAME+" ON "+TABLE_NAME+"."+KEY_ID+"="+CashLogic.TABLE_NAME+"."+CashLogic.KEY_CREDITCARD_ID);
            sb.append(" WHERE ("+CashLogic.KEY_DATE+" BETWEEN ? AND ?)");//'2015-06-01','2015-06-30'
            sb.append(" AND "+CashLogic.KEY_Type_ID+"=2"); //CC
            sb.append(" UNION");
            sb.append(" SELECT IFNULL(C."+KEY_PAYMENT_PER_MONTH+",0) as "+CashLogic.KEY_SUM);
            sb.append(",CC."+KEY_ID+" AS "+CashLogic.KEY_CREDITCARD_ID+",CC."+KEY_CC_NAME+",CC."+KEY_CC_TOKEN);
            sb.append(" FROM "+TABLE_NAME+" AS CC");
            sb.append(" LEFT JOIN "+CashLogic.TABLE_NAME+" ON "+CashLogic.TABLE_NAME+"."+CashLogic.KEY_CREDITCARD_ID+"=CC."+KEY_ID+" AND "+
                CashLogic.TABLE_NAME+"."+CashLogic.KEY_IS_CREDIT+"=1 AND strftime('%m',?)-strftime('%m',"+CashLogic.TABLE_NAME+"."+CashLogic.KEY_DATE+") >0");//'2015-05-01'
            sb.append(" LEFT JOIN "+TABLE_NAME_CREDIT+" AS C ON C."+KEY_SUMS_ID+"="+CashLogic.TABLE_NAME+"."+CashLogic.KEY_ID+
                " AND strftime('%m',C."+KEY_TO_DATE+")-strftime('%m',?)>-1");//'2015-05-01'
            sb.append(") GROUP BY "+CashLogic.KEY_CREDITCARD_ID);
            sb.append(") AS SUMS_1");
            sb.append(" INNER JOIN");
            sb.append("(SELECT SUM("+CashLogic.KEY_SUM+") AS "+CashLogic.KEY_SUM+","+CashLogic.KEY_CREDITCARD_ID+","+KEY_CC_NAME+","+KEY_CC_TOKEN);
            sb.append(" FROM");
            sb.append("(SELECT IFNULL("+CashLogic.TABLE_NAME+"."+CashLogic.KEY_SUM+",0) AS "+CashLogic.KEY_SUM);
            sb.append(","+TABLE_NAME+"."+KEY_ID+" AS "+CashLogic.KEY_CREDITCARD_ID+","+TABLE_NAME+"."+KEY_CC_NAME+","+TABLE_NAME+"."+KEY_CC_TOKEN);
            sb.append(" FROM "+CashLogic.TABLE_NAME);
            sb.append(" INNER JOIN "+TABLE_NAME+" ON "+TABLE_NAME+"."+KEY_ID+"="+CashLogic.TABLE_NAME+"."+CashLogic.KEY_CREDITCARD_ID);
            sb.append(" WHERE ("+CashLogic.KEY_DATE+" BETWEEN ? AND ?)");//'2015-06-01','2015-06-30'
            sb.append(" AND "+CashLogic.KEY_Type_ID+"=2");//CC
            sb.append(" AND "+CashLogic.KEY_IS_CREDIT+"=0");
            sb.append(" UNION");
            sb.append(" SELECT (CASE WHEN strftime('%m',?)-strftime('%m',"+CashLogic.TABLE_NAME+"."+CashLogic.KEY_DATE+") = 0 THEN C."+KEY_TOTAL_CREDIT);//'2015-06-01'
            sb.append(" ELSE IFNULL(C."+KEY_PAYMENT_PER_MONTH+"*(strftime('%m',C."+KEY_TO_DATE+")-strftime('%m',?)+1),0) END) as "+CashLogic.KEY_SUM);//'2015-06-30'
            sb.append(",CC."+KEY_ID+" AS "+CashLogic.KEY_CREDITCARD_ID+",CC."+KEY_CC_NAME+",CC."+KEY_CC_TOKEN);
            sb.append(" FROM "+TABLE_NAME+ " AS CC");
            sb.append(" LEFT JOIN "+CashLogic.TABLE_NAME+" ON "+CashLogic.TABLE_NAME+"."+CashLogic.KEY_CREDITCARD_ID+"=CC."+KEY_ID+" AND "+
                CashLogic.TABLE_NAME+"."+CashLogic.KEY_IS_CREDIT+"=1 AND strftime('%m',?)-strftime('%m',"+CashLogic.TABLE_NAME+"."+CashLogic.KEY_DATE+") >-1");//'2015-06-01'
            sb.append(" LEFT JOIN "+TABLE_NAME_CREDIT+" AS C ON C."+KEY_SUMS_ID+"="+CashLogic.TABLE_NAME+"."+CashLogic.KEY_ID+
                " AND strftime('%m',C."+KEY_TO_DATE+")-strftime('%m',?)>-1");//'2015-06-30'
            sb.append(") GROUP BY "+CashLogic.KEY_CREDITCARD_ID+") AS SUMS_2 ON SUMS_2."+CashLogic.KEY_CREDITCARD_ID+"=SUMS_1."+CashLogic.KEY_CREDITCARD_ID);


            String startDate = Tools.getDateForDB(Global.getStartDate());
            String endDate = Tools.getDateForDB(Global.getEndDate());

            Cursor cursor = db.rawQuery(sb.toString(),new String[]{startDate,endDate,startDate,startDate,startDate,endDate,startDate,endDate,startDate,endDate});
            while (cursor.moveToNext()){
                String strName = cursor.getString(cursor.getColumnIndex(KEY_CC_NAME));
                String strToken = cursor.getString(cursor.getColumnIndex(KEY_CC_TOKEN));
                long id = cursor.getLong(cursor.getColumnIndex(CashLogic.KEY_CREDITCARD_ID));
                double currentMonth = cursor.getDouble(cursor.getColumnIndex("thisMonth"));
                double total = cursor.getDouble(cursor.getColumnIndex("allMonths"));
                creditCardInfo = new CreditCardInfo(new CreditCard(cursor.getString(cursor.getColumnIndex(KEY_CC_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_CC_TOKEN)),cursor.getLong(cursor.getColumnIndex(CashLogic.KEY_CREDITCARD_ID))),
                cursor.getDouble(cursor.getColumnIndex("thisMonth")),cursor.getDouble(cursor.getColumnIndex("allMonths")));
                creditCardInfoList.add(creditCardInfo);
            }
            cursor.close();
            return creditCardInfoList;
        }
    }

    public static class CheckLogic{
        private static final String TABLE_NAME = "Checks";
        private static final String KEY_ID = "fID";
        private static final String KEY_PAY_TO = "fPayTo";
        private static final String KEY_PAY_FOR = "fPayFor";
        private static final String KEY_SUM = "fSum";
        private static final String KEY_DATE = "fDate";
        private static final String KEY_NUMBER = "fNumber";
        private static final String KEY_CATEGORY_ID = "fCategoryID";

        public static void buildTable(SQLiteDatabase db){
            StringBuilder sb = new StringBuilder ();
            sb.append("CREATE TABLE "+TABLE_NAME+"(");
            sb.append(KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
            sb.append(KEY_CATEGORY_ID+" INTEGER NOT NULL,");
            sb.append(KEY_PAY_TO+" TEXT NOT NULL,");
            sb.append(KEY_PAY_FOR+" TEXT NULL,");
            sb.append(KEY_SUM+" REAL NOT NULL,");
            sb.append(KEY_DATE+" TEXT NOT NULL,");
            sb.append(KEY_NUMBER+" TEXT NOT NULL");
            sb.append(")");
            db.execSQL(sb.toString());
        }

        public static void insert(DBAdapter dbAdapter, Check check){
            SQLiteDatabase db = dbAdapter.dbHelper.getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO " + TABLE_NAME + "("+KEY_CATEGORY_ID+","+ KEY_PAY_TO+","+KEY_PAY_FOR+"," +
                KEY_SUM+","+KEY_DATE+","+KEY_NUMBER+")");
            sb.append("VALUES(?,?,?,?,?,?)");
            db.execSQL(sb.toString(), new Object[]{check.getCategoryID(), check.getPayTo(),check.getPayFor(),
                check.getSum(),check.getDate(),check.getNumber()});
        }

        public static ArrayList<Check> getCheckList(Context context1, DBAdapter dbAdapter){
            SQLiteDatabase db = dbAdapter.dbHelper.getReadableDatabase();
            ArrayList<Check> checkArrayList = new ArrayList<Check>();
            Check check;
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT IFNULL(CHECKS_1."+KEY_SUM+",0) AS fThisMonth, CHECKS_2."+KEY_DATE+",CHECKS_2."+KEY_ID+
                ",IFNULL(CHECKS_2."+KEY_SUM+",0) AS fTotalSum");
            sb.append(" FROM");
            sb.append("(SELECT "+KEY_SUM+","+ KEY_DATE+","+KEY_ID);
            sb.append(" FROM "+TABLE_NAME);
            sb.append(" WHERE STRFTIME('%m',"+KEY_DATE+")-STRFTIME('%m',?)=0");//'2015-08-01'
            sb.append(" UNION");
            sb.append(" SELECT "+KEY_SUM+","+KEY_DATE+","+KEY_ID);
            sb.append(" FROM "+TABLE_NAME);
            sb.append(" WHERE STRFTIME('%m',"+KEY_DATE+")-STRFTIME('%m',?) > 0");//'2015-08-01'
            sb.append(") AS CHECKS_2");
            sb.append(" LEFT JOIN");
            sb.append("(SELECT "+KEY_SUM+","+KEY_DATE+","+KEY_ID);
            sb.append(" FROM "+TABLE_NAME+" AS CHECKS_1");
            sb.append(" WHERE STRFTIME('%m',"+KEY_DATE+")-STRFTIME('%m',?)=0");//'2015-08-01'
            sb.append(") AS CHECKS_1 ON CHECKS_1."+KEY_ID+"=CHECKS_2."+KEY_ID);
            String date = Tools.getDateForDB(Global.getStartDate());
                    Cursor cursor = db.rawQuery(sb.toString(),new String[]{date, date, date});
            while(cursor.moveToNext()){
                check = new Check(cursor.getDouble(cursor.getColumnIndex("fThisMonth")),cursor.getDouble(cursor.getColumnIndex("fTotalSum")));
                checkArrayList.add(check);
            }
            cursor.close();
            return  checkArrayList;
        }
    }
}
