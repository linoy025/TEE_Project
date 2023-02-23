package TryTee;

import com.intel.crypto.DataMigration;
import com.intel.crypto.NotInitializedException;
import com.intel.crypto.SymmetricBlockCipherAlg;
import com.intel.langutil.ArrayUtils;
import com.intel.langutil.TypeConverter;
import com.intel.util.*;

//
// Implementation of DAL Trusted Application: TryTee 
//
// **************************************************************************************************
// NOTE:  This default Trusted Application implementation is intended for DAL API Level 7 and above
// **************************************************************************************************

public class TryTee extends IntelApplet {

	private static final int CMD_SAVE_DATA = 1;
	private static final int CMD_LOAD_DATA = 2;
	
	  private static final int CMD_LOAD_DATA_USING_SYMMETRIC_CIPHER_API = 3;
	  private static final int CMD_LOAD_DATA_USING_DATA_MIGRATION_API = 4;
	  
	 //Status codes
	 private static final int STATUS_INVALID_DATA_FORMAT = -10;
	  private static final int STATUS_DATA_MIGRATION_NOT_INITIALIZED = -20;
	 

	private static final byte[] magicStr = "--- BEGIN ---".getBytes();
	private static final int SAVE_FIRST_LOCATION = 5;
	private static final int SAVE_APPEND_LOCATION = 6;
	private static final int CHECK_ID_IN_LOCATION = 7;

	/**
	 * This method will be called by the VM when a new session is opened to the
	 * Trusted Application and this Trusted Application instance is being created to
	 * handle the new session. This method cannot provide response data and
	 * therefore calling setResponse or setResponseCode methods from it will throw a
	 * NullPointerException.
	 * 
	 * @param request the input data sent to the Trusted Application during session
	 *                creation
	 * 
	 * @return APPLET_SUCCESS if the operation was processed successfully, any other
	 *         error status code otherwise (note that all error codes will be
	 *         treated similarly by the VM by sending "cancel" error code to the SW
	 *         application).
	 */
	public int onInit(byte[] request) {
		DebugPrint.printString("Hello, DAL!");
		return APPLET_SUCCESS;
	}

	/**
	 * This method will be called by the VM to handle a command sent to this Trusted
	 * Application instance.
	 * 
	 * @param commandId the command ID (Trusted Application specific)
	 * @param request   the input data for this command
	 * @return the return value should not be used by the applet
	 */
	public int invokeCommand(int commandId, byte[] request) {

		DebugPrint.printString("Received command Id: " + commandId + ".");
		if (request != null) {
			DebugPrint.printString("Received buffer:");
			DebugPrint.printBuffer(request);
		}
		int res=0;

		switch (commandId) {
		case CMD_SAVE_DATA:
			res = saveData(request);
			break;

		case CMD_LOAD_DATA:
			res = loadData(request);
			break;
			
		case SAVE_FIRST_LOCATION:
			//res = loadData(request);
			//in id, location 
			//out - encrypted file with first location 

		//file:
			//first 10 bytes - id 
			// each 10 bytes - location 
			
			
		case SAVE_APPEND_LOCATION:
			//in - id, encrypted locations file 
			// out encrypted(file + new location)
			
		case CHECK_ID_IN_LOCATION:
			//bool 
//res = loadData(request);
			//break;

		}


		/*
		 * To return the response data to the command, call the setResponse method
		 * before returning from this method. Note that calling this method more than
		 * once will reset the response data previously set.
		 */
		//setResponse(res, 0, res.length);

		/*
		 * In order to provide a return value for the command, which will be delivered
		 * to the SW application communicating with the Trusted Application,
		 * setResponseCode method should be called. Note that calling this method more
		 * than once will reset the code previously set. If not set, the default
		 * response code that will be returned to SW application is 0.
		 */
		setResponseCode(res);

		/*
		 * The return value of the invokeCommand method is not guaranteed to be
		 * delivered to the SW application, and therefore should not be used for this
		 * purpose. Trusted Application is expected to return APPLET_SUCCESS code from
		 * this method and use the setResposeCode method instead.
		 */
		return APPLET_SUCCESS;
	}
	
	

	
	
	

