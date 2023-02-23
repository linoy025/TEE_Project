package fProject;

import com.intel.util.*;

//
// Implementation of DAL Trusted Application: TeeProj 
//
// **************************************************************************************************
// NOTE:  This default Trusted Application implementation is intended for DAL API Level 7 and above
// **************************************************************************************************

public class TeeRunner extends IntelApplet {

	private int FileForLocations = 1; 
	private int FileForId = 0; 

	
	/**
	 * This method will be called by the VM when a new session is opened to the Trusted Application 
	 * and this Trusted Application instance is being created to handle the new session.
	 * This method cannot provide response data and therefore calling
	 * setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @param	request	the input data sent to the Trusted Application during session creation
	 * 
	 * @return	APPLET_SUCCESS if the operation was processed successfully, 
	 * 		any other error status code otherwise (note that all error codes will be
	 * 		treated similarly by the VM by sending "cancel" error code to the SW application).
	 */
	public int onInit(byte[] request) {
		DebugPrint.printString("Hello, DAL!");
		return APPLET_SUCCESS;
	}
	
	/**
	 * This method will be called by the VM to handle a command sent to this
	 * Trusted Application instance.
	 * 
	 * @param	commandId	the command ID (Trusted Application specific) 
	 * @param	request		the input data for this command 
	 * @return	the return value should not be used by the applet
	 */
	public int invokeCommand(int commandId, byte[] request) {
		
		DebugPrint.printString("Received command Id: " + commandId + ".");
		if(request != null)
		{
			DebugPrint.printString("Received buffer:");
			DebugPrint.printBuffer(request);
		}
		switch (commandId)
		{
		case 0:
			SaveIdInFile(request); 
			break; 
		case 1:
			SaveLocationInFile(request);
		case 2: 
			GetId();
			break; 
		case 3:
			GetLocations(); 
			break; 
		
		}
		
		final byte[] myResponse = { 'O', 'K' };

		/*
		 * To return the response data to the command, call the setResponse
		 * method before returning from this method. 
		 * Note that calling this method more than once will 
		 * reset the response data previously set.
		 */
		setResponse(myResponse, 0, myResponse.length);

		/*
		 * In order to provide a return value for the command, which will be
		 * delivered to the SW application communicating with the Trusted Application,
		 * setResponseCode method should be called. 
		 * Note that calling this method more than once will reset the code previously set. 
		 * If not set, the default response code that will be returned to SW application is 0.
		 */
		setResponseCode(commandId);

		/*
		 * The return value of the invokeCommand method is not guaranteed to be
		 * delivered to the SW application, and therefore should not be used for
		 * this purpose. Trusted Application is expected to return APPLET_SUCCESS code 
		 * from this method and use the setResposeCode method instead.
		 */
		return APPLET_SUCCESS;
	}

	private void GetLocations() {
		// TODO Auto-generated method stub
		
	}

	private void GetId() {
		// TODO Auto-generated method stub
		
	}

	private boolean SaveLocationInFile(byte[] location) {
		// TODO Auto-generated method stub
		int fileSize = FlashStorage.getFlashDataSize(FileForLocations); 
		byte[] data = new byte[fileSize + 10];
		try 
		{
			FlashStorage.readFlashData(1, data, fileSize); 
			byte[] newLocation = CombineByteArray(data, location);
			FlashStorage.writeFlashData(FileForLocations, newLocation, 0, newLocation.length);
		}
		catch(Exception e)
		{
			DebugPrint.printString(e.toString());
			return false; 
		}
		return true; 
	}

	private boolean SaveIdInFile(byte[] id) {
		// TODO Auto-generated method stub
		FlashStorage.writeFlashData(FileForId, id, 0, id.length);
		return true; 
	}
	
	private byte[] CombineByteArray(byte[] a, byte[] b)
	{
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);

		return c; 
	}

	/**
	 * This method will be called by the VM when the session being handled by
	 * this Trusted Application instance is being closed 
	 * and this Trusted Application instance is about to be removed.
	 * This method cannot provide response data and therefore
	 * calling setResponse or setResponseCode methods from it will throw a NullPointerException.
	 * 
	 * @return APPLET_SUCCESS code (the status code is not used by the VM).
	 */
	public int onClose() {
		DebugPrint.printString("Goodbye, DAL!");
		return APPLET_SUCCESS;
	}
}
