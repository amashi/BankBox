package bankbox.clientlayer;

import java.util.List;

import org.cto.VVS3Box.api.AndroidClientAPI;
import org.cto.VVS3Box.api.FileTransferManager;
import org.cto.VVS3Box.api.VVEXcption;
import org.cto.VVS3Box.api.VVFileInfo;
import org.cto.VVS3Box.api.VVSessionInfo;
import org.cto.VVS3Box.api.VVUserInfo;

public class AndroidClientImpl implements AndroidClientAPI  {

	
	public AndroidClientImpl(){
		super(); 
	}

	@Override
	public VVSessionInfo login(String identityKey) throws VVEXcption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VVSessionInfo getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void logout() throws VVEXcption {
		// TODO Auto-generated method stub
		
	}

	@Override
	public VVUserInfo getUserInfo() throws VVEXcption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VVFileInfo getRootFolder() throws VVEXcption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VVFileInfo getFileInfoAt(String path) throws VVEXcption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VVFileInfo> getChildrenOf(String path) throws VVEXcption {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createFolder(String fullpath) throws VVEXcption {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String fullpath) throws VVEXcption {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FileTransferManager getTransferManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWebLoginUrl() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

}