	/**
	 * This method encrypts the received data with Platform-binded key Note that in
	 * real use case - TA should enforce saving the data.
	 * 
	 * @param commandData data to encrypt
	 * @return IntelApplet.APPLET_SUCCESS for success,
	 *         IntelApplet.APPLET_ERROR_GENERIC otherwise
	 */
	private int saveData(byte[] commandData) {
		int status = IntelApplet.APPLET_ERROR_GENERIC;
		try {
			if (commandData == null) {
				throw new Exception("An empty buffer was sent.");
			}

			// Create Platform-Binded key cipher
			SymmetricBlockCipherAlg SymmetricCipher = SymmetricBlockCipherAlg
					.create(SymmetricBlockCipherAlg.ALG_TYPE_PBIND_AES_256_CBC);

			// The encrypted data should be saved in a known structure
			// In this sample the data structure is:
			// Prefix of a magic string: "--- BEGIN ---"
			// 4 bytes that contain the buffer size
			// Data buffer
			int dataSize = magicStr.length + TypeConverter.INT_BYTE_SIZE + commandData.length;
			// Align the data size to block buffer size
			short blockSize = SymmetricCipher.getBlockSize();
			if (dataSize % blockSize != 0)
				dataSize = dataSize + blockSize - (dataSize % blockSize);

			// An array for the data to encrypt
			byte[] data = new byte[dataSize];
			// An array for the encrypted data
			// Data size stays the same after encryption because we are using a symmetric
			// key
			byte[] response = new byte[dataSize];

			// Copy the magic string to the beginning of the data
			ArrayUtils.copyByteArray(magicStr, 0, data, 0, magicStr.length);
			// Next 4 bytes contain the data size
			TypeConverter.intToBytes(commandData.length, data, magicStr.length);
			// Copy the data buffer to encrypt to the end of the buffer
			ArrayUtils.copyByteArray(commandData, 0, data, magicStr.length + TypeConverter.INT_BYTE_SIZE,
					commandData.length);

			// Encrypt the data using the platform-binded Key
			SymmetricCipher.encryptComplete(data, (short) 0, (short) dataSize, response, (short) 0);

			// You can get here the security version number (SVN) of the platform's security
			// engine using the API:
			// PlatformInfo.getSecurityEngineSVN()
			// then you can save it for further comparison.

			DebugPrint.printString("Encrypted data:");
			DebugPrint.printBuffer(response);

			// i added - here to save response in file
			
			
			
			
			
			
			
			// Return the encrypted data to the host application
			setResponse(response, 0, dataSize);

			status = IntelApplet.APPLET_SUCCESS;
		} catch (Exception ex) {
			String error = "ERROR: failed to save data\n" + ex.getMessage();
			DebugPrint.printString(error);
			setResponse(error.getBytes(), 0, error.length());
			return status;
		}
		return status;
	}

	/**
	 * This method decrypts the data using the correct API; SymmetricBlockCipher or DataMigration
	 * @param commandData	data to decrypt
	 * @return IntelApplet.APPLET_SUCCESS for success, IntelApplet.APPLET_ERROR_GENERIC otherwise
	 */
	private int loadData(byte[] commandData) {
		// You can get here the security version (SVN) of the platform's security engine using the API:
		// PlatformInfo.getSecurityEngineSVN()
		// then you can compare it to the previous known SVN and check if the SVN has been increased
		
		int res = loadDataUsingSymmetricCipherApi(commandData);
		if(res == STATUS_INVALID_DATA_FORMAT)
			res = loadDataUsingDataMigrationApi(commandData);
		return res;
	}
	
	/**
	 * This method decrypts the sent data using the Pbind Symmetric key stored in the Flash. 
	 * If the security engine SVN has been increased and those the Pbind key has changed, 
	 * the decryption fails and the 'decrypted' data structure is invalid => IntelApplet.APPLET_ERROR_GENERIC is returned.
	 * Note that in a real use case - if the data is not loaded, the TA should not perform any further functionality.
	 * @param commandData data to decrypt and load
	 * @return IntelApplet.APPLET_SUCCESS for success, IntelApplet.APPLET_ERROR_GENERIC otherwise
	 */
	private int loadDataUsingSymmetricCipherApi(byte[] commandData)
	{
		int status = IntelApplet.APPLET_ERROR_GENERIC;
			try
			{
				if (commandData == null)
				{
					throw new Exception("A Null buffer was sent.");
				}
				byte[] decryptedData = new byte[commandData.length];

				// Decrypt the data using the platform-binded Key
				SymmetricBlockCipherAlg SymmetricCipher = SymmetricBlockCipherAlg
						.create(SymmetricBlockCipherAlg.ALG_TYPE_PBIND_AES_256_CBC);
				SymmetricCipher.decryptComplete(commandData, (short) 0,
						(short) commandData.length, decryptedData, (short) 0);

				// Check that the decrypted data is in the correct structure (starts with the magic prefix)
				boolean res = ArrayUtils.compareByteArray(decryptedData, 0, magicStr, 0, magicStr.length);
				if(!res)
				{
					status = STATUS_INVALID_DATA_FORMAT;
					throw new Exception("Failed to decrypt - decrypted data format is invalid.");
				}
				
				// Extract the size of the buffer
				int size = TypeConverter.bytesToInt(decryptedData, magicStr.length);
				byte[] decryptedDataSub = new byte[size];
				ArrayUtils.copyByteArray(decryptedData, magicStr.length + TypeConverter.INT_BYTE_SIZE, decryptedDataSub, 0, size);
				
				DebugPrint.printString("Decrypted data:");
				DebugPrint.printBuffer(decryptedDataSub);
				
				// Return the decrypted data to the host application
				setResponse(decryptedData, magicStr.length + TypeConverter.INT_BYTE_SIZE, size);

				status = IntelApplet.APPLET_SUCCESS;
			}
			catch (Exception ex)
			{
				String error = "ERROR: Failed to decrypt data\n" + ex.getMessage();
				DebugPrint.printString(error);
				setResponse(error.getBytes(), 0, error.length());
				return status;
			}
		
		return status;
	}

