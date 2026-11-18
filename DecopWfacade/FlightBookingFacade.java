package DecopWfacade;

public class FlightBookingFacade {
    private final SeatInventorySystem seatSystem;
    private final BaggagePricingEngine baggageEngine;
    private final CurrencyConverter currencyConverter;
    private final PaymentProcessor paymentProcessor;
    private final TicketIssuer ticketIssuer;
    public FlightBookingFacade(){
        this.seatSystem = new SeatInventorySystem();
        this.baggageEngine = new BaggagePricingEngine();
        this.currencyConverter = new CurrencyConverter();
        this.paymentProcessor = new PaymentProcessor();
        this.ticketIssuer = new TicketIssuer();
    }
    public String bookFlight(String passengerName, String flightCode, String seatNumber, double baseFareUSD, int bagCount, double bagWeightKg, String currency, String cardNumber){
        String lockToken=null;
        try{
            seatSystem.connectToReservationServer();
            lockToken=seatSystem.lockSeat(flightCode, seatNumber, passengerName);
            baggageEngine.calculateBaggageFeeUSD(bagCount,bagWeightKg);
            currencyConverter.convertFromUSD(totalUSD, currency );
            paymentProcessor.initializeGateway();
            paymentProcessor.chargeCard(cardNumber,convertedTotal, currency);
            ticketIssuer.generateTicket(passengerName, flightCode, lockToken, receipID);
        } catch (RuntimeException e){
            if(lockToken!=null){
                seatSystem.releaseSeatLock(lockToken);
                return "BOOKING_FAILED: " + e.getMessage();
            }

        } finally {
            seatSystem.disconnect();

        }
    }
}
