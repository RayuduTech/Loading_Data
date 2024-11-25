
public class Testing_milli_Mins {

	 private static final long TOKEN_EXPIRY_BUFFER = 20 * 60 * 1000; // 20 minutes in milliseconds

	    public static void main(String[] args) {
	        long tokenExpiryTime = System.currentTimeMillis() + (1079 * 1000) - TOKEN_EXPIRY_BUFFER;
	        long currentTime = System.currentTimeMillis();
	        
	        // Calculate the difference in milliseconds
	        long timeDifferenceInMillis = tokenExpiryTime - currentTime;
	        
	        // Convert the difference to minutes
	        long timeDifferenceInMinutes = timeDifferenceInMillis / (60 * 1000);
	        
	        System.out.println("Time difference in minutes: " + timeDifferenceInMinutes);
	    }

}