	/**
	 * This method decrypts the sent data using the DataMigration API after security engine SVN increment.
	 * Note that in a real use case - if the data is not loaded the TA should not perform any further functionality.
	 * @param commandData 	data to decrypt
	 * @return IntelApplet.APPLET_SUCCESS for success, IntelApplet.APPLET_ERROR_GENERIC otherwise
	 */
	private int loadDataUsingDataMigrationApi(byte[] commandData)
	{
		int status = IntelApplet.APPLET_ERROR_GENERIC;
		try
			{
				if (commandData == null)
				{
					throw new Exception("A Null buffer was sent.");
				}
				byte[] decryptedData = new byte[commandData.length];

				// Decrypt the data with the old platform-binded key by DataMigration API
				DataMigration dataMigration = DataMigration.create(DataMigration.DECRYPT_ALG_TYPE_PBIND_AES_256_CBC);
				dataMigration.decryptComplete(commandData, (short) 0,
						(short) commandData.length, decryptedData, (short) 0);
			
				// Check that the decrypted data is in the correct structure (starts with the magic prefix)
				boolean res = ArrayUtils.compareByteArray(decryptedData, 0, magicStr, 0, magicStr.length);
				if(!res)
				{
					status = STATUS_INVALID_DATA_FORMAT;
					throw new Exception("Failed to decrypt - decrypted data format is invalid.");
				}
				
				// Extract the size of the buffer
				int size = TypeConverter.bytesToInt(decryptedData, magicStr.length);
				
				byte[] decryptedDataSub = new byte[size];
				ArrayUtils.copyByteArray(decryptedData, magicStr.length + TypeConverter.INT_BYTE_SIZE, decryptedDataSub, 0, size);
				
				DebugPrint.printString("Decrypted data:");
				DebugPrint.printBuffer(decryptedDataSub);
				
				// Return the decrypted data to the host application
				setResponse(decryptedData, magicStr.length + TypeConverter.INT_BYTE_SIZE, size);

				status = IntelApplet.APPLET_SUCCESS;
			}
			catch (NotInitializedException ex) {
				status = STATUS_DATA_MIGRATION_NOT_INITIALIZED;
				String error = "ERROR: Failed to decrypt data\n" +
						"NotInitializedException was trown.\n" +
						"This exception means that no overriden Pbind key is stored in the flash.\n" +
						"Have you already done FW Update with SVN increment?";
				setResponse(error.getBytes(), 0, error.length());
				return status;
			}
			catch (Exception ex)
			{
				String error = "ERROR: Failed to decrypt data using DataMigration API\n"
						+ ex.getMessage();
				DebugPrint.printString(error);
				setResponse(error.getBytes(), 0, error.length());
				return status;
			}
		
		return status;
	}
			  
		
	
	
	public byte[] CombineByteArray(byte[] a, byte[] b)
	{
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);

		return c; 
	}
	
	
	
	
	public boolean SaveFirstLocationInFile(byte[] id, byte[] location) {
		// TODO Auto-generated method stub
//		int fileSize = FlashStorage.getFlashDataSize(FileForLocations); 
		
	byte[] idPlusLocation = new byte[id.length + location.length];
	idPlusLocation = CombineByteArray(id, location) ;
		try 
		{
			int res = saveData(idPlusLocation);
//			FlashStorage.readFlashData(1, data, fileSize); 
//			byte[] newLocation = CombineByteArray(data, location);
//			FlashStorage.writeFlashData(FileForLocations, newLocation, 0, newLocation.length);
		}
		catch(Exception e)
		{
		DebugPrint.printString(e.toString());
			return false; 
		}
		return true; 
	}
	
	
	
	//in - id, encrypted locations file 
	// out encrypted(file + new location)
//	public boolean SaveAdditionalLocationInFile(byte[] id, byte[] location ,) {
//		// TODO Auto-generated method stub
////		int fileSize = FlashStorage.getFlashDataSize(FileForLocations); 
//		
//	byte[] idPlusLocation = new byte[id.length + location.length];
//	idPlusLocation = CombineByteArray(id, location) ;
//		try 
//		{
//			int res = saveData(idPlusLocation);
////			FlashStorage.readFlashData(1, data, fileSize); 
////			byte[] newLocation = CombineByteArray(data, location);
////			FlashStorage.writeFlashData(FileForLocations, newLocation, 0, newLocation.length);
//		}
//		catch(Exception e)
//		{
//		DebugPrint.printString(e.toString());
//			return false; 
//		}
//		return true; 
//	}
	
	
	
	
	
	

	/**
	 * This method will be called by the VM when the session being handled by this
	 * Trusted Application instance is being closed and this Trusted Application
	 * instance is about to be removed. This method cannot provide response data and
	 * therefore calling setResponse or setResponseCode methods from it will throw a
	 * NullPointerException.
	 * 
	 * @return APPLET_SUCCESS code (the status code is not used by the VM).
	 */
	public int onClose() {
		DebugPrint.printString("Goodbye, DAL!");
		return APPLET_SUCCESS;
	}
}
