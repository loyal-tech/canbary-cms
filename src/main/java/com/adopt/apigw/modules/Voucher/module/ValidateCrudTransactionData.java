package com.adopt.apigw.modules.Voucher.module;

import com.adopt.apigw.utils.APIConstants;

public class ValidateCrudTransactionData
{
	public static boolean validateStringTypeFieldValue(String fieldValue)
	{
		try
		{
			if(fieldValue == null || fieldValue.isEmpty() || fieldValue.equalsIgnoreCase(APIConstants.BLANK_STRING))
			{
				return false;
			}
			return true;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	public static boolean validateLongTypeFieldValue(Long fieldValue)
	{
		try
		{
			if(fieldValue == null || fieldValue == 0)
			{
				return false;
			}
			return true;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	public static boolean validateBooleanTypeFieldValue(Boolean fieldValue)
	{
		try
		{
			if(fieldValue == null)
			{
				return false;
			}
			return true;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	public static boolean validateIntegerTypeFieldValue(Integer fieldValue)
	{
		try
		{
			if(fieldValue == null || fieldValue == 0)
			{
				return false;
			}
			return true;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
	public String getClassName(Object obj)
	{
		Class<?> class1 = obj.getClass();
		String className = class1.getSimpleName();
		return className;
	}

	public static Long validateMvnoId(Long mvnoId)
	{
		try
		{
			if(mvnoId == null)
			{
				return 2L;
			}
			return mvnoId;
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}
}
