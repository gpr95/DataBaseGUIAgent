
public class ProtocolConsts
{
	/** HEADERS */
	public static final String ADD_RECORD_HEADER = "ADD_RECORD";
	public static final String CREATE_TABLE_HEADER ="CREATE_TABLE";
	public static final String GET_TABLES_HEADER ="GET_TABLES";
	public static final String GET_CLUMNS_WITH_META_HEADER ="GET_COLUMNS_WITH_META";

	/** STANDARD COLUMNS TO SEND */
	public static final String EVENT_TIME_NAME = "EVENT_TIME";
	public static final String MACHINE_ID_NAME = "REPORTING_MACHINE_ID";
	public static final String EVENT_TIME_TYPE = "VARCHAR2(30)";
	public static final String MACHINE_ID_TYPE = "VARCHAR2(30)";
	
	/** OTHERS */
	public static final String TAB_NAME = "TAB_NAME";
	public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	
	
	private ProtocolConsts()
	{}
}
