package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;

import rental.CarRentalCompany;
import rental.CarType;
import rental.ICarRentalCompany;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;

public class Client extends AbstractTestBooking {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;

	private final static String NAME = "Hertz";
	private ICarRentalCompany icrc = null;

	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(null);

		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;

		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", NAME, localOrRemote == 1);
		client.run();
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public Client(String scriptFile, String carRentalCompanyName, boolean remote) {
		super(scriptFile);
		System.out.println("Client scriptfile loaded");

		try {
			Registry reg;
			if (remote) {
				reg = LocateRegistry.getRegistry("127.0.0.1", 10481);
			} else {
				reg = LocateRegistry.getRegistry();
			}
			this.icrc = (ICarRentalCompany) reg.lookup(carRentalCompanyName);

			System.out.println("ICarRentalCompany located in rmi registry! = " + this.icrc);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Check which car types are available in the given period (across all companies
	 * and regions) and print this list of car types.
	 *
	 * @param start start time of the period
	 * @param end   end time of the period
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
		Set<CarType> availableCarTypes = icrc.getAvailableCarTypes(start, end);

		for (CarType car : availableCarTypes) {
			System.out.println(car);
		}
		System.out.println("\n\n");
	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param clientName name of the client
	 * @param start      start time for the quote
	 * @param end        end time for the quote
	 * @param carType    type of car to be reserved
	 * @param region     region in which car must be available
	 * @return the newly created quote
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
			throws Exception {
		Quote result = icrc.createQuote(new ReservationConstraints(start, end, carType, region), clientName);
		System.out.println("NEW QUOTE : " + result + "\n");
		return result;
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		Reservation result = icrc.confirmQuote(quote);
		System.out.println("RESERVATION : " + result + "\n");
		return result;
	}

	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {
		List<Reservation> list = icrc.getReservationsByRenter(clientName);

		for (Reservation res : list) {
			System.out.println("RESERVATION BY RENTER: " + res.getReservationInfo());
		}
		System.out.println("\n\n");

		return list;
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param carType name of the car type
	 * @return number of reservations for the given car type
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {
		int amount = icrc.getNumberOfReservationsForCarType(carType);

		System.out.println("AMOUNT OF RESERVATIONS: " + carType + " has " + amount + " reservations.");

		return amount;
	}
}