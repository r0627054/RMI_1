package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Set;

public interface ICarRentalCompany extends Remote {

	Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

	Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, RemoteException;

	Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;

}
