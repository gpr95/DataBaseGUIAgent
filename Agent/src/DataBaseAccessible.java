import java.util.List;
import java.util.Map;

/**
 * @author Grzegorz Przytu³a - Interface as a header for DataBaseAccessor
 */
public interface DataBaseAccessible
{
	/** CREATING TABLE METHODS */
		/** Creating new table */
		public String createNewLedger(String ledgerName,Map<String,ValueType> columnTypes);
	/** ADDING DATA METHODS */
		/** Pushing new record to given table (logRec contains columnname=value Strings */
		public String insertLog(String tabName,String eventTime, String machineId, List<String> logRec);
	/** DELETING DATA */
		public String deleteLog(String tabName, String id);
	/** GETTING DATA METHODS */
		public List<String> getAllRowsFromLedger(String tabName);
	/** METADATA METHODS */
		/** Checking that tabName exists in data base */
		public boolean tableExists(String tabName);
		/** Returns all user table names  */
		public List<String> getTables();
		/** Returns all columns for given table */
		public Map<String,String> getTableColumnsWithType(String tabName);
	/** CHECK METHODS */
		public boolean isDataBaseExisting();
}
