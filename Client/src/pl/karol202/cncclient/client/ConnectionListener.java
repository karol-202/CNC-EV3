package pl.karol202.cncclient.client;

public interface ConnectionListener
{
	void onConnected();
	
	void onAuthenticated();
	
	void onUnknownHost();
	
	void onCannotConnect();
	
	void onAuthenticationFailed();
	
	void onDisconnected();
	
	void onConnectionProblem();
	
	void onSent();
	
	void onSendingDenied();
	
	void onStarted();
	
	void onStartingDenied();
	
	void onPaused();
	
	void onPausingDenied();
	
	void onResumed();
	
	void onResumingDenied();
	
	void onStopped();
	
	void onMachineStateUpdated();
}