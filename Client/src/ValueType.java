public enum ValueType
{
	INT(0,"NUMBER(6)"),
	VARCHAR10(1,"VARCHAR2(10)"),
	VARCHAR15(2,"VARCHAR2(15)"),
	VARCHAR30(3,"VARCHAR2(30)");
	private int intValue;
	private String nameInDB;
	private ValueType(int value, String typeName)
	{
		this.intValue = value;
		this.nameInDB = typeName;
	}
	public static ValueType fromIntValue(int intValue)
	{
		for (ValueType r : ValueType.values())
		{
			if (r.intValue == intValue)
			{
				return r;
			}
		}

		String message = "Nie ma definicji sprawozdania o wartoœci numerycznej " + intValue;
		throw new RuntimeException(message);
	}
	public static ValueType fromStringValue(String stringValue)
	{
		for (ValueType r : ValueType.values())
		{
			if (r.nameInDB.equals(stringValue))
			{
				return r;
			}
		}

		String message = "Nie ma definicji sprawozdania o wartoœci tekstowej " + stringValue;
		throw new RuntimeException(message);
	}
	public int toInt()
	{
		return intValue;
	}

	@Override
	public String toString()
	{
		return nameInDB;
	}
	
	public static String[] getValues()
	{
		String[] result = new String[ValueType.values().length];
		int i =0;
		for (ValueType r : ValueType.values())
		{
			result[i++] = r.toString();
		}
		return result;
	}
}
