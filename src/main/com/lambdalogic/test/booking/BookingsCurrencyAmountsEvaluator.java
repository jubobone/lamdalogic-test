package com.lambdalogic.test.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import com.lambdalogic.test.booking.exception.InconsistentCurrenciesException;
import com.lambdalogic.test.booking.model.Booking;
import com.lambdalogic.test.booking.model.CurrencyAmount;

public class BookingsCurrencyAmountsEvaluator implements IBookingsCurrencyAmountsEvaluator {

	private BigDecimal sumTotalAmount;
	private BigDecimal sumTotalPaidAmount;
	private BigDecimal sumTotalOpenAmount;
	
	private String bookingCurrency = null;
	
	@Override
	public void calculate(List<Booking> bookingList, Long invoiceRecipientID) throws InconsistentCurrenciesException {
		
		List<Booking> matchedBookings = bookingList.stream()
				.filter(booking -> booking.getInvoiceRecipientPK() == invoiceRecipientID)
				.collect(Collectors.toList());
		
		for(Booking booking: matchedBookings) {
			if(bookingCurrency == null) {
				bookingCurrency = booking.getCurrency();
				this.sumTotalAmount = BigDecimal.ZERO.setScale(2);
				this.sumTotalPaidAmount = BigDecimal.ZERO.setScale(2);
				this.sumTotalOpenAmount = BigDecimal.ZERO.setScale(2);
			} 
			
			if(bookingCurrency != booking.getCurrency()) {
				this.sumTotalAmount = null;
				this.sumTotalPaidAmount = null;
				this.sumTotalOpenAmount = null;
				throw new InconsistentCurrenciesException(bookingCurrency, booking.getCurrency());
			} 
			
			if(booking.isGross()){
				sumTotalAmount = sumTotalAmount.add(booking.getTotalAmount());
			}
				
				
			
		}
	}

	@Override
	public CurrencyAmount getTotalAmount() {
		if(sumTotalAmount != null) {
			sumTotalAmount = sumTotalAmount.setScale(2, RoundingMode.HALF_UP);
			return new CurrencyAmount(sumTotalAmount, bookingCurrency);
		}
		return null;
	}

	@Override
	public CurrencyAmount getTotalPaidAmount() {
		if(sumTotalPaidAmount != null) {
			sumTotalPaidAmount = sumTotalPaidAmount.setScale(2, RoundingMode.HALF_UP);
			return new CurrencyAmount(sumTotalPaidAmount, bookingCurrency);
		}
		return null;
	}

	@Override
	public CurrencyAmount getTotalOpenAmount() {
		if(sumTotalOpenAmount != null) {
			sumTotalOpenAmount = sumTotalOpenAmount.setScale(2, RoundingMode.HALF_UP);
			return new CurrencyAmount(sumTotalOpenAmount, bookingCurrency);
		}
		return null;
	}

}
