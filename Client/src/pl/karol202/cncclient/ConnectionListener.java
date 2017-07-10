package pl.karol202.cncclient;

interface ConnectionListener
{
	void onConnected();
	
	void onUnknownHost();
	
	void onCannotConnect();
	
	void onAuthenticationFailed();
	
	void onDisconnected();
	
	void onConnectionProblem();
	
	void onSent();
	
	void onSendingDenied();
	
	void onStarted();
	
	void onStartingDenied();
}