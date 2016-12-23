public class ProtocolConsts
{
	/** HEADERS */
	public static final String ADD_RECORD_HEADER = "ADD_RECORD";
	public static final String CREATE_TABLE_HEADER ="CREATE_TABLE";
	public static final String GET_TABLES_HEADER ="GET_TABLES";
	public static final String GET_CLUMNS_WITH_META_HEADER ="GET_COLUMNS_WITH_META";
	public static final String GET_CLUMNS_AND_VALUES_HEADER ="GET_COLUMNS_AND_VALUES";
	public static final String DELETE_HEADER ="DELETE_RECORD";
	
	/** SERVER CONFIGURATION */
	public static final int PORT_NR = 6666;
	public static final int MAX_CLIENTS = 15;
	
	
	private ProtocolConsts()
	{}
}
